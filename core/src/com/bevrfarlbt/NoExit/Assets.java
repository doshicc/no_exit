package com.bevrfarlbt.NoExit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;

public class Assets {
    public static AssetManager manager = new AssetManager();

    public static TextureRegion floorDefault, upWall, downWall, leftRightWall, box, shelf;
    public static Array<TextureRegion> floorDetails = new Array<>();

    public static Animation<TextureRegion> playerIdle, playerWalk, playerAttack, playerDeath;

    public static Animation<TextureRegion> zombieDefaultIdle, zombieDefaultWalk, zombieDefaultAttack, zombieDefaultDeath;
    public static Animation<TextureRegion> zombieRunnerIdle, zombieRunnerWalk, zombieRunnerAttack, zombieRunnerDeath;
    public static Animation<TextureRegion> zombieFatIdle, zombieFatWalk, zombieFatAttack, zombieFatDeath;

    public static Animation<TextureRegion> turretIdle, turretAttack, turretDeath;

    public static TextureRegion emptyHeart, extraHeart, fullHeart;

    public static TextureRegion powerupHeal, powerupShield, powerupOneShot;

    public static TextureRegion joystickBg, joystickKnob;
    public static TextureRegion backArrow;
    public static TextureRegion coinIcon;
    public static TextureRegion turretIcon;

    public static BitmapFont mainFont;
    public static BitmapFont titleFont;

    public static Texture menuBackground;

    public static Sound hitSound;
    public static Sound powerupSound;
    public static Sound stepsSound;
    public static Sound zombieSound;

    public static Music menuSound;
    public static Array<Music> gameTracks = new Array<>();

    public static void load() {
        manager.load("level/floor/floor_default.png", Texture.class);
        manager.load("level/floor/floor1.png", Texture.class);
        manager.load("level/floor/floor2.png", Texture.class);
        manager.load("level/floor/floor3.png", Texture.class);
        manager.load("level/wall/upWall.png", Texture.class);
        manager.load("level/wall/downWall.png", Texture.class);
        manager.load("level/wall/leftRightWall.png", Texture.class);
        manager.load("level/objects/box.png", Texture.class);
        manager.load("level/objects/shelf.png", Texture.class);

        manager.load("player/hearts/extrahealth.png", Texture.class);
        manager.load("player/hearts/full.png", Texture.class);
        manager.load("player/hearts/null.png", Texture.class);

        manager.load("player/buff/powerup_heal.png", Texture.class);
        manager.load("player/buff/powerup_shield.png", Texture.class);
        manager.load("player/buff/powerup_oneshot.png", Texture.class);

        manager.load("player/joystick/joystick_bg.png", Texture.class);
        manager.load("player/joystick/joystick_knob.png", Texture.class);

        manager.load("ui/back_arrow.png", Texture.class);
        manager.load("ui/coin.png", Texture.class);
        manager.load("ui/turret.png", Texture.class);

        manager.load("Screen/MenuBackground.png", Texture.class);

        for (int i = 1; i <= 2; i++) manager.load("player/idle/idle" + i + ".png", Texture.class);
        for (int i = 1; i <= 12; i++) manager.load("player/walk/walk" + i + ".png", Texture.class);
        for (int i = 1; i <= 5; i++) manager.load("player/attack/attack" + i + ".png", Texture.class);
        for (int i = 1; i <= 5; i++) manager.load("player/death/death" + i + ".png", Texture.class);

        for (int i = 1; i <= 4; i++) manager.load("zombies/zombie_default/idle/idle" + i + ".png", Texture.class);
        for (int i = 1; i <= 10; i++) manager.load("zombies/zombie_default/walk/walk" + i + ".png", Texture.class);
        for (int i = 1; i <= 5; i++) manager.load("zombies/zombie_default/attack/attack" + i + ".png", Texture.class);
        for (int i = 1; i <= 3; i++) manager.load("zombies/zombie_default/death/death" + i + ".png", Texture.class);

        for (int i = 1; i <= 3; i++) manager.load("zombies/zombie_runner/idle/idle" + i + ".png", Texture.class);
        for (int i = 1; i <= 3; i++) manager.load("zombies/zombie_runner/walk/walk" + i + ".png", Texture.class);
        for (int i = 1; i <= 2; i++) manager.load("zombies/zombie_runner/attack/attack" + i + ".png", Texture.class);
        for (int i = 1; i <= 3; i++) manager.load("zombies/zombie_runner/death/death" + i + ".png", Texture.class);

        for (int i = 1; i <= 2; i++) manager.load("zombies/zombie_fat/idle/idle" + i + ".png", Texture.class);
        for (int i = 1; i <= 4; i++) manager.load("zombies/zombie_fat/walk/walk" + i + ".png", Texture.class);
        for (int i = 1; i <= 3; i++) manager.load("zombies/zombie_fat/attack/attack" + i + ".png", Texture.class);
        for (int i = 1; i <= 3; i++) manager.load("zombies/zombie_fat/death/death" + i + ".png", Texture.class);

        for (int i = 1; i <= 2; i++) manager.load("objects/turret/idle/idle" + i + ".png", Texture.class);
        for (int i = 1; i <= 4; i++) manager.load("objects/turret/attack/attack" + i + ".png", Texture.class);
        for (int i = 1; i <= 3; i++) manager.load("objects/turret/death/death" + i + ".png", Texture.class);

        manager.load("sounds/hit.mp3", Sound.class);
        manager.load("sounds/powerup.mp3", Sound.class);
        manager.load("sounds/zombie.mp3", Sound.class);
        manager.load("sounds/menuSound.mp3", Music.class);

        manager.load("sounds/track1.mp3", Music.class);
        manager.load("sounds/track2.mp3", Music.class);
        manager.load("sounds/track3.mp3", Music.class);
        manager.load("sounds/track4.mp3", Music.class);
    }

    public static void setup() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/uvKits.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.characters = parameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz" + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" + "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" + "0123456789" + " .,!?;:-_+=/\\()[]{}<>\"'№%&*";
        mainFont = generator.generateFont(parameter);

        FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParam.size = 72;
        titleParam.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz";
        titleFont = generator.generateFont(titleParam);
        generator.dispose();

        floorDefault = new TextureRegion(manager.get("level/floor/floor_default.png", Texture.class));
        floorDetails.add(new TextureRegion(manager.get("level/floor/floor1.png", Texture.class)));
        floorDetails.add(new TextureRegion(manager.get("level/floor/floor2.png", Texture.class)));
        floorDetails.add(new TextureRegion(manager.get("level/floor/floor3.png", Texture.class)));
        upWall = new TextureRegion(manager.get("level/wall/upWall.png", Texture.class));
        downWall = new TextureRegion(manager.get("level/wall/downWall.png", Texture.class));
        leftRightWall = new TextureRegion(manager.get("level/wall/leftRightWall.png", Texture.class));
        box = new TextureRegion(manager.get("level/objects/box.png", Texture.class));
        shelf = new TextureRegion(manager.get("level/objects/shelf.png", Texture.class));

        fullHeart = new TextureRegion(manager.get("player/hearts/full.png", Texture.class));
        emptyHeart = new TextureRegion(manager.get("player/hearts/null.png", Texture.class));
        extraHeart = new TextureRegion(manager.get("player/hearts/extrahealth.png", Texture.class));

        powerupHeal = new TextureRegion(manager.get("player/buff/powerup_heal.png", Texture.class));
        powerupShield = new TextureRegion(manager.get("player/buff/powerup_shield.png", Texture.class));
        powerupOneShot = new TextureRegion(manager.get("player/buff/powerup_oneshot.png", Texture.class));

        joystickBg = new TextureRegion(manager.get("player/joystick/joystick_bg.png", Texture.class));
        joystickKnob = new TextureRegion(manager.get("player/joystick/joystick_knob.png", Texture.class));

        backArrow = new TextureRegion(manager.get("ui/back_arrow.png", Texture.class));
        coinIcon = new TextureRegion(manager.get("ui/coin.png", Texture.class));
        turretIcon = new TextureRegion(manager.get("ui/turret.png", Texture.class));

        menuBackground = manager.get("Screen/MenuBackground.png", Texture.class);

        playerIdle = new Animation<>(0.3f, getFrames("player/idle/idle", 2), Animation.PlayMode.LOOP);
        playerWalk = new Animation<>(0.1f, getFrames("player/walk/walk", 12), Animation.PlayMode.LOOP);
        playerAttack = new Animation<>(0.07f, getFrames("player/attack/attack", 5), Animation.PlayMode.NORMAL);
        playerDeath = new Animation<>(0.25f, getFrames("player/death/death", 5), Animation.PlayMode.NORMAL);

        zombieDefaultIdle = new Animation<>(0.2f, getFrames("zombies/zombie_default/idle/idle", 4), Animation.PlayMode.LOOP);
        zombieDefaultWalk = new Animation<>(0.1f, getFrames("zombies/zombie_default/walk/walk", 10), Animation.PlayMode.LOOP);
        zombieDefaultAttack = new Animation<>(0.15f, getFrames("zombies/zombie_default/attack/attack", 5), Animation.PlayMode.LOOP);
        zombieDefaultDeath = new Animation<>(0.25f, getFrames("zombies/zombie_default/death/death", 3), Animation.PlayMode.NORMAL);

        zombieRunnerIdle = new Animation<>(0.2f, getFrames("zombies/zombie_runner/idle/idle", 3), Animation.PlayMode.LOOP);
        zombieRunnerWalk = new Animation<>(0.1f, getFrames("zombies/zombie_runner/walk/walk", 3), Animation.PlayMode.LOOP);
        zombieRunnerAttack = new Animation<>(0.1f, getFrames("zombies/zombie_runner/attack/attack", 2), Animation.PlayMode.LOOP);
        zombieRunnerDeath = new Animation<>(0.2f, getFrames("zombies/zombie_runner/death/death", 3), Animation.PlayMode.NORMAL);

        zombieFatIdle = new Animation<>(0.3f, getFrames("zombies/zombie_fat/idle/idle", 2), Animation.PlayMode.LOOP);
        zombieFatWalk = new Animation<>(0.2f, getFrames("zombies/zombie_fat/walk/walk", 4), Animation.PlayMode.LOOP);
        zombieFatAttack = new Animation<>(0.2f, getFrames("zombies/zombie_fat/attack/attack", 3), Animation.PlayMode.LOOP);
        zombieFatDeath = new Animation<>(0.25f, getFrames("zombies/zombie_fat/death/death", 3), Animation.PlayMode.NORMAL);

        turretIdle = new Animation<>(0.25f, getFrames("objects/turret/idle/idle", 2), Animation.PlayMode.LOOP);
        turretAttack = new Animation<>(0.1f, getFrames("objects/turret/attack/attack", 4), Animation.PlayMode.NORMAL);
        turretDeath = new Animation<>(0.15f, getFrames("objects/turret/death/death", 3), Animation.PlayMode.NORMAL);

        hitSound = manager.get("sounds/hit.mp3", Sound.class);
        powerupSound = manager.get("sounds/powerup.mp3", Sound.class);
        zombieSound = manager.get("sounds/zombie.mp3", Sound.class);
        menuSound = manager.get("sounds/menuSound.mp3", Music.class);

        gameTracks.clear();
        gameTracks.add(manager.get("sounds/track1.mp3", Music.class));
        gameTracks.add(manager.get("sounds/track2.mp3", Music.class));
        gameTracks.add(manager.get("sounds/track3.mp3", Music.class));
        gameTracks.add(manager.get("sounds/track4.mp3", Music.class));
    }

    private static Array<TextureRegion> getFrames(String path, int count) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= count; i++) {
            frames.add(new TextureRegion(manager.get(path + i + ".png", Texture.class)));
        }
        return frames;
    }

    public static void dispose() {
        if (mainFont != null) mainFont.dispose();
        manager.dispose();
    }
}