package dev.adamhodgkinson.game;

import com.badlogic.gdx.InputAdapter;
import dev.adamhodgkinson.screens.GameScreen;

/**Handles all user input eg key presses etc, and passes them to the relevant parts of the program*/
public class UserInputHandler extends InputAdapter { // temporary
    GameScreen screen;

    public UserInputHandler(GameScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean keyDown(int keycode) {
        moveByKeyCode(keycode, false);
        return super.keyDown(keycode);
    }

    /**Parses the keycode and wether it is pressed or not to communicate the movement direction to the player
     * @param keycode the numerical id of the key being pressed
     * @param isKeyUp wether or not the key has been pushed down or released up*/
    public void moveByKeyCode(int keycode, boolean isKeyUp) {
        Player player = screen.getGame().getPlayer();
        // m is used as a multiplier to invert the direction dependant on if the key has come up or down
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
        moveByKeyCode(keycode, true);
        return super.keyDown(keycode);
    }
}
