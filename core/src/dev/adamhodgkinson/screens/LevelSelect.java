package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import dev.adamhodgkinson.GDXClient;

import java.util.ArrayList;

public class LevelSelect extends ScreenAdapter {

    static final int PAGE_SIZE = 10;
    static int verticalOffset = 180;
    GDXClient client;
    Stage stage;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    BitmapFont font;
    VerticalGroup vg;
    HorizontalGroup hg;
    Skin defaultUISkin;
    ArrayList<LevelEntry> entries;
    TextField searchBox;
    int pageNo;


    public LevelSelect(GDXClient app) {
        client = app;
        entries = new ArrayList<>();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(client.uiCam.combined);

        stage = new Stage(); // to hold the buttons
        stage.getCamera().position.set(0, 0, 0);
        stage.getCamera().viewportWidth = client.uiCam.viewportWidth;
        stage.getCamera().viewportHeight = client.uiCam.viewportHeight;

        defaultUISkin = new Skin(Gdx.files.internal("skins/uiskin.json"));

        searchBox = new TextField("", defaultUISkin);
        searchBox.setPosition(-searchBox.getWidth(), +stage.getHeight() / 2 - searchBox.getHeight() - 5);
        TextButton searchButton = new TextButton("Search", defaultUISkin);
        searchButton.setPosition(searchButton.getWidth(), +stage.getHeight() / 2 - searchButton.getHeight() - 5);
        stage.addActor(searchButton);
        stage.addActor(searchBox);


        for (int i = 0; i < 10; i++) {
            LevelEntry entry = new LevelEntry("Level1", "author", "date", defaultUISkin);
            entries.add(entry);
        }
        vg = new VerticalGroup();

        updateEntries();

        font = client.assets.get("noto25.ttf");
    }

    public void updateEntries() {
        vg.remove();

        for (int i = 0; i < entries.size(); i++) {
            vg.addActor(entries.get(i).hg);
        }
        vg.setPosition(0, verticalOffset);

        vg.space(5);
        stage.addActor(vg);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act();
        vg.layout();
        ScreenUtils.clear(0, 0, 0, 1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(.3f, .3f, .3f, 1);
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).draw(shapeRenderer);
        }
        shapeRenderer.end();

        stage.draw();
    }

    static class LevelEntry {
        public HorizontalGroup hg;


        public LevelEntry(String name, String author, String date, Skin defaultUISkin) {
            hg = new HorizontalGroup();
            hg.space(200);
            hg.align(Align.left);

            HorizontalGroup innerLeft = new HorizontalGroup();
            HorizontalGroup innerRight = new HorizontalGroup();
            innerLeft.space(20);
            innerRight.space(20);

            innerLeft.addActor(new Label(name, defaultUISkin));
            innerLeft.addActor(new Label(author, defaultUISkin));
            innerLeft.addActor(new Label(date, defaultUISkin));
            innerRight.addActor(new TextButton("Download", defaultUISkin));
            innerRight.addActor(new TextButton("Play", defaultUISkin));

            hg.addActor(innerLeft);
            hg.addActor(innerRight);
            hg.pad(5);
            innerLeft.center();
            innerRight.center();
        }

        public void draw(ShapeRenderer shapeRenderer) {
            shapeRenderer.rect(hg.getX(), hg.getY() + verticalOffset, hg.getWidth(), hg.getHeight());
        }


    }
}
