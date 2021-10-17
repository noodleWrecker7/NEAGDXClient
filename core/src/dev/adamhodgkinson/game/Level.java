package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import dev.adamhodgkinson.game.enemies.Enemy;
import dev.adamhodgkinson.game.navigation.NavGraph;
import dev.adamhodgkinson.game.navigation.NavGraphBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;


public class Level {
    TileGroup solids;
    ArrayList<Enemy> enemiesArray;
    Vector2 playerSpawnPos;
    NavGraph navGraph;
    int worldWidth;
    int worldHeight;
    //    GridPoint2 playerLastNavPos;
    Game game;

    public TileGroup getSolids() {
        return solids;
    }

    public Level(FileHandle file, World world, AssetManager assets, Game _game) {
        game = _game;
        try {
            initialize(file, world, assets);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed reading level file");
            System.exit(1);
        }
    }

    public NavGraph getNavGraph() {
        return navGraph;
    }

    public void update(float dt) {
        // iterates through each enemy to update them
        for (int i = 0; i < enemiesArray.size(); i++) {
            enemiesArray.get(i).update(dt);
            if (enemiesArray.get(i).isDead()) {
                enemiesArray.get(i).destroy(game.world);
                enemiesArray.remove(i);
                i--;
            }
        }
    }

    public void initialize(FileHandle file, World world, AssetManager assets) throws ParserConfigurationException, IOException, SAXException {
        // Loads and parses xml file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file.read());
        doc.getDocumentElement().normalize();

        Node level = doc.getElementsByTagName("ShootyStabbyLevel").item(0);
        worldWidth = Integer.parseInt(level.getAttributes().getNamedItem("width").getNodeValue());
        worldHeight = Integer.parseInt(level.getAttributes().getNamedItem("height").getNodeValue());
        Node spawnpos = doc.getElementsByTagName("spawnpos").item(0);
        playerSpawnPos = new Vector2(Integer.parseInt(spawnpos.getAttributes().getNamedItem("x").getNodeValue()), Integer.parseInt(spawnpos.getAttributes().getNamedItem("y").getNodeValue()));


        NodeList solidTiles = doc.getElementsByTagName("solid");
        solids = new TileGroup(world);

        for (int i = 0; i < solidTiles.getLength(); i++) {
            NamedNodeMap attr = solidTiles.item(i).getAttributes();
            Tile t = Tile.createTileFromXml(attr);
            if (t.getX() >= worldWidth || t.getY() >= worldHeight || t.getX() < 0 || t.getY() < 0) {
                System.out.println("Tile outside level bounds at ( " + t.getX() + " , " + t.getY() + " )");
                continue;
            }
            solids.addTile(t); // moved tile initialization to a static method in the tile class
        }
        solids.build();
        solids.setBodyType(GameBodyType.TILE_SOLID);
        NavGraphBuilder navGraphBuilder = new NavGraphBuilder(worldWidth, worldHeight + 2, solids, 10, 16, world.getGravity().y);
        navGraph = navGraphBuilder.finish();


        // load enemies
        NodeList enemies = doc.getElementsByTagName("enemy");

        enemiesArray = new ArrayList<>();
        for (int i = 0; i < enemies.getLength(); i++) {
            Node node = enemies.item(i);
            Enemy e = Enemy.createFromNode(node, assets, world);
            enemiesArray.add(e);
        }

        BodyDef worldEdgeDef = new BodyDef();
        worldEdgeDef.type = BodyDef.BodyType.StaticBody;
        worldEdgeDef.allowSleep = true;
        worldEdgeDef.position.x = 0;
        worldEdgeDef.position.y = 0;
        Body worldEdge = world.createBody(worldEdgeDef);

        FixtureDef fixDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        fixDef.density = 1;
        shape.setAsBox(.1f, worldHeight / 2f, new Vector2(-.6f, worldHeight / 2f), 0);
        fixDef.shape = shape;
        worldEdge.createFixture(fixDef);
        shape.setAsBox(.1f, worldHeight / 2f, new Vector2(worldWidth + .6f, worldHeight / 2f), 0);
        fixDef.shape = shape;
        worldEdge.createFixture(fixDef);

        shape.setAsBox(worldWidth / 2f, .1f, new Vector2(worldWidth / 2f, -.6f), 0);
        fixDef.shape = shape;
        worldEdge.createFixture(fixDef);
        shape.setAsBox(worldWidth / 2f, .1f, new Vector2(worldWidth / 2f, worldHeight + .6f), 0);
        fixDef.shape = shape;
        worldEdge.createFixture(fixDef);


    }

    public ArrayList<Enemy> getEnemiesArray() {
        return enemiesArray;
    }
}

