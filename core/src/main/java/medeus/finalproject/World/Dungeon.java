package medeus.finalproject.World;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Dungeon {

    private Texture floor;
    private Texture wallTop;
    private Texture wallBottom;
    private Texture wallLeft;
    private Texture wallRight;

    private static final float MAP_SIZE    = 1600f;
    private static final float WALL_THICK  = 64f;

    public Dungeon() {
        floor = new Texture("696.jpg");
        floor.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        wallTop    = createWallTexture();
        wallBottom = createWallTexture();
        wallLeft   = createWallTexture();
        wallRight  = createWallTexture();
    }


    private Texture createWallTexture() {
        com.badlogic.gdx.graphics.Pixmap px =
                new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        px.setColor(0.16f, 0.16f, 0.16f, 1f);
        px.fill();
        Texture t = new Texture(px);
        px.dispose();
        return t;
    }

    public void render(SpriteBatch batch) {
        batch.draw(floor, 0, 0, 1600, 1600, 0, 0, 4, 4);
    }

    public void dispose() {
        floor.dispose();
        wallTop.dispose();
        wallBottom.dispose();
        wallLeft.dispose();
        wallRight.dispose();
    }
}
