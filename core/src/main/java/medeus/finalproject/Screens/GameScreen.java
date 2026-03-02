package medeus.finalproject.Screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import medeus.finalproject.Entities.Heroes.Archer;
import medeus.finalproject.Entities.Heroes.Mage;
import medeus.finalproject.Entities.Heroes.Warrior;
import medeus.finalproject.Entities.Player;

public class GameScreen implements Screen {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private boolean heroChosen = false;

    public GameScreen() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        background = new Texture("background.png");
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        Player player;
        boolean heroChosen = false;

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            player = new Warrior(100, 100);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            player = new Archer(100, 100);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            player = new Mage(100, 100);

        }
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

        batch.end();


        camera.position.x = player.getX();
        camera.position.y = player.getY();
        camera.update();

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
