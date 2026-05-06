package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Assets;
import com.mygdx.game.MyGdxGame;

public class MenuScreen implements Screen {
    private final MyGdxGame game;
    private final SpriteBatch batch;
    private Stage stage;
    private Texture buttonUpTexture;
    private Texture buttonDownTexture;
    private BitmapFont buttonFont;

    public MenuScreen(final MyGdxGame game) {
        this.game = game;
        this.batch = game.batch;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(MyGdxGame.SCR_WIDTH, MyGdxGame.SCR_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        createFont();
        createButtonTextures();
        createUI();
    }

    private void createFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/uvKits.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхЦчшщъыьэюя1234567890: -_!?abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        buttonFont = generator.generateFont(parameter);
        generator.dispose();
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
        pixmapDown.drawRectangle(0, 0, 350, 70);

        buttonDownTexture = new Texture(pixmapDown);
        pixmapDown.dispose();
    }

    private void createUI() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = buttonFont;
        style.fontColor = Color.WHITE;
        style.up = new TextureRegionDrawable(new TextureRegion(buttonUpTexture));
        style.down = new TextureRegionDrawable(new TextureRegion(buttonDownTexture));

        TextButton playButton = new TextButton("PLAY", style);
        TextButton settingsButton = new TextButton("SETTINGS", style);
        TextButton exitButton = new TextButton("EXIT", style);

        playButton.getLabel().setAlignment(Align.center);
        settingsButton.getLabel().setAlignment(Align.center);
        exitButton.getLabel().setAlignment(Align.center);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(playButton).pad(20).width(350).height(70).row();
        table.add(settingsButton).pad(20).width(350).height(70).row();
        table.add(exitButton).pad(20).width(350).height(70).row();

        // Переключение на PlayScreen
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game)); // Убедитесь, что класс PlayScreen существует
                dispose();
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

        batch.begin();
        batch.draw(Assets.menuBackground, 0, 0, MyGdxGame.SCR_WIDTH, MyGdxGame.SCR_HEIGHT);
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (buttonUpTexture != null) buttonUpTexture.dispose();
        if (buttonDownTexture != null) buttonDownTexture.dispose();
        if (buttonFont != null) buttonFont.dispose();
    }
}