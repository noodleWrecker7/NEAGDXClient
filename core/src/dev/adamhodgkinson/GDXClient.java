package dev.adamhodgkinson;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dev.adamhodgkinson.screens.Loading;

public class GDXClient extends Game {
    public AssetManager assets;

    public SpriteBatch batch;

    public OrthographicCamera cam;

    public BitmapFont font;

    public ShapeRenderer shapeRenderer;
    float zoom;

    public boolean debug = false;
    public GDXClient(float zoom) {
        super();
        this.zoom = zoom;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        // Creates camera and sets correct sizing and positioning
        cam = new OrthographicCamera();
        int pixelsPerUnit = 32;
        cam.setToOrtho(false, (Gdx.graphics.getWidth() / pixelsPerUnit) / zoom, (Gdx.graphics.getHeight() / pixelsPerUnit) / zoom); // ortho camera, 1 unit is one tile
        cam.position.set(5, 3, 0);
        cam.update();

        // Sets correct camera viewports for renderers
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer.setProjectionMatrix(cam.combined);

        assets = new AssetManager(); // manages loading of multiple assets asynchronously

        // todo important, remember to mention in design that there used to be multiple atlases
        assets.load("packed/pack.atlas", TextureAtlas.class);

        // Standard font for use throughout the game
        font = new BitmapFont();
        font.getData().setScale(1f, 1f);

        // Starts the loading screen while the assets are loading
        setScreen(new Loading(this)); // changes screen to the loading screen
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
