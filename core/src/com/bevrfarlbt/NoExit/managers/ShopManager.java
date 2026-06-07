package com.bevrfarlbt.NoExit.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class ShopManager {
    private static final String PREFS_NAME = "NoExit_SaveData";
    private static Preferences prefs;

    private static void init() {
        if (prefs == null) {
            prefs = Gdx.app.getPreferences(PREFS_NAME);
        }
    }

    public static int getCoins() {
        init();
        return prefs.getInteger("coins", 0);
    }

    public static void setCoins(int coins) {
        init();
        prefs.putInteger("coins", coins);
        prefs.flush();
    }

    public static int getTurretInventory() {
        init();
        return prefs.getInteger("turret_inventory", 0);
    }

    public static void setTurretInventory(int turretInventory) {
        init();
        prefs.putInteger("turret_inventory", turretInventory);
        prefs.flush();
    }

    public static void addCoins(int amount) {
        init();
        int current = getCoins();
        prefs.putInteger("coins", current + amount);
        prefs.flush();
    }

    public static boolean spendCoins(int amount) {
        init();
        int current = getCoins();
        if (current >= amount) {
            prefs.putInteger("coins", current - amount);
            prefs.flush();
            return true;
        }
        return false;
    }

    public static void addTurretsToInventory(int count) {
        init();
        int current = getTurretInventory();
        prefs.putInteger("turret_inventory", current + count);
        prefs.flush();
    }

    public static boolean useTurretFromInventory() {
        init();
        int current = getTurretInventory();
        if (current > 0) {
            prefs.putInteger("turret_inventory", current - 1);
            prefs.flush();
            return true;
        }
        return false;
    }
}