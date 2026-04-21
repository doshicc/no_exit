package com.mygdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.B2DVars;

public class Player {
    public Body body;
    private float speed = 5f;

    private Animation<TextureRegion> walkAnim;
    private Animation<TextureRegion> idleAnim;
    private Animation<TextureRegion> attackAnim;

    private float stateTime = 0;
    private float rotation = 0;
    public boolean isAttacking = false;
    private float attackTimer = 0;

    public Player(World world, float x, float y) {
        // Физика остается без изменений
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x / B2DVars.PPM, y / B2DVars.PPM);
        bdef.fixedRotation = true;
        body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(15 / B2DVars.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        body.createFixture(fdef).setUserData("player");
        shape.dispose();

        // Загружаем ВСЕ кадры
        loadAnimations();
    }

    private void loadAnimations() {
        // Idle: 2 кадра (character/idle/idle1.png, idle2.png)
        TextureRegion[] idleFrames = new TextureRegion[2];
        for (int i = 0; i < 2; i++) {
            idleFrames[i] = new TextureRegion(new Texture("character/idle/idle" + (i + 1) + ".png"));
        }
        idleAnim = new Animation<>(0.4f, idleFrames); // Медленное дыхание

        // Walk: 6 кадров (walk1.png до walk6.png)
        TextureRegion[] walkFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            walkFrames[i] = new TextureRegion(new Texture("character/walk/walk" + (i + 1) + ".png"));
        }
        // 0.1f - стандарт для бодрой походки
        walkAnim = new Animation<>(0.1f, walkFrames);

        // Attack: 5 кадров (attack1.png до attack5.png)
        TextureRegion[] atkFrames = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            atkFrames[i] = new TextureRegion(new Texture("character/attack/attack" + (i + 1) + ".png"));
        }
        // Делаем атаку очень быстрой и резкой
        attackAnim = new Animation<>(0.04f, atkFrames);
    }

    public void update(float dt, Vector2 mousePos) {
        stateTime += dt;

        float relX = mousePos.x - (body.getPosition().x * B2DVars.PPM);
        float relY = mousePos.y - (body.getPosition().y * B2DVars.PPM);
        rotation = (float) Math.toDegrees(Math.atan2(relY, relX));

        if (isAttacking) {
            attackTimer -= dt;
            if (attackTimer <= 0) {
                isAttacking = false;
            }
        }
    }

    public void handleInput(Vector2 moveDir) {
        // Если атакуем — персонаж замирает для удара (можно убрать, если хочешь бить на бегу)
        if (!isAttacking) {
            body.setLinearVelocity(moveDir.scl(speed));
        } else {
            body.setLinearVelocity(0, 0);
        }
    }

    public void attack() {
        if (isAttacking) return;
        isAttacking = true;
        stateTime = 0; // Начинаем анимацию удара с первого кадра
        attackTimer = attackAnim.getAnimationDuration();
    }

    public void draw(SpriteBatch batch) {
        TextureRegion frame;

        // Выбираем правильный кадр на основе состояния
        if (isAttacking) {
            frame = attackAnim.getKeyFrame(stateTime, false);
        } else if (body.getLinearVelocity().len() > 0.1f) {
            frame = walkAnim.getKeyFrame(stateTime, true);
        } else {
            frame = idleAnim.getKeyFrame(stateTime, true);
        }

        // Логика разворота (Flip)
        boolean flip = (rotation > 90 || rotation < -90);

        // Рисуем с учетом флипа через scaleX
        batch.draw(
                frame,
                (body.getPosition().x * B2DVars.PPM) - 32,
                (body.getPosition().y * B2DVars.PPM) - 32,
                32, 32,
                64, 64,
                flip ? -1 : 1, 1,
                0 // Угол 0, так как мы используем флип
        );
    }
}