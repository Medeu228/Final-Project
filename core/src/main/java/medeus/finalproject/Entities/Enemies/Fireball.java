package medeus.finalproject.Entities.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import medeus.finalproject.Entities.Player;

public class Fireball {

    private float x, y;
    private float dirX, dirY;
    private static final float SPEED = 300f;
    private static final float SIZE  = 64f;
    private static final int   DAMAGE = 30;

    private Texture texture;
    private Animation<TextureRegion> anim;
    private float stateTime = 0f;
    private Rectangle hitbox;
    private boolean active = true;

    public Fireball(float startX, float startY, float targetX, float targetY, Texture texture) {
        this.x = startX;
        this.y = startY;
        this.texture = texture;

        // Направление к игроку
        float dx = targetX - startX;
        float dy = targetY - startY;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        this.dirX = dx / len;
        this.dirY = dy / len;

        // Загружаем анимацию
        TextureRegion[][] frames = TextureRegion.split(texture, 80, 80);
        anim = new Animation<>(0.08f, frames[0]);

        hitbox = new Rectangle(x, y, SIZE, SIZE);
    }

    public void update(float delta, Player player) {
        if (!active) return;

        stateTime += delta;
        x += dirX * SPEED * delta;
        y += dirY * SPEED * delta;
        hitbox.setPosition(x, y);

        // Попал в игрока
        if (hitbox.overlaps(player.getHitbox())) {
            player.takeDamage(DAMAGE);
            active = false;
        }

        // Улетел за карту
        if (x < 0 || x > 1600 || y < 0 || y > 1600) {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (!active) return;
        batch.draw(anim.getKeyFrame(stateTime, true), x, y, SIZE, SIZE);
    }

    public boolean isActive() { return active; }
}