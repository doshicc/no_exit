package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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
    private float attackCooldown = 1.5f;
    private float cooldownTimer = 0f;
    private boolean isFacingLeft = false;

    private float hitStunTimer = 0f;
    private PowerUp droppedPowerUp = null;

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
        body.setUserData(this);

        shape.dispose();
    }

    public void update(float dt, Vector2 playerPos, Player player) {
        if (isDead) {
            stateTime += dt;
            if (body != null) {
                body.setLinearVelocity(0, 0);
            }
            return;
        }
        stateTime += dt;
        cooldownTimer += dt;

        if (hitStunTimer > 0) {
            hitStunTimer -= dt;
            return;
        }

        Vector2 enemyPos = body.getPosition();
        Vector2 toPlayer = playerPos.cpy().sub(enemyPos);
        float dist = toPlayer.len();

        if (currentState == EntityState.ATTACK) {
            if (Assets.zombieAttack.isAnimationFinished(stateTime)) {
                if (dist < 8.0f) {
                    setState(EntityState.WALK);
                } else {
                    setState(EntityState.IDLE);
                }
            } else {
                body.setLinearVelocity(toPlayer.nor().scl(0.9f));

                if (dist < 1.8f && cooldownTimer >= attackCooldown) {
                    player.takeDamage();
                    cooldownTimer = 0f;
                }
            }
        } else {
            if (dist < 1.8f && cooldownTimer >= attackCooldown) {
                setState(EntityState.ATTACK);
                cooldownTimer = 0f;
                body.setLinearVelocity(toPlayer.nor().scl(0.9f));
            } else if (dist < 8.0f) {
                if (cooldownTimer < attackCooldown) {
                    setState(EntityState.WALK);
                    body.setLinearVelocity(toPlayer.nor().scl(0.9f));
                } else {
                    setState(EntityState.WALK);
                    body.setLinearVelocity(toPlayer.nor().scl(speed));
                }
            } else {
                setState(EntityState.IDLE);
                body.setLinearVelocity(0, 0);
            }
        }

        if (toPlayer.x != 0) isFacingLeft = toPlayer.x < 0;
    }

    public void takeDamage(int dmg) {
        if (isDead) return;
        health -= dmg;
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
        if (MathUtils.random() <= 0.05f) {
            float posX = (body != null ? body.getPosition().x : 0f) * B2DVars.PPM;
            float posY = (body != null ? body.getPosition().y : 0f) * B2DVars.PPM;

            PowerUp.Type type;
            int rand = MathUtils.random(1, 3);
            if (rand == 1) {
                type = PowerUp.Type.HEAL;
            } else if (rand == 2) {
                type = PowerUp.Type.SHIELD;
            } else {
                type = PowerUp.Type.ONE_SHOT;
            }

            droppedPowerUp = new PowerUp(posX, posY, type);
            return droppedPowerUp;
        }
        return null;
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
                anim = Assets.zombieAttack;
                isLooping = false;
                break;
            case DEATH:
                anim = Assets.zombieDeath;
                isLooping = false;
                break;
            case WALK:
                anim = Assets.zombieWalk;
                break;
            default:
                anim = Assets.zombieIdle;
                break;
        }

        TextureRegion frame = anim.getKeyFrame(stateTime, isLooping);
        float x = body != null ? (body.getPosition().x * B2DVars.PPM) : 0f;
        float y = body != null ? (body.getPosition().y * B2DVars.PPM) : 0f;

        batch.draw(frame, isFacingLeft ? x + 32 : x - 32, y - 32, isFacingLeft ? -64 : 64, 64);
    }

    public PowerUp getPowerUp() {
        return droppedPowerUp;
    }
}