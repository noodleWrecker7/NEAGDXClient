package dev.adamhodgkinson.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import dev.adamhodgkinson.GDXClient;

public class DevLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        float zoom = 1;
        config.width = Math.round(960 * zoom);
        config.height = Math.round(512 * zoom);
        config.resizable = false;
        config.addIcon("icon.png", Files.FileType.Internal);


        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.stripWhitespaceX = true; // needed to ensure hitboxes and visuals match
        settings.stripWhitespaceY = true;
        TexturePacker.process(settings, "raw", "core/assets/packed", "pack");

        GDXClient client = new GDXClient(zoom);
        client.debug = true;
        new LwjglApplication(client, config);
    }
}
