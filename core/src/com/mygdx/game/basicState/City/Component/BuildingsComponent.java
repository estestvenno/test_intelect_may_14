package com.mygdx.game.basicState.City.Component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.basicState.Type.BuildingsType;

import java.util.HashMap;
import java.util.Map;

public class BuildingsComponent implements Component {
    public HashMap<BuildingsType, Array<Vector2>> Buildings;

    public BuildingsComponent() {
        Buildings = new HashMap<BuildingsType, Array<Vector2>>();
    }

    public void addBuilding(Vector2 BuildPoint, BuildingsType BuildType){
        Array<Vector2> points = Buildings.get(BuildType);
        if (points == null){
            points = new Array<Vector2>();
            Buildings.put(BuildType, points);
        }
        points.add(BuildPoint);
    }

    public Array<Vector2> returnAllBuilding(){
        Array<Vector2> allBuilding = new Array<Vector2>();
        for (Array<Vector2> points : Buildings.values()) {
            for (Vector2 point : points) {
                allBuilding.add(point);
            }
        }
        return allBuilding;
    }

    public HashMap<BuildingsType, Integer> getNumberOfJobs() {
        HashMap<BuildingsType, Integer> Otv = new HashMap<>();
        for (Map.Entry<BuildingsType, Array<Vector2>> entry : Buildings.entrySet()) {
            Otv.put(entry.getKey(), entry.getValue().size);
        }
        return Otv;
    }
}
