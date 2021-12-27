package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {
    private short x;
    private short y;
    String textureName;
    int textureIndex = -1;
    TextureRegion texture;

    public TextureRegion getTexture() {
        return texture;
    }

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

    public Tile(short x, short y, TextureRegion texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
    }

}


