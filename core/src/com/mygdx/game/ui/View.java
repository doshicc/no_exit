package com.mygdx.game.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public class View {
    public float x, y;
    public float width, height;

    public View(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = 0;
        this.height = 0;
    }

    public View(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void draw(Batch batch) {
    }

    public boolean contains(float pointX, float pointY) {
        return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}