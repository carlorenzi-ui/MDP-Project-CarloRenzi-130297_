package it.unicam.cs.mpgc.afc250713.model.item;

import it.unicam.cs.mpgc.afc250713.model.student.StudyEquipmentManager;

/**
 * Abstraction for any entity capable of consuming items.
 * Decouples {@link Item#use(ItemConsumer)} from the concrete student
 * implementation, enabling future consumers (e.g. classmates) without
 * forcing inheritance from AbstractStudent.
 */
public interface ItemConsumer {

    /**
     * Restores morale points up to the entity's cap.
     *
     * @param amount The amount of morale to restore.
     */
    void restoreMorale(int amount);

    /**
     * Adjusts the fatigue level by the given delta (positive or negative).
     *
     * @param amount The fatigue modification amount.
     */
    void modifyFatigue(int amount);

    /**
     * @return The entity's current morale.
     */
    int getMorale();

    /**
     * @return The entity's maximum morale.
     */
    int getMaxMorale();

    /**
     * @return The entity's current fatigue level.
     */
    int getFatigue();

    /**
     * @return The study equipment manager controlling active buffs.
     */
    StudyEquipmentManager getStudyEquipment();
}
