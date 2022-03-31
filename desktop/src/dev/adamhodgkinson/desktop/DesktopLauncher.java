package dev.adamhodgkinson.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import dev.adamhodgkinson.GDXClient;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        int zoom = 1;
        config.width = Math.round(960 * zoom);
        config.height = Math.round(512 * zoom);
        config.resizable = false;
        config.addIcon("icon.png", Files.FileType.Internal);


//        TexturePacker.Settings settings = new TexturePacker.Settings();
//        settings.stripWhitespaceX = true; // needed to ensure hitboxes and visuals match
//        settings.stripWhitespaceY = true;
//        TexturePacker.process(settings, "raw", "packed", "pack");

        new LwjglApplication(new GDXClient(zoom), config);
    }
}
