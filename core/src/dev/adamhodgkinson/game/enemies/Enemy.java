package dev.adamhodgkinson.game.enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import dev.adamhodgkinson.game.Animated;
import dev.adamhodgkinson.game.Physical;
import dev.adamhodgkinson.game.Sprite;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public class Enemy extends Sprite implements Physical {
    float health;
    Vector2 spawnPos; // where the enemy started
    int moveRange; // how far the enemy can move from its spawn point
    Body body;

    HashMap<String,Class> map; // dynamically convert node name to class

    static Enemy createFromNode(Node node){
        String name = node.getNodeName();
        return new Enemy();
    }

    //Weapon weapon;

    public Enemy(AssetManager assets, String textureName, World world, int x , int y){
        // creates animations, adds them to object
        this.setDefaultAnims(assets, textureName);
        BodyDef def = new BodyDef();
        def.position.x = x;
        def.position.y = y;
        def.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(def);
        body.setFixedRotation(true);


    }

    public void attack(){

    }

    public void die(){
    }

    public void update(float dt){
        super.update(dt);

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
