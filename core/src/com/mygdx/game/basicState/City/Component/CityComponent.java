package com.mygdx.game.basicState.City.Component;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.basicState.Type.ResourceType;

import java.util.HashMap;

public class CityComponent implements Component {
    public String name; // название оружия

    public Boolean LifeStatus = true;
    public int cityId;

    public int x, y; // координаты города
    public int populationSize;
    public int populationArmySize = 0;
    public HashMap<ResourceType, Integer> forecastNextMove;

    public CityComponent(String name, int x, int y) {
        this.name = name;
        this.x = x; // номер в строке
        this.y = y; // строка
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public void setForecastNextMove(HashMap<ResourceType, Integer> forecastNextMove) {
        this.forecastNextMove = forecastNextMove;
    }


}
