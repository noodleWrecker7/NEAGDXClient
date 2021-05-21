package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Sprite extends Animated {
    Vector2 movement = new Vector2();
    Body body;
    float speed;
    boolean isRunning = false;

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
}
