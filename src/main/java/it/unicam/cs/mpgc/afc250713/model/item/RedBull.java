package it.unicam.cs.mpgc.afc250713.model.item;

/**
 * An energy drink that fully restores the student's missing morale.
 */
public class RedBull extends AbstractItem {

    @Override
    public String getId() {
        return "REDBULL";
    }

    @Override
    public String getName() {
        return "LATTINA DI ENERGY DRINK";
    }

    /**
     * Fully restores morale. Throws an exception if morale is already maxed out.
     */
    @Override
    public void use(ItemConsumer consumer) {
        if (consumer.getMorale() == consumer.getMaxMorale()) {
            throw new IllegalStateException("Non serve, il morale e' gia' alle stelle.");
        }
        consumer.restoreMorale(consumer.getMaxMorale() - consumer.getMorale());
    }
}
