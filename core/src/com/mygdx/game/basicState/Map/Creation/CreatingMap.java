package com.mygdx.game.basicState.Map.Creation;


import java.util.Random;


public class CreatingMap {
    // инструменты для создание карт
    public int[][] generatePoints(int seed, int num_points, int size) {
        int[][] points = new int[num_points][3];
        Random random = new Random(seed);
        for (int i = 0; i < num_points; i++) {
            int x = random.nextInt(size);
            int y = random.nextInt(size);
            points[i][0] = x;
            points[i][1] = y;
            points[i][2] = i;
        }
        return points;
    } // создание массива точек
    public static float[][] generateNoiseMap(int size, int res, int SID, int octaves) {
        Perlin2D perlin = new Perlin2D(SID);
        float[][] noiseMap = new float[size][size];
        float scale = (float) size / res;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                noiseMap[x][y] = perlin.getNoise(x / scale, y / scale, octaves, 0.5f);
            }
        }
        return noiseMap;
    } // Генерируем карту шума
    public int findNearestPointIndex(int x, int y, int[][] points) {
        float minDistance = Float.MAX_VALUE;
        int nearestPointIndex = -1;

        for (int i = 0; i < points.length; i++) {
            float distance = (float) Math.sqrt(Math.pow((points[i][0] - x), 2) + Math.pow((points[i][1] - y), 2));
            if (distance < minDistance) {
                minDistance = distance;
                nearestPointIndex = i;
            }
        }

        return nearestPointIndex;
    } // поиск ближайшей точки для конкретной клетки
    public int[][] VoronoiMap(int[][] points, int size, int NUM_ITERATIONS) {
        int[][] VoronoiMap = new int[size][size];
        int[][] HelpingLloyd = new int[points.length][3];
        for (int iteration = 0; iteration < NUM_ITERATIONS; iteration++) {
            //Вороной
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    int nearestPointIndex = findNearestPointIndex(x, y, points);
                    VoronoiMap[x][y] = points[nearestPointIndex][2];
                    HelpingLloyd[nearestPointIndex][0] += x;
                    HelpingLloyd[nearestPointIndex][1] += y;
                    HelpingLloyd[nearestPointIndex][2]++;
                }
            }

            // Релаксация Ллойда
            for (int i = 0; i < points.length; i++) {
                int sumX = HelpingLloyd[i][0];
                int sumY = HelpingLloyd[i][1];
                int count = HelpingLloyd[i][2];

                count = (count != 0) ? count : 1;
                HelpingLloyd[i][0] = 0;
                HelpingLloyd[i][1] = 0;
                HelpingLloyd[i][2] = 0;
                points[i][0] = sumX / count;
                points[i][1] = sumY / count;
            }
        }
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int nearestPointIndex = findNearestPointIndex(x, y, points);
                VoronoiMap[x][y] = points[nearestPointIndex][2];
                HelpingLloyd[nearestPointIndex][0] += x;
                HelpingLloyd[nearestPointIndex][1] += y;
                HelpingLloyd[nearestPointIndex][2]++;
            }
        }
        return VoronoiMap;
    }
}
