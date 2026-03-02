package medeus.finalproject.Entities.Heroes;

import com.badlogic.gdx.graphics.Texture;
import medeus.finalproject.Entities.Player;

public class Mage extends Player {

    public Mage(float x, float y) {
        super(x, y);
    }

    @Override
    protected void loadStats() {
        hp = 75;
        attack = 30;
    }

    @Override
    protected void loadTexture() {
        texture = new Texture("Mage.jpeg");
    }
}
