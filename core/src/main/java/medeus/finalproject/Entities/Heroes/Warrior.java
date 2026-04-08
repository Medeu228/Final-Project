package medeus.finalproject.Entities.Heroes;

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

    private static final float ATTACK_RADIUS = 175f; // ~1.75 клетки

    public Warrior(float x, float y) { super(x, y); }

    @Override
    protected void loadStats() {
        hp = 200;
        attack = 35;
        attackCooldown = 0.9f;
    }

    @Override
    public void performAttack(List<EnemyAbstract> enemies) {
        if (!canAttack()) return;
        attackTimer = attackCooldown;

        float cx = x + 64, cy = y + 64; // центр спрайта
        for (EnemyAbstract e : enemies) {
            float ex = e.getHitbox().x + 64, ey = e.getHitbox().y + 64;
            float dist = (float) Math.sqrt((ex - cx) * (ex - cx) + (ey - cy) * (ey - cy));
            if (dist <= ATTACK_RADIUS) {
                e.takeDamage(attack);
            }
        }
    }

    @Override
    public void renderHUD(SpriteBatch batch, BitmapFont font, float camX, float camY) {
        float cd = getAttackTimer();
        String text = cd > 0
            ? String.format("[F] ATK CD: %.1fs", cd)
            : "[F] ATTACK READY";
        font.draw(batch, "Warrior | HP: " + hp, camX - 380, camY + 280);
        font.draw(batch, text, camX - 380, camY + 250);
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

    @Override
    public void renderAttackRange(ShapeRenderer sr) {
        sr.setColor(1, 0.3f, 0.3f, 0.3f); // красный
        sr.circle(x + 64, y + 64, 225f);
    }
}
