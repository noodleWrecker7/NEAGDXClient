package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import dev.adamhodgkinson.GDXClient;
import dev.adamhodgkinson.game.Game;
import dev.adamhodgkinson.game.Tile;
import dev.adamhodgkinson.game.UserInputHandler;
import dev.adamhodgkinson.game.UserInterfaceRenderer;
import dev.adamhodgkinson.game.navigation.Arc;
import dev.adamhodgkinson.game.navigation.NavGraph;
import dev.adamhodgkinson.game.navigation.Vertex;

public class GameScreen extends ScreenAdapter {

    GDXClient client;
    Game game;
    TextureAtlas gameTextures;
    UserInterfaceRenderer uirender;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;

    public GDXClient getClient() {
        return client;
    }

    public GameScreen(GDXClient client, FileHandle file) {
        this.client = client;

        // temporary
        this.game = new Game(client.playerData, client.assets, file); // this should get players data from somewhere, eg be initialised earlier get from server etc
        gameTextures = client.assets.get(Gdx.files.internal("packed/pack.atlas").path()); // keep this in the class to be used often

        uirender = new UserInterfaceRenderer(this.client, this.game);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
    }

    @Override
    /**Called immediately as the screen is made visible*/
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(new UserInputHandler(this));
    }

    @Override
    public void render(float delta) {
        client.worldCam.position.x = game.getPlayer().getPos().x;
        client.worldCam.position.y = game.getPlayer().getPos().y;
        client.worldCam.update();
        // Sets correct camera viewports for renderers
        batch.setProjectionMatrix(client.worldCam.combined);
        shapeRenderer.setProjectionMatrix(client.worldCam.combined);
        update(delta);
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        // actually render

        for (Tile t : game.getLevel().getSolids().getTiles()) {
            drawTile(t);
        }

        game.getPlayer().draw(batch);

        for (int i = 0; i < game.getLevel().getEnemiesArray().size(); i++) {
            game.getLevel().getEnemiesArray().get(i).draw(batch);
        }
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < game.getLevel().getEnemiesArray().size(); i++) {
            game.getLevel().getEnemiesArray().get(i).renderHealth(shapeRenderer);
        }
        shapeRenderer.end();
        if (client.debug) {
            renderNavGraph();
        }
        uirender.render();
    }

    public void renderNavGraph() {
        NavGraph ng = game.getLevel().getNavGraph();
        Vertex[] vertices = ng.getNodesArray();
        shapeRenderer.begin();
        for (int i = 0; i < vertices.length; i++) {
            Vertex v = vertices[i];
            shapeRenderer.setColor(255, 255, 255, 255);
            shapeRenderer.circle(v.x, v.y, .3f);
            shapeRenderer.setColor(0, 255, 0, 255);
            Arc[] edges = ng.getAdjacencyMatrix()[i];
            for (int j = 0; j < edges.length; j++) {
                if (edges[j] != null) {
                    Vertex v2 = ng.getNodesArray()[j];
                    if (ng.getAdjacencyMatrix()[j][i] != null) {
                        shapeRenderer.setColor(255, 0, 0, 255);
                    }
                    shapeRenderer.line(v.x, v.y, v2.x, v2.y);
                }
            }

        }
        shapeRenderer.end();
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
        batch.draw(t.getTexture(), t.getX() - .5f, t.getY() - .5f, 1, 1); // had to add .5f as forgot coords are at center

    }

    public Game getGame() {
        return game;
    }
}
