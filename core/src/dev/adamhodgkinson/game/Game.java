package dev.adamhodgkinson.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.adamhodgkinson.PlayerData;

/**
 * Contains all the logic of the game itself, should remain abstracted from any
 * render logic
 */
public class Game {
    Level level;
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
    public Game(PlayerData playerData, AssetManager assets, FileHandle file) {
        /** Currently non functional */

        // The box2d physics world object which all physical bodies will be placed into
        world = new World(new Vector2(0, -30), true); // Given vector is gravity
        level = new Level(file, world, assets, this);
        player = new Player(playerData, world, (TextureAtlas) assets.get(Gdx.files.internal("packed/pack.atlas").path()), level);
        for (Enemy e : level.getEnemiesArray()) {
            e.setTarget(player);
        }

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                final Physical B = (Physical) contact.getFixtureB().getBody().getUserData();
                final Physical A = (Physical) contact.getFixtureA().getBody().getUserData();
                if (A == null || B == null) {
                    return;
                }
                A.beginCollide(contact.getFixtureB());
                B.beginCollide(contact.getFixtureA());

            }

            @Override
            public void endContact(Contact contact) {
                final Physical B = (Physical) contact.getFixtureB().getBody().getUserData();
                final Physical A = (Physical) contact.getFixtureA().getBody().getUserData();
                if (A == null || B == null) {
                    return;
                }
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

        level.update(dt);
        /*int len = level.navGraph.getNodesArray().length - 1;
        int n1 = (int) Math.round(Math.random() * len);
        int n2 = (int) Math.round(Math.random() * len);
        System.out.println("pathfind : " + n1 + ", " + n2);
        long startTime = System.nanoTime();
        pathFinder.search(n1, n2);
        long endTime = System.nanoTime();
        System.out.println("Time to pathfind: " + (float) (endTime - startTime) / 1000000f);*/
    }
}

