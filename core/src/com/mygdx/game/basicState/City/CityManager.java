package com.mygdx.game.basicState.City;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.mygdx.game.basicState.gameConfig.CapacityResidentialAreas;
import static com.mygdx.game.basicState.gameConfig.GlobalMapAVAILABLE_LAND;
import static com.mygdx.game.basicState.gameConfig.GlobalMapSEED;
import static com.mygdx.game.basicState.gameConfig.GlobalMapSIZE;
import static com.mygdx.game.basicState.gameConfig.MaxStartPopulation;
import static com.mygdx.game.basicState.gameConfig.MinStartPopulation;
import static com.mygdx.game.basicState.gameConfig.NumCities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.basicState.City.CitySystem.TradingSystem1;
import com.mygdx.game.basicState.City.Component.BuildingsComponent;
import com.mygdx.game.basicState.City.Component.CityComponent;
import com.mygdx.game.basicState.City.Component.InventoryComponent;
import com.mygdx.game.basicState.Map.GlobalHexagonMap;
import com.mygdx.game.basicState.Type.BuildingsType;
import com.mygdx.game.basicState.Type.ResourceType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CityManager {
    public Array<Entity> listCity = new Array<>();
    public static int numCiti = 0;
    public GlobalHexagonMap GHM;

    public TradingSystem1 TR;
    public Engine engine;

    public CityManager(GlobalHexagonMap ghm, Engine engine, TradingSystem1 tradingSystem1) {
        GHM = ghm;
        TR = tradingSystem1;
        this.engine = engine;

        //начинаем процесс создание городов
        for (int i = 0; i < NumCities; i++) {
            Entity city = new Entity();
            //Ну а че вы хотели. Пока что так
            String nameA = new String[]{"Град", "Лес", "Море", "Гор"}[random.nextInt(3)];
            String nameB = new String[]{"бург", "ополь", "град", "мор"}[random.nextInt(3)];

            int[] coordinates = spawnCoordinatesCity();
            GHM.HexagonMap[coordinates[0]][coordinates[1]].informationAboutBuilding = BuildingsType.CITY_CENTER;
            GHM.HexagonMap[coordinates[0]][coordinates[1]].changTexture();

            CityComponent city_inf = new CityComponent(nameA + nameB, coordinates[0], coordinates[1]);
            BuildingsComponent city_build = new BuildingsComponent();
            InventoryComponent city_inventory = new InventoryComponent(new HashMap<>(), new Array<Entity>(), new Array<Entity>());

            city.add(city_inf);
            city.add(city_build);
            city.add(city_inventory);

            listCity.add(city);
            engine.addEntity(city);
            numCiti++;
            city.getComponent(CityComponent.class).cityId = numCiti;

            constructionManufacturingCity(city);
        }


    }

    public int[] spawnCoordinatesCity() {
        Random random = new Random();

        int x = 0;
        int y = 0;
        boolean blocking_flag;
        for (int count_generations = 0; count_generations < 1000; count_generations++) {
            random.setSeed(GlobalMapSEED + count_generations);

            blocking_flag = false;
            x = random.nextInt(GlobalMapSIZE - GlobalMapAVAILABLE_LAND * 4) + GlobalMapAVAILABLE_LAND * 2;
            y = random.nextInt(GlobalMapSIZE - GlobalMapAVAILABLE_LAND * 4) + GlobalMapAVAILABLE_LAND * 2;

            for (int i = -GlobalMapAVAILABLE_LAND; i <= GlobalMapAVAILABLE_LAND && !blocking_flag; i++) {
                for (int j = -GlobalMapAVAILABLE_LAND; j <= GlobalMapAVAILABLE_LAND; j++) {
                    int visible_x = x + i;
                    int visible_y = y + j;
                    if (visible_x >= 0 && visible_x < GlobalMapSIZE && visible_y >= 0 && visible_y < GlobalMapSIZE) {
                        if (GHM.GlobalMap[visible_x][visible_y][0] == 0 || GHM.GlobalMap[visible_x][visible_y][1] == 2) {
                            blocking_flag = true;
                            break;
                        }
                    }
                }
            }

            for (Entity visible_city : listCity) {
                CityComponent cityComponent = visible_city.getComponent(CityComponent.class);

                double distance = Math.sqrt(Math.pow(cityComponent.x - x, 2) + Math.pow(cityComponent.y - y, 2));
                if (distance < ((double) GlobalMapSIZE / (listCity.size + 2))) {
                    blocking_flag = true;
                    break;
                }
                if (distance < GlobalMapAVAILABLE_LAND * 2) {
                    blocking_flag = true;
                    break;
                }
            }

            if (blocking_flag) {
                continue;
            }
            break;
        }

        return new int[]{x, y};
    }

    public void constructionManufacturingCity(Entity city) {
        int population = random.nextInt(MaxStartPopulation) + MinStartPopulation;

        // добавлем стартовые ресурсы
        for (ResourceType resource : ResourceType.values()) {
            city.getComponent(InventoryComponent.class).Inventory.put(resource, 1000f);
        }
        city.getComponent(InventoryComponent.class).Inventory.put(ResourceType.COINS, 5000f);

        city.getComponent(CityComponent.class).setPopulationSize(population);
        city.getComponent(InventoryComponent.class).Inventory.put(ResourceType.UNIT, (float) population);
        // добавлем стартовые здания
        for (int i = 0; i <= (int) Math.ceil((double) population / CapacityResidentialAreas); i++) {
            spawnBuilding(city, BuildingsType.ResidentialAreas);
        }

        for (BuildingsType buildingsType : BuildingsType.values()){
            switch (buildingsType){
                case CITY_CENTER:
                case Guild_of_Trade_Routes:
                case ResidentialAreas:
                    continue;

            }
            int kol = random.nextInt(3);
            for (int i = 0; i < kol; i++){
                spawnBuilding(city, buildingsType);
            }
        }

    }

    public Boolean spawnBuilding(Entity city, BuildingsType type) {
        CityComponent cityComponent = city.getComponent(CityComponent.class);
        BuildingsComponent buildingsComponent = city.getComponent(BuildingsComponent.class);
        Random random = new Random();
        int x;
        int y;

        boolean blocking_flag;
        for (int count_generations = 0; count_generations < (GlobalMapAVAILABLE_LAND * GlobalMapAVAILABLE_LAND); count_generations++) {
            blocking_flag = false;

            x = random.nextInt(GlobalMapAVAILABLE_LAND * 2) + cityComponent.x - GlobalMapAVAILABLE_LAND;
            y = random.nextInt(GlobalMapAVAILABLE_LAND * 2) + cityComponent.y - GlobalMapAVAILABLE_LAND;

            if (x >= 0 && x < GlobalMapSIZE && y >= 0 && y < GlobalMapSIZE) {
                if (GHM.GlobalMap[x][y][0] == 0 || GHM.GlobalMap[x][y][1] == 2) {
                    continue;
                }
                if (!GHM.HexagonMap[x][y].informationAboutRoad.equals("")) {
                    continue;
                }
            } else {
                continue;
            }

            for (Vector2 vector : buildingsComponent.returnAllBuilding()) {
                if (vector.x == x && vector.y == y) {
                    blocking_flag = true;
                    break;
                }
            }
            if (cityComponent.x == x && cityComponent.y == y) {
                continue;
            }

            if (blocking_flag) {
                continue;
            }

            buildingsComponent.addBuilding(new Vector2(x, y), type);
            GHM.HexagonMap[x][y].informationAboutBuilding = type;
            GHM.HexagonMap[x][y].changTexture();

            return true;
        }
        return false;
    }

    public void Death(Entity city) {
        city.getComponent(CityComponent.class).LifeStatus = false;
        int x = city.getComponent(CityComponent.class).x;
        int y = city.getComponent(CityComponent.class).y;
        GHM.HexagonMap[x][y].informationAboutBuilding = null;


//        for (Map.Entry<ResourceType, Array<TradingSystem1.TradingPosition>> typeArrayMap : TR.StockMarket.entrySet()){
//            for (TradingSystem1.TradingPosition tradingPosition : typeArrayMap.getValue()){
//                if (tradingPosition.ownerPosition == city){
//                    TR.deletingTradingPosition(city, typeArrayMap.getKey(), tradingPosition.Quantity);
//                }
//            }
//        }

        for (ResourceType resourceType : ResourceType.values()) {
            TR.deletingTradingPosition(city, resourceType, Integer.MAX_VALUE);
        }

        listCity.removeValue(city, true);
        engine.removeEntity(city);
    }

    public Entity cityForPlayer() {
        Entity city = new Entity();
        //Ну а че вы хотели. Пока что так
        String nameA = new String[]{"Шлюх", "Лес", "Море", "Гор"}[random.nextInt(3)];
        String nameB = new String[]{"бург", "ополь", "град", "мор"}[random.nextInt(3)];

        int[] coordinates = spawnCoordinatesCity();
        GHM.HexagonMap[coordinates[0]][coordinates[1]].informationAboutBuilding = BuildingsType.CITY_CENTER;
        GHM.HexagonMap[coordinates[0]][coordinates[1]].changTexture();

        CityComponent city_inf = new CityComponent(nameA + nameB, coordinates[0], coordinates[1]);
        BuildingsComponent city_build = new BuildingsComponent();
        InventoryComponent city_inventory = new InventoryComponent(new HashMap<>(), new Array<Entity>(), new Array<Entity>());

        city.add(city_inf);
        city.add(city_build);
        city.add(city_inventory);

        listCity.add(city);
        engine.addEntity(city);
        numCiti++;
        city.getComponent(CityComponent.class).cityId = numCiti;

        constructionManufacturingCity(city);

        for (BuildingsType buildingsType: BuildingsType.values()){
            spawnBuilding(city, buildingsType);
        }


        return city;
    }
}
