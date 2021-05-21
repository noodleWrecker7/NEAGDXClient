package dev.adamhodgkinson.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.World;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Level {
    TileGroup solids;

    public TileGroup getSolids() {
        return solids;
    }

    public Level(FileHandle file, World world) {
        try {
            initialize(file, world);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed reading level file");
            System.exit(1);
        }
    }

    public void initialize(FileHandle file, World world) throws ParserConfigurationException, IOException, SAXException {
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


    }
}
