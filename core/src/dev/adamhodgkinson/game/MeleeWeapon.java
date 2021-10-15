package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import dev.adamhodgkinson.game.enemies.Enemy;

import java.util.ArrayList;

public class MeleeWeapon extends Weapon implements Physical {
    GameSprite parentSprite;
    Body body;
    Joint joint;
    ArrayList<GameSprite> collidingBodies;

    public MeleeWeapon(int damage, int range, int attackspeed, TextureRegion _texture, GameSprite sprite) {
        super(damage, range, attackspeed, _texture);
        parentSprite = sprite;

        collidingBodies = new ArrayList<>();

        // creating the sensor body for the weapon
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(parentSprite.body.getPosition());
        body = parentSprite.body.getWorld().createBody(def);
        body.setUserData(this);

        final FixtureDef fix = new FixtureDef();
        final CircleShape shape = new CircleShape();
        fix.isSensor = true;
        shape.setRadius(1);
        fix.shape = shape;
        body.createFixture(fix);
    }

    public void update(float dt) {
        this.body.setTransform(parentSprite.getPos(), 0);
    }

    public void destroy() {
        World world = body.getWorld();
        world.destroyJoint(joint);
        world.destroyBody(body);
    }

    @Override
    public void attack() {
        if (System.currentTimeMillis() - this.timeOfLastHit < this.attackspeed) {
            return;
        }
        this.timeOfLastHit = System.currentTimeMillis();
        for (GameSprite s : collidingBodies) {
            s.takeDamage(this.damage);
        }
    }

    @Override
    public void beginCollide(Fixture fixture) {
        Object obj1 = fixture.getBody().getUserData(); // the object collided with
        if (isValidObj(obj1)) {
            collidingBodies.add((GameSprite) obj1);
        }
    }

    public boolean isValidObj(Object obj) {
        if (!(obj instanceof GameSprite)) { // if obj is not an attackable entity
            return false;
        }
        if (obj instanceof Player && parentSprite instanceof Player) { // player cant attack itself
            return false;
        }
        if (obj instanceof Enemy && parentSprite instanceof Enemy) { // enemy cannot attack other enemies
            return false;
        }
        return true;
    }

    @Override
    public void endCollide(Fixture fixture) {
        Object obj1 = fixture.getBody().getUserData(); // the object collided with
        if (isValidObj(obj1)) {
            collidingBodies.remove((GameSprite) obj1);
        }

    }
}
