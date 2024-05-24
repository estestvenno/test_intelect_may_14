package com.mygdx.game.basicState.pathFinder;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.basicState.City.Component.CityComponent;
import com.mygdx.game.basicState.Map.GlobalHexagonalCell;

public class pathManager {
    public RoadGameGraph GlobalGraph;
    public final RoadVariationHeuristic herRoad;
    public Array<pathNodes> RoadRoutes;

    private pathManager(RoadGameGraph graph, RoadVariationHeuristic herRoad) {
        this.GlobalGraph = graph;
        this.herRoad = herRoad;
    }

    public static pathManager createPathManagerByMap(GlobalHexagonalCell[][] map) {
        pathNodes[][] pointRoad = pathMapTools.getRoadNodes(map);
        RoadGameGraph RoadWayBetweenCities = new RoadGameGraph(pointRoad);
        RoadVariationHeuristic herRoad = new RoadVariationHeuristic();

        return new pathManager(RoadWayBetweenCities, herRoad);
    }

    public void LookAWayACity(Array<Entity> listCity) {
        // строим маршруты дорог
        Array<pathNodes> HarvestingRoadNodes = new Array<>();

        for (int i = 0; i < listCity.size - 1; i++) {
            CityComponent cityComponentA = listCity.get(i).getComponent(CityComponent.class);
            pathNodes cityA = new pathNodes(cityComponentA.x, cityComponentA.y);
            CityComponent cityComponentB = listCity.get(i + 1).getComponent(CityComponent.class);
            pathNodes cityB = new pathNodes(cityComponentB.x, cityComponentB.y);


            GraphPath<pathNodes> pointRoadWayBetweenCities = GlobalGraph.findRoadPathGraph(cityA, cityB, herRoad);

            if (pointRoadWayBetweenCities == null) {
                continue;
            }


            int k = -1;
            for (pathNodes a : pointRoadWayBetweenCities) {
                a.cellType = 5;
                HarvestingRoadNodes.add(a);
            }
        }

        RoadRoutes = HarvestingRoadNodes;
    }


}
