package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import dev.adamhodgkinson.GDXClient;
import dev.adamhodgkinson.PlayerData;
import dev.adamhodgkinson.game.Game;
import dev.adamhodgkinson.game.Tile;

public class GameScreen extends ScreenAdapter {

    GDXClient client;
    Game game;
    TextureAtlas gameTextures;

    public GameScreen(GDXClient client) {
        this.client = client;

        // temporary
        this.game = new Game(new PlayerData(), client.assets); // this should get players data from somewhere, eg be initialised earlier get from server etc
        gameTextures = client.assets.get("packed/pack.atlas"); // keep this in the class to be used often


    }

    @Override
    public void show() {
        super.show();
        System.out.println("We here");

        Gdx.input.setInputProcessor(new InputAdapter() { // temporary
            @Override
            public boolean keyTyped(char character) {
//                System.out.println(character);
//                game.getPlayer().getHit(); // to test animation switching
                return super.keyTyped(character);
            }

            @Override
            public boolean keyDown(int keycode) {
//                System.out.println(keycode);
                switch (keycode) {
                    case 51:
                    case 29:
                    case 47:
                    case 32:
                        game.getPlayer().moveKeyDown(keycode);
                }
                return super.keyDown(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    case 51:
                    case 29:
                    case 47:
                    case 32:
                        game.getPlayer().moveKeyUp(keycode);
                }
                return super.keyDown(keycode);
            }
        });
    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0, 0, 0, 1);
        client.batch.begin();
        // actually render

        for (Tile t : game.getLevel().getSolids().getTiles()) {
            drawTile(t);
        }

        Vector2 pos = game.getPlayer().getPos();
        client.batch.draw(game.getPlayer().getFrame(), pos.x - 1, pos.y - 1.5f, 2, 3.5f); /* had to add coord offsets to account for being at center of object   */
        client.batch.end();
    }

    public void update(float dt) {
        game.update(dt);
    }

    public void drawTile(Tile t) {
        client.batch.draw(gameTextures.findRegion(t.getTextureName(), t.getTextureIndex()), t.getX() - .5f, t.getY() - .5f, 1, 1); // had to add .5f as forgot coords are at center

    }
}
