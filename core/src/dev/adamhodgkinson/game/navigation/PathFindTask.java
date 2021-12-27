package dev.adamhodgkinson.game.navigation;

import com.badlogic.gdx.math.GridPoint2;
import dev.adamhodgkinson.game.Enemy;

public class PathFindTask implements Runnable {
    public static PathFinder pathFinder;
    Enemy e;
    GridPoint2 start;
    GridPoint2 end;

    public PathFindTask(Enemy e, GridPoint2 start, GridPoint2 end) {
        this.start = start;
        this.end = end;
        this.e = e;
    }

    public void run() {
        Vertex[] path = pathFinder.search(start, end);
        e.receivePath(path);
    }
}
