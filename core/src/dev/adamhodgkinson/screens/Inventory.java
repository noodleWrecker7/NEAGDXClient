package dev.adamhodgkinson.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import dev.adamhodgkinson.GDXClient;

public class Inventory extends ScreenAdapter {
    GDXClient client;
    Stage stage;
    BitmapFont font;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    public Inventory(GDXClient app) {
        client = app;
        stage = new Stage();

        stage = new Stage(); // to hold the buttons
        stage.getCamera().position.set(0, 0, 0);
        stage.getCamera().viewportWidth = client.uiCam.viewportWidth;
        stage.getCamera().viewportHeight = client.worldCam.viewportHeight;


        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        batch.setProjectionMatrix(client.uiCam.combined);
        shapeRenderer.setProjectionMatrix(client.uiCam.combined);

        font = client.assets.get("noto10.ttf");
    }

    @Override
    public void render(float delta){
        font.draw(batch, "hello", 0,0);
    }

}
