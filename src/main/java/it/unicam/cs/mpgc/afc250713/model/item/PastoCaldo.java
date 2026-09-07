package it.unicam.cs.mpgc.afc250713.model.item;

/**
 * A hot meal at the university canteen, used to reset the student's
 * accumulated fatigue.
 */
public class PastoCaldo extends AbstractItem {

    @Override
    public String getId() {
        return "PASTO_CALDO";
    }

    @Override
    public String getName() {
        return "PASTO ALLA MENSA";
    }

    /**
     * Fully resets the consumer's fatigue to zero. Throws an exception if
     * the consumer is not tired at all.
     */
    @Override
    public void use(ItemConsumer consumer) {
        if (consumer.getFatigue() == 0) {
            throw new IllegalStateException("Non sei ancora stanco.");
        }
        consumer.modifyFatigue(-consumer.getFatigue());
    }
}
