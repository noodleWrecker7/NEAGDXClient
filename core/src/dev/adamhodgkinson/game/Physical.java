package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public abstract class Physical {
    Fixture fixture;
    Body body;
    Sprite sprite;

    public void draw(SpriteBatch batch){
        sprite.draw(batch);
    }

    public void setPos(float x, float y){
        sprite.setPosition(x, y);
    }

    public void update(float dt) {

    }
}
