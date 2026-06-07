package com.bevrfarlbt.NoExit.data;

public class Document {
    public final int id;
    public final int chapter;
    public final String title;
    public final String text;

    public Document(int id, int chapter, String title, String text) {
        this.id = id;
        this.chapter = chapter;
        this.title = title;
        this.text = text;
    }
}