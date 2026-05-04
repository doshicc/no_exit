package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Assets;
import com.mygdx.game.B2DVars;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.managers.BodyFactory;
import com.mygdx.game.managers.LevelManager;
import com.mygdx.game.renderers.LevelRenderer;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.EnemyZombie;
import com.mygdx.game.objects.PowerUp;
import com.mygdx.game.data.RoomData;
import com.mygdx.game.managers.AttackListener;

public class PlayScreen implements Screen {
    private final SpriteBatch batch;
    private World world;
    private OrthographicCamera cam;
    private Player player;

    private LevelManager levelManager;
    private LevelRenderer levelRenderer;
    private BodyFactory bodyFactory;

    private Array<EnemyZombie> zombies;
    private Array<PowerUp> powerUps;
    private RoomData currentRoom, nextRoom;
    private float roomW = 1280f, roomH = 720f, corridorGap = 350f, tileSize = 64f;
    private boolean roomCleared = false;

    private OrthographicCamera uiCam;

    private TextureRegion uiFullHeart;
    private TextureRegion uiEmptyHeart;
    private TextureRegion uiExtraHeart;

    private float oneShotTimer = 0f;

    public PlayScreen(MyGdxGame game) {
        this.batch = game.batch;
    }

    @Override
    public void show() {
        world = new World(new Vector2(0, 0), true);
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        bodyFactory = new BodyFactory(world);
        levelManager = new LevelManager(world);
        levelRenderer = new LevelRenderer();

        zombies = new Array<>();
        powerUps = new Array<>();

        currentRoom = levelManager.createRoom(0, 0, roomW, roomH, true);
        player = new Player(world, 200, roomH / 2);
        world.setContactListener(new AttackListener(player));

        uiFullHeart = Assets.fullHeart;
        uiEmptyHeart = Assets.emptyHeart;
        uiExtraHeart = Assets.extraHeart;

        spawnZombies(currentRoom);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        levelRenderer.render(batch, currentRoom);

        if (nextRoom != null) {
            drawCorridor();
            levelRenderer.render(batch, nextRoom);
        }

        if (!roomCleared) {
            batch.setColor(0, 0, 0, 0.8f);
            batch.draw(Assets.leftRightWall, currentRoom.position.x + roomW - tileSize, roomH/2 - tileSize, tileSize, tileSize*3);
            batch.setColor(1, 1, 1, 1f);
        }

        for (EnemyZombie z : zombies) z.draw(batch);
        for (PowerUp p : powerUps) p.draw(batch);
        player.draw(batch);

        batch.end();

        // Отрисовка наводки атаки (код возвращён в классе Player.java)
        player.drawDebugAttack(cam.combined);

        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();

        int currentLives = player.getLives();
        int maxLives = player.getMaxLives();
        boolean hasExtra = player.hasExtraLife();

        float heartSize = 32f; // Единый размер для всех сердечек
        float startX = 20f;
        float startY = Gdx.graphics.getHeight() - heartSize - 20f;
        float gap = 40f;

        for (int i = 0; i < maxLives; i++) {
            if (i < currentLives) {
                batch.draw(uiFullHeart, startX + (i * gap), startY, heartSize, heartSize);
            } else {
                batch.draw(uiEmptyHeart, startX + (i * gap), startY, heartSize, heartSize);
            }
        }

        if (hasExtra) {
            // Теперь extraHeart рисуется с тем же размером 32f, что и обычные
            batch.draw(uiExtraHeart, startX + (3 * gap), startY, heartSize, heartSize);
        }

        batch.end();
    }

    private void drawCorridor() {
        float startX = currentRoom.position.x + roomW;
        float endX = nextRoom.position.x;
        float centerY = roomH / 2f;

        for (float x = startX; x < endX; x += tileSize) {
            batch.draw(Assets.floorDefault, x, centerY, tileSize, tileSize);
            batch.draw(Assets.floorDefault, x, centerY + tileSize, tileSize, tileSize);
            batch.draw(Assets.floorDefault, x, centerY - tileSize, tileSize, tileSize);
            batch.draw(Assets.upWall, x, centerY + tileSize * 2, tileSize, tileSize);
            batch.draw(Assets.downWall, x, centerY - tileSize * 2, tileSize, tileSize);
        }
    }

    private void update(float dt) {
        world.step(1/60f, 6, 2);

        if (oneShotTimer > 0) {
            oneShotTimer -= dt;
        }

        if (!roomCleared) {
            boolean anyAlive = false;
            for (int i = 0; i < zombies.size; i++) {
                EnemyZombie z = zombies.get(i);

                if (z.isDead && z.getPowerUp() == null) {
                    PowerUp p = z.trySpawnPowerUp();
                    if (p != null) {
                        powerUps.add(p);
                    }
                }

                if (!z.isDead) anyAlive = true;
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
                bodyFactory.createRect(nextRoom.position.x + tileSize/2, roomH/2, tileSize, tileSize*2, true, 0, 0, "wall_side");

                cleanupZombies();
                currentRoom.destroy(world);
                currentRoom = nextRoom;
                nextRoom = null;
                roomCleared = false;
            }
        }

        handleInput(dt);

        Vector2 playerPos = player.body.getPosition().cpy().scl(B2DVars.PPM);

        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUp p = powerUps.get(i);
            p.update(dt);

            if (playerPos.dst(p.basePosition) < 40f) {
                applyPowerUp(p);
                powerUps.removeIndex(i);
            }
        }

        for (EnemyZombie z : zombies) z.update(dt, player.body.getPosition(), player);

        float camX = Math.max(player.body.getPosition().x * B2DVars.PPM, Gdx.graphics.getWidth() / 2f);
        cam.position.lerp(new Vector3(camX, roomH / 2, 0), 0.1f);
        cam.update();
    }

    private void applyPowerUp(PowerUp p) {
        switch (p.type) {
            case HEAL:
                player.heal();
                Gdx.app.log("POWERUP", "Здоровье восстановлено!");
                break;
            case SHIELD:
                player.addExtraLife();
                break;
            case ONE_SHOT:
                oneShotTimer = 5f;
                for (EnemyZombie z : zombies) {
                    if (!z.isDead) {
                        z.takeDamage(10);
                    }
                }
                Gdx.app.log("POWERUP", "Супер-баф: Уничтожение всех зомби!");
                break;
        }
    }

    private void createCorridorPhysics() {
        float startX = currentRoom.position.x + roomW;
        float endX = nextRoom.position.x;
        float yBottom = roomH / 2 - tileSize;
        float yTop = roomH / 2 + tileSize + tileSize;

        currentRoom.bodies.add(bodyFactory.createRect(startX + (endX - startX)/2, yBottom - 5, (endX-startX), 10, true, 0, 0, "wall_low"));
        currentRoom.bodies.add(bodyFactory.createRect(startX + (endX - startX)/2, yTop + 5, (endX-startX), 10, true, 0, 0, "wall_up"));
    }

    private void spawnZombies(RoomData room) {
        int numberOfZombies = MathUtils.random(4, 7);
        float padding = 100f;

        for (int i = 0; i < numberOfZombies; i++) {
            float randomX = room.position.x + MathUtils.random(padding, roomW - padding);
            float randomY = room.position.y + MathUtils.random(padding, roomH - padding);

            zombies.add(new EnemyZombie(world, randomX, randomY));
        }
    }

    private void handleInput(float dt) {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.unproject(mouse);

        player.update(dt, new Vector2(mouse.x, mouse.y));

        Vector2 move = new Vector2(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) move.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) move.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) move.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) move.x += 1;
        player.handleInput(move.nor());

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && player.canAttack()) {
            player.attack();
            handleCombat();
        }
    }

    private void handleCombat() {
        for (EnemyZombie z : zombies) {
            if (z.isDead) continue;

            Vector2 toZ = z.body.getPosition().cpy().sub(player.body.getPosition());

            if (toZ.len() < player.getAttackRadius()) {
                Vector2 lookDir = player.getLookDirection().cpy().nor();
                Vector2 targetDir = toZ.cpy().nor();

                float dotProduct = lookDir.dot(targetDir);
                float angleRad = (float) Math.acos(MathUtils.clamp(dotProduct, -1f, 1f));
                float angleDeg = MathUtils.radiansToDegrees * angleRad;

                if (angleDeg < player.getAttackAngleRange() / 2f) {
                    z.takeDamage(1);
                    z.applyStun(0.3f);
                    Vector2 pushVelocity = toZ.cpy().nor().scl(6f);
                    z.body.setLinearVelocity(pushVelocity);
                }
            }
        }
    }

    private void cleanupZombies() {
        for (int i = zombies.size - 1; i >= 0; i--) {
            EnemyZombie z = zombies.get(i);

            if (z.isDead || (z.body != null && z.body.getPosition().x * B2DVars.PPM < currentRoom.position.x + roomW)) {
                if (z.body != null) {
                    world.destroyBody(z.body);
                }
                zombies.removeIndex(i);
            }
        }
        powerUps.clear();
    }

    @Override public void resize(int width, int height) { cam.setToOrtho(false, width, height); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        world.dispose();
        player.dispose();
    }
}