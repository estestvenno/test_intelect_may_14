package com.mygdx.game.basicState.pathFinder;


import com.mygdx.game.basicState.Map.GlobalHexagonalCell;

public class pathMapTools {
    public static pathNodes[][] getRoadNodes(GlobalHexagonalCell[][] map) {
        pathNodes[][] otv = new pathNodes[map.length][map[0].length];
        pathNodes[][] otv_ship = new pathNodes[map.length][map[0].length];

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                otv[i][j] = (map[i][j].informationAboutBiome == 0 || map[i][j].informationAboutArea == 2 || map[i][j].informationAboutArea == 3) ? null : new pathNodes(i, j);
                otv_ship[i][j] = (map[i][j].informationAboutArea == 2 || map[i][j].informationAboutArea == 3) ? null : new pathNodes(i, j);

                if (otv[i][j] != null) {
                    if (map[i][j].informationAboutBuilding != null) {
                        otv[i][j].cellType = 10;
                    } else if (map[i][j].informationAboutBiome == 0) {
                        otv[i][j].cellType = 9;
                    }
                }
            }
        }


        return otv;
    }
}
