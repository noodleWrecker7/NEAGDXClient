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
                        bestTimeString = entry.username + " - " + entry.time;
                    } else {
                        bestTimeString = "None Set";
                    }
                });
            });

            // todo give random weapon
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        if (success) {
            font.draw(batch, "Congratulations!\nLevel Completed in: " + time + "s", 0, 0, 100, Align.center, false);
            font.draw(batch, "Best Time: " + bestTimeString, 0, -80, 100, Align.center, false);
        } else {
            font.draw(batch, "You failed!", 0, 0, 100, Align.center, false);
        }
        font.draw(batch, "Press ESC to return to menu\nOr press SPACEBAR to restart", 0, -150, 100, Align.center, false);

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
