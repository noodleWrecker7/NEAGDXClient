package dev.adamhodgkinson.game.enemies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import dev.adamhodgkinson.game.GameSprite;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Enemy extends GameSprite {
    float maxHealth;
    Vector2 spawnPos; // where the enemy started
    int moveRange; // how far the enemy can move from its spawn point
    static final float healthBarHeight = 0.2f;


    public static Enemy createFromNode(Node node, AssetManager assets, World world) {
        NamedNodeMap attr = node.getAttributes();
        String name = attr.getNamedItem("name").getNodeValue();
        float _health = Float.parseFloat(attr.getNamedItem("health").getNodeValue());

        int x = Integer.parseInt(attr.getNamedItem("x").getNodeValue());
        int y = Integer.parseInt(attr.getNamedItem("y").getNodeValue());
        Enemy e = new Enemy(assets, name, world, x, y);
        e.health = _health;
        e.maxHealth = _health;
        return e;
    }

    //Weapon weapon;
    public Enemy(AssetManager assets, String textureName, World world, int x, int y) {
        super(world, x, y, textureName, assets);


    }

    public void attack() {

    }

    public void die() {
    }

    public void update(float dt) {
        super.update(dt);

    }


    public void renderHealth(ShapeRenderer renderer) {
        renderer.setColor(1, 0, 0, 1);
        renderer.rect(this.getPos().x - width / 2f, this.getPos().y + height / 2.f + healthBarHeight, width, healthBarHeight);

        renderer.setColor(0, 1, 0, 1);
        float greenWidth = width * (this.health / this.maxHealth);
        renderer.rect(this.getPos().x - width / 2f, this.getPos().y + height / 2.f + healthBarHeight, greenWidth, healthBarHeight);
    }


    /**
     * Sets the target to the supplied gamesprite
     *
     * @param target - the object to target
     * @return boolean representing wether the action succeeded
     */
    public boolean setTarget(GameSprite target) {
        return false;
    }

    @Override
    public void beginCollide(Fixture fixture) {

    }

    @Override
    public void endCollide(Fixture fixture) {

    }


}
