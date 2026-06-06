package com.bevrfarlbt.NoExit.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.B2DVars;
import com.bevrfarlbt.NoExit.enums.EntityState;

public class Player {
    public Body body;
    private float stateTime = 0;
    private EntityState currentState = EntityState.IDLE;
    private final Vector2 lookDirection = new Vector2(1, 0);

    private boolean isAiming = false;

    private final ShapeRenderer shapeRenderer;
    private final float attackAngleRange = 70f;
    private final float attackRadius = 2.8f;

    private float attackTimer = 0f;

    private final int maxLives = 3;
    private int currentLives = 3;
    private boolean hasExtraLife = false;

    private float damageTimer = 0f;
    public boolean isDead = false;

    private int turretCount = 3;
    private boolean faceLeft = false;

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

    public void update(float dt) {
        stateTime += dt;
        if (isDead) {
            body.setLinearVelocity(0, 0);
            return;
        }
        if (attackTimer > 0) attackTimer -= dt;
        if (damageTimer > 0) damageTimer -= dt;

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

    public void setAiming(boolean aiming) {
        this.isAiming = aiming;
    }

    public void setLookDirection(Vector2 direction) {
        if (isDead || direction.len() < 0.1f) return;
        this.lookDirection.set(direction).nor();
    }

    public void handleInput(Vector2 move) {
        if (isDead) return;
        if (Math.abs(move.x) > 0.05f) {
            faceLeft = move.x < 0;
        }
        body.setLinearVelocity(move.scl(3.5f));
    }

    public void takeDamage() {
        if (isDead) return;
        if (damageTimer <= 0) {
            if (hasExtraLife) {
                hasExtraLife = false;
            } else if (currentLives > 0) {
                currentLives--;
                if (currentLives <= 0) die();
            }
            damageTimer = 1.0f;
        }
    }

    public void addExtraLife() { if (!hasExtraLife) hasExtraLife = true; }
    public void heal() { if (currentLives < maxLives) currentLives++; }

    private void die() {
        isDead = true;
        setState(EntityState.DEATH);
        if (body != null) {
            for (Fixture fixture : body.getFixtureList()) fixture.setSensor(true);
        }
    }

    public boolean canAttack() { return attackTimer <= 0 && !isDead; }
    public void attack() {
        if (!canAttack()) return;
        setState(EntityState.ATTACK);
        attackTimer = 0.5f;
    }

    public int getTurretCount() { return turretCount; }
    public void addTurrets(int count) { this.turretCount += count; }
    public boolean useTurret() {
        if (turretCount > 0 && !isDead) {
            turretCount--;
            return true;
        }
        return false;
    }

    public void setState(EntityState state) {
        if (currentState != state) {
            currentState = state;
            stateTime = 0;
        }
    }

    public Vector2 getLookDirection() { return lookDirection; }
    public int getLives() { return currentLives; }
    public int getMaxLives() { return maxLives; }
    public boolean hasExtraLife() { return hasExtraLife; }
    public float getAttackRadius() { return attackRadius; }
    public float getAttackAngleRange() { return attackAngleRange; }

    public void draw(SpriteBatch batch) {
        Animation<TextureRegion> anim;
        boolean isLooping = true;
        switch (currentState) {
            case ATTACK: anim = Assets.playerAttack; isLooping = false; break;
            case DEATH: anim = Assets.playerDeath; isLooping = false; break;
            case WALK: anim = Assets.playerWalk; break;
            default: anim = Assets.playerIdle; break;
        }
        TextureRegion frame = anim.getKeyFrame(stateTime, isLooping);
        float x = body.getPosition().x * B2DVars.PPM;
        float y = body.getPosition().y * B2DVars.PPM;
        batch.draw(frame, faceLeft ? x + 32 : x - 32, y - 32, faceLeft ? -64 : 64, 64);
    }

    public void drawDebugAttack(Matrix4 projMatrix) {
        if (isDead || !isAiming) return;

        Vector2 playerPosPx = body.getPosition().cpy().scl(B2DVars.PPM);
        float angle = lookDirection.angleDeg();
        float radiusPx = attackRadius * B2DVars.PPM;
        shapeRenderer.setProjectionMatrix(projMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.arc(playerPosPx.x, playerPosPx.y, radiusPx, angle - (attackAngleRange / 2f), attackAngleRange);
        shapeRenderer.end();
    }

    public void dispose() { shapeRenderer.dispose(); }
}