package dev.adamhodgkinson.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import dev.adamhodgkinson.GDXClient;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960 *2;
        config.height = 512*2;
        config.resizable = false;

        TexturePacker.Settings settings = new TexturePacker.Settings();
        TexturePacker.process("raw", "core/assets/packed", "pack");

        new LwjglApplication(new GDXClient(), config);
    }
}
