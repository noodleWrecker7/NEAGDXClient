package dev.adamhodgkinson.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

public interface Physical {

    public void beginCollide(Fixture fixture);

    public void endCollide(Fixture fixture);

}
