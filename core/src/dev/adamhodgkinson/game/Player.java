package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player extends Animated implements Physical {
    Body body;
    GameBodyType type = GameBodyType.PLAYER;
    float speed = 8f; // pixels per second

    public Player(World world, AssetManager assets) {
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
        shape2.setAsBox(1,.2f, new Vector2(0, -1.5f), 0);
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.shape = shape2;
        body.createFixture(sensorFixtureDef);

        body.setUserData(this);

        // needs to dynamically figure out asset name
        Animation<TextureRegion> runAnimation = new Animation<TextureRegion>(.15f, ((TextureAtlas) assets.get("packed/pack.atlas")).findRegions("game/sprites/elf_m_run_anim"), Animation.PlayMode.LOOP);
        Animation<TextureRegion> hitAnimation = new Animation<TextureRegion>(.5f, ((TextureAtlas) assets.get("packed/pack.atlas")).findRegions("game/sprites/elf_m_hit_anim"), Animation.PlayMode.NORMAL);
        Animation<TextureRegion> idleAnimation = new Animation<TextureRegion>(.25f, ((TextureAtlas) assets.get("packed/pack.atlas")).findRegions("game/sprites/elf_m_idle_anim"), Animation.PlayMode.LOOP);
        this.addAnimation("idle", idleAnimation);
        this.addAnimation("run", runAnimation);
        this.addAnimation("hit", hitAnimation);
    }

    public Vector2 getPos() {
        return body.getPosition();
    }

    boolean isRunning = false;

    public void getHit() {
        playAnimation("hit");
    }

    @Override
    public void update(float dt) { // keeps track of time for animations
        super.update(dt);
        if (currentAnimation.equals("hit") && getAnimation().isAnimationFinished(animTime)) {
            if (isRunning) {
                playAnimation("run");
            } else {
                playAnimation("idle");
            }
        }
//        body.setLinearVelocity(movement.x * speed, movement.y * speed);
        Vector2 mov = body.getLinearVelocity();
        body.setLinearVelocity(movement.x != 0 ? movement.x * speed : mov.x, movement.y != 0 ? movement.y * speed : mov.y);
        if(!this.isRunning && (body.getLinearVelocity().x >0.1 || body.getLinearVelocity().x < -0.1 )){
            this.isRunning = true;
            this.playAnimation("run");
            System.out.println("run");
        } else if (this.isRunning &&(body.getLinearVelocity().x < 0.2 && body.getLinearVelocity().x > -0.2)) {
            this.isRunning = false;
            this.playAnimation("idle");
        }
    }

    @Override
    public void beginCollide(Fixture fixture) {

    }

    @Override
    public void endCollide(Fixture fixture) {

    }

    Vector2 movement = new Vector2();

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

    public void updateFlippage(){
        if(movement.x < 0) {
            this.isXFlipped = true;
        } else if(movement.x > 0){
            this.isXFlipped = false;
        }
    }
}
