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

    public PathFinder(NavGraph nav) {
        this.nav = nav;

    }

    public void addToQueue(int node, short weight) {
        for (int i = 0; i < queue.size(); i++) {
            if (weightToNode[queue.get(i)] > weight) {
                queue.add(i, node);
            }
        }
    }

    public Vertex[] search(GridPoint2 start, GridPoint2 end) {
        int startIndex = nav.coordToIndexMap[start.x][start.y];
        if (startIndex == -1) {
            return null;
        }
        int endIndex = nav.coordToIndexMap[end.x][end.y];
        if (endIndex == -1) {
            return null;
        }
        int[] path = search(startIndex, endIndex);
        if(path == null){
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
        while (queue.size() != 0) {
            int checking = queue.get(0);
            queue.remove(0);
            visited.add(checking);
            Arc[] connections = nav.adjacencyMatrix[checking];
            for (int i = 0; i < connections.length; i++) {
                Arc connection = connections[i];
                if (connection == null) {
                    continue;
                }
                if (visited.contains(i)) { // if already visited
                    continue;
                }
                addToQueue(i, (short) (weightToNode[checking] + connection.weight));
                if (weightToNode[i] > weightToNode[checking] + connection.weight || weightToNode[i] == -1) {
                    weightToNode[i] = (short) (weightToNode[checking] + connection.weight);
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

class PathNode {
    int nodeIndex;
    short weight;
    int previousIndex;

    public PathNode(int index, short _weight, int previous) {
        nodeIndex = index;
        weight = _weight;
        previousIndex = previous;
    }
}
