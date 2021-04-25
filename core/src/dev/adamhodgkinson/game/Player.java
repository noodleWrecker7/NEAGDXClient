package dev.adamhodgkinson.game;

import com.badlogic.gdx.physics.box2d.*;

public class Player {
    Body body;
    public Player(World world){
        BodyDef def = new BodyDef();
        def.position.set(1,1);
        def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(def);

        FixtureDef fix = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(.5f, .75f);
        fix.shape = shape;
        fix.density = 2;
        fix.friction = .3f;
        body.createFixture(fix);
    }
}
