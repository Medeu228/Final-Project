package medeus.finalproject.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Nether {

    private static final float MAP_SIZE  = 1600f;
    // sqrt(10) ≈ 3.162 — повторений по каждой оси, итого ~10 тайлов
    private static final float REPEAT    = 3.162f;
    // расстояние между стыками = 1600 / 3.162 ≈ 506px
    private static final float SEAM_STEP = MAP_SIZE / REPEAT;
    private static final int   SEAM_COUNT = (int) REPEAT; // 3 стыка
    private static final int   SEAM_W     = 20;

    private Texture floor;
    private Texture seamV;
    private Texture seamH;

    public Nether() {
        floor = new Texture("Nether.jpeg");
        floor.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        floor.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Pixmap sheet = new Pixmap(Gdx.files.internal("Nether.jpeg"));
        int tw = sheet.getWidth();
        int th = sheet.getHeight();

        Pixmap vp = new Pixmap(SEAM_W, th, sheet.getFormat());
        vp.drawPixmap(sheet, 0, 0, tw / 2 - SEAM_W / 2, 0, SEAM_W, th);
        seamV = new Texture(vp);
        seamV.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        vp.dispose();

        Pixmap hp = new Pixmap(tw, SEAM_W, sheet.getFormat());
        hp.drawPixmap(sheet, 0, 0, 0, th / 2 - SEAM_W / 2, tw, SEAM_W);
        seamH = new Texture(hp);
        seamH.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        hp.dispose();

        sheet.dispose();
    }

    public void render(SpriteBatch batch) {
        batch.draw(floor, 0, 0, MAP_SIZE, MAP_SIZE, 0, 0, REPEAT, REPEAT);

        for (int i = 1; i <= SEAM_COUNT; i++) {
            float sx = i * SEAM_STEP - SEAM_W / 2f;
            if (sx > 0 && sx < MAP_SIZE)
                batch.draw(seamV, sx, 0, SEAM_W, MAP_SIZE, 0, 0, 1, (int) REPEAT + 1);
        }

        for (int i = 1; i <= SEAM_COUNT; i++) {
            float sy = i * SEAM_STEP - SEAM_W / 2f;
            if (sy > 0 && sy < MAP_SIZE)
                batch.draw(seamH, 0, sy, MAP_SIZE, SEAM_W, 0, 0, (int) REPEAT + 1, 1);
        }
    }

    public void dispose() {
        floor.dispose();
        seamV.dispose();
        seamH.dispose();
    }
}
