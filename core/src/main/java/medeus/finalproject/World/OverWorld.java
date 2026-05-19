package medeus.finalproject.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Random;

public class OverWorld {

    private enum WorldType { OVERWORLD, DUNGEON, NETHER }

    private WorldType type;
    private Dungeon dungeon;
    private Nether nether;
    private Texture background;

    private static final float MAP_PADDING   = 80f;

    // Деревья
    private static final int   TREE_COUNT   = 15;
    private static final float TREE_RENDER_W = 128f;
    private static final float TREE_RENDER_H = 160f;
    private Texture objectSheet;
    private TextureRegion tree1;
    private TextureRegion tree2;
    private float[] treeX, treeY;
    private int[]   treeType;

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
                type = WorldType.OVERWORLD;

                // Фон BG1.jpg — тайлится 4×4 = 16 раз на карте 1600×1600
                background = new Texture(Gdx.files.internal("BG1.jpg"));
                background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

                // Деревья
                objectSheet = new Texture(Gdx.files.internal("OverWorld/Objects.png"));
                tree1 = new TextureRegion(objectSheet, 369, 0, 64, 80);
                tree2 = new TextureRegion(objectSheet, 433, 0, 64, 80);

                treeX    = new float[TREE_COUNT];
                treeY    = new float[TREE_COUNT];
                treeType = new int[TREE_COUNT];
                Random rng = new Random();
                for (int i = 0; i < TREE_COUNT; i++) {
                    treeX[i]    = MAP_PADDING + rng.nextFloat() * (1600f - MAP_PADDING * 2);
                    treeY[i]    = MAP_PADDING + rng.nextFloat() * (1600f - MAP_PADDING * 2);
                    treeType[i] = rng.nextInt(2);
                }
                break;
        }
    }

    public void render(SpriteBatch batch) {
        switch (type) {
            case DUNGEON: dungeon.render(batch); break;
            case NETHER:  nether.render(batch);  break;
            default:
                // 4×4 = 16 тайлов: srcW=4, srcH=4
                batch.draw(background, 0, 0, 1600, 1600, 0, 0, 4, 4);

                // Деревья поверх фона
                for (int i = 0; i < TREE_COUNT; i++) {
                    TextureRegion tr = (treeType[i] == 0) ? tree1 : tree2;
                    batch.draw(tr, treeX[i], treeY[i], TREE_RENDER_W, TREE_RENDER_H);
                }
                break;
        }
    }

    public void dispose() {
        switch (type) {
            case DUNGEON: dungeon.dispose(); break;
            case NETHER:  nether.dispose();  break;
            default:
                background.dispose();
                if (objectSheet != null) objectSheet.dispose();
                break;
        }
    }
}
