package dev.adamhodgkinson.game.navigation;

import dev.adamhodgkinson.game.GameSprite;
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

    public NavGraphBuilder(int width, int height, TileGroup solids, float maxXSpeed, float maxJumpSpeed,
                           float gravity) {
        this.solids = solids;
        this.width = width;
        this.height = height;
        this.maxXSpeed = maxXSpeed;
        this.maxJumpSpeed = maxJumpSpeed;
        this.gravity = gravity;
    }


    public void addJumpsEdges(float jumpSpeed) {
        // checks every node as a starting node
        final Vertex[] nodes = navGraph.getNodesArray();

        for (final Vertex node : nodes) {
            // the delta x and y from the starting node
            short dx = (short) -node.x;
            short dy = (short) -node.y;
            while (true) {
                dy++; // moves the destination node 1 higher each time
                if (node.y + dy >= height) { // if out of bounds
                    dx++;
                    dy = (short) -node.y;
                    continue;
                }
                Arc arc = navGraph.getArc(node.x, node.y, (short) (node.x + dx), (short) (node.y + dy));

                // escape conditions
                // if the arc already exists
                if (arc != null) {
                    continue;
                }

                // if out of bounds
                if (node.x + dx >= width) {
                    break;
                }

                // cant jump to itself
                if (dx == 0 && dy == 0) {
                    continue;
                }

                // gets the destination node object
                final Vertex destinationNode = navGraph.getVertexByCoords((short) (node.x + dx), (short) (node.y + dy));

                // if there is no node there
                if (destinationNode == null) {
                    continue;
                }

                // the calculated jump speed to jump from the start to the destination node
                final float[] speeds = findHorizontalSpeedForJump(dx, dy, jumpSpeed);

                // if the jump is too far
                if (speeds == null) {
                    dx++;
                    dy = (short) -node.y;
                    continue;
                }

                float finalSpeed = chooseBestSpeed(speeds, node.x, node.y, node.x + dx, node.y + dy, jumpSpeed);
                if (finalSpeed == 0) {
                    continue;
                }

                /* calculates the weight by the horizontal distance, multiplied by the ratio of
                 the maxXSpeed to the calculated xSpeed, this means that a lower final speed results in a greater weight,
                 since going slower takes longer */
                final short weight = (short) (Math.abs((dx * (maxXSpeed / finalSpeed))) + Math.abs(dx));

                // creates the jump edge;
                navGraph.addJumpEdge(node.x, node.y, (short) (node.x + dx), (short) (node.y + dy), weight, finalSpeed,
                        jumpSpeed);
            }
        }
    }

    public float chooseBestSpeed(float[] speeds, int startX, int startY, int endX, int endY, float jumpSpeed) {
        float finalSpeed = 0;
        if (isValidArc(startX, startY, endX, jumpSpeed, speeds[0])) {
            finalSpeed = speeds[0];
            if (Float.isInfinite(finalSpeed) || Float.isNaN(finalSpeed) || Math.abs(finalSpeed) <= 0.5f || Math.abs(finalSpeed) > maxXSpeed * 1.5f) {
                finalSpeed = 0;
            }
        }
        if (isValidArc(startX, startY, endX, jumpSpeed, speeds[1])) {
            if ((Math.abs(speeds[1]) < Math.abs(finalSpeed) || finalSpeed == 0) && speeds[1] != 0) {
                finalSpeed = speeds[1];
            }
        }
        if (Float.isInfinite(finalSpeed) || Float.isNaN(finalSpeed) || Math.abs(finalSpeed) <= 0.5f || Math.abs(finalSpeed) > maxXSpeed * 1.5f) {
            finalSpeed = 0;
        }

        return finalSpeed;
    }

    public boolean isValidArc(int startX, int startY, int endX, float jumpSpeed, float horizSpeed) {
        final int samplesPerTile = 20;
        final int totalSamples = Math.abs((endX - startX) * samplesPerTile);

        int sign;
        if (startX < endX) {
            sign = 1;
        } else {
            sign = -1;
        }
        float x, y;

        for (float sample = 0; sample <= totalSamples; sample++) {

            x = ((sample * sign) / (float) samplesPerTile);

            y = f(x, horizSpeed, jumpSpeed);

            for (float xOffset = -GameSprite.MAX_PHYSICAL_WIDTH / 2; xOffset <= GameSprite.MAX_PHYSICAL_WIDTH / 2; xOffset += GameSprite.MAX_PHYSICAL_WIDTH) {

                float yOffset = -GameSprite.MAX_PHYSICAL_HEIGHT / 2 + 0.5f;

                Tile t = solids.findTileByCoords(Math.round(x + startX + xOffset), Math.round(y + startY + yOffset));
                if (t != null)
                    return false;

                yOffset = GameSprite.MAX_PHYSICAL_HEIGHT - yOffset;
                t = solids.findTileByCoords(Math.round(x + startX + xOffset), Math.round(y + startY + yOffset));
                if (t != null)
                    return false;
            }
        }
        return true;
    }

    public float f(float x, float horizSpeed, float jumpSpeed) {
        final float a = 0.5f * gravity / (horizSpeed * horizSpeed);
        final float b = jumpSpeed / horizSpeed;

        return a * x * x + b * x; // y
    }

    /**
     * returns the horizontal speed to jump the specified distances
     */
    public float[] findHorizontalSpeedForJump(int x, int y, float jumpSpeed) {
        float correctedY = y;
        if (y == 0) {
            correctedY = 0.1f;
        }
        final double discriminant = jumpSpeed * jumpSpeed * x * x + 2 * correctedY * gravity * x * x;
        if (discriminant < 0)
            return null;

        // values of y==0 need to be changed to just be small otherwise some valid values can get lost, even though the jump is technically possible

        final float speed1 = (float) (jumpSpeed * x + Math.sqrt(discriminant)) / (2 * correctedY);
        final float speed2 = (float) (jumpSpeed * x - Math.sqrt(discriminant)) / (2 * correctedY);
        return new float[]{speed1, speed2};
    }

    public void addValidNodes() {
        final ArrayList<Tile> tiles = solids.getTiles();
        for (int i = 0; i < tiles.size(); i++) {
            final Tile t = tiles.get(i);
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
            final Vertex v = navGraph.getNodesArray()[i];
            if (navGraph.getVertexByCoords((short) (v.x - 1), v.y) != null) {
                navGraph.addBiDirEdge(v.x, v.y, (short) (v.x - 1), v.y, (short) 1, false);
            }
        }
    }

    public NavGraph generateNavGraph() {
        navGraph = new NavGraph(width, height);
        addValidNodes();
        navGraph.compile();
        addAdjacentEdges();

        for (float i = 0; i <= maxJumpSpeed; i += 1f) {
            addJumpsEdges(i);
        }

        return navGraph;
    }
}
