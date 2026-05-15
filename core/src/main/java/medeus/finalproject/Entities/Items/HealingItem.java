package medeus.finalproject.Entities.Items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class HealingItem {

    private float x, y;
    private static final float SIZE = 24f;
    private static final int HEAL_AMOUNT = 30;

    private Texture texture;
    private Rectangle bounds;
    private boolean collected = false;

    public HealingItem(float x, float y, Texture texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
    }

    public void render(SpriteBatch batch) {
        if (!collected) {
            batch.draw(texture, x, y, SIZE, SIZE);
        }
    }

    public boolean checkPickup(Rectangle playerBounds) {
        if (!collected && bounds.overlaps(playerBounds)) {
            collected = true;
            return true;
        }
        return false;
    }

    public boolean isCollected() { return collected; }
    public int getHealAmount()   { return HEAL_AMOUNT; }
}