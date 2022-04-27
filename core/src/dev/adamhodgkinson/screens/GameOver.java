package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.gson.Gson;
import dev.adamhodgkinson.GDXClient;
import dev.adamhodgkinson.WeaponData;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GameOver extends ScreenAdapter {

    GDXClient client;
    Stage stage;
    ShapeRenderer shapeRenderer;
    BitmapFont font;
    SpriteBatch batch;
    boolean success;
    int time;
    String bestTimeString = "loading...";
    Gson gson = new Gson();

    /**
     * Screen to display game over info to player,
     *
     * @param _success boolean flag wether or not player succeeded to complete the level
     * @param _time    time taken in seconds
     */
    public GameOver(GDXClient app, String levelID, boolean _success, int _time) {
        client = app;
        success = _success;
        time = _time;
        batch = new SpriteBatch();
        batch.setProjectionMatrix(client.uiCam.combined);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        font = client.assets.get("noto25.ttf");

        SetTimeBody body = new SetTimeBody();
        body.levelID = levelID;
        body.time = _time;
        System.out.println(levelID + " " + _time);

        // handles key input
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                // returns to menu on esc
                if (keycode == 111) {
                    client.setScreen(new Menu(client));
                } else if (keycode == 62) { // restarts level on space pressed
                    client.setScreen(new GameScreen(client, Gdx.files.internal("core/assets/downloaded_levels/" + levelID + ".json")));
                }
                return super.keyDown(event, keycode);
            }
        });

        if (success) {
            // uploads users' time to server
            String bodystring = new Gson().toJson(body);
            client.postRequest("/level/" + levelID + "/time", bodystring).thenAccept(response -> {
                // gets top time
                client.getRequest("/level/" + levelID + "/leaderboard").thenAccept(stringHttpResponse -> {
                    // if success
                    if (stringHttpResponse.statusCode() == 200) {
                        // set best time to the data downloaded
                        LeaderboardEntry entry = gson.fromJson(stringHttpResponse.body(), LeaderboardEntry.class);
                        bestTimeString = entry.username + " : " + entry.time;
                    } else {
                        bestTimeString = "None Set";
                    }
                });
            });

            uploadNewWeapon();

            // todo give random weapon
        }
    }

    public void uploadNewWeapon() {
        // post /weapon

        WeaponData wep = new WeaponData();

        ArrayList<String> weaponTextures = new ArrayList<>();
        File folder = new File("core/assets/raw/game/weapons");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                weaponTextures.add(file.getName());
            }
        }
        Random rand = new Random();
        int index = rand.nextInt(weaponTextures.size());
        String texture = weaponTextures.get(index);
        wep.textureName = "game/weapons/" + texture.substring(0, texture.length() - 4);

        wep.damage = rand.nextInt(50) + 1;
        wep.range = rand.nextInt(10) + 1;
        wep.knockback = rand.nextInt(10) + 1;
        wep.attackspeed = rand.nextInt(4000) + 200;
        wep.isMelee = true;

        client.postRequest("/inventory/weapon", gson.toJson(wep));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        if (success) {
            font.draw(batch, "Congratulations!\nLevel Completed in: " + time + "s", -30, 30, 100, Align.center, false);
            font.draw(batch, "Best Time: " + bestTimeString, -30, -40, 100, Align.center, false);
        } else {
            font.draw(batch, "You failed!", -30, 30, 100, Align.center, false);
        }
        font.draw(batch, "Press ESC to return to menu\nOr press SPACEBAR to restart", -30, -150, 100, Align.center, false);

        batch.end();
    }

    /**
     * Data class for http request
     */
    static class LeaderboardEntry {
        public String levelID;
        public String username;
        public long time;
    }

    /**
     * Data class for http request
     */
    static class SetTimeBody {
        String levelID;
        int time;
    }
}
