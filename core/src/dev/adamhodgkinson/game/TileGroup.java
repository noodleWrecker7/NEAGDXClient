package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class TileGroup {
    GameBodyType type;
    private ArrayList<Tile> tiles;
    Body body;

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public TileGroup(World world) {
        tiles = new ArrayList<>();

        BodyDef def = new BodyDef();
        def.position.set(0, 0);
        body = world.createBody(def);
    }

    public void addTile(Tile t) {
        tiles.add(t);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(.5f, .5f, new Vector2(t.getX(), t.getY()), 0);
        body.createFixture(shape, 1);
    }
}
