package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ScreenUtils;
import dev.adamhodgkinson.GDXClient;
import dev.adamhodgkinson.PlayerData;
import dev.adamhodgkinson.game.Game;
import dev.adamhodgkinson.game.Tile;
import dev.adamhodgkinson.game.UserInputHandler;
import dev.adamhodgkinson.game.navigation.Arc;
import dev.adamhodgkinson.game.navigation.NavGraph;
import dev.adamhodgkinson.game.navigation.Vertex;

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
    /**Called immediately as the screen is made visible*/
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(new UserInputHandler(this));
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

        game.getPlayer().draw(client.batch);
        for (int i = 0; i < game.getLevel().getEnemiesArray().size(); i++) {
            game.getLevel().getEnemiesArray().get(i).draw(client.batch);
        }

        client.batch.end();
        if (client.debug) {
            renderNavGraph();
        }
    }

    public void renderNavGraph() {
        NavGraph ng = game.getLevel().getNavGraph();
        Vertex[] vertices = ng.getNodesArray();
        client.shapeRenderer.begin();
        for (int i = 0; i < vertices.length; i++) {
            Vertex v = vertices[i];
            client.shapeRenderer.setColor(255, 255, 255, 255);
            client.shapeRenderer.circle(v.x, v.y, .3f);
            client.shapeRenderer.setColor(0, 255, 0, 255);
            Arc[] edges = ng.getAdjacencyMatrix()[i];
            for (int j = 0; j < edges.length; j++) {
                if (edges[j] != null) {
                    Vertex v2 = ng.getNodesArray()[j];
                    client.shapeRenderer.line(v.x, v.y, v2.x, v2.y);
                }
            }

        }
        client.shapeRenderer.end();
    }

    public void update(float dt) {
        game.update(dt);
    }

    /**
     * Draws the given tile to the SpriteBatch created in the client class
     *
     * @param t The tile to be rendered
     */
    public void drawTile(Tile t) {
        client.batch.draw(gameTextures.findRegion(t.getTextureName(), t.getTextureIndex()), t.getX() - .5f, t.getY() - .5f, 1, 1); // had to add .5f as forgot coords are at center

    }

    public Game getGame() {
        return game;
    }
}
