package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import dev.adamhodgkinson.game.navigation.Arc;
import dev.adamhodgkinson.game.navigation.JumpArc;
import dev.adamhodgkinson.game.navigation.PathFinder;
import dev.adamhodgkinson.game.navigation.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Enemy extends GameSprite {
    // Constants
    static final float HEALTH_BAR_HEIGHT = 0.2f;
    public static final int MAX_INVALID_PATHS = 3;
    public static final long TIME_UNTIL_GIVE_UP_ON_ARC = 3500;
    static final long TIME_BETWEEN_PATH_FINDS = 2000;
    /**
     * Enemies will still attack if player no more than this distance out of the attack range
     */
    static final int ATTACK_RANGE_BUFFER = 4;
    boolean isSearching = false;

    // Pathfinding
    Player target;
    public static PathFinder pathFinder;
    ArrayList<Vertex> path;

    Vertex currentPoint;
    Arc currentArc;


    boolean alreadyJumpedThisArc = false;
    long timeOfArcAttempt;
    int invalidPathCount = 0;
    long timeOfLastPathFind = System.currentTimeMillis() + (new Random()).nextInt(5000);
    int targetRange = 10; // what range a target must be within to target it


    public Enemy(TextureAtlas atlas, String textureName, World world, int x, int y) {
        super(world, x, y, textureName, atlas);
    }

    public void attack() {
        weapon.attack();
    }

    public GridPoint2 getGridPoint() {
        return new GridPoint2(Math.round(getPos().x), Math.round(getPos().y - height / 2 + 0.5f));
    }

    private boolean isTargetInRangeToAttack() {
        Vector2 pos = target.getPos();
        float distX = Math.abs(pos.x - getPos().x);
        float distY = Math.abs(pos.y - getPos().y);

        if (pos.x - getPos().x > 0 == !isXFlipped) {
            return distX * distX + distY * distY < (this.weapon.range + ATTACK_RANGE_BUFFER) * (this.weapon.range + ATTACK_RANGE_BUFFER);
        }

        return false;


    }

    @Override
    public void update(float dt) {
        if (weapon != null && isTargetInRangeToAttack()) {
            attack();
        }
        super.update(dt);
        if (target == null) {
            return;
        }
        if (System.currentTimeMillis() - timeOfLastPathFind > TIME_BETWEEN_PATH_FINDS && (currentArc == null || System.currentTimeMillis() - timeOfArcAttempt > TIME_UNTIL_GIVE_UP_ON_ARC)) {
            GridPoint2 lastTargetPosition = target.getLastValidPosition();
            System.out.println("dist" + lastTargetPosition.dst(getGridPoint()));
            if (lastTargetPosition != null && lastTargetPosition.dst(getGridPoint()) < targetRange) {

                if (!isSearching) {
                    System.out.println("finding new task");
                    pathFinder.newTask(this, getGridPoint(), target.getLastValidPosition());
                    isSearching = true;
                    timeOfLastPathFind = System.currentTimeMillis();
                    timeOfArcAttempt = System.currentTimeMillis();
                }
            }
        }
        followPath();

    }

    public void receivePath(Vertex[] _path) {
        System.out.println("received path");
        isSearching = false;
        if (_path == null || _path.length == 0) {
            invalidPathCount++;
            System.out.println("bad path");
            return;
        }
        System.out.println("path ok");
        path = new ArrayList<>(Arrays.asList(_path));
        currentArc = null;
        currentPoint = null;
        invalidPathCount = 0;

    }

    public void followPath() {
        System.out.println("invalid count: " + invalidPathCount);
        if (invalidPathCount > MAX_INVALID_PATHS) { // if ai gets stuck then it should just walk until a new path is found
            if (invalidPathCount % MAX_INVALID_PATHS * 2 < MAX_INVALID_PATHS) { // so it swaps direction occaisonally
                movement.set(1, 0);
            } else {
                movement.set(-1, 0);
            }
            return;
        }

        if (path == null) {
            return;
        }
        GridPoint2 gridPos = getGridPoint();

        System.out.println("currentpoint null: " + (currentPoint == null));
        if (currentPoint == null) { // if no current target point
            if (path.size() == 0) { // if path empty
                System.out.println("path empty");
                currentArc = null;
                if (invalidPathCount == 0 && System.currentTimeMillis() - timeOfLastPathFind > TIME_BETWEEN_PATH_FINDS) {
                    if (!isSearching) {
                        System.out.println("finding new task2");
                        pathFinder.newTask(this, getGridPoint(), target.getLastValidPosition());
                        isSearching = true;
                        timeOfLastPathFind = System.currentTimeMillis();
                        timeOfArcAttempt = System.currentTimeMillis();
                    }
                }
                return;
            }
            // get next path point
            currentPoint = path.get(0);
            if (currentPoint == null) {
                System.out.println("PATH CAME WITH NULL POINT");
            }
            path.remove(0);


            // get arc to point
            currentArc = pathFinder.getNav().getArc((short) gridPos.x, (short) (gridPos.y), currentPoint.x, currentPoint.y);
            timeOfArcAttempt = System.currentTimeMillis();
            alreadyJumpedThisArc = false;


            return;
        }

        // if at destination
        if (gridPos.dst(currentPoint.x, currentPoint.y) == 0) {
            currentPoint = null;
            return;
        }

        if (target.getLastValidPosition() != null && target.getLastValidPosition().dst(getGridPoint()) > targetRange) {
            currentPoint = null;
            path = null;
            currentArc = null;
        }

        // if arc not real
        if (currentArc == null) {
            return;
        }

        // if is a jump arc
        if (currentArc.isJump() && !alreadyJumpedThisArc) {
            doJumpArc();
            return;
        } else if (currentArc.isJump()) {
            return;
        }

        // walk to destination point
        if (currentPoint.x > getPos().x) {
            movement.set(1, 0);
        } else {
            movement.set(-1, 0);
        }
    }

    public void doJumpArc() {
        movement.set(0, 0);
        GridPoint2 pos = getGridPoint();
        body.setTransform(pos.x, pos.y + height / 2 - 0.4f, 0);
        // perform the jump
        if (!jump(((JumpArc) currentArc).jumpSpeed)) { // if jump impossible
            return;
        }
        alreadyJumpedThisArc = true;
        body.setLinearVelocity(((JumpArc) currentArc).xSpeed, body.getLinearVelocity().y);
    }

    public void renderHealth(ShapeRenderer renderer) {
        renderer.setColor(1, 0, 0, 1);
        renderer.rect(this.getPos().x - width / 2f, this.getPos().y + height / 2.f + HEALTH_BAR_HEIGHT, width, HEALTH_BAR_HEIGHT);

        renderer.setColor(0, 1, 0, 1);
        float greenWidth = width * (this.health / this.maxHealth);
        renderer.rect(this.getPos().x - width / 2f, this.getPos().y + height / 2.f + HEALTH_BAR_HEIGHT, greenWidth, HEALTH_BAR_HEIGHT);
    }

    /**
     * Sets the target to the supplied gamesprite
     *
     * @param _target - the object to target
     */
    public void setTarget(Player _target) {
        target = _target;
    }
}
