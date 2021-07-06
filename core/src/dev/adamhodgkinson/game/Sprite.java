package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Sprite extends Animated implements Physical {
    Vector2 movement = new Vector2();
    Body body;
    float speed;
    boolean isRunning = false;
    float width;
    float height;

    public Sprite(World world, float x, float y, String textureName, AssetManager assets) {
        this(world, x, y, 5, 8f, .6f, 0, textureName, assets);
    }

    public Sprite(World world, float x, float y, float density, float speed, float friction, float linearDamping, String textureName, AssetManager assets) {
        TextureAtlas atlas = assets.get("packed/pack.atlas");
        String prefix = "game/sprites/" + textureName;
        width = (float) atlas.findRegions(prefix + "_idle_anim").get(0).originalWidth / 8;
        height = (float) atlas.findRegions(prefix + "_idle_anim").get(0).originalHeight / 8;
        setDefaultAnims(assets, textureName);

        BodyDef def = new BodyDef();
        def.position.set(x, y);
        def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(def);

        body.setFixedRotation(true);

        // main fixture
        FixtureDef fix = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, (height / 2) - height / 8); // height/8 accounts for whitespace in texture
        fix.shape = shape;
        fix.density = density;
        fix.friction = friction;
        body.setLinearDamping(linearDamping);
        body.createFixture(fix);
    }

    public void setDefaultAnims(AssetManager assets, String textureName) {
        TextureAtlas atlas = assets.get("packed/pack.atlas");
        String prefix = "game/sprites/" + textureName;
        Animation<TextureRegion> runAnimation = new Animation<TextureRegion>(.15f, atlas.findRegions(prefix + "_run_anim"), Animation.PlayMode.LOOP);
        Animation<TextureRegion> hitAnimation = new Animation<TextureRegion>(.5f, atlas.findRegions(prefix + "_hit_anim"), Animation.PlayMode.NORMAL);
        Animation<TextureRegion> idleAnimation = new Animation<TextureRegion>(.25f, atlas.findRegions(prefix + "_idle_anim"), Animation.PlayMode.LOOP);
        this.addAnimation("idle", idleAnimation);
        this.addAnimation("run", runAnimation);
        this.addAnimation("hit", hitAnimation);

    }

    public void draw(SpriteBatch batch) {
        Vector2 pos = this.getPos();
        batch.draw(this.getFrame(), pos.x - width / 2, pos.y - height / 2 + height / 8, width, height); /* had to add coord offsets to account for being at center of object + weird height/8 offset accounts for extra whitespace in texture   */
    }

    public Vector2 getPos() {
        return body.getPosition();
    }

    public void updateFlippage() {
        if (movement.x < 0) {
            this.isXFlipped = true;
        } else if (movement.x > 0) {
            this.isXFlipped = false;
        }
    }

    public void getHit() {
        playAnimation("hit");
    }

    @Override
    public void update(float dt) {
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
        if (!this.isRunning && (body.getLinearVelocity().x > 0.1 || body.getLinearVelocity().x < -0.1)) {
            this.isRunning = true;
            this.playAnimation("run");
            System.out.println("run");
        } else if (this.isRunning && (body.getLinearVelocity().x < 0.2 && body.getLinearVelocity().x > -0.2)) {
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


}
