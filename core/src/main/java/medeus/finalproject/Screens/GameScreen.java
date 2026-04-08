package medeus.finalproject.Screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import medeus.finalproject.Main;
import medeus.finalproject.World.OverWorld;

public class GameScreen implements Screen {

    private Main game;
    private OverWorld overWorld;
    private SpriteBatch batch;

    public GameScreen(Main game) {
        this.game = game;
        overWorld = new OverWorld();
    }

    @Override public void show() {}
    @Override public void render(float delta) {
        overWorld.render(batch);
    }
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        overWorld.dispose();
    }
}
