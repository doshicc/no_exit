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
import com.badlogic.gdx.graphics.Color;

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
        docsTable.pad(20);

        Label.LabelStyle chapterStyle = new Label.LabelStyle();
        chapterStyle.font = Assets.mainFont;
        chapterStyle.fontColor = Color.WHITE;

        Label.LabelStyle numberStyle = new Label.LabelStyle();
        numberStyle.font = Assets.mainFont;
        numberStyle.fontColor = new Color(0.25f, 0.15f, 0.05f, 1f); // тёмно-коричневый

        Button.ButtonStyle foundScrollStyle = new Button.ButtonStyle();
        foundScrollStyle.up = new TextureRegionDrawable(Assets.scrollFound);
        foundScrollStyle.down = new TextureRegionDrawable(Assets.scrollFound);

        Button.ButtonStyle lockedScrollStyle = new Button.ButtonStyle();
        lockedScrollStyle.up = new TextureRegionDrawable(Assets.scrollLocked);
        lockedScrollStyle.down = new TextureRegionDrawable(Assets.scrollLocked);
        lockedScrollStyle.disabled = new TextureRegionDrawable(Assets.scrollLocked);

        for (int chapter = 1; chapter <= 5; chapter++) {
            Label chapterLabel = new Label(
                    "Глава " + chapter + " ("
                            + DocumentManager.getCollectedCount(chapter)
                            + "/"
                            + DocumentManager.getTotalCount(chapter)
                            + ")",
                    chapterStyle
            );

            docsTable.add(chapterLabel).left().padTop(20).padBottom(15).colspan(4);
            docsTable.row();

            Table chapterGrid = new Table();

            for (int i = 1; i <= 8; i++) {
                int docId = (chapter - 1) * 8 + i;

                Document foundDoc = null;
                for (Document doc : DocumentManager.getCollectedDocuments()) {
                    if (doc.id == docId) {
                        foundDoc = doc;
                        break;
                    }
                }

                Stack stack = new Stack();

                if (foundDoc != null) {
                    final Document currentDoc = foundDoc;

                    Button scrollButton = new Button(foundScrollStyle);
                    scrollButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            game.setScreen(new DocumentViewScreen(game, currentDoc));
                        }
                    });

                    Label numberLabel = new Label(String.valueOf(i), numberStyle);
                    numberLabel.setAlignment(1); // center

                    stack.add(scrollButton);

                    Table overlay = new Table();
                    overlay.setFillParent(true);
                    overlay.center();
                    overlay.add(numberLabel);
                    stack.add(overlay);

                } else {
                    Button scrollButton = new Button(lockedScrollStyle);
                    scrollButton.setDisabled(true);

                    stack.add(scrollButton);
                }

                chapterGrid.add(stack)
                        .width(130)
                        .height(170)
                        .pad(10);

                if (i % 4 == 0) {
                    chapterGrid.row();
                }
            }

            docsTable.add(chapterGrid).left().padBottom(25).colspan(4);
            docsTable.row();
        }

        ScrollPane scrollPane = new ScrollPane(docsTable, scrollStyle);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        ImageButton backButton = new ImageButton(
                new TextureRegionDrawable(Assets.backArrow)
        );

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        root.padTop(100);
        root.add(scrollPane).expand().fill();

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