package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import dev.adamhodgkinson.GDXClient;
import dev.adamhodgkinson.WeaponData;

import java.util.ArrayList;
import java.util.Arrays;

public class Inventory extends ScreenAdapter {
    GDXClient client;
    Stage stage;
    BitmapFont font;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    Skin defaultUISkin;
    int pageNo = 1;
    Button[] buttons;
    int cols = 7;
    int rows = 5;

    public Inventory(GDXClient app) {
        client = app;

        stage = new Stage(); // to hold the buttons
        stage.getCamera().position.set(0, 0, 0);
        stage.getCamera().viewportWidth = client.uiCam.viewportWidth;
        stage.getCamera().viewportHeight = client.uiCam.viewportHeight;
        // returns to main menu when esc is pressed
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


        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        batch.setProjectionMatrix(client.uiCam.combined);
        shapeRenderer.setProjectionMatrix(client.uiCam.combined);


        client.playerData.retrieveWeaponData();
        createPagination();
        createInvisButtons();
        createPreviewSection();

        setInvImages();


//        stage.addActor(table);
        font = client.assets.get("noto10.ttf");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    float imagePreviewHeight; // height of smaller image previews
    float previewImageHeight = 200; // height of larger preview image
    Image previewImage;

    Label damageValue;
    Label rangeValue;
    Label speedValue;
    Label knockbackValue;

    TextButton equipButton;
    TextButton deleteButton;

    /**
     * Creates ui for the preview section of the currently selected weapon
     */
    public void createPreviewSection() {
        // damage range speed knockback
        Label damageLabel = new Label("Damage: ", defaultUISkin);
        Label rangeLabel = new Label("Range: ", defaultUISkin);
        Label speedLabel = new Label("Speed: ", defaultUISkin);
        Label knockbackLabel = new Label("Knockback: ", defaultUISkin);

        damageLabel.setPosition(-client.uiCam.viewportWidth / 2 + client.uiCam.viewportWidth * .03f, -80);
        rangeLabel.setPosition(-client.uiCam.viewportWidth / 2 + client.uiCam.viewportWidth * .03f, -100);
        speedLabel.setPosition(-client.uiCam.viewportWidth / 2 + client.uiCam.viewportWidth * .03f, -120);
        knockbackLabel.setPosition(-client.uiCam.viewportWidth / 2 + client.uiCam.viewportWidth * .03f, -140);

        damageValue = new Label("1", defaultUISkin);
        rangeValue = new Label("2", defaultUISkin);
        speedValue = new Label("3", defaultUISkin);
        knockbackValue = new Label("4", defaultUISkin);

        damageValue.setPosition(-client.uiCam.viewportWidth / 2 + client.uiCam.viewportWidth * .15f, -80);
        rangeValue.setPosition(-client.uiCam.viewportWidth / 2 + client.uiCam.viewportWidth * .15f, -100);
        speedValue.setPosition(-client.uiCam.viewportWidth / 2 + client.uiCam.viewportWidth * .15f, -120);
        knockbackValue.setPosition(-client.uiCam.viewportWidth / 2 + client.uiCam.viewportWidth * .15f, -140);

        equipButton = new TextButton("Equip", defaultUISkin);
        deleteButton = new TextButton("Delete", defaultUISkin);

        equipButton.setPosition(-client.uiCam.viewportWidth / 2 + client.uiCam.viewportWidth * .13f, -190);
        deleteButton.setPosition(-client.uiCam.viewportWidth / 2 + client.uiCam.viewportWidth * .03f, -190);

        stage.addActor(equipButton);
        stage.addActor(deleteButton);

        stage.addActor(damageLabel);
        stage.addActor(rangeLabel);
        stage.addActor(speedLabel);
        stage.addActor(knockbackLabel);
        stage.addActor(damageValue);
        stage.addActor(rangeValue);
        stage.addActor(speedValue);
        stage.addActor(knockbackValue);

        equipButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String selectedWep = client.playerData.inventory.storedweapons[selectedWeapon].weaponID;
                client.postRequest("/inventory/weapon/equipped", selectedWep).thenAccept(stringHttpResponse -> {
                    System.out.println(stringHttpResponse.body());
                    client.playerData.retrieveWeaponData();
                    setInvImages();
                });
            }
        });

        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (client.playerData.inventory.storedweapons[selectedWeapon].weaponID.equals(client.playerData.getEquippedWeaponData().weaponID)) {
                    return;
                }
                client.deleteRequest("weapon/" + client.playerData.inventory.storedweapons[selectedWeapon].weaponID).thenAccept(stringHttpResponse -> {
                    if (stringHttpResponse.statusCode() != 200) {
                        System.out.println(stringHttpResponse.body());
                    }
                });
            }
        });
    }

    /**
     * Creates pagination ui to switch pages and indicate the current page
     */
    public void createPagination() {
        Label pageNumLabel = new Label(Integer.toString(pageNo), defaultUISkin);
        TextButton nextPageButton = new TextButton("Next", defaultUISkin);
        TextButton prevPageButton = new TextButton("Prev", defaultUISkin);
        pageNumLabel.setPosition(395 - pageNumLabel.getWidth() / 2, -250);
        nextPageButton.setPosition(440 - nextPageButton.getWidth() / 2, -250);
        prevPageButton.setPosition(360 - prevPageButton.getWidth() / 2, -250);

        nextPageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // if too nothing on next page
                if (pageNo * cols * rows >= client.playerData.inventory.storedweapons.length) {
                    return;
                }
                pageNumLabel.setText(++pageNo);
            }
        });
        prevPageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (pageNo == 1) {
                    return;
                }
                pageNumLabel.setText(--pageNo);
            }
        });

        TextButton refreshButton = new TextButton("Refresh", defaultUISkin);
        refreshButton.setPosition(440 - refreshButton.getWidth() / 2, 230 - refreshButton.getHeight() / 2);
        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                client.playerData.retrieveWeaponData();
                setInvImages();
            }
        });


        stage.addActor(pageNumLabel);
        stage.addActor(nextPageButton);
        stage.addActor(prevPageButton);
        stage.addActor(refreshButton);
    }

    /**
     * Required to mainatain refernces to images so they can be deleted when the page is refreshed
     */
    ArrayList<Image> images = new ArrayList<>();

    /**
     * Creates the inventory images for the items in the inventory, also wipes previous images to act as a reset
     */
    public void setInvImages() {
        WeaponData[] weps = client.playerData.inventory.storedweapons;
        weps = Arrays.copyOfRange(weps, (pageNo - 1) * cols * rows, pageNo * cols * rows);
        TextureAtlas atlas = client.assets.get("packed/pack.atlas");

        for (Image i : images) {
            i.remove();
        }

        for (int i = 0; i < weps.length; i++) {
            if (weps[i] == null) {
                continue;
            }
            TextureRegion region = atlas.findRegion(weps[i].textureName);
            Image img = new Image(region);
            img.setHeight(imagePreviewHeight);
            img.setWidth(region.getRegionWidth() * imagePreviewHeight / region.getRegionHeight());
            img.setPosition(buttons[i].getX() + buttons[i].getWidth() / 2 - img.getWidth() / 2,
                    buttons[i].getY() + buttons[i].getHeight() / 2 - img.getHeight() / 2);
            img.setTouchable(Touchable.disabled);
            images.add(img);
            stage.addActor(img);
        }
    }

    /**
     * Creates the invisible buttons, they go behind the images of the weapons to act as listeners for clicks
     */
    public void createInvisButtons() {

        int leftMargin = 220;
        int padding = 20;
        float workableSpace = client.uiCam.viewportWidth - leftMargin - (padding * 2);
        float spacePerCol = workableSpace / cols;
        float gapPerCol = 25;
        float colWidth = spacePerCol - gapPerCol;

        float bottomMargin = 60;
        float workableHeight = client.uiCam.viewportHeight - bottomMargin - (padding * 2);
        float spacePerRow = workableHeight / rows;
        float gapPerRow = gapPerCol;
        float rowHeight = spacePerRow - gapPerRow;
        imagePreviewHeight = rowHeight * .8f;

        buttons = new Button[cols * rows];

        for (int i = 0; i < cols; i++) {
            float x = spacePerCol * i + leftMargin + padding - client.uiCam.viewportWidth / 2;
            for (int j = 0; j < rows; j++) {
                float y = spacePerRow * j + bottomMargin + padding - client.uiCam.viewportHeight / 2;
//                shapeRenderer.rect(x, y, colWidth, rowHeight);
                Button button = new Button(defaultUISkin);
                button.setColor(1, 1, 1, .5f);
                button.setWidth(colWidth);
                button.setHeight(rowHeight);
                button.setPosition(x, y);
                button.setVisible(true);
                button.setDisabled(false);

                int count = (rows - j - 1) * cols + i;
                buttons[count] = button;
                button.setUserObject(count);
//                font.draw(batch, Integer.toString(count), x, y);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        System.out.println("clicked" + event.getTarget().getUserObject());
                        selectItem((int) event.getTarget().getUserObject());
                    }
                });
                stage.addActor(button);
            }
        }
    }

    int selectedWeapon;

    /**
     * Called by click listener when an inventory item is clicked
     */
    public void selectItem(int index) {

        System.out.println("selected " + index);
        if (index < 0 || index >= buttons.length || index >= client.playerData.inventory.storedweapons.length) {
            System.out.println("Error trying to get weapon index: " + index);
            return;
        }

        selectedWeapon = index;
        WeaponData wep = client.playerData.inventory.storedweapons[index];
        if (previewImage != null) previewImage.remove();

        TextureRegion region = ((TextureAtlas) client.assets.get("packed/pack.atlas")).findRegion(wep.textureName);
        previewImage = new Image(region);
        previewImage.setHeight(previewImageHeight);
        previewImage.setWidth(region.getRegionWidth() * previewImageHeight / region.getRegionHeight());
        previewImage.setPosition(-client.uiCam.viewportWidth / 2 + previewImage.getWidth() / 2 + 20, client.uiCam.viewportHeight / 2 - previewImage.getHeight() - 60);

        damageValue.setText(Float.toString(wep.damage));
        rangeValue.setText(Float.toString(wep.range));
        speedValue.setText(Float.toString(wep.attackspeed));
        knockbackValue.setText(Float.toString(wep.knockback));

        // if weapon is equipped disables the buttons
        equipButton.setDisabled(wep.weaponID.equals(client.playerData.getEquippedWeaponData().weaponID));
        deleteButton.setDisabled(wep.weaponID.equals(client.playerData.getEquippedWeaponData().weaponID));

        stage.addActor(previewImage);

    }


    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act();
        ScreenUtils.clear(0, 0, 0, 1);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);

        shapeRenderer.begin();

        shapeRenderer.rect(1 - client.uiCam.viewportWidth / 2, 1 - client.uiCam.viewportHeight / 2, client.uiCam.viewportWidth * .2f, client.uiCam.viewportHeight - 1);
        shapeRenderer.end();
//        batch.begin();

//        batch.end();

//        shapeRenderer.end();
        stage.draw();
    }

}
