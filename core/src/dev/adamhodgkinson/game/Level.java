package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import dev.adamhodgkinson.game.enemies.Enemy;
import dev.adamhodgkinson.game.enemies.Runt;
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
import java.util.HashMap;


public class Level {
    TileGroup solids;
    ArrayList<Enemy> enemiesArray;

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

    public void initialize(FileHandle file, World world, AssetManager assets) throws ParserConfigurationException, IOException, SAXException {
        // Loads and parses xml file
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file.read());
        doc.getDocumentElement().normalize();

        NodeList solidTiles = doc.getElementsByTagName("solid");
        solids = new TileGroup(world);

        for (int i = 0; i < solidTiles.getLength(); i++) {
            NamedNodeMap attr = solidTiles.item(i).getAttributes();

            solids.addTile(Tile.createTileFromXml(attr)); // moved tile initialization to a static method in the tile class
        }
        solids.build();
        solids.setBodyType(GameBodyType.TILE_SOLID);
        // load enemies


        NodeList enemies = doc.getElementsByTagName("Enemies");
        if (enemies.getLength() > 1) {
            System.out.println("Level Read Error: Multiple Enemies tags found. Only the first will be used");
        }
        NodeList enemiesTag = enemies.item(0).getChildNodes();

        enemiesArray = new ArrayList<>();
        for (int i = 0; i < enemiesTag.getLength() ; i++){
            Node node = enemiesTag.item(i);
            Enemy e = Enemy.createFromNode(node, assets, world);
            enemiesArray.add(e);
        }
    }

    public ArrayList<Enemy> getEnemiesArray(){
        return enemiesArray;
    }
}

