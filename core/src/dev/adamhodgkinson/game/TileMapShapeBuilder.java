package dev.adamhodgkinson.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.graalvm.compiler.serviceprovider.IsolateUtil;

import java.util.ArrayList;

public class TileMapShapeBuilder {

    ArrayList<Edge> edges = new ArrayList<>();

    public void addTile(Tile t) {
        Vector2 v1 = new Vector2(t.getX() - .5f, t.getY() - .5f);
        Vector2 v2 = new Vector2(t.getX() - .5f, t.getY() + .5f);
        Vector2 v3 = new Vector2(t.getX() + .5f, t.getY() + .5f);
        Vector2 v4 = new Vector2(t.getX() + .5f, t.getY() - .5f);
        System.out.println(v1);
        System.out.println(v2);
        System.out.println(v3);
        System.out.println(v4);

        edges.add(new Edge(v1, v2));
        edges.add(new Edge(v2, v3));
        edges.add(new Edge(v3, v4));
        edges.add(new Edge(v4, v1));
    }

    public ArrayList<Fixture> build(Body b) {
        deleteCommonEdges();
        deleteCollinearEdges();
        System.out.println(edges.size());
        ArrayList<ArrayList<Edge>> fixtures = separateToFixtures();
        System.out.println(fixtures.size());

        return createFixtures(b, fixtures);
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
            if(vectors.length < 2){
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
        int initialCount = edges.size();
        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < edges.size(); j++) {
                // check each edge against every other edge
                if (i == j) continue;
//                System.out.println("Checking i: " + i + ", j: " + j); // debugging
                Edge e1 = edges.get(i);
                Edge e2 = edges.get(j);
                if (e1.equals(e2)) { // if edges are the same
                    edges.remove(e1);
                    edges.remove(e1);
                    j--; // must decrement the counters as the array has just shrunk
                    i--;
                    if (i < 0) i = 0; // preventing IndexOutOfBounds errors
                    if (j < 0) j = 0;
                }
            }
        }
        System.out.println("Deleted: " + (initialCount - edges.size()));
    }

    /**
     * Combines two collinear edges in to one single, longer edge
     */
    public void deleteCollinearEdges() {
        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < edges.size(); j++) {
                // for each edge
                if(i==j){
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
    public ArrayList<ArrayList<Edge>> separateToFixtures() {
        ArrayList<ArrayList<Edge>> fixtures = new ArrayList<>(); // 2d list of fixtures

        ArrayList<Edge> fixture = new ArrayList<>(); // will make one fixture, contains all the edges of a shape
        fixture.add(edges.remove(0)); // starts off the fixture
        while (edges.size() > 0) { // while there are still edges left
            int nextIndex = indexOfNextEdge(fixture.get(fixture.size() - 1).p2); // gets the edge which immediately suceeds the edge at the end of the fixture ArrayList
            if (nextIndex == -1 || fixture.get(fixture.size()-1).p2.equals(fixture.get(0).p1)) { // if no edge is found
                fixtures.add(fixture); // save the fixture
                fixture = new ArrayList<>(); // reset the ArrayList
                fixture.add(edges.remove(0));  // starts off the fixture
                continue;
            }
            // adds the next edge to the fixture and removes it from the pool
            Edge nextEdge = edges.get(nextIndex);
            fixture.add(nextEdge);
            edges.remove(nextEdge);
        }

        return fixtures;
    }


    /**
     * Find the next edge which starts at vector p2
     */
    public int indexOfNextEdge(Vector2 p2) {
        // Iterates each edge
        for (int i = 0; i < edges.size(); i++) {
            if (edges.get(i).p1.equals(p2)) {
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

    public Edge(float x1, float y1, float x2, float y2) {
        this(new Vector2(x1, y1), new Vector2(x2, y2));
    }

    public Edge(Vector2 _p1, Vector2 _p2) {
        this.p1 = _p1;
        this.p2 = _p2;
    }

    /**
     * Overrode the equals methods to allow the edges to be easily equated
     */
    public boolean equals(Edge edge) {
        if (this == edge) return true;
        if (edge == null || getClass() != edge.getClass()) return false;
        return p1.equals(edge.p1) && p2.equals(edge.p2) || p1.equals(edge.p2) && p2.equals(edge.p1);
    }
}


