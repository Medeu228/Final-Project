package medeus.finalproject.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Player {

    private float x;
    private float y;
    private float speed = 200;

    private ShapeRenderer shape;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        shape = new ShapeRenderer();
    }

    public void update(float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.W)) y += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) y -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) x -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) x += speed * delta;
    }

    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        batch.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.rect(x, y, 40, 40);
        shape.end();

        batch.begin();
    }
}
