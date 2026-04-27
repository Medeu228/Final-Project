package medeus.finalproject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import medeus.finalproject.Screens.Menu;
import medeus.finalproject.Screens.GameScreen;

public class Main extends Game{

    public Batch batch;
    public boolean devMode = false;

    public boolean godMode = false;
    public boolean freeSpawn = false;
    public boolean showDebugInfo = false;

    @Override
    public void create() {
        setScreen(new Menu(this));
    }

    @Override
    public void render() {
        super.render();
    }
}
