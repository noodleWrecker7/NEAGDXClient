package dev.adamhodgkinson.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.adamhodgkinson.PlayerData;

public class Game {
    Level level;
    PlayerData playerData;
    Player player;
    World world;

    public Player getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }

    public Level getLevel() {
        return level;
    }

    public Game(PlayerData playerData, AssetManager assets) {
        this.playerData = playerData;
        this.world = new World(new Vector2(0, -30), true);
        this.level = new Level(Gdx.files.internal("level.xml"), world);
        this.player = new Player(world, assets);

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Physical B = (Physical) contact.getFixtureB().getBody().getUserData();
                Physical A = (Physical) contact.getFixtureA().getBody().getUserData();

            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    public void update(float dt) {
        //
        world.step(dt, 10, 10);
        player.update(dt);
    }
}

enum GameBodyType {
    TILE_SOLID,
    TILE_ONEWAY,
    TILE_DEADLY,
    PLAYER,
    ENEMY,
    PLAYER_PROJECTILE,
    ENEMY_PROJECTILE,
    DESTRUCTABLE
}
