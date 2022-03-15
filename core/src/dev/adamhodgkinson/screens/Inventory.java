package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

        stage = new Stage(); // to hold the buttons
        stage.getCamera().position.set(0, 0, 0);
        stage.getCamera().viewportWidth = client.uiCam.viewportWidth;
        stage.getCamera().viewportHeight = client.uiCam.viewportHeight;
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                System.out.println(keycode);
                if (keycode == 111) {
                    client.setScreen(new Menu(client));
                }
                return super.keyDown(event, keycode);
            }
        });


        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        batch.setProjectionMatrix(client.uiCam.combined);
        shapeRenderer.setProjectionMatrix(client.uiCam.combined);
        defaultUISkin = client.assets.get("skins/uiskin.json");
        createInvisButtons();
        client.playerData.retrieveWeaponData();
        TextureRegion region = ((TextureAtlas) client.assets.get("packed/pack.atlas")).findRegion("game/weapons/weapon_anime_sword");
        Image i = new Image(region);

        i.setHeight(imagePreviewHeight);
        System.out.println(i.getImageWidth());
        i.setWidth(region.getRegionWidth() * imagePreviewHeight / region.getRegionHeight());
        i.setPosition(0, 0);
        stage.addActor(i);


//        stage.addActor(table);
        font = client.assets.get("noto10.ttf");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    float imagePreviewHeight;

    public void createInvisButtons() {
        int cols = 7;
        int rows = 5;
        int leftMargin = 220;
        int padding = 20;
        float workableSpace = client.uiCam.viewportWidth - leftMargin - (padding * 2);
        float spacePerCol = workableSpace / cols;
        float gapPerCol = 25;
        float colWidth = spacePerCol - gapPerCol;

        float bottomMargin = 60;
        float workableHeight = client.uiCam.viewportHeight - bottomMargin - (padding * 2);
        float spacePerRow = workableHeight / rows;
        float gapPerRow = gapPerCol;
        float rowHeight = spacePerRow - gapPerRow;
        imagePreviewHeight = rowHeight;


        for (int i = 0; i < cols; i++) {
            float x = spacePerCol * i + leftMargin + padding - client.uiCam.viewportWidth / 2;
            for (int j = 0; j < rows; j++) {
                float y = spacePerRow * j + bottomMargin + padding - client.uiCam.viewportHeight / 2;
//                shapeRenderer.rect(x, y, colWidth, rowHeight);
                Button button = new Button(defaultUISkin);
                button.setColor(1, 1, 1, .5f);
                button.setWidth(colWidth);
                button.setHeight(rowHeight);
                button.setPosition(x, y);
                button.setVisible(true);
                button.setDisabled(false);

                int count = (rows - j - 1) * cols + i;
                button.setUserObject(count);
//                font.draw(batch, Integer.toString(count), x, y);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        System.out.println("clicked" + event.getTarget().getUserObject());
                    }
                });
                stage.addActor(button);
            }
        }
    }


    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act();
        ScreenUtils.clear(0, 0, 0, 1);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
//        batch.begin();

//        batch.end();

//        shapeRenderer.end();
        stage.draw();
    }

}
