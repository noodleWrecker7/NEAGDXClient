package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import dev.adamhodgkinson.GDXClient;

public class Inventory extends ScreenAdapter {
    GDXClient client;
    Stage stage;
    BitmapFont font;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    Skin defaultUISkin;

    public Inventory(GDXClient app) {
        client = app;
        stage = new Stage();

        stage = new Stage(); // to hold the buttons
        stage.getCamera().position.set(0, 0, 0);
        stage.getCamera().viewportWidth = client.uiCam.viewportWidth;
        stage.getCamera().viewportHeight = client.uiCam.viewportHeight;


        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        batch.setProjectionMatrix(client.uiCam.combined);
        shapeRenderer.setProjectionMatrix(client.uiCam.combined);
        defaultUISkin = client.assets.get("skins/uiskin.json");
    /*    TextureAtlas atlas = client.assets.get("packed/pack.atlas");
        Skin invButtonSkins = new Skin();
        invButtonSkins.addRegions(atlas);


        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.up = invButtonSkins.getDrawable("game/weapons/weapon_anime_sword");
        ImageButton button = new ImageButton(buttonStyle);

        ImageButton.ImageButtonStyle buttonStyle2 = new ImageButton.ImageButtonStyle();
        buttonStyle2.up = invButtonSkins.getDrawable("game/weapons/weapon_axe");
        ImageButton button2 = new ImageButton(buttonStyle2);

        ImageButton.ImageButtonStyle buttonStyle3 = new ImageButton.ImageButtonStyle();
        buttonStyle3.up = invButtonSkins.getDrawable("game/weapons/weapon_knife");
        ImageButton button3 = new ImageButton(buttonStyle3);


        Table table = new Table(defaultUISkin);
        table.setSize(stage.getWidth(), stage.getHeight());
        table.setPosition(-table.getWidth()/2, -table.getHeight()/2);
        table.add(button).expand();

        table.add(button2);
        table.add(button3);*/


//        stage.addActor(table);
        font = client.assets.get("noto10.ttf");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act();
        ScreenUtils.clear(0, 0, 0, 1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        batch.begin();
        int cols = 7;
        int rows = 5;
        int leftMargin = 220;
        int padding = 20;
        float workableSpace = stage.getWidth() - leftMargin - (padding * 2);
        float spacePerCol = workableSpace / cols;
        float gapPerCol = 25;
        float colWidth = spacePerCol - gapPerCol;

        float bottomMargin = 60;
        float workableHeight = stage.getHeight() - bottomMargin - (padding * 2);
        float spacePerRow = workableHeight / rows;
        float gapPerRow = gapPerCol;
        float rowHeight = spacePerRow - gapPerRow;

        for (int i = 0; i < cols; i++) {
            float x = spacePerCol * i + leftMargin + padding - stage.getWidth() / 2;
            for (int j = 0; j < rows; j++) {
                float y = spacePerRow * j + bottomMargin + padding - stage.getHeight() / 2;
                shapeRenderer.rect(x, y, colWidth, rowHeight);
                int count = (rows - j - 1) * cols + i;
                font.draw(batch, Integer.toString(count), x, y);

            }

        }
        batch.end();

        shapeRenderer.end();
        stage.draw();
    }

}
