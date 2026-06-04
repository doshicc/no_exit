package com.bevrfarlbt.NoExit.managers;

import com.bevrfarlbt.NoExit.Settings;

public class TutorialManager {
    public enum Step {
        MOVE,
        KILL_ZOMBIE,
        PICK_POWERUP,
        PLACE_TURRET,
        FINISHED
    }

    private static Step currentStep = Step.MOVE;
    private static boolean tutorialPowerUpSpawned = false;

    public static boolean hasSpawnedTutorialPowerUp() {
        return tutorialPowerUpSpawned;
    }

    public static void setTutorialPowerUpSpawned(boolean value) {
        tutorialPowerUpSpawned = value;
    }

    public static void reset() {
        currentStep = Step.MOVE;
        tutorialPowerUpSpawned = false;
    }

    public static Step getCurrentStep() {
        return currentStep;
    }

    public static void nextStep() {

        switch (currentStep) {

            case MOVE:
                currentStep = Step.KILL_ZOMBIE;
                break;

            case KILL_ZOMBIE:
                currentStep = Step.PICK_POWERUP;
                break;

            case PICK_POWERUP:
                currentStep = Step.PLACE_TURRET;
                break;

            case PLACE_TURRET:
                currentStep = Step.FINISHED;
                Settings.tutorialCompleted = true;
                Settings.save();
                break;
        }
    }

    public static boolean isFinished() {
        return currentStep == Step.FINISHED;
    }

    public static String getCurrentText() {
        switch (currentStep) {
            case MOVE:
                return "Используйте левый джойстик для движения";
            case KILL_ZOMBIE:
                return "Уничтожьте первого зомби, \n атакуя с помощью правого джойстика";
            case PICK_POWERUP:
                return "Из зомби могут выпадать бонусы. \n Подбирайте их, чтобы выжить";
            case PLACE_TURRET:
                return "Установите турель кнопкой 'Турель'";
            default:
                return "";
        }
    }
}
