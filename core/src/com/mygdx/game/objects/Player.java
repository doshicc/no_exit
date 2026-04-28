package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.B2DVars;

public class Player {
    public Body body;
    private Animation<TextureRegion> idleAnim, walkAnim, attackAnim;
    private float stateTime = 0;

    private boolean isAttacking = false;
    private Vector2 mousePos;
    private Vector2 lookDirection;

    public Player(World world, float x, float y) {
        this.mousePos = new Vector2();
        this.lookDirection = new Vector2(1, 0);

        loadAnimations();
        createPhysics(world, x, y);
    }

    private void loadAnimations() {
        // Загрузка IDLE (2 кадра по твоей структуре)
        Array<TextureRegion> idleFrames = new Array<>();
        for (int i = 1; i <= 2; i++) {
            idleFrames.add(new TextureRegion(new Texture("player/idle/idle" + i + ".png")));
        }
        idleAnim = new Animation<>(0.3f, idleFrames, Animation.PlayMode.LOOP);

        // Загрузка WALK (6 кадров по твоей структуре)
        Array<TextureRegion> walkFrames = new Array<>();
        for (int i = 1; i <= 6; i++) {
            walkFrames.add(new TextureRegion(new Texture("player/walk/walk" + i + ".png")));
        }
        walkAnim = new Animation<>(0.1f, walkFrames, Animation.PlayMode.LOOP);

        // Загрузка ATTACK (5 кадров)
        Array<TextureRegion> attackFrames = new Array<>();
        for (int i = 1; i <= 5; i++) {
            attackFrames.add(new TextureRegion(new Texture("player/attack/attack" + i + ".png")));
        }
        attackAnim = new Animation<>(0.07f, attackFrames, Animation.PlayMode.NORMAL);
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
        fdef.friction = 0.5f;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;

        body.createFixture(fdef).setUserData("player");
        shape.dispose();
    }

    public void handleInput(Vector2 move) {
        float speed = 4.5f;
        body.setLinearVelocity(move.scl(speed));
    }

    public void update(float dt, Vector2 currentMousePos) {
        stateTime += dt;
        this.mousePos.set(currentMousePos);

        // Направление взгляда для хитбокса атаки
        lookDirection.set(mousePos).sub(body.getPosition().scl(B2DVars.PPM)).nor();

        if (isAttacking && attackAnim.isAnimationFinished(stateTime)) {
            isAttacking = false;
        }
    }

    public void attack() {
        if (!isAttacking) {
            isAttacking = true;
            stateTime = 0;
        }
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (isAttacking) {
            currentFrame = attackAnim.getKeyFrame(stateTime);
        } else if (body.getLinearVelocity().len() > 0.1f) {
            currentFrame = walkAnim.getKeyFrame(stateTime);
        } else {
            currentFrame = idleAnim.getKeyFrame(stateTime);
        }

        // --- ИСПРАВЛЕНИЕ РАЗМЕРА ---
        // Укажи здесь нужный размер в пикселях (например, 64x64)
        float drawWidth = 64f;
        float drawHeight = 64f;

        // Центрируем спрайт относительно узкого физического тела
        float x = (body.getPosition().x * B2DVars.PPM) - drawWidth / 2;
        float y = (body.getPosition().y * B2DVars.PPM) - drawHeight / 2;

        boolean flip = lookDirection.x < 0;

        // Рисуем с фиксированным размером drawWidth/drawHeight
        if (flip) {
            batch.draw(currentFrame, x + drawWidth, y, -drawWidth, drawHeight);
        } else {
            batch.draw(currentFrame, x, y, drawWidth, drawHeight);
        }
    }
    public Vector2 getLookDirection() {
        return lookDirection;
    }
}