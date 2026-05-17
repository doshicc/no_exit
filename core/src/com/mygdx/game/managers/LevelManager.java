package com.mygdx.game.managers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.data.RoomData;

public class LevelManager {
    private final BodyFactory bodyFactory;
    private final float tileSize = 64f;

    public LevelManager(World world) {
        this.bodyFactory = new BodyFactory(world);
    }

    public RoomData createRoom(float offsetX, float offsetY, float width, float height, boolean isFirstRoom) {
        RoomData room = new RoomData(offsetX, offsetY, width, height);
        int cols = (int) (width / tileSize);
        int rows = (int) (height / tileSize);
        room.floorMap = new int[cols][rows];

        // Генерация пола
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                room.floorMap[x][y] = (MathUtils.random() < 0.20f) ? MathUtils.random(1, 3) : 0;
            }
        }

        // Стены
        for (float x = 0; x < width; x += tileSize) {
            room.bodies.add(bodyFactory.createRect(offsetX + x + tileSize/2, offsetY + tileSize/2, tileSize, tileSize, true, 0, 0, "wall_low"));
            room.bodies.add(bodyFactory.createRect(offsetX + x + tileSize/2, offsetY + height - tileSize/2, tileSize, tileSize, true, 0, 0, "wall_up"));
        }

        for (float y = 0; y < height; y += tileSize) {
            if (isFirstRoom || (y < height/2 - tileSize || y > height/2 + tileSize)) {
                room.bodies.add(bodyFactory.createRect(offsetX + tileSize/2, offsetY + y + tileSize/2, tileSize, tileSize, true, 0, 0, "wall_side"));
            }
            if (y < height/2 - tileSize || y > height/2 + tileSize) {
                room.bodies.add(bodyFactory.createRect(offsetX + width - tileSize/2, offsetY + y + tileSize/2, tileSize, tileSize, true, 0, 0, "wall_side"));
            }
        }

        int objectsCount = MathUtils.random(5, 10);
        for (int i = 0; i < objectsCount; i++) {
            float ox = offsetX + MathUtils.random(tileSize * 2, width - tileSize * 2);
            float oy = offsetY + MathUtils.random(tileSize * 2, height - tileSize * 2);

            if (MathUtils.randomBoolean()) {
                room.bodies.add(bodyFactory.createRect(ox, oy, 25, 25, true, 0, 0, "box"));
            } else {
                room.bodies.add(bodyFactory.createRect(ox, oy, 80, 25, true, 0, 0, "shelf"));
            }
        }
        return room;
    }
}