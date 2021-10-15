package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

abstract public class Weapon extends Sprite {
    int damage;
    int range;
    int attackspeed; // time in ms between hits, higher value is slower
    long timeOfLastHit;
    float xOffset = .5f;
    float yOffset = .7f;
    float absoluteRotation = 20;
    int rotationfFlip = -1;

    public Weapon(int _damage, int _range, int _attackspeed, TextureRegion _texture) {
        super(_texture);
        damage = _damage;
        range = _range;
        attackspeed = _attackspeed;
        this.setScale(1 / 16.f);
        this.setRotation(absoluteRotation * rotationfFlip);
    }

    public void draw(SpriteBatch batch, float x, float y) {
        setCenter(x + xOffset, y + yOffset);
        super.draw(batch);
    }

    public void flipx() {
        flip(true, false);
        xOffset = -xOffset;
        rotationfFlip = -rotationfFlip;
        this.setRotation(absoluteRotation * rotationfFlip);
    }

    abstract public void update(float dt);

    /**
     * Performs an attack
     */
    abstract public void attack();

}
