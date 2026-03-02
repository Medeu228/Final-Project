package medeus.finalproject.Entities.Heroes;

import com.badlogic.gdx.graphics.Texture;
import medeus.finalproject.Entities.Player;

public class Warrior extends Player {

    public Warrior(float x, float y) {
        super(x, y);
    }

    @Override
    protected void loadStats() {
        hp = 150;
        attack = 20;
    }

    @Override
    protected void loadTexture() {
        texture = new Texture("Warrior.jpeg");
    }
}
