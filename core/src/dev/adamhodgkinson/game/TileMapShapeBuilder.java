package dev.adamhodgkinson.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.ArrayList;

/**
 * Takes a set of tiles and simplifies them into a single shape
 */
public class TileMapShapeBuilder {

    ArrayList<Edge> edges = new ArrayList<>();

    /**
     * Adds the tile to the builder to be included in the chain shape
     *
     * @param t The tile object to be added
     */
    public void addTile(Tile t) {
        Vector2 v1 = new Vector2(t.getX() - .5f, t.getY() - .5f);
        Vector2 v2 = new Vector2(t.getX() - .5f, t.getY() + .5f);
        Vector2 v3 = new Vector2(t.getX() + .5f, t.getY() + .5f);
        Vector2 v4 = new Vector2(t.getX() + .5f, t.getY() - .5f);

        edges.add(new Edge(v1, v2));
        edges.add(new Edge(v2, v3));
        edges.add(new Edge(v3, v4));
        edges.add(new Edge(v4, v1));
    }

    public ArrayList<Fixture> build(Body b) {
        deleteCommonEdges();
        combineCollinearEdges();
        System.out.println(edges.size());
        System.out.println(edges);
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            System.out.println(e.p2);
        }
        ArrayList<ArrayList<Edge>> polygons = separateToPolygons();
        System.out.println("polygons" + polygons.size());

        return createFixtures(b, polygons);
    }

    /**
     * Creates the individual chain shapes and fixtures to add to the body
     */
    public ArrayList<Fixture> createFixtures(Body b, ArrayList<ArrayList<Edge>> fixtures) {
        // Array of box2d fixtures to be returned
        ArrayList<Fixture> physicsFixtures = new ArrayList<>();
        // for each set of data
        System.out.println(fixtures.size());
        for (int i = 0; i < fixtures.size(); i++) {
            ArrayList<Edge> fixture = fixtures.get(i);
            Vector2[] vectors = new Vector2[fixture.size()];
            // Gets the first point on each edge, and adds to an ArrayList of vectors
            for (int j = 0; j < fixture.size(); j++) {
                vectors[j] = fixture.get(j).p1;
                System.out.println(vectors[j]);
            }
            ChainShape chainShape = new ChainShape();
            // creates the chain shape from the vectors
            if (vectors.length < 2) {
                continue;
            }
            chainShape.createChain(vectors);
            physicsFixtures.add(b.createFixture(chainShape, 1));
            System.out.println("Made a shape");
        }
        System.out.println("finished making shapes");
        return physicsFixtures;
    }

    /**
     * Deletes any edges which are in identical positions
     */
    public void deleteCommonEdges() {
        // checks each edge against each other edge
        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < edges.size(); j++) {
                if (i == j) continue; // cant test an edge against itself
                Edge e1 = edges.get(i);
                Edge e2 = edges.get(j);
                if (e1.equals(e2)) { // if edges are the same
                    // remove them both
                    edges.remove(e2);
                    edges.remove(e1);
                    j--; // must decrement the counters as the array has just shrunk
                    i--;
                    if (i < 0) i = 0; // preventing IndexOutOfBounds errors
                    if (j < 0) j = 0;
                }
            }
        }
    }

    /**
     * Combines two collinear edges in to one single, longer edge
     */
    public void combineCollinearEdges() {
        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < edges.size(); j++) {
                // for each edge
                if (i == j) {
                    continue;
                }
                Edge e1 = edges.get(i);
                Edge e2 = edges.get(j);
                if (e1.p2.equals(e2.p1)) { // if one ends where the other starts
                    Vector2 edge1V = new Vector2(e1.p2.x - e1.p1.x, e1.p2.y - e1.p1.y); // the difference between p1 and p2
                    Vector2 edge2V = new Vector2(e2.p2.x - e2.p1.x, e2.p2.y - e2.p1.y);
                    if (!edge1V.isCollinear(edge2V)) {
                        continue;
                    }
                    edges.set(i, new Edge(e1.p1, e2.p2)); // replaces the first with a longer one
                    edges.remove(j); // removes the redundant edge
                    j--; // must decrement the counters as the array has just shrunk
                    i--;
                    if (i < 0) i = 0; // preventing IndexOutOfBounds errors
                    if (j < 0) j = 0;
                }
            }
        }
    }

    /**
     * Takes a large list of edges and separates it into separate shapes
     */
    public ArrayList<ArrayList<Edge>> separateToPolygons() {
        ArrayList<ArrayList<Edge>> polygons = new ArrayList<>(); // 2d list of polygons

        ArrayList<Edge> polygon = new ArrayList<>(); // will make one polygon, contains all the edges of a shape
        polygon.add(edges.remove(0)); // starts off the polygon
        while (edges.size() > 0) { // while there are still edges left
            int nextIndex = indexOfNextEdge(polygon.get(polygon.size() - 1).p2); // gets the edge which immediately succeeds the edge at the end of the polygon ArrayList
            if (nextIndex == -1 || polygon.get(polygon.size() - 1).p2.equals(polygon.get(0).p1)) { // if no edge is found
                polygons.add(polygon); // save the polygon
                polygon = new ArrayList<>(); // reset the ArrayList
                polygon.add(edges.remove(0));  // starts off the polygon
                continue;
            }
            // adds the next edge to the polygon and removes it from the pool
            Edge nextEdge = edges.get(nextIndex);
            polygon.add(nextEdge);
            edges.remove(nextEdge);
        }
        // can assume that as the loop exits it has found the final point and a polygon and just needs to save it
        polygons.add(polygon); // save the polygon

        return polygons;
    }


    /**
     * Find the next edge which starts at vector p2
     */
    public int indexOfNextEdge(Vector2 p2) {
        // Iterates each edge
        for (int i = 0; i < edges.size(); i++) {
            if (edges.get(i).p1.equals(p2)) {
                return i;
            } else if (edges.get(i).p2.equals(p2)) {
                edges.get(i).reverse();
                return i;
            }
        }
        return -1;
    }
}

/**
 * Represents an edge of a tile, contains two vector representing the start and end coordinates
 */
class Edge {
    public Vector2 p1;
    public Vector2 p2;

    public Edge(Vector2 _p1, Vector2 _p2) {
        this.p1 = _p1;
        this.p2 = _p2;
    }

    /**
     * Override the equals methods to allow the edges to be easily equated in deleteCommonEdges()
     */
    public boolean equals(Edge edge) {
        if (this == edge) return true;
        if (edge == null || getClass() != edge.getClass()) return false;
        return p1.equals(edge.p1) && p2.equals(edge.p2) || p1.equals(edge.p2) && p2.equals(edge.p1);
    }

    /**
     * Swaps p1 & p2
     */
    public void reverse() {
        Vector2 tempVec = p1.cpy();
        p1 = p2.cpy();
        p2 = tempVec;
    }
}


