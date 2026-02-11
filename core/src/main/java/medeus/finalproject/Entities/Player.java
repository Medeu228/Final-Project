package medeus.finalproject.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {

    private float x;
    private float y;
    private float speed = 200;

    private Texture texture;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        texture = new Texture("player.png");
    }

    public void update(float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * delta;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }
}
