package medeus.finalproject.Entities.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import medeus.finalproject.Entities.EnemyAbstract;

/**
 * Скелет — быстрый стекло-пушка.
 * Мало HP, высокая скорость, высокий урон, часто атакует.
 */
public class Skeleton extends EnemyAbstract {

    public Skeleton(float x, float y) {
        super(x, y);
    }

    @Override
    protected void loadStats() {
        hp             = 50;    // мало HP — убивается с 1-2 ударов
        attack         = 20;    // высокий урон
        speed          = 100f;  // быстрый
        attackCooldown = 1.2f;  // часто бьёт
    }

    @Override
    protected void loadAnimation() {
        // Walk — Down/Up/Left/Right (строки 0/1/2/3)
        spriteSheet = new Texture("Спрайты/Скелет спрайты/Skeleton_walk.png");
        splitFrames = TextureRegion.split(spriteSheet, 64, 64);

        idleDown  = splitFrames[0][0];
        idleUp    = splitFrames[1][0];
        idleLeft  = splitFrames[2][0];
        idleRight = splitFrames[3][0];

        int frameCount = splitFrames[0].length;
        TextureRegion[] d = new TextureRegion[frameCount];
        TextureRegion[] u = new TextureRegion[frameCount];
        TextureRegion[] l = new TextureRegion[frameCount];
        TextureRegion[] r = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            d[i] = splitFrames[0][i]; u[i] = splitFrames[1][i];
            l[i] = splitFrames[2][i]; r[i] = splitFrames[3][i];
        }
        walkDown  = new Animation<>(0.10f, d); // чуть быстрее анимация
        walkUp    = new Animation<>(0.10f, u);
        walkLeft  = new Animation<>(0.10f, l);
        walkRight = new Animation<>(0.10f, r);

        // Attack — 9 фреймов
        attackSpriteSheet = new Texture("Спрайты/Скелет спрайты/Skeleton_attack.png");
        TextureRegion[][] atkFrames = TextureRegion.split(attackSpriteSheet, 64, 64);
        int atkCount = atkFrames[0].length;
        TextureRegion[] ad = new TextureRegion[atkCount];
        TextureRegion[] au = new TextureRegion[atkCount];
        TextureRegion[] al = new TextureRegion[atkCount];
        TextureRegion[] ar = new TextureRegion[atkCount];
        for (int i = 0; i < atkCount; i++) {
            ad[i] = atkFrames[0][i]; au[i] = atkFrames[1][i];
            al[i] = atkFrames[2][i]; ar[i] = atkFrames[3][i];
        }
        attackAnimDown  = new Animation<>(0.08f, ad); // быстрая анимация атаки
        attackAnimUp    = new Animation<>(0.08f, au);
        attackAnimLeft  = new Animation<>(0.08f, al);
        attackAnimRight = new Animation<>(0.08f, ar);
        attackAnimDuration = atkCount * 0.08f; // 0.72с
    }
}
