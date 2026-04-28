package com.mygdx.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.B2DVars;
import com.mygdx.game.objects.RoomData;

public class LevelManager {
    private World world;
    public TextureRegion floorDefault, upWall, downWall, leftRightWall, boxTexture, shelfTexture;
    private Array<TextureRegion> floorDetails = new Array<>();
    private Array<Texture> loadedTextures = new Array<>();
    private float tileSize = 64f;

    public LevelManager(World world) {
        this.world = world;
        loadAssets();
    }

    private void loadAssets() {
        // Убедись, что все пути верны и файлы лежат в assets
        String[] paths = {
                "level/floor/floor_default.png", "level/floor/floor1.png",
                "level/floor/floor2.png", "level/floor/floor3.png",
                "level/wall/upWall.png", "level/wall/downWall.png",
                "level/wall/leftRightWall.png", "level/objects/box.png",
                "level/objects/shelf.png"
        };
        for (String path : paths) {
            Texture t = new Texture(path);
            loadedTextures.add(t);
        }
        floorDefault = new TextureRegion(loadedTextures.get(0));
        floorDetails.add(new TextureRegion(loadedTextures.get(1)));
        floorDetails.add(new TextureRegion(loadedTextures.get(2)));
        floorDetails.add(new TextureRegion(loadedTextures.get(3)));
        upWall = new TextureRegion(loadedTextures.get(4));
        downWall = new TextureRegion(loadedTextures.get(5));
        leftRightWall = new TextureRegion(loadedTextures.get(6));
        boxTexture = new TextureRegion(loadedTextures.get(7));
        shelfTexture = new TextureRegion(loadedTextures.get(8));
    }

    public RoomData createRoom(float offsetX, float offsetY, float width, float height, boolean isFirstRoom) {
        RoomData room = new RoomData(offsetX, offsetY, width, height);
        int cols = (int) (width / tileSize);
        int rows = (int) (height / tileSize);
        room.floorMap = new int[cols][rows];

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                room.floorMap[x][y] = (MathUtils.random() < 0.20f) ? MathUtils.random(1, 3) : 0;
            }
        }

        // Стены (горизонтальные)
        for (float x = 0; x < width; x += tileSize) {
            room.bodies.add(createStaticRect(offsetX + x + tileSize/2, offsetY + tileSize/2, tileSize/2, tileSize/2, "wall_low"));
            room.bodies.add(createStaticRect(offsetX + x + tileSize/2, offsetY + height - tileSize/2, tileSize/2, tileSize/2, "wall_up"));
        }
        // Стены (вертикальные)
        for (float y = 0; y < height; y += tileSize) {
            if (isFirstRoom || (y < height/2 - tileSize || y > height/2 + tileSize)) {
                room.bodies.add(createStaticRect(offsetX + tileSize/2, offsetY + y + tileSize/2, tileSize/2, tileSize/2, "wall_side"));
            }
            if (y < height/2 - tileSize || y > height/2 + tileSize) {
                room.bodies.add(createStaticRect(offsetX + width - tileSize/2, offsetY + y + tileSize/2, tileSize/2, tileSize/2, "wall_side"));
            }
        }

        // Объекты
        int objectsCount = MathUtils.random(5, 10);
        for (int i = 0; i < objectsCount; i++) {
            float ox = offsetX + MathUtils.random(tileSize * 2, width - tileSize * 2);
            float oy = offsetY + MathUtils.random(tileSize * 2, height - tileSize * 2);
            if (oy > height/2 - tileSize && oy < height/2 + tileSize) oy += tileSize * 2;

            if (MathUtils.randomBoolean()) {
                room.bodies.add(createBox(ox, oy));
            } else {
                room.bodies.add(createShelf(ox, oy));
            }
        }
        return room;
    }

    public Body createStaticRect(float x, float y, float hw, float hh, String userData) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x / B2DVars.PPM, y / B2DVars.PPM);
        Body body = world.createBody(bdef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hw / B2DVars.PPM, hh / B2DVars.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_WALL;
        body.createFixture(fdef).setUserData(userData);
        body.setUserData(userData);
        shape.dispose();
        return body;
    }

    private Body createBox(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x / B2DVars.PPM, y / B2DVars.PPM);
        bdef.linearDamping = 10f;
        Body body = world.createBody(bdef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(20 / B2DVars.PPM, 20 / B2DVars.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1.0f;
        body.createFixture(fdef).setUserData("box");
        body.setUserData("box");
        shape.dispose();
        return body;
    }

    private Body createShelf(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x / B2DVars.PPM, y / B2DVars.PPM);
        bdef.linearDamping = 15f; // Полки тяжелее, затухание выше
        Body body = world.createBody(bdef);
        PolygonShape shape = new PolygonShape();

        // Хитбокс увеличен: ширина 96 (48*2), высота 64 (32*2)
        shape.setAsBox(48 / B2DVars.PPM, 32 / B2DVars.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 3.0f;
        body.createFixture(fdef).setUserData("shelf");
        body.setUserData("shelf");
        shape.dispose();
        return body;
    }

    public void drawRoom(SpriteBatch batch, RoomData room, boolean isFirstRoom) {
        if (room == null) return;
        float ox = room.position.x;
        float oy = room.position.y;

        // Пол
        for (int x = 0; x < room.floorMap.length; x++) {
            for (int y = 0; y < room.floorMap[0].length; y++) {
                int type = room.floorMap[x][y];
                TextureRegion reg = (type == 0) ? floorDefault : floorDetails.get(type - 1);
                batch.draw(reg, ox + x * tileSize, oy + y * tileSize, tileSize, tileSize);
            }
        }

        // Все объекты и стены по телам Box2D
        for (Body b : room.bodies) {
            String type = (String) b.getUserData();
            if (type == null) continue;

            float bx = b.getPosition().x * B2DVars.PPM;
            float by = b.getPosition().y * B2DVars.PPM;
            float angle = b.getAngle() * MathUtils.radDeg;

            switch (type) {
                case "wall_low":
                    batch.draw(downWall, bx - tileSize/2, by - tileSize/2, tileSize, tileSize);
                    break;
                case "wall_up":
                    batch.draw(upWall, bx - tileSize/2, by - tileSize/2, tileSize, tileSize);
                    break;
                case "wall_side":
                    batch.draw(leftRightWall, bx - tileSize/2, by - tileSize/2, tileSize, tileSize);
                    break;
                case "box":
                    batch.draw(boxTexture, bx - 20, by - 20, 20, 20, 40, 40, 1, 1, angle);
                    break;
                case "shelf":
                    // bx-48, by-32 центрирует текстуру 96x64 ровно по хитбоксу
                    batch.draw(shelfTexture, bx - 48, by - 32, 48, 32, 96, 64, 1, 1, angle);
                    break;
            }
        }
    }

    public void dispose() {
        for (Texture t : loadedTextures) t.dispose();
        loadedTextures.clear();
    }
}