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
import com.mygdx.game.objects.RoomData;

public class PlayScreen implements Screen {
    private final SpriteBatch batch;
    private World world;
    private OrthographicCamera cam;
    private Player player;
    private LevelManager levelManager;
    private Array<EnemyZombie> zombies;

    private RoomData currentRoom, nextRoom;
    private float roomW = 1280f, roomH = 720f, corridorGap = 350f, tileSize = 64f;
    private boolean roomCleared = false;

    public PlayScreen(MyGdxGame game) {
        this.batch = game.batch;
    }

    @Override
    public void show() {
        world = new World(new Vector2(0, 0), true);
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        levelManager = new LevelManager(world);
        zombies = new Array<>();

        currentRoom = levelManager.createRoom(0, 0, roomW, roomH, true);
        player = new Player(world, 200, roomH / 2);

        spawnZombies(currentRoom);
    }

    private void spawnZombies(RoomData room) {
        zombies.add(new EnemyZombie(world, room.position.x + 700, room.position.y + 300));
        zombies.add(new EnemyZombie(world, room.position.x + 1000, room.position.y + 500));
        zombies.add(new EnemyZombie(world, room.position.x + 850, room.position.y + 150));
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        levelManager.drawRoom(batch, currentRoom, currentRoom.position.x == 0);

        if (nextRoom != null) {
            // Коридор (визуал)
            for (float x = currentRoom.position.x + roomW; x < nextRoom.position.x; x += tileSize) {
                float centerY = roomH / 2f;
                batch.draw(levelManager.floorDefault, x, centerY, tileSize, tileSize);
                batch.draw(levelManager.floorDefault, x, centerY + tileSize, tileSize, tileSize);
                batch.draw(levelManager.floorDefault, x, centerY - tileSize, tileSize, tileSize);
                batch.draw(levelManager.upWall, x, centerY + tileSize * 2, tileSize, tileSize);
                batch.draw(levelManager.downWall, x, centerY - tileSize * 2, tileSize, tileSize);
            }
            levelManager.drawRoom(batch, nextRoom, false);
        }

        if (!roomCleared) {
            batch.setColor(0, 0, 0, 0.8f);
            batch.draw(levelManager.leftRightWall, currentRoom.position.x + roomW - tileSize, roomH/2 - tileSize, tileSize, tileSize*3);
            batch.setColor(1, 1, 1, 1f);
        }

        for (EnemyZombie z : zombies) z.draw(batch);
        player.draw(batch);

        batch.end();
    }

    private void update(float dt) {
        world.step(1/60f, 6, 2);

        if (!roomCleared) {
            boolean anyAlive = false;
            for (EnemyZombie z : zombies) {
                if (!z.isDead) { anyAlive = true; break; }
            }
            if (!anyAlive) roomCleared = true;
        }

        if (roomCleared && nextRoom == null) {
            float nx = currentRoom.position.x + roomW + corridorGap;
            nextRoom = levelManager.createRoom(nx, 0, roomW, roomH, false);
            spawnZombies(nextRoom);
            createCorridorPhysics();
        }

        if (nextRoom != null) {
            float playerX = player.body.getPosition().x * B2DVars.PPM;
            if (playerX > nextRoom.position.x + 128) {
                levelManager.createStaticRect(nextRoom.position.x + tileSize/2, roomH/2, tileSize/2, tileSize, "wall_side");
                cleanupZombies();
                currentRoom.destroy(world);
                currentRoom = nextRoom;
                nextRoom = null;
                roomCleared = false;
            }
        }

        handleInput(dt);
        for (EnemyZombie z : zombies) z.update(dt, player.body.getPosition());

        float camX = Math.max(player.body.getPosition().x * B2DVars.PPM, Gdx.graphics.getWidth() / 2f);
        cam.position.lerp(new Vector3(camX, roomH / 2, 0), 0.1f);
        cam.update();
    }

    private void createCorridorPhysics() {
        float startX = currentRoom.position.x + roomW;
        float endX = nextRoom.position.x;
        float yBottom = roomH / 2 - tileSize;
        float yTop = roomH / 2 + tileSize + tileSize;
        currentRoom.bodies.add(levelManager.createStaticRect(startX + (endX - startX)/2, yBottom - 5, (endX-startX)/2, 5, "wall_low"));
        currentRoom.bodies.add(levelManager.createStaticRect(startX + (endX - startX)/2, yTop + 5, (endX-startX)/2, 5, "wall_up"));
    }

    private void handleInput(float dt) {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.unproject(mouse);
        player.update(dt, new Vector2(mouse.x, mouse.y));

        Vector2 move = new Vector2(0, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.W)) move.y += 1;
        if(Gdx.input.isKeyPressed(Input.Keys.S)) move.y -= 1;
        if(Gdx.input.isKeyPressed(Input.Keys.A)) move.x -= 1;
        if(Gdx.input.isKeyPressed(Input.Keys.D)) move.x += 1;
        player.handleInput(move.nor());

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            player.attack();
            handleCombat();
        }
    }

    private void handleCombat() {
        for (EnemyZombie z : zombies) {
            if (z.isDead) continue;
            Vector2 toZ = z.body.getPosition().cpy().sub(player.body.getPosition());
            if (toZ.len() < 2.8f) {
                float angle = Math.abs(player.getLookDirection().angleDeg(toZ));
                if (angle < 70f) {
                    z.takeDamage(1);
                    z.body.applyLinearImpulse(toZ.nor().scl(5f), z.body.getWorldCenter(), true);
                }
            }
        }
    }

    private void cleanupZombies() {
        for (int i = zombies.size - 1; i >= 0; i--) {
            EnemyZombie z = zombies.get(i);
            if (z.isDead || (z.body.getPosition().x * B2DVars.PPM < currentRoom.position.x + roomW)) {
                world.destroyBody(z.body);
                zombies.removeIndex(i);
            }
        }
    }

    @Override public void resize(int width, int height) { cam.setToOrtho(false, width, height); }
    @Override public void dispose() { world.dispose(); levelManager.dispose(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}