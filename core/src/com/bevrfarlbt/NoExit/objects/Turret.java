package com.bevrfarlbt.NoExit.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bevrfarlbt.NoExit.Assets;
import com.bevrfarlbt.NoExit.B2DVars;
import com.bevrfarlbt.NoExit.enums.EntityState;

public class Turret {
    private final Vector2 position;
    private float stateTime = 0;
    private EntityState currentState = EntityState.IDLE;

    private float shootTimer = 0f;
    private final float shootInterval = 3.0f;
    private final float attackRadius = 4.0f;

    private float lifeTimer = 15.0f;
    public boolean isDestroyed = false;
    private boolean faceLeft = false;

    // Новые поля для ХП и отрисовки выстрела
    private int health = 5; // Зомби могут сломать турель раньше времени кулаками
    private final ShapeRenderer lineRenderer;
    private Vector2 lastTargetPosPx = null;
    private float beamVisibleTimer = 0f;

    public Turret(float x, float y) {
        this.position = new Vector2(x, y);
        this.lineRenderer = new ShapeRenderer();
    }

    public void update(float dt, Array<EnemyZombie> zombies) {
        stateTime += dt;

        if (beamVisibleTimer > 0) {
            beamVisibleTimer -= dt;
        }

        if (currentState == EntityState.DEATH) {
            if (Assets.turretDeath.isAnimationFinished(stateTime)) {
                isDestroyed = true;
            }
            return;
        }

        lifeTimer -= dt;
        if (lifeTimer <= 0 || health <= 0) {
            setState(EntityState.DEATH);
            return;
        }

        if (currentState == EntityState.ATTACK && Assets.turretAttack.isAnimationFinished(stateTime)) {
            setState(EntityState.IDLE);
        }

        shootTimer += dt;
        if (shootTimer >= shootInterval) {
            if (tryShoot(zombies)) {
                shootTimer = 0f;
            }
        }
    }

    private boolean tryShoot(Array<EnemyZombie> zombies) {
        EnemyZombie closestZombie = null;
        float closestDist = attackRadius;
        Vector2 turretPosInMeters = position.cpy().scl(1f / B2DVars.PPM);

        for (EnemyZombie z : zombies) {
            if (z.isDead) continue;
            float dist = turretPosInMeters.dst(z.body.getPosition());
            if (dist < closestDist) {
                closestDist = dist;
                closestZombie = z;
            }
        }

        if (closestZombie != null) {
            setState(EntityState.ATTACK);
            closestZombie.takeDamage(1);

            // Фиксируем координаты для отрисовки лазерного луча / линии пули
            lastTargetPosPx = closestZombie.body.getPosition().cpy().scl(B2DVars.PPM);
            beamVisibleTimer = 0.15f; // Линия видна 0.15 секунды

            if (closestZombie.body.getPosition().x < turretPosInMeters.x) {
                faceLeft = true;
            } else {
                faceLeft = false;
            }
            return true;
        }
        return false;
    }

    public void takeDamage(int dmg) {
        if (currentState == EntityState.DEATH) return;
        health -= dmg;
        if (health <= 0) {
            setState(EntityState.DEATH);
        }
    }

    public void setState(EntityState state) {
        if (currentState != state) {
            currentState = state;
            stateTime = 0;
        }
    }

    public void draw(SpriteBatch batch) {
        Animation<TextureRegion> anim;
        boolean isLooping = true;

        switch (currentState) {
            case ATTACK:
                anim = Assets.turretAttack;
                isLooping = false;
                break;
            case DEATH:
                anim = Assets.turretDeath;
                isLooping = false;
                break;
            default:
                anim = Assets.turretIdle;
                break;
        }

        TextureRegion frame = anim.getKeyFrame(stateTime, isLooping);
        batch.draw(frame, faceLeft ? position.x + 32 : position.x - 32, position.y - 32, faceLeft ? -64 : 64, 64);

        // Рисуем вспышку / лазерную траекторию, если турель только что выстрелила
        if (beamVisibleTimer > 0 && lastTargetPosPx != null) {
            batch.end(); // Приостанавливаем спрайтбатч для рисования примитивов

            lineRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            lineRenderer.begin(ShapeRenderer.ShapeType.Line);
            lineRenderer.setColor(Color.CYAN); // Цвет луча
            // Стреляем из дула (примерно по центру турели)
            lineRenderer.line(position.x, position.y + 10, lastTargetPosPx.x, lastTargetPosPx.y);
            lineRenderer.end();

            batch.begin(); // Возобновляем спрайтбатч
        }
    }

    public Vector2 getPosition() { return position; }
    public EntityState getCurrentState() { return currentState; }
    public void dispose() { lineRenderer.dispose(); }
}