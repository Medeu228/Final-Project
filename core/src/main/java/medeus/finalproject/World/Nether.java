package medeus.finalproject.World;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Nether {

    private static final float MAP_SIZE = 1600f;

    private Texture floor;

    public Nether() {
        floor = new Texture("background3.png");
        floor.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    public void render(SpriteBatch batch) {
        batch.draw(floor, 0, 0, MAP_SIZE, MAP_SIZE, 0, 0, 50, 50);
    }

    public void dispose() {
        floor.dispose();
    }
}
