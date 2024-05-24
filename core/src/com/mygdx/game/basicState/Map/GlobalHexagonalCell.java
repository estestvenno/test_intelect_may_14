package com.mygdx.game.basicState.Map;

import static com.mygdx.game.Config.GameTexManager;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.basicState.Type.BuildingsType;

public class GlobalHexagonalCell {
    //Унивирсальный класс клетки. Используеться как для отрисовки, так и для обработки действий

    public final Vector2 center;
    private final Vector2 GlobalCenter;
    private final int hexR;
    Texture base;
    Texture details;

    // Вся информация о клетке поля
    public int informationAboutBiome;
    public int informationAboutArea;
    public String informationAboutRoad;
    public BuildingsType informationAboutBuilding;
    public int informationAboutView;

    public GlobalHexagonalCell(float x, float y, float width, float height, int hexR, int[] inf, int gy, int gx) {
        this.hexR = hexR;
        this.center = new Vector2(x + width / 2, y + height / 2);
        this.GlobalCenter = new Vector2(gx, gy);
        informationAboutBiome = inf[0]; // биом(0);
        informationAboutArea = inf[1]; // тип местности(1);
        informationAboutRoad = ""; // тип дороги(2);
        informationAboutBuilding = null; // тип здания(3); 0 нету, 1 - центр. остальные - прочие
        informationAboutView = 0; // туман войны(4)
        changTexture();
    }
    public void changTexture() {
        Texture[] Tex = GameTexManager.textureSelection(informationAboutBiome, informationAboutArea, informationAboutRoad, informationAboutBuilding, informationAboutView);
        this.base = Tex[0];
        this.details = Tex[1];
    }

    public int getPriority(){
        if (informationAboutBiome == 0){return 4;}
        if (informationAboutArea == 3){return 1;}
        return 3;
    }
    public void drawB(SpriteBatch batch) {
        float x = center.x - hexR;
        float y = center.y - hexR;
        batch.draw(base, x, y / 2, hexR * 2, hexR * 1.2f, 0, 0, hexR * 2, hexR * 2, false, false);
    }
    public void drawD(SpriteBatch batch) {
        float x = center.x - hexR;
        float y = center.y - hexR;
        batch.draw(details, x, y / 2, hexR * 2, hexR * 2, 0, 0, details.getWidth(), details.getHeight(), false, false);
    }

    public float getY(){
        return center.y / 2;
    }

    public float getX() {
        return center.x;
    }
}
