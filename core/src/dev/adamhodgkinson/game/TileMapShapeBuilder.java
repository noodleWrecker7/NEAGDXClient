package dev.adamhodgkinson.game;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class TileMapShapeBuilder {

    ArrayList<Edge> edges = new ArrayList<>();

    public TileMapShapeBuilder(){

    }

    public void addTile(Tile t){
        Vector2 v1 = new Vector2(t.getX() - .5f, t.getY() - .5f);
        Vector2 v2 = new Vector2(t.getX() - .5f, t.getY() + .5f);
        Vector2 v3 = new Vector2(t.getX() + .5f, t.getY() + .5f);
        Vector2 v4 = new Vector2(t.getX() + .5f, t.getY() - .5f);

        edges.add(new Edge(v1,v2));
        edges.add(new Edge(v2,v3));
        edges.add(new Edge(v3,v4));
        edges.add(new Edge(v4,v1));
    }

    public void build(){
        deleteCommonEdges();
        deleteCollinearEdges();
    }

    public void deleteCommonEdges(){
        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < edges.size(); j++) {
                if(i==j) continue;

                if(edges.get(i).equals(edges.get(j))) {
                    ArrayList<Integer> r = new ArrayList<>();
                    r.add(i);
                    r.add(j);
                    edges.removeAll(r);
                    i -= 2;
                }
            }
        }
    }

    public void deleteCollinearEdges(){
        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < edges.size(); j++) {
                Edge e1 = edges.get(i);
                Edge e2 = edges.get(j);
            }
        }
    }
}

class Edge{
    public Vector2 p1;
    public Vector2 p2;
    public Edge(float x1,float y1,float x2,float y2){
        this(new Vector2(x1,y1), new Vector2(x2,y2));
    }
    public Edge(Vector2 _p1, Vector2 _p2) {
        this.p1 = _p1;
        this.p2 = _p2;
    }

    public boolean equals(Edge edge) {
        if (this == edge) return true;
        if (edge == null || getClass() != edge.getClass()) return false;
        return p1.equals(edge.p1) && p2.equals(p2) || p1.equals(edge.p2) && p2.equals(p1) ;
    }
}
