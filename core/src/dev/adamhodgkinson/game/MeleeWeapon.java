package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class MeleeWeapon extends Weapon implements Physical {
    GameSprite parentSprite;
    Body body;
    /**
     * List of all gamesprites within range of the weapon, constantly kept up to date when an entity is detected within range
     */
    ArrayList<GameSprite> collidingBodies;
    float animationRotation = 0;
    boolean attackOnCooldown = false;


    public MeleeWeapon(TextureRegion _texture, GameSprite sprite) {
        super(_texture);
        parentSprite = sprite;

        collidingBodies = new ArrayList<>();

        // creating the sensor body for the weapon
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(parentSprite.body.getPosition());
        body = parentSprite.body.getWorld().createBody(def);
        body.setUserData(this);

        setRange(this.range);
    }

    /**
     * Sets the range of the weapon and recreates the collision sensor which tests for entities within range
     */
    @Override
    public void setRange(float r) {
        // should only ever have one fixture
        for (Fixture f : body.getFixtureList()) {
            body.destroyFixture(f);
        }
        this.range = r;
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
            // allows weapon to be used again once cooldown is done
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
        this.setRotation((absoluteRotation + animationRotation) * rotationFlip);
    }

    @Override
    public void destroy() {
        World world = body.getWorld();
        world.destroyBody(body);
        collidingBodies = null;
        parentSprite = null;
    }

    @Override
    public void attack() {
        if (attackOnCooldown) {
            return;
        }
        this.attackOnCooldown = true;
        this.timeOfLastHit = System.currentTimeMillis();
        // for every sprite within range
        for (GameSprite s : collidingBodies) {
            // only allows sprite to be damaged if they are being faced by this weapon
            if (rotationFlip == -1) {
                if (s.getPos().x < parentSprite.getPos().x) {
                    continue;
                }
            }
            if (rotationFlip == 1) {
                if (s.getPos().x > parentSprite.getPos().x) {
                    continue;
                }
            }
            s.takeDamage(this.damage, this.knockback, parentSprite);
        }
    }

    @Override
    public void beginCollide(Fixture fixture) {
        Object obj1 = fixture.getBody().getUserData(); // the object collided with
        if (isValidObj(obj1)) {
            collidingBodies.add((GameSprite) obj1);
        }
    }

    /**
     * Returns true if the specified object is a valid entity that can be attacked by this weapon
     */
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
