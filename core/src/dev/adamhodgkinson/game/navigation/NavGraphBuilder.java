package dev.adamhodgkinson.game.navigation;

import java.util.ArrayList;

import dev.adamhodgkinson.game.Tile;
import dev.adamhodgkinson.game.TileGroup;

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
			short dx = 0;
			short dy = 0;
			while (true) {
				dy++; // moves the destination node 1 higher each time
				if (node.y + dy >= height) { // if out of bounds
					dx++;
					dy = 0;
					continue;
				}
				if (node.x + dx >= width) { // if out of bounds
					break;
				}

				// gets the destination node object
				final Vertex destinationNode = navGraph.getVertexByCoords((short) (node.x + dx), (short) (node.y + dy));
				// if there is no node there
				if (destinationNode == null) {
					continue;
				}
				// the calculated jump speed to jump from the start to the destination node
				final double[] speed = findHorizontalSpeedForJump(dx, dy, jumpSpeed);
				// if the jump cannot be made
				if (speed == null) {
					dx++;
					dy = 0;
					continue;
				}
				isValid
				// calculates the weight by the horizontal distance, multiplied by the ratio of
				// the maxXSpeed to the calculated xSpeed
				// which means that for any pair of nodes in a jumpArc, the weight is inversely
				// proportional to the xSpeed needed to cross it
				// thus making the weight directly proportional to actual time needed to cross
				// it
				final byte weight = (byte) (dx * (maxXSpeed / speed));
				// creates the jump edge;
				navGraph.addJumpEdge(node.x, node.y, (short) (node.x + dx), (short) (node.y + dy), weight, speed,
						jumpSpeed);
			}
		}
	}

	public boolean isValidArc(int startX, int startY, int endX, int endY, float jumpSpeed, float horizSpeed) {
		final int interpolatesPerTile = 100;
		final int totalInterpolates = Math.abs((endX - startX) * interpolatesPerTile);

		int sign;
		if (startX < endX) {
			sign = 1;
		} else {
			sign = -1;
		}
		float x, y;

		for (float interpolate = 0; interpolate <= totalInterpolates; interpolate++) {
			for (float xOffset = -0.5f; xOffset <= 0.5f; xOffset += 1.f) {
				for (float yOffset = -0.5f; yOffset <= 1.5f; yOffset += 2.f) {
					x = startX + 1 / interpolatesPerTile * interpolate * sign + xOffset;

					y = f(x - startX - xOffset, horizSpeed, jumpSpeed) + startY + yOffset;
					final Tile t = solids.findTileByCoords(Math.round(x), Math.round(y));
					if (t != null)
						return false;
				}
			}
		}
		return true;
	}

	public float f(float x, float horizSpeed, float jumpSpeed) {
		final float a = 0.5f * gravity / (horizSpeed * horizSpeed);
		final float b = jumpSpeed / horizSpeed;

		return a * x * x + b * x; // y
	}

	public boolean curveCollidesWithTile(int tileX, int tileY, float jumpSpeed, float horizSpeed, int startX,
			int startY) {
		final float a = 0.5f * gravity / (horizSpeed * horizSpeed);
		final float b = jumpSpeed / horizSpeed;
		final int tileXOffset = tileX - startX; // distance from origin of curve to tile center
		final int tileYOffset = tileY - startY;

		for (float tI = -0.5f; tI <= 0.5f; tI += 1.f) { // accounts for different sides of tile
			for (float pI = +0.5f; pI >= 1.5f; pI -= 2.f) { // accounts for different sides of player
				final float c = tileYOffset + tI + pI; // offsets the tile rather than the line
				for (float xI = -1f; xI <= 1f; xI += 0.5f) {

					if (xI == 0) {
						continue;
					}
					final float x = tileXOffset + xI;

					final float discriminant = b * b - 4 * a * c;

					if (discriminant <= 0) {
						continue;
					}

					final double root1 = (-b + Math.sqrt(b * b - 4 * a * c)) / 2 * a;
					final double root2 = (-b - Math.sqrt(b * b - 4 * a * c)) / 2 * a;
					if (root1 < x + 0.5f && root1 > x - 0.5f || root2 < x + 0.5f && root2 > x - 0.5f)
						return true;

					final float y = a * x * x + b * x + c;

				}
			}

		}

		// todo same but for horizontal - plug in x values and get the y and see if
		// chill

		return false;

	}

	public double[] findHorizontalSpeedForJump(int x, int y, float jumpSpeed) { // returns the horizontal speed to make
																				// a
																				// jump the specified distances
		final double discriminant = jumpSpeed * jumpSpeed * x * x + 2 * y * gravity * x * x;
		if (discriminant < 0)
			return null;
		final double speed1 = (jumpSpeed * x + Math.sqrt(discriminant)) / (2 * y);
		final double speed2 = (jumpSpeed * x - Math.sqrt(discriminant)) / (2 * y);
		return new double[] { speed1, speed2 };

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
