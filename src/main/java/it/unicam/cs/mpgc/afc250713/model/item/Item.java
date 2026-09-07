package it.unicam.cs.mpgc.afc250713.model.item;

/**
 * Interface representing a usable item in the game.
 * Uses the Strategy pattern: each concrete item encapsulates its own
 * effect, so new items can be added without touching the consumer logic.
 */
public interface Item {

    /**
     * @return The unique identifier of the item.
     */
    String getId();

    /**
     * @return The display name of the item.
     */
    String getName();

    /**
     * Applies the item's effect to the consumer.
     *
     * @param consumer The entity using the item.
     */
    void use(ItemConsumer consumer);
}
