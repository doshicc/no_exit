package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.Assets;
import com.mygdx.game.B2DVars;
import com.mygdx.game.enums.EntityState;

public class Player {
    public Body body;
    private float stateTime = 0;
    private EntityState currentState = EntityState.IDLE;
    private Vector2 lookDirection = new Vector2(1, 0);

    public Player(World world, float x, float y) {
        createPhysics(world, x, y);
    }

    private void createPhysics(World world, float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x / B2DVars.PPM, y / B2DVars.PPM);
        bdef.fixedRotation = true;
        bdef.linearDamping = 1.0f;
        body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(12 / B2DVars.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0.5f;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;

        body.createFixture(fdef).setUserData("player");
        shape.dispose();
    }

    public void handleInput(Vector2 move) {
        float baseSpeed = 4.5f;

        // --- ПРАВКА ТУТ ---
        // Если атакуем — режем скорость пополам
        float speedModifier = (currentState == EntityState.ATTACK) ? 0.5f : 1.0f;

        body.setLinearVelocity(move.scl(baseSpeed * speedModifier));
    }

    public void update(float dt, Vector2 mousePos) {
        stateTime += dt;

        Vector2 playerPosPx = body.getPosition().cpy().scl(B2DVars.PPM);
        lookDirection.set(mousePos).sub(playerPosPx).nor();

        // Логика состояний
        if (currentState == EntityState.ATTACK) {
            if (Assets.playerAttack.isAnimationFinished(stateTime)) {
                setState(EntityState.IDLE);
            }
        } else {
            // Если мы не атакуем, переключаемся между бегом и покоем
            if (body.getLinearVelocity().len() > 0.1f) {
                setState(EntityState.WALK);
            } else {
                setState(EntityState.IDLE);
            }
        }
    }

    public void setState(EntityState newState) {
        if (currentState == newState) return;
        currentState = newState;
        stateTime = 0;
    }

    public void attack() {
        // Теперь атака не блокирует управление полностью,
        // а просто переводит игрока в это состояние
        setState(EntityState.ATTACK);
    }

    public void draw(SpriteBatch batch) {
        Animation<TextureRegion> anim;

        // Приоритет анимации атаки
        if (currentState == EntityState.ATTACK) {
            anim = Assets.playerAttack;
        } else if (body.getLinearVelocity().len() > 0.1f) {
            anim = Assets.playerWalk;
        } else {
            anim = Assets.playerIdle;
        }

        TextureRegion frame = anim.getKeyFrame(stateTime);
        float w = 64f, h = 64f;
        float x = (body.getPosition().x * B2DVars.PPM) - w / 2;
        float y = (body.getPosition().y * B2DVars.PPM) - h / 2;

        boolean flip = lookDirection.x < 0;
        if (flip) {
            batch.draw(frame, x + w, y, -w, h);
        } else {
            batch.draw(frame, x, y, w, h);
        }
    }

    public Vector2 getLookDirection() { return lookDirection; }
}