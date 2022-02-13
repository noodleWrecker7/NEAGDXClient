package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.physics.box2d.World;
import dev.adamhodgkinson.game.navigation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Enemy extends GameSprite {
    // Constants
    static final float HEALTH_BAR_HEIGHT = 0.2f;
    public static final int MAX_INVALID_PATHS = 3;
    public static final long TIME_UNTIL_GIVE_UP_ON_ARC = 3500;
    static final long TIME_BETWEEN_PATH_FINDS = 2000;

    // Pathfinding
    Player target;
    public static PathFinder pathFinder;
    List<Vertex> path;

    Vertex currentPoint;
    Arc currentArc;

    static final ExecutorService pool = Executors.newFixedThreadPool(20);

    boolean alreadyJumpedThisArc = false;
    long timeOfArcAttempt;
    int invalidPathCount = 0;
    long timeOfLastPathFind = System.currentTimeMillis() + (new Random()).nextInt(5000);
    int targetRange = 20; // what range a target must be within to target it


    public Enemy(TextureAtlas atlas, String textureName, World world, int x, int y) {
        super(world, x, y, textureName, atlas);
//        pathFinderThread = new PathFinderThread(this);
//        pathFinderThread.start();
    }

    public void attack() {

    }

    public GridPoint2 getGridPoint() {
        return new GridPoint2(Math.round(getPos().x), Math.round(getPos().y));
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (target == null) {
            return;
        }
        if (System.currentTimeMillis() - timeOfLastPathFind > TIME_BETWEEN_PATH_FINDS && (currentArc == null || System.currentTimeMillis() - timeOfArcAttempt > TIME_UNTIL_GIVE_UP_ON_ARC)) {
            if (target.getLastValidPosition().dst(Math.round(getPos().x), Math.round(getPos().y)) < targetRange) {
//                pathFinderThread.notify();
//                pathFinderThread.requestPath(getGridPoint(), target.getLastValidPosition());

                PathFindTask task = new PathFindTask(this, getGridPoint(), target.getLastValidPosition());
                pool.execute(task);
                timeOfLastPathFind = System.currentTimeMillis();
                timeOfArcAttempt = System.currentTimeMillis();
//                findNewPath();
            }
        }
        followPath();

    }

    public synchronized void receivePath(Vertex[] _path) {
        if (_path == null) {
            invalidPathCount++;
            return;
        }
        path = new ArrayList<>(Arrays.asList(_path));
        currentArc = null;
        currentPoint = null;
    }

    public void followPath() {
        if (invalidPathCount > MAX_INVALID_PATHS) { // if ai gets stuck then it should just walk until a new path is found
            if (invalidPathCount % MAX_INVALID_PATHS * 2 < MAX_INVALID_PATHS) { // so it swaps direction occaisonally
                body.setLinearVelocity(speed, body.getLinearVelocity().y);
            } else {
                body.setLinearVelocity(-speed, body.getLinearVelocity().y);
            }
            return;
        }

        if (path == null) {
            return;
        }
//        System.out.println("follow path");
        GridPoint2 gridPos = getGridPoint();


        if (currentPoint == null) { // if no current target point
            if (path.size() == 0) { // if path empty
//                System.out.println("path empty");
                currentArc = null;
                if (invalidPathCount == 0 && System.currentTimeMillis() - timeOfLastPathFind > TIME_BETWEEN_PATH_FINDS / 6) {
//                    pathFinder.requestPath();
                }
                return;
            }
            // get next path point
            currentPoint = path.get(0);
            path.remove(0);

            // get arc to point
            currentArc = pathFinder.getNav().getArc((short) gridPos.x, (short) gridPos.y, currentPoint.x, currentPoint.y);
            timeOfArcAttempt = System.currentTimeMillis();
            alreadyJumpedThisArc = false;


            return;
        }

        // if at destination
        if (gridPos.dst(currentPoint.x, currentPoint.y) == 0) {
            currentPoint = null;
            return;
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
            body.setLinearVelocity(speed, body.getLinearVelocity().y);
        } else {
            body.setLinearVelocity(-speed, body.getLinearVelocity().y);
        }
    }

    public void doJumpArc() {
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
