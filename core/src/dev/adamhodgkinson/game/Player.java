package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends Sprite implements Physical {

	GameBodyType type = GameBodyType.PLAYER;

	public Player(World world, AssetManager assets, float x, float y) {
		super(world, x, y, "elf_m", assets);

		speed = 8f;
		final float width = 2;
		final float height = 3;
		final float sensorHeight = .4f;

		// sensor
		final FixtureDef sensorFixtureDef = new FixtureDef();
		final PolygonShape shape2 = new PolygonShape();
		shape2.setAsBox(width / 2, sensorHeight / 2, new Vector2(0, -height / 2), 0);
		sensorFixtureDef.isSensor = true;
		sensorFixtureDef.shape = shape2;
		body.createFixture(sensorFixtureDef);

		body.setUserData(this);
	}

	/**
	 * Takes a 2d direction vector and adds it to the current movement vector, this
	 * is only called once every time a key is pressed or released so the maximum
	 * total range for the movement vector is -1 to +1
	 *
	 * @param x the x direction
	 * @param y the y direction
	 */
	public void addMoveDir(int x, int y) {
		movement.add(x, y * 2);
		updateFlippage();
	}

}
