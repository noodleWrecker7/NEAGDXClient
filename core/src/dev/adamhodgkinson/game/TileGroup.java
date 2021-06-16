package dev.adamhodgkinson.game;

import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class TileGroup implements Physical {
    GameBodyType type;
    private ArrayList<Tile> tiles;
    Body body;
    Fixture fixture;
    World world;
    ChainShape chainShape;

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public TileGroup(World _world) {
        tiles = new ArrayList<>();
        world = _world;
        createBody();

    }

    public void createBody() {
        if (body != null) {
            world.destroyBody(body);
        }
        BodyDef def = new BodyDef();
        def.position.set(0, 0);
        body = world.createBody(def);

        body.setUserData(this);
        fixtures = new ArrayList<>();
    }

    ArrayList<Fixture> fixtures = new ArrayList<>();

    protected void build() {
        createBody();
        TileMapShapeBuilder builder = new TileMapShapeBuilder();
        for (int i = 0; i < tiles.size(); i++) {
            Tile t = tiles.get(i);
            builder.addTile(t);
        }
        fixtures = builder.build(body);

        System.out.println("Built");
    }

    protected void addTile(Tile t) {
        tiles.add(t);

    }
    public void setBodyType(GameBodyType type) {
        this.type = type;
    }

    public void update(float dt) {

    }

    @Override
    public void beginCollide(Fixture fixture) {

    }

    @Override
    public void endCollide(Fixture fixture) {

    }
}
