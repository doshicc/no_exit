package com.bevrfarlbt.NoExit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {
    private static final String PREFS_NAME = "mygame_settings";
    private static Preferences prefs;

    // Настройки по умолчанию
    public static boolean musicMenuEnabled = true;
    public static boolean musicGameEnabled = true;
    public static boolean soundHitEnabled = true;
    public static boolean soundZombieEnabled = true;

    private static Preferences getPrefs() {
        if (prefs == null) {
            prefs = Gdx.app.getPreferences(PREFS_NAME);
        }
        return prefs;
    }

    public static void load() {
        try {
            Preferences p = getPrefs();
            if (p != null) {
                musicMenuEnabled = p.getBoolean("musicMenu", true);
                musicGameEnabled = p.getBoolean("musicGame", true);
                soundHitEnabled = p.getBoolean("soundHit", true);
                soundZombieEnabled = p.getBoolean("soundZombie", true);
            }
        } catch (Exception e) {
            Gdx.app.log("SETTINGS", "Не удалось загрузить настройки: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            Preferences p = getPrefs();
            if (p != null) {
                p.putBoolean("musicMenu", musicMenuEnabled);
                p.putBoolean("musicGame", musicGameEnabled);
                p.putBoolean("soundHit", soundHitEnabled);
                p.putBoolean("soundZombie", soundZombieEnabled);
                p.flush();
            }
        } catch (Exception e) {
            Gdx.app.log("SETTINGS", "Не удалось сохранить настройки: " + e.getMessage());
        }
    }
}