package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

public class Player {
    Body body;
    Animation<TextureRegion> runAnimation;
    Animation<TextureRegion> hitAnimation;
    Animation<TextureRegion> idleAnimation;
    float animTime = 0;

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
        runAnimation = new Animation<TextureRegion>(.25f, ((TextureAtlas) assets.get("packed/pack.atlas")).findRegions("game/sprites/elf_m_run_anim"), Animation.PlayMode.LOOP);
        hitAnimation = new Animation<TextureRegion>(.5f, ((TextureAtlas) assets.get("packed/pack.atlas")).findRegions("game/sprites/elf_m_hit_anim"), Animation.PlayMode.NORMAL);
        idleAnimation = new Animation<TextureRegion>(.25f, ((TextureAtlas) assets.get("packed/pack.atlas")).findRegions("game/sprites/elf_m_idle_anim"), Animation.PlayMode.LOOP);
    }

    public TextureRegion getFrame() { // figures out which animation should be used and gets the frame
//        return idleAnimation.getKeyFrame(animTime);
        if (isHitPlaying) {
            return hitAnimation.getKeyFrame(animTime);
        } else {
            return idleAnimation.getKeyFrame(animTime);
        }

    }

    boolean isHitPlaying = false, isRunning = false, isIdle = true;

    public void getHit() { // called when hit and resets animation settings
        animTime = 0;
        isHitPlaying = true;
        isRunning = false;
        isIdle = false;
    }

    public boolean isHitPlaying() {
        return isHitPlaying;
    }

    public void update(float dt) { // keeps track of time for animations
        animTime += dt;
        if (isHitPlaying && hitAnimation.isAnimationFinished(animTime)) {
            isHitPlaying = false;
            isIdle = true;
        }
    }
}
