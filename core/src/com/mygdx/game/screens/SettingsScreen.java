package com.mygdx.game.screens;

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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Assets;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Settings;

public class SettingsScreen implements Screen {
    private final MyGdxGame game;
    private final SpriteBatch batch;
    private Stage stage;
    private Texture btnUp;
    private Texture btnDown;

    public SettingsScreen(final MyGdxGame game) {
        this.game = game;
        this.batch = game.batch;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(MyGdxGame.SCR_WIDTH, MyGdxGame.SCR_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        createButtonTextures();
        createUI();
    }

    private void createButtonTextures() {
        Pixmap pixUp = new Pixmap(450, 60, Pixmap.Format.RGBA8888);
        pixUp.setColor(0.15f, 0.15f, 0.15f, 0.85f);
        pixUp.fill();
        pixUp.setColor(0.8f, 0.4f, 0.1f, 1.0f);
        pixUp.drawRectangle(0, 0, 450, 60);
        btnUp = new Texture(pixUp);
        pixUp.dispose();

        Pixmap pixDown = new Pixmap(450, 60, Pixmap.Format.RGBA8888);
        pixDown.setColor(0.25f, 0.25f, 0.25f, 0.9f);
        pixDown.fill();
        pixDown.setColor(1.0f, 0.6f, 0.2f, 1.0f);
        btnDown = new Texture(pixDown);
        pixDown.dispose();
    }

    private void createUI() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = Assets.mainFont;
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.ORANGE;
        style.up = new TextureRegionDrawable(new TextureRegion(btnUp));
        style.down = new TextureRegionDrawable(new TextureRegion(btnDown));

        final TextButton menuMusicBtn = new TextButton(getMusicMenuText(), style);
        final TextButton gameMusicBtn = new TextButton(getGameMusicText(), style);
        final TextButton hitSoundBtn = new TextButton(getHitSoundText(), style);
        final TextButton zombieSoundBtn = new TextButton(getZombieSoundText(), style);
        TextButton backBtn = new TextButton("НАЗАД В МЕНЮ", style);

        menuMusicBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Settings.musicMenuEnabled = !Settings.musicMenuEnabled;
                menuMusicBtn.setText(getMusicMenuText());
                Settings.save();

                if (Assets.menuSound != null) {
                    try {
                        if (Settings.musicMenuEnabled) {
                            Assets.menuSound.setLooping(true);
                            Assets.menuSound.setVolume(0.5f);
                            Assets.menuSound.play();
                        } else {
                            Assets.menuSound.stop(); // Полная остановка звука
                        }
                    } catch (Exception e) {
                        // Если в Assets лежит Sound, а не Music, этот блок поймает ошибку и не даст игре вылететь
                        Gdx.app.log("AUDIO_ERROR", "Не удалось изменить состояние музыки меню: " + e.getMessage());
                    }
                }
            }
        });

        gameMusicBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Settings.musicGameEnabled = !Settings.musicGameEnabled;
                gameMusicBtn.setText(getGameMusicText());
                Settings.save();
            }
        });

        hitSoundBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Settings.soundHitEnabled = !Settings.soundHitEnabled;
                hitSoundBtn.setText(getHitSoundText());
                Settings.save();
            }
        });

        zombieSoundBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Settings.soundZombieEnabled = !Settings.soundZombieEnabled;
                zombieSoundBtn.setText(getZombieSoundText());
                Settings.save();
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });


        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(menuMusicBtn).pad(10).width(450).height(60).row();
        table.add(gameMusicBtn).pad(10).width(450).height(60).row();
        table.add(hitSoundBtn).pad(10).width(450).height(60).row();
        table.add(zombieSoundBtn).pad(10).width(450).height(60).row();
        table.add(backBtn).pad(30).width(450).height(60).row();

        stage.addActor(table);
    }

    private String getMusicMenuText() { return "МУЗЫКА В МЕНЮ: " + (Settings.musicMenuEnabled ? "ВКЛ" : "ВЫКЛ"); }
    private String getGameMusicText() { return "МУЗЫКА В ИГРЕ: " + (Settings.musicGameEnabled ? "ВКЛ" : "ВЫКЛ"); }
    private String getHitSoundText() { return "ЗВУКИ УДАРОВ: " + (Settings.soundHitEnabled ? "ВКЛ" : "ВЫКЛ"); }
    private String getZombieSoundText() { return "ЗВУКИ ЗОМБИ: " + (Settings.soundZombieEnabled ? "ВКЛ" : "ВЫКЛ"); }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        if (Assets.menuBackground != null) {
            batch.draw(Assets.menuBackground, 0, 0, MyGdxGame.SCR_WIDTH, MyGdxGame.SCR_HEIGHT);
        }
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (btnUp != null) btnUp.dispose();
        if (btnDown != null) btnDown.dispose();
    }
}