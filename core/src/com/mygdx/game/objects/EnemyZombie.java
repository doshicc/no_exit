package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.Assets;
import com.mygdx.game.B2DVars;
import com.mygdx.game.enums.EntityState;

public class EnemyZombie {
    public Body body;
    private float stateTime = 0;
    public EntityState currentState = EntityState.IDLE;
    public boolean isDead = false;
    private int health = 3;
    private float speed = 1.8f;
    private boolean isFacingLeft = false;

    public EnemyZombie(World world, float x, float y) {
        createPhysics(world, x, y);
    }

    private void createPhysics(World world, float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x / B2DVars.PPM, y / B2DVars.PPM);
        bdef.fixedRotation = true;
        body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(15 / B2DVars.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = (short) 2;
        body.createFixture(fdef).setUserData("zombie");
        shape.dispose();
    }

    public void update(float dt, Vector2 playerPos) {
        if (isDead) {
            stateTime += dt;
            body.setLinearVelocity(0, 0);
            return;
        }
        stateTime += dt;

        Vector2 enemyPos = body.getPosition();
        Vector2 toPlayer = playerPos.cpy().sub(enemyPos);
        float dist = toPlayer.len();

        if (dist < 1.8f) {
            setState(EntityState.ATTACK);
            body.setLinearVelocity(0, 0);
        } else if (dist < 8.0f) {
            setState(EntityState.WALK);
            body.setLinearVelocity(toPlayer.nor().scl(speed));
        } else {
            setState(EntityState.IDLE);
            body.setLinearVelocity(0, 0);
        }

        if (toPlayer.x != 0) isFacingLeft = toPlayer.x < 0;
    }

    public void setState(EntityState newState) {
        if (currentState == newState) return;
        currentState = newState;
        stateTime = 0;
    }

    public void takeDamage(int dmg) {
        if (isDead) return;
        health -= dmg;
        if (health <= 0) die();
    }

    private void die() {
        isDead = true;
        setState(EntityState.DEATH);
        for (Fixture f : body.getFixtureList()) f.setSensor(true);
    }

    public void draw(SpriteBatch batch) {
        Animation<TextureRegion> anim;
        switch (currentState) {
            case ATTACK: anim = Assets.zombieAttack; break;
            case DEATH:  anim = Assets.zombieDeath; break;
            default:     anim = Assets.zombieIdle; break;
        }

        TextureRegion frame = anim.getKeyFrame(stateTime);
        float x = (body.getPosition().x * B2DVars.PPM);
        float y = (body.getPosition().y * B2DVars.PPM);

        batch.draw(frame, isFacingLeft ? x + 32 : x - 32, y - 32, isFacingLeft ? -64 : 64, 64);
    }
}