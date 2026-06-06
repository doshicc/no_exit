package com.bevrfarlbt.NoExit.objects;

import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.managers.BodyFactory;

public class ZombieRunner extends EnemyZombie {

    public ZombieRunner(BodyFactory bodyFactory, float x, float y) {
        super(bodyFactory, x, y);
        this.health = 2;
        this.speed = 3.0f;
        this.attackCooldown = 0.8f;
    }

    @Override
    protected void initAnimations() {
        this.animIdle = Assets.zombieRunnerIdle;
        this.animWalk = Assets.zombieRunnerWalk;
        this.animAttack = Assets.zombieRunnerAttack;
        this.animDeath = Assets.zombieRunnerDeath;
    }
}