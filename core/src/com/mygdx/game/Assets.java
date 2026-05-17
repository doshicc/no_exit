package com.mygdx.game;

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

    // Окружение
    public static TextureRegion floorDefault, upWall, downWall, leftRightWall, box, shelf;
    public static Array<TextureRegion> floorDetails = new Array<>();

    // Игрок
    public static Animation<TextureRegion> playerIdle, playerWalk, playerAttack, playerDeath;

    // Зомби
    public static Animation<TextureRegion> zombieIdle, zombieWalk, zombieAttack, zombieDeath;

    // Сердца
    public static TextureRegion emptyHeart, extraHeart, fullHeart;

    // Бафы
    public static TextureRegion powerupHeal, powerupShield, powerupOneShot;

    // UI Джойстики
    public static TextureRegion joystickBg, joystickKnob;

    // Шрифты
    public static BitmapFont mainFont;

    // ФОН МЕНЮ
    public static Texture menuBackground;

    // --- ЗВУКИ И МУЗЫКА ---
    public static Sound hitSound;
    public static Sound powerupSound;
    public static Sound stepsSound;
    public static Sound zombieSound;

    // Твоя фоновая музыка для меню (загружаем как Music)
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

        // Загрузка джойстиков
        manager.load("player/joystick/joystick_bg.png", Texture.class);
        manager.load("player/joystick/joystick_knob.png", Texture.class);

        manager.load("Screen/MenuBackground.png", Texture.class);

        for (int i = 1; i <= 2; i++) manager.load("player/idle/idle" + i + ".png", Texture.class);
        for (int i = 1; i <= 6; i++) manager.load("player/walk/walk" + i + ".png", Texture.class);
        for (int i = 1; i <= 5; i++) manager.load("player/attack/attack" + i + ".png", Texture.class);
        for (int i = 1; i <= 5; i++) manager.load("player/death/death" + i + ".png", Texture.class);

        for (int i = 1; i <= 4; i++) manager.load("zombie/idle/idle" + i + ".png", Texture.class);
        for (int i = 1; i <= 4; i++) manager.load("zombie/walk/walk" + i + ".png", Texture.class);
        for (int i = 1; i <= 5; i++) manager.load("zombie/attack/attack" + i + ".png", Texture.class);
        for (int i = 1; i <= 3; i++) manager.load("zombie/death/death" + i + ".png", Texture.class);

        // --- ЗАГРУЗКА АУДИО ФАЙЛОВ ---
        manager.load("sounds/hit.mp3", Sound.class);
        manager.load("sounds/powerup.mp3", Sound.class);
        manager.load("sounds/zombie.mp3", Sound.class);

        // Загружаем фон меню как Music
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
        parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхЦчшщъыьэюя1234567890: -_!?";
        mainFont = generator.generateFont(parameter);
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

        // Настройка джойстиков
        joystickBg = new TextureRegion(manager.get("player/joystick/joystick_bg.png", Texture.class));
        joystickKnob = new TextureRegion(manager.get("player/joystick/joystick_knob.png", Texture.class));

        menuBackground = manager.get("Screen/MenuBackground.png", Texture.class);

        playerIdle = new Animation<>(0.3f, getFrames("player/idle/idle", 2), Animation.PlayMode.LOOP);
        playerWalk = new Animation<>(0.1f, getFrames("player/walk/walk", 6), Animation.PlayMode.LOOP);
        playerAttack = new Animation<>(0.07f, getFrames("player/attack/attack", 5), Animation.PlayMode.NORMAL);
        playerDeath = new Animation<>(0.25f, getFrames("player/death/death", 5), Animation.PlayMode.NORMAL);

        zombieIdle = new Animation<>(0.2f, getFrames("zombie/idle/idle", 4), Animation.PlayMode.LOOP);
        zombieWalk = new Animation<>(0.15f, getFrames("zombie/walk/walk", 4), Animation.PlayMode.LOOP);
        zombieAttack = new Animation<>(0.15f, getFrames("zombie/attack/attack", 5), Animation.PlayMode.LOOP);
        zombieDeath = new Animation<>(0.25f, getFrames("zombie/death/death", 3), Animation.PlayMode.NORMAL);

        // --- ИНИЦИАЛИЗАЦИЯ АУДИО ---
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