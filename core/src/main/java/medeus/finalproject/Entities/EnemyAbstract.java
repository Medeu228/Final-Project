package medeus.finalproject.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public abstract class EnemyAbstract {

    protected float x;
    protected float y;
    protected int hp;
    protected int attack;
    protected float speed = 75;
    protected Texture texture;
    protected Rectangle bounds;
    protected Rectangle hitbox;
    protected Texture spriteSheet;
    protected TextureRegion[][] splitFrames;

    protected Animation<TextureRegion> walkDown;
    protected Animation<TextureRegion> walkLeft;
    protected Animation<TextureRegion> walkRight;
    protected Animation<TextureRegion> walkUp;

    protected TextureRegion idleDown;
    protected TextureRegion idleLeft;
    protected TextureRegion idleRight;
    protected TextureRegion idleUp;

    protected float stateTime;
    protected boolean moving;
    protected String direction = "down";

    public EnemyAbstract(float x, float y) {
        this.x = x;
        this.y = y;
        loadStats();
        loadAnimation();
        hitbox = new Rectangle(x, y, 128, 128);
    }

    protected abstract void loadStats();
    protected abstract void loadAnimation();

    public void update(float delta, float playerX, float playerY) {
        stateTime += delta;
        moving = false;

        float dx = playerX - x;
        float dy = playerY - y;

        if (Math.abs(dx) > 2 || Math.abs(dy) > 2) {
            moving = true;
        }

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                x += speed * delta;
                direction = "right";
            } else if (dx < 0) {
                x -= speed * delta;
                direction = "left";
            }
        } else {
            if (dy > 0) {
                y += speed * delta;
                direction = "up";
            } else if (dy < 0) {
                y -= speed * delta;
                direction = "down";
            }
        }

        hitbox.setPosition(x, y);
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (moving) {
            switch (direction) {
                case "up":
                    currentFrame = walkUp.getKeyFrame(stateTime, true);
                    break;
                case "left":
                    currentFrame = walkLeft.getKeyFrame(stateTime, true);
                    break;
                case "right":
                    currentFrame = walkRight.getKeyFrame(stateTime, true);
                    break;
                default:
                    currentFrame = walkDown.getKeyFrame(stateTime, true);
                    break;
            }
        } else {
            switch (direction) {
                case "up":
                    currentFrame = idleUp;
                    break;
                case "left":
                    currentFrame = idleLeft;
                    break;
                case "right":
                    currentFrame = idleRight;
                    break;
                default:
                    currentFrame = idleDown;
                    break;
            }
        }

        batch.draw(currentFrame, x, y, 128, 128);
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public int getHp() { return hp; }
    public int getAttack() { return attack; }
}
