package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
    private final MyGdxGame game;
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

    private int roomsClearedCount = 0;
    private float sessionTimeSeconds = 0f;

    private OrthographicCamera uiCam;

    private TextureRegion uiFullHeart;
    private TextureRegion uiEmptyHeart;
    private TextureRegion uiExtraHeart;

    private float oneShotTimer = 0f;

    private Stage stageUI;
    private Skin skin;

    public PlayScreen(MyGdxGame game) {
        this.game = game;
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

        stageUI = new Stage(new ScreenViewport());

        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(stageUI);
        Gdx.input.setInputProcessor(multiplexer);

        createSkin();
        createUI();
    }

    private void createSkin() {
        skin = new Skin();
        skin.add("default", Assets.mainFont);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.getFont("default");
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.ORANGE;
        style.overFontColor = Color.CYAN;
        skin.add("default", style);
    }

    private void createUI() {
        TextButton menuButton = new TextButton("MENU", skin);

        float btnWidth = 120f;
        float btnHeight = 50f;

        menuButton.setPosition(Gdx.graphics.getWidth() - btnWidth - 20f, Gdx.graphics.getHeight() - btnHeight - 20f);
        menuButton.setSize(btnWidth, btnHeight);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        });

        stageUI.addActor(menuButton);
    }

    @Override
    public void render(float delta) {
        update(delta);

        // Очистка экрана
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        levelRenderer.render(batch, currentRoom);

        if (nextRoom != null) {
            drawCorridor();
            levelRenderer.render(batch, nextRoom);
        }

        if (!roomCleared) {
            batch.setColor(0, 0, 0, 0.8f);
            batch.draw(Assets.leftRightWall, currentRoom.position.x + roomW - tileSize, roomH / 2 - tileSize, tileSize, tileSize * 3);
            batch.setColor(1, 1, 1, 1f);
        }

        for (EnemyZombie z : zombies) z.draw(batch);
        for (PowerUp p : powerUps) p.draw(batch);
        player.draw(batch);

        batch.end();

        player.drawDebugAttack(cam.combined);

        // Отрисовка UI
        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();

        float heartSize = 32f;
        float startX = 20f;
        float startY = Gdx.graphics.getHeight() - heartSize - 20f;
        float gap = 40f;

        for (int i = 0; i < player.getMaxLives(); i++) {
            if (i < player.getLives()) {
                batch.draw(uiFullHeart, startX + (i * gap), startY, heartSize, heartSize);
            } else {
                batch.draw(uiEmptyHeart, startX + (i * gap), startY, heartSize, heartSize);
            }
        }

        if (player.hasExtraLife()) {
            batch.draw(uiExtraHeart, startX + (3 * gap), startY, heartSize, heartSize);
        }

        Assets.mainFont.draw(batch, "Комнаты: " + roomsClearedCount, Gdx.graphics.getWidth() / 2f - 60f, Gdx.graphics.getHeight() - 20f);

        int minutes = (int) (sessionTimeSeconds / 60);
        int seconds = (int) (sessionTimeSeconds % 60);
        Assets.mainFont.draw(batch, String.format("Session Time: %02d:%02d", minutes, seconds), Gdx.graphics.getWidth() / 2f - 60f, Gdx.graphics.getHeight() - 45f);

        batch.end();

        stageUI.act(delta);
        stageUI.draw();
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
        world.step(1 / 60f, 6, 2);
        sessionSecondsUpdate(dt);

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
            if (!anyAlive) {
                roomCleared = true;
                powerUps.clear();
                roomsClearedCount++;
            }
        }

        if (roomCleared && nextRoom == null) {
            float nx = currentRoom.position.x + roomW + corridorGap;
            nextRoom = levelManager.createRoom(nx, 0f, roomW, roomH, false);
            spawnZombies(nextRoom);
            createCorridorPhysics();
        }

        if (nextRoom != null) {
            float playerX = player.body.getPosition().x * B2DVars.PPM;
            if (playerX > nextRoom.position.x + 64f) { // Исправлена проверка триггера перехода
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

    private void sessionSecondsUpdate(float dt) {
        sessionTimeSeconds += dt;
    }

    private void applyPowerUp(PowerUp p) {
        switch (p.type) {
            case HEAL:
                player.heal();
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
                break;
        }
    }

    private void createCorridorPhysics() {
        float startX = currentRoom.position.x + roomW;
        float endX = nextRoom.position.x;
        float yBottom = roomH / 2 - tileSize;
        float yTop = roomH / 2 + tileSize + tileSize;

        currentRoom.bodies.add(bodyFactory.createRect(startX + (endX - startX) / 2, yBottom - 5, (endX - startX), 10, true, 0, 0, "wall_low"));
        currentRoom.bodies.add(bodyFactory.createRect(startX + (endX - startX) / 2, yTop + 5, (endX - startX), 10, true, 0, 0, "wall_up"));
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

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(false, width, height);
        UIResize(width, height);
    }

    private void UIResize(int w, int h) {
        stageUI.getViewport().update(w, h, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        world.dispose();
        player.dispose();
        if (stageUI != null) stageUI.dispose();
    }
}