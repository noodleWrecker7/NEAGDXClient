package dev.adamhodgkinson.game;

import com.badlogic.gdx.math.Vector2;
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

    public void setBodyType(GameBodyType type) {
        this.type = type;
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
     /*       Vector2 v1 = new Vector2(t.getX() - .5f, t.getY() - .5f); // i could definitely have looped this bit
            Vector2 v2 = new Vector2(t.getX() - .5f, t.getY() + .5f);
            Vector2 v3 = new Vector2(t.getX() + .5f, t.getY() - .5f);
            Vector2 v4 = new Vector2(t.getX() + .5f, t.getY() + .5f);

            vectors.add(v1);
            vectors.add(v2);
            vectors.add(v3);
            vectors.add(v4);*/
        }
        fixtures = builder.build(body);

      /*  Vector2[] array = new Vector2[vectors.size()];
        vectors.toArray(array);*/
        /*chainShape = new ChainShape();
        chainShape.createChain(array);
        fixture = body.createFixture(chainShape, 1);*/

        System.out.println("Built");
//        System.out.println("og vertices" + array.length);
//        System.out.println("chain verts " + chainShape.getVertexCount());


    }

    protected void addTile(Tile t) {
        tiles.add(t);

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void beginCollide(Fixture fixture) {

    }

    @Override
    public void endCollide(Fixture fixture) {

    }
}
