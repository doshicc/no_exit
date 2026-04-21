package com.mygdx.game.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.B2DVars;

public class LevelManager {
    private World world;

    // Текстуры пола
    private TextureRegion floorDefault;
    private Array<TextureRegion> floorDetails;
    private int[][] floorMap;

    // Текстуры стен
    private TextureRegion upWall;
    private TextureRegion downWall;
    private TextureRegion leftRightWall;

    // Текстуры объектов
    private TextureRegion crateTex;
    private Array<Vector2> cratePositions;

    private float tileSize = 64f;

    public LevelManager(World world) {
        this.world = world;
        this.floorDetails = new Array<>();
        this.cratePositions = new Array<>();
        loadAssets();
    }

    private void loadAssets() {
        // Полы
        floorDefault = new TextureRegion(new Texture("level/floor/floor_default.png"));
        floorDetails.add(new TextureRegion(new Texture("level/floor/floor1.png")));
        floorDetails.add(new TextureRegion(new Texture("level/floor/floor2.png")));
        floorDetails.add(new TextureRegion(new Texture("level/floor/floor3.png")));

        // Стены (теперь три разных файла из assets/level/wall/)
        upWall = new TextureRegion(new Texture("level/wall/upWall.png"));
        downWall = new TextureRegion(new Texture("level/wall/downWall.png"));
        leftRightWall = new TextureRegion(new Texture("level/wall/leftRightWall.png"));

        // Объекты
        crateTex = new TextureRegion(new Texture("level/crate.png"));
    }

    public void createRoom(float width, float height) {
        int cols = (int) (width / tileSize);
        int rows = (int) (height / tileSize);
        floorMap = new int[cols][rows];
        cratePositions.clear();

        // 1. Генерация карты пола (80% дефолт, 20% детали)
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                if (MathUtils.random() < 0.20f) {
                    floorMap[x][y] = MathUtils.random(1, 3);
                } else {
                    floorMap[x][y] = 0;
                }
            }
        }

        // 2. Создание физических тел стен по периметру
        // Горизонтальные (Верх и Низ)
        for (float x = 0; x < width; x += tileSize) {
            createStaticRect(x + tileSize / 2, tileSize / 2, tileSize / 2, tileSize / 2); // Низ
            createStaticRect(x + tileSize / 2, height - tileSize / 2, tileSize / 2, tileSize / 2); // Верх
        }
        // Вертикальные (Лево и Право)
        for (float y = tileSize; y < height - tileSize; y += tileSize) {
            createStaticRect(tileSize / 2, y + tileSize / 2, tileSize / 2, tileSize / 2); // Лево
            createStaticRect(width - tileSize / 2, y + tileSize / 2, tileSize / 2, tileSize / 2); // Право
        }

        // 3. Генерация ящиков
        for (float x = tileSize * 2; x < width - tileSize * 2; x += tileSize) {
            for (float y = tileSize * 2; y < height - tileSize * 2; y += tileSize) {
                // Зона безопасности игрока
                if (Math.abs(x - width / 2) < 150 && Math.abs(y - height / 2) < 150) continue;

                if (MathUtils.random() < 0.12f) {
                    createStaticRect(x, y, 20, 20); // Физический размер ящика
                    cratePositions.add(new Vector2(x, y));
                }
            }
        }
    }

    private void createStaticRect(float x, float y, float hw, float hh) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x / B2DVars.PPM, y / B2DVars.PPM);
        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hw / B2DVars.PPM, hh / B2DVars.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_WALL;
        body.createFixture(fdef).setUserData("wall");
        shape.dispose();
    }

    public void draw(SpriteBatch batch, float width, float height) {
        // 1. Отрисовка пола
        for (int x = 0; x < floorMap.length; x++) {
            for (int y = 0; y < floorMap[0].length; y++) {
                int type = floorMap[x][y];
                TextureRegion reg = (type == 0) ? floorDefault : floorDetails.get(type - 1);
                batch.draw(reg, x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }

        // 2. Отрисовка стен с использованием разных текстур
        for (float x = 0; x < width; x += tileSize) {
            batch.draw(downWall, x, 0, tileSize, tileSize); // Нижняя стена
            batch.draw(upWall, x, height - tileSize, tileSize, tileSize); // Верхняя стена
        }

        for (float y = tileSize; y < height - tileSize; y += tileSize) {
            batch.draw(leftRightWall, 0, y, tileSize, tileSize); // Левая стена
            batch.draw(leftRightWall, width - tileSize, y, tileSize, tileSize); // Правая стена
        }

        // 3. Отрисовка ящиков
        for (Vector2 p : cratePositions) {
            batch.draw(crateTex, p.x - 32, p.y - 32, 64, 64);
        }
    }

    public void dispose() {
        floorDefault.getTexture().dispose();
        for (TextureRegion tr : floorDetails) tr.getTexture().dispose();
        upWall.getTexture().dispose();
        downWall.getTexture().dispose();
        leftRightWall.getTexture().dispose();
        crateTex.getTexture().dispose();
    }
}