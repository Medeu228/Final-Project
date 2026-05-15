package medeus.finalproject.Screens;

import java.util.ArrayList;
import java.util.Random;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import medeus.finalproject.Entities.Items.HealingItem;
import java.util.Iterator;

public class GameScreen implements Screen {

    private static final float MAP_WIDTH  = 1600f;
    private static final float MAP_HEIGHT = 1600f;

    private static final int[]   ENEMY_COUNT = { 0, 10, 20, 30 };
    private static final float[] DIFF_SCALE  = { 0, 1.0f, 1.2f, 1.4f };

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

    private Texture heartTexture;
    private ArrayList<HealingItem> healingItems;
    private float heartSpawnTimer = 0f;
    private static final float HEART_SPAWN_INTERVAL = 15f;
    private static final int MAX_HEARTS = 3;

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
        heartTexture = new Texture("heart.png");
        healingItems = new ArrayList<>();

        // Создаём Warrior нужного уровня сразу
        player = new Warrior(100, 100, level);

        if (level == 1) {
            spawnTrigger = new SpawnTrigger(MAP_WIDTH, MAP_HEIGHT);
        } else {
            // Уровни 2 и 3 — враги спавнятся сразу
            spawnEnemies();
            waveStarted = true;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

        player.update(delta);
        // Спавн сердечек каждые 15 секунд
        heartSpawnTimer += delta;
        if (heartSpawnTimer >= HEART_SPAWN_INTERVAL && healingItems.size() < MAX_HEARTS) {
            spawnHearts(1);
            heartSpawnTimer = 0f;
        }

        // Уровень 1: активация волны через SpawnTrigger
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

        float halfW = camera.viewportWidth / 2f;   // = 400
        float halfH = camera.viewportHeight / 2f;  // = 300

        float targetX = player.getX() + 64f;  // центр спрайта 128px
        float targetY = player.getY() + 64f;  // центр спрайта 128px

        float camX = Math.max(halfW, Math.min(targetX, MAP_WIDTH  - halfW));
        float camY = Math.max(halfH, Math.min(targetY, MAP_HEIGHT - halfH));

        camera.position.set(camX, camY, 0);
        camera.update();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        player.renderAttackRange(shapeRenderer);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        overWorld.render(batch);

        if (level == 1 && spawnTrigger != null) {
            spawnTrigger.render(batch, font, player.getX(), player.getY());
        }

        player.renderHUD(batch, font, camera.position.x, camera.position.y);
        font.draw(batch, "Level: " + level, camera.position.x + 200, camera.position.y + 280);
        if (waveStarted) {
            font.draw(batch, "Enemies: " + enemies.size(), camera.position.x + 200, camera.position.y + 250);
        }

        player.render(batch);
        Iterator<HealingItem> it = healingItems.iterator();
        while (it.hasNext()) {
            HealingItem heart = it.next();
            heart.render(batch);
            if (heart.checkPickup(player.getHitbox())) {
                player.heal(heart.getHealAmount());
                it.remove();
            }
        }

        for (EnemyAbstract enemy : enemies) {
            enemy.update(delta, player.getX(), player.getY());
            enemy.tryAttackPlayer(player);
            enemy.render(batch);
        }

        batch.end();
    }

    private void spawnEnemies() {
        int count   = ENEMY_COUNT[level];
        float scale = DIFF_SCALE[level];

        for (int i = 0; i < count; i++) {
            float x, y;
            do {
                x = 100 + random.nextFloat() * 1400;
                y = 100 + random.nextFloat() * 1400;
            } while (Math.abs(x - 100) < 300 && Math.abs(y - 100) < 300);

            EnemyAbstract enemy = random.nextBoolean()
                ? new Zombie(x, y)
                : new Skeleton(x, y);

            enemy.applyDifficultyScale(scale);
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
        heartTexture.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    private void spawnHearts(int count) {
        for (int i = 0; i < count; i++) {
            float x = 100 + random.nextFloat() * 1400;
            float y = 100 + random.nextFloat() * 1400;
            healingItems.add(new HealingItem(x, y, heartTexture));
        }
    }
}
