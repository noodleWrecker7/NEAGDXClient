package dev.adamhodgkinson.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import dev.adamhodgkinson.GDXClient;

public class Loading extends ScreenAdapter {

    GDXClient client;
    ScreenAdapter nextScreen;
    SpriteBatch batch;

    public Loading(GDXClient gdxClient, ScreenAdapter screen) {
        this.client = gdxClient;
        this.nextScreen = screen;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        batch.setProjectionMatrix(client.worldCam.combined);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1); // clear screen

        // todo render loading screen & progress bar
        if (client.assets.isLoaded("noto10.ttf")) {

        }
        if (client.assets.update()) { // if its ready
            System.out.println("loaded assets");
            client.setScreen(this.nextScreen); // go to menu
        }
    }
}