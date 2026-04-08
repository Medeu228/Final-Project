package medeus.finalproject.World;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TestingRange {

    private Texture background;

    public TestingRange() {
        background = new Texture("background.png");
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    public void render(SpriteBatch batch) {
        batch.draw(background, 0, 0, 1600, 1600, 0, 0, 50, 50);
    }

    public void dispose() {
        background.dispose();
    }
}
