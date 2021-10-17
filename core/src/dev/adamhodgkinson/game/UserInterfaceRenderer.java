package dev.adamhodgkinson.game;

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
        renderDebugInfo();
        batch.end();
    }

    public void renderDebugInfo() {
        DecimalFormat df = new DecimalFormat("#.##");
        BitmapFont font = client.assets.get("noto10.ttf");
        font.draw(batch, "Debug=true", -client.uiCam.viewportWidth / 2 + 5, 15 - client.uiCam.viewportHeight / 2, 10, Align.left, false);
        font.draw(batch, "Player X/Y: ( " + df.format(game.player.getPos().x) + " , " + df.format(game.player.getPos().y) + " )\n"+
                "       dX/dY: ("+ df.format(game.player.body.getLinearVelocity().x)+","+df.format(game.player.body.getLinearVelocity().y)+")"
                , -client.uiCam.viewportWidth / 2 + 5, client.uiCam.viewportHeight / 2 - 15, 10, Align.left, false);
    }


}
