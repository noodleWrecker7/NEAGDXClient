package dev.adamhodgkinson.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

abstract public class GameSprite extends Animated implements Physical {
    Vector2 movement = new Vector2();
    Body body;
    float speed;
    float jumpSpeed;
    boolean isRunning = false;
    protected float width;
    protected float height;
    Weapon weapon;
    protected float health;
    protected float maxHealth;
    boolean jumpAvailable = true;

    public void destroy(World world) {
        world.destroyBody(body);
        if (weapon != null) {
            weapon.destroy();
        }
    }

    public void takeDamage(float amt, float knockback, GameSprite from) {
        this.health -= amt;
        playAnimation("hit");
        // target(from)
        if (this.getPos().x <= from.getPos().x) {
            this.body.applyLinearImpulse(new Vector2(-knockback, knockback), this.body.getPosition(), true);
        } else if (this.getPos().x >= from.getPos().x) {
            this.body.applyLinearImpulse(new Vector2(knockback, knockback), this.body.getPosition(), true);
        }
    }

    public GameSprite(World world, float x, float y, String textureName, AssetManager assets) {
        this(world, x, y, 5, 8f, 4f, 0, textureName, assets);
    }

    public GameSprite(World world, float x, float y, float density, float speed, float friction, float linearDamping,
                      String textureName, AssetManager assets) {
        this.speed = speed;
        jumpSpeed = speed * 2;
        final TextureAtlas atlas = assets.get("core/assets/packed/pack.atlas");
        final String prefix = "game/sprites/" + textureName;
        width = (float) atlas.findRegions(prefix + "_idle_anim").get(0).packedWidth / 16;
        height = (float) atlas.findRegions(prefix + "_idle_anim").get(0).packedHeight / 16;
        setDefaultAnims(assets, textureName);

        final BodyDef def = new BodyDef();
        def.position.set(x, y);
        def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(def);

        body.setFixedRotation(true);

        // main fixture
        final FixtureDef fix = new FixtureDef();
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2); // height/8 accounts for whitespace in texture
        fix.shape = shape;
        fix.density = density;
        fix.friction = friction;
        body.setLinearDamping(linearDamping);
        body.createFixture(fix);

        body.setUserData(this);

        this.health = this.maxHealth = 10;

    }

    public void jump() {
        jump(jumpSpeed);
    }

    public boolean jump(float _jumpSpeed) {
        if (_jumpSpeed > jumpSpeed) {
            return false;
        }
        body.setLinearVelocity(body.getLinearVelocity().x, _jumpSpeed);
        return true;
    }


    public void setDefaultAnims(AssetManager assets, String textureName) {
        final TextureAtlas atlas = assets.get("core/assets/packed/pack.atlas");
        final String prefix = "game/sprites/" + textureName;
        final Animation<TextureRegion> runAnimation = new Animation<TextureRegion>(.15f,
                atlas.findRegions(prefix + "_run_anim"), Animation.PlayMode.LOOP);
        final Animation<TextureRegion> hitAnimation = new Animation<TextureRegion>(.5f,
                atlas.findRegions(prefix + "_hit_anim"), Animation.PlayMode.NORMAL);
        final Animation<TextureRegion> idleAnimation = new Animation<TextureRegion>(.25f,
                atlas.findRegions(prefix + "_idle_anim"), Animation.PlayMode.LOOP);
        addAnimation("idle", idleAnimation);
        addAnimation("run", runAnimation);
        addAnimation("hit", hitAnimation);

    }

    public void draw(SpriteBatch batch) {
        final Vector2 pos = getPos();
        final TextureRegion frame = getFrame();

        if (this.weapon != null) {
            this.weapon.draw(batch, pos.x, pos.y);
//            batch.draw(this.weapon, pos.x, pos.y, width, this.weapon.getHeight() / 16.f);
        }

        // gets width each frame as it can change
        batch.draw(frame, pos.x - width / 2, pos.y - height / 2, frame.getRegionWidth() / 16.f,
                frame.getRegionHeight() / 16.f); /*
         * had to add coord offsets to account for being at center of object
         * + weird height/8 offset accounts for extra whitespace in texture
         */

    }

    public Vector2 getPos() {
        return body.getPosition();
    }

    public void updateFlippage() {
        if (movement.x < 0 && !isXFlipped) {
            isXFlipped = true;
            this.weapon.flipx();
        } else if (movement.x > 0 && isXFlipped) {
            isXFlipped = false;
            this.weapon.flipx();
        }
    }

    public void getHit() {
        playAnimation("hit");
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (currentAnimation.equals("hit") && getAnimation().isAnimationFinished(animTime)) {
            if (isRunning) {
                playAnimation("run");
            } else {
                playAnimation("idle");
            }
        }
//        body.setLinearVelocity(movement.x * speed, movement.y * speed);
        final Vector2 mov = body.getLinearVelocity();
        body.setLinearVelocity(movement.x != 0 ? movement.x * speed : mov.x,
                movement.y != 0 ? movement.y * speed : mov.y);
        if (!isRunning && (body.getLinearVelocity().x > 0.1 || body.getLinearVelocity().x < -0.1)) {
            isRunning = true;
            playAnimation("run");
        } else if (isRunning && body.getLinearVelocity().x < 0.2 && body.getLinearVelocity().x > -0.2) {
            isRunning = false;
            playAnimation("idle");
        }
        if (this.weapon != null) {
            this.weapon.update(dt);
        }

        if (this.health <= 0) {
            this.die();
        }
    }

    public void die() {
        System.out.println("dead");
    }


    public boolean isDead() {
        return health <= 0;
    }

    public void onGround() {
        jumpAvailable = true;
    }

    @Override
    public void beginCollide(Fixture fixture) {
        if (fixture.getBody().getUserData() instanceof TileGroup) {

        }
    }

    @Override
    public void endCollide(Fixture fixture) {

    }

}
