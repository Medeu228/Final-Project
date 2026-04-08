package medeus.finalproject.Entities.Heroes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import medeus.finalproject.Entities.EnemyAbstract;
import medeus.finalproject.Entities.Player;
import java.util.List;

public class Mage extends Player {

    private static final float ATTACK_RADIUS = 300f; // ~3 клетки, без слепой зоны

    private float mana = 100f;
    private static final float MAX_MANA = 100f;
    private static final float MANA_REGEN = 8f;  // в секунду
    private static final float MANA_COST = 25f;

    public Mage(float x, float y) { super(x, y); }

    @Override
    protected void loadStats() {
        hp = 120;
        attack = 40;
        attackCooldown = 1.2f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        mana = Math.min(MAX_MANA, mana + MANA_REGEN * delta);
    }

    @Override
    public void performAttack(List<EnemyAbstract> enemies) {
        if (!canAttack()) return;
        if (mana < MANA_COST) return; // нет маны

        float cx = x + 64, cy = y + 64;
        boolean hit = false;
        for (EnemyAbstract e : enemies) {
            float ex = e.getHitbox().x + 64, ey = e.getHitbox().y + 64;
            float dist = (float) Math.sqrt((ex - cx) * (ex - cx) + (ey - cy) * (ey - cy));
            if (dist <= ATTACK_RADIUS) {
                e.takeDamage(attack);
                hit = true;
            }
        }

        if (hit) {
            mana -= MANA_COST;
            attackTimer = attackCooldown;
        }
    }

    @Override
    public void renderHUD(SpriteBatch batch, BitmapFont font, float camX, float camY) {
        float cd = getAttackTimer();
        String atkText = mana < MANA_COST
            ? "[F] NOT ENOUGH MANA"
            : cd > 0
            ? String.format("[F] ATK CD: %.1fs", cd)
            : "[F] CAST READY";
        font.draw(batch, "Mage | HP: " + hp, camX - 380, camY + 280);
        font.draw(batch, String.format("Mana: %.0f/%.0f", mana, MAX_MANA), camX - 380, camY + 250);
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
