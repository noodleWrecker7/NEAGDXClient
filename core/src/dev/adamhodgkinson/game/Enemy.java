package dev.adamhodgkinson.game;

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

public class Enemy extends Animated implements Physical{
    float health;
    Vector2 spawnPos; // where the enemy started
    int moveRange; // how far the enemy can move from its spawn point
    Body body;

    //Weapon weapon;

    public Enemy(AssetManager assets, String textureName, World world, int x , int y){
        // creates animations, adds them to object
        TextureAtlas atlas = assets.get("packed/pack.atlas");
        String prefix = "game/sprites/"+textureName;
        Animation<TextureRegion> runAnimation = new Animation<TextureRegion>(.15f, atlas.findRegions(prefix +"_run_anim"), Animation.PlayMode.LOOP);
        Animation<TextureRegion> hitAnimation = new Animation<TextureRegion>(.5f, atlas.findRegions(prefix+"_hit_anim"), Animation.PlayMode.NORMAL);
        Animation<TextureRegion> idleAnimation = new Animation<TextureRegion>(.25f, atlas.findRegions(prefix + "_idle_anim"), Animation.PlayMode.LOOP);
        this.addAnimation("idle", idleAnimation);
        this.addAnimation("run", runAnimation);
        this.addAnimation("hit", hitAnimation);

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
