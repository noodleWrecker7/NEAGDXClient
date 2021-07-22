package dev.adamhodgkinson.game.navigation;

import dev.adamhodgkinson.game.Tile;
import dev.adamhodgkinson.game.TileGroup;

import java.util.ArrayList;

public class NavGraphBuilder {

    NavGraph navGraph;
    TileGroup solids;
    int width;
    int height;
    float maxXSpeed;
    float maxJumpSpeed;
    float gravity;

    public NavGraphBuilder(int width, int height, TileGroup solids, float maxXSpeed, float maxJumpSpeed, float gravity) {
        this.solids = solids;
        this.width = width;
        this.height = height;
        this.maxXSpeed = maxXSpeed;
        this.maxJumpSpeed = maxJumpSpeed;
        this.gravity = gravity;
    }

    public void addJumpsEdges(float jumpSpeed) {
        // checks every node as a starting node
        Vertex[] nodes = navGraph.getNodesArray();
        for (Vertex node : nodes) {
            // the delta x and y from the starting node
            short dx = 0;
            short dy = 0;
            while (true) {
                dy++; // moves the destination node 1 higher each time
                if (node.y + dy >= this.height) { // if out of bounds
                    dx++;
                    dy = 0;
                    continue;
                }
                if (node.x + dx >= this.width) { // if out of bounds
                    break;
                }
                // gets the destination node object
                Vertex destinationNode = navGraph.getVertexByCoords((short) (node.x + dx), (short) (node.y + dy));
                // if there is no node there
                if (destinationNode == null) {
                    continue;
                }
                // the calculated jump speed to jump from the start to the destination node
                double speed = findHorizontalSpeedForJump(dx, dy, jumpSpeed);
                // if the jump cannot be made
                if (speed == 0) {
                    dx++;
                    dy = 0;
                    continue;
                }
                // calculates the weight by the horizontal distance, multiplied by the ratio of the maxXSpeed to the calculated xSpeed
                // which means that for any pair of nodes in a jumpArc, the weight is inversely proportional to the xSpeed needed to cross it
                // thus making the weight directly proportional to actual time needed to cross it
                byte weight = (byte) (dx * (maxXSpeed / speed));
                // creates the jump edge;
                navGraph.addJumpEdge(node.x, node.y, (short) (node.x + dx), (short) (node.y + dy), weight, speed, jumpSpeed);
            }
        }
    }

    public double findHorizontalSpeedForJump(int x, int y, float jumpSpeed) {
        double discriminant = jumpSpeed * jumpSpeed * x * x + 2 * y * gravity * x * x;
        if (discriminant < 0) {
            return 0;
        }
        double speed1 = ((jumpSpeed * x) + Math.sqrt(discriminant)) / (2 * y);
        double speed2 = ((jumpSpeed * x) - Math.sqrt(discriminant)) / (2 * y);
        if (Math.abs(speed1) < Math.abs(speed2)) {
            return speed1;
        } else {
            return speed2;
        }

    }

    public void addValidNodes() {
        ArrayList<Tile> tiles = solids.getTiles();
        for (int i = 0; i < tiles.size(); i++) {
            Tile t = tiles.get(i);
            boolean valid = true;
            for (int j = 1; j <= 2; j++) {
                if (solids.findTileByCoords(t.getX(), t.getY() + j) != null) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                navGraph.addVertex(t.getX(), (short) (t.getY() + 1));
            }
        }
    }

    public void addAdjacentEdges() {
        for (int i = 0; i < navGraph.getNodesArray().length; i++) {
            Vertex v = navGraph.getNodesArray()[i];
            if (navGraph.getVertexByCoords((short) (v.x - 1), v.y) != null) {
                navGraph.addBiDirEdge(v.x, v.y, (short) (v.x - 1), v.y, (byte) 1, false);
            }
        }
    }

    public NavGraph finish() {
        navGraph = new NavGraph(width, height);
        addValidNodes();
        navGraph.compile();
        addAdjacentEdges();
        addJumpsEdges(maxJumpSpeed);
        return navGraph;
    }
}
