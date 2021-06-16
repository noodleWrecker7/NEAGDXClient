package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player extends Sprite implements Physical {

    GameBodyType type = GameBodyType.PLAYER;

    public Player(World world, AssetManager assets, int x, int y) {
        super(world, x, y);

        this.speed = 8f;
        float width = 2;
        float height = 3;
        float density = 5;
        float friction = 0;
        float linearDamping = 2;
        float sensorHeight = .4f;




        // sensor
        FixtureDef sensorFixtureDef = new FixtureDef();
        PolygonShape shape2 = new PolygonShape();
        shape2.setAsBox(width / 2, sensorHeight / 2, new Vector2(0, -height / 2), 0);
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.shape = shape2;
        body.createFixture(sensorFixtureDef);

        body.setUserData(this);

        // needs to dynamically figure out asset name
        this.setDefaultAnims(assets, "elf_m");
    }

    public void defaultAnimations() {
    }


    public void moveKeyUp(int keycode) {
        int y = 0, x = 0;
        switch (keycode) {
            case 51: // w
                y--;
                break;
            case 29: //a
                x++;
                break;
            case 47: //s
                y++;
                break;
            case 32: //d
                x--;
                break;
        }

        movement.add(x, y);

        updateFlippage();

    }

    public void moveKeyDown(int keycode) {
        int y = 0, x = 0;
        switch (keycode) {
            case 51: // w
                y++;
                break;
            case 29: //a
                x--;
                break;
            case 47: //s
                y--;
                break;
            case 32: //d
                x++;
                break;
        }
        movement.add(x, y);
        updateFlippage();

    }

}
