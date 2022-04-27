package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dev.adamhodgkinson.WeaponData;

abstract public class Weapon extends Sprite {
    float damage = 1;
    float range = 1;
    float attackspeed = 1000; // time in ms between hits, higher value is slower
    long timeOfLastHit;
    float xOffset = .25f;
    float yOffset = -.25f;
    float absoluteRotation = 20;
    // negative is facing rig ht
    int rotationfFlip = -1;
    float knockback = 50f;

    /**
     * Destroys any objects, called when parent sprite dies
     */
    abstract public void destroy();

    /**Static helper method to create a weapon instance */
    public static Weapon createFromData(WeaponData data, TextureAtlas atlas, GameSprite s) {
        Weapon w;
        TextureRegion texture = atlas.findRegion(data.textureName);
        if (texture == null) {
            System.out.println("Error reading texture: " + data.textureName + ", Using default.");
            texture = atlas.findRegion("game/weapons/weapon_anime_sword");
        }

        if (data.isMelee) {
            w = new MeleeWeapon(texture, s);
        } else {
            return null;
        }
        w.knockback = data.knockback;
        w.damage = data.damage;
        w.setRange(data.range);
        w.attackspeed = data.attackspeed;
        return w;
    }

    public void setRange(float r) {
        this.range = r;
    }

    public Weapon(TextureRegion _texture) {
        super(_texture);
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
