package com.bevrfarlbt.NoExit.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.MyGdxGame;
import com.bevrfarlbt.NoExit.data.Document;

public class DocumentViewScreen implements Screen {

    private final MyGdxGame game;
    private final Document document;

    private Stage stage;
    private Skin skin;

    public DocumentViewScreen(
            MyGdxGame game,
            Document document) {

        this.game = game;
        this.document = document;
    }

    @Override
    public void show() {

        stage = new Stage(
                new ExtendViewport(
                        MyGdxGame.SCR_WIDTH,
                        MyGdxGame.SCR_HEIGHT));

        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        skin.add("default", Assets.mainFont);

        Label.LabelStyle labelStyle =
                new Label.LabelStyle();

        labelStyle.font = Assets.mainFont;

        skin.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle =
                new TextButton.TextButtonStyle();

        buttonStyle.font = Assets.mainFont;

        skin.add("default", buttonStyle);

        createUI();
    }

    private void createUI() {

        Table root = new Table();

        root.setFillParent(true);

        stage.addActor(root);

        Label title =
                new Label(
                        document.title,
                        skin);

        Label text =
                new Label(
                        document.text,
                        skin);

        text.setWrap(true);

        TextButton back =
                new TextButton(
                        "Назад",
                        skin);

        back.addListener(
                new ClickListener() {

                    @Override
                    public void clicked(
                            InputEvent event,
                            float x,
                            float y) {

                        game.setScreen(
                                new DocumentArchiveScreen(
                                        game));
                    }
                });

        root.add(title)
                .padBottom(20)
                .row();

        root.add(text)
                .width(800)
                .padBottom(40)
                .row();

        root.add(back)
                .width(250)
                .height(60);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(
                0,
                0,
                0,
                1);

        Gdx.gl.glClear(
                GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width,int height){
        stage.getViewport().update(width,height,true);
    }

    @Override public void pause(){}
    @Override public void resume(){}
    @Override public void hide(){}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}