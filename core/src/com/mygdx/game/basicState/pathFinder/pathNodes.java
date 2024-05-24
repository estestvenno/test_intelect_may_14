package com.mygdx.game.basicState.pathFinder;

import com.badlogic.gdx.math.Vector2;

public class pathNodes {
    public Vector2 coordinates;
    public Vector2 coordinatesInMatrix;
    public int index;
    public int cellType = 0;

    public pathNodes(int x, int y){
        coordinatesInMatrix = new Vector2(x, y);
        float x_n = y * 256 * 0.75f;
        float y_n = x * 256;
        if (y % 2 != 0) {
            y_n += (float) 256 / 2;
        }
        coordinates = new Vector2((x_n + 128), (y_n + 128) / 2);
    }

    public pathNodes(float x, float y){
        coordinates = new Vector2(x, y);
    }

    public void setIndex(int index){
        this.index = index;
    }

    public boolean equals(pathNodes that) {
        return that.coordinatesInMatrix.x == coordinatesInMatrix.x && that.coordinatesInMatrix.y == coordinatesInMatrix.y;
    }
}

