package dev.adamhodgkinson.screens;

import com.badlogic.gdx.ScreenAdapter;
import dev.adamhodgkinson.GDXClient;

public class GameScreen extends ScreenAdapter {

    private GDXClient client;

    public GameScreen(GDXClient client) {
        this.client = client;

    }

    @Override
    public void show() {
        super.show();
        System.out.println("gameee");
    }
}
