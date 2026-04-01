package medeus.finalproject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import medeus.finalproject.Screens.Menu;
import medeus.finalproject.Screens.GameScreen;

public class Main extends Game{

    public Batch batch;

    @Override
    public void create() {
        setScreen(new Menu(this));
    }

    @Override
    public void render() {
        super.render();
    }
}
