package me.fliqq.customhomes.object;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ConfirmDeletionHolder implements InventoryHolder {
    private final String homeName;

    public ConfirmDeletionHolder(String homeName) {
        this.homeName = homeName;
    }

    public String getHomeName() {
        return homeName;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}

