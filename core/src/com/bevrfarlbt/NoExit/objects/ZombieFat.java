package com.bevrfarlbt.NoExit.objects;

import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.managers.BodyFactory;

public class ZombieFat extends EnemyZombie {

    public ZombieFat(BodyFactory bodyFactory, float x, float y) {
        super(bodyFactory, x, y);

        if (this.body != null) {
            this.body.getWorld().destroyBody(this.body);
        }

        this.body = bodyFactory.createCircle(x, y, 24f, false, 6.0f, "zombie", this);

        this.health = 7;
        this.speed = 0.8f;
        this.attackCooldown = 1.8f;
    }

    @Override
    protected void initAnimations() {
        this.animIdle = Assets.zombieFatIdle;
        this.animWalk = Assets.zombieFatWalk;
        this.animAttack = Assets.zombieFatAttack;
        this.animDeath = Assets.zombieFatDeath;
    }
}