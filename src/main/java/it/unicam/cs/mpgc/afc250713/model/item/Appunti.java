package it.unicam.cs.mpgc.afc250713.model.item;

import it.unicam.cs.mpgc.afc250713.model.student.StudyBuffType;

/**
 * A set of well-written lecture notes, granting a temporary bonus to study
 * power once used.
 */
public class Appunti extends AbstractItem {

    /** Number of exam attempts the notes last before wearing out. */
    public static final int DEFAULT_DURABILITY = 3;

    @Override
    public String getId() {
        return "APPUNTI";
    }

    @Override
    public String getName() {
        return "APPUNTI DI UN FUORICORSO PIU' BRAVO";
    }

    /**
     * Applies maximum durability buff to the consumer's study power.
     */
    @Override
    public void use(ItemConsumer consumer) {
        consumer.getStudyEquipment().addBuff(StudyBuffType.APPUNTI, DEFAULT_DURABILITY);
    }
}
