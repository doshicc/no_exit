package com.bevrfarlbt.NoExit;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.ExtendViewport; // Изменено на ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bevrfarlbt.NoExit.screens.MenuScreen;

public class MyGdxGame extends Game {
    public SpriteBatch batch;

    public OrthographicCamera camera;
    public Viewport viewport;

    // Это теперь базовая виртуальная рабочая область.
    // Высота 720 зафиксируется, а ширина будет увеличиваться (например, до 1440 или 1600) на длинных экранах.
    public static final int SCR_WIDTH = 1280;
    public static final int SCR_HEIGHT = 720;

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        // Переключаемся на ExtendViewport, чтобы убрать черные полосы по бокам
        viewport = new ExtendViewport(SCR_WIDTH, SCR_HEIGHT, camera);
        camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);

        // 1. Инициализируем ассеты
        Assets.load();
        Assets.manager.finishLoading();
        Assets.setup();

        if (Assets.mainFont == null) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/uvKits.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 24;
            parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхЦчшщъыьэюя1234567890: -_!?";

            Assets.mainFont = generator.generateFont(parameter);
            generator.dispose();
        }

        setScreen(new MenuScreen(this));
    }

    @Override
    public void resize(int width, int height) {
        // Обновляем виупорт под реальные пиксели экрана смартфона
        viewport.update(width, height, true);

        // ВАЖНО: Центрируем камеру по фактической ширине и высоте получившегося виртуального мира,
        // а не по жесткой константе SCR_WIDTH, иначе картинка съедет вбок.
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
        camera.update();
    }

    @Override
    public void render() {
        // Чистим экран темно-серым цветом
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Применяем настройки виупорта перед отрисовкой текущего экрана
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        Assets.dispose();
    }
}