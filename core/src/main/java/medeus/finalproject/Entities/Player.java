package medeus.finalproject.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.List;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Player {

    protected float x, y;
    protected float speed = 200;
    protected int hp, attack;
    protected Rectangle hitbox;
    protected Texture spriteSheet;
    protected TextureRegion[][] splitFrames;
    protected Animation<TextureRegion> walkDown, walkLeft, walkRight, walkUp;
    protected TextureRegion idleDown, idleLeft, idleRight, idleUp;
    protected float stateTime;
    protected boolean moving;
    protected String direction = "down";

    protected float attackCooldown = 1.0f;
    protected float attackTimer = 0f;

    protected abstract void loadStats();
    protected abstract void loadAnimation();
    public abstract void performAttack(List<EnemyAbstract> enemies);
    public abstract void renderHUD(SpriteBatch batch, BitmapFont font, float camX, float camY);

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        loadStats();
        loadAnimation();
        hitbox = new Rectangle(x, y, 128, 128);
    }

    public abstract void renderAttackRange(ShapeRenderer sr);

    public void update(float delta) {
        stateTime += delta;
        moving = false;
        if (attackTimer > 0) attackTimer -= delta;

        float currentSpeed = speed;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) currentSpeed *= 2;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { y += currentSpeed * delta; moving = true; direction = "up"; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { y -= currentSpeed * delta; moving = true; direction = "down"; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { x -= currentSpeed * delta; moving = true; direction = "left"; }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { x += currentSpeed * delta; moving = true; direction = "right"; }

        x = Math.max(0, Math.min(x, 1600));
        y = Math.max(0, Math.min(y, 1600));
        hitbox.setPosition(x, y);
    }

    public boolean canAttack() { return attackTimer <= 0; }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;
        if (moving) {
            switch (direction) {
                case "up": currentFrame = walkUp.getKeyFrame(stateTime, true); break;
                case "left": currentFrame = walkLeft.getKeyFrame(stateTime, true); break;
                case "right": currentFrame = walkRight.getKeyFrame(stateTime, true); break;
                default: currentFrame = walkDown.getKeyFrame(stateTime, true); break;
            }
        } else {
            switch (direction) {
                case "up": currentFrame = idleUp; break;
                case "left": currentFrame = idleLeft; break;
                case "right": currentFrame = idleRight; break;
                default: currentFrame = idleDown; break;
            }
        }
        batch.draw(currentFrame, x, y, 128, 128);
    }

    public void dispose() { spriteSheet.dispose(); }
    public void takeDamage(int damage) { hp -= damage; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getHp() { return hp; }
    public int getAttack() { return attack; }
    public float getAttackTimer() { return attackTimer; }
    public float getAttackCooldown() { return attackCooldown; }
    public Rectangle getHitbox() { return hitbox; }
    public Rectangle getBounds() { return hitbox; }
}
