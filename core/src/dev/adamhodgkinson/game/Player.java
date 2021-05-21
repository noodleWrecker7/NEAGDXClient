package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player extends Sprite implements Physical {

    GameBodyType type = GameBodyType.PLAYER;

    public Player(World world, AssetManager assets) {
        this.speed = 8f;
        BodyDef def = new BodyDef();
        def.position.set(1, 3);
        def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(def);

        body.setFixedRotation(true);

        // main fixture
        FixtureDef fix = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1f, 1.5f);
        fix.shape = shape;
        fix.density = 5;
        fix.friction = 0f;
        body.setLinearDamping(2f);
        body.createFixture(fix);

        // sensor
        FixtureDef sensorFixtureDef = new FixtureDef();
        PolygonShape shape2 = new PolygonShape();
        shape2.setAsBox(1, .2f, new Vector2(0, -1.5f), 0);
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.shape = shape2;
        body.createFixture(sensorFixtureDef);

        body.setUserData(this);

        // needs to dynamically figure out asset name
        this.setDefaultAnims(assets, "elf_m");
    }

    public void defaultAnimations(){}


    public Vector2 getPos() {
        return body.getPosition();
    }




    @Override
    public void update(float dt) { // keeps track of time for animations
        super.update(dt);
    }

    @Override
    public void beginCollide(Fixture fixture) {

    }

    @Override
    public void endCollide(Fixture fixture) {

    }

    public void moveKeyUp(int keycode) {
        int y = 0, x = 0;
        switch (keycode) {
            case 51: // w
                y--;
                break;
            case 29: //a
                x++;
                break;
            case 47: //s
                y++;
                break;
            case 32: //d
                x--;
                break;
        }

        movement.add(x, y);

        updateFlippage();

    }

    public void moveKeyDown(int keycode) {
        int y = 0, x = 0;
        switch (keycode) {
            case 51: // w
                y++;
                break;
            case 29: //a
                x--;
                break;
            case 47: //s
                y--;
                break;
            case 32: //d
                x++;
                break;
        }
        movement.add(x, y);
        updateFlippage();

    }

}
