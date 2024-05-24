package com.mygdx.game.basicState.City.CitySystem;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.basicState.City.Component.InventoryComponent;
import com.mygdx.game.basicState.Type.BuildingsType;
import com.mygdx.game.basicState.Type.ResourceType;

import java.util.HashMap;
import java.util.Map;

public class TradingSystem {
    public HashMap<ResourceType, Array<TradingSystem.TradingPosition>> StockMarket;

    TradingSystem() {
        this.StockMarket = new HashMap<>();
        for (ResourceType rt : ResourceType.values()) {
            StockMarket.put(rt, new Array<>());
        }
    }

    public static HashMap<ResourceType, Float> pricesCalculating(HashMap<ResourceType, Float> knownPrices) {
        //копируем имеющиеся цены
        HashMap<ResourceType, Float> prices = new HashMap<>();
        for (ResourceType rt : ResourceType.values()) {
            Float price = knownPrices.get(rt);
            prices.put(rt, price != null ? price : 0f);
        }

        if (prices.get(ResourceType.TOOLS) == 0) {
            prices.put(ResourceType.TOOLS, ResourceType.TOOLS.getStartingPrice());
        }


        //досчитываем остатки
        boolean recalculationFlag;
        int iteration = 0;

        do {
            iteration++;
            recalculationFlag = false;


            for (BuildingsType Build : BuildingsType.values()) {
                if (Build.getIncome() == null) {
                    continue;
                } // проверка на полезность здания
                boolean blockingCalculation = false; // блокируем если нет некоторых цен

                // считаем затраты
                int profit = 0;
                float expenses = ResourceType.UNIT.getStartingPrice();

                for (Map.Entry<ResourceType, Float> entryResource : Build.getIncome().entrySet()) {
                    if (entryResource.getValue() < 0) {
                        if (prices.get(entryResource.getKey()) == 0) {
                            blockingCalculation = true;
                            break;
                        }

                        expenses -= prices.get(entryResource.getKey()) * entryResource.getValue();
                    }
                    if (entryResource.getValue() > 0) {
                        profit++;
                    }
                }

                if (blockingCalculation) {
                    continue;
                }

                for (Map.Entry<ResourceType, Float> entryResource : Build.getIncome().entrySet()) {
                    float value = entryResource.getValue();
                    if (value > 0) {
                        ResourceType key = entryResource.getKey();
                        float startingPrice = key.getStartingPrice();
                        if (prices.get(key) == 0 || prices.get(key) == startingPrice) {
                            prices.put(key, (expenses / profit) / value);
                        }
                    }
                }
            }

            for (ResourceType Resource : ResourceType.values()) {
                if (Resource != ResourceType.UNIT && Resource != ResourceType.COINS && prices.get(Resource) == 0) {
                    recalculationFlag = true;
                    break;
                }
            }


        } while (recalculationFlag && iteration < 100 || iteration < 5);

        return prices;
    }

    public Object[] possibilityPurchase(Entity buyer, ResourceType productType, Float quantity, Float money) {
        // попытка покупки
        float cost = 0;

        Array<TradingSystem.TradingPosition> positionsResource = StockMarket.get(productType);
        positionsResource.sort((positions1, positions2) -> {
            return Float.compare(positions1.PricePerPiece, positions2.PricePerPiece);
        });

        for (TradingSystem.TradingPosition tradingPosition : positionsResource) {
            if (tradingPosition.ownerPosition == buyer) {
                continue;
            }


            int purchaseAmount = (int) Math.min(quantity, Math.min(tradingPosition.Quantity, money / tradingPosition.PricePerPiece));


            cost += purchaseAmount * tradingPosition.PricePerPiece;
            money -= purchaseAmount * tradingPosition.PricePerPiece;
            quantity -= purchaseAmount;

            if (quantity == 0 || money <= 0) {
                break;
            }
        }

        return new Object[]{cost, quantity};
    }

    public Object[] Purchase(Entity buyer, ResourceType productType, Float quantity, Float money) {
        // попытка покупки
        float cost = 0;

        Array<TradingSystem.TradingPosition> positionsResource = StockMarket.get(productType);
        positionsResource.sort((positions1, positions2) -> {
            return Float.compare(positions1.PricePerPiece, positions2.PricePerPiece);
        });

        for (TradingSystem.TradingPosition tradingPosition : positionsResource) {
            if (tradingPosition.ownerPosition == buyer) {
                continue;
            }


            int purchaseAmount = (int) Math.min(quantity, Math.min(tradingPosition.Quantity, money / tradingPosition.PricePerPiece));


            cost += purchaseAmount * tradingPosition.PricePerPiece;
            money -= purchaseAmount * tradingPosition.PricePerPiece;
            quantity -= purchaseAmount;

            float ownersMoney = tradingPosition.ownerPosition.getComponent(InventoryComponent.class).Inventory.get(ResourceType.COINS);
            tradingPosition.ownerPosition.getComponent(InventoryComponent.class).Inventory.put(productType, (float) (tradingPosition.Quantity - purchaseAmount));
            tradingPosition.ownerPosition.getComponent(InventoryComponent.class).Inventory.put(ResourceType.COINS, ownersMoney + purchaseAmount * tradingPosition.PricePerPiece);


            buyer.getComponent(InventoryComponent.class).Inventory.put(ResourceType.COINS, money);

            deletingTradingPosition(tradingPosition.ownerPosition, productType, purchaseAmount);
            if (quantity == 0 || money <= 0) {
                break;
            }
        }

        return new Object[]{cost, quantity};
    }

    public void deletingTradingPosition(Entity city, ResourceType productType, int quantity) {
        for (TradingSystem.TradingPosition tradingPosition : StockMarket.get(productType)) {
            if (tradingPosition.ownerPosition != city) {
                continue;
            }

            tradingPosition.Quantity -= quantity;
            if (tradingPosition.Quantity == 0) {
                StockMarket.get(productType).removeValue(tradingPosition, true);
            }
        }
    }


    class TradingPosition {
        protected final Entity ownerPosition;
        protected float PricePerPiece;
        protected int Quantity;

        TradingPosition(Entity ownerPosition, float PricePerPiece, int Quantity) {
            this.ownerPosition = ownerPosition;
            this.PricePerPiece = PricePerPiece;
            this.Quantity = Quantity;
        }
    }
}
