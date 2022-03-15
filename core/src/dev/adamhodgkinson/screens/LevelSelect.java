package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.gson.Gson;
import dev.adamhodgkinson.GDXClient;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;

public class LevelSelect extends ScreenAdapter {

    static final int PAGE_SIZE = 10;
    static int verticalOffset = 180;
    GDXClient client;
    Stage stage;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    BitmapFont font;
    VerticalGroup vg;
    HorizontalGroup hg;
    Skin defaultUISkin;
    ArrayList<LevelEntry> entries;
    TextField searchBox;
    int pageNo = 1;
    Gson g = new Gson();


    public LevelSelect(GDXClient app) {
        client = app;
        entries = new ArrayList<>();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(client.uiCam.combined);

        stage = new Stage(); // to hold the buttons
        stage.getCamera().position.set(0, 0, 0);
        stage.getCamera().viewportWidth = client.uiCam.viewportWidth;
        stage.getCamera().viewportHeight = client.uiCam.viewportHeight;
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                System.out.println(keycode);
                if (keycode == 111) {
                    client.setScreen(new Menu(client));
                }
                return super.keyDown(event, keycode);
            }
        });

        defaultUISkin = client.assets.get("skins/uiskin.json");

        searchBox = new TextField("", defaultUISkin);
        searchBox.setPosition(-searchBox.getWidth(), stage.getHeight() / 2 - searchBox.getHeight() - 5);
        TextButton searchButton = new TextButton("Search", defaultUISkin);
        searchButton.setPosition(searchButton.getWidth(), stage.getHeight() / 2 - searchButton.getHeight() - 5);
        searchButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                refreshDataFromServer();
            }
        });
        stage.addActor(searchButton);
        stage.addActor(searchBox);

        Label pageNumLabel = new Label(Integer.toString(pageNo), defaultUISkin);
        TextButton nextPageButton = new TextButton("Next", defaultUISkin);
        TextButton prevPageButton = new TextButton("Prev", defaultUISkin);
        pageNumLabel.setPosition(395 - pageNumLabel.getWidth() / 2, -250);
        nextPageButton.setPosition(440 - nextPageButton.getWidth() / 2, -250);
        prevPageButton.setPosition(360 - prevPageButton.getWidth() / 2, -250);

        nextPageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // must mean no more pages
                if (entries.size() == 0) {
                    return;
                }
                pageNumLabel.setText(++pageNo);
                refreshDataFromServer();
            }
        });
        prevPageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (pageNo == 1) {
                    return;
                }
                pageNumLabel.setText(--pageNo);
                refreshDataFromServer();
            }
        });

        TextButton refreshButton = new TextButton("Refresh", defaultUISkin);
        refreshButton.setPosition(440 - refreshButton.getWidth() / 2, 230 - refreshButton.getHeight() / 2);
        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                refreshDataFromServer();
            }
        });


        stage.addActor(pageNumLabel);
        stage.addActor(nextPageButton);
        stage.addActor(prevPageButton);
        stage.addActor(refreshButton);


//        for (int i = 0; i < 10; i++) {
//            LevelEntry entry = new LevelEntry("Level1", "author", "date", defaultUISkin);
//            entries.add(entry);
//        }
        refreshDataFromServer();
        vg = new VerticalGroup();

        updateEntries();

        font = client.assets.get("noto25.ttf");
    }

    /**
     * Returns array of level files currently downloaded
     */
    public FileHandle[] getDownloadedLevelIds() {
        FileHandle dir = Gdx.files.internal("core/assets/downloaded_levels");
        return dir.list(".json");
    }

    public void refreshDataFromServer() {
        FileHandle[] ids = getDownloadedLevelIds();


        client.getRequest("/level/list?page_size=" + PAGE_SIZE + "&page_num=" + pageNo + "&search=" + URLEncoder.encode(searchBox.getText(), StandardCharsets.UTF_8)).thenAccept(stringHttpResponse -> {
            switch (stringHttpResponse.statusCode()) {
                case 200:
                    LevelMeta[] levels = g.fromJson(stringHttpResponse.body(), LevelMeta[].class);
                    entries.clear();
                    for (int i = 0; i < levels.length; i++) {
                        LevelMeta m = levels[i];
                        LevelEntry entry = new LevelEntry(m.levelID, m.title, m.creatorID, m.dateCreated.toString(), defaultUISkin, client);
                        for (int j = 0; j < ids.length; j++) {
                            String idname = ids[j].name();
                            String mId = m.levelID + ".json";
                            if (idname.equals(mId)) {
                                entry.isDownloaded = true;
                                entry.updateButtons();
                                break;
                            }
                        }
                        entries.add(entry);

                    }
                    updateEntries();
                    break;
                default:
                    System.out.println("Possible error refreshing level list");
                    System.out.println(stringHttpResponse.body());
            }
        });
    }

    public void updateEntries() {
        vg.remove();
        vg = new VerticalGroup();

        for (int i = 0; i < entries.size(); i++) {
            vg.addActor(entries.get(i).hg);
        }
        vg.setPosition(0, verticalOffset);

        vg.space(5);
        stage.addActor(vg);
        vg.layout();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act();

        ScreenUtils.clear(0, 0, 0, 1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(.3f, .3f, .3f, 1);
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).draw(shapeRenderer);
        }
        shapeRenderer.end();

        stage.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    static class LevelMeta {
        public String levelID;
        public String creatorID;
        public Date dateCreated;
        public String title;
    }

    static class LevelEntry {
        public HorizontalGroup hg;
        public String levelId;
        GDXClient client;
        Gson g = new Gson();
        boolean isDownloaded = false;
        TextButton downloadButton;
        TextButton playButton;


        public LevelEntry(String id, String name, String author, String date, Skin defaultUISkin, GDXClient client) {
            levelId = id;
            this.client = client;
            hg = new HorizontalGroup();
            hg.space(200);
            hg.align(Align.left);

            HorizontalGroup innerLeft = new HorizontalGroup();
            HorizontalGroup innerRight = new HorizontalGroup();
            innerLeft.space(20);
            innerRight.space(20);

            innerLeft.addActor(new Label(name, defaultUISkin));
            innerLeft.addActor(new Label(author, defaultUISkin));
            innerLeft.addActor(new Label(date, defaultUISkin));
            downloadButton = new TextButton("Download", defaultUISkin);
            playButton = new TextButton("Play", defaultUISkin);
            playButton.setDisabled(true);

            innerRight.addActor(downloadButton);
            innerRight.addActor(playButton);
            downloadButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handleDownloadClicked();
                }
            });
            playButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handlePlayClicked();
                }
            });

            hg.addActor(innerLeft);
            hg.addActor(innerRight);
            hg.pad(5);
            innerLeft.center();
            innerRight.center();
        }

        public void updateButtons() {
            playButton.setDisabled(!isDownloaded);
            downloadButton.setDisabled(isDownloaded);
        }

        public void handlePlayClicked() {
            if (!isDownloaded) return;

            client.setScreen(new GameScreen(client, Gdx.files.internal("core/assets/downloaded_levels/" + this.levelId + ".json")));
        }

        public void handleDownloadClicked() {

            client.getRequest("/level/" + this.levelId).thenAccept(stringHttpResponse -> {
                if (stringHttpResponse.statusCode() != 200) {
                    System.out.println(stringHttpResponse.statusCode());
                    System.out.println(stringHttpResponse.body());
                    return;
                }
                try {
                    String pathString = Gdx.files.internal("core/assets/downloaded_levels/").path() + "/" + this.levelId + ".json";
                    System.out.println(pathString);
                    BufferedWriter writer = new BufferedWriter(new FileWriter(pathString, false));
                    writer.write(stringHttpResponse.body());
                    writer.close();
                    isDownloaded = true;
                    updateButtons();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            });

        }


        public void draw(ShapeRenderer shapeRenderer) {
            shapeRenderer.rect(hg.getX(), hg.getY() + verticalOffset, hg.getWidth(), hg.getHeight());
        }


    }
}
