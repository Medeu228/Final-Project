package medeus.finalproject.Entities.Heroes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import medeus.finalproject.Entities.EnemyAbstract;
import medeus.finalproject.Entities.Player;
import java.util.List;

public class Archer extends Player {

    private static final float BLIND_ZONE = 300f;  // ~3 клетки — ближе не стреляет
    private static final float MAX_RANGE  = 900f;  // ~9 клеток

    private int arrows = 20;
    private static final int MAX_ARROWS = 20;

    public Archer(float x, float y) { super(x, y); }

    @Override
    protected void loadStats() {
        hp = 130;
        attack = 30;
        attackCooldown = 0.7f;
    }

    @Override
    public void performAttack(List<EnemyAbstract> enemies) {
        if (!canAttack()) return;
        if (arrows <= 0) return; // нет стрел

        float cx = x + 64, cy = y + 64;
        EnemyAbstract target = null;
        float minDist = Float.MAX_VALUE;

        for (EnemyAbstract e : enemies) {
            float ex = e.getHitbox().x + 64, ey = e.getHitbox().y + 64;
            float dist = (float) Math.sqrt((ex - cx) * (ex - cx) + (ey - cy) * (ey - cy));
            // только в кольце: вне слепой зоны и в пределах дальности
            if (dist >= BLIND_ZONE && dist <= MAX_RANGE && dist < minDist) {
                minDist = dist;
                target = e;
            }
        }

        if (target != null) {
            target.takeDamage(attack);
            arrows--;
            attackTimer = attackCooldown;
        }
    }

    @Override
    public void renderHUD(SpriteBatch batch, BitmapFont font, float camX, float camY) {
        float cd = getAttackTimer();
        String atkText = arrows <= 0
            ? "[F] NO ARROWS!"
            : cd > 0
            ? String.format("[F] ATK CD: %.1fs", cd)
            : "[F] SHOOT READY";
        font.draw(batch, "Archer | HP: " + hp, camX - 380, camY + 280);
        font.draw(batch, "Arrows: " + arrows + "/" + MAX_ARROWS, camX - 380, camY + 250);
        font.draw(batch, atkText, camX - 380, camY + 220);
    }

    @Override
    protected void loadAnimation() {
        spriteSheet = new Texture("тестовый спрайт.png");
        splitFrames = TextureRegion.split(spriteSheet, 64, 64);
        idleDown = splitFrames[0][0]; idleLeft = splitFrames[1][0];
        idleRight = splitFrames[2][0]; idleUp = splitFrames[3][0];
        TextureRegion[] d = {splitFrames[0][1], splitFrames[0][2], splitFrames[0][3]};
        TextureRegion[] l = {splitFrames[1][1], splitFrames[1][2], splitFrames[1][3]};
        TextureRegion[] r = {splitFrames[2][1], splitFrames[2][2], splitFrames[2][3]};
        TextureRegion[] u = {splitFrames[3][1], splitFrames[3][2], splitFrames[3][3]};
        walkDown = new Animation<>(0.15f, d); walkLeft = new Animation<>(0.15f, l);
        walkRight = new Animation<>(0.15f, r); walkUp = new Animation<>(0.15f, u);
    }
}
