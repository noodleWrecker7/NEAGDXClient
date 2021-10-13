package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

public class MeleeWeapon extends Weapon implements Physical {
    Fixture fixture;
    Body parentBody;

    public MeleeWeapon(int damage, int range, int attackspeed, TextureRegion _texture, Body body) {
        super(damage, range, attackspeed, _texture);
        parentBody = body;
    }

    //todo collisions
    //  probably create a sensor fixture/body and use that to check for touchng,
    @Override
    public void attack() {
        BodyDef def = new BodyDef();
        def.position.set(parentBody.getPosition());
        Body body = parentBody.getWorld().createBody(def);

        final FixtureDef fix = new FixtureDef();
        final PolygonShape shape = new PolygonShape();
        fix.isSensor = true;
        shape.setRadius(range);
        fix.shape = shape;
        body.createFixture(fix);

        body.getWorld().step(0, 10, 10);
    }

    @Override
    public void beginCollide(Fixture fixture) {
        Object obj = fixture.getBody().getUserData();
        if (obj instanceof GameSprite) {
            ((GameSprite) obj).takeDamage(this.damage);
        }
    }

    @Override
    public void endCollide(Fixture fixture) {

    }
}
