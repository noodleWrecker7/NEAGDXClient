package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

abstract public class Weapon extends Sprite {
    int damage;
    int range;
    int attackspeed; // time in ms between hits, higher value is slower
    long timeOfLastHit;
    float xOffset = .25f;
    float yOffset = -.25f;
    float absoluteRotation = 20;
    // negative is facing right
    int rotationfFlip = -1;

    abstract public void destroy();

    public Weapon(int _damage, int _range, int _attackspeed, TextureRegion _texture) {
        super(_texture);
        damage = _damage;
        range = _range;
        attackspeed = _attackspeed;
        this.setScale(1 / 16.f);
        this.setRotation(absoluteRotation * rotationfFlip);
        this.setOrigin(_texture.getRegionWidth() / 2f, 0); // makes it rotate about the center of the bottom edge
    }

    public void draw(SpriteBatch batch, float x, float y) {
        setCenter(x + xOffset, y + yOffset + this.getHeight() / 2); // because of the way the origin is set, the drawing of the weapon has to be offset in the y axis
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
