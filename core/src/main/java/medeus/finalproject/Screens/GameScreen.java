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
import medeus.finalproject.Entities.Enemies.Skeleton;
import medeus.finalproject.Entities.Enemies.Zombie;
import medeus.finalproject.Entities.EnemyAbstract;
import medeus.finalproject.Entities.Heroes.Archer;
import medeus.finalproject.Entities.Heroes.Mage;
import medeus.finalproject.Entities.Heroes.Warrior;
import medeus.finalproject.Entities.Player;

public class GameScreen implements Screen {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    Player player;
    boolean heroChosen = false;
    private ArrayList<EnemyAbstract> enemies;
    private Random random;

    public GameScreen() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        background = new Texture("background.png");
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        enemies = new ArrayList<>();
        random = new Random();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!heroChosen) {

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

        player.update(delta);
        camera.position.x = player.getX();
        camera.position.y = player.getY();
        camera.update();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(
            background,
            0, 0,
            1600, 1600,
            0, 0,
            50, 50
        );

        player.render(batch);

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            spawnEnemy();
        }

        for (EnemyAbstract enemy : enemies) {
            enemy.update(delta, player.getX(), player.getY());
            enemy.render(batch);
        }

        batch.end();
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

    @Override
    public void dispose() {
        player.dispose();
        batch.dispose();
        background.dispose();
    }

    private Texture background;



    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
