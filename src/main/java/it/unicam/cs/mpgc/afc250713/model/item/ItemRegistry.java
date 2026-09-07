package it.unicam.cs.mpgc.afc250713.model.item;

import java.util.HashMap;
import java.util.Map;

/**
 * Injectable registry for Item instances (Flyweight pattern).
 * Instantiated and injected where needed, following the Dependency
 * Inversion Principle.
 */
public class ItemRegistry {
    private final Map<String, Item> registry = new HashMap<>();

    /**
     * Creates a registry pre-populated with the default game items.
     *
     * @return A ready-to-use ItemRegistry.
     */
    public static ItemRegistry createDefault() {
        ItemRegistry instance = new ItemRegistry();
        instance.register(new RedBull());
        instance.register(new PastoCaldo());
        instance.register(new GruppoStudio());
        instance.register(new Appunti());
        return instance;
    }

    /**
     * Registers an item in this registry.
     *
     * @param item The item to register.
     */
    public void register(Item item) {
        registry.put(item.getId(), item);
    }

    /**
     * Retrieves a flyweight instance of an item by its ID.
     *
     * @param id The item ID.
     * @return The flyweight item instance.
     * @throws IllegalArgumentException if no item is registered with the given ID.
     */
    public Item get(String id) {
        Item item = registry.get(id);
        if (item == null) {
            throw new IllegalArgumentException("No item registered with ID: " + id);
        }
        return item;
    }
}
