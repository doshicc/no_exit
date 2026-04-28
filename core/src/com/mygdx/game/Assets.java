package com.mygdx.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Assets {
    public static AssetManager manager = new AssetManager();

    // Окружение
    public static TextureRegion floorDefault, upWall, downWall, leftRightWall, box, shelf;
    public static Array<TextureRegion> floorDetails = new Array<>();

    // Игрок
    public static Animation<TextureRegion> playerIdle, playerWalk, playerAttack;

    // Зомби
    public static Animation<TextureRegion> zombieIdle, zombieAttack, zombieDeath;

    public static void load() {
        // Окружение
        manager.load("level/floor/floor_default.png", Texture.class);
        manager.load("level/floor/floor1.png", Texture.class);
        manager.load("level/floor/floor2.png", Texture.class);
        manager.load("level/floor/floor3.png", Texture.class);
        manager.load("level/wall/upWall.png", Texture.class);
        manager.load("level/wall/downWall.png", Texture.class);
        manager.load("level/wall/leftRightWall.png", Texture.class);
        manager.load("level/objects/box.png", Texture.class);
        manager.load("level/objects/shelf.png", Texture.class);

        // Игрок
        for (int i = 1; i <= 2; i++) manager.load("player/idle/idle" + i + ".png", Texture.class);
        for (int i = 1; i <= 6; i++) manager.load("player/walk/walk" + i + ".png", Texture.class);
        for (int i = 1; i <= 5; i++) manager.load("player/attack/attack" + i + ".png", Texture.class);

        // Зомби
        for (int i = 1; i <= 4; i++) manager.load("zombie/idle/idle" + i + ".png", Texture.class);
        for (int i = 1; i <= 5; i++) manager.load("zombie/attack/attack" + i + ".png", Texture.class);
        for (int i = 1; i <= 3; i++) manager.load("zombie/death/death" + i + ".png", Texture.class);
    }

    public static void setup() {
        // Окружение
        floorDefault = new TextureRegion(manager.get("level/floor/floor_default.png", Texture.class));
        floorDetails.add(new TextureRegion(manager.get("level/floor/floor1.png", Texture.class)));
        floorDetails.add(new TextureRegion(manager.get("level/floor/floor2.png", Texture.class)));
        floorDetails.add(new TextureRegion(manager.get("level/floor/floor3.png", Texture.class)));
        upWall = new TextureRegion(manager.get("level/wall/upWall.png", Texture.class));
        downWall = new TextureRegion(manager.get("level/wall/downWall.png", Texture.class));
        leftRightWall = new TextureRegion(manager.get("level/wall/leftRightWall.png", Texture.class));
        box = new TextureRegion(manager.get("level/objects/box.png", Texture.class));
        shelf = new TextureRegion(manager.get("level/objects/shelf.png", Texture.class));

        // Анимации Игрока
        playerIdle = new Animation<>(0.3f, getFrames("player/idle/idle", 2), Animation.PlayMode.LOOP);
        playerWalk = new Animation<>(0.1f, getFrames("player/walk/walk", 6), Animation.PlayMode.LOOP);
        playerAttack = new Animation<>(0.07f, getFrames("player/attack/attack", 5), Animation.PlayMode.NORMAL);

        // Анимации Зомби
        zombieIdle = new Animation<>(0.2f, getFrames("zombie/idle/idle", 4), Animation.PlayMode.LOOP);
        zombieAttack = new Animation<>(0.15f, getFrames("zombie/attack/attack", 5), Animation.PlayMode.LOOP);
        zombieDeath = new Animation<>(0.25f, getFrames("zombie/death/death", 3), Animation.PlayMode.NORMAL);
    }

    private static Array<TextureRegion> getFrames(String path, int count) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= count; i++) {
            frames.add(new TextureRegion(manager.get(path + i + ".png", Texture.class)));
        }
        return frames;
    }

    public static void dispose() {
        manager.dispose();
    }
}