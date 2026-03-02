package medeus.finalproject.Entities.Heroes;

import com.badlogic.gdx.graphics.Texture;
import medeus.finalproject.Entities.Player;

public class Archer extends Player {

    public Archer(float x, float y) {
        super(x, y);
    }

    @Override
    protected void loadStats() {
        hp = 100;
        attack = 15;
    }

    @Override
    protected void loadTexture() {
        texture = new Texture("Archer.jpeg");
    }
}
