package medeus.finalproject.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Player {

    protected float x;
    protected float y;
    protected float speed = 200;
    protected int hp;
    protected int attack;
    protected Texture texture;
    protected Rectangle bounds;
    protected Rectangle hitbox;
    protected Texture spriteSheet;
    protected TextureRegion[][] splitFrames;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> walkAnimation;
    protected float stateTime;
    protected boolean moving;
    protected abstract void loadStats();
    protected abstract void loadAnimation();
    protected Animation<TextureRegion> walkDown;
    protected Animation<TextureRegion> walkLeft;
    protected Animation<TextureRegion> walkRight;
    protected Animation<TextureRegion> walkUp;

    protected TextureRegion idleDown;
    protected TextureRegion idleLeft;
    protected TextureRegion idleRight;
    protected TextureRegion idleUp;

    protected String direction = "down";


    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        loadStats();
        loadAnimation();

        hitbox = new Rectangle(x, y, 128, 128);
    }

    public void update(float delta) {
        stateTime += delta;
        moving = false;

        float currentSpeed = speed;

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            currentSpeed *= 2;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += currentSpeed * delta;
            moving = true;
            direction = "up";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= currentSpeed * delta;
            moving = true;
            direction = "down";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= currentSpeed * delta;
            moving = true;
            direction = "left";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += currentSpeed * delta;
            moving = true;
            direction = "right";
        }

        float mapWidth = 1600;
        float mapHeight = 1600;

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > mapWidth) x = mapWidth;
        if (y > mapHeight) y = mapHeight;

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
    public void dispose() {
        spriteSheet.dispose();
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public Rectangle getBounds() {
        return bounds;
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public int getHp() { return hp; }
    public int getAttack() { return attack; }
}
