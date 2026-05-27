package com.bevrfarlbt.NoExit.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class TextView extends View {

    private BitmapFont font;
    private String text;

    public TextView(BitmapFont font, float x, float y) {
        super(x, y);
        this.font = font;
    }

    public TextView(BitmapFont font, float x, float y, String text) {
        this(font, x, y);
        this.text = text;

        if (font != null && text != null) {
            GlyphLayout glyphLayout = new GlyphLayout(font, text);
            this.width = glyphLayout.width;
            this.height = glyphLayout.height;
        }
    }

    @Override
    public void draw(Batch batch) {
        if (font != null && text != null) {
            font.draw(batch, text, x, y + height);
        }
    }

    public void setText(String text) {
        this.text = text;
        if (font != null && text != null) {
            GlyphLayout glyphLayout = new GlyphLayout(font, text);
            this.width = glyphLayout.width;
            this.height = glyphLayout.height;
        }
    }
}