package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Assets;
import com.mygdx.game.B2DVars;
import com.mygdx.game.enums.EntityState;

public class Player {
    public Body body;
    private float stateTime = 0;
    private EntityState currentState = EntityState.IDLE;
    private Vector2 lookDirection = new Vector2(1, 0);

    private ShapeRenderer shapeRenderer;
    private float attackAngleRange = 70f;
    private float attackRadius = 2.8f;

    private float attackCooldown = 0.5f;
    private float attackTimer = 0f;

    private int maxLives = 3;
    private int currentLives = 3;

    public Player(World world, float x, float y) {
        createPhysics(world, x, y);
        shapeRenderer = new ShapeRenderer();
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
        float speedModifier = (currentState == EntityState.ATTACK) ? 0.5f : 1.0f;
        body.setLinearVelocity(move.scl(baseSpeed * speedModifier));
    }

    public void update(float dt, Vector2 mousePos) {
        stateTime += dt;

        if (attackTimer > 0) {
            attackTimer -= dt;
        }

        Vector2 playerPosPx = body.getPosition().cpy().scl(B2DVars.PPM);
        lookDirection.set(mousePos).sub(playerPosPx).nor();

        if (currentState == EntityState.ATTACK) {
            if (Assets.playerAttack.isAnimationFinished(stateTime)) {
                setState(EntityState.IDLE);
            }
        } else {
            if (body.getLinearVelocity().len() > 0.1f) {
                setState(EntityState.WALK);
            } else {
                setState(EntityState.IDLE);
            }
        }
    }

    public void takeDamage() {
        if (currentLives > 0) {
            currentLives--;
        }
    }

    public int getLives() {
        return currentLives;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public boolean canAttack() {
        return attackTimer <= 0 && currentState != EntityState.ATTACK;
    }

    public void setState(EntityState newState) {
        if (currentState == newState) return;
        currentState = newState;
        stateTime = 0;
    }

    public void attack() {
        setState(EntityState.ATTACK);
        attackTimer = attackCooldown;
    }

    public void draw(SpriteBatch batch) {
        Animation<TextureRegion> anim;

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

    public void drawDebugAttack(Matrix4 projectionMatrix) {
        shapeRenderer.setProjectionMatrix(projectionMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0.3f, 0.3f, 1f);

        Vector2 playerPos = body.getPosition().cpy().scl(B2DVars.PPM);

        // Используем константный угол (70 градусов) или переменную attackAngleRange
        float angle = lookDirection.angleDeg();
        float range = 70f; // Угол обзора в градусах

        shapeRenderer.arc(
                playerPos.x, playerPos.y,
                attackRadius * B2DVars.PPM,
                angle - (range / 2),
                range
        );
        shapeRenderer.end();
    }

    public Vector2 getLookDirection() { return lookDirection; }

    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}