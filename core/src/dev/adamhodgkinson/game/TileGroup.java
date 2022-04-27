package dev.adamhodgkinson.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class TileGroup implements Physical {
    private final ArrayList<Tile> tiles;
    Body body;
    World world;

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public TileGroup(World _world) {
        tiles = new ArrayList<>();
        world = _world;
        createBody();
    }

    public Tile findTileByCoords(int x, int y) {
        for (int i = 0; i < tiles.size(); i++) {
            if (tiles.get(i).getX() == x && tiles.get(i).getY() == y) {
                return tiles.get(i);
            }
        }
        return null;
    }

    public void createBody() {
        if (body != null) {
            world.destroyBody(body);
        }
        BodyDef def = new BodyDef();
        def.position.set(0, 0);
        body = world.createBody(def);

        body.setUserData(this);
        fixtures = null;
    }

    ArrayList<Fixture> fixtures;

    protected void build() {
        System.out.println("Building tile group");
        createBody();
        TileMapShapeBuilder builder = new TileMapShapeBuilder();
        for (Tile t : tiles) {
            builder.addTile(t);
        }
        fixtures = builder.build(body);
    }

    protected void addTile(Tile t) {
        tiles.add(t);

    }

    @Override
    public void beginCollide(Fixture fixture) {

    }

    @Override
    public void endCollide(Fixture fixture) {

    }
}
