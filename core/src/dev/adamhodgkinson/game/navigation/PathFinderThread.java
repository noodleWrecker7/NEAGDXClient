package dev.adamhodgkinson.game.navigation;

import com.badlogic.gdx.math.GridPoint2;
import dev.adamhodgkinson.game.Enemy;

public class PathFinderThread extends Thread {
    Enemy e;
    boolean searching = false;
    GridPoint2 start;
    GridPoint2 end;


    public PathFinderThread(Enemy _e) {
        this.e = _e;
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
        while (true) {
            try {
                sleep(200);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}

class Request {
    Enemy e;
    GridPoint2 start;
    GridPoint2 end;

    public Request(Enemy e, GridPoint2 start, GridPoint2 end) {
        this.e = e;
        this.start = start;
        this.end = end;
    }
}