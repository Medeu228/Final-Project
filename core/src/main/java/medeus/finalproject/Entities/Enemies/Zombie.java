package medeus.finalproject.Entities.Enemies;

import com.badlogic.gdx.graphics.Texture;
import medeus.finalproject.Entities.EnemyAbstract;

public class Zombie extends EnemyAbstract {

    public Zombie(float x, float y) {
        super(x, y);
    }

    @Override
    protected void loadStats() {
        hp = 60;
        attack = 10;
    }

    @Override
    protected void loadTexture() {
        texture = new Texture("Zombie.jpeg");
    }
}
