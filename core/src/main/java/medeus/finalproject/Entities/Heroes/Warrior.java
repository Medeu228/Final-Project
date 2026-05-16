package medeus.finalproject.Entities.Heroes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import medeus.finalproject.Entities.EnemyAbstract;
import medeus.finalproject.Entities.Player;
import java.util.List;

public class Warrior extends Player {

    private int level;
    private int maxHp;              // максимальное HP — фиксируется при loadStats
    private float attackRadius;
    private float sectorAngle;

    private Texture pixel;          // 1×1 белый пиксель для рисования баров

    private Texture idleSheet;
    private Texture walkSheet;
    private Texture runSheet;
    private Texture attackSheet;
    private Texture walkAttackSheet;
    private Texture runAttackSheet;

    private Animation<TextureRegion> idleDown,  idleLeft,  idleRight,  idleUp;
    private Animation<TextureRegion> runDown,   runLeft,   runRight,   runUp;
    private Animation<TextureRegion> atkDown,   atkLeft,   atkRight,   atkUp;
    private Animation<TextureRegion> wAtkDown,  wAtkLeft,  wAtkRight,  wAtkUp;
    private Animation<TextureRegion> rAtkDown,  rAtkLeft,  rAtkRight,  rAtkUp;

    private boolean isRunning   = false;
    private boolean isAttacking = false;
    private boolean wasMoving   = false;
    private boolean wasRunning  = false;

    private float attackAnimTimer = 0f;

    private static final float DUR_ATK      = 8 * 0.10f;  // 0.80с
    private static final float DUR_WALK_ATK = 6 * 0.10f;  // 0.60с
    private static final float DUR_RUN_ATK  = 8 * 0.08f;  // 0.64с

    // ─── Конструктор ──────────────────────────────────────────────────────────

    public Warrior(float x, float y, int level) {
        super(x, y);
        this.level = level;
        disposeSheets();
        loadStats();
        maxHp = hp;   // фиксируем максимум после реального loadStats()
        loadAnimation();

        // 1×1 белый пиксель — используется для рисования цветных прямоугольников
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();
    }

    // ─── Характеристики ───────────────────────────────────────────────────────

    @Override
    protected void loadStats() {
        switch (level) {
            case 2:
                hp = 300; attack = 55; attackCooldown = 0.75f;
                attackRadius = 210f; sectorAngle = 120f;
                break;
            case 3:
                hp = 400; attack = 75; attackCooldown = 0.65f;
                attackRadius = 235f; sectorAngle = 150f;
                break;
            default: // level 1 (и level=0 при первом вызове из super)
                hp = 220; attack = 40; attackCooldown = 0.90f;
                attackRadius = 175f; sectorAngle =  90f;
                break;
        }
    }

    // ─── Загрузка анимаций ────────────────────────────────────────────────────

    @Override
    protected void loadAnimation() {
        int lvl = (level == 0) ? 1 : level;
        String p = "Спрайты/Перс " + lvl + " лвл/Player_lvl" + lvl;

        idleSheet       = loadAnimsWithCounts(p + "_idle.png",        0.12f, idleAnims  = new Animation[4], new int[]{12, 12, 12, 4});
        spriteSheet     = loadAnims(p + "_walk.png",                  0.12f, walkAnims  = new Animation[4]);
        runSheet        = loadAnims(p + "_run.png",                   0.08f, runAnims   = new Animation[4]);
        attackSheet     = loadAnims(p + "_attack.png",                0.10f, atkAnims   = new Animation[4]);
        walkAttackSheet = loadAnims(p + "_walk_attack.png",           0.10f, wAtkAnims  = new Animation[4]);
        runAttackSheet  = loadAnims(p + "_run_attack.png",            0.08f, rAtkAnims  = new Animation[4]);

        idleDown  = idleAnims[0];  idleLeft  = idleAnims[1];  idleRight  = idleAnims[2];  idleUp  = idleAnims[3];
        runDown   = runAnims[0];   runLeft   = runAnims[1];   runRight   = runAnims[2];   runUp   = runAnims[3];
        atkDown   = atkAnims[0];   atkLeft   = atkAnims[1];   atkRight   = atkAnims[2];   atkUp   = atkAnims[3];
        wAtkDown  = wAtkAnims[0];  wAtkLeft  = wAtkAnims[1];  wAtkRight  = wAtkAnims[2];  wAtkUp  = wAtkAnims[3];
        rAtkDown  = rAtkAnims[0];  rAtkLeft  = rAtkAnims[1];  rAtkRight  = rAtkAnims[2];  rAtkUp  = rAtkAnims[3];

        walkDown  = walkAnims[0]; walkLeft  = walkAnims[1]; walkRight  = walkAnims[2]; walkUp  = walkAnims[3];
    }

    private Animation[] idleAnims, walkAnims, runAnims, atkAnims, wAtkAnims, rAtkAnims;

    private Texture loadAnims(String path, float frameDur, Animation[] out) {
        Texture tex = new Texture(path);
        TextureRegion[][] frames = TextureRegion.split(tex, 64, 64);
        for (int row = 0; row < 4; row++) {
            int count = frames[row].length;
            TextureRegion[] arr = new TextureRegion[count];
            for (int i = 0; i < count; i++) arr[i] = frames[row][i];
            out[row] = new Animation<>(frameDur, arr);
        }
        return tex;
    }

    private Texture loadAnimsWithCounts(String path, float frameDur, Animation[] out, int[] counts) {
        Texture tex = new Texture(path);
        TextureRegion[][] frames = TextureRegion.split(tex, 64, 64);
        for (int row = 0; row < 4; row++) {
            int count = counts[row];
            TextureRegion[] arr = new TextureRegion[count];
            for (int i = 0; i < count; i++) arr[i] = frames[row][i];
            out[row] = new Animation<>(frameDur, arr);
        }
        return tex;
    }

    // ─── Обновление ───────────────────────────────────────────────────────────

    @Override
    public void update(float delta) {
        super.update(delta);
        isRunning = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        if (attackAnimTimer > 0) {
            attackAnimTimer -= delta;
            if (attackAnimTimer <= 0) isAttacking = false;
        }
    }

    // ─── Атака (сектор) ───────────────────────────────────────────────────────

    @Override
    public void performAttack(List<EnemyAbstract> enemies) {
        if (!canAttack()) return;
        attackTimer     = attackCooldown;
        wasMoving       = moving;
        wasRunning      = isRunning;
        isAttacking     = true;
        attackAnimTimer = wasRunning ? DUR_RUN_ATK : (wasMoving ? DUR_WALK_ATK : DUR_ATK);

        float cx = x + 64, cy = y + 64;
        float facingAngle = directionToAngle(direction);
        float half = sectorAngle / 2f;

        for (EnemyAbstract e : enemies) {
            float ex = e.getHitbox().x + 64, ey = e.getHitbox().y + 64;
            float dx = ex - cx, dy = ey - cy;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist > attackRadius) continue;

            float angleToEnemy = (float) Math.toDegrees(Math.atan2(dy, dx));
            if (angleDiff(angleToEnemy, facingAngle) <= half) {
                e.takeDamage(attack);
            }
        }
    }

    private float angleDiff(float a, float b) {
        float diff = ((a - b + 180 + 360) % 360) - 180;
        return Math.abs(diff);
    }

    private float directionToAngle(String dir) {
        switch (dir) {
            case "up":   return 90f;
            case "left": return 180f;
            case "down": return 270f;
            default:     return 0f;
        }
    }

    // ─── Рендер ───────────────────────────────────────────────────────────────

    @Override
    public void render(SpriteBatch batch) {
        Animation<TextureRegion> anim = pickAnimation();
        float t    = isAttacking ? (getAnimDuration() - attackAnimTimer) : stateTime;
        boolean loop = !isAttacking;
        batch.draw(anim.getKeyFrame(t, loop), x, y, 128, 128);
    }

    private Animation<TextureRegion> pickAnimation() {
        if (isAttacking) return wasRunning ? rAtkByDir() : (wasMoving ? wAtkByDir() : atkByDir());
        if (moving && isRunning) return runByDir();
        if (moving)              return walkByDir();
        return idleByDir();
    }

    private float getAnimDuration() {
        return wasRunning ? DUR_RUN_ATK : (wasMoving ? DUR_WALK_ATK : DUR_ATK);
    }

    private Animation<TextureRegion> idleByDir() {
        switch (direction) { case "up": return idleUp; case "left": return idleLeft; case "right": return idleRight; default: return idleDown; }
    }
    private Animation<TextureRegion> walkByDir() {
        switch (direction) { case "up": return walkUp; case "left": return walkLeft; case "right": return walkRight; default: return walkDown; }
    }
    private Animation<TextureRegion> runByDir() {
        switch (direction) { case "up": return runUp; case "left": return runLeft; case "right": return runRight; default: return runDown; }
    }
    private Animation<TextureRegion> atkByDir() {
        switch (direction) { case "up": return atkUp; case "left": return atkLeft; case "right": return atkRight; default: return atkDown; }
    }
    private Animation<TextureRegion> wAtkByDir() {
        switch (direction) { case "up": return wAtkUp; case "left": return wAtkLeft; case "right": return wAtkRight; default: return wAtkDown; }
    }
    private Animation<TextureRegion> rAtkByDir() {
        switch (direction) { case "up": return rAtkUp; case "left": return rAtkLeft; case "right": return rAtkRight; default: return rAtkDown; }
    }

    // ─── HUD ──────────────────────────────────────────────────────────────────

    @Override
    public void renderHUD(SpriteBatch batch, BitmapFont font, float camX, float camY) {

        // ── Позиция HUD (верхний левый угол экрана) ──────────────────────────
        float left = camX - 390f;
        float top  = camY + 285f;

        // ── Размеры health bar ────────────────────────────────────────────────
        float barW    = 220f;
        float barH    = 18f;
        float borderW = 2f;

        float ratio = Math.max(0f, (float) hp / maxHp);

        // Цвет заливки: зелёный → жёлтый → красный
        Color fillColor;
        if (ratio > 0.5f) {
            fillColor = new Color(1f - (ratio - 0.5f) * 2f, 1f, 0f, 1f); // зелёный→жёлтый
        } else {
            fillColor = new Color(1f, ratio * 2f, 0f, 1f);                // жёлтый→красный
        }

        // ── Рисуем рамку (тёмный фон немного шире) ───────────────────────────
        drawRect(batch, Color.BLACK,
            left - borderW,
            top - barH - borderW,
            barW + borderW * 2,
            barH + borderW * 2);

        // ── Фон бара (тёмно-серый) ────────────────────────────────────────────
        drawRect(batch, new Color(0.15f, 0.15f, 0.15f, 1f),
            left, top - barH, barW, barH);

        // ── Заливка по текущему HP ────────────────────────────────────────────
        if (ratio > 0f) {
            drawRect(batch, fillColor,
                left, top - barH, barW * ratio, barH);
        }

        // ── Текст: HP числами внутри бара ─────────────────────────────────────
        batch.setColor(Color.WHITE);
        font.draw(batch,
            "HP  " + hp + " / " + maxHp,
            left + 6f,
            top - 3f);

        // ── Заголовок над баром ───────────────────────────────────────────────
        font.draw(batch, "Warrior  Lv." + level, left, top + 14f);

        // ── Кулдаун атаки ─────────────────────────────────────────────────────
        float cd = getAttackTimer();
        String atkText = cd > 0
            ? String.format("[F]  ATK CD: %.1fs", cd)
            : "[F]  ATTACK READY";
        font.draw(batch, atkText, left, top - barH - 10f);

        // Сбрасываем цвет batch обратно в белый (чтобы не ломать другие спрайты)
        batch.setColor(Color.WHITE);
    }

    /** Рисует закрашенный прямоугольник через 1×1 пиксель. */
    private void drawRect(SpriteBatch batch, Color color, float x, float y, float w, float h) {
        batch.setColor(color);
        batch.draw(pixel, x, y, w, h);
        batch.setColor(Color.WHITE);
    }

    @Override
    public void renderAttackRange(ShapeRenderer sr) {
        float cx = x + 64, cy = y + 64;
        float facing = directionToAngle(direction);
        float startAngle = facing - sectorAngle / 2f;
        sr.setColor(1f, 0.3f, 0.3f, 0.25f);
        sr.arc(cx, cy, attackRadius, startAngle, sectorAngle, 32);
    }

    // ─── Dispose ──────────────────────────────────────────────────────────────

    private void disposeSheets() {
        if (spriteSheet     != null) { spriteSheet.dispose();     spriteSheet     = null; }
        if (idleSheet       != null) { idleSheet.dispose();       idleSheet       = null; }
        if (runSheet        != null) { runSheet.dispose();        runSheet        = null; }
        if (attackSheet     != null) { attackSheet.dispose();     attackSheet     = null; }
        if (walkAttackSheet != null) { walkAttackSheet.dispose(); walkAttackSheet = null; }
        if (runAttackSheet  != null) { runAttackSheet.dispose();  runAttackSheet  = null; }
    }

    @Override
    public void dispose() {
        disposeSheets();
        if (pixel != null) pixel.dispose();
    }
}
