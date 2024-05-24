package com.mygdx.game.basicState.City.Component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.basicState.Type.ResourceType;

import java.util.Map;

public class InventoryComponent implements Component {
    public Map<ResourceType, Float> Inventory;
    public Array<Entity> WeaponInventory;
    public Array<Entity> ArmorInventory;

    public InventoryComponent(Map<ResourceType, Float> Inventory, Array<Entity> WeaponInventory, Array<Entity> ArmorInventory){
        this.Inventory = Inventory;
        this.ArmorInventory = ArmorInventory;
        this.WeaponInventory = WeaponInventory;
    }

    public void ChangeResource(ResourceType type, int quantity){
        this.Inventory.put(type, (float) quantity);
    }

    public void RemoveWeapon(int index){
        this.WeaponInventory.removeIndex(index);
    }

    public void RemoveArmor(int index){
        this.ArmorInventory.removeIndex(index);
    }

    public void AddWeapon(Entity Weapon){
        this.WeaponInventory.add(Weapon);
    }

    public void AddArmor(Entity Armor){
        this.ArmorInventory.add(Armor);
    }
}
