package medeus.finalproject.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import medeus.finalproject.Main;

public class Loading extends ScreenAdapter {

    private Main game;
    private Screen nextScreen;
    private float loadingDuration;

    private ShapeRenderer shapeRenderer;
    private float timer;

    public Loading(Main game, Screen nextScreen, float loadingDuration) {
        this.game = game;
        this.nextScreen = nextScreen;
        this.loadingDuration = loadingDuration;
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        timer = 0f;
    }

    @Override
    public void render(float delta) {
        timer += delta;

        if (timer >= loadingDuration) {
            game.setScreen(nextScreen);
            dispose();
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float radius = 12f;
        float spacing = 35f;

        float startX = screenWidth - 120f;
        float y = 50f;

        float cycleTime = 1.2f;
        float localTime = timer % cycleTime;

        boolean dot1 = false;
        boolean dot2 = false;
        boolean dot3 = false;

        if (localTime < 0.3f) {
            dot1 = true;
        } else if (localTime < 0.6f) {
            dot1 = true;
            dot2 = true;
        } else if (localTime < 0.9f) {
            dot1 = true;
            dot2 = true;
            dot3 = true;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (dot1) {
            shapeRenderer.circle(startX, y, radius);
        }

        if (dot2) {
            shapeRenderer.circle(startX + spacing, y, radius);
        }

        if (dot3) {
            shapeRenderer.circle(startX + spacing * 2, y, radius);
        }

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
