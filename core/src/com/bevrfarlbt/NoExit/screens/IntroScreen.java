package com.bevrfarlbt.NoExit.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.MyGdxGame;
import com.bevrfarlbt.NoExit.Settings;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.audio.Music;

public class IntroScreen implements Screen {

    private final MyGdxGame game;
    private final SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Music introMusic;

    private Texture[] slides;
    private String[] slideTexts;
    private int currentSlide;
    private boolean waitingForRelease;
    private GlyphLayout layout;


    public IntroScreen(MyGdxGame game) {
        this.game = game;
        this.batch = game.batch;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        waitingForRelease = true;
        layout = new GlyphLayout();
        viewport = new ExtendViewport(
                MyGdxGame.SCR_WIDTH,
                MyGdxGame.SCR_HEIGHT,
                camera
        );

        introMusic = Gdx.audio.newMusic(
                Gdx.files.internal("sounds/intro.mp3")
        );

        introMusic.setLooping(true);
        introMusic.setVolume(0.4f);

        if (Settings.musicMenuEnabled) {
            introMusic.play();
        }

        currentSlide = 0;

        slides = new Texture[]{
                new Texture("intro/ChatGPT Image Jun 1, 2026 at 02_22_03 PM.png"),
                new Texture("intro/ChatGPT Image Jun 1, 2026 at 02_22_46 PM.png"),
                new Texture("intro/ChatGPT Image Jun 1, 2026 at 02_24_03 PM.png"),
                new Texture("intro/ChatGPT Image Jun 1, 2026 at 02_25_59 PM.png"),
                new Texture("intro/ChatGPT Image Jun 1, 2026 at 02_26_56 PM.png"),
                new Texture("intro/ChatGPT Image Jun 1, 2026 at 02_29_41 PM.png"),
                new Texture("intro/ChatGPT Image Jun 1, 2026 at 02_32_12 PM.png"),
                new Texture("intro/ChatGPT Image Jun 1, 2026 at 02_33_27 PM.png")
        };

        slideTexts = new String[] {
                "Каждый день был одинаковым.\n Смена за сменой. \n Шум машин. Бесконечная работа.",
                "Но в тот день что-то пошло не так.\n Сирены завыли по всему комплексу.",
                "Затем — вспышка.\n И темнота.",
                "Когда я очнулся...\n Завод стал другим.",
                "Коридоры тянулись бесконечно.\n А в темноте кто-то двигался.",
                "Это были не люди.\n Уже не люди.",
                "Выхода не было.\n Только я... и они.",
                "Если я хочу выжить...\n Мне придется пройти через весь этот завод."
        };
    }

    private void nextSlide() {
        if (currentSlide < slides.length - 1) {
            currentSlide++;
        } else {
            Settings.introWatched = true;
            Settings.save();
            game.setScreen(new PlayScreen(game));
        }
    }

    @Override
    public void render(float delta) {
        if (waitingForRelease) {

            if (!Gdx.input.isTouched()) {
                waitingForRelease = false;
            }
        } else {

            if (Gdx.input.justTouched()
                    || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                    || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                nextSlide();
            }
        }


        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        Texture currentTexture = slides[currentSlide];

        batch.draw(currentTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        layout.setText(Assets.mainFont, slideTexts[currentSlide]);
        Assets.mainFont.draw(batch, layout, 50, 200);

        Assets.mainFont.draw(batch, "Нажмите для продолжения", 30, 40);
        Assets.mainFont.draw(batch, (currentSlide + 1) + " / " + slides.length, viewport.getWorldWidth() - 120, 40);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        if (introMusic != null) {
            introMusic.stop();
        }
    }

    @Override
    public void dispose() {
        if (introMusic != null) {
            introMusic.dispose();
        }

        if (slides != null) {
            for (Texture texture : slides) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }
    }
}