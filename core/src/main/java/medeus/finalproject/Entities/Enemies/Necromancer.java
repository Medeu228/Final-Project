package medeus.finalproject.Entities.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import medeus.finalproject.Entities.EnemyAbstract;
import medeus.finalproject.Entities.Player;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Necromancer extends EnemyAbstract {

    private static final int SPELL_FRAME_W  = 144;
    private static final int SPELL_FRAME_H  = 112;
    private static final int RENDER_SIZE    = 192;
    private static final float MAGIC_RANGE  = 400f;

    private int attackCount = 0;

    private int maxHp;
    private int currentPhase = 1;
    private List<EnemyAbstract> enemyList;

    // Файерболы
    private Texture fireballTexture;
    private List<Fireball> fireballs = new ArrayList<>();
    private Player targetPlayer;

    private Texture spell1Sheet;
    private Animation<TextureRegion> fireAnimLeft;
    private Animation<TextureRegion> fireAnimRight;
    private boolean isCastingFireball = false;
    private float fireAnimTimer = 0f;
    private static final float FIRE_ANIM_DUR = 19 * 0.10f; // 1.9с

    public Necromancer(float x, float y) {
        super(x, y);
        maxHp = hp;
        hitbox = new com.badlogic.gdx.math.Rectangle(x, y, RENDER_SIZE, RENDER_SIZE);
        fireballTexture = new Texture("Спрайты/Necromancer/fireball(80x80).png");
    }

    public int getMaxHp() { return maxHp; }

    public void setEnemyList(List<EnemyAbstract> list) {
        this.enemyList = list;
    }

    // ─── Фазы ─────────────────────────────────────────────────────────────────

    private void updatePhase() {
        float ratio = (float) hp / maxHp;
        int target;
        if      (ratio > 0.6f) target = 1;
        else if (ratio > 0.3f) target = 2;
        else                   target = 3;

        if (target == currentPhase) return;
        currentPhase = target;
        switch (currentPhase) {
            case 2: speed = 100; attack = 35; break;
            case 3: speed = 160; attack = 50; break;
        }
    }

    // ─── Призыв миньонов ──────────────────────────────────────────────────────

    private void summonMinions() {
        if (enemyList == null) return;
        switch (currentPhase) {
            case 1:
                enemyList.add(new Skeleton(x + 100, y));
                break;
            case 2:
                enemyList.add(new Skeleton(x + 100, y));
                enemyList.add(new Zombie(x - 100, y));
                break;
            case 3:
                enemyList.add(new Skeleton(x + 100, y));
                enemyList.add(new Skeleton(x - 100, y));
                enemyList.add(new Zombie(x, y + 100));
                enemyList.add(new Zombie(x, y - 100));
                break;
        }
    }

    // ─── Запуск файербола ─────────────────────────────────────────────────────

    private void launchFireball() {
        if (targetPlayer == null) return;
        float startX = x + RENDER_SIZE / 2f;
        float startY = y + RENDER_SIZE / 2f;
        float targetX = targetPlayer.getHitbox().x + 64;
        float targetY = targetPlayer.getHitbox().y + 64;
        fireballs.add(new Fireball(startX, startY, targetX, targetY, fireballTexture));
    }

    // ─── Обновление ───────────────────────────────────────────────────────────

    @Override
    public void update(float delta, float playerX, float playerY) {
        updatePhase();

        if (attackTimer > 0) attackTimer -= delta;

        // Таймер анимации призыва (Spell2)
        if (attackAnimTimer > 0) {
            attackAnimTimer -= delta;

            if (hitPending && attackAnimTimer <= attackAnimDuration / 2f) {
                hitPending = false;
                if (attackCount % 2 == 0) {
                    summonMinions();
                } else {
                    isCastingFireball = true;
                    fireAnimTimer = FIRE_ANIM_DUR;
                }
            }

            if (attackAnimTimer <= 0) isAttacking = false;
        }

        // Таймер анимации файербола (Spell1) — ОТДЕЛЬНО от attackAnimTimer
        if (fireAnimTimer > 0) {
            fireAnimTimer -= delta;
            if (isCastingFireball && fireAnimTimer <= FIRE_ANIM_DUR / 2f) {
                isCastingFireball = false;
                launchFireball();
            }
        }

        // Обновляем файерболы
        if (targetPlayer != null) {
            Iterator<Fireball> it = fireballs.iterator();
            while (it.hasNext()) {
                Fireball fb = it.next();
                fb.update(delta, targetPlayer);
                if (!fb.isActive()) it.remove();
            }
        }

        stateTime += delta;
        moving = false;

        // Во время призыва — стоим, во время файербола — двигаемся
        if (isAttacking) {
            hitbox.setPosition(x, y);
            return;
        }

        float dx = playerX - x;
        float dy = playerY - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

// Останавливаемся если уже в зоне атаки
        if (dist > MAGIC_RANGE * 0.7f) {
            if (Math.abs(dx) > 2 || Math.abs(dy) > 2) moving = true;

            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) { x += speed * delta; direction = "right"; }
                else        { x -= speed * delta; direction = "left";  }
            } else {
                if (dy > 0) { y += speed * delta; direction = "up";   }
                else        { y -= speed * delta; direction = "down";  }
            }
        } else {
            moving = false; // стоим на месте и атакуем
        }

        hitbox.setPosition(x, y);
    }

    // ─── Атака ────────────────────────────────────────────────────────────────

    @Override
    public boolean tryAttackPlayer(Player player) {
        float ex = hitbox.x + 64, ey = hitbox.y + 64;
        float px = player.getHitbox().x + 64, py = player.getHitbox().y + 64;
        float dist = (float) Math.sqrt((ex - px) * (ex - px) + (ey - py) * (ey - py));

        if (dist <= MAGIC_RANGE && attackTimer <= 0) {
            attackTimer     = attackCooldown;
            isAttacking     = true;
            attackAnimTimer = attackAnimDuration;
            hitPending      = true;
            pendingTarget   = player;
            targetPlayer    = player; // сохраняем для файербола
            attackCount++;
            return true;
        }

        return false;
    }

    // ─── Характеристики ───────────────────────────────────────────────────────

    @Override
    protected void loadStats() {
        hp     = 800;
        attack = 20;
        speed  = 60;
        attackCooldown = 2.0f;
    }

    // ─── Анимации ─────────────────────────────────────────────────────────────

    @Override
    protected void loadAnimation() {
        spriteSheet = new Texture("Спрайты/Necromancer/Walk.png");
        TextureRegion[][] split = TextureRegion.split(spriteSheet, SPELL_FRAME_W, SPELL_FRAME_H);

        TextureRegion[] leftFrames  = split[0];
        TextureRegion[] rightFrames = mirror(leftFrames);

        walkLeft  = new Animation<>(0.12f, leftFrames);
        walkRight = new Animation<>(0.12f, rightFrames);
        walkDown  = new Animation<>(0.12f, rightFrames);
        walkUp    = new Animation<>(0.12f, leftFrames);

        idleLeft  = leftFrames[0];
        idleRight = rightFrames[0];
        idleDown  = rightFrames[0];
        idleUp    = leftFrames[0];

        attackSpriteSheet = new Texture("Спрайты/Necromancer/Spell2.png");
        TextureRegion[][] spellSplit = TextureRegion.split(attackSpriteSheet, SPELL_FRAME_W, SPELL_FRAME_H);

        TextureRegion[] spellLeft  = spellSplit[0];
        TextureRegion[] spellRight = mirror(spellLeft);

        attackAnimLeft  = new Animation<>(0.10f, spellLeft);
        attackAnimRight = new Animation<>(0.10f, spellRight);
        attackAnimDown  = new Animation<>(0.10f, spellRight);
        attackAnimUp    = new Animation<>(0.10f, spellLeft);

        attackAnimDuration = spellLeft.length * 0.10f;

        spell1Sheet = new Texture("Спрайты/Necromancer/Spell1.png");
        TextureRegion[][] spell1Split = TextureRegion.split(spell1Sheet, SPELL_FRAME_W, SPELL_FRAME_H);
        TextureRegion[] spell1Left  = spell1Split[0];
        TextureRegion[] spell1Right = mirror(spell1Left);
        fireAnimLeft  = new Animation<>(0.10f, spell1Left);
        fireAnimRight = new Animation<>(0.10f, spell1Right);
    }


    private TextureRegion[] mirror(TextureRegion[] src) {
        TextureRegion[] out = new TextureRegion[src.length];
        for (int i = 0; i < src.length; i++) {
            out[i] = new TextureRegion(src[i]);
            out[i].flip(true, false);
        }
        return out;
    }

    // ─── Рендер ───────────────────────────────────────────────────────────────

    @Override
    public void render(SpriteBatch batch) {
        // Рендерим файерболы
        for (Fireball fb : fireballs) {
            fb.render(batch);
        }

        TextureRegion currentFrame;

        if (isCastingFireball && fireAnimRight != null) {
            // Spell1 — анимация броска файербола
            float elapsed = FIRE_ANIM_DUR - fireAnimTimer;
            currentFrame = direction.equals("left")
                    ? fireAnimLeft.getKeyFrame(elapsed, false)
                    : fireAnimRight.getKeyFrame(elapsed, false);
        } else if (isAttacking && attackAnimDown != null) {
            // Spell2 — анимация призыва
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
                case "left":  currentFrame = idleLeft;  break;
                case "right": currentFrame = idleRight; break;
                case "up":    currentFrame = idleUp;    break;
                default:      currentFrame = idleDown;  break;
            }
        }

        batch.draw(currentFrame, x, y, RENDER_SIZE, RENDER_SIZE);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (fireballTexture != null) fireballTexture.dispose();
        if (spell1Sheet != null) spell1Sheet.dispose();
    }
}