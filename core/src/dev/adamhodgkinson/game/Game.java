package dev.adamhodgkinson.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.adamhodgkinson.PlayerData;

/**
 * Contains all the logic of the game itself, should remain abstracted from any
 * render logic
 */
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

    /**
     * Initialises all game data
     *
     * @param playerData contains player's data eg equipped weapons and inventory
     *                   etc
     * @param assets     assets manager of the client, should be passed in by the
     *                   screen object
     */
    public Game(PlayerData playerData, AssetManager assets) {
        /** Currently non functional */
        this.playerData = playerData; // will eventually be read from file/server

        // The box2d physics world object which all physical bodies will be placed into
        world = new World(new Vector2(0, -30), true); // Given vector is gravity
        level = new Level(Gdx.files.internal("core/assets/level.xml"), world, assets);
        player = new Player(world, assets, level.playerSpawnPos.x, level.playerSpawnPos.y);

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                final Physical B = (Physical) contact.getFixtureB().getBody().getUserData();
                final Physical A = (Physical) contact.getFixtureA().getBody().getUserData();
                A.beginCollide(contact.getFixtureB());
                B.beginCollide(contact.getFixtureA());

            }

            @Override
            public void endContact(Contact contact) {
                final Physical B = (Physical) contact.getFixtureB().getBody().getUserData();
                final Physical A = (Physical) contact.getFixtureA().getBody().getUserData();
                A.endCollide(contact.getFixtureB());
                B.endCollide(contact.getFixtureA());
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
        // processes the physics simulation
        world.step(dt, 10, 10);
        player.update(dt);
        // iterates through each enemy to update them
        for (int i = 0; i < level.getEnemiesArray().size(); i++) {
            level.getEnemiesArray().get(i).update(dt);
        }
    }
}

enum GameBodyType {
    TILE_SOLID, TILE_ONEWAY, TILE_DEADLY, PLAYER, ENEMY, PLAYER_PROJECTILE, ENEMY_PROJECTILE, DESTRUCTABLE
}
