package dev.adamhodgkinson.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.google.gson.Gson;
import dev.adamhodgkinson.game.navigation.NavGraph;
import dev.adamhodgkinson.game.navigation.NavGraphBuilder;
import dev.adamhodgkinson.game.navigation.PathFinder;

import java.util.ArrayList;


public class Level {
    TileGroup tilesGroup;
    ArrayList<Enemy> enemiesArray = new ArrayList<>();

    Vector2 playerSpawnPos;
    NavGraph navGraph;
    int worldWidth;
    int worldHeight;
    Game game;

    String levelID;

    public TileGroup getTilesGroup() {
        return tilesGroup;
    }

    public Level(FileHandle file, World world, AssetManager assets, Game _game) {
        game = _game;
        try {
            initialize(file, world, assets);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed reading level file");
            System.exit(1);
        }
    }

    public NavGraph getNavGraph() {
        return navGraph;
    }

    public String getID() {
        return levelID;
    }

    /**
     * Performs update on any entities within the level
     */
    public void update(float dt) {
        // iterates through each enemy to update them
        for (int i = 0; i < enemiesArray.size(); i++) {
            enemiesArray.get(i).update(dt);
            if (enemiesArray.get(i).isDead()) {
                enemiesArray.get(i).destroy(game.world);
                enemiesArray.remove(i);
                i--;
            }
        }
    }

    /**
     * Ensures all objects needed for level are created and populated with data
     */
    public void initialize(FileHandle file, World world, AssetManager assets) {
        levelID = file.name().substring(0, file.name().length() - 5);
        Gson gson = new Gson();
        // takes level information from level file and parses it to an object
        LevelDataSchema levelData = gson.fromJson(file.reader(), LevelDataSchema.class);
        System.out.println("Level name: " + levelData.name);

        worldWidth = levelData.width;
        worldHeight = levelData.height;

        playerSpawnPos = new Vector2(levelData.playerSpawnPosX, levelData.playerSpawnPosY);

        tilesGroup = new TileGroup(world);
        TextureAtlas atlas = assets.get(Gdx.files.internal("packed/pack.atlas").path());

        // for each tile, check it is valid and add it to the world
        for (int i = 0; i < levelData.tiles.length; i++) {
            TileData data = levelData.tiles[i];
            if (data.x >= worldWidth || data.y >= worldHeight || data.x < 0 || data.y < 0) {
                System.out.println("Tile outside level bounds at ( " + data.x + " , " + data.y + " )");
                continue;
            }
            Tile t = new Tile(data.x, data.y, atlas.findRegion(data.texture));
            tilesGroup.addTile(t);
        }

        // formalises the formation of tiles and also simplifies the number of physical bodies to fewer polygons
        tilesGroup.build();

        // generates a navgraph object for the given tilemap
        NavGraphBuilder navGraphBuilder = new NavGraphBuilder(worldWidth, worldHeight + 2, tilesGroup, 10, 16, world.getGravity().y);
        navGraph = navGraphBuilder.generateNavGraph();
        // the pathfinder object is given to the enemy class as a static member that they can use
        Enemy.pathFinder = new PathFinder(navGraph);

        // adds all the enemies to enemiesArray
        if (levelData.enemies != null) {
            for (int i = 0; i < levelData.enemies.length; i++) {
                EnemyData data = levelData.enemies[i];
                Enemy e = new Enemy(atlas, data.texture, world, data.x, data.y);
                e.health = data.health;
                e.maxHealth = data.health;
                if (data.weapon != null) {
                    e.weapon = Weapon.createFromData(data.weapon, atlas, e);
                }
                enemiesArray.add(e);
            }
        }

        createWorldEdge(world);
    }

    /**
     * Creates the worlds edges for the entites to collide with
     */
    public void createWorldEdge(World world) {
        BodyDef worldEdgeDef = new BodyDef();
        worldEdgeDef.type = BodyDef.BodyType.StaticBody;
        worldEdgeDef.allowSleep = true;
        worldEdgeDef.position.x = 0;
        worldEdgeDef.position.y = 0;
        Body worldEdge = world.createBody(worldEdgeDef);
        worldEdge.setUserData(new Physical() {
            @Override
            public void beginCollide(Fixture fixture) {
                if (fixture.getBody().getUserData() instanceof GameSprite) {
                    ((GameSprite) fixture.getBody().getUserData()).die();
                }
            }

            @Override
            public void endCollide(Fixture fixture) {

            }
        });

        FixtureDef fixDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        fixDef.density = 1;
        shape.setAsBox(.1f, worldHeight / 2f, new Vector2(-.6f, worldHeight / 2f), 0);
        fixDef.shape = shape;
        worldEdge.createFixture(fixDef);
        shape.setAsBox(.1f, worldHeight / 2f, new Vector2(worldWidth + .6f, worldHeight / 2f), 0);
        fixDef.shape = shape;
        worldEdge.createFixture(fixDef);

        shape.setAsBox(worldWidth / 2f, .1f, new Vector2(worldWidth / 2f, -.6f), 0);
        fixDef.shape = shape;
        worldEdge.createFixture(fixDef);
        shape.setAsBox(worldWidth / 2f, .1f, new Vector2(worldWidth / 2f, worldHeight + .6f), 0);
        fixDef.shape = shape;
        worldEdge.createFixture(fixDef);
    }

    public ArrayList<Enemy> getEnemiesArray() {
        return enemiesArray;
    }
}

