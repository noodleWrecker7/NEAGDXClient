package dev.adamhodgkinson.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import dev.adamhodgkinson.GDXClient;

import java.text.DecimalFormat;

/**
 * Class used to manager rendering of the user interfaces in the gameplay screen
 */
public class UserInterfaceRenderer {

    private final GDXClient client;
    private final Game game;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    DecimalFormat df;
    BitmapFont font;

    public UserInterfaceRenderer(GDXClient _client, Game _game) {
        client = _client;
        game = _game;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        batch.setProjectionMatrix(client.uiCam.combined);
        shapeRenderer.setProjectionMatrix(client.uiCam.combined);
        healthBarWidth = client.uiCam.viewportWidth * .75f;

        df = new DecimalFormat("#.##");
        font = client.assets.get("noto10.ttf");
    }

    float healthBarWidth;
    float healthBarHeight = 10;

    public void render() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 1);

        shapeRenderer.rect(-healthBarWidth / 2, -client.uiCam.viewportHeight / 2 + healthBarHeight / 2, healthBarWidth, healthBarHeight);

        shapeRenderer.setColor(0, 1, 0, 1);
        float greenWidth = healthBarWidth * (game.player.health / game.player.maxHealth);
        shapeRenderer.rect(-healthBarWidth / 2, -client.uiCam.viewportHeight / 2 + healthBarHeight / 2, greenWidth, healthBarHeight);
        shapeRenderer.end();
        batch.begin();
        renderInfo();

        batch.end();
    }

    public void renderInfo() {
        font.draw(batch, "Player X/Y: ( " + df.format(game.player.getPos().x) + " , " + df.format(game.player.getPos().y) + " )\n" +
                        "       Elapsed Time: " + (System.currentTimeMillis() - game.startTime) / 1000 + "s \n" +
                        "       Enemies: " + game.level.enemiesArray.size() + "\n" +
                        "       FPS: " + Gdx.graphics.getFramesPerSecond()
                , -client.uiCam.viewportWidth / 2, client.uiCam.viewportHeight / 2, 10, Align.left, false);
    }


}
