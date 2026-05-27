package com.bevrfarlbt.NoExit.objects;

import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.managers.BodyFactory;

public class ZombieRunner extends EnemyZombie {

    public ZombieRunner(BodyFactory bodyFactory, float x, float y) {
        super(bodyFactory, x, y);

        // Переопределяем статы бегуна
        this.health = 2;            // Меньше ХП, чем у обычного
        this.speed = 3.0f;          // Скорость значительно выше (у дефолта 1.8f)
        this.attackCooldown = 0.8f; // Атакует чаще
    }

    @Override
    protected void initAnimations() {
        // Подвязываем ассеты бегуна
        this.animIdle = Assets.zombieRunnerIdle;
        this.animWalk = Assets.zombieRunnerWalk;
        this.animAttack = Assets.zombieRunnerAttack;
        this.animDeath = Assets.zombieRunnerDeath;
    }
}