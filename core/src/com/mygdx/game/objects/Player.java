package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
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
    private float attackRadius = 2.8f; // Дистанция атаки в метрах Box2D

    private float attackCooldown = 0.5f;
    private float attackTimer = 0f;

    private int maxLives = 3;
    private int currentLives = 3;
    private boolean hasExtraLife = false;

    private float damageCooldown = 1.0f;
    private float damageTimer = 0f;
    public boolean isDead = false;

    public Player(World world, float x, float y) {
        createPhysics(world, x, y);
        shapeRenderer = new ShapeRenderer();
    }

    private void createPhysics(World world, float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x / B2DVars.PPM, y / B2DVars.PPM);
        bdef.fixedRotation = true;
        body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(12 / B2DVars.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = (short) 1;
        fdef.filter.maskBits = (short) (2 | 4 | 8);

        body.createFixture(fdef).setUserData("player");
        body.setUserData(this);

        shape.dispose();
    }

    public void update(float dt, Vector2 mousePos) {
        stateTime += dt;

        if (isDead) {
            body.setLinearVelocity(0, 0);
            return;
        }

        if (attackTimer > 0) attackTimer -= dt;
        if (damageTimer > 0) damageTimer -= dt;

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
        if (isDead) return;

        if (damageTimer <= 0) {
            if (hasExtraLife) {
                hasExtraLife = false;
                Gdx.app.log("PLAYER", "Потеряна экстра-жизнь!");
            } else if (currentLives > 0) {
                currentLives--;
                Gdx.app.log("PLAYER", "ХП: " + currentLives);
                if (currentLives <= 0) {
                    die();
                }
            }
            damageTimer = damageCooldown;
        }
    }

    public void addExtraLife() {
        if (!hasExtraLife) {
            hasExtraLife = true;
            Gdx.app.log("PLAYER", "Получена экстра-жизнь!");
        }
    }

    public void heal() {
        if (currentLives < maxLives) {
            currentLives++;
            Gdx.app.log("PLAYER", "ХП восстановлено: " + currentLives);
        }
    }

    private void die() {
        isDead = true;
        setState(EntityState.DEATH);
        if (body != null) {
            for (Fixture fixture : body.getFixtureList()) {
                fixture.setSensor(true);
            }
        }
    }

    public void handleInput(Vector2 move) {
        if (isDead) return;
        body.setLinearVelocity(move.scl(3.5f));
    }

    public boolean canAttack() {
        return attackTimer <= 0 && !isDead;
    }

    public void attack() {
        if (!canAttack()) return;
        setState(EntityState.ATTACK);
        attackTimer = attackCooldown;
    }

    public void setState(EntityState state) {
        if (currentState != state) {
            currentState = state;
            stateTime = 0;
        }
    }

    public Vector2 getLookDirection() {
        return lookDirection;
    }

    public int getLives() {
        return currentLives;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public boolean hasExtraLife() {
        return hasExtraLife;
    }

    public float getAttackRadius() {
        return attackRadius;
    }

    public float getAttackAngleRange() {
        return attackAngleRange;
    }

    public void draw(SpriteBatch batch) {
        Animation<TextureRegion> anim;
        boolean isLooping = true;

        switch (currentState) {
            case ATTACK:
                anim = Assets.playerAttack;
                isLooping = false;
                break;
            case DEATH:
                anim = Assets.playerDeath;
                isLooping = false;
                break;
            case WALK:
                anim = Assets.playerWalk;
                break;
            default:
                anim = Assets.playerIdle;
                break;
        }

        TextureRegion frame = anim.getKeyFrame(stateTime, isLooping);
        float x = body.getPosition().x * B2DVars.PPM;
        float y = body.getPosition().y * B2DVars.PPM;

        boolean faceLeft = lookDirection.x < 0;
        batch.draw(frame, faceLeft ? x + 32 : x - 32, y - 32, faceLeft ? -64 : 64, 64);
    }

    // Метод отрисовки дуги сектора атаки
    public void drawDebugAttack(Matrix4 projMatrix) {
        if (isDead) return;

        Vector2 playerPosPx = body.getPosition().cpy().scl(B2DVars.PPM);
        float angle = lookDirection.angleDeg();
        float radiusPx = attackRadius * B2DVars.PPM;

        shapeRenderer.setProjectionMatrix(projMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        shapeRenderer.arc(
                playerPosPx.x, playerPosPx.y,
                radiusPx,
                angle - (attackAngleRange / 2f),
                attackAngleRange
        );

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}