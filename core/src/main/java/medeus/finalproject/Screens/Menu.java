package medeus.finalproject.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import medeus.finalproject.Main;

public class Menu implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;

    public Menu(final Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());

        createBasicSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton startButton = new TextButton("START GAME", skin);
        TextButton exitButton = new TextButton("EXIT", skin);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // ПОДСТРАИВАЕМСЯ ПОД ТВОР КOD: вызываем пустой конструктор
                game.setScreen(new GameScreen());
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        table.add(startButton).fillX().uniformX().pad(10).row();
        table.add(exitButton).fillX().uniformX().pad(10);
    }

    private void createBasicSkin() {
        skin = new Skin();
        // Используем стандартный шрифт LibGDX (проверь наличие файла в assets)
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = skin.getFont("default-font");
        btnStyle.up = skin.newDrawable("white", Color.GRAY);
        btnStyle.down = skin.newDrawable("white", Color.BLACK);
        btnStyle.fontColor = Color.WHITE;

        skin.add("default", btnStyle);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
