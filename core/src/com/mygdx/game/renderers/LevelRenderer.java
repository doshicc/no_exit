package com.mygdx.game.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.Assets;
import com.mygdx.game.B2DVars;
import com.mygdx.game.data.RoomData;

public class LevelRenderer {
    private final float tileSize = 64f;

    public void render(SpriteBatch batch, RoomData room) {
        drawFloor(batch, room);

        drawObjects(batch, room);
    }

    private void drawFloor(SpriteBatch batch, RoomData room) {
        for (int x = 0; x < room.floorMap.length; x++) {
            for (int y = 0; y < room.floorMap[0].length; y++) {
                TextureRegion tile;
                int tileType = room.floorMap[x][y];

                // Выбираем текстуру пола на основе данных из RoomData
                if (tileType > 0 && tileType <= Assets.floorDetails.size) {
                    tile = Assets.floorDetails.get(tileType - 1);
                } else {
                    tile = Assets.floorDefault;
                }

                batch.draw(tile,
                        room.position.x + x * tileSize,
                        room.position.y + y * tileSize,
                        tileSize, tileSize);
            }
        }
    }

    private void drawObjects(SpriteBatch batch, RoomData room) {
        for (Body b : room.bodies) {
            String type = (String) b.getUserData();
            if (type == null) continue;

            float x = b.getPosition().x * B2DVars.PPM;
            float y = b.getPosition().y * B2DVars.PPM;

            switch (type) {
                case "shelf":
                    batch.draw(Assets.shelf, x - 48, y - 32, 96, 64);
                    break;

                case "box":
                    // Хитбокс 25x25, спрайт 40x40. Центрируем.
                    batch.draw(Assets.box, x - 20, y - 20, 40, 40);
                    break;

                case "wall_up":
                    // Стены статичны 64x64, тут хитбокс совпадает со спрайтом.
                    batch.draw(Assets.upWall, x - 32, y - 32, 64, 64);
                    break;

                case "wall_low":
                    batch.draw(Assets.downWall, x - 32, y - 32, 64, 64);
                    break;

                case "wall_side":
                    batch.draw(Assets.leftRightWall, x - 32, y - 32, 64, 64);
                    break;
            }
        }
    }
}