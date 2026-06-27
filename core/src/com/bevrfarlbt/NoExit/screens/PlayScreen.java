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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
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
import com.bevrfarlbt.NoExit.managers.ShopManager;
import com.bevrfarlbt.NoExit.managers.TutorialManager;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.physics.box2d.Body;
import com.bevrfarlbt.NoExit.managers.SaveManager;
import com.bevrfarlbt.NoExit.data.Document;
import com.bevrfarlbt.NoExit.managers.DocumentManager;


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
    private int roomLevel = 1;
    private float sessionTimeSeconds = 0f;

    private TextureRegion uiFullHeart, uiEmptyHeart, uiExtraHeart;
    private float oneShotTimer = 0f;

    private Stage stageUI;
    private Skin skin;
    private Touchpad moveTouchpad, aimTouchpad;

    private Table gameOverTable;
    private boolean isGameOver = false;
    private boolean isPaused = false;
    private Table pauseTable;
    private Table documentTable;
    private Label documentTitle;
    private Label documentText;
    private boolean documentOpened = false;

    private boolean wasAimingLastFrame = false;

    private int currentTrackIndex = 0;
    private Music currentMusic = null;

    private GlyphLayout tutorialLayout;

    private Body exitBlocker;
    private Body entranceBlocker;
    private TextButton turretButton;

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
        createExitBlocker();
        player = new Player(world, 200, roomH / 2);
        world.setContactListener(new AttackListener(player));

        uiFullHeart = Assets.fullHeart;
        uiEmptyHeart = Assets.emptyHeart;
        uiExtraHeart = Assets.extraHeart;

        stageUI = new Stage(uiViewport, batch);
        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(stageUI);
        Gdx.input.setInputProcessor(multiplexer);

        tutorialLayout = new GlyphLayout();

        if (!Settings.tutorialCompleted) {
            TutorialManager.reset();
            if (ShopManager.getTurretInventory() == 0) {
                ShopManager.addTurretsToInventory(1);
            }
        }

        createSkin();
        createUI();
        if (SaveManager.hasSave()) {

            roomsClearedCount = SaveManager.getRooms();
            roomLevel = SaveManager.getRoomLevel();

            sessionTimeSeconds = SaveManager.getSessionTime();

            player.setLives(
                    SaveManager.getLives());

            if (SaveManager.hasExtraLifeSaved()) {
                player.addExtraLife();
            }
            if (roomsClearedCount > 0) {
                createEntranceBlocker();
            }
        }

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

    private void createExitBlocker() {
        exitBlocker = bodyFactory.createRect(currentRoom.position.x + roomW - tileSize / 2f,
                roomH / 2f, tileSize, tileSize * 3f, true, 0, 0, "exit_blocker");
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

        Label.LabelStyle defaultLabelStyle = new Label.LabelStyle();
        defaultLabelStyle.font = Assets.mainFont;
        defaultLabelStyle.fontColor = Color.WHITE;
        skin.add("default", defaultLabelStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = Assets.mainFont;
        labelStyle.fontColor = Color.RED;
        skin.add("dieStyle", labelStyle);
    }

    private void createUI() {
        Table topTable = new Table();
        topTable.setFillParent(true);
        stageUI.addActor(topTable);

        TextButton pauseButton = new TextButton("Пауза", skin);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isPaused = !isPaused;
                pauseTable.setVisible(isPaused);

                moveTouchpad.setVisible(!isPaused);
                aimTouchpad.setVisible(!isPaused);
            }
        });
        topTable.add(pauseButton).size(120, 50).expand().top().right().pad(15);

        Touchpad.TouchpadStyle touchStyle = new Touchpad.TouchpadStyle();
        touchStyle.background = new TextureRegionDrawable(Assets.joystickBg);
        touchStyle.knob = new TextureRegionDrawable(Assets.joystickKnob);

        moveTouchpad = new Touchpad(10, touchStyle);
        stageUI.addActor(moveTouchpad);

        aimTouchpad = new Touchpad(10, touchStyle);
        stageUI.addActor(aimTouchpad);

        turretButton = new TextButton("Турель", skin);

        turretButton.setSize(180, 70);

        float buttonX = aimTouchpad.getX() + 10;
        float buttonY = aimTouchpad.getY() + aimTouchpad.getHeight() + 20;

        turretButton.setPosition(buttonX, buttonY);

        turretButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (ShopManager.useTurretFromInventory()) {
                    float pX = player.body.getPosition().x * B2DVars.PPM;
                    float pY = player.body.getPosition().y * B2DVars.PPM;
                    turrets.add(new Turret(pX, pY));

                    if (!Settings.tutorialCompleted
                            && TutorialManager.getCurrentStep() == TutorialManager.Step.PLACE_TURRET) {
                        TutorialManager.nextStep();
                        Settings.tutorialCompleted = true;
                        Settings.save();
                    }
                }
            }
        });

        stageUI.addActor(turretButton);

        gameOverTable = new Table();
        gameOverTable.setFillParent(true);
        gameOverTable.setVisible(false);
        gameOverTable.setBackground(skin.newDrawable("white_pixel", new Color(0, 0, 0, 0.7f)));
        stageUI.addActor(gameOverTable);

        Label dieLabel = new Label("ВЫ ПОГИБЛИ!", skin, "dieStyle");
        TextButton restartBtn = new TextButton("Играть снова", skin);
        TextButton backToMenuBtn = new TextButton("Выйти в меню", skin);

        pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.setVisible(false);
        pauseTable.setBackground(
                skin.newDrawable("white_pixel",
                        new Color(0, 0, 0, 0.7f)));

        stageUI.addActor(pauseTable);

        Label pauseLabel = new Label("ПАУЗА", skin);

        TextButton resumeBtn = new TextButton("Продолжить", skin);
        TextButton menuBtn = new TextButton("В меню", skin);

        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isPaused = false;
                pauseTable.setVisible(false);

                moveTouchpad.setVisible(true);
                aimTouchpad.setVisible(true);
            }
        });

        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentMusic != null) {
                    try {
                        currentMusic.stop();
                    } catch (Exception ignored) {}
                }
                saveProgress();

                game.setScreen(new MenuScreen(game));
            }
        });

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

        pauseTable.add(pauseLabel).padBottom(40).row();
        pauseTable.add(resumeBtn).size(250, 60).padBottom(15).row();
        pauseTable.add(menuBtn).size(250, 60);

        documentTable = new Table();
        documentTable.setFillParent(true);
        documentTable.setVisible(false);
        documentTable.setBackground(skin.newDrawable("white_pixel", new Color(0, 0, 0, 0.85f)));
        stageUI.addActor(documentTable);

        Label.LabelStyle documentTitleStyle = new Label.LabelStyle();
        documentTitleStyle.font = Assets.mainFont;
        documentTitleStyle.fontColor = new Color(0.22f, 0.12f, 0.05f, 1f);

        Label.LabelStyle documentTextStyle = new Label.LabelStyle();
        documentTextStyle.font = Assets.mainFont;
        documentTextStyle.fontColor = new Color(0.18f, 0.10f, 0.05f, 1f);

        documentTitle = new Label("", documentTitleStyle);
        documentTitle.setWrap(true);
        documentTitle.setAlignment(1);

        documentText = new Label("", documentTextStyle);
        documentText.setWrap(true);
        documentText.setAlignment(0);

        Table documentContent = new Table();
        documentContent.top();
        documentContent.left();

        documentContent.add(documentTitle)
                .width(850)
                .padBottom(0)
                .center()
                .row();

        documentContent.add(documentText)
                .width(850)
                .left()
                .top()
                .row();

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        ScrollPane documentScrollPane = new ScrollPane(documentContent, scrollStyle);
        documentScrollPane.setFadeScrollBars(false);
        documentScrollPane.setScrollingDisabled(true, false);

        Table paperTable = new Table();
        paperTable.setBackground(new TextureRegionDrawable(Assets.documentViewBg));

        paperTable.padTop(220);
        paperTable.padBottom(90);
        paperTable.padLeft(120);
        paperTable.padRight(120);

        paperTable.add(documentScrollPane)
                .width(850)
                .height(230)
                .top()
                .left();

        TextButton closeDocButton = new TextButton("Закрыть", skin);

        closeDocButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                documentOpened = false;
                documentTable.setVisible(false);
                moveTouchpad.setVisible(true);
                aimTouchpad.setVisible(true);
            }
        });

        Table popupTable = new Table();
        popupTable.center();

        popupTable.add(paperTable)
                .width(1250)
                .height(560)
                .row();

        popupTable.add(closeDocButton)
                .size(220, 60)
                .padTop(20);

        documentTable.add(popupTable).center();
    }

    private void handleInput(float dt) {
        if (isGameOver) return;
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

        if (!Settings.tutorialCompleted && TutorialManager.getCurrentStep() == TutorialManager.Step.MOVE && move.len() > 0.2f) {
            TutorialManager.nextStep();
        }

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

        if (entranceBlocker != null) {
            batch.setColor(0, 0, 0, 0.8f);
            batch.draw(
                    Assets.leftRightWall,
                    currentRoom.position.x,
                    roomH / 2 - tileSize,
                    tileSize,
                    tileSize * 3
            );
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

    private void drawTextWithShadow(String text, float x, float y, Color color) {
        Assets.mainFont.setColor(0, 0, 0, 0.9f);
        Assets.mainFont.draw(batch, text, x + 3f, y - 3f);

        Assets.mainFont.setColor(color);
        Assets.mainFont.draw(batch, text, x, y);

        Assets.mainFont.setColor(Color.WHITE);
    }

    private void renderUIElements() {
        float heartSize = 32f;
        float startX = 20f;
        float startY = uiViewport.getWorldHeight() - 52f;
        float gap = 40f;

        for (int i = 0; i < player.getMaxLives(); i++) {
            TextureRegion heart = i < player.getLives() ? uiFullHeart : uiEmptyHeart;
            batch.draw(heart, startX + (i * gap), startY, heartSize, heartSize);
        }

        if (player.hasExtraLife()) {
            batch.draw(uiExtraHeart, startX + (3 * gap), startY, heartSize, heartSize);
        }

        float worldCenterX = uiViewport.getWorldWidth() / 2f;
        float worldTopY = uiViewport.getWorldHeight();

        drawTextWithShadow(
                "Комната: " + roomLevel,
                worldCenterX - 60f,
                worldTopY - 20f,
                Color.WHITE
        );

        int minutes = (int) (sessionTimeSeconds / 60);
        int seconds = (int) (sessionTimeSeconds % 60);

        drawTextWithShadow(
                String.format("Время: %02d:%02d", minutes, seconds),
                worldCenterX - 60f,
                worldTopY - 45f,
                Color.WHITE
        );

        float coinSize = 30f;
        float coinX = 20f;
        float coinY = startY - 50f;

        batch.draw(Assets.coinIcon, coinX, coinY, coinSize, coinSize);

        drawTextWithShadow(
                String.valueOf(ShopManager.getCoins()),
                coinX + coinSize + 8f,
                coinY + 24f,
                Color.WHITE
        );

        float turretSize = 32f;
        float turretX = 20f;
        float turretY = coinY - 45f;

        batch.draw(Assets.turretIcon, turretX, turretY, turretSize, turretSize);

        drawTextWithShadow(
                "[1] " + ShopManager.getTurretInventory(),
                turretX + turretSize + 8f,
                turretY + 25f,
                Color.WHITE
        );

        if (!Settings.tutorialCompleted
                && TutorialManager.getCurrentStep() == TutorialManager.Step.PLACE_TURRET) {
            drawTextWithShadow(
                    "Нажмите сюда\nдля установки турели",
                    uiViewport.getWorldWidth() - 500,
                    380,
                    Color.WHITE
            );
        }

        if (!Settings.tutorialCompleted) {
            String tutorialText = TutorialManager.getCurrentText();

            float tutorialX = uiViewport.getWorldWidth() - 520;
            float tutorialY = uiViewport.getWorldHeight() - 140;

            drawTextWithShadow(
                    tutorialText,
                    tutorialX,
                    tutorialY,
                    Color.WHITE
            );
        }
    }

    private void update(float dt) {
        if (!isGameOver && !isPaused && !documentOpened) {
            world.step(1 / 60f, 6, 2);
            sessionTimeSeconds += dt;

            if (oneShotTimer > 0) {
                oneShotTimer -= dt;
            }

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
                    try {
                        currentMusic.stop();
                    } catch (Exception ignored) {}
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

            for (EnemyZombie z : zombies) {
                z.update(dt, player.body.getPosition(), player, turrets);
            }

            if (player.getLives() <= 0) {
                SaveManager.deleteSave();

                isGameOver = true;

                if (currentMusic != null) {
                    try {
                        currentMusic.stop();
                    } catch (Exception ignored) {}
                }

                gameOverTable.setVisible(true);
                moveTouchpad.setVisible(false);
                aimTouchpad.setVisible(false);
            }
        }

        if (turretButton != null) {
            turretButton.setDisabled(ShopManager.getTurretInventory() <= 0);
            turretButton.setText("Турель (" + ShopManager.getTurretInventory() + ")");
        }

        float camX = Math.max(
                player.body.getPosition().x * B2DVars.PPM,
                gameViewport.getWorldWidth() / 2f
        );

        cam.position.lerp(new Vector3(camX, roomH / 2, 0), 0.1f);
        cam.update();
    }

    private void handleRoomLogic() {
        if (!roomCleared) {
            boolean anyAlive = false;
            for (EnemyZombie z : zombies) {
                if (z.isDead && !z.hasRolledPowerUp()) {
                    PowerUp p = null;
                    if (!Settings.tutorialCompleted && TutorialManager.getCurrentStep() == TutorialManager.Step.KILL_ZOMBIE && !TutorialManager.hasSpawnedTutorialPowerUp()) {
                        p = z.forceSpawnPowerUp();
                        TutorialManager.setTutorialPowerUpSpawned(true);
                        TutorialManager.nextStep();
                    } else {
                        p = z.trySpawnPowerUp();
                    }
                    if (p != null) {
                        powerUps.add(p);
                    }
                }
                if (!z.isDead) anyAlive = true;
            }
            if (!anyAlive) {
                if (exitBlocker != null) {
                    world.destroyBody(exitBlocker);
                    exitBlocker = null;
                }
                roomCleared = true;
                powerUps.clear();
                roomsClearedCount++;
                ShopManager.addCoins(1);
                roomLevel++;
                if (DocumentManager.allDocumentsCollected()) {
                    if (!DocumentManager.isCollected(41)) {
                        Document epilogue = DocumentManager.getEpilogue();
                        DocumentManager.markAsCollected(epilogue);
                        showDocument(epilogue.title, epilogue.text);
                    }
                } else {
                    if (MathUtils.randomBoolean(0.40f)) {
                        int chapter = DocumentManager.getCurrentChapter();
                        Document doc = DocumentManager.getRandomDocument(chapter);
                        if (doc != null) {
                            DocumentManager.markAsCollected(doc);
                            showDocument(doc.title, doc.text);
                        }
                    }
                }
            }
        }
        if (roomCleared && nextRoom == null) {
            nextRoom = levelManager.createRoom(currentRoom.position.x + roomW + corridorGap, 0f, roomW, roomH, false);
            spawnZombies(nextRoom);
            createCorridorPhysics();
        }
        if (nextRoom != null && player.body.getPosition().x * B2DVars.PPM > nextRoom.position.x + 64f) {
            cleanupZombies();

            if (entranceBlocker != null) {
                try {
                    world.destroyBody(entranceBlocker);
                } catch (Exception ignored) {}
                entranceBlocker = null;
            }

            currentRoom.destroy(world);

            currentRoom = nextRoom;
            nextRoom = null;
            roomCleared = false;

            createEntranceBlocker();
            createExitBlocker();

            turrets.clear();
            saveProgress();
        }
    }

    private void handlePowerUps(float dt) {
        Vector2 pPos = player.body.getPosition().cpy().scl(B2DVars.PPM);
        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUp p = powerUps.get(i);
            p.update(dt);
            if (pPos.dst(p.basePosition) < 40f) {
                applyPowerUp(p);
                if (!Settings.tutorialCompleted && TutorialManager.getCurrentStep() == TutorialManager.Step.PICK_POWERUP) {
                    TutorialManager.nextStep();
                }
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

    private void createEntranceBlocker() {
        if (entranceBlocker != null) {
            try {
                world.destroyBody(entranceBlocker);
            } catch (Exception ignored) {}
            entranceBlocker = null;
        }

        entranceBlocker = bodyFactory.createRect(
                currentRoom.position.x + tileSize / 2f,
                roomH / 2f,
                tileSize,
                tileSize * 3f,
                true,
                0,
                0,
                "entrance_blocker"
        );
    }

    private void saveProgress() {
        SaveManager.save(
                roomsClearedCount,
                roomLevel,
                ShopManager.getCoins(),
                ShopManager.getTurretInventory(),
                player.getLives(),
                player.hasExtraLife(),
                sessionTimeSeconds
        );
    }

    private void showDocument(String title, String text) {
        documentOpened = true;
        documentTitle.setText(title);
        documentText.setText(text);
        documentTable.setVisible(true);
        moveTouchpad.setVisible(false);
        aimTouchpad.setVisible(false);
    }

    private void spawnZombies(RoomData room) {
        int budget = 3 + roomLevel * 2;
        while (budget > 0) {
            float spawnX = room.position.x + MathUtils.random(100, roomW - 100);
            float spawnY = room.position.y + MathUtils.random(100, roomH - 100);
            if (roomLevel <= 3) {
                zombies.add(new EnemyZombie(bodyFactory, spawnX, spawnY));
                budget -= 1;
                continue;
            }
            if (roomLevel <= 6) {
                float roll = MathUtils.random();
                if (roll < 0.25f && budget >= 2) {
                    zombies.add(new ZombieRunner(bodyFactory, spawnX, spawnY));
                    budget -= 2;
                } else {
                    zombies.add(new EnemyZombie(bodyFactory, spawnX, spawnY));
                    budget -= 1;
                }
                continue;
            }
            float roll = MathUtils.random();
            if (roll < 0.15f && budget >= 4) {
                zombies.add(new ZombieFat(bodyFactory, spawnX, spawnY));
                budget -= 4;
            } else if (roll < 0.45f && budget >= 2) {
                zombies.add(new ZombieRunner(bodyFactory, spawnX, spawnY));
                budget -= 2;
            } else {
                zombies.add(new EnemyZombie(bodyFactory, spawnX, spawnY));
                budget -= 1;
            }
        }
    }

    private int getCurrentChapter() {
        if (roomsClearedCount < 8) {
            return 1;
        }
        if (roomsClearedCount < 16) {
            return 2;
        }
        if (roomsClearedCount < 24) {
            return 3;
        }
        if (roomsClearedCount < 32) {
            return 4;
        }
        return 5;
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

        if (turretButton != null) {
            turretButton.setBounds(uiWidth - 240, 270, 180, 70);
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        if (currentMusic != null) {
            try { currentMusic.stop(); } catch (Exception ignored) {}
        }
        if (!isGameOver) {
            saveProgress();
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