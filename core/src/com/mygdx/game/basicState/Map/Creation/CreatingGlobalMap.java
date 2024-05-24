package com.mygdx.game.basicState.Map.Creation;


import static com.mygdx.game.basicState.gameConfig.GlobalMapBORDER_WIDTH;
import static com.mygdx.game.basicState.gameConfig.GlobalMapNUM_ITERATIONS;
import static com.mygdx.game.basicState.gameConfig.GlobalMapNUM_POINTS;
import static com.mygdx.game.basicState.gameConfig.GlobalMapSEA_LEVEL;
import static com.mygdx.game.basicState.gameConfig.GlobalMapSEED;
import static com.mygdx.game.basicState.gameConfig.GlobalMapSIZE;
import static com.mygdx.game.basicState.gameConfig.GlobalMapSMOOTHING;

public class CreatingGlobalMap extends CreatingMap{
    // вернем эти
    int[][] TemporaryMap;
    int[][] ElevationMap;
    int[][] BiomeMap;
    int[][] biomePoints = new int[GlobalMapNUM_POINTS][2];
    public int[][][] FinishedMap;

    public CreatingGlobalMap() {
        this.FinishedMap = new int[GlobalMapSIZE][GlobalMapSIZE][3];
        this.TemporaryMap = new int[GlobalMapSIZE][GlobalMapSIZE];
        this.ElevationMap = new int[GlobalMapSIZE][GlobalMapSIZE];
        this.BiomeMap = new int[GlobalMapSIZE][GlobalMapSIZE];
        this.biomePoints = generatePoints(GlobalMapSEED, GlobalMapNUM_POINTS, GlobalMapSIZE);
        preformGameMap();
        for (int i = 0; i < GlobalMapSMOOTHING; i++) {
            smoothingMatrices();
        }
        postProcessingMap();
    }

    public static int[] generateGradientList() {
        int[] gradientList = new int[GlobalMapSIZE];
        for (int i = 0; i < GlobalMapSIZE; i++) {
            double interpolation;

            if (i < GlobalMapSIZE / 5) {
                // От -35 до -5 (1/5 массива)
                interpolation = (double) i / (GlobalMapSIZE / 5 - 1);
                gradientList[GlobalMapSIZE - 1 - i] = (int) (-35 + interpolation * (-5 - (-35)));
            } else if (i < (4 * GlobalMapSIZE / 5)) {
                // От -5 до 25 (3/5 массива)
                interpolation = (double) (i - GlobalMapSIZE / 5) / (3 * GlobalMapSIZE / 5 - 1);
                gradientList[GlobalMapSIZE - 1 - i] = (int) (-5 + interpolation * (25 - (-5)));
            } else {
                // От 25 до 40 (1/5 массива)
                interpolation = (double) (i - 4 * GlobalMapSIZE / 5) / (GlobalMapSIZE / 5 - 1);
                gradientList[GlobalMapSIZE - 1 - i] = (int) (25 + interpolation * (40 - 25));
            }
        }
        return gradientList;
    }

    private void preformGameMap() {
        // нужна в дальнейшем как буфер для размытия
        int[][] preformMap = VoronoiMap(this.biomePoints, GlobalMapSIZE, GlobalMapNUM_ITERATIONS);

        Perlin2D perlin = new Perlin2D(GlobalMapSEED);
        float[][] noiseMap1 = generateNoiseMap(GlobalMapSIZE, 32, 200, 8);
        float[][] noiseMap2 = generateNoiseMap(GlobalMapSIZE, 32, 250, 8);
        int[] gradientList = generateGradientList();
        for (int x = 0; x < GlobalMapSIZE; x++) {
            for (int y = 0; y < GlobalMapSIZE; y++) {
                // Обработка отрицательных индексов
                int i = ((int) (x + GlobalMapBORDER_WIDTH * noiseMap1[x][y]) + GlobalMapSIZE) % GlobalMapSIZE;
                int j = ((int) (y + GlobalMapBORDER_WIDTH * noiseMap2[x][y]) + GlobalMapSIZE) % GlobalMapSIZE;
                int index = i * GlobalMapSIZE + j;

                i = index / GlobalMapSIZE;
                j = index % GlobalMapSIZE;

                int diffX = x - i;
                int diffY = y - j;

                int dist = Math.max(Math.abs(diffX), Math.abs(diffY));
                this.BiomeMap[x][y] = (dist > GlobalMapBORDER_WIDTH) ? preformMap[x][y] : preformMap[i][j];
                this.TemporaryMap[x][y] = gradientList[this.biomePoints[this.BiomeMap[x][y]][1]];
                this.ElevationMap[x][y] = (int) (128 + (127 * Math.tanh(perlin.getNoise(x / 100f, y / 100f, 6, 0.5f) * 3)));
            }
        }
    }

    public void smoothingMatrices() {
        int[][] directions;
        for (int x = 0; x < GlobalMapSIZE; x++) {
            for (int y = 0; y < GlobalMapSIZE; y++) {
                int sum_of_neighbors = 0;
                int count = 0;
                if (y % 2 == 0) {
                    directions = new int[][]{{0, -1}, {-1, -1}, {-1, 0}, {0, 1}, {1, 1}, {1, 0}, {0, 0}};
                } else {
                    directions = new int[][]{{0, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 0}, {1, -1}, {0, 0}};
                }
                for (int[] dir : directions) {
                    int nr = x + dir[0];
                    int nc = y + dir[1];
                    if (nr >= 0 && nr < GlobalMapSIZE && nc >= 0 && nc < GlobalMapSIZE) {
                        sum_of_neighbors += this.TemporaryMap[nr][nc];
                        count++;
                    }
                }
                this.TemporaryMap[x][y] = sum_of_neighbors / count;
            }
        }
    }

    private void postProcessingMap() {
        Perlin2D noise = new Perlin2D(GlobalMapSEED);
        float localSeaLevel = 255 * GlobalMapSEA_LEVEL; // Уровень моря
        for (int x = 0; x < GlobalMapSIZE; x++) {
            for (int y = 0; y < GlobalMapSIZE; y++) {
                boolean f_pes = false;
                // проверяем что местность не берег
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if (i + x >= 0 && j + y >= 0 && i + x < GlobalMapSIZE && j + y < GlobalMapSIZE) {
                            if (ElevationMap[i + x][j + y] <= localSeaLevel) {
                                f_pes = true;
                                break;
                            }
                        }
                    }
                    if (f_pes) {
                        break;
                    }
                }
                // определяем океан
                if (ElevationMap[x][y] < localSeaLevel) {
                    FinishedMap[x][y][0] = 0;
                    FinishedMap[x][y][1] = (int) (ElevationMap[x][y] / (localSeaLevel / 4));
                    continue;
                }
                // определяем лес и горы
                float noisL = noise.getNoise(x / 1000f, y / 1000f, 8, 1f); // Шум леса
                float noisG = noise.getNoise(x / 100f, y / 100f, 6, 0.5f); // Шум горы
                if (f_pes) {
                    FinishedMap[x][y][1] = 3;
                } else if (0.2f < noisG) {
                    FinishedMap[x][y][1] = 2;
                } else if (0.01f < noisL) {
                    FinishedMap[x][y][1] = 1;
                } else {
                    FinishedMap[x][y][1] = 0;
                }


                //Типы суши
                if (TemporaryMap[x][y] < -5) {
                    FinishedMap[x][y][0] = 1;
                } else if (TemporaryMap[x][y] < 10) {
                    FinishedMap[x][y][0] = 2;
                } else if (TemporaryMap[x][y] < 25) {
                    FinishedMap[x][y][0] = 3;
                } else {
                    FinishedMap[x][y][0] = 4;
                }
            }
        }
    }
}