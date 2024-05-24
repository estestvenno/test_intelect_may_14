package com.mygdx.game.basicState.City.CitySystem;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.mygdx.game.basicState.Type.BuildingsType.productionProcess;

import static java.lang.Float.NaN;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.basicState.City.CityManager;
import com.mygdx.game.basicState.City.Component.BuildingsComponent;
import com.mygdx.game.basicState.City.Component.CityComponent;
import com.mygdx.game.basicState.City.Component.InventoryComponent;
import com.mygdx.game.basicState.Map.GlobalHexagonMap;
import com.mygdx.game.basicState.Type.BuildingsType;
import com.mygdx.game.basicState.Type.ResourceType;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LifeSupportSystem1 extends IntervalSystem {
    private static int k = 0;
    private final GlobalHexagonMap GHM;
    private final CityManager CM;
    private final TradingSystem1 TR;

    public LifeSupportSystem1(float interval, GlobalHexagonMap ghm, CityManager CM) {
        super((int) interval);
        this.GHM = ghm;
        this.CM = CM;
        this.TR = CM.TR;
    }

    @Override
    protected void updateInterval() {
        Family family = Family.all(CityComponent.class).get();
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(family);

        // Обрабатываем каждую сущность
        for (Entity city : entities) {
            SystemOperation(city);
        }

        // вывод биржи
        System.out.println();
        System.out.println("hod_" + k++);
        for (Map.Entry<ResourceType, Array<TradingSystem1.TradingPosition>> rTAE : TR.StockMarket.entrySet()) {
            if (rTAE.getValue().size == 0) {
                continue;
            }
            System.out.println(rTAE.getKey());
            for (TradingSystem1.TradingPosition tradingPosition : rTAE.getValue()) {
                int citiId = tradingPosition.ownerPosition.getComponent(CityComponent.class).cityId;
                System.out.print("id_" + citiId + " (" + tradingPosition.PricePerPiece + ") " + tradingPosition.Quantity + "  ___  ");
            }
            System.out.println();
        }
        System.out.println();

        for (Entity city : entities) {
            System.out.println("cityID_" + city.getComponent(CityComponent.class).cityId);
            for (Map.Entry<ResourceType, Float> inv : city.getComponent(InventoryComponent.class).Inventory.entrySet()){
                System.out.print(inv + "   ");
            }
            System.out.println();
        }
        System.out.println();
    }


    private void SystemOperation(Entity city) {
        // все для начала
        // вся производственная мощь + количество еды за ход
        int maximumProductionCapacity = city.getComponent(CityComponent.class).populationSize;
        int standardFoodCosts = maximumProductionCapacity + city.getComponent(CityComponent.class).populationArmySize;

        // список ресурсов полученных за ход
        HashMap<ResourceType, Float> compensatedResources = new HashMap<>();
        compensatedResources.put(ResourceType.BEER, (float) -standardFoodCosts);


        //блок ответственный за строительство новых зданий
        int ChanceConstruction = random.nextInt(5);
        if (ChanceConstruction == 0){
            BuildingsType[] buildingTypes = BuildingsType.values();
            int index = random.nextInt(buildingTypes.length);
            switch (buildingTypes[index]){
                case CITY_CENTER:
                case Guild_of_Trade_Routes:
                case ResidentialAreas:
                    break;
                default:
                    CM.spawnBuilding(city, buildingTypes[index]);
                    for (Map.Entry<ResourceType, Float> entry : buildingTypes[index].getCost().entrySet()){
                        compensatedResources.put(entry.getKey(), -entry.getValue());
                    }
            }
        }




        //блок ответственный за потдержание армии


        //блок ответственный за создание цен продажи и покупки
        HashMap<ResourceType, Float> salePrices = TR.priceCreationSection(city);
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<ResourceType, Float> entry : salePrices.entrySet()) {
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
        }
        String pricesString = stringBuilder.toString();
        if (!pricesString.isEmpty()) {
            pricesString = pricesString.substring(0, pricesString.length() - 2); // Удаляем последнюю запятую и пробел
        }

        //блок ответственный за создание рабочих мест
        HashMap<BuildingsType, Integer> jobs = city.getComponent(BuildingsComponent.class).getNumberOfJobs();
        double[] simpleBounds = new double[jobs.size()];
        BuildingsType[] indexes = new BuildingsType[jobs.size()];
        int index = -1;
        for (Map.Entry<BuildingsType, Integer> entry : jobs.entrySet()) {
            index++;
            BuildingsType buildingType = entry.getKey();
            indexes[index] = buildingType;
            simpleBounds[index] = 0;

            if (buildingType.getIncome().size() == 0) {
                continue;
            }

            simpleBounds[index] = entry.getValue();
        }


        // блок ответственный за оптимизацию функции
        double[] sigma = new double[simpleBounds.length];

        for (int i = 0; i < simpleBounds.length; i++) {
            sigma[i] = (simpleBounds[i] == 0) ? 0 : 1;
        }


        CMAESOptimizer optimizer = new CMAESOptimizer(
                5000,
                0,
                true,
                0,
                100,
                new MersenneTwister(),
                true,
                new SimpleValueChecker(-1, 1));


        PointValuePair result = optimizer.optimize(
                new CMAESOptimizer.PopulationSize(100),
                new CMAESOptimizer.Sigma(sigma),
                new ObjectiveFunction(x -> profitCalculation(city, x, indexes, salePrices, compensatedResources)),
                new MaxEval(500),
                GoalType.MAXIMIZE,
                new InitialGuess(new double[simpleBounds.length]),
                new SimpleBounds(new double[simpleBounds.length], simpleBounds)
        );

        double[] resultJob = new double[result.getPoint().length];
        System.out.println();
        for (int i = 0; i < result.getPoint().length; i++) {
            resultJob[i] = (int) result.getPoint()[i];
            System.out.print(resultJob[i] + "  ");
        }
        System.out.println();
        realizeProfit(city, resultJob, indexes, salePrices, compensatedResources);
    }

    private double profitCalculation(Entity city, double[] jobs,
                                     BuildingsType[] indexes,
                                     HashMap<ResourceType, Float> salePrices,
                                     HashMap<ResourceType, Float> compensatedResources) {

        HashMap<ResourceType, Float> resource = new HashMap<>();
        float finalPrice = 0;
        int powerConsumption = 0;
        Map<ResourceType, Float> originalInventory = city.getComponent(InventoryComponent.class).Inventory;
        float money = city.getComponent(InventoryComponent.class).Inventory.get(ResourceType.COINS);
        for (ResourceType type : ResourceType.values()) {
            resource.put(type, 0f);
        }
        resource.putAll(compensatedResources);

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

                Object[] ReturnPurchase = TR.possibilityPurchase(city, RC.getKey(), -RC.getValue(), moneyCycle);
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

            Object[] ReturnPurchase = TR.possibilityPurchase(city, RC.getKey(), -RC.getValue(), money);
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
//        System.out.println(Arrays.toString(jobs));
//        System.out.println();
        return finalPrice;
    }

    private void realizeProfit(Entity city, double[] jobs,
                               BuildingsType[] indexes,
                               HashMap<ResourceType, Float> salePrices, HashMap<ResourceType, Float> compensatedResources) {


        float money = city.getComponent(InventoryComponent.class).Inventory.get(ResourceType.COINS);

        Map<ResourceType, Float> originalInventory = city.getComponent(InventoryComponent.class).Inventory;

        HashMap<ResourceType, Float> resource = new HashMap<>();
        for (ResourceType type : ResourceType.values()) {
            resource.put(type, 0f);
        }
        resource.putAll(compensatedResources);
        for (int k = 0; k < indexes.length; k++) {
            if ((int) jobs[k] == 0) {
                continue;
            }
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

                Object[] ReturnPurchase = TR.possibilityPurchase(city, RC.getKey(), -RC.getValue(), moneyCycle);
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


        for (Map.Entry<ResourceType, Float> RC : resource.entrySet()) {
            if (RC.getValue() < 0) {
                Object[] ReturnPurchase = TR.Purchase(city, RC.getKey(), -RC.getValue(), money);
                float price = ReturnPurchase[0] instanceof Number ? ((Number) ReturnPurchase[0]).floatValue() : 0.0f;
                int quantity = ReturnPurchase[1] instanceof Number ? ((Number) ReturnPurchase[1]).intValue() : 0;


                //если хватило денег
                money -= price;
                if (quantity == 0) {
                    continue;
                }


                //если не хватило
                //если можем взять взаймы
                if (originalInventory.get(RC.getKey()) >= quantity) {
                    originalInventory.put(RC.getKey(), originalInventory.get(RC.getKey()) - quantity);
                    TR.deletingTradingPosition(city, RC.getKey(), quantity);
                    continue;
                }

                quantity -= originalInventory.get(RC.getKey());
                TR.deletingTradingPosition(city, RC.getKey(), originalInventory.get(RC.getKey()).intValue());
                originalInventory.put(RC.getKey(), 0f);

                if (RC.getKey() == ResourceType.BEER) {
                    int quantity_del = Math.min(city.getComponent(CityComponent.class).populationArmySize, quantity);
                    quantity -= quantity_del;
                    city.getComponent(CityComponent.class).populationArmySize -= quantity_del;

                    if (quantity > 0) {
                        System.out.println(city.getComponent(CityComponent.class).populationSize + " " + quantity);
                        quantity_del = Math.min(city.getComponent(CityComponent.class).populationSize, quantity);
                        city.getComponent(CityComponent.class).populationSize -= quantity_del;
                        if (city.getComponent(CityComponent.class).populationSize <= 0) {
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            CM.Death(city);
                            break;
                        }
                    }

                }
            } else if (RC.getValue() > 0) {
                originalInventory.put(RC.getKey(), originalInventory.get(RC.getKey()) + RC.getValue().intValue());
                TR.addingTradingPosition(city, RC.getKey(), salePrices.get(RC.getKey()), RC.getValue().intValue());
            }
        }
    }
}
