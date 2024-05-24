package com.mygdx.game.basicState.Map;

import static com.mygdx.game.basicState.gameConfig.GlobalMapSIZE;

import com.mygdx.game.basicState.City.CityManager;
import com.mygdx.game.basicState.Map.Creation.CreatingGlobalMap;
import com.mygdx.game.basicState.pathFinder.pathManager;
import com.mygdx.game.basicState.pathFinder.pathNodes;

public class GlobalHexagonMap {
    public int[][][] GlobalMap;
    public GlobalHexagonalCell[][] HexagonMap = new GlobalHexagonalCell[GlobalMapSIZE][GlobalMapSIZE];
    public GlobalHexagonMap(int HexRadius){
        CreatingGlobalMap crMap = new CreatingGlobalMap();

        GlobalMap = crMap.FinishedMap;
        for (int i = 0; i < GlobalMapSIZE; i++) {
            for (int j = 0; j < GlobalMapSIZE; j++) {
                float x = j * HexRadius * 1.5f;
                float y = i * HexRadius * 2.0f;
                if (j % 2 != 0) {
                    y += HexRadius;
                }
                HexagonMap[i][j] = new GlobalHexagonalCell(x, y, HexRadius * 2.0f, HexRadius * 2.0f, HexRadius, GlobalMap[i][j], i, j);
            }
        }
    }
    public void reloadingTheRoad(CityManager CM, pathManager PM){
        for (pathNodes cell : PM.RoadRoutes) {
            HexagonMap[(int) cell.coordinatesInMatrix.x][(int) cell.coordinatesInMatrix.y].informationAboutRoad = "0";
        }

        for (pathNodes cell : PM.RoadRoutes) {
            int i = (int) cell.coordinatesInMatrix.x;
            int j = (int) cell.coordinatesInMatrix.y;
            int[][] directions;
            if (j % 2 == 0) {
                directions = new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
            } else {
                directions = new int[][]{{1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 0}, {0, -1}};
            }

            StringBuilder sides = new StringBuilder();
            for (int z = 0; z < directions.length; z++) {
                int ni = i + directions[z][0];
                int nj = j + directions[z][1];
                if (ni >= 0 && ni < HexagonMap.length && nj >= 0 && nj < HexagonMap[i].length) {
                    if (!HexagonMap[ni][nj].informationAboutRoad.equals("")) {
                        sides.append(z + 1);
                    }
                }
            }
            if (sides.length() >= 5){
                HexagonMap[(int) cell.coordinatesInMatrix.x][(int) cell.coordinatesInMatrix.y].informationAboutRoad = "";
                HexagonMap[(int) cell.coordinatesInMatrix.x][(int) cell.coordinatesInMatrix.y].changTexture();
                continue;}
            HexagonMap[(int) cell.coordinatesInMatrix.x][(int) cell.coordinatesInMatrix.y].informationAboutRoad = sides.toString();
            HexagonMap[(int) cell.coordinatesInMatrix.x][(int) cell.coordinatesInMatrix.y].changTexture();
        }
    }
}
