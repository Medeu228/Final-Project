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
import medeus.finalproject.Main;
import medeus.finalproject.Battle.BattleEngine;
import medeus.finalproject.Battle.Combatant;
import medeus.finalproject.Battle.EnemyCombatantAdapter;
import medeus.finalproject.Battle.HeroCombatantAdapter;
import medeus.finalproject.Entities.Enemies.Skeleton;
import medeus.finalproject.Entities.Enemies.Zombie;
import medeus.finalproject.Entities.EnemyAbstract;
import medeus.finalproject.Entities.Heroes.Archer;
import medeus.finalproject.Entities.Heroes.Mage;
import medeus.finalproject.Entities.Heroes.Warrior;
import medeus.finalproject.Entities.Player;
import medeus.finalproject.World.TestingRange;

public class GameScreen implements Screen {

    private Main game;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private boolean heroChosen = false;
    private ArrayList<EnemyAbstract> enemies;
    private Random random;
    private boolean gameOver = false;
    private BitmapFont font;

    private Texture warriorPreview;
    private Texture archerPreview;
    private Texture magePreview;

    private TestingRange testingrange;

    public GameScreen(Main game) {
        this.game = game;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        font = new BitmapFont();
        font.setColor(1, 0, 0, 1);

        enemies = new ArrayList<>();
        random = new Random();

        warriorPreview = new Texture("Warrior.jpeg");
        archerPreview = new Texture("Archer.jpeg");
        magePreview = new Texture("Mage.jpeg");

        testingrange = new TestingRange();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!heroChosen) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();

            batch.draw(warriorPreview, 200, 300, 128, 128);
            batch.draw(archerPreview, 350, 300, 128, 128);
            batch.draw(magePreview, 500, 300, 128, 128);

            font.draw(batch, "Press 1 - Warrior", 200, 270);
            font.draw(batch, "Press 2 - Archer", 350, 270);
            font.draw(batch, "Press 3 - Mage", 500, 270);

            batch.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                player = new Warrior(100, 100);
                heroChosen = true;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                player = new Archer(100, 100);
                heroChosen = true;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                player = new Mage(100, 100);
                heroChosen = true;
            }

            return;
        }

        if (gameOver) {
            Gdx.gl.glClearColor(0.3f, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                game.setScreen(new Loading(game, new Menu(game), 2f));
                dispose();
            }
            return;
        }

        player.update(delta);

        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            spawnEnemy();
        }

        EnemyAbstract enemyToRemove = null;

        batch.begin();

        testingrange.render(batch);

        font.draw(batch, "DEV SCREEN", camera.position.x - 60, camera.position.y + 250);
        font.draw(batch, "HP: " + player.getHp(), camera.position.x - 380, camera.position.y + 280);
        font.draw(batch, "E - spawn enemy", camera.position.x - 380, camera.position.y + 250);
        font.draw(batch, "ESC - menu when dead", camera.position.x - 380, camera.position.y + 220);

        player.render(batch);

        for (EnemyAbstract enemy : enemies) {
            enemy.update(delta, player.getX(), player.getY());

            if (player.getHitbox().overlaps(enemy.getHitbox())) {
                startBattle(enemy);
                enemyToRemove = enemy;
                break;
            }

            enemy.render(batch);
        }

        batch.end();

        if (enemyToRemove != null) {
            enemies.remove(enemyToRemove);
        }
    }

    private void spawnEnemy() {
        float mapWidth = 1600;
        float mapHeight = 1600;

        float x = random.nextFloat() * mapWidth;
        float y = random.nextFloat() * mapHeight;

        EnemyAbstract enemy;

        if (random.nextBoolean()) {
            enemy = new Zombie(x, y);
        } else {
            enemy = new Skeleton(x, y);
        }

        enemies.add(enemy);
    }

    private void startBattle(EnemyAbstract enemy) {
        Combatant hero = new HeroCombatantAdapter(player);
        Combatant enemyAdapter = new EnemyCombatantAdapter(enemy);

        BattleEngine.getInstance().fight(hero, enemyAdapter);

        if (!hero.isAlive()) {
            gameOver = true;
        }
    }

    @Override
    public void dispose() {
        if (player != null) {
            player.dispose();
        }
        batch.dispose();
        font.dispose();
        warriorPreview.dispose();
        archerPreview.dispose();
        magePreview.dispose();
        testingrange.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
