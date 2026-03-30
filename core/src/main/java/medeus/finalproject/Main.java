package medeus.finalproject;

import com.badlogic.gdx.Game;
import medeus.finalproject.Screens.Menu;
import medeus.finalproject.Screens.GameScreen;

public class Main extends Game{

    @Override
    public void create() {
        setScreen(new Menu(this));
    }

    @Override
    public void render() {
        super.render();
    }
}
