package com.bevrfarlbt.NoExit.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SaveManager {

    private static final Preferences prefs =
            Gdx.app.getPreferences("NoExitSave");

    public static void save(
            int rooms,
            int roomLevel,
            int coins,
            int turrets,
            int lives,
            boolean extraLife,
            float sessionTime) {

        prefs.putBoolean("hasSave", true);
        prefs.putInteger("rooms", rooms);
        prefs.putInteger("roomLevel", roomLevel);
        prefs.putInteger("lives", lives);
        prefs.putBoolean("extraLife", extraLife);
        prefs.putFloat("sessionTime", sessionTime);
        prefs.flush();
    }

    public static boolean hasSave() {
        return prefs.getBoolean("hasSave", false);
    }

    public static void deleteSave() {
        prefs.remove("hasSave");
        prefs.remove("rooms");
        prefs.remove("roomLevel");
        prefs.remove("lives");
        prefs.remove("extraLife");
        prefs.remove("sessionTime");
        prefs.flush();
    }

    public static int getRooms() {
        return prefs.getInteger("rooms", 0);
    }

    public static int getRoomLevel() {
        return prefs.getInteger("roomLevel", 1);
    }

    public static int getLives() {
        return prefs.getInteger("lives", 3);
    }

    public static boolean hasExtraLifeSaved() {
        return prefs.getBoolean("extraLife", false);
    }

    public static float getSessionTime() {
        return prefs.getFloat("sessionTime", 0f);
    }
}