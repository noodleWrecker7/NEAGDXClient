package dev.adamhodgkinson.game.navigation;

import com.badlogic.gdx.math.GridPoint2;
import dev.adamhodgkinson.game.enemies.Enemy;

public class PathFinderThread extends Thread {
    Enemy e;

    public PathFinderThread(Enemy _e) {
        e = _e;
    }

    public synchronized void requestPath(GridPoint2 start, GridPoint2 end) {
        Vertex[] path = Enemy.pathFinder.search(start, end);
        e.receivePath(path);
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {

    }
}

class Request {
    Enemy e;
    GridPoint2 start;
    GridPoint2 end;
}