package com.mygdx.game.basicState.Type;

public enum ResourceType {
    // необработанные
    WOOD(5f), // дерево
    STONE(5f), // камень
    IRON(5f), // железо
    LEATHER(5f), // кожа


    BEER(10f), // пиво
    WHEAT(5f), // пшеница


    // обработанные
    STEEL(5f), // сталь
    TOOLS(5f), // инструменты нужны для строительства
    SHIP(5f), //корабли
    WEAPON(5f), //оружие
    ARMOR(5f), //броня
    GOLD(5f), //золото
    COINS(5F), // деньшка



    // нужен для оценки жизниспособности города
    UNIT(1000);
    private final float startingPrice;

    ResourceType(float v) {
        this.startingPrice = v;
    }

    public float getStartingPrice() {
        return startingPrice;
    }
}
