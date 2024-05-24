package com.mygdx.game.basicState.Player;

import static com.mygdx.game.basicState.Type.BuildingsType.productionProcess;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.basicState.City.CityManager;
import com.mygdx.game.basicState.City.Component.CityComponent;
import com.mygdx.game.basicState.City.Component.InventoryComponent;
import com.mygdx.game.basicState.Map.GlobalHexagonMap;
import com.mygdx.game.basicState.Type.BuildingsType;
import com.mygdx.game.basicState.Type.ResourceType;
import com.mygdx.game.basicState.pathFinder.pathManager;

import java.util.HashMap;
import java.util.Map;

public class Player {
    public Entity PlayersCity;
    public HashMap<BuildingsType, Integer> Workers;
    private final pathManager PM;
    private final GlobalHexagonMap GHM;
    private final CityManager CM;

    public Player(pathManager pm, GlobalHexagonMap ghm, CityManager cm) {
        PlayersCity = cm.cityForPlayer();
        PM = pm;
        GHM = ghm;
        CM = cm;
        Workers = new HashMap<BuildingsType, Integer>();
    }

    public int[] GoToCoordinates() {
        int[] coordinates = new int[2];

        coordinates[0] = PlayersCity.getComponent(CityComponent.class).x;
        coordinates[1] = PlayersCity.getComponent(CityComponent.class).y;
        return coordinates;
    }

    public HashMap<ResourceType, Float> ProfitPerTurn() {
        //блок ответственный за создание цен продажи и покупки
        HashMap<ResourceType, Float> salePrices = CM.TR.priceCreationSection(PlayersCity);
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<ResourceType, Float> entry : salePrices.entrySet()) {
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
        }
        String pricesString = stringBuilder.toString();
        if (!pricesString.isEmpty()) {
            pricesString = pricesString.substring(0, pricesString.length() - 2); // Удаляем последнюю запятую и пробел
        }


        float finalPrice = 0;
        int powerConsumption = 0;

        HashMap<ResourceType, Float> resource = new HashMap<>();
        Map<ResourceType, Float> originalInventory = PlayersCity.getComponent(InventoryComponent.class).Inventory;
        float money = PlayersCity.getComponent(InventoryComponent.class).Inventory.get(ResourceType.COINS);
        for (ResourceType type : ResourceType.values()) {
            resource.put(type, 0f);
        }

        int maximumProductionCapacity = PlayersCity.getComponent(CityComponent.class).populationSize;
        int standardFoodCosts = maximumProductionCapacity + PlayersCity.getComponent(CityComponent.class).populationArmySize;
        resource.put(ResourceType.BEER, (float) -standardFoodCosts);

        BuildingsType indexes[] = new BuildingsType[Workers.size()];
        int jobs[] = new int[Workers.size()];

        int c = 0;
        for (Map.Entry<BuildingsType, Integer> entry : Workers.entrySet()) {
            indexes[c] = entry.getKey();
            jobs[c] = entry.getValue();
            c++;
        }

        for (int k = 0; k < indexes.length; k++) {
            if ((int) jobs[k] == 0) {
                continue;
            }
            powerConsumption += (int) jobs[k];
            productionProcess(resource, indexes[k], (int) jobs[k]);
        }

        // редактируем производства
        for (int i = 0; i < 5; i++) {
            float moneyCycle = money;
            Map<ResourceType, Float> copyInventory = new HashMap<>(originalInventory);

            for (Map.Entry<ResourceType, Float> RC : resource.entrySet()) {
                if (RC.getValue() >= 0) {
                    continue;
                }

                Object[] ReturnPurchase = CM.TR.possibilityPurchase(PlayersCity, RC.getKey(), -RC.getValue(), moneyCycle);
                float price = ReturnPurchase[0] instanceof Number ? ((Number) ReturnPurchase[0]).floatValue() : 0.0f;
                int quantity = ReturnPurchase[1] instanceof Number ? ((Number) ReturnPurchase[1]).intValue() : 0;


                //если хватило денег
                moneyCycle -= price;
                if (quantity == 0) {
                    continue;
                }

                //если не хватило
                //если можем взять взаймы
                if (copyInventory.get(RC.getKey()) >= quantity) {
                    copyInventory.put(RC.getKey(), copyInventory.get(RC.getKey()) - quantity);
                    continue;
                }

                quantity -= copyInventory.get(RC.getKey());
                copyInventory.put(RC.getKey(), 0f);
                // если не можем
                // уменьшаем количество зданий котрые тратят этот ресурс
                // считаем сколько нужно комписировать
                int compensatedQuantity = quantity;


                for (int k = 0; k < indexes.length; k++) {
                    if (jobs[k] != 0 && indexes[k].getIncome().containsKey(RC.getKey()) && indexes[k].getIncome().get(RC.getKey()) < 0) {
                        float compensationCoefficient = compensatedQuantity / -indexes[k].getIncome().get(RC.getKey());
//                        System.out.println(indexes[k] + " " + jobs[k]);
//                        System.out.println(compensationCoefficient + " " + compensatedQuantity+ " " + indexes[k].getIncome().get(RC.getKey()));

                        if (jobs[k] < compensationCoefficient) {
                            productionProcess(resource, indexes[k], (int) -jobs[k]);
                            jobs[k] = 0;
                            continue;
                        }

                        productionProcess(resource, indexes[k], -compensationCoefficient);
                        jobs[k] -= compensationCoefficient;
                    }
                }
            }
        }

        // считаем итогувую сумму
        for (Map.Entry<ResourceType, Float> RC : resource.entrySet()) {
            if (RC.getValue() >= 0) {
                finalPrice += salePrices.get(RC.getKey()) * RC.getValue();
                continue;
            }

            Object[] ReturnPurchase = CM.TR.possibilityPurchase(PlayersCity, RC.getKey(), -RC.getValue(), money);
            float price = ReturnPurchase[0] instanceof Number ? ((Number) ReturnPurchase[0]).floatValue() : 0.0f;
            int quantity = ReturnPurchase[1] instanceof Number ? ((Number) ReturnPurchase[1]).intValue() : 0;


            finalPrice -= price;

            float denominator = -RC.getValue() - quantity;
            denominator = (denominator == 0) ? 1 : denominator; // предотвращение деления на ноль

            float averagePrice = price / denominator;
            averagePrice = (averagePrice == 0) ? salePrices.get(RC.getKey()) * 2 : averagePrice; // проверка на NaN

            if (RC.getKey() == ResourceType.BEER) {
                finalPrice -= quantity * 10 * averagePrice;
            } else {
                finalPrice -= quantity * 2 * averagePrice;
            }
            finalPrice -= quantity * 2 * averagePrice;
        }

        resource.put(ResourceType.COINS, finalPrice);
        return resource;
    }
}





