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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.MyGdxGame;
import com.bevrfarlbt.NoExit.managers.ShopManager;

public class ShopScreen implements Screen {
    private final MyGdxGame game;
    private final SpriteBatch batch;
    private Stage stage;
    private Texture buttonUpTexture;
    private Texture buttonDownTexture;

    private Label infoLabel; // Текст с количеством монет и турелей

    public ShopScreen(final MyGdxGame game) {
        this.game = game;
        this.batch = game.batch;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(MyGdxGame.SCR_WIDTH, MyGdxGame.SCR_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        createButtonTextures();
        createUI();
    }

    private void createButtonTextures() {
        Pixmap pixmapUp = new Pixmap(350, 70, Pixmap.Format.RGBA8888);
        pixmapUp.setColor(0.15f, 0.15f, 0.15f, 0.85f);
        pixmapUp.fill();
        pixmapUp.setColor(0.1f, 0.6f, 0.8f, 1.0f);
        pixmapUp.drawRectangle(0, 0, 350, 70);
        buttonUpTexture = new Texture(pixmapUp);
        pixmapUp.dispose();

        Pixmap pixmapDown = new Pixmap(350, 70, Pixmap.Format.RGBA8888);
        pixmapDown.setColor(0.25f, 0.25f, 0.25f, 0.9f);
        pixmapDown.fill();
        pixmapDown.setColor(0.2f, 0.8f, 1.0f, 1.0f);
        buttonDownTexture = new Texture(pixmapDown);
        pixmapDown.dispose();
    }

    private void createUI() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = Assets.mainFont;
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.CYAN;
        style.up = new TextureRegionDrawable(new TextureRegion(buttonUpTexture));
        style.down = new TextureRegionDrawable(new TextureRegion(buttonDownTexture));

        // Инициализация метки информации о балансе
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = Assets.mainFont;
        labelStyle.fontColor = Color.YELLOW;
        infoLabel = new Label("", labelStyle);
        updateInfoLabel();

        // Кнопка покупки турели за 5 монет
        TextButton buyTurretButton = new TextButton("КУПИТЬ ТУРЕЛЬ (5 мон.)", style);
        buyTurretButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (ShopManager.spendCoins(5)) {
                    ShopManager.addTurretsToInventory(1);
                    updateInfoLabel();
                }
            }
        });

        // Кнопка возврата в меню
        TextButton backButton = new TextButton("НАЗАД", style);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Формируем интерфейс магазина вертикально
        table.add(infoLabel).padBottom(40).row();
        table.add(buyTurretButton).width(350).height(70).padBottom(20).row();
        table.add(backButton).width(350).height(70);

        stage.addActor(table);
    }

    private void updateInfoLabel() {
        if (infoLabel != null) {
            infoLabel.setText("Ваши Монеты: " + ShopManager.getCoins() + "  |  В наличии турелей: " + ShopManager.getTurretInventory());
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1);
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
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (buttonUpTexture != null) buttonUpTexture.dispose();
        if (buttonDownTexture != null) buttonDownTexture.dispose();
    }
}