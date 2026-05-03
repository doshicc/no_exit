package com.mygdx.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.objects.Player;

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

        if (dataA != null && dataB != null) {
            String strA = dataA.toString();
            String strB = dataB.toString();

            if ((strA.equals("player") && strB.contains("zombie")) ||
                    (strB.equals("player") && strA.contains("zombie"))) {

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