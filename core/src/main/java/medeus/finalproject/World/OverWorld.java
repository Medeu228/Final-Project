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

    // Трава
    private static final int   GRASS_COUNT      = 25;
    private static final float GRASS_RENDER_SIZE = 64f;
    private Texture grassSheet;
    private TextureRegion grassRegion;
    private TextureRegion grassRegion2;
    private TextureRegion grassRegion3;
    private float[] grassX,  grassY;
    private float[] grassX2, grassY2;
    private float[] grassX3, grassY3;

    // Деревья
    private static final int   TREE_COUNT    = 15;
    private static final float TREE_RENDER_W = 128f;
    private static final float TREE_RENDER_H = 160f;
    private Texture objectSheet;
    private TextureRegion tree1;
    private TextureRegion tree2;
    private float[] treeX, treeY;
    private int[]   treeType;

    // Кусты
    private static final int   BUSH_COUNT       = 10;
    private static final float BUSH_RENDER_SIZE  = 84f;  // 42×2
    private TextureRegion bush;
    private float[] bushX, bushY;

    // Валуны
    private static final int   BOULDER_COUNT       = 10;
    private static final float BOULDER_RENDER_SIZE  = 128f; // 64×2
    private TextureRegion boulder;
    private float[] boulderX, boulderY;

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

                // Фон BG1.jpg — растягиваем на всю карту без тайлинга
                background = new Texture(Gdx.files.internal("BG1.jpg"));

                // Трава
                grassSheet  = new Texture(Gdx.files.internal("Overworld/spots_lianas.png"));
                grassRegion  = new TextureRegion(grassSheet,   8, 442, 16, 16);
                grassRegion2 = new TextureRegion(grassSheet, 310, 504, 16, 16);
                grassRegion3 = new TextureRegion(grassSheet,   2, 467, 16, 16);

                // Деревья
                objectSheet = new Texture(Gdx.files.internal("OverWorld/Objects.png"));
                tree1   = new TextureRegion(objectSheet, 369, 0,   64, 80);
                tree2   = new TextureRegion(objectSheet, 433, 0,   64, 80);
                bush    = new TextureRegion(objectSheet, 499, 146, 42, 42);
                boulder = new TextureRegion(objectSheet, 449, 78,  64, 64);

                Random rng = new Random();

                // Общий пул всех занятых позиций — чтобы объекты разных типов не накладывались
                int totalObjects = GRASS_COUNT * 3 + TREE_COUNT + BUSH_COUNT + BOULDER_COUNT;
                float[] allX = new float[totalObjects];
                float[] allY = new float[totalObjects];
                int placed = 0;

                // Трава
                grassX  = new float[GRASS_COUNT]; grassY  = new float[GRASS_COUNT];
                grassX2 = new float[GRASS_COUNT]; grassY2 = new float[GRASS_COUNT];
                grassX3 = new float[GRASS_COUNT]; grassY3 = new float[GRASS_COUNT];
                placed = placeObjects(grassX,  grassY,  GRASS_COUNT,  80f, rng, allX, allY, placed);
                placed = placeObjects(grassX2, grassY2, GRASS_COUNT,  80f, rng, allX, allY, placed);
                placed = placeObjects(grassX3, grassY3, GRASS_COUNT,  80f, rng, allX, allY, placed);

                // Деревья
                treeX    = new float[TREE_COUNT];
                treeY    = new float[TREE_COUNT];
                treeType = new int[TREE_COUNT];
                placed = placeObjects(treeX, treeY, TREE_COUNT, 160f, rng, allX, allY, placed);
                for (int i = 0; i < TREE_COUNT; i++) treeType[i] = rng.nextInt(2);

                // Кусты
                bushX = new float[BUSH_COUNT]; bushY = new float[BUSH_COUNT];
                placed = placeObjects(bushX, bushY, BUSH_COUNT, 120f, rng, allX, allY, placed);

                // Валуны
                boulderX = new float[BOULDER_COUNT]; boulderY = new float[BOULDER_COUNT];
                placed = placeObjects(boulderX, boulderY, BOULDER_COUNT, 150f, rng, allX, allY, placed);
                break;
        }
    }

    public void renderBackground(SpriteBatch batch) {
        switch (type) {
            case DUNGEON: dungeon.render(batch); break;
            case NETHER:  nether.render(batch);  break;
            default:
                batch.draw(background, 0, 0, 1600, 1600);

                // Трава — под объектами и персонажем
                for (int i = 0; i < GRASS_COUNT; i++) {
                    batch.draw(grassRegion,  grassX[i],  grassY[i],  GRASS_RENDER_SIZE, GRASS_RENDER_SIZE);
                    batch.draw(grassRegion2, grassX2[i], grassY2[i], GRASS_RENDER_SIZE, GRASS_RENDER_SIZE);
                    batch.draw(grassRegion3, grassX3[i], grassY3[i], GRASS_RENDER_SIZE, GRASS_RENDER_SIZE);
                }
                break;
        }
    }

    public void renderObjects(SpriteBatch batch) {
        if (type != WorldType.OVERWORLD) return;

        // Деревья, кусты, валуны — поверх игрока и врагов
        for (int i = 0; i < BUSH_COUNT; i++) {
            batch.draw(bush, bushX[i], bushY[i], BUSH_RENDER_SIZE, BUSH_RENDER_SIZE);
        }
        for (int i = 0; i < TREE_COUNT; i++) {
            TextureRegion tr = (treeType[i] == 0) ? tree1 : tree2;
            batch.draw(tr, treeX[i], treeY[i], TREE_RENDER_W, TREE_RENDER_H);
        }
        for (int i = 0; i < BOULDER_COUNT; i++) {
            batch.draw(boulder, boulderX[i], boulderY[i], BOULDER_RENDER_SIZE, BOULDER_RENDER_SIZE);
        }
    }

    /**
     * Размещает count объектов в outX/outY с минимальным расстоянием minDist
     * от всех уже размещённых объектов в общем пуле allX/allY.
     * Возвращает новое значение placed (сколько всего объектов в пуле).
     */
    private int placeObjects(float[] outX, float[] outY, int count, float minDist,
                             Random rng, float[] allX, float[] allY, int placed) {
        float range = 1600f - MAP_PADDING * 2;
        for (int i = 0; i < count; i++) {
            float x, y;
            int attempts = 0;
            do {
                x = MAP_PADDING + rng.nextFloat() * range;
                y = MAP_PADDING + rng.nextFloat() * range;
                attempts++;
            } while (!isFarEnough(x, y, allX, allY, placed, minDist) && attempts < 300);
            outX[i]       = x;
            outY[i]       = y;
            allX[placed]  = x;
            allY[placed]  = y;
            placed++;
        }
        return placed;
    }

    private boolean isFarEnough(float nx, float ny, float[] xs, float[] ys,
                                int count, float minDist) {
        for (int i = 0; i < count; i++) {
            float dx = xs[i] - nx, dy = ys[i] - ny;
            if (dx * dx + dy * dy < minDist * minDist) return false;
        }
        return true;
    }

    public void dispose() {
        switch (type) {
            case DUNGEON: dungeon.dispose(); break;
            case NETHER:  nether.dispose();  break;
            default:
                background.dispose();
                if (grassSheet  != null) grassSheet.dispose();
                if (objectSheet != null) objectSheet.dispose();
                break;
        }
    }
}
