package medeus.finalproject.Screens;

import java.util.ArrayList;
import java.util.Random;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import medeus.finalproject.Main;
import medeus.finalproject.Entities.Enemies.Skeleton;
import medeus.finalproject.Entities.Enemies.Zombie;
import medeus.finalproject.Entities.EnemyAbstract;
import medeus.finalproject.Entities.Heroes.Warrior;
import medeus.finalproject.Entities.Player;
import medeus.finalproject.World.OverWorld;

public class devScreen implements Screen {

    private static final float MAP_WIDTH  = 1600f;
    private static final float MAP_HEIGHT = 1600f;
    private static final String PREFS     = "collision_settings";
    private static final float  STEP      = 1f;

    private Main game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private ArrayList<EnemyAbstract> enemies;
    private Random random;
    private boolean gameOver = false;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private int warriorLevel = 1;
    private OverWorld overWorld;

    // ─── Редактор коллизий ────────────────────────────────────────────────────
    private enum EditMode { BOULDER, TREE, PLAYER }
    private EditMode editMode = EditMode.BOULDER;

    // Хитбоксы хранятся как (x1,y1,x2,y2) — отступы от краёв спрайта.
    // x1=левый край, y1=нижний, x2=правый, y2=верхний.
    // Размер спрайтов: boulder=128x128, tree=128x160, player=128x128

    private float b_x1, b_y1, b_x2, b_y2; // boulder
    private float t_x1, t_y1, t_x2, t_y2; // tree trunk
    private float p_x1, p_y1, p_x2, p_y2; // player

    // Позиции объектов в мире
    private float boulderWorldX, boulderWorldY;
    private float treeWorldX,    treeWorldY;

    // Флаг сохранения
    private boolean justSaved  = false;
    private float   savedTimer = 0f;

    // Зум камеры
    private float cameraZoom = 1.0f;
    private static final float ZOOM_STEP = 0.05f;
    private static final float ZOOM_MIN  = 0.3f;
    private static final float ZOOM_MAX  = 2.0f;

    // ─── Конструктор ──────────────────────────────────────────────────────────

    public devScreen(Main game) {
        this.game = game;
        batch         = new SpriteBatch();
        camera        = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        font          = new BitmapFont();
        font.setColor(1, 0, 0, 1);
        enemies       = new ArrayList<>();
        random        = new Random();
        shapeRenderer = new ShapeRenderer();

        overWorld = new OverWorld(1);
        overWorld.placeInRow();

        float[] bp = overWorld.getBoulderPositions();
        boulderWorldX = bp[0]; boulderWorldY = bp[1];

        float[] tp = overWorld.getTreePositions();
        treeWorldX = tp[0]; treeWorldY = tp[1];

        // Загружаем сохранённые значения (дефолты = полный спрайт)
        Preferences prefs = Gdx.app.getPreferences(PREFS);
        b_x1 = prefs.getFloat("b_x1", 0f);
        b_y1 = prefs.getFloat("b_y1", 0f);
        b_x2 = prefs.getFloat("b_x2", 128f);
        b_y2 = prefs.getFloat("b_y2", 128f);

        t_x1 = prefs.getFloat("t_x1", 46f);  // дефолт: ствол дерева
        t_y1 = prefs.getFloat("t_y1", 0f);
        t_x2 = prefs.getFloat("t_x2", 82f);
        t_y2 = prefs.getFloat("t_y2", 50f);

        p_x1 = prefs.getFloat("p_x1", 0f);
        p_y1 = prefs.getFloat("p_y1", 0f);
        p_x2 = prefs.getFloat("p_x2", 128f);
        p_y2 = prefs.getFloat("p_y2", 128f);

        // Применяем загруженные значения
        applyBoulderRect();
        applyTreeRect();

        player = new Warrior(100, 100, warriorLevel);
        applyPlayerRect();
    }

    // ─── Применение хитбоксов ─────────────────────────────────────────────────

    private void applyBoulderRect() {
        overWorld.setBoulderCollision(
            boulderWorldX + b_x1,
            boulderWorldY + b_y1,
            b_x2 - b_x1,
            b_y2 - b_y1
        );
    }

    private void applyTreeRect() {
        overWorld.setTreeCollision(
            treeWorldX + t_x1,
            treeWorldY + t_y1,
            t_x2 - t_x1,
            t_y2 - t_y1
        );
    }

    private void applyPlayerRect() {
        player.setHitboxParams(p_x1, p_y1, p_x2 - p_x1, p_y2 - p_y1);
    }

    // ─── Render ───────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameOver) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            font.draw(batch, "GAME OVER",              350, 320);
            font.draw(batch, "ESC - вернуться в меню", 300, 290);
            batch.end();
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                game.setScreen(new Loading(game, new Menu(game), 2f));
                dispose();
            }
            return;
        }

        if (savedTimer > 0) { savedTimer -= delta; } else { justSaved = false; }

        // Зум камеры
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS))        cameraZoom = Math.min(ZOOM_MAX, cameraZoom + ZOOM_STEP);
        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS))       cameraZoom = Math.max(ZOOM_MIN, cameraZoom - ZOOM_STEP);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0))        cameraZoom = 1.0f; // сброс

        handleInput();

        player.update(delta);
        applyPlayerRect(); // применяем каждый кадр чтобы hitbox обновлялся с позицией
        resolveCollisions(player);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) player.performAttack(enemies);
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) spawnEnemy();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) switchWarriorLevel(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) switchWarriorLevel(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) switchWarriorLevel(3);

        enemies.removeIf(e -> !e.isAlive());
        if (player.getHp() <= 0) { gameOver = true; return; }

        // Камера
        float halfW = camera.viewportWidth  / 2f;
        float halfH = camera.viewportHeight / 2f;
        float camX  = Math.max(halfW, Math.min(player.getX(), MAP_WIDTH  - halfW));
        float camY  = Math.max(halfH, Math.min(player.getY(), MAP_HEIGHT - halfH));
        camera.zoom = cameraZoom;
        camera.position.set(camX, camY, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        overWorld.renderBackground(batch);
        player.render(batch);
        for (EnemyAbstract e : enemies) {
            e.update(delta, player.getX(), player.getY());
            e.tryAttackPlayer(player);
            e.render(batch);
        }
        overWorld.renderObjects(batch);

        // HUD — последним, поверх всех объектов
        player.renderHUD(batch, font, camX, camY);
        font.draw(batch, "DEV SCREEN",            camX - 60,  camY + 280);
        font.draw(batch, "1/2/3 - warrior level", camX + 200, camY + 280);
        font.draw(batch, "E - spawn enemy",        camX + 200, camY + 250);
        font.draw(batch, "F - attack",             camX + 200, camY + 220);
        font.draw(batch, "ESC - menu when dead",   camX + 200, camY + 190);
        font.draw(batch, String.format("Zoom: %.2f  (-/= zoom  0=reset)", cameraZoom), camX + 200, camY + 160);
        renderEditorPanel(camX, camY);
        batch.end();

        // ── ShapeRenderer поверх всего ────────────────────────────────────────
        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        player.renderAttackRange(shapeRenderer);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Rectangle r : overWorld.getCollisionRects()) {
            // Активный тип — ярко-зелёный, остальные — тускло-зелёный
            boolean active = isActiveRect(r);
            shapeRenderer.setColor(active ? Color.GREEN : new Color(0, 0.5f, 0, 1f));
            shapeRenderer.rect(r.x, r.y, r.width, r.height);
        }
        // Хитбокс игрока
        shapeRenderer.setColor(editMode == EditMode.PLAYER ? Color.YELLOW : new Color(0.5f, 0.5f, 0, 1f));
        Rectangle pb = player.getHitbox();
        shapeRenderer.rect(pb.x, pb.y, pb.width, pb.height);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    // ─── Ввод редактора ───────────────────────────────────────────────────────

    private void handleInput() {
        // TAB — смена режима
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            if      (editMode == EditMode.BOULDER) editMode = EditMode.TREE;
            else if (editMode == EditMode.TREE)    editMode = EditMode.PLAYER;
            else                                   editMode = EditMode.BOULDER;
        }

        // ENTER — сохранить
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            savePrefs();
        }

        boolean left  = Gdx.input.isKeyJustPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyJustPressed(Input.Keys.RIGHT);
        boolean down  = Gdx.input.isKeyJustPressed(Input.Keys.DOWN);
        boolean up    = Gdx.input.isKeyJustPressed(Input.Keys.UP);
        boolean j     = Gdx.input.isKeyJustPressed(Input.Keys.J);  // x2 −
        boolean l     = Gdx.input.isKeyJustPressed(Input.Keys.L);  // x2 +
        boolean k     = Gdx.input.isKeyJustPressed(Input.Keys.K);  // y2 −
        boolean i     = Gdx.input.isKeyJustPressed(Input.Keys.I);  // y2 +

        switch (editMode) {
            case BOULDER:
                if (left)  b_x1 -= STEP;  if (right) b_x1 += STEP;
                if (down)  b_y1 -= STEP;  if (up)    b_y1 += STEP;
                if (j)     b_x2 -= STEP;  if (l)     b_x2 += STEP;
                if (k)     b_y2 -= STEP;  if (i)     b_y2 += STEP;
                b_x2 = Math.max(b_x1 + 1, b_x2);
                b_y2 = Math.max(b_y1 + 1, b_y2);
                applyBoulderRect();
                break;
            case TREE:
                if (left)  t_x1 -= STEP;  if (right) t_x1 += STEP;
                if (down)  t_y1 -= STEP;  if (up)    t_y1 += STEP;
                if (j)     t_x2 -= STEP;  if (l)     t_x2 += STEP;
                if (k)     t_y2 -= STEP;  if (i)     t_y2 += STEP;
                t_x2 = Math.max(t_x1 + 1, t_x2);
                t_y2 = Math.max(t_y1 + 1, t_y2);
                applyTreeRect();
                break;
            case PLAYER:
                if (left)  p_x1 -= STEP;  if (right) p_x1 += STEP;
                if (down)  p_y1 -= STEP;  if (up)    p_y1 += STEP;
                if (j)     p_x2 -= STEP;  if (l)     p_x2 += STEP;
                if (k)     p_y2 -= STEP;  if (i)     p_y2 += STEP;
                p_x2 = Math.max(p_x1 + 1, p_x2);
                p_y2 = Math.max(p_y1 + 1, p_y2);
                applyPlayerRect();
                break;
        }
    }

    // ─── Панель редактора ─────────────────────────────────────────────────────

    private void renderEditorPanel(float camX, float camY) {
        float px = camX - 390f;
        float py = camY + 100f;
        float lh = 18f; // line height

        String mode;
        float x1, y1, x2, y2;
        switch (editMode) {
            case TREE:   mode = ">> TREE   <<"; x1=t_x1; y1=t_y1; x2=t_x2; y2=t_y2; break;
            case PLAYER: mode = ">> PLAYER <<"; x1=p_x1; y1=p_y1; x2=p_x2; y2=p_y2; break;
            default:     mode = ">> BOULDER<<"; x1=b_x1; y1=b_y1; x2=b_x2; y2=b_y2; break;
        }

        font.draw(batch, "[TAB] " + mode,                            px, py);
        font.draw(batch, "Arrows  = Left/Bottom edge",               px, py -= lh);
        font.draw(batch, "J/L/K/I = Right/Top edge  -/+",           px, py -= lh);
        font.draw(batch, "[ENTER] = SAVE",                           px, py -= lh);
        py -= lh * 0.5f;
        font.draw(batch, String.format("x1=%.0f  y1=%.0f", x1, y1), px, py -= lh);
        font.draw(batch, String.format("x2=%.0f  y2=%.0f", x2, y2), px, py -= lh);
        font.draw(batch, String.format("w=%.0f   h=%.0f", x2-x1, y2-y1), px, py -= lh);
        if (justSaved) font.draw(batch, "*** SAVED! ***",            px, py -= lh);
    }

    // ─── Определяем активный rect для подсветки ───────────────────────────────

    private boolean isActiveRect(Rectangle r) {
        switch (editMode) {
            case BOULDER:
                return Math.abs(r.x - (boulderWorldX + b_x1)) < 1f;
            case TREE:
                return Math.abs(r.x - (treeWorldX + t_x1)) < 1f;
            default:
                return false;
        }
    }

    // ─── Сохранение ───────────────────────────────────────────────────────────

    private void savePrefs() {
        Preferences prefs = Gdx.app.getPreferences(PREFS);
        prefs.putFloat("b_x1", b_x1); prefs.putFloat("b_y1", b_y1);
        prefs.putFloat("b_x2", b_x2); prefs.putFloat("b_y2", b_y2);
        prefs.putFloat("t_x1", t_x1); prefs.putFloat("t_y1", t_y1);
        prefs.putFloat("t_x2", t_x2); prefs.putFloat("t_y2", t_y2);
        prefs.putFloat("p_x1", p_x1); prefs.putFloat("p_y1", p_y1);
        prefs.putFloat("p_x2", p_x2); prefs.putFloat("p_y2", p_y2);
        prefs.flush();
        justSaved  = true;
        savedTimer = 2f;
    }

    // ─── Коллизия ─────────────────────────────────────────────────────────────

    private void resolveCollisions(Player player) {
        Rectangle pb = player.getHitbox();
        float ox = player.getHitboxOffsetX();
        float oy = player.getHitboxOffsetY();
        for (Rectangle rect : overWorld.getCollisionRects()) {
            if (!pb.overlaps(rect)) continue;
            float oLeft   = (pb.x + pb.width)     - rect.x;
            float oRight  = (rect.x + rect.width)  - pb.x;
            float oBottom = (pb.y + pb.height)     - rect.y;
            float oTop    = (rect.y + rect.height) - pb.y;
            float minX = Math.min(oLeft, oRight);
            float minY = Math.min(oBottom, oTop);
            if (minX < minY) {
                // Выталкиваем по X: target = позиция спрайта так чтобы hitbox.x был нужным
                if (oLeft < oRight) player.setX(rect.x - pb.width  - ox);
                else                player.setX(rect.x + rect.width - ox);
            } else {
                // Выталкиваем по Y
                if (oBottom < oTop) player.setY(rect.y - pb.height - oy);
                else                player.setY(rect.y + rect.height - oy);
            }
        }
    }

    // ─── Вспомогательные ──────────────────────────────────────────────────────

    private void switchWarriorLevel(int lvl) {
        if (lvl == warriorLevel) return;
        float px = player.getX(), py = player.getY();
        player.dispose();
        warriorLevel = lvl;
        player = new Warrior(px, py, warriorLevel);
        applyPlayerRect();
    }

    private void spawnEnemy() {
        float x = random.nextFloat() * 1600;
        float y = random.nextFloat() * 1600;
        enemies.add(random.nextBoolean() ? new Zombie(x, y) : new Skeleton(x, y));
    }

    @Override
    public void dispose() {
        if (player    != null) player.dispose();
        if (overWorld != null) overWorld.dispose();
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
