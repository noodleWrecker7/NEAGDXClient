package dev.adamhodgkinson.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ScreenUtils;
import dev.adamhodgkinson.GDXClient;

public class Loading extends ScreenAdapter {

    GDXClient client;

    public Loading(GDXClient gdxClient) {
        this.client = gdxClient;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,1); // clear screen
        if (client.assets.update()) { // if its ready
            System.out.println("loaded assets");
            client.setScreen(new Menu(client)); // go to menu
        }
        // todo render loading screen & progress bar
    }
}