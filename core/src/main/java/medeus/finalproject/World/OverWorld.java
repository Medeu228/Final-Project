package medeus.finalproject.World;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class OverWorld {

    private Texture background;


    public OverWorld(int level) {
        String textureName;
        switch (level) {
            default: textureName = "BG1.jpg";  break;
        }
        background = new Texture(textureName);
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    public void render(SpriteBatch batch) {
        batch.draw(background, 0, 0, 1600, 1600, 0, 0, 16, 16);
    }

    public void dispose() {
        background.dispose();
    }
}
