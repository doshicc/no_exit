package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.B2DVars;

public class EnemyZombie {
    public Body body;
    private Animation<TextureRegion> idleAnim, attackAnim, deathAnim;
    private float stateTime = 0;

    public enum State { IDLE, ATTACK, DEATH }
    public State currentState = State.IDLE;

    private boolean isFacingLeft = false;
    public boolean isDead = false;
    private int health = 3;
    private float speed = 1.8f; // Скорость преследования

    public EnemyZombie(World world, float x, float y) {
        loadAnimations();
        createPhysics(world, x, y);
    }

    private void loadAnimations() {
        Array<TextureRegion> idleFrames = new Array<>();
        for (int i = 1; i <= 4; i++) {
            idleFrames.add(new TextureRegion(new Texture("zombie/idle/idle" + i + ".png")));
        }
        idleAnim = new Animation<>(0.2f, idleFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> attackFrames = new Array<>();
        for (int i = 1; i <= 5; i++) {
            attackFrames.add(new TextureRegion(new Texture("zombie/attack/attack" + i + ".png")));
        }
        attackAnim = new Animation<>(0.15f, attackFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> deathFrames = new Array<>();
        for (int i = 1; i <= 3; i++) {
            deathFrames.add(new TextureRegion(new Texture("zombie/death/death" + i + ".png")));
        }
        deathAnim = new Animation<>(0.25f, deathFrames, Animation.PlayMode.NORMAL);
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
        fdef.filter.categoryBits = B2DVars.BIT_WALL;
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

        // Поведение ИИ
        if (dist < 1.8f) { // Дистанция атаки (метры Box2D)
            currentState = State.ATTACK;
            body.setLinearVelocity(0, 0);
        } else if (dist < 12.0f) { // Дистанция агро (заметить игрока)
            currentState = State.IDLE; // Пока нет анимки ходьбы, юзаем idle
            body.setLinearVelocity(toPlayer.nor().scl(speed));
        } else {
            currentState = State.IDLE;
            body.setLinearVelocity(0, 0);
        }

        if (toPlayer.x != 0) isFacingLeft = toPlayer.x < 0;
    }

    public void takeDamage(int dmg) {
        if (isDead) return;
        health -= dmg;
        if (health <= 0) die();
    }

    private void die() {
        isDead = true;
        currentState = State.DEATH;
        stateTime = 0;
        body.getFixtureList().first().setSensor(true);
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;
        switch (currentState) {
            case ATTACK: currentFrame = attackAnim.getKeyFrame(stateTime); break;
            case DEATH: currentFrame = deathAnim.getKeyFrame(stateTime); break;
            default: currentFrame = idleAnim.getKeyFrame(stateTime); break;
        }

        batch.draw(currentFrame,
                (body.getPosition().x * B2DVars.PPM) - 32,
                (body.getPosition().y * B2DVars.PPM) - 32,
                32, 32, 64, 64,
                isFacingLeft ? -1 : 1, 1, 0);
    }
}