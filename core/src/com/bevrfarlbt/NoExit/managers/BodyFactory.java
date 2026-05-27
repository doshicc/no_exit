package com.bevrfarlbt.NoExit.managers;

import com.badlogic.gdx.physics.box2d.*;
import com.bevrfarlbt.NoExit.B2DVars;

public class BodyFactory {
    private World world;

    public BodyFactory(World world) {
        this.world = world;
    }

    // Твой старый метод для прямоугольников
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
        fdef.filter.categoryBits = isStatic ? B2DVars.BIT_WALL : (short)1;

        body.createFixture(fdef).setUserData(userData);
        body.setUserData(userData);
        shape.dispose();
        return body;
    }

    // НОВЫЙ МЕТОД: Создание круглой физики (для зомби и сущностей)
    public Body createCircle(float x, float y, float radius, boolean isStatic, float density, String userData, Object entityInstance) {
        BodyDef bdef = new BodyDef();
        bdef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        bdef.position.set(x / B2DVars.PPM, y / B2DVars.PPM);
        bdef.fixedRotation = true;

        Body body = world.createBody(bdef);
        CircleShape shape = new CircleShape();
        shape.setRadius(radius / B2DVars.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = density;
        fdef.filter.categoryBits = (short) 2; // Категория врагов

        body.createFixture(fdef).setUserData(userData);
        body.setUserData(entityInstance); // Привязываем ссылку на конкретный класс зомби
        shape.dispose();
        return body;
    }
}