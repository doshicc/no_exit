package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.screens.PlayScreen;

public class MyGdxGame extends Game {
    public SpriteBatch batch;

    // Поля для камеры и вьюпорта
    public OrthographicCamera camera;
    public Viewport viewport;

    // Целевое разрешение, под которое мы пишем игру
    public static final int SCR_WIDTH = 1280;
    public static final int SCR_HEIGHT = 720;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // 1. Создаем камеру
        camera = new OrthographicCamera();
        // 2. Инициализируем вьюпорт с целевым разрешением и привязываем к камере
        viewport = new FitViewport(SCR_WIDTH, SCR_HEIGHT, camera);
        // Выравниваем камеру
        camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);

        // 3. Подготавливаем ассеты
        Assets.load();

        // 4. Ждем, пока всё загрузится в память
        Assets.manager.finishLoading();

        // 5. Раскладываем загруженные текстуры по переменным
        Assets.setup();

        // 6. И только теперь, когда текстуры готовы, запускаем экран игры
        this.setScreen(new PlayScreen(this));
    }

    @Override
    public void resize(int width, int height) {
        // Обновляем вьюпорт при изменении размеров окна (например, при повороте телефона)
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        // Обязательно обновляем камеру перед отрисовкой
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        // Не забываем чистить AssetManager при выходе из игры
        Assets.dispose();
    }
}