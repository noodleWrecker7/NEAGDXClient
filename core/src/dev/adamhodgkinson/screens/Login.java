package dev.adamhodgkinson.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.gson.Gson;
import dev.adamhodgkinson.GDXClient;

import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class Login extends ScreenAdapter {

    GDXClient client;
    Stage stage;

    TextField usernameBox;
    TextField passwordBox;
    Label errorLabel;


    public Login(GDXClient client) {
        this.client = client;

        stage = new Stage();
        stage.getCamera().position.set(0, 0, 0); // centers cam
        stage.getCamera().viewportWidth = client.uiCam.viewportWidth;
        stage.getCamera().viewportHeight = client.uiCam.viewportHeight;

        Skin defaultUISkin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        Label usernameLabel = new Label("Username:", defaultUISkin);
        Label passwordLabel = new Label("Password:", defaultUISkin);
        usernameBox = new TextField("", defaultUISkin);
        passwordBox = new TextField("", defaultUISkin);
        TextButton loginButton = new TextButton("Login", defaultUISkin);
        TextButton signupButton = new TextButton("Signup", defaultUISkin);

        int buttonHeight = -80;
        loginButton.setPosition(-30 - loginButton.getWidth() / 2, buttonHeight);
        signupButton.setPosition(30 - signupButton.getWidth() / 2, buttonHeight);

        usernameBox.setPosition(-usernameBox.getWidth() / 2, usernameBox.getHeight());
        passwordBox.setPosition(-passwordBox.getWidth() / 2, -passwordBox.getHeight());

        passwordLabel.setPosition(-passwordBox.getWidth() / 2, 0);
        usernameLabel.setPosition(-usernameBox.getWidth() / 2, usernameBox.getHeight() * 2);

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleLoginClicked();
            }
        });
        signupButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleSignupClicked();
            }
        });

        errorLabel = new Label("", defaultUISkin);
        errorLabel.setColor(Color.RED);
        errorLabel.setAlignment(Align.center);
        errorLabel.setPosition(0, -120);


        stage.addActor(usernameBox);
        stage.addActor(loginButton);
        stage.addActor(usernameLabel);
        stage.addActor(passwordLabel);
        stage.addActor(passwordBox);
        stage.addActor(signupButton);
        stage.addActor(errorLabel);


    }

    public void handleLoginClicked() {
        Gson g = new Gson();
        SignupBody data = new SignupBody();
        data.username = usernameBox.getText();
        data.password = passwordBox.getText();
        client.postRequest("/user/login", g.toJson(data)).thenAccept(handler);
    }


    public void handleSignupClicked() {
        Gson g = new Gson();
        SignupBody data = new SignupBody();
        data.username = usernameBox.getText();
        data.password = passwordBox.getText();
        System.out.println("signup clicked");
        client.postRequest("/user/signup", g.toJson(data)).thenAccept(handler);
    }

    Consumer<HttpResponse<String>> handler = httpResponse -> {
        System.out.println(httpResponse.statusCode());
        switch (httpResponse.statusCode()) {
            case 500:
            case 409:
            case 400:
            case 401:
                errorLabel.setText(httpResponse.body());
                errorLabel.setVisible(true);
                break;
            case 200:
                errorLabel.setVisible(false);
                System.out.println("Switch to menu");
                this.hide();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        client.setScreen(new Menu(client));
                    }
                });
                break;
        }
    };


    static class SignupBody {
        String username;
        String password;
    }

    @Override
    public void show() {
        // setup input handling
        System.out.println("back on login");
        Gdx.input.setInputProcessor(stage); // input must be directed to stage for this screen
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        ScreenUtils.clear(0, 0, 0, 1);
        // render menu screen
        stage.draw();
    }

    @Override
    public void hide() {
        super.hide();
        System.out.println("hiding login");
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        super.dispose();
    }
}
