package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.screens.MenuScreen; // или PlayScreen, если вы хотите сразу запускать его

public class MyGdxGame extends Game {
    public SpriteBatch batch;

    public OrthographicCamera camera;
    public Viewport viewport;

    public static final int SCR_WIDTH = 1280;
    public static final int SCR_HEIGHT = 720;

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(SCR_WIDTH, SCR_HEIGHT, camera);
        camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);

        // 1. Инициализируем ассеты
        Assets.load();
        Assets.manager.finishLoading();
        Assets.setup();

        // 2. Гарантированно создаем шрифт, если он не был создан в Assets
        if (Assets.mainFont == null) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/uvKits.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 24;
            parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхЦчшщъыьэюя1234567890: -_!?";

            Assets.mainFont = generator.generateFont(parameter);
            generator.dispose();
        }

        // 3. Открываем главное меню (если нужно сразу PlayScreen, замените на new PlayScreen(this))
        setScreen(new MenuScreen(this));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(SCR_WIDTH / 2f, SCR_HEIGHT / 2f, 0);
        camera.update();
    }

    @Override
    public void render() {
        // Очищаем экран общим цветом, чтобы не было серых/черных артефактов
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Вызываем метод render() активного экрана
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        Assets.dispose();
    }
}