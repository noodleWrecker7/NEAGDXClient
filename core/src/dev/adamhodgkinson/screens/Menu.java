package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
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

        Skin skin = new Skin(); // will store texture data
        TextureAtlas texAtlas = client.assets.get("core/assets/packed/pack.atlas");
        skin.addRegions(texAtlas);

        float scaledHeight = client.zoom * texAtlas.findRegion("Menu/ButtonLevelSelect").originalHeight;
//        skin.setScale(scaleFactor); // to shrink the images to fit on viewport


        stage = new Stage(); // to hold the buttons
        stage.getCamera().position.set(0, 0, 0);
        stage.getCamera().viewportWidth = client.uiCam.viewportWidth;
        stage.getCamera().viewportHeight = client.uiCam.viewportHeight;

        String[] buttonNames = {"ButtonLevelSelect", "ButtonMultiplayer", "ButtonInventory", "ButtonOptions", "ButtonExit"};


        float topMargin = 5f; // needs to be tied to viewport scale
        float verticalSpacePerButton = (client.uiCam.viewportHeight - topMargin) / buttonNames.length;

        float buttonHeight = texAtlas.findRegion("Menu/ButtonInventory").originalHeight;
        float spaceBetweenButtons = buttonHeight/3;
        float totalHeight = (buttonHeight +spaceBetweenButtons) * buttonNames.length + spaceBetweenButtons;

//        if(totalHeight > client.uiCam.viewportHeight){
            float scale = client.uiCam.viewportHeight / totalHeight;
//        }


        for (int i = 0; i < buttonNames.length; i++) {
            ImageButton menuButton;
            ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle(); // how the button is displayed

            imageButtonStyle.up = skin.getDrawable("Menu/"+buttonNames[i]); // assigns texture to button
            menuButton = new ImageButton(imageButtonStyle); // creates button from styling

            // starts from top and lowers the height by and equal amount for each button
            float yPos = client.uiCam.viewportHeight / 2 - (spaceBetweenButtons+buttonHeight)*i;
            menuButton.setPosition(0, yPos, Align.center);

            stage.addActor(menuButton); // adds button to stage

            menuButton.setName(buttonNames[i]); // stores name of button for use later

            menuButton.addListener(new ClickListener() { // adds listener to each button
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // handle input here
                    // change screen etc
                    goToButtonTarget(event.getListenerActor().getName());
                }
            });
        }
    }

    public void goToButtonTarget(String name) {
        switch (name) {
            case "ButtonLevelSelect":
                client.setScreen(new GameScreen(client));
                break;
            case "ButtonInventory":
                client.setScreen(new Inventory(client));
                break;
        }
    }

    @Override
    public void show() {
        // setup input handling
        Gdx.input.setInputProcessor(stage); // input must be directed to stage for this screen

    }

    @Override
    public void render(float delta) {
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
