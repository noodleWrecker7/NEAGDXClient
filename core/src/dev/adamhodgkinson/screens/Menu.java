package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import dev.adamhodgkinson.GDXClient;

public class Menu extends ScreenAdapter {
    GDXClient client;

    Stage stage; // handles multiple actors eg buttons - and handles input events

    public Menu(GDXClient app) {
        this.client = app;

        System.out.println("We at menu");

        Skin skin = new Skin(); // will store texture data
        TextureAtlas texAtlas = client.assets.get(Gdx.files.internal("packed/pack.atlas").path());
        skin.addRegions(texAtlas);

        stage = new Stage(); // renderer of buttons
        stage.getCamera().position.set(0, 0, 0); // centers cam
        stage.getCamera().viewportWidth = client.uiCam.viewportWidth;
        stage.getCamera().viewportHeight = client.uiCam.viewportHeight;

        String[] buttonNames = {"ButtonLevelSelect", "ButtonMultiplayer", "ButtonInventory", "ButtonOptions", "ButtonExit"};

        float buttonHeight = texAtlas.findRegion("Menu/ButtonInventory").originalHeight;

        float topMargin = 25f; // extra height above buttons - makes room for title image

        float verticalSpacePerButton = (client.uiCam.viewportHeight - topMargin) / buttonNames.length; // how much space is possible to allocate per button
        float spaceBetweenButtons = verticalSpacePerButton / 3; // how much gap should be between a button

        float scale = (verticalSpacePerButton - spaceBetweenButtons) / buttonHeight;
        float scaledHeight = buttonHeight * scale;


        for (int i = 0; i < buttonNames.length; i++) {

            Button.ButtonStyle imageButtonStyle = new Button.ButtonStyle(); // how the button is displayed
            imageButtonStyle.up = skin.getDrawable("Menu/" + buttonNames[i]); // assigns texture to button

            Button menuButton = new Button(imageButtonStyle); // creates button from styling
            menuButton.setTransform(true);

            // starts from top and lowers the height by equal amount per button
            float yPos = client.uiCam.viewportHeight / 2 - (verticalSpacePerButton) * i - topMargin - scaledHeight / 2;

            menuButton.setPosition(0 + scaledHeight, yPos, Align.center);
            menuButton.setScale(scale);

            stage.addActor(menuButton); // adds button to stage

            menuButton.setName(buttonNames[i]); // stores name of button for use later

            menuButton.addListener(new ClickListener() { // adds listener to each button
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    goToButtonTarget(event.getListenerActor().getName());
                }
            });
        }
    }

    public void goToButtonTarget(String name) {
        switch (name) {
            case "ButtonLevelSelect":
                client.setScreen(new LevelSelect(client));
                break;
            case "ButtonMultiplayer":
                client.setScreen(new GameScreen(client, Gdx.files.internal("core/assets/level.json")));
                break;
            case "ButtonInventory":
                client.setScreen(new Inventory(client));
                break;
            case "ButtonExit":
                Gdx.app.exit();
                break;
        }
    }

    @Override
    public void show() {
        super.show();
        client.playerData.retrieveWeaponData();
        // setup input handling
        Gdx.input.setInputProcessor(stage); // input must be directed to stage for this screen

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        ScreenUtils.clear(0, 0, 0, 1);
        // render menu screen
        stage.draw();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        super.dispose();
    }
}


//        Gdx.input.setInputProcessor(null); // removes input listener
