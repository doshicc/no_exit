package com.mygdx.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.EnemyZombie;

public class AttackListener implements ContactListener {

    private final Player player;

    public AttackListener(Player player) {
        this.player = player;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa == null || fb == null) return;

        Object dataA = fa.getUserData();
        Object dataB = fb.getUserData();

        // Проверяем, участвует ли игрок в столкновении
        boolean isPlayerA = "player".equals(dataA) || (fa.getBody().getUserData() instanceof Player);
        boolean isPlayerB = "player".equals(dataB) || (fb.getBody().getUserData() instanceof Player);

        if (isPlayerA || isPlayerB) {
            EnemyZombie zombie = null;

            // Пытаемся найти объект EnemyZombie из userData у тел (Body)
            if (fa.getBody().getUserData() instanceof EnemyZombie) {
                zombie = (EnemyZombie) fa.getBody().getUserData();
            } else if (fb.getBody().getUserData() instanceof EnemyZombie) {
                zombie = (EnemyZombie) fb.getBody().getUserData();
            }

            // Если зомби существует и мертв (isDead == true), игнорируем урон
            if (zombie != null && zombie.isDead) {
                return;
            }

            if (zombie != null) {
                Gdx.app.log("ATTACK", "Игрок получает урон!");
                if (player != null) {
                    player.takeDamage();
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {}
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}