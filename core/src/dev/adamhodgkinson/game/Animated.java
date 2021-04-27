package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public abstract class Animated {

    float animTime = 0f;
    HashMap<String, Animation<TextureRegion>> animations = new HashMap<>();



    public void update(float dt) {
        animTime += dt;
    }
}
