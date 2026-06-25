package com.bevrfarlbt.NoExit.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.MyGdxGame;
import com.bevrfarlbt.NoExit.data.Document;
import com.bevrfarlbt.NoExit.managers.DocumentManager;

public class DocumentArchiveScreen implements Screen {

    private final MyGdxGame game;

    private Stage stage;
    private Skin skin;

    public DocumentArchiveScreen(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(MyGdxGame.SCR_WIDTH, MyGdxGame.SCR_HEIGHT));

        Gdx.input.setInputProcessor(stage);

        createSkin();
        createUI();
    }

    private void createSkin() {
        skin = new Skin();

        skin.add("default", Assets.mainFont);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = Assets.mainFont;

        skin.add("default", labelStyle);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = Assets.mainFont;
        skin.add("default", buttonStyle);
    }

    private void createUI() {
        Table root = new Table();

        root.setFillParent(true);
        stage.addActor(root);
        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();

        Table docsTable = new Table();

        docsTable.top();
        docsTable.left();

        for (int chapter = 1; chapter <= 5; chapter++) {
            Label chapterLabel = new Label("Глава "
                                    + chapter
                                    + " ("
                                    + DocumentManager.getCollectedCount(chapter)
                                    + "/"
                                    + DocumentManager.getTotalCount(chapter)
                                    + ")", skin);

            docsTable.add(chapterLabel).left().padTop(20).padBottom(10);
            docsTable.row();

            for (int i = 1; i <= 8; i++) {
                int docId = (chapter - 1) * 8 + i;

                Document foundDoc = null;

                for (Document doc : DocumentManager.getCollectedDocuments()) {
                    if (doc.id == docId) {
                        foundDoc = doc;
                        break;
                    }
                }

                if (foundDoc != null) {
                    final Document currentDoc = foundDoc;
                    TextButton btn = new TextButton(currentDoc.title, skin);
                    btn.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    game.setScreen(new DocumentViewScreen(game, currentDoc));
                                }
                            });

                    docsTable.add(btn).width(500).pad(5);

                } else {
                    TextButton btn = new TextButton("???", skin);
                    btn.setDisabled(true);
                    docsTable.add(btn).width(500).pad(5);
                }
                docsTable.row();
            }
        }

        ScrollPane scrollPane = new ScrollPane(docsTable, scrollStyle);

        ImageButton backButton = new ImageButton(
                new TextureRegionDrawable(Assets.backArrow)
        );

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        root.padTop(90);
        root.add(scrollPane).expand().fill();

        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.top().left();
        backTable.setTouchable(Touchable.childrenOnly);

        backTable.add(backButton)
                .pad(20)
                .width(60)
                .height(60);

        stage.addActor(backTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);

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