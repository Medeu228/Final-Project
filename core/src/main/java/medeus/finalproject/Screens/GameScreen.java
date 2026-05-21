package medeus.finalproject.Screens;

import java.util.ArrayList;
import java.util.Random;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import medeus.finalproject.Main;
import medeus.finalproject.Entities.Enemies.Skeleton;
import medeus.finalproject.Entities.Enemies.Zombie;
import medeus.finalproject.Entities.EnemyAbstract;
import medeus.finalproject.Entities.Heroes.Warrior;
import medeus.finalproject.Entities.Player;
import medeus.finalproject.World.OverWorld;
import medeus.finalproject.World.SpawnTrigger;

public class GameScreen implements Screen {

    private static final float MAP_WIDTH  = 1600f;
    private static final float MAP_HEIGHT = 1600f;

    // Кол-во врагов на уровень
    private static final int[] ENEMY_COUNT = { 0, 8, 15, 25 };

    // Множитель характеристик врагов (HP и ATK)
    private static final float[] STAT_SCALE  = { 0f, 1.0f, 1.6f, 2.4f };

    // Множитель скорости врагов
    private static final float[] SPEED_SCALE = { 0f, 1.0f, 1.25f, 1.5f };

    private Main game;
    private int level;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private boolean waveStarted = false;
    private ArrayList<EnemyAbstract> enemies;
    private Random random;
    private boolean gameOver = false;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    private OverWorld overWorld;
    private SpawnTrigger spawnTrigger;

    public GameScreen(Main game, int level) {
        this.game  = game;
        this.level = level;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        font = new BitmapFont();
        font.setColor(1, 0, 0, 1);

        shapeRenderer = new ShapeRenderer();
        enemies = new ArrayList<>();
        random  = new Random();

        overWorld = new OverWorld(level);
        player    = new Warrior(100, 100, level);

        if (level == 1) {
            spawnTrigger = new SpawnTrigger(MAP_WIDTH, MAP_HEIGHT);
        } else {
            spawnEnemies();
            waveStarted = true;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ── Game Over ──────────────────────────────────────────────────────────
        if (gameOver) {
            Gdx.gl.glClearColor(0.3f, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            font.draw(batch, "GAME OVER", 350, 320);
            font.draw(batch, "ESC - вернуться в меню", 300, 290);
            batch.end();
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                game.setScreen(new Loading(game, new Menu(game), 2f));
                dispose();
            }
            return;
        }

        // ── Level Complete ─────────────────────────────────────────────────────
        if (waveStarted && enemies.isEmpty()) {
            Gdx.gl.glClearColor(0, 0.3f, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            font.draw(batch, "LEVEL " + level + " COMPLETE!", 310, 330);
            if (level < 3) {
                font.draw(batch, "ENTER - следующий уровень", 290, 300);
            } else {
                font.draw(batch, "Вы прошли игру!", 320, 300);
            }
            font.draw(batch, "ESC - в меню", 350, 270);
            batch.end();

            if (level < 3 && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                game.setScreen(new Loading(game, new GameScreen(game, level + 1), 2f));
                dispose();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                game.setScreen(new Loading(game, new Menu(game), 2f));
                dispose();
            }
            return;
        }

        // ── Gameplay ───────────────────────────────────────────────────────────
        player.update(delta);

        if (level == 1 && spawnTrigger != null && !waveStarted) {
            if (spawnTrigger.checkActivation(player.getX(), player.getY())) {
                spawnEnemies();
                waveStarted = true;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            player.performAttack(enemies);
        }

        enemies.removeIf(e -> !e.isAlive());

        if (player.getHp() <= 0) {
            gameOver = true;
            return;
        }

        // Камера следует за игроком, но не выходит за границы карты
        float halfW = camera.viewportWidth  / 2f;
        float halfH = camera.viewportHeight / 2f;
        float camX  = Math.max(halfW,  Math.min(player.getX(), MAP_WIDTH  - halfW));
        float camY  = Math.max(halfH,  Math.min(player.getY(), MAP_HEIGHT - halfH));
        camera.position.set(camX, camY, 0);
        camera.update();

        // Сектор атаки
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        player.renderAttackRange(shapeRenderer);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Рендер мира и сущностей
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        overWorld.renderBackground(batch);  // фон + трава

        if (level == 1 && spawnTrigger != null) {
            spawnTrigger.render(batch, font, player.getX(), player.getY());
        }

        player.renderHUD(batch, font, camera.position.x, camera.position.y);
        font.draw(batch, "Level: " + level, camera.position.x + 200, camera.position.y + 280);
        if (waveStarted) {
            font.draw(batch, "Enemies: " + enemies.size(), camera.position.x + 200, camera.position.y + 250);
        }

        player.render(batch);

        for (EnemyAbstract enemy : enemies) {
            enemy.update(delta, player.getX(), player.getY());
            enemy.tryAttackPlayer(player);
            enemy.render(batch);
        }

        overWorld.renderObjects(batch);  // деревья, кусты, валуны — поверх всех

        batch.end();
    }

    private void spawnEnemies() {
        int   count      = ENEMY_COUNT[level];
        float statScale  = STAT_SCALE[level];
        float speedScale = SPEED_SCALE[level];

        for (int i = 0; i < count; i++) {
            float x, y;
            do {
                x = 100 + random.nextFloat() * 1400;
                y = 100 + random.nextFloat() * 1400;
            } while (Math.abs(x - 100) < 300 && Math.abs(y - 100) < 300);

            EnemyAbstract enemy = random.nextBoolean()
                ? new Zombie(x, y)
                : new Skeleton(x, y);

            enemy.applyDifficultyScale(statScale);
            enemies.add(enemy);
        }
    }

    @Override
    public void dispose() {
        if (player != null) player.dispose();
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
        overWorld.dispose();
        if (spawnTrigger != null) spawnTrigger.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
