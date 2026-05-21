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
    protected Texture spriteSheet;
    protected Texture attackSpriteSheet;
    protected TextureRegion[][] splitFrames;

    protected Rectangle hitbox;

    // Анимации ходьбы
    protected Animation<TextureRegion> walkDown;
    protected Animation<TextureRegion> walkLeft;
    protected Animation<TextureRegion> walkRight;
    protected Animation<TextureRegion> walkUp;

    // Idle-кадры (первый кадр ходьбы)
    protected TextureRegion idleDown;
    protected TextureRegion idleLeft;
    protected TextureRegion idleRight;
    protected TextureRegion idleUp;

    // Анимации атаки (заполняются в подклассах)
    protected Animation<TextureRegion> attackAnimDown;
    protected Animation<TextureRegion> attackAnimLeft;
    protected Animation<TextureRegion> attackAnimRight;
    protected Animation<TextureRegion> attackAnimUp;

    protected float stateTime;
    protected boolean moving;
    protected String direction = "down";

    protected float attackCooldown    = 1.5f;
    protected float attackTimer       = 0f;

    // Состояние анимации атаки
    protected boolean isAttacking     = false;
    protected float   attackAnimTimer = 0f;
    protected float   attackAnimDuration = 0f; // задаётся подклассом
    protected boolean hitPending     = false;   // ← добавь
    protected Player  pendingTarget;

    public EnemyAbstract(float x, float y) {
        this.x = x;
        this.y = y;
        loadStats();
        loadAnimation();
        hitbox = new Rectangle(x, y, 128, 128);
    }

    protected abstract void loadStats();
    protected abstract void loadAnimation();

    public void applyDifficultyScale(float multiplier) {
        hp     = Math.round(hp     * multiplier);
        attack = Math.round(attack * multiplier);
    }

    public void update(float delta, float playerX, float playerY) {

        if (attackTimer > 0) attackTimer -= delta;

        // Обратный отсчёт анимации атаки
        if (attackAnimTimer > 0) {
            attackAnimTimer -= delta;

            // Урон в середине анимации
            if (hitPending && attackAnimTimer <= attackAnimDuration / 2f) {
                hitPending = false;
                if (pendingTarget != null) {
                    pendingTarget.takeDamage(attack);
                }
            }

            if (attackAnimTimer <= 0) isAttacking = false;
        }

        stateTime += delta;
        moving = false;

        // Во время анимации атаки враг стоит на месте
        if (isAttacking) {
            hitbox.setPosition(x, y);
            return;
        }

        float dx = playerX - x;
        float dy = playerY - y;

        if (Math.abs(dx) > 2 || Math.abs(dy) > 2) moving = true;

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) { x += speed * delta; direction = "right"; }
            else        { x -= speed * delta; direction = "left";  }
        } else {
            if (dy > 0) { y += speed * delta; direction = "up";   }
            else        { y -= speed * delta; direction = "down";  }
        }

        hitbox.setPosition(x, y);
    }

    // Враг бьёт только когда подошёл вплотную
    private static final float MELEE_RANGE = 90f;

    public boolean tryAttackPlayer(Player player) {
        float ex = hitbox.x + 64, ey = hitbox.y + 64;
        float px = player.getHitbox().x + 64, py = player.getHitbox().y + 64;
        float dist = (float) Math.sqrt((ex - px) * (ex - px) + (ey - py) * (ey - py));

        if (dist <= MELEE_RANGE && attackTimer <= 0) {
            attackTimer = attackCooldown;
            if (attackAnimDown != null) {
                isAttacking     = true;
                attackAnimTimer = attackAnimDuration;
                hitPending      = true;
                pendingTarget   = player;
            } else {
                // если анимации нет — наносим сразу
                player.takeDamage(attack);
            }
            return true;
        }
        return false;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (isAttacking && attackAnimDown != null) {
            // Время с начала анимации атаки
            float elapsed = attackAnimDuration - attackAnimTimer;
            switch (direction) {
                case "up":    currentFrame = attackAnimUp.getKeyFrame(elapsed, false);    break;
                case "left":  currentFrame = attackAnimLeft.getKeyFrame(elapsed, false);  break;
                case "right": currentFrame = attackAnimRight.getKeyFrame(elapsed, false); break;
                default:      currentFrame = attackAnimDown.getKeyFrame(elapsed, false);  break;
            }
        } else if (moving) {
            switch (direction) {
                case "up":    currentFrame = walkUp.getKeyFrame(stateTime, true);    break;
                case "left":  currentFrame = walkLeft.getKeyFrame(stateTime, true);  break;
                case "right": currentFrame = walkRight.getKeyFrame(stateTime, true); break;
                default:      currentFrame = walkDown.getKeyFrame(stateTime, true);  break;
            }
        } else {
            switch (direction) {
                case "up":    currentFrame = idleUp;    break;
                case "left":  currentFrame = idleLeft;  break;
                case "right": currentFrame = idleRight; break;
                default:      currentFrame = idleDown;  break;
            }
        }

        batch.draw(currentFrame, x, y, 128, 128);
    }

    public void takeDamage(int damage) { hp -= damage; }
    public boolean isAlive()           { return hp > 0; }
    public Rectangle getHitbox()       { return hitbox; }
    public int getHp()                 { return hp; }
    public int getAttack()             { return attack; }

    public void dispose() {
        if (spriteSheet       != null) spriteSheet.dispose();
        if (attackSpriteSheet != null) attackSpriteSheet.dispose();
    }


}