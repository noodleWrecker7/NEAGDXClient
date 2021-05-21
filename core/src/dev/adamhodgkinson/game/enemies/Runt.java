package dev.adamhodgkinson.game.enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;

public class Runt extends Enemy{
    public Runt(AssetManager assets, String textureName, World world, int x, int y) {

        super(assets, textureName, world, x, y);
    }
}
