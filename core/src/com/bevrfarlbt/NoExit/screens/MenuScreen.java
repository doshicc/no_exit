package com.bevrfarlbt.NoExit.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.MyGdxGame;
import com.bevrfarlbt.NoExit.Settings;
import com.bevrfarlbt.NoExit.managers.SaveManager;

public class MenuScreen implements Screen {
    private final MyGdxGame game;
    private final SpriteBatch batch;
    private Stage stage;
    private Texture buttonUpTexture;
    private Texture buttonDownTexture;

    public MenuScreen(final MyGdxGame game) {
        this.game = game;
        this.batch = game.batch;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(MyGdxGame.SCR_WIDTH, MyGdxGame.SCR_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        createButtonTextures();
        createUI();

        if (Assets.menuSound != null) {
            try {
                if (Settings.musicMenuEnabled) {
                    if (!Assets.menuSound.isPlaying()) {
                        Assets.menuSound.setLooping(true);
                        Assets.menuSound.setVolume(0.5f);
                        Assets.menuSound.play();
                    }
                } else {
                    Assets.menuSound.stop();
                }
            } catch (Exception e) {
                if (Settings.musicMenuEnabled) {
                    Assets.menuSound.play();
                } else {
                    Assets.menuSound.stop();
                }
            }
        }
    }

    private void createButtonTextures() {
        Pixmap pixmapUp = new Pixmap(350, 70, Pixmap.Format.RGBA8888);
        pixmapUp.setColor(0.15f, 0.15f, 0.15f, 0.85f);
        pixmapUp.fill();
        pixmapUp.setColor(0.8f, 0.4f, 0.1f, 1.0f);
        pixmapUp.drawRectangle(0, 0, 350, 70);
        buttonUpTexture = new Texture(pixmapUp);
        pixmapUp.dispose();

        Pixmap pixmapDown = new Pixmap(350, 70, Pixmap.Format.RGBA8888);
        pixmapDown.setColor(0.25f, 0.25f, 0.25f, 0.9f);
        pixmapDown.fill();
        pixmapDown.setColor(1.0f, 0.6f, 0.2f, 1.0f);
        buttonDownTexture = new Texture(pixmapDown);
        pixmapDown.dispose();
    }

    private void createUI() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = Assets.mainFont;
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.ORANGE;
        style.up = new TextureRegionDrawable(new TextureRegion(buttonUpTexture));
        style.down = new TextureRegionDrawable(new TextureRegion(buttonDownTexture));

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = Assets.titleFont;
        titleStyle.fontColor = Color.ORANGE;
        Label title = new Label("NO EXIT", titleStyle);

        TextButton playButton = new TextButton("ИГРАТЬ", style);
        TextButton continueButton = new TextButton("ПРОДОЛЖИТЬ", style);
        TextButton archiveButton = new TextButton("АРХИВ", style);
        TextButton shopButton = new TextButton("МАГАЗИН", style);
        TextButton settingsButton = new TextButton("НАСТРОЙКИ", style);
        TextButton exitButton = new TextButton("ВЫХОД", style);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(title).colspan(2).padBottom(80);
        table.row();

        if (SaveManager.hasSave()) {
            table.add(playButton).width(350).height(70).pad(15);
            table.add(continueButton).width(350).height(70).pad(15);
            table.row();
        } else {
            table.add(playButton).width(350).height(70).pad(15).colspan(2);
            table.row();
        }
        table.add(archiveButton).width(350).height(70).pad(15);
        table.add(shopButton).width(350).height(70).pad(15);
        table.row();
        table.add(settingsButton).width(350).height(70).pad(15);
        table.add(exitButton).width(350).height(70).pad(15);

        archiveButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new DocumentArchiveScreen(game));
                    }
                });

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                SaveManager.deleteSave();

                if (Assets.menuSound != null) {
                    try {
                        Assets.menuSound.stop();
                    } catch (Exception ignored) {}
                }

                if (!Settings.introWatched) {
                    game.setScreen(new IntroScreen(game));
                } else {
                    game.setScreen(new PlayScreen(game));
                }
            }
        });

        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (Assets.menuSound != null) {
                    try {
                        Assets.menuSound.stop();
                    } catch (Exception ignored) {}
                }

                game.setScreen(new PlayScreen(game));
            }
        });

        shopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ShopScreen(game));
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getViewport().apply();
        batch.setProjectionMatrix(stage.getCamera().combined);

        batch.begin();
        if (Assets.menuBackground != null) {
            float worldWidth = stage.getViewport().getWorldWidth();
            float worldHeight = stage.getViewport().getWorldHeight();
            batch.draw(Assets.menuBackground, 0, 0, worldWidth, worldHeight);
        }
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (buttonUpTexture != null) buttonUpTexture.dispose();
        if (buttonDownTexture != null) buttonDownTexture.dispose();
    }
}