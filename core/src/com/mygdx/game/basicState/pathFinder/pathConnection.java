package com.mygdx.game.basicState.pathFinder;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class pathConnection implements Connection<pathNodes> {
    pathNodes fromCellHex;
    pathNodes toCellHex;
    float cost;

    public pathConnection(pathNodes fromCell, pathNodes toCell){
        this.fromCellHex = fromCell;
        this.toCellHex = toCell;
        cost = Vector2.dst(fromCellHex.coordinates.x, fromCellHex.coordinates.y, toCellHex.coordinates.x, toCellHex.coordinates.y);

    }

    @Override
    public float getCost() {
        if (toCellHex.cellType == 5){
            return cost / 10;
        }
        if (toCellHex.cellType == 10){
            return cost * 10000;
        }
        else {
            return cost * 10;
        }
    }

    @Override
    public pathNodes getFromNode() {
        return fromCellHex;
    }

    @Override
    public pathNodes getToNode() {
        return toCellHex;
    }

    public boolean equals(pathConnection that) {
        return fromCellHex.equals(that.getFromNode()) && toCellHex.equals(that.getToNode());
    }

    public void render(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(255, 0, 0, 1);
        shapeRenderer.rectLine(fromCellHex.coordinates.x, fromCellHex.coordinates.y, toCellHex.coordinates.x, toCellHex.coordinates.y, 4);

    }
}

