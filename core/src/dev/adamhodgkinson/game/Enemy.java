package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Enemy extends Animated implements Physical{
    float health;
    Vector2 spawnPos; // where the enemy started
    int moveRange; // how far the enemy can move from its spawn point
    Body body;

    //Weapon weapon;
    public void attack(){

    }

    public void die(){
    }

    public void update(float dt){

    }
    public void render(SpriteBatch batch){

    }
    @Override
    public void beginCollide(Fixture fixture) {

    }

    @Override
    public void endCollide(Fixture fixture) {

    }


}
