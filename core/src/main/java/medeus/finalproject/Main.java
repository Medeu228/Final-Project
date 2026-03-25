package medeus.finalproject;

import com.badlogic.gdx.Game;
import medeus.finalproject.Screens.GameScreen;
import medeus.finalproject.Menu.MainMenuScreenDemo;

public class Main extends Game{

    @Override
    public void create() {
        setScreen(new MainMenuScreenDemo(this));
    }

    @Override
    public void render () {
        // ОБЯЗАТЕЛЬНО: Вызываем render базового класса Game,
        // иначе setScreen() не будет работать.
        super.render();
    }

    @Override
    public void dispose () {
        super.dispose();
        // Очистка ресурсов главного класса, если они есть
    }
}
