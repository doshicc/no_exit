package com.bevrfarlbt.NoExit.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.bevrfarlbt.NoExit.Assets;

public class PowerUp {
    public enum Type { HEAL, SHIELD, ONE_SHOT }

    public Vector2 basePosition;
    public Vector2 drawPosition;
    public Type type;
    private TextureRegion texture;
    private float size = 32f;

    private float floatingTimer = 0f;
    private float amplitude = 5f;
    private float speed = 4f;

    public PowerUp(float x, float y, Type type) {
        this.basePosition = new Vector2(x, y);
        this.drawPosition = new Vector2(x, y);
        this.type = type;
        this.floatingTimer = MathUtils.random(0f, 10f);

        switch (type) {
            case HEAL:
                this.texture = Assets.powerupHeal;
                break;
            case SHIELD:
                this.texture = Assets.extraHeart;
                break;
            case ONE_SHOT:
                this.texture = Assets.powerupOneShot;
                break;
        }
    }

    public void update(float dt) {
        floatingTimer += dt;
        float offset = MathUtils.sin(floatingTimer * speed) * amplitude;
        drawPosition.y = basePosition.y + offset;
        drawPosition.x = basePosition.x;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, drawPosition.x - size / 2, drawPosition.y - size / 2, size, size);
    }
}