package dev.adamhodgkinson.game;

import org.w3c.dom.NamedNodeMap;

public class Tile {
    private short x;
    private short y;
    String textureName;
    int textureIndex = -1;

    public String getTextureName() {
        return textureName;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    public static Tile createTileFromXml(NamedNodeMap attr) {
        Tile t = new Tile();
        t.x = Short.parseShort(attr.getNamedItem("x").getNodeValue());
        t.y = Short.parseShort(attr.getNamedItem("y").getNodeValue());
        t.textureName = attr.getNamedItem("texture").getNodeValue();
        if (attr.getNamedItem("i") != null) {
            t.textureIndex = Integer.parseInt(attr.getNamedItem("i").getNodeValue());
        }
        return t;
    }
}


