package dev.adamhodgkinson.game.navigation;

import com.badlogic.gdx.math.GridPoint2;
import dev.adamhodgkinson.game.Enemy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PathFinder {
    // todo put an algorithm here

    NavGraph nav;
    final ExecutorService pool = Executors.newFixedThreadPool(10);


    public NavGraph getNav() {
        return nav;
    }

    public PathFinder(NavGraph nav) {
        this.nav = nav;
    }

    public void newTask(Enemy e, GridPoint2 start, GridPoint2 end) {
        pool.execute(new PathFindTask(e, start, end, this));
    }

    private void addToQueue(int node, short weight, ArrayList<Integer> queue, short[] weightToNode) {
        if (queue.contains(node)) {
            return;
        }
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
            System.out.println("Start index negative");
            return null;
        }
        int endIndex = nav.coordToIndexMap[end.x][end.y];
        if (endIndex == -1) {
            System.out.println("End index no exist");
            return null;
        }
        int[] path = search(startIndex, endIndex);
        if (path == null) {
            System.out.println("Couldn't find path");
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
        int[] previousNode;
        short[] weightToNode;
        ArrayList<Integer> visited;
        ArrayList<Integer> queue;

        previousNode = new int[nav.nodesArray.length];
        weightToNode = new short[nav.nodesArray.length];
        Arrays.fill(weightToNode, (short) -1);
        weightToNode[start] = 0;
        queue = new ArrayList<>();
        visited = new ArrayList<>();
        queue.add(start);
        while (queue.size() != 0) { // whilst queue not empty
            System.out.println("searching");
            System.out.println("q: " + queue.size() + " v: " + visited.size());
            int checking = queue.remove(0); // gets first node in queue and removes it

            visited.add(checking); // node marked as visited
            if (checking == end) { // if reached end
                return generatePath(start, end, previousNode);
            }
            Arc[] connections = nav.adjacencyMatrix[checking]; // all possible routes from current node
            for (int i = 0; i < connections.length; i++) { // for each connected node
                Arc connection = connections[i]; // conenction to neighbour
                if (connection == null) { // if connection is empty
                    continue;
                }
                if (visited.contains(i)) { // if already visited
                    continue;
                }
                addToQueue(i, (short) (weightToNode[checking] + connection.weight), queue, weightToNode); // adds found node to queue
                if (weightToNode[i] > weightToNode[checking] + connection.weight || weightToNode[i] == -1) { // if this route to the node is shorter
                    weightToNode[i] = (short) (weightToNode[checking] + connection.weight); // sets the weight to the new weight
                    previousNode[i] = checking;
                }
            }
        }
        return null;
    }

    // tried a whole bunch of different ways of threading, turned out the issue was in here,
    // sometimes if start and end were the same it would infinite loop and crash out of heap

    private int[] generatePath(int start, int end, int[] previousNode) {
        // creates array such that the start of backwards is the end node and the end of backwards is the start node
        // and all the nodes in between are the steps in the path, in order
        ArrayList<Integer> backwards = new ArrayList<>();
        backwards.add(end);
        while (backwards.get(backwards.size() - 1) != start) {
            int index = backwards.size() - 1;
            int node = backwards.get(index);
            backwards.add(previousNode[node]); // gets previous node and adds it to the end
        }

        // reverses the list
        int[] result = new int[backwards.size()];
        for (int i = 0; i < backwards.size(); i++) {
            result[i] = backwards.get(backwards.size() - i - 1);
        }

        return result;
    }
}

