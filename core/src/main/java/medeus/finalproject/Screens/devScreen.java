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
import medeus.finalproject.World.TestingRange;

public class devScreen implements Screen {

    private Main game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private ArrayList<EnemyAbstract> enemies;
    private Random random;
    private boolean gameOver = false;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    // Текущий уровень Warrior в dev-режиме (переключается клавишами 1/2/3)
    private int warriorLevel = 1;

    private TestingRange testingrange;

    public devScreen(Main game) {
        this.game = game;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        font = new BitmapFont();
        font.setColor(1, 0, 0, 1);

        enemies = new ArrayList<>();
        random = new Random();
        shapeRenderer = new ShapeRenderer();
        testingrange = new TestingRange();

        // Создаём Warrior Lv.1 сразу
        player = new Warrior(100, 100, warriorLevel);
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

        // Переключение уровня Warrior (1/2/3) — пересоздаём с той же позицией
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) switchWarriorLevel(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) switchWarriorLevel(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) switchWarriorLevel(3);

        player.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            player.performAttack(enemies);
        }

        enemies.removeIf(e -> !e.isAlive());

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            spawnEnemy();
        }

        if (player.getHp() <= 0) {
            gameOver = true;
            return;
        }

        camera.position.set(player.getX(), player.getY(), 0);
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

        testingrange.render(batch);

        player.renderHUD(batch, font, camera.position.x, camera.position.y);
        font.draw(batch, "DEV SCREEN",            camera.position.x - 60,  camera.position.y + 280);
        font.draw(batch, "1/2/3 - warrior level", camera.position.x + 200, camera.position.y + 280);
        font.draw(batch, "E - spawn enemy",        camera.position.x + 200, camera.position.y + 250);
        font.draw(batch, "F - attack",             camera.position.x + 200, camera.position.y + 220);
        font.draw(batch, "ESC - menu when dead",   camera.position.x + 200, camera.position.y + 190);

        player.render(batch);

        for (EnemyAbstract enemy : enemies) {
            enemy.update(delta, player.getX(), player.getY());
            enemy.tryAttackPlayer(player);
            enemy.render(batch);
        }

        batch.end();
    }

    private void switchWarriorLevel(int newLevel) {
        if (newLevel == warriorLevel) return;
        float px = player.getX();
        float py = player.getY();
        player.dispose();
        warriorLevel = newLevel;
        player = new Warrior(px, py, warriorLevel);
    }

    private void spawnEnemy() {
        float x = random.nextFloat() * 1600;
        float y = random.nextFloat() * 1600;
        enemies.add(random.nextBoolean() ? new Zombie(x, y) : new Skeleton(x, y));
    }

    @Override
    public void dispose() {
        if (player != null) player.dispose();
        batch.dispose();
        font.dispose();
        testingrange.dispose();
        shapeRenderer.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
