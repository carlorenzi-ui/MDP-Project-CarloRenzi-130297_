package it.unicam.cs.mpgc.afc250713.model.resource;

import it.unicam.cs.mpgc.afc250713.model.item.Item;

/**
 * Interface representing an entity capable of collecting resources (items
 * gained as rewards for passing exams, buying goods, etc).
 */
public interface ResourceCollector {

    /**
     * Stores a specific item quantity in the backpack.
     *
     * @param item   Item variant.
     * @param amount Quantity to add.
     */
    void addItem(Item item, int amount);
}
