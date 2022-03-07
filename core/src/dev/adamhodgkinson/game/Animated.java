package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public abstract class Animated {

    float animTime = 0f;
    HashMap<String, Animation<TextureRegion>> animations = new HashMap<>();
    String currentAnimation = "idle"; // default

    /**
     * Indicated wether the sprite is facing the opposite direction, right = false, left = true
     */
    boolean isXFlipped = false;

    protected void addAnimation(String name, Animation<TextureRegion> animation) {
        animations.put(name, animation);
    }

    void playAnimation(String name) {
        animTime = 0f;
        if (animations.containsKey(name)) {
            currentAnimation = name;
        }
    }

    Animation<TextureRegion> getAnimation() {
        return animations.get(currentAnimation);
    }

    public TextureRegion getFrame() {
        TextureRegion r= animations.get(currentAnimation).getKeyFrame(animTime);
        if(this.isXFlipped != r.isFlipX()) {
            r.flip(true, false);
        }
        return r;
    }


    public void update(float dt) {
        animTime += dt;
    }
}
