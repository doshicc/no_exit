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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ShopScreen implements Screen {
    private final MyGdxGame game;
    private final SpriteBatch batch;
    private Stage stage;
    private Texture buttonUpTexture;
    private Texture buttonDownTexture;

    private Label coinsLabel;
    private Label turretCountLabel;
    private Label messageLabel;

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

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = Assets.mainFont;
        labelStyle.fontColor = Color.YELLOW;

        Label.LabelStyle messageStyle = new Label.LabelStyle();
        messageStyle.font = Assets.mainFont;
        messageStyle.fontColor = Color.WHITE;

        coinsLabel = new Label("", labelStyle);
        turretCountLabel = new Label("", labelStyle);
        messageLabel = new Label("", messageStyle);

        updateInfoLabel();

        Image balanceCoinImage = new Image(new TextureRegionDrawable(Assets.coinIcon));
        Image balanceTurretImage = new Image(new TextureRegionDrawable(Assets.turretIcon));

        Table balanceTable = new Table();

        balanceTable.add(balanceCoinImage)
                .width(34)
                .height(34)
                .padRight(10);

        balanceTable.add(coinsLabel)
                .padRight(35);

        balanceTable.add(balanceTurretImage)
                .width(38)
                .height(38)
                .padRight(10);

        balanceTable.add(turretCountLabel);

        Image priceCoinImage = new Image(new TextureRegionDrawable(Assets.coinIcon));
        Label priceText = new Label("Цена:", labelStyle);
        Label priceValue = new Label("5", labelStyle);

        Table priceTable = new Table();
        priceTable.add(priceText).padRight(10);
        priceTable.add(priceCoinImage)
                .width(28)
                .height(28)
                .padRight(8);
        priceTable.add(priceValue);

        TextButton buyTurretButton = new TextButton("КУПИТЬ ТУРЕЛЬ", style);
        buyTurretButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (ShopManager.spendCoins(5)) {
                    ShopManager.addTurretsToInventory(1);

                    messageLabel.setColor(Color.GREEN);
                    messageLabel.setText("Турель куплена");
                } else {
                    messageLabel.setColor(Color.RED);
                    messageLabel.setText("Недостаточно монет");
                }

                updateInfoLabel();
            }
        });

        ImageButton backButton = new ImageButton(
                new TextureRegionDrawable(Assets.backArrow)
        );

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(balanceTable)
                .padBottom(25)
                .row();

        table.add(priceTable)
                .padBottom(15)
                .row();

        table.add(buyTurretButton)
                .width(380)
                .height(70)
                .padBottom(20)
                .row();

        table.add(messageLabel)
                .padTop(5)
                .row();

        stage.addActor(table);

        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.top().left();

        backTable.add(backButton)
                .pad(20)
                .width(80)
                .height(80);

        stage.addActor(backTable);
    }

    private void updateInfoLabel() {
        if (coinsLabel != null) {
            coinsLabel.setText(String.valueOf(ShopManager.getCoins()));
        }

        if (turretCountLabel != null) {
            turretCountLabel.setText(String.valueOf(ShopManager.getTurretInventory()));
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