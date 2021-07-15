package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import dev.adamhodgkinson.game.enemies.Enemy;
import dev.adamhodgkinson.game.navigation.NavGraph;
import dev.adamhodgkinson.game.navigation.Vertex;
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
    int width;
    int height;

    public TileGroup getSolids() {
        return solids;
    }

    public Level(FileHandle file, World world, AssetManager assets) {
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

    public void initialize(FileHandle file, World world, AssetManager assets) throws ParserConfigurationException, IOException, SAXException {
        // Loads and parses xml file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file.read());
        doc.getDocumentElement().normalize();

        Node level = doc.getElementsByTagName("ShootyStabbyLevel").item(0);
        width = Integer.parseInt(level.getAttributes().getNamedItem("width").getNodeValue());
        height = Integer.parseInt(level.getAttributes().getNamedItem("height").getNodeValue());
        Node spawnpos = doc.getElementsByTagName("spawnpos").item(0);
        playerSpawnPos = new Vector2(Integer.parseInt(spawnpos.getAttributes().getNamedItem("x").getNodeValue()), Integer.parseInt(spawnpos.getAttributes().getNamedItem("y").getNodeValue()));


        NodeList solidTiles = doc.getElementsByTagName("solid");
        solids = new TileGroup(world);

        for (int i = 0; i < solidTiles.getLength(); i++) {
            NamedNodeMap attr = solidTiles.item(i).getAttributes();
            Tile t = Tile.createTileFromXml(attr);
            if (t.getX() >= width || t.getY() >= height || t.getX() < 0 || t.getY() < 0) {
                System.out.println("Tile outside level bounds at ( " + t.getX() + " , " + t.getY() + " )");
                continue;
            }
            solids.addTile(t); // moved tile initialization to a static method in the tile class
        }
        solids.build();
        solids.setBodyType(GameBodyType.TILE_SOLID);
        ArrayList<Tile> tiles = solids.getTiles();
        navGraph = new NavGraph(width, height);
        for (int i = 0; i < tiles.size(); i++) {
            Tile t = tiles.get(i);
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
        navGraph.compile();
        // add edges
        for (int i = 0; i < navGraph.getNodesArray().length; i++) {
            Vertex v = navGraph.getNodesArray()[i];
            if (navGraph.getVertexByCoords((short) (v.x - 1), v.y) != null) {
                navGraph.addBiDirEdge(v.x, v.y, (short) (v.x - 1), v.y, (byte) 1);
            }
        }

        // load enemies
        NodeList enemies = doc.getElementsByTagName("enemy");

        enemiesArray = new ArrayList<>();
        for (int i = 0; i < enemies.getLength(); i++) {
            Node node = enemies.item(i);
            Enemy e = Enemy.createFromNode(node, assets, world);
            enemiesArray.add(e);
        }
    }

    public ArrayList<Enemy> getEnemiesArray() {
        return enemiesArray;
    }
}

