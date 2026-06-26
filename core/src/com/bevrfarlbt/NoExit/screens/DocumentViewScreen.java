package com.bevrfarlbt.NoExit.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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

    public DocumentViewScreen(MyGdxGame game, Document document) {
        this.game = game;
        this.document = document;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(MyGdxGame.SCR_WIDTH, MyGdxGame.SCR_HEIGHT));

        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        skin.add("default", Assets.mainFont);

        Label.LabelStyle labelStyle = new Label.LabelStyle();

        labelStyle.font = Assets.mainFont;

        skin.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();

        buttonStyle.font = Assets.mainFont;

        skin.add("default", buttonStyle);

        createUI();
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = Assets.mainFont;
        titleStyle.fontColor = new Color(0.22f, 0.12f, 0.05f, 1f);

        Label.LabelStyle textStyle = new Label.LabelStyle();
        textStyle.font = Assets.mainFont;
        textStyle.fontColor = new Color(0.18f, 0.10f, 0.05f, 1f);

        Label title = new Label(document.title, titleStyle);
        title.setWrap(true);
        title.setAlignment(1);

        Label text = new Label(document.text, textStyle);
        text.setWrap(true);
        text.setAlignment(0);

        Table documentContent = new Table();
        documentContent.top();
        documentContent.left();

        documentContent.add(title)
                .width(1180)
                .padBottom(0)
                .center()
                .row();

        documentContent.add(text)
                .width(1180)
                .left()
                .top()
                .row();

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        ScrollPane textPane = new ScrollPane(documentContent, scrollStyle);
        textPane.setFadeScrollBars(false);
        textPane.setScrollingDisabled(true, false);

        Table paperTable = new Table();
        paperTable.setBackground(new TextureRegionDrawable(Assets.documentViewBg));

        paperTable.padTop(430);
        paperTable.padBottom(100);
        paperTable.padLeft(130);
        paperTable.padRight(130);

        paperTable.add(textPane)
                .width(1180)
                .height(520)
                .top()
                .left();

        root.add(paperTable)
                .width(1650)
                .height(760)
                .center();

        ImageButton backButton = new ImageButton(
                new TextureRegionDrawable(Assets.backArrow)
        );

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new DocumentArchiveScreen(game));
            }
        });

        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.top().left();
        backTable.setTouchable(Touchable.childrenOnly);

        backTable.add(backButton)
                .pad(20)
                .width(80)
                .height(80);

        stage.addActor(backTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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