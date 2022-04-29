package dev.adamhodgkinson.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

abstract public class GameSprite extends Animated implements Physical {
    public static final float MAX_PHYSICAL_WIDTH = .90f;
    public static final float MAX_PHYSICAL_HEIGHT = 1.90f;

    public static final float LINEAR_DAMPING = 0;

    Vector2 movement = new Vector2();
    protected Body body;
    protected float speed;
    float jumpSpeed;
    boolean isRunning = false;
    protected float width;
    protected float height;
    Weapon weapon;
    protected float health;
    protected float maxHealth;

    protected int maxJumps = 1, jumpsAvailable = 1;

    public void destroy(World world) {
        world.destroyBody(body);
        if (weapon != null) {
            weapon.destroy();
        }
    }

    public GameSprite(World world, float x, float y, float density, float speed, float friction,
                      String textureName, TextureAtlas atlas) {
        this.health = this.maxHealth = 10;
        this.speed = speed;
        this.jumpSpeed = speed * 2;

        try {
            // sets dimensions bases on textures
            width = (float) atlas.findRegions(textureName + "_idle_anim").get(0).packedWidth / 16;
            height = (float) atlas.findRegions(textureName + "_idle_anim").get(0).packedHeight / 16;
            // sets animations
            setDefaultAnims(atlas, textureName);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Could not find texture for GameSprite");
            return;
        }

        // setup physics definitions
        final BodyDef def = new BodyDef();
        def.position.set(x, y + 0.5f);
        def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(def);
        body.setFixedRotation(true);

        // main fixture for the entity
        final FixtureDef fix = new FixtureDef();
        final PolygonShape shape = new PolygonShape();
        fix.restitution = .1f;
        if (width > MAX_PHYSICAL_WIDTH) {
            width = MAX_PHYSICAL_WIDTH;
        }
        if (height > MAX_PHYSICAL_HEIGHT) {
            height = MAX_PHYSICAL_HEIGHT;
        }
        shape.setAsBox(width / 2, height / 2);
        fix.shape = shape;
        fix.density = density;
        fix.friction = friction;
        body.setLinearDamping(LINEAR_DAMPING);

        // creates a filter for the body which allows enemies to pass through each other
        Filter filter = new Filter();
        // player = 1, nemy 2, tile 4
        if (this instanceof Player) {
            filter.categoryBits = 1;
            filter.maskBits = 7;
        } else {
            filter.categoryBits = 2;
            filter.maskBits = 5;
        }
        body.createFixture(fix).setFilterData(filter);
        body.setUserData(this);
    }

    public GameSprite(World world, float x, float y, String textureName, TextureAtlas atlas) {
        this(world, x, y, 5, 4f, .5f, textureName, atlas);
    }

    /**
     * Called by another gamesprite to damage this object, decrements health by amt, and handles knockback application
     */
    public void takeDamage(float amt, float knockback, GameSprite from) {
        this.health -= amt;
        playAnimation("hit");

        if (this.getPos().x <= from.getPos().x) {
            this.body.applyLinearImpulse(new Vector2(-knockback, knockback), this.body.getPosition(), true);
        } else if (this.getPos().x >= from.getPos().x) {
            this.body.applyLinearImpulse(new Vector2(knockback, knockback), this.body.getPosition(), true);
        }
    }

    public float getHealth() {
        return health;
    }

    public void jump() {
        jump(jumpSpeed);
    }

    public boolean jump(float _jumpSpeed) {
        if (jumpsAvailable <= 0) {
            return false;
        }
        body.setLinearVelocity(body.getLinearVelocity().x, _jumpSpeed);
        body.setTransform(body.getPosition().x, body.getPosition().y + .1f, 0);
        jumpsAvailable--;
        return true;
    }

    /**
     * Loads animations from texture atlas, and adds them using the Animated interface
     */
    public void setDefaultAnims(TextureAtlas atlas, String textureName) {
        final Animation<TextureRegion> runAnimation = new Animation<TextureRegion>(.15f,
                atlas.findRegions(textureName + "_run_anim"), Animation.PlayMode.LOOP);
        final Animation<TextureRegion> hitAnimation = new Animation<TextureRegion>(.5f,
                atlas.findRegions(textureName + "_hit_anim"), Animation.PlayMode.NORMAL);
        final Animation<TextureRegion> idleAnimation = new Animation<TextureRegion>(.25f,
                atlas.findRegions(textureName + "_idle_anim"), Animation.PlayMode.LOOP);
        addAnimation("idle", idleAnimation);
        addAnimation("run", runAnimation);
        addAnimation("hit", hitAnimation);

    }

    /**
     * Renders textures to screen using specified batch renderer
     */
    public void draw(SpriteBatch batch) {
        final Vector2 pos = getPos();
        final TextureRegion frame = getFrame();

        if (this.weapon != null) {
            this.weapon.draw(batch, pos.x, pos.y);
        }

        // gets width each frame as it can change
        float width = frame.getRegionWidth() / 16.f;
        float height = frame.getRegionHeight() / 16.f;

        batch.draw(frame, pos.x - width / 2, pos.y - height / 2, width, height); // had to add coord offsets to account for being at center of object
    }

    public Vector2 getPos() {
        return body.getPosition();
    }

    /**
     * Sets which direction the textures are flipped to based on the current movement of the entity
     */
    public void updateFlippage() {
        if (movement.x < 0 && !isXFlipped) {
            isXFlipped = true;
            if (weapon != null) {
                this.weapon.flipx();
            }
        } else if (movement.x > 0 && isXFlipped) {
            isXFlipped = false;
            if (weapon != null) {
                this.weapon.flipx();
            }
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        // manages current animation state after being hit
        if (currentAnimation.equals("hit") && getAnimation().isAnimationFinished(animTime)) {
            if (isRunning) {
                playAnimation("run");
            } else {
                playAnimation("idle");
            }
        }

        final Vector2 mov = body.getLinearVelocity();
        /* for each direction: if the player is being moved by key being held then the speed is set to the speed of the sprite,
         * otherwise it is left unchanged so that it can decay naturally in the physics engine */
        body.setLinearVelocity(movement.x != 0 ? movement.x * speed : mov.x, movement.y != 0 ? movement.y * speed : mov.y);

        // manages current animation state between running and idle
        if (!isRunning && (body.getLinearVelocity().x > 0.1 || body.getLinearVelocity().x < -0.1)) {
            isRunning = true;
            playAnimation("run");
        } else if (isRunning && body.getLinearVelocity().x < 0.2 && body.getLinearVelocity().x > -0.2) {
            isRunning = false;
            playAnimation("idle");
        }
        // updates weapon only if it exists
        if (this.weapon != null) {
            this.weapon.update(dt);
        }

        this.updateFlippage();
    }

    /**
     * Removes all health
     */
    public void die() {
        this.health = 0;
    }

    /**
     * Returns true if all health has been depleted
     */
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * Called when entity touches ground, this refreshes the number of jumps available to the entity
     */
    public void onGround() {
        jumpsAvailable = maxJumps;
    }

    @Override
    public void beginCollide(Fixture fixture) {
        // if collides with tile or entity, jump is refreshed
        if (fixture.getBody().getUserData() instanceof TileGroup || fixture.getBody().getUserData() instanceof GameSprite) {
            onGround();
        } else if (fixture.getBody().getUserData().equals("worldedge")) {
            this.die();
        }
    }

    @Override
    public void endCollide(Fixture fixture) {
    }
}
