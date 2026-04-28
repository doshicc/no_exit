package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.PlayScreen;

public class MyGdxGame extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // 1. Сначала подготавливаем ассеты
        Assets.load();

        // 2. Ждем, пока всё загрузится в память.
        // В будущем лучше сделать Screen загрузки, но для теста finishLoading — самое то.
        Assets.manager.finishLoading();

        // 3. Раскладываем загруженные текстуры по переменным (floorDefault и т.д.)
        Assets.setup();

        // 4. И только теперь, когда текстуры готовы, запускаем экран игры
        this.setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        // 5. Не забывай чистить AssetManager при выходе из игры
        Assets.dispose();
    }
}