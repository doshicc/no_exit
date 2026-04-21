package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.B2DVars;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.managers.LevelManager;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.EnemyZombie;

public class PlayScreen implements Screen {
    private final SpriteBatch batch;
    private World world;
    private OrthographicCamera cam;

    private Player player;
    private LevelManager levelManager;
    private Array<EnemyZombie> zombies;

    public PlayScreen(MyGdxGame game) {
        this.batch = game.batch;
    }

    @Override
    public void show() {
        // Мир без гравитации для Top-Down
        world = new World(new Vector2(0, 0), true);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Загрузка уровня
        levelManager = new LevelManager(world);
        levelManager.createRoom(1280, 720);

        // Создание игрока
        player = new Player(world, 640, 360);

        // Создание врагов
        zombies = new Array<>();
        zombies.add(new EnemyZombie(world, 300, 300));
        zombies.add(new EnemyZombie(world, 900, 400));
        zombies.add(new EnemyZombie(world, 600, 150));
    }

    @Override
    public void render(float delta) {
        update(delta);

        // Очистка экрана
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        // Порядок отрисовки: пол -> враги -> игрок
        levelManager.draw(batch, 1280, 720);

        for (EnemyZombie z : zombies) {
            z.draw(batch);
        }

        player.draw(batch);

        batch.end();
    }

    public void update(float dt) {
        // Шаг физики
        world.step(1/60f, 6, 2);

        // Поворот игрока за мышью
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.unproject(mouse);
        player.update(dt, new Vector2(mouse.x, mouse.y));

        // Обновление ИИ зомби (преследование игрока)
        for (EnemyZombie z : zombies) {
            z.update(dt, player.body.getPosition());
        }

        // Ввод движения игрока
        Vector2 move = new Vector2(0, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.W)) move.y += 1;
        if(Gdx.input.isKeyPressed(Input.Keys.S)) move.y -= 1;
        if(Gdx.input.isKeyPressed(Input.Keys.A)) move.x -= 1;
        if(Gdx.input.isKeyPressed(Input.Keys.D)) move.x += 1;
        player.handleInput(move.nor());

        // Механика атаки
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            player.attack();

            // Проверка попадания по зомби
            for (EnemyZombie z : zombies) {
                if (z.isDead) continue;

                float dist = player.body.getPosition().dst(z.body.getPosition());
                // Если зомби в радиусе удара монтировки (1.5 метра в Box2D)
                if (dist < 1.5f) {
                    z.takeDamage(1);
                }
            }
        }

        // Плавное следование камеры за игроком
        cam.position.lerp(new Vector3(
                player.body.getPosition().x * B2DVars.PPM,
                player.body.getPosition().y * B2DVars.PPM,
                0), 0.1f);
        cam.update();
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(false, width, height);
    }

    @Override
    public void dispose() {
        world.dispose();
        levelManager.dispose();
        // Здесь можно добавить dispose для текстур игрока и зомби,
        // если они не управляются через AssetsManager
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}