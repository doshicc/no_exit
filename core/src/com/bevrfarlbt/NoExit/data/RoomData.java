package com.bevrfarlbt.NoExit.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class RoomData {
    public Vector2 position;
    public float width, height;
    public Array<Body> bodies = new Array<>();
    public Array<Body> shelfBodies = new Array<>();
    public int[][] floorMap;

    public RoomData(float x, float y, float w, float h) {
        this.position = new Vector2(x, y);
        this.width = w;
        this.height = h;
    }

    public void destroy(World world) {
        for (Body b : bodies) {
            if (b != null) world.destroyBody(b);
        }
        bodies.clear();
        shelfBodies.clear();
        floorMap = null;
    }
}