package medeus.finalproject.Screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import medeus.finalproject.Entities.Player;

public class Game implements Screen {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Player player;

    public Game() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        batch = new SpriteBatch();
        player = new Player(100, 100);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.update(delta);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        player.render(batch);
        batch.end();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        batch.dispose();
    }
}
