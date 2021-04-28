package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

public class Player extends Animated {
    Body body;

    public Player(World world, AssetManager assets) {
        BodyDef def = new BodyDef();
        def.position.set(1, 1);
        def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(def);

        FixtureDef fix = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(.5f, .75f);
        fix.shape = shape;
        fix.density = 2;
        fix.friction = .3f;
        body.createFixture(fix);

        // needs to dynamically figure out asset name
        // todo make interface Animated for animations to be managed by
        Animation<TextureRegion> runAnimation = new Animation<TextureRegion>(.25f, ((TextureAtlas) assets.get("packed/pack.atlas")).findRegions("game/sprites/elf_m_run_anim"), Animation.PlayMode.LOOP);
        Animation<TextureRegion> hitAnimation = new Animation<TextureRegion>(.5f, ((TextureAtlas) assets.get("packed/pack.atlas")).findRegions("game/sprites/elf_m_hit_anim"), Animation.PlayMode.NORMAL);
        Animation<TextureRegion> idleAnimation = new Animation<TextureRegion>(.25f, ((TextureAtlas) assets.get("packed/pack.atlas")).findRegions("game/sprites/elf_m_idle_anim"), Animation.PlayMode.LOOP);
        this.addAnimation("idle", idleAnimation);
        this.addAnimation("run", runAnimation);
        this.addAnimation("hit", hitAnimation);
    }

   /* public TextureRegion getFrame() { // figures out which animation should be used and gets the frame
//        return idleAnimation.getKeyFrame(animTime);
        if (isHitPlaying) {
            return hitAnimation.getKeyFrame(animTime);
        } else {
            return idleAnimation.getKeyFrame(animTime);
        }

    }*/

    boolean isRunning = false;
    public void getHit(){playAnimation("hit");}
    @Override
    public void update(float dt) { // keeps track of time for animations
        animTime += dt;
        if(currentAnimation.equals("hit") && getAnimation().isAnimationFinished(animTime)){
            if(isRunning) {
                playAnimation("run");
            } else {
                playAnimation("idle");
            }
        }
    }
}
