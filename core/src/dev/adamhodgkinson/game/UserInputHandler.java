package dev.adamhodgkinson.game;

import com.badlogic.gdx.InputAdapter;
import dev.adamhodgkinson.screens.GameScreen;
import dev.adamhodgkinson.screens.Menu;

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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        player.handleInput(Action.ATTACK);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean keyDown(int keycode) {
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
            case 111:
                screen.getClient().setScreen(new Menu(screen.getClient()));
        }
        return super.keyDown(keycode);
    }

    /**
     * So that actions can be bound to different keys in future
     */
    enum Action {
        JUMP, MOVE_LEFT_START, MOVE_RIGHT_START, MOVE_LEFT_END, MOVE_RIGHT_END, ATTACK
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
