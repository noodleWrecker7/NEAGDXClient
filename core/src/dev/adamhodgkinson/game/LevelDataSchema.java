package dev.adamhodgkinson.game;


import dev.adamhodgkinson.WeaponData;

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
}


class EnemyData {
    String texture;
    int x;
    int y;
    float health;
    WeaponData weapon;
}
