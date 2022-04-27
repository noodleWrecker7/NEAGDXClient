package dev.adamhodgkinson.game.navigation;

import com.badlogic.gdx.math.GridPoint2;
import dev.adamhodgkinson.game.Enemy;

public class PathFindTask implements Runnable {
    private PathFinder pathFinder;
    Enemy e;
    GridPoint2 start;
    GridPoint2 end;

    public PathFindTask(Enemy e, GridPoint2 start, GridPoint2 end, PathFinder pf) {
        this.start = start;
        this.end = end;
        this.e = e;
        pathFinder = pf;
    }

    public void run() {
        System.out.println("running task finder");
        try {
            Vertex[] path = pathFinder.search(start, end);
            e.receivePath(path);
            System.out.println("sent to enemy");
        } catch (Exception err) {
            System.out.println("failed finding path");
            System.out.println(err.getMessage());
            e.receivePath(null);
        }
    }
}
