package com.mygdx.game.basicState.Type;

import static com.mygdx.game.basicState.Type.ResourceType.ARMOR;
import static com.mygdx.game.basicState.Type.ResourceType.BEER;
import static com.mygdx.game.basicState.Type.ResourceType.GOLD;
import static com.mygdx.game.basicState.Type.ResourceType.IRON;
import static com.mygdx.game.basicState.Type.ResourceType.LEATHER;
import static com.mygdx.game.basicState.Type.ResourceType.SHIP;
import static com.mygdx.game.basicState.Type.ResourceType.STEEL;
import static com.mygdx.game.basicState.Type.ResourceType.STONE;
import static com.mygdx.game.basicState.Type.ResourceType.TOOLS;
import static com.mygdx.game.basicState.Type.ResourceType.WEAPON;
import static com.mygdx.game.basicState.Type.ResourceType.WHEAT;
import static com.mygdx.game.basicState.Type.ResourceType.WOOD;

import java.util.HashMap;
import java.util.Map;

public enum BuildingsType {
    CITY_CENTER(
            new HashMap<ResourceType, Float>(),

            new HashMap<ResourceType, Float>()),

    Guild_of_Trade_Routes(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 10f);
                put(WOOD, 500f);
                put(IRON, 50f);
                put(STONE, 250f);
                put(STEEL, 50f);
            }},

            new HashMap<ResourceType, Float>()),
    MeltingFurnace(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 10f);
                put(WOOD, 500f);
                put(IRON, 50f);
                put(STONE, 250f);
                put(STEEL, 50f);
            }},

            new HashMap<ResourceType, Float>() {{
                put(STEEL, -100f);
                put(LEATHER, -100f);
                put(WOOD, -100f);
                put(WEAPON, 5f);
            }}), // создание случаного оружия и добавление на его на внутренний рынок + возможность заказать оружие (индекс 1)
    BlastFurnace(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 10f);
                put(WOOD, 500f);
                put(IRON, 50f);
                put(STONE, 250f);
                put(STEEL, 50f);
            }},

            new HashMap<ResourceType, Float>() {{
                put(STEEL, -100f);
                put(LEATHER, -100f);
                put(WOOD, -100f);
                put(ARMOR, 5f);
            }}), // создание случаной брони и добавление на ее на внутренний рынок + возможность заказать броню (индекс 2)
    Mine(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 10f);
                put(WOOD, 500f);
                put(IRON, 40f);
            }},

            new HashMap<ResourceType, Float>() {{
                put(IRON, 175f);
                put(TOOLS, -35f);
                put(GOLD, 1f);
            }}), // добавляет на внутренний рынок железо и редко золото (индекс 3)
    TannerWorkshop(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 10f);
                put(WOOD, 50f);
                put(IRON, 20f);
            }},

            new HashMap<ResourceType, Float>() {{
                put(TOOLS, -25f);
                put(LEATHER, 25f);
            }}), // добавляет на внутренний рынок кожу (индекс 4)
    Brewery(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 10f);
                put(WOOD, 500f);
                put(STONE, 500f);
            }},

            new HashMap<ResourceType, Float>() {{
                put(WHEAT, -50f);
                put(BEER, 30f);
            }}), // добавляет на внутренний рынок пиво взамен на пшеницу (индекс 5)
    MetallurgistsWorkshop(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 10f);
                put(WOOD, 50f);
                put(IRON, 20f);
            }},

            new HashMap<ResourceType, Float>() {{
                put(STEEL, 75f);
                put(IRON, -150f);
            }}), // добавляет сталь на внутренний рынок (индекс 6)
    Sawmill(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 10f);
                put(WOOD, 50f);
                put(IRON, 20f);
            }},

            new HashMap<ResourceType, Float>() {{
                put(WOOD, 90f);
            }}), // добавляет дерево на внутренний рынок (индекс 7)
    WheatField(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 10f);
                put(WOOD, 50f);
            }},

            new HashMap<ResourceType, Float>() {{
                put(WHEAT, 90f);
            }}), // добавляет пшеницу на внутренний рынок (индекс 8)
    StonemasonWorkshop(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 10f);
                put(WOOD, 50f);
                put(STEEL, 50f);
            }},

            new HashMap<ResourceType, Float>() {{
                put(STONE, 150f);
                put(TOOLS, -30f);
            }}), // добавляет камень на внутренний рынок (индекс 12)
    Shipyard(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 50f);
                put(WOOD, 500f);
                put(STONE, 500f);
                put(STEEL, 250f);
                put(IRON, 300f);
            }},

            new HashMap<ResourceType, Float>() {{
                put(TOOLS, -100f);
                put(WOOD, -500f);
                put(STEEL, -50f);
                put(IRON, -300f);
                put(SHIP, 1f);
            }}), // добавляет корабли(недоступны на рынке, но нужны для работы флота) (индекс 13)
    ResidentialAreas(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 50f);
                put(WOOD, 500f);
                put(STONE, 500f);
            }},

            new HashMap<ResourceType, Float>()), // жилые районы города (индекс 15)
    Workshop(
            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 50f);
                put(WOOD, 300f);
                put(STEEL, 150f);
            }},

            new HashMap<ResourceType, Float>() {{
                put(TOOLS, 15f);
                put(WOOD, -30f);
                put(STEEL, -15f);
            }}); // мастерская производит инструменты (индекс 16)

    private final HashMap<ResourceType, Float> cost;
    private final HashMap<ResourceType, Float> income;

    BuildingsType(HashMap<ResourceType, Float> cost, HashMap<ResourceType, Float> income) {
        this.cost = cost;
        this.income = income;
    }


    public HashMap<ResourceType, Float> getCost() {
        return cost;
    }

    public HashMap<ResourceType, Float> getIncome() {
        return income;
    }

    public static void productionProcess(HashMap<ResourceType, Float> resource, BuildingsType type, float job) {
        for (Map.Entry<ResourceType, Float> eR : type.getIncome().entrySet()) {
            float received = eR.getValue() * job;
            resource.put(eR.getKey(), (float) (resource.get(eR.getKey()) + received));
        }
    }
}
