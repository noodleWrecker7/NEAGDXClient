package dev.adamhodgkinson.game;

public class LevelDataSchema {
    int height;
    int width;
    int playerSpawnPosX;
    int playerSpawnPosY;
    String name;

    TileData[] tiles;
    EnemyData[] enemies;


}

class TileData {
    short x;
    short y;
    String texture;
    int i; // tile index, for variations of same tile
}

class EnemyData {
    String texture;
    int x;
    int y;
    float health;
}
