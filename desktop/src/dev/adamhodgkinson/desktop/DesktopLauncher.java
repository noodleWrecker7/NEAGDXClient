package dev.adamhodgkinson.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import dev.adamhodgkinson.GDXClient;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        int zoom = 1;
        config.width = 960 * zoom;
        config.height = 512 * zoom;
        config.resizable = false;


        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.stripWhitespaceX = true; // needed to ensure hitboxes and visuals match
        settings.stripWhitespaceY = true;
        TexturePacker.process(settings, "raw", "core/assets/packed", "pack");

        new LwjglApplication(new GDXClient(zoom), config);
    }
}
