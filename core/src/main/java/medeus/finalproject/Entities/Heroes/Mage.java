package medeus.finalproject.Entities.Heroes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import medeus.finalproject.Entities.Player;

public class Mage extends Player {

    public Mage(float x, float y) {
        super(x, y);
    }

    @Override
    protected void loadStats() {
        hp = 150;
        attack = 20;
    }

    @Override
    protected void loadAnimation() {
        spriteSheet = new Texture("тестовый спрайт.png");
        splitFrames = TextureRegion.split(spriteSheet, 64, 64);

        idleDown = splitFrames[0][0];
        idleLeft = splitFrames[1][0];
        idleRight = splitFrames[2][0];
        idleUp = splitFrames[3][0];

        TextureRegion[] downFrames = new TextureRegion[3];
        TextureRegion[] leftFrames = new TextureRegion[3];
        TextureRegion[] rightFrames = new TextureRegion[3];
        TextureRegion[] upFrames = new TextureRegion[3];

        downFrames[0] = splitFrames[0][1];
        downFrames[1] = splitFrames[0][2];
        downFrames[2] = splitFrames[0][3];

        leftFrames[0] = splitFrames[1][1];
        leftFrames[1] = splitFrames[1][2];
        leftFrames[2] = splitFrames[1][3];

        rightFrames[0] = splitFrames[2][1];
        rightFrames[1] = splitFrames[2][2];
        rightFrames[2] = splitFrames[2][3];

        upFrames[0] = splitFrames[3][1];
        upFrames[1] = splitFrames[3][2];
        upFrames[2] = splitFrames[3][3];

        walkDown = new Animation<>(0.15f, downFrames);
        walkLeft = new Animation<>(0.15f, leftFrames);
        walkRight = new Animation<>(0.15f, rightFrames);
        walkUp = new Animation<>(0.15f, upFrames);
    }
}
