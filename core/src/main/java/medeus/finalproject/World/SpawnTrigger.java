package medeus.finalproject.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class SpawnTrigger {

    private static final float SIZE         = 80f;
    private static final float INTERACT_RANGE = 160f;

    private float x, y;
    private Rectangle bounds;
    private Texture texture;
    private boolean activated = false;

    public SpawnTrigger(float mapWidth, float mapHeight) {
        x = mapWidth  / 2f - SIZE / 2f;
        y = mapHeight / 2f - SIZE / 2f;
        bounds = new Rectangle(x, y, SIZE, SIZE);

        Pixmap pixmap = new Pixmap((int) SIZE, (int) SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.YELLOW);
        pixmap.fill();
        pixmap.setColor(Color.ORANGE);
        pixmap.drawRectangle(0, 0, (int) SIZE, (int) SIZE);
        texture = new Texture(pixmap);
        pixmap.dispose();
    }


    public boolean isPlayerNearby(float playerX, float playerY) {
        float cx = x + SIZE / 2f;
        float cy = y + SIZE / 2f;
        float pcx = playerX + 64f;
        float pcy = playerY + 64f;
        float dist = (float) Math.sqrt((pcx - cx) * (pcx - cx) + (pcy - cy) * (pcy - cy));
        return dist <= INTERACT_RANGE;
    }


    public boolean checkActivation(float playerX, float playerY) {
        if (activated) return false;
        if (isPlayerNearby(playerX, playerY) && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            activated = true;
            return true;
        }
        return false;
    }

    public boolean isActivated() {
        return activated;
    }

    public void render(SpriteBatch batch, BitmapFont font, float playerX, float playerY) {
        if (activated) return; // после активации не рисуем

        batch.draw(texture, x, y, SIZE, SIZE);

        if (isPlayerNearby(playerX, playerY)) {
            font.draw(batch, "[E] to start wave", x - 20, y + SIZE + 20);
        }
    }

    public void dispose() {
        texture.dispose();
    }
}
