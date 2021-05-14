package dev.adamhodgkinson.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TileMapShapeBuilder {

    ArrayList<Edge> edges = new ArrayList<>();

    public TileMapShapeBuilder() {

    }

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
        System.out.println("Deleting edges");
        deleteCommonEdges();
        deleteCollinearEdges();
        ArrayList<ArrayList<Edge>> fixtures = separateToFixtures();

        return createFixtures(b, fixtures);

    }

    public ArrayList<Fixture> createFixtures(Body b, ArrayList<ArrayList<Edge>> fixtures) {
        ArrayList<Fixture> physicsFixtures = new ArrayList<>();
        for (int i = 0; i < fixtures.size(); i++) {
            ArrayList<Edge> fixture = fixtures.get(i);
            ArrayList<Vector2> vectors = new ArrayList<>();
            for (int j = 0; j < fixture.size(); j++) {
                vectors.add(fixture.get(j).p1);
            }
            ChainShape chainShape = new ChainShape();
            chainShape.createChain((Vector2[]) vectors.toArray());
            physicsFixtures.add(b.createFixture(chainShape, 1));
        }

        return physicsFixtures;
    }


    public void deleteCommonEdges() {
        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < edges.size(); j++) {
                if (i == j) continue;

                if (edges.get(i).equals(edges.get(j))) {
                    ArrayList<Integer> r = new ArrayList<>();
                    r.add(i);
                    r.add(j);
                    edges.removeAll(r);
                    i -= 2;
                    if(i<0) i=0;
                    if(j<0) j=0;
                }
            }
        }
    }

    public void deleteCollinearEdges() {
        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < edges.size(); j++) {
                Edge e1 = edges.get(i);
                Edge e2 = edges.get(j);
                if (e1.p2.equals(e2.p1)) {
                    edges.set(i, new Edge(e1.p1, e2.p2));
                    edges.remove(j);
                    i--;
                }
            }
        }
    }

    public ArrayList<ArrayList<Edge>> separateToFixtures() {
        ArrayList<ArrayList<Edge>> fixtures = new ArrayList<>();

        ArrayList<Edge> fixture = new ArrayList<>();
        fixture.add(edges.remove(0));
        while (edges.size() > 0) {
            int nextIndex = indexOfNextEdge(fixture.get(fixture.size() - 1).p2);
            if (nextIndex == -1) {
                fixtures.add(fixture);
                fixture = new ArrayList<>();
                fixture.add(edges.remove(0));
            }
            Edge nextEdge = edges.get(nextIndex);
            fixture.add(nextEdge);
            edges.remove(nextEdge);

        }

        return fixtures;
    }


    public int indexOfNextEdge(Vector2 p2) {
        for (int i = 0; i < edges.size(); i++) {
            if (edges.get(i).p1.equals(p2)) {
                return i;
            }
        }
        return -1;
    }
}

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

    public boolean equals(Edge edge) {
        if (this == edge) return true;
        if (edge == null || getClass() != edge.getClass()) return false;
        return p1.equals(edge.p1) && p2.equals(p2) || p1.equals(edge.p2) && p2.equals(p1);
    }
}
