package com.bevrfarlbt.NoExit.objects;

import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.managers.BodyFactory;

public class ZombieFat extends EnemyZombie {

    public ZombieFat(BodyFactory bodyFactory, float x, float y) {
        // Вызываем супер-конструктор, чтобы базовые механизмы отработали
        super(bodyFactory, x, y);

        // Уничтожаем дефолтное физическое тело, созданное в super()
        if (this.body != null) {
            this.body.getWorld().destroyBody(this.body);
        }

        // Создаем массивный хитбокс: радиус 24f, высокая плотность 6.0f
        this.body = bodyFactory.createCircle(x, y, 24f, false, 6.0f, "zombie", this);

        // Танк-характеристики
        this.health = 7;             // Переживает 7 ударов
        this.speed = 0.8f;           // Довольно медленный враг
        this.attackCooldown = 1.8f;  // Бьет по кулдауну медленнее
    }

    @Override
    protected void initAnimations() {
        // Подключаем анимационные дорожки толстяка из ассетов
        this.animIdle = Assets.zombieFatIdle;
        this.animWalk = Assets.zombieFatWalk;
        this.animAttack = Assets.zombieFatAttack;
        this.animDeath = Assets.zombieFatDeath;
    }
}