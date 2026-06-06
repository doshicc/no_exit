package com.bevrfarlbt.NoExit.managers;

public class DifficultyManager {

    private int roomNumber = 1;

    public void nextRoom() {
        roomNumber++;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getRoomBudget() {
        return 5 + roomNumber * 2;
    }

    public static final int ZOMBIE_DEFAULT_COST = 1;
    public static final int ZOMBIE_RUNNER_COST = 2;
    public static final int ZOMBIE_FAT_COST = 4;
}