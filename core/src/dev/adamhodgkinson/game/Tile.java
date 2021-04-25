package dev.adamhodgkinson.game;

import org.w3c.dom.NamedNodeMap;

public class Tile {
    private int x;
    private int y;
    String textureName;
    int textureIndex = -1;

    public String getTextureName() {
        return textureName;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static Tile createTileFromXml(NamedNodeMap attr) {
        Tile t = new Tile();
        t.x = Integer.parseInt(attr.getNamedItem("x").getNodeValue());
        t.y = Integer.parseInt(attr.getNamedItem("y").getNodeValue());
        t.textureName = attr.getNamedItem("texture").getNodeValue();
        if (attr.getNamedItem("i") != null) {
            t.textureIndex = Integer.parseInt(attr.getNamedItem("i").getNodeValue());
        }
        return t;
    }
}


