package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
                System.out.println(character);
                game.getPlayer().getHit(); // to test animation switching
                return super.keyTyped(character);
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

        client.batch.draw(game.getPlayer().getFrame(), 0, 0, 2, 3.5f);
        client.batch.end();

    }

    public void update(float dt) {
        game.update(dt);
    }
    public void drawTile(Tile t) {
        client.batch.draw(gameTextures.findRegion(t.getTextureName(), t.getTextureIndex()), t.getX(), t.getY(), 1, 1);
    }
}
