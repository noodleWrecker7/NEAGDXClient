package dev.adamhodgkinson.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

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
		world = new World(new Vector2(0, -30), true);
		level = new Level(Gdx.files.internal("core/assets/level.xml"), world, assets);
		player = new Player(world, assets, level.playerSpawnPos.x, level.playerSpawnPos.y);

		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				final Physical B = (Physical) contact.getFixtureB().getBody().getUserData();
				final Physical A = (Physical) contact.getFixtureA().getBody().getUserData();

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
		System.out.println("X: " + player.getPos().x + "Y: " + player.getPos().y);
		System.out.println("h: " + player.height);
		for (int i = 0; i < level.getEnemiesArray().size(); i++) {
			level.getEnemiesArray().get(i).update(dt);
		}
	}
}

enum GameBodyType {
	TILE_SOLID, TILE_ONEWAY, TILE_DEADLY, PLAYER, ENEMY, PLAYER_PROJECTILE, ENEMY_PROJECTILE, DESTRUCTABLE
}
