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
    float animationRotation = 0;
    boolean attackOnCooldown = false;

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
        shape.setRadius(this.range);
        fix.shape = shape;
        body.createFixture(fix);


    }

    public void update(float dt) {
        this.body.setTransform(parentSprite.getPos(), 0);
        if (attackOnCooldown) {
            float timeSinceHit = System.currentTimeMillis() - this.timeOfLastHit;
            if (timeSinceHit >= this.attackspeed) {
                attackOnCooldown = false;
                return;
            }
            float fractionOfCooldownLeft = (timeSinceHit / this.attackspeed);
            animationRotation = 90 * fractionOfCooldownLeft * 2;
            if (animationRotation >= 90) {
//                animationRotation = 90 - (animationRotation - 90)
                animationRotation = 180 - animationRotation; // equivalent to above line
            }
        }
        this.setRotation((absoluteRotation + animationRotation) * rotationfFlip);
    }

    public void destroy() {
        World world = body.getWorld();
        world.destroyJoint(joint);
        world.destroyBody(body);
    }

    @Override
    public void attack() {
        if (attackOnCooldown) {
            return;
        }
        this.attackOnCooldown = true;
        this.timeOfLastHit = System.currentTimeMillis();
        for (GameSprite s : collidingBodies) {
            if (rotationfFlip == -1) {
                if (s.getPos().x < parentSprite.getPos().x) {
                    return;
                }

            }
            if (rotationfFlip == 1) {
                if (s.getPos().x > parentSprite.getPos().x) {
                    return;
                }
            }
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
