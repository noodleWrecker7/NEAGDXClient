package dev.adamhodgkinson;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import dev.adamhodgkinson.screens.Loading;
import dev.adamhodgkinson.screens.Login;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class GDXClient extends Game {
    public AssetManager assets;

    public OrthographicCamera worldCam;
    public OrthographicCamera uiCam;

    public final String SERVER_ADDRESS = "http://localhost:26500";
    HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .cookieHandler(new CookieManager())
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public PlayerData playerData;

//    public BitmapFont font;

    public float zoom;

    public boolean debug = false;

    public GDXClient(float zoom) {
        super();
        this.zoom = zoom;
    }

    @Override
    public void create() {
        // Creates camera and sets correct sizing and positioning
        worldCam = new OrthographicCamera();
        uiCam = new OrthographicCamera();
        int pixelsPerUnit = 32;
        // camera for rendering objects in a world that move
        // could probably be moved to game screen
        worldCam.setToOrtho(false, ((float) Gdx.graphics.getWidth() / pixelsPerUnit) / zoom, ((float) Gdx.graphics.getHeight() / pixelsPerUnit) / zoom); // ortho camera, 1 unit is one tile
        worldCam.position.set(5, 3, 0);
        worldCam.update();

        // ui camera, static position in window
        uiCam.setToOrtho(false, (Gdx.graphics.getWidth()) / zoom, (Gdx.graphics.getHeight()) / zoom);
        uiCam.position.set(0, 0, 0);
        uiCam.update();

        assets = new AssetManager(); // manages loading of multiple assets asynchronously

        loadAssets();

        // todo load playe data from server
        playerData = new PlayerData();

        // Starts the loading screen while the assets are loading
        setScreen(new Loading(this, new Login(this))); // changes screen to the loading screen
    }

    public void loadAssets() {
// todo important, remember to mention in design that there used to be multiple atlases

        assets.load(Gdx.files.internal("packed/pack.atlas").path(), TextureAtlas.class);


        // set the loaders for the generator and the fonts themselves
        // freetype font gen takes a vectorised ttf format and creates a bitmap font to be used by the renderer

        // the resolver loads the files from disk
        FileHandleResolver resolver = new InternalFileHandleResolver();
        // sets a the loader for the freetypefontgenerator, the loader comes bundled with the package
        assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        // sets the loader for font files, will pick up any .ttf files loaded
        assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        loadFont("fonts/NotoSansMono-Regular.ttf", 10, "noto10");
        loadFont("fonts/NotoSansMono-Regular.ttf", 15, "noto15");
        loadFont("fonts/NotoSansMono-Regular.ttf", 25, "noto25");
    }

    /**
     * Loads a font using freetypefontgen
     *
     * @param fileName   - the name/location of the .ttf file on disk
     * @param bitmapSize - the size in pixels of the bitmap font to be generated
     * @param identifier - the id to get the font from asset manager, will be suffixed with .ttf
     */
    public void loadFont(String fileName, int bitmapSize, String identifier) {
        // params are used to store data about the font being loaded
        FreetypeFontLoader.FreeTypeFontLoaderParameter size1Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        // the file actually loaded from disk
        size1Params.fontFileName = fileName;
        // bitmap size of the font to generate
        size1Params.fontParameters.size = bitmapSize;

        size1Params.fontParameters.genMipMaps = true;
        size1Params.fontParameters.mono = true;
        // loads the file, the name given here is only used as an arbitrary identifer - size1Params contains the actual disk location
        // the .ttf extension must be there to trigger the correct loader to be used
        assets.load(identifier + ".ttf", BitmapFont.class, size1Params);
    }


    @Override
    public void dispose() {
        assets.dispose();
    }

    public CompletableFuture<HttpResponse<String>> postRequest(String endpoint, String json) {
        HttpRequest r = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_ADDRESS + endpoint))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();


        return httpClient.sendAsync(r, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> getRequest(String endpoint) {
        HttpRequest r = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_ADDRESS + endpoint))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return httpClient.sendAsync(r, HttpResponse.BodyHandlers.ofString());
    }

}

