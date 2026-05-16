package medeus.finalproject.World;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class OverWorld {

    private enum WorldType { OVERWORLD, DUNGEON, NETHER }

    private WorldType type;
    private Dungeon dungeon;
    private Nether nether;
    private Texture background;

    public OverWorld(int level) {
        switch (level) {
            case 2:
                type    = WorldType.DUNGEON;
                dungeon = new Dungeon();
                break;
            case 3:
                type   = WorldType.NETHER;
                nether = new Nether();
                break;
            default: // уровень 1
                type       = WorldType.OVERWORLD;
                background = new Texture("BG1.jpg");
                background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
                break;
        }
    }

    public void render(SpriteBatch batch) {
        switch (type) {
            case DUNGEON:  dungeon.render(batch); break;
            case NETHER:   nether.render(batch);  break;
            default:       batch.draw(background, 0, 0, 1600, 1600, 0, 0, 16, 16); break;
        }
    }

    public void dispose() {
        switch (type) {
            case DUNGEON:  dungeon.dispose();    break;
            case NETHER:   nether.dispose();     break;
            default:       background.dispose(); break;
        }
    }
}
