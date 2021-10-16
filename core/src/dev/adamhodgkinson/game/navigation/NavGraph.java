package dev.adamhodgkinson.game.navigation;

import java.util.ArrayList;
import java.util.Arrays;

public class NavGraph {
	Vertex[] nodesArray;

	ArrayList<Vertex> initalNodes = new ArrayList();
	/**
	 * To save time accessing finding a nodes index
	 */
	final int[][] coordToIndexMap;
	Arc[][] adjacencyMatrix;
	boolean compiled = false;

	public Arc[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	public NavGraph(int mapWidth, int mapHeight) {
		coordToIndexMap = new int[mapWidth][mapHeight];
		// fills with default, invalid values
		for (int i = 0; i < mapWidth; i++) {
			Arrays.fill(coordToIndexMap[i], -1);
		}

	}

	public int addVertex(short x, short y) {
		return addVertex(new Vertex(x, y));
	}

	public Vertex[] getNodesArray() {
		return nodesArray;
	}

	/**
	 * Registers a vertex to the graph, does nothing if compile() has already been
	 * called
	 *
	 * @return index of node
	 */
	public int addVertex(Vertex v) {
		if (compiled)
			return -1;
		initalNodes.add(v);
		final int index = initalNodes.size() - 1;
		if (index == Integer.MAX_VALUE - 1)
			return -1;
		coordToIndexMap[v.x][v.y] = index;
		return index;
	}

	/**
	 * Finalizes node list and creates empty adjacency matrix
	 */
	public void compile() {
		nodesArray = new Vertex[initalNodes.size()];
		initalNodes.toArray(nodesArray);
		initalNodes.clear();
		initalNodes = null;
		adjacencyMatrix = new Arc[nodesArray.length][nodesArray.length];
	}

	/**
	 * Directional from 1 to 2
	 */
	public Arc addLinearEdge(short x1, short y1, short x2, short y2, short weight) {
		final int index1 = coordToIndexMap[x1][y1];
		final int index2 = coordToIndexMap[x2][y2];
		adjacencyMatrix[index1][index2] = new Arc(weight);
		return adjacencyMatrix[index1][index2];
	}

	public JumpArc addJumpEdge(short x1, short y1, short x2, short y2, short weight, double xSpeed, double jumpSpeed) {
		final int index1 = coordToIndexMap[x1][y1];
		final int index2 = coordToIndexMap[x2][y2];
		final JumpArc arc = new JumpArc(weight, xSpeed, jumpSpeed);
		adjacencyMatrix[index1][index2] = arc;
		return arc;
	}

	public void addBiDirEdge(short x1, short y1, short x2, short y2, short weight, boolean isJump) {
		addLinearEdge(x1, y1, x2, y2, weight);
		addLinearEdge(x2, y2, x1, y1, weight);
	}

	public Vertex getVertexByCoords(short x, short y) {
		if (x < 0 || y < 0)
			return null;
		final int index = coordToIndexMap[x][y];
		if (index < 0)
			return null;
		return nodesArray[index];

	}
}
