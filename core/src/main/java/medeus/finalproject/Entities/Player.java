package medeus.finalproject.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Player {

    protected float x;
    protected float y;
    protected float speed = 200;

    protected int hp;
    protected int attack;

    protected Texture texture;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        loadStats();
        loadTexture();
    }

    protected abstract void loadStats();
    protected abstract void loadTexture();

    public void update(float delta) {

        float currentSpeed = speed;

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            currentSpeed *= 2;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) y += currentSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) y -= currentSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) x -= currentSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) x += currentSpeed * delta;

        float mapWidth = 1600;
        float mapHeight = 1600;

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > mapWidth) x = mapWidth;
        if (y > mapHeight) y = mapHeight;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, 128, 128);
    }

    public void dispose() {
        texture.dispose();
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public int getHp() { return hp; }
    public int getAttack() { return attack; }
}
