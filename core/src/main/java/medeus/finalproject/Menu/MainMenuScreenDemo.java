package medeus.finalproject.Menu;
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
import medeus.finalproject.Screens.GameScreen;

public class MainMenuScreenDemo implements Screen{
    // Ссылка на основной класс игры (чтобы менять экраны)
    // Замените "MyRPGGame" на название вашего главного класса (который extends Game)
    private final Main game;
    private Stage stage;
    private Skin skin;

    public MainMenuScreenDemo(final medeus.finalproject.Main game) {
        this.game = game;

        // 1. Создаем Сцену - контейнер для UI
        stage = new Stage(new ScreenViewport());

        // ВАЖНО: Отдаем ввод данных сцене, чтобы кнопки нажимались
        Gdx.input.setInputProcessor(stage);

        // 2. Создаем Скин программно (чтобы не возиться с JSON)
        createBasicSkin();

        // 3. Создаем Таблицу для верстки
        Table table = new Table();
        table.setFillParent(true); // Таблица займет весь экран
        // table.setDebug(true); // Раскомментируйте, чтобы видеть границы ячеек
        stage.addActor(table);

        // 4. Создаем кнопки
        TextButton startButton = new TextButton("START GAME", skin);
        TextButton musicButton = new TextButton("MUSIC: ON", skin);
        TextButton outButton = new TextButton("EXIT", skin);

        // 5. Добавляем логику нажатия (Слушатели)
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MENU", "Start pressed - Переходим к игре");
                // game.setScreen(new GameScreen(game)); // Раскомментируйте, когда создадите GameScreen
                game.setScreen(new medeus.finalproject.Screens.GameScreen(game));
            }
        });

        musicButton.addListener(new ChangeListener() {
            boolean musicOn = true;
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicOn = !musicOn;
                musicButton.setText(musicOn ? "MUSIC: ON" : "MUSIC: OFF");
                Gdx.app.log("MENU", "Music toggled: " + musicOn);
                // Здесь будет логика включения/выключения музыки
            }
        });

        outButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MENU", "Exit pressed - Выходим");
                Gdx.app.exit(); // Закрыть приложение
            }
        });

        // 6. Размещаем кнопки в таблице (Верстка)
        // Добавляем заголовок (просто текст)
        // table.add("MY RPG GAME").padBottom(50).row(); // Нужен Label style в скине

        // Добавляем кнопки: fillX() растянет по ширине, regularX() сделает одинаковыми
        table.add(startButton).fillX().uniformX().padBottom(10);
        table.row(); // Переход на новую строку
        table.add(musicButton).fillX().uniformX().padBottom(10);
        table.row();
        table.add(outButton).fillX().uniformX();
    }

    /**
     * Вспомогательный метод для создания простейшего скина в коде.
     * Использует стандартный шрифт и генерирует белую текстуру для фона кнопок.
     */
    private void createBasicSkin() {
        skin = new Skin();

        // ЗАГРУЗКА ШРИФТА: Убедитесь, что файл default.fnt есть в assets/
        // Если у вас другой шрифт, замените название файла
        BitmapFont font = new BitmapFont(Gdx.files.internal("default.fnt"));
        skin.add("default-font", font);

        // ГЕНЕРАЦИЯ ТЕКСТУРЫ: Создаем простую белую картинку 1x1 пиксель
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white_pixel", new Texture(pixmap));

        // НАСТРОЙКА СТИЛЯ КНОПКИ
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = skin.getFont("default-font");

        // Фон кнопки в разных состояниях (используем белую текстуру, подкрашенную кодом)
        textButtonStyle.up = skin.newDrawable("white_pixel", Color.DARK_GRAY); // Обычное состояние
        textButtonStyle.down = skin.newDrawable("white_pixel", Color.NAVY);    // При нажатии
        textButtonStyle.over = skin.newDrawable("white_pixel", Color.LIGHT_GRAY); // При наведении (ПК)

        textButtonStyle.fontColor = Color.WHITE; // Цвет текста

        skin.add("default", textButtonStyle); // Регистрируем стиль как дефолтный
    }

    @Override
    public void render(float delta) {
        // Очистка экрана (темно-синий фон)
        ScreenUtils.clear(0, 0, 0.2f, 1);

        // Обновляем логику сцены (анимации, если есть)
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        // Рисуем сцену
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Обновляем вьюпорт сцены при изменении размера окна
        stage.getViewport().update(width, height, true);
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        // Обязательно освобождаем память!
        stage.dispose();
        skin.dispose();
    }
}
