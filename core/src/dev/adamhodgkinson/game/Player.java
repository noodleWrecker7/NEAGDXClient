package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import dev.adamhodgkinson.PlayerData;
import dev.adamhodgkinson.game.UserInputHandler.Action;

public class Player extends GameSprite {

    State _state = State.IDLE;
    GridPoint2 lastValidPosition; // the last time the player was a valid pathfind-able position
    Level level;
    PlayerData playerData;

    static final float DENSITY = 50;
    static final float SPEED = 6;
    static final float FRICTION = 4;
    static final int MAX_JUMPS = 2;
    static final float JUMP_SPEED = 14f;

    public GridPoint2 getLastValidPosition() {
        return lastValidPosition;
    }


    public Player(PlayerData _playerData, World world, TextureAtlas atlas, Level _level) {
        super(world, _level.playerSpawnPos.x, _level.playerSpawnPos.y, DENSITY, SPEED, FRICTION, _playerData.getTexture(), atlas);
        playerData = _playerData;
        level = _level;

        this.jumpSpeed = JUMP_SPEED;
        this.maxJumps = MAX_JUMPS;

        final float sensorHeight = .4f;

        // sensor creation
        final FixtureDef sensorFixtureDef = new FixtureDef(); // for jumps / detecting if on ground
        final PolygonShape shape2 = new PolygonShape();
        shape2.setAsBox(width / 2, sensorHeight / 2, new Vector2(0, -height / 2), 0);
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.shape = shape2;
        body.createFixture(sensorFixtureDef);

        //fixme temporary
        //todo rename all weapons to remove weapon_ prefix
//        TextureAtlas atlas = assets.get("core/assets/packed/pack.atlas");

        this.weapon = Weapon.createFromData(playerData.getEquippedWeaponData(), atlas, this);
    }

    @Override
    public void jump() {
        super.jump();
    }

    public void handleInput(Action input) {
        switch (_state) {
            case RUNNING:
            case IDLE:
                if (input == Action.JUMP) {
                    jump();
                }
                handleLeftRightMovement(input);
                break;
            case JUMPING:
                handleLeftRightMovement(input);
                _state = State.JUMPING;
                break;
        }
        if (input == Action.ATTACK) {
            this.weapon.attack();
        }
    }

    /**
     * If there is any left right movement it is acted upon
     */
    public void handleLeftRightMovement(Action input) {
        switch (input) {
            case MOVE_LEFT_END:
            case MOVE_RIGHT_START:
                addMoveDir(1, 0);
                break;
            case MOVE_LEFT_START:
            case MOVE_RIGHT_END:
                addMoveDir(-1, 0);
                break;
        }
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
        if (movement.x == 0) {
            _state = State.IDLE;
        } else {
            _state = State.RUNNING;
        }
        updateFlippage();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        GridPoint2 pathFindingCoords = getPathFindingCoords();
        if (level.navGraph.getVertexByCoords((short) pathFindingCoords.x, (short) pathFindingCoords.y) != null) {
            this.lastValidPosition = pathFindingCoords;
        }
    }

    protected GridPoint2 getPathFindingCoords() {
        float x, y;
        y = getPos().y - height / 2 + 0.5f;
        x = getPos().x;
        return new GridPoint2(Math.round(x), Math.round(y));
    }

}

enum State {
    JUMPING, RUNNING, IDLE
}
