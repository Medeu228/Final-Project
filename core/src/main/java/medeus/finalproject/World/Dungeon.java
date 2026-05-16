package medeus.finalproject.World;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Dungeon {

    private Texture floor;

    public Dungeon() {
        floor = new Texture("696.jpg");
        floor.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    public void render(SpriteBatch batch) {
        batch.draw(floor, 0, 0, 1600, 1600, 0, 0, 4, 4);
    }

    public void dispose() {
        floor.dispose();
    }
}
