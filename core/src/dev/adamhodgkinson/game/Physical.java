package dev.adamhodgkinson.game;

import com.badlogic.gdx.physics.box2d.Fixture;

public interface Physical {

    void beginCollide(Fixture fixture);

    void endCollide(Fixture fixture);

}
