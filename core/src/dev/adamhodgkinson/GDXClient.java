package dev.adamhodgkinson;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import dev.adamhodgkinson.screens.Loading;

public class GDXClient extends Game {
    public SpriteBatch batch;
    Sprite sprite;

    public OrthographicCamera cam = new OrthographicCamera();

    public AssetManager assets;
    public BitmapFont font;


    @Override
    public void create() {
        batch = new SpriteBatch();

		cam.setToOrtho(false, 60, 32); // ortho camera, 1 unit is one tile
		cam.position.set(0,0, 0);
		cam.update();
		batch.setProjectionMatrix(cam.combined); // batch draws through camera viewport

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("Game.atlas")); // loads gameplay textures
        sprite = atlas.createSprite("big_demon_idle_anim_f0"); // temporary sprite for testing display
        sprite.setPosition(0, 0);

		assets = new AssetManager(); // manages loading of multiple assets asynchronously
		assets.load("Game.atlas", TextureAtlas.class);
		assets.load("UI.atlas", TextureAtlas.class);

		font = new BitmapFont();
		font.getData().setScale(1f,1f);

        setScreen(new Loading(this)); // changes screen to the loading screen
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}


//	public BitmapFont font;


	/*@Override
	public void create () {
		batch = new SpriteBatch();
//		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("Game.atlas"));
//		sprite = atlas.createSprite("big_demon_idle_anim_f0");
//		sprite.setPosition(10,10);
		font = new BitmapFont();
		font.getData().setScale(.1f,.1f);


	}*/