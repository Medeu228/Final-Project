package medeus.finalproject.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;
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

    // Коллизии — заполняются только для OVERWORLD
    private final List<Rectangle> collisionRects = new ArrayList<>();

    // Размеры хитбоксов ствола дерева (центр снизу спрайта 128×160)
    private static final float TRUNK_W      = 36f;
    private static final float TRUNK_H      = 50f;
    private static final float TRUNK_OFFSET_X = (TREE_RENDER_W - TRUNK_W) / 2f; // = 46
    private static final float TRUNK_OFFSET_Y = 0f; // от нижнего края спрайта

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

                // Строим хитбоксы коллизий
                // Валун — загружаем сохранённый offset если есть
                com.badlogic.gdx.Preferences prefs =
                    Gdx.app.getPreferences("collision_settings");
                float savedBOX = prefs.getFloat("bOX", 0f);
                float savedBOY = prefs.getFloat("bOY", 0f);
                float savedBW  = prefs.getFloat("bW",  BOULDER_RENDER_SIZE);
                float savedBH  = prefs.getFloat("bH",  BOULDER_RENDER_SIZE);

                for (int i = 0; i < BOULDER_COUNT; i++) {
                    collisionRects.add(new Rectangle(
                        boulderX[i] + savedBOX,
                        boulderY[i] + savedBOY,
                        savedBW, savedBH
                    ));
                }
                // Дерево — ствол с загрузкой из preferences
                com.badlogic.gdx.Preferences treePrefs =
                    Gdx.app.getPreferences("collision_settings");
                float t_x1 = treePrefs.getFloat("t_x1", TRUNK_OFFSET_X);
                float t_y1 = treePrefs.getFloat("t_y1", TRUNK_OFFSET_Y);
                float t_x2 = treePrefs.getFloat("t_x2", TRUNK_OFFSET_X + TRUNK_W);
                float t_y2 = treePrefs.getFloat("t_y2", TRUNK_OFFSET_Y + TRUNK_H);
                for (int i = 0; i < TREE_COUNT; i++) {
                    collisionRects.add(new Rectangle(
                        treeX[i] + t_x1, treeY[i] + t_y1,
                        t_x2 - t_x1,     t_y2 - t_y1
                    ));
                }
                break;
        }
    }

    public List<Rectangle> getCollisionRects() {
        return collisionRects;
    }

    /** Возвращает позицию первого валуна в мире: [x, y]. */
    public float[] getBoulderPositions() {
        if (boulderX == null || boulderX.length == 0) return new float[]{0f, 0f};
        return new float[]{ boulderX[0], boulderY[0] };
    }

    /** Возвращает позицию первого дерева в мире: [x, y]. */
    public float[] getTreePositions() {
        if (treeX == null || treeX.length == 0) return new float[]{0f, 0f};
        return new float[]{ treeX[0], treeY[0] };
    }

    /** Обновляет хитбокс первого валуна (индекс 0 в collisionRects). */
    public void setBoulderCollision(float x, float y, float w, float h) {
        if (collisionRects.isEmpty()) return;
        collisionRects.get(0).set(x, y, w, h);
    }

    /**
     * Обновляет хитбокс первого дерева в ряду.
     * В collisionRects: [0]=boulder, [1]=tree1, [2]=tree2
     */
    public void setTreeCollision(float x, float y, float w, float h) {
        if (collisionRects.size() < 2) return;
        collisionRects.get(1).set(x, y, w, h);
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
    /**
     * DEV: расставляет все объекты в один ряд по центру карты.
     * Порядок: трава1, трава2, трава3, куст, валун, дерево1, дерево2
     * Вызывать сразу после конструктора с level=1.
     */
    public void placeInRow() {
        if (type != WorldType.OVERWORLD) return;
        collisionRects.clear();

        float y    = 700f;   // высота ряда
        float x    = 150f;   // начальная позиция
        float step = 200f;   // шаг между объектами

        // Трава — по одному экземпляру каждого вида
        grassX[0]  = x;           grassY[0]  = y + 20f;  x += step;
        grassX2[0] = x;           grassY2[0] = y + 20f;  x += step;
        grassX3[0] = x;           grassY3[0] = y + 20f;  x += step;
        // Остальные экземпляры прячем за карту
        for (int i = 1; i < GRASS_COUNT; i++) {
            grassX[i]  = -500f; grassY[i]  = -500f;
            grassX2[i] = -500f; grassY2[i] = -500f;
            grassX3[i] = -500f; grassY3[i] = -500f;
        }

        // Куст
        bushX[0] = x; bushY[0] = y + 20f; x += step;
        for (int i = 1; i < BUSH_COUNT; i++) { bushX[i] = -500f; bushY[i] = -500f; }

        // Валун
        boulderX[0] = x; boulderY[0] = y; x += step;
        for (int i = 1; i < BOULDER_COUNT; i++) { boulderX[i] = -500f; boulderY[i] = -500f; }

        // Дерево 1
        treeX[0] = x; treeY[0] = y; treeType[0] = 0; x += step;
        // Дерево 2
        treeX[1] = x; treeY[1] = y; treeType[1] = 1;
        for (int i = 2; i < TREE_COUNT; i++) { treeX[i] = -500f; treeY[i] = -500f; }

        // Пересчитываем коллизии под новые позиции
        collisionRects.add(new Rectangle(
            boulderX[0], boulderY[0], BOULDER_RENDER_SIZE, BOULDER_RENDER_SIZE
        ));
        for (int i = 0; i < 2; i++) {
            collisionRects.add(new Rectangle(
                treeX[i] + TRUNK_OFFSET_X, treeY[i] + TRUNK_OFFSET_Y, TRUNK_W, TRUNK_H
            ));
        }
    }

    /**
     * DEV: рисует все коллизионные прямоугольники красным контуром.
     * Вызывать между shapeRenderer.begin() и shapeRenderer.end().
     */
    public void renderDebugCollisions(ShapeRenderer sr) {
        sr.setColor(Color.RED);
        for (Rectangle r : collisionRects) {
            sr.rect(r.x, r.y, r.width, r.height);
        }
    }

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
