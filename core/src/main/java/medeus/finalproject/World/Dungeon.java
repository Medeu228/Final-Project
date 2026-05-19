package medeus.finalproject.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Dungeon {

    private static final float MAP_SIZE  = 1600f;
    private static final int   TILE_COLS = 4;       // 4×4 = 16 повторений
    private static final int   TILE_ROWS = 4;
    private static final float TILE_W    = MAP_SIZE / TILE_COLS; // 400px
    private static final float TILE_H    = MAP_SIZE / TILE_ROWS; // 400px
    private static final int   SEAM_W    = 20;      // ширина заплатки в px

    private Texture floor;
    private Texture seamV;  // вертикальная заплатка (для стыков по X)
    private Texture seamH;  // горизонтальная заплатка (для стыков по Y)

    public Dungeon() {
        floor = new Texture("696.jpg");
        floor.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Вырезаем полоски из центра текстуры
        Pixmap sheet = new Pixmap(Gdx.files.internal("696.jpg"));
        int tw = sheet.getWidth();
        int th = sheet.getHeight();

        // Вертикальная заплатка: тонкий срез из центра по X
        Pixmap vp = new Pixmap(SEAM_W, th, sheet.getFormat());
        vp.drawPixmap(sheet, 0, 0, tw / 2 - SEAM_W / 2, 0, SEAM_W, th);
        seamV = new Texture(vp);
        seamV.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        vp.dispose();

        // Горизонтальная заплатка: тонкий срез из центра по Y
        Pixmap hp = new Pixmap(tw, SEAM_W, sheet.getFormat());
        hp.drawPixmap(sheet, 0, 0, 0, th / 2 - SEAM_W / 2, tw, SEAM_W);
        seamH = new Texture(hp);
        seamH.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        hp.dispose();

        sheet.dispose();
    }

    public void render(SpriteBatch batch) {
        // Основные тайлы 4×4
        floor.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        batch.draw(floor, 0, 0, MAP_SIZE, MAP_SIZE, 0, 0, TILE_COLS, TILE_ROWS);

        // Вертикальные заплатки на стыках x = 400, 800, 1200
        for (int col = 1; col < TILE_COLS; col++) {
            float sx = col * TILE_W - SEAM_W / 2f;
            batch.draw(seamV, sx, 0, SEAM_W, MAP_SIZE, 0, 0, 1, TILE_ROWS);
        }

        // Горизонтальные заплатки на стыках y = 400, 800, 1200
        for (int row = 1; row < TILE_ROWS; row++) {
            float sy = row * TILE_H - SEAM_W / 2f;
            batch.draw(seamH, 0, sy, MAP_SIZE, SEAM_W, 0, 0, TILE_COLS, 1);
        }
    }

    public void dispose() {
        floor.dispose();
        seamV.dispose();
        seamH.dispose();
    }
}
