package it.unicam.cs.mpgc.afc250713.model.item;

import it.unicam.cs.mpgc.afc250713.model.student.StudyBuffType;

/**
 * A study group that provides a shield buff, absorbing part of the stress
 * inflicted during an exam.
 */
public class GruppoStudio extends AbstractItem {

    /** Amount of stress the study group can absorb before dissolving. */
    public static final int SHIELD_VALUE = 50;

    @Override
    public String getId() {
        return "GRUPPO_STUDIO";
    }

    @Override
    public String getName() {
        return "GRUPPO DI STUDIO";
    }

    /**
     * Equips the shield buff on the consumer with a fixed durability value.
     */
    @Override
    public void use(ItemConsumer consumer) {
        consumer.getStudyEquipment().addBuff(StudyBuffType.GRUPPO_STUDIO, SHIELD_VALUE);
    }
}
