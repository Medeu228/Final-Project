package medeus.finalproject.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;



public abstract class EnemyAbstract {

    protected float x;
    protected float y;
    protected int hp;
    protected int attack;
    protected float speed = 75;
    protected Texture texture;
    protected Rectangle bounds;
    protected Rectangle hitbox;

    public EnemyAbstract(float x, float y) {
        this.x = x;
        this.y = y;
        loadStats();
        loadTexture();
        hitbox = new Rectangle(x, y, 128, 128);
    }

    protected abstract void loadStats();
    protected abstract void loadTexture();

    public void update(float delta, float playerX, float playerY) {

        if (playerX > x) x += speed * delta;
        if (playerX < x) x -= speed * delta;

        if (playerY > y) y += speed * delta;
        if (playerY < y) y -= speed * delta;

        hitbox.setPosition(x, y);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, 128, 128);
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void dispose() {
        texture.dispose();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public int getHp() { return hp; }
    public int getAttack() { return attack; }
}
