package com.bevrfarlbt.NoExit.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.B2DVars;
import com.bevrfarlbt.NoExit.MyGdxGame;
import com.bevrfarlbt.NoExit.Settings;
import com.bevrfarlbt.NoExit.managers.BodyFactory;
import com.bevrfarlbt.NoExit.managers.LevelManager;
import com.bevrfarlbt.NoExit.renderers.LevelRenderer;
import com.bevrfarlbt.NoExit.objects.Player;
import com.bevrfarlbt.NoExit.objects.EnemyZombie;
import com.bevrfarlbt.NoExit.objects.ZombieRunner;
import com.bevrfarlbt.NoExit.objects.ZombieFat;
import com.bevrfarlbt.NoExit.objects.PowerUp;
import com.bevrfarlbt.NoExit.objects.Turret;
import com.bevrfarlbt.NoExit.data.RoomData;
import com.bevrfarlbt.NoExit.managers.AttackListener;
import com.bevrfarlbt.NoExit.managers.ShopManager; // Импорт менеджера магазина

public class PlayScreen implements Screen {
    private final MyGdxGame game;
    private final SpriteBatch batch;
    private World world;
    private OrthographicCamera cam, uiCam;
    private Viewport gameViewport, uiViewport;

    private Player player;

    private LevelManager levelManager;
    private LevelRenderer levelRenderer;
    private BodyFactory bodyFactory;

    private Array<EnemyZombie> zombies;
    private Array<PowerUp> powerUps;
    private Array<Turret> turrets;
    private RoomData currentRoom, nextRoom;
    private float roomW = 1280f, roomH = 720f, corridorGap = 350f, tileSize = 64f;
    private boolean roomCleared = false;

    private int roomsClearedCount = 0;
    private float sessionTimeSeconds = 0f;

    private TextureRegion uiFullHeart, uiEmptyHeart, uiExtraHeart;
    private float oneShotTimer = 0f;

    private Stage stageUI;
    private Skin skin;
    private Touchpad moveTouchpad, aimTouchpad;

    private Table gameOverTable;
    private boolean isGameOver = false;

    private boolean wasAimingLastFrame = false;

    private int currentTrackIndex = 0;
    private Music currentMusic = null;

    public PlayScreen(MyGdxGame game) {
        this.game = game;
        this.batch = game.batch;
    }

    @Override
    public void show() {
        world = new World(new Vector2(0, 0), true);

        cam = new OrthographicCamera();
        gameViewport = new ExtendViewport(MyGdxGame.SCR_WIDTH, MyGdxGame.SCR_HEIGHT, cam);

        uiCam = new OrthographicCamera();
        uiViewport = new ExtendViewport(MyGdxGame.SCR_WIDTH, MyGdxGame.SCR_HEIGHT, uiCam);

        bodyFactory = new BodyFactory(world);
        levelManager = new LevelManager(world);
        levelRenderer = new LevelRenderer();
        zombies = new Array<>();
        powerUps = new Array<>();
        turrets = new Array<>();

        currentRoom = levelManager.createRoom(0, 0, roomW, roomH, true);
        player = new Player(world, 200, roomH / 2);
        world.setContactListener(new AttackListener(player));

        uiFullHeart = Assets.fullHeart;
        uiEmptyHeart = Assets.emptyHeart;
        uiExtraHeart = Assets.extraHeart;

        stageUI = new Stage(uiViewport, batch);
        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(stageUI);
        Gdx.input.setInputProcessor(multiplexer);

        createSkin();
        createUI();
        spawnZombies(currentRoom);

        if (Assets.gameTracks.size > 0 && Settings.musicGameEnabled) {
            try {
                currentTrackIndex = MathUtils.random(0, Assets.gameTracks.size - 1);
                currentMusic = Assets.gameTracks.get(currentTrackIndex);
                currentMusic.setVolume(0.4f);
                currentMusic.play();
            } catch (Exception e) {
                Gdx.app.log("AUDIO", "Ошибка запуска музыки: " + e.getMessage());
            }
        }
    }

    private void createSkin() {
        skin = new Skin();
        skin.add("default", Assets.mainFont);

        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        com.badlogic.gdx.graphics.Texture texture = new com.badlogic.gdx.graphics.Texture(pixmap);
        skin.add("white_pixel", texture);
        pixmap.dispose();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.getFont("default");
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.GRAY;
        style.up = skin.newDrawable("white_pixel", new Color(0.2f, 0.2f, 0.2f, 0.8f));
        style.down = skin.newDrawable("white_pixel", Color.BLACK);

        skin.add("default", style);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = Assets.mainFont;
        labelStyle.fontColor = Color.RED;
        skin.add("dieStyle", labelStyle);
    }

    private void createUI() {
        Table topTable = new Table();
        topTable.setFillParent(true);
        stageUI.addActor(topTable);

        TextButton menuButton = new TextButton("Меню", skin);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Assets.menuSound != null && Settings.musicMenuEnabled) Assets.menuSound.play();
                if (currentMusic != null) {
                    try { currentMusic.stop(); } catch (Exception ignored) {}
                }
                game.setScreen(new MenuScreen(game));
            }
        });
        topTable.add(menuButton).size(120, 50).expand().top().right().pad(15);

        Touchpad.TouchpadStyle touchStyle = new Touchpad.TouchpadStyle();
        touchStyle.background = new TextureRegionDrawable(Assets.joystickBg);
        touchStyle.knob = new TextureRegionDrawable(Assets.joystickKnob);

        moveTouchpad = new Touchpad(10, touchStyle);
        stageUI.addActor(moveTouchpad);

        aimTouchpad = new Touchpad(10, touchStyle);
        stageUI.addActor(aimTouchpad);

        gameOverTable = new Table();
        gameOverTable.setFillParent(true);
        gameOverTable.setVisible(false);
        gameOverTable.setBackground(skin.newDrawable("white_pixel", new Color(0, 0, 0, 0.7f)));
        stageUI.addActor(gameOverTable);

        Label dieLabel = new Label("ВЫ ПОГИБЛИ!", skin, "dieStyle");
        TextButton restartBtn = new TextButton("Играть снова", skin);
        TextButton backToMenuBtn = new TextButton("Выйти в меню", skin);

        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Assets.menuSound != null && Settings.musicMenuEnabled) Assets.menuSound.play();
                if (currentMusic != null) {
                    try { currentMusic.stop(); } catch (Exception ignored) {}
                }
                game.setScreen(new PlayScreen(game));
            }
        });

        backToMenuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Assets.menuSound != null && Settings.musicMenuEnabled) Assets.menuSound.play();
                if (currentMusic != null) {
                    try { currentMusic.stop(); } catch (Exception ignored) {}
                }
                game.setScreen(new MenuScreen(game));
            }
        });

        gameOverTable.add(dieLabel).padBottom(40).row();
        gameOverTable.add(restartBtn).size(250, 60).padBottom(15).row();
        gameOverTable.add(backToMenuBtn).size(250, 60);
    }

    private void handleInput(float dt) {
        if (isGameOver) return;

        // Проверяем нажатие на цифру 1 для установки турели из общего инвентаря сохранения
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            if (ShopManager.useTurretFromInventory()) {
                float pX = player.body.getPosition().x * B2DVars.PPM;
                float pY = player.body.getPosition().y * B2DVars.PPM;
                turrets.add(new Turret(pX, pY));
            }
        }

        Vector2 move = new Vector2(moveTouchpad.getKnobPercentX(), moveTouchpad.getKnobPercentY());
        if (move.len() < 0.1f) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) move.y += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) move.y -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) move.x -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) move.x += 1;
            move.nor();
        }
        player.handleInput(move);

        Vector2 aim = new Vector2(aimTouchpad.getKnobPercentX(), aimTouchpad.getKnobPercentY());
        float deadzone = 0.25f;

        if (aim.len() > deadzone) {
            player.setLookDirection(aim);
            player.setAiming(true);
            wasAimingLastFrame = true;
        } else {
            player.setAiming(false);
            if (wasAimingLastFrame) {
                if (player.canAttack()) {
                    player.attack();
                    handleCombat();
                }
                wasAimingLastFrame = false;
            }
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
                float angleDeg = MathUtils.radiansToDegrees * (float) Math.acos(MathUtils.clamp(dotProduct, -1f, 1f));

                if (angleDeg < player.getAttackAngleRange() / 2f) {
                    z.takeDamage(1);

                    if (Assets.hitSound != null && Settings.soundHitEnabled) {
                        Assets.hitSound.play(0.8f);
                    }

                    z.applyStun(0.3f);
                    z.body.setLinearVelocity(toZ.cpy().nor().scl(6f));
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameViewport.apply();
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

        for (Turret t : turrets) t.draw(batch);

        for (EnemyZombie z : zombies) z.draw(batch);
        for (PowerUp p : powerUps) p.draw(batch);
        player.draw(batch);
        batch.end();

        player.drawDebugAttack(cam.combined);

        uiViewport.apply();
        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();
        renderUIElements();
        batch.end();

        stageUI.act(delta);
        stageUI.draw();
    }

    private void renderUIElements() {
        float heartSize = 32f, startX = 20f, startY = uiViewport.getWorldHeight() - 52f, gap = 40f;
        for (int i = 0; i < player.getMaxLives(); i++) {
            batch.draw(i < player.getLives() ? uiFullHeart : uiEmptyHeart, startX + (i * gap), startY, heartSize, heartSize);
        }
        if (player.hasExtraLife()) batch.draw(uiExtraHeart, startX + (3 * gap), startY, heartSize, heartSize);

        float worldCenterX = uiViewport.getWorldWidth() / 2f;
        float worldTopY = uiViewport.getWorldHeight();

        Assets.mainFont.draw(batch, "Комнаты: " + roomsClearedCount, worldCenterX - 60f, worldTopY - 20f);
        int minutes = (int) (sessionTimeSeconds / 60);
        int seconds = (int) (sessionTimeSeconds % 60);
        Assets.mainFont.draw(batch, String.format("Время: %02d:%02d", minutes, seconds), worldCenterX - 60f, worldTopY - 45f);

        // Отображаем количество монет и турелей из ShopManager
        Assets.mainFont.draw(batch, "Монеты: " + ShopManager.getCoins(), 20f, startY - 20f);
        Assets.mainFont.draw(batch, "Турели [1]: " + ShopManager.getTurretInventory(), 20f, startY - 45f);
    }

    private void update(float dt) {
        if (!isGameOver) {
            world.step(1 / 60f, 6, 2);
            sessionTimeSeconds += dt;
            if (oneShotTimer > 0) oneShotTimer -= dt;

            if (Settings.musicGameEnabled) {
                if (currentMusic != null && !currentMusic.isPlaying()) {
                    try {
                        currentTrackIndex = (currentTrackIndex + 1) % Assets.gameTracks.size;
                        currentMusic = Assets.gameTracks.get(currentTrackIndex);
                        currentMusic.setVolume(0.4f);
                        currentMusic.play();
                    } catch (Exception ignored) {}
                }
            } else {
                if (currentMusic != null && currentMusic.isPlaying()) {
                    try { currentMusic.stop(); } catch (Exception ignored) {}
                }
            }

            handleRoomLogic();
            handleInput(dt);
            player.update(dt);
            handlePowerUps(dt);

            for (int i = turrets.size - 1; i >= 0; i--) {
                Turret t = turrets.get(i);
                t.update(dt, zombies);
                if (t.isDestroyed) {
                    turrets.removeIndex(i);
                }
            }

            for (EnemyZombie z : zombies) z.update(dt, player.body.getPosition(), player, turrets);

            if (player.getLives() <= 0) {
                isGameOver = true;
                if (currentMusic != null) {
                    try { currentMusic.stop(); } catch (Exception ignored) {}
                }
                gameOverTable.setVisible(true);
                moveTouchpad.setVisible(false);
                aimTouchpad.setVisible(false);
            }
        }

        float camX = Math.max(player.body.getPosition().x * B2DVars.PPM, gameViewport.getWorldWidth() / 2f);
        cam.position.lerp(new Vector3(camX, roomH / 2, 0), 0.1f);
        cam.update();
    }

    private void handleRoomLogic() {
        if (!roomCleared) {
            boolean anyAlive = false;
            for (EnemyZombie z : zombies) {
                if (z.isDead && z.getPowerUp() == null) {
                    PowerUp p = z.trySpawnPowerUp();
                    if (p != null) powerUps.add(p);
                }
                if (!z.isDead) anyAlive = true;
            }
            if (!anyAlive) {
                roomCleared = true;
                powerUps.clear();
                roomsClearedCount++;
                ShopManager.addCoins(1); // Начисляем 1 монету за зачистку комнаты
            }
        }
        if (roomCleared && nextRoom == null) {
            nextRoom = levelManager.createRoom(currentRoom.position.x + roomW + corridorGap, 0f, roomW, roomH, false);
            spawnZombies(nextRoom);
            createCorridorPhysics();
        }
        if (nextRoom != null && player.body.getPosition().x * B2DVars.PPM > nextRoom.position.x + 64f) {
            cleanupZombies();
            currentRoom.destroy(world);
            currentRoom = nextRoom;
            nextRoom = null;
            roomCleared = false;
            turrets.clear();
        }
    }

    private void handlePowerUps(float dt) {
        Vector2 pPos = player.body.getPosition().cpy().scl(B2DVars.PPM);
        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUp p = powerUps.get(i);
            p.update(dt);
            if (pPos.dst(p.basePosition) < 40f) {
                applyPowerUp(p);
                powerUps.removeIndex(i);
            }
        }
    }

    private void applyPowerUp(PowerUp p) {
        if (Assets.powerupSound != null && Settings.soundHitEnabled) {
            Assets.powerupSound.play(0.7f);
        }
        switch (p.type) {
            case HEAL: player.heal(); break;
            case SHIELD: player.addExtraLife(); break;
            case ONE_SHOT:
                oneShotTimer = 5f;
                for (EnemyZombie z : zombies) if (!z.isDead) z.takeDamage(10);
                break;
        }
    }

    private void drawCorridor() {
        float startX = currentRoom.position.x + roomW, endX = nextRoom.position.x, centerY = roomH / 2f;
        for (float x = startX; x < endX; x += tileSize) {
            for (int i = -1; i <= 1; i++) batch.draw(Assets.floorDefault, x, centerY + (i * tileSize), tileSize, tileSize);
            batch.draw(Assets.upWall, x, centerY + tileSize * 2, tileSize, tileSize);
            batch.draw(Assets.downWall, x, centerY - tileSize * 2, tileSize, tileSize);
        }
    }

    private void createCorridorPhysics() {
        float startX = currentRoom.position.x + roomW, endX = nextRoom.position.x;
        currentRoom.bodies.add(bodyFactory.createRect(startX + (endX - startX) / 2, roomH / 2 - tileSize - 5, (endX - startX), 10, true, 0, 0, "wall_low"));
        currentRoom.bodies.add(bodyFactory.createRect(startX + (endX - startX) / 2, roomH / 2 + tileSize * 2 + 5, (endX - startX), 10, true, 0, 0, "wall_up"));
    }

    private void spawnZombies(RoomData room) {
        int num = MathUtils.random(5, 11);
        for (int i = 0; i < num; i++) {
            float spawnX = room.position.x + MathUtils.random(100, roomW - 100);
            float spawnY = room.position.y + MathUtils.random(100, roomH - 100);

            float rand = MathUtils.random();

            if (rand <= 0.20f) {
                zombies.add(new ZombieFat(bodyFactory, spawnX, spawnY));
            } else if (rand <= 0.50f) {
                zombies.add(new ZombieRunner(bodyFactory, spawnX, spawnY));
            } else {
                zombies.add(new EnemyZombie(bodyFactory, spawnX, spawnY));
            }
        }
    }

    private void cleanupZombies() {
        for (int i = zombies.size - 1; i >= 0; i--) {
            EnemyZombie z = zombies.get(i);
            if (z.isDead || (z.body != null && z.body.getPosition().x * B2DVars.PPM < currentRoom.position.x + roomW)) {
                if (z.body != null) world.destroyBody(z.body);
                zombies.removeIndex(i);
            }
        }
        powerUps.clear();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, false);
        uiViewport.update(width, height, true);
        stageUI.getViewport().update(width, height, true);

        float uiWidth = uiViewport.getWorldWidth();
        moveTouchpad.setBounds(50, 50, 200, 200);
        aimTouchpad.setBounds(uiWidth - 250, 50, 200, 200);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        if (currentMusic != null) {
            try { currentMusic.stop(); } catch (Exception ignored) {}
        }
    }

    @Override public void dispose() {
        if (currentMusic != null) {
            try { currentMusic.stop(); } catch (Exception ignored) {}
        }
        world.dispose();
        player.dispose();
        if (stageUI != null) stageUI.dispose();
    }
}