package dev.adamhodgkinson.game;

import com.badlogic.gdx.InputAdapter;

import dev.adamhodgkinson.screens.GameScreen;

/**
 * Handles all user input eg key presses etc, and passes them to the relevant
 * parts of the program
 */
public class UserInputHandler extends InputAdapter { // temporary
	GameScreen screen;
	final Player player;

	public UserInputHandler(GameScreen screen) {
		this.screen = screen;
		player = screen.getGame().getPlayer();
	}

	@Override
	public boolean keyDown(int keycode) {
		System.out.println(keycode);
		switch (keycode) {
		case 51:
		case 62:
			player.handleInput(Action.JUMP);
			break;
		case 29:
			player.handleInput(Action.MOVE_LEFT_START);
			break;
		case 32:
			player.handleInput(Action.MOVE_RIGHT_START);
			break;
		}
		return super.keyDown(keycode);
	}

	/**
	 * So that actions can be bound to different keys in future
	 */
	enum Action {
		JUMP, MOVE_LEFT_START, MOVE_RIGHT_START, MOVE_LEFT_END, MOVE_RIGHT_END, ATTACK
	}

	/**
	 * Parses the keycode and wether it is pressed or not to communicate the
	 * movement direction to the player
	 *
	 * @param keycode the numerical id of the key being pressed
	 * @param isKeyUp wether or not the key has been pushed down or released up
	 */
	public void moveByKeyCode(int keycode, boolean isKeyUp) {

		// m is used as a multiplier to invert the direction dependant on if the key has
		// come up or down
		int m = 1;
		if (isKeyUp) {
			m = -1;
		}
		switch (keycode) {
		case 51:
			player.addMoveDir(0 * m, 1 * m);
			break;
		case 29:
			player.addMoveDir(-1 * m, 0 * m);
			break;
		case 47:
			player.addMoveDir(0 * m, -1 * m);
			break;
		case 32:
			player.addMoveDir(1 * m, 0 * m);
			break;
		}
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case 29:
			player.handleInput(Action.MOVE_LEFT_END);
			break;
		case 32:
			player.handleInput(Action.MOVE_RIGHT_END);
			break;
		}
		return super.keyDown(keycode);
	}
}
