package com.bevrfarlbt.NoExit.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.B2DVars;
import com.bevrfarlbt.NoExit.Settings;
import com.bevrfarlbt.NoExit.enums.EntityState;
import com.bevrfarlbt.NoExit.managers.BodyFactory;
import com.bevrfarlbt.NoExit.managers.TutorialManager;

public class EnemyZombie {
    public Body body;
    protected float stateTime = 0;
    public EntityState currentState = EntityState.IDLE;
    public boolean isDead = false;

    protected int health = 3;
    protected float speed = 1.8f;
    protected float attackCooldown = 1.5f;

    protected float cooldownTimer = 0f;
    protected boolean isFacingLeft = false;
    protected float hitStunTimer = 0f;
    protected PowerUp droppedPowerUp = null;
    protected boolean hasRolledPowerUp = false;

    protected float damageFlashTimer = 0f;
    protected final float FLASH_DURATION = 0.2f;

    protected Animation<TextureRegion> animIdle;
    protected Animation<TextureRegion> animWalk;
    protected Animation<TextureRegion> animAttack;
    protected Animation<TextureRegion> animDeath;

    public EnemyZombie(BodyFactory bodyFactory, float x, float y) {
        this.body = bodyFactory.createCircle(x, y, 15f, false, 1.0f, "zombie", this);
        initAnimations();
    }

    protected void initAnimations() {
        this.animIdle = Assets.zombieDefaultIdle;
        this.animWalk = Assets.zombieDefaultWalk;
        this.animAttack = Assets.zombieDefaultAttack;
        this.animDeath = Assets.zombieDefaultDeath;
    }

    public void update(float dt, Vector2 playerPos, Player player, Array<Turret> turrets) {
        if (isDead) {
            stateTime += dt;
            if (body != null) {
                body.setLinearVelocity(0, 0);
            }
            return;
        }
        stateTime += dt;
        cooldownTimer += dt;

        if (damageFlashTimer > 0) {
            damageFlashTimer -= dt;
        }

        if (hitStunTimer > 0) {
            hitStunTimer -= dt;
            return;
        }

        Vector2 enemyPos = body.getPosition();
        Vector2 targetPos = playerPos;
        Object targetEntity = player;
        float minDist = enemyPos.dst(playerPos);

        for (Turret t : turrets) {
            if (t.isDestroyed || t.getCurrentState() == EntityState.DEATH) continue;

            Vector2 turretPosInMeters = t.getPosition().cpy().scl(1f / B2DVars.PPM);
            float distToTurret = enemyPos.dst(turretPosInMeters);

            if (distToTurret < minDist) {
                minDist = distToTurret;
                targetPos = turretPosInMeters;
                targetEntity = t;
            }
        }

        Vector2 toTarget = targetPos.cpy().sub(enemyPos);
        float dist = toTarget.len();

        if (currentState == EntityState.ATTACK) {
            if (animAttack.isAnimationFinished(stateTime)) {
                if (dist < 8.0f) {
                    setState(EntityState.WALK);
                } else {
                    setState(EntityState.IDLE);
                }
            } else {
                body.setLinearVelocity(toTarget.nor().scl(0.9f));

                if (dist < 1.8f && cooldownTimer >= attackCooldown) {
                    if (targetEntity instanceof Player) {
                        ((Player) targetEntity).takeDamage();
                    } else if (targetEntity instanceof Turret) {
                        ((Turret) targetEntity).takeDamage(1);
                    }
                    cooldownTimer = 0f;
                }
            }
        } else {
            if (dist < 1.8f && cooldownTimer >= attackCooldown) {
                setState(EntityState.ATTACK);

                if (Assets.zombieSound != null && Settings.soundZombieEnabled) {
                    Assets.zombieSound.play(0.4f);
                }

                cooldownTimer = 0f;
                body.setLinearVelocity(toTarget.nor().scl(0.9f));
            } else if (dist < 8.0f) {
                if (cooldownTimer < attackCooldown) {
                    setState(EntityState.WALK);
                    body.setLinearVelocity(toTarget.nor().scl(0.9f));
                } else {
                    setState(EntityState.WALK);
                    body.setLinearVelocity(toTarget.nor().scl(speed));
                }
            } else {
                setState(EntityState.IDLE);
                body.setLinearVelocity(0, 0);
            }
        }

        if (toTarget.x != 0) isFacingLeft = toTarget.x < 0;
    }

    public void takeDamage(int dmg) {
        if (isDead) return;
        health -= dmg;
        damageFlashTimer = FLASH_DURATION;
        if (health <= 0) die();
    }

    public void applyStun(float duration) {
        this.hitStunTimer = duration;
    }

    private void die() {
        isDead = true;
        setState(EntityState.DEATH);

        if (body != null) {
            for (Fixture fixture : body.getFixtureList()) {
                fixture.setSensor(true);
                fixture.setFilterData(new Filter());
            }
            body.setUserData(null);
        }
    }

    public PowerUp trySpawnPowerUp() {
        if (hasRolledPowerUp) return null;
        hasRolledPowerUp = true;

        if (MathUtils.random() <= 0.15f) {
            float posX = (body != null ? body.getPosition().x : 0f) * B2DVars.PPM;
            float posY = (body != null ? body.getPosition().y : 0f) * B2DVars.PPM;

            PowerUp.Type type;
            int rand = MathUtils.random(1, 3);
            if (rand == 1) type = PowerUp.Type.HEAL;
            else if (rand == 2) type = PowerUp.Type.SHIELD;
            else type = PowerUp.Type.ONE_SHOT;

            droppedPowerUp = new PowerUp(posX, posY, type);
            return droppedPowerUp;
        }
        return null;
    }

    public PowerUp forceSpawnPowerUp() {

        if (hasRolledPowerUp) return null;

        hasRolledPowerUp = true;

        float posX = body.getPosition().x * B2DVars.PPM;
        float posY = body.getPosition().y * B2DVars.PPM;

        droppedPowerUp = new PowerUp(
                posX,
                posY,
                PowerUp.Type.HEAL
        );

        return droppedPowerUp;
    }

    public void setState(EntityState newState) {
        if (currentState == newState) return;
        currentState = newState;
        stateTime = 0;
    }

    public void draw(SpriteBatch batch) {
        Animation<TextureRegion> anim;
        boolean isLooping = true;

        switch (currentState) {
            case ATTACK:
                anim = animAttack;
                isLooping = false;
                break;
            case DEATH:
                anim = animDeath;
                isLooping = false;
                break;
            case WALK:
                anim = animWalk;
                break;
            default:
                anim = animIdle;
                break;
        }

        TextureRegion frame = anim.getKeyFrame(stateTime, isLooping);
        float x = body != null ? (body.getPosition().x * B2DVars.PPM) : 0f;
        float y = body != null ? (body.getPosition().y * B2DVars.PPM) : 0f;

        if (damageFlashTimer > 0) {
            batch.setColor(1f, 0.3f, 0.3f, 1f);
        }

        batch.draw(frame, isFacingLeft ? x + 32 : x - 32, y - 32, isFacingLeft ? -64 : 64, 64);

        batch.setColor(Color.WHITE);
    }

    public PowerUp getPowerUp() {
        return droppedPowerUp;
    }
}