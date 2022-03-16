package dev.adamhodgkinson.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import dev.adamhodgkinson.GDXClient;

import java.text.DecimalFormat;

public class UserInterfaceRenderer {

    private final GDXClient client;
    private final Game game;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    public UserInterfaceRenderer(GDXClient _client, Game _game) {
        client = _client;
        game = _game;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        batch.setProjectionMatrix(client.uiCam.combined);
        shapeRenderer.setProjectionMatrix(client.uiCam.combined);
    }

    public void render() {
        batch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 1);
        float width = client.uiCam.viewportWidth * .8f;
        shapeRenderer.rect(-width / 2, -client.uiCam.viewportHeight / 2, width, 10);

        shapeRenderer.setColor(0, 1, 0, 1);
        float greenWidth = width * (game.player.health / game.player.maxHealth);
        shapeRenderer.rect(-width / 2, -client.uiCam.viewportHeight / 2, greenWidth, 10);
        if (client.debug) {
            renderDebugInfo();
        }
        shapeRenderer.end();
        batch.end();
    }

    public void renderDebugInfo() {
        DecimalFormat df = new DecimalFormat("#.##");
        BitmapFont font = client.assets.get("noto10.ttf");
        font.draw(batch, "Debug=true", -client.uiCam.viewportWidth / 2 + 5, 15 - client.uiCam.viewportHeight / 2, 10, Align.left, false);
        font.draw(batch, "Player X/Y: ( " + df.format(game.player.getPos().x) + " , " + df.format(game.player.getPos().y) + " )\n" +
                        "       dX/dY: (" + df.format(game.player.body.getLinearVelocity().x) + "," + df.format(game.player.body.getLinearVelocity().y) + ")\n" +
                        "       Enemies: " + game.level.enemiesArray.size() + "\n" +
                        "       PlayerWeaponFlip: " + game.player.weapon.rotationfFlip + "\n" +
                        "       FPS: " + Gdx.graphics.getFramesPerSecond()
                , -client.uiCam.viewportWidth / 2 + 5, client.uiCam.viewportHeight / 2 - 15, 10, Align.left, false);
    }


}
