package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.PlayScreen;

public class MyGdxGame extends Game {
    // SpriteBatch лучше держать здесь один на всю игру, чтобы не плодить мусор в памяти
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Устанавливаем экран загрузки или сразу игровой экран
        // Теперь при запуске LibGDX создаст твой PlayScreen
        this.setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        // Обязательно вызываем super.render(), иначе отрисовка в PlayScreen не заработает
        super.render();
    }

    @Override
    public void dispose() {
        // Чистим память при закрытии
        batch.dispose();
    }
}
