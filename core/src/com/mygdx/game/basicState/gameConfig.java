package com.mygdx.game.basicState;

import static com.badlogic.gdx.math.MathUtils.random;

public class gameConfig {
    //настройка глобальной карты
    // Основные настройки
    public static int GlobalMapSIZE = 512; // размер карты
//    public static int GlobalMapSEED = random.nextInt(100000) + 1; // любое число
    public static int GlobalMapSEED = 351; // любое число

    // Побочные настройки карты
    public static final int GlobalMapNUM_ITERATIONS = 0; // Сколько раз мы будем выравнивать точки
    public static final int GlobalMapNUM_POINTS = 50;  // количество биомных точек
    public static final int GlobalMapSMOOTHING = 0; // сглаживание
    public static final int GlobalMapBORDER_WIDTH = 16; // Размер границы биомов
    public static final float GlobalMapSEA_LEVEL = 0.4F; // Уровень воды
    public static final int RoadTortuosity = 1200; // извилистость дороги. Если число слишком маленькое то могут появиться артефакты

    // Настройки городов
    public static final int GlobalMapAVAILABLE_LAND = 4; // земля доступная для застройки
    public static final int NumCities = 15;
    public static final int MaxStartPopulation = 10;
    public static final int MinStartPopulation = 4;


    // настройка зданий
    public static final int CapacityResidentialAreas = 5;

    public static void setGlobalMapSEED(int seed) {
        GlobalMapSEED = seed;
    }

    public static void setGlobalMapSIZE(int size) {
        GlobalMapSIZE = size;
    }
}
