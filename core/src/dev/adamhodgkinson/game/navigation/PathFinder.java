package dev.adamhodgkinson.game.navigation;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Arrays;

public class PathFinder {
    // todo put an algorithm here

    NavGraph nav;
    int[] previousNode;
    short[] weightToNode;
    ArrayList<Integer> visited;
    ArrayList<Integer> queue;

    public NavGraph getNav() {
        return nav;
    }

    public PathFinder(NavGraph nav) {
        this.nav = nav;

    }

    public void addToQueue(int node, short weight) {
        if (queue.size() == 0) {
            queue.add(node);
            return;
        }
        for (int i = 0; i <= queue.size(); i++) {
            if (weightToNode[queue.get(i)] > weight) {
                queue.add(i, node);
                return;
            }
            if (i == queue.size() - 1) { // because the loop wouldnt start without being <= but i still need to prevent oob error
                queue.add(node);
                return;
            }
        }
    }

    public Vertex[] search(GridPoint2 start, GridPoint2 end) {
        if (start.x < 0 || start.x >= nav.coordToIndexMap.length || start.y < 0 || start.y >= nav.coordToIndexMap[0].length) {
            return null;
        }
        int startIndex = nav.coordToIndexMap[start.x][start.y];
        if (startIndex == -1) {
            System.out.println("pfs: start not found");

            return null;
        }
        int endIndex = nav.coordToIndexMap[end.x][end.y];
        if (endIndex == -1) {
            System.out.println("pfs: end not found");
            return null;
        }
        int[] path = search(startIndex, endIndex);
        if (path == null) {
            System.out.println("pfs: path failed");
            return null;
        }
        Vertex[] result = new Vertex[path.length];
        for (int i = 0; i < path.length; i++) {
            result[i] = nav.nodesArray[path[i]];
        }
        return result;
    }

    /**
     * Performs dijkstra's shortest path and returns array of nodes to travel to in order
     */
    public int[] search(int start, int end) {
        previousNode = new int[nav.nodesArray.length];
        weightToNode = new short[nav.nodesArray.length];
        Arrays.fill(weightToNode, (short) -1);
        queue = new ArrayList<>();
        visited = new ArrayList<>();
//        addToQueue(new PathNode(null, null, start));
        queue.add(start);
        while (queue.size() != 0) { // whilst queue not empty
            int checking = queue.get(0); // gets first node in quque
            System.out.println("path search: " + checking);
            queue.remove(0); // pops it off queue
            visited.add(checking); // node marked as visited
            Arc[] connections = nav.adjacencyMatrix[checking]; // all possible routes from current node
            for (int i = 0; i < connections.length; i++) { // for each connected node
                System.out.println("checking connection " + i);
                Arc connection = connections[i]; // conenction to neighbour
                if (connection == null) { // if connection is empty
                    System.out.println("no connection");
                    continue;
                }
                if (visited.contains(i)) { // if already visited
                    System.out.println("already visited");
                    continue;
                }
                addToQueue(i, (short) (weightToNode[checking] + connection.weight)); // adds found node to queue
                if (weightToNode[i] > weightToNode[checking] + connection.weight || weightToNode[i] == -1) { // if this route to the node is shorter
                    weightToNode[i] = (short) (weightToNode[checking] + connection.weight); // sets the weight to the new weight
                    previousNode[i] = checking;
                }
                if (i == end) { // if reached end
                    return generatePath(start, end);
                }

            }
        }
        return null;
    }

    public int[] generatePath(int start, int end) {
        System.out.println("gen path");
        // creates array such that the start of backwards is the end node and the end of backwards is the start node
        // and all the nodes in between are the steps in the path, in order
        ArrayList<Integer> backwards = new ArrayList<>();
        backwards.add(end);
        do {
            System.out.println(backwards.size());
            backwards.add(previousNode[backwards.get(backwards.size() - 1)]); // gets previous node and adds it to the end
        } while (backwards.get(backwards.size() - 1) != start);

        // reverses the list
        int[] result = new int[backwards.size()];
        for (int i = 0; i < backwards.size(); i++) {
            result[i] = backwards.get(backwards.size() - i - 1);
        }

        return result;
    }
}
