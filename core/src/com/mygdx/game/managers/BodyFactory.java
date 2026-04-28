package com.mygdx.game.managers;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.B2DVars;

public class BodyFactory {
    private World world;

    public BodyFactory(World world) {
        this.world = world;
    }

    public Body createRect(float x, float y, float width, float height, boolean isStatic, float density, float damping, String userData) {
        BodyDef bdef = new BodyDef();
        bdef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        bdef.position.set(x / B2DVars.PPM, y / B2DVars.PPM);
        bdef.linearDamping = damping;

        Body body = world.createBody(bdef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / B2DVars.PPM, height / 2 / B2DVars.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = density;
        fdef.filter.categoryBits = isStatic ? B2DVars.BIT_WALL : (short)1; // Пример фильтрации

        body.createFixture(fdef).setUserData(userData);
        body.setUserData(userData);
        shape.dispose();
        return body;
    }
}