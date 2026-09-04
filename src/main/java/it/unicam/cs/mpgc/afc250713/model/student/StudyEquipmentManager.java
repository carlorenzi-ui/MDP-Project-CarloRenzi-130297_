package it.unicam.cs.mpgc.afc250713.model.student;

import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Manages active study buffs such as lecture-note durability and study-group shielding.
 */
public class StudyEquipmentManager {

    /** Bonus study power granted by having good lecture notes. */
    public static final int APPUNTI_POWER_BONUS = 25;

    private final PropertyChangeSupport support;
    private final Map<StudyBuffType, Integer> activeBuffs = new EnumMap<>(StudyBuffType.class);

    public StudyEquipmentManager(PropertyChangeSupport support) {
        this.support = support;
    }

    /**
     * @return An unmodifiable view of active buffs.
     */
    public Map<StudyBuffType, Integer> getActiveBuffs() {
        return Collections.unmodifiableMap(this.activeBuffs);
    }

    /**
     * Adds or replaces a buff with the given value.
     *
     * @param type  The buff type.
     * @param value The buff value (e.g. remaining uses or shield points).
     */
    public void addBuff(StudyBuffType type, int value) {
        int oldVal = getBuffValue(type);
        this.activeBuffs.put(type, value);
        support.firePropertyChange("buff_" + type.name(), oldVal, value);
    }

    /**
     * @return The value of the given buff, or 0 if not active.
     */
    public int getBuffValue(StudyBuffType type) {
        return this.activeBuffs.getOrDefault(type, 0);
    }

    /**
     * Removes an active buff.
     *
     * @param type The buff type to remove.
     */
    public void removeBuff(StudyBuffType type) {
        int oldVal = getBuffValue(type);
        this.activeBuffs.remove(type);
        support.firePropertyChange("buff_" + type.name(), oldVal, 0);
    }

    /**
     * @return True if the given buff is active with a positive value.
     */
    public boolean hasBuff(StudyBuffType type) {
        return getBuffValue(type) > 0;
    }

    /**
     * Calculates the total study power considering active note buffs.
     *
     * @param basePower The base power without buffs.
     * @return The total power output.
     */
    public int calculatePower(int basePower) {
        int totalPower = basePower;
        if (hasBuff(StudyBuffType.APPUNTI)) {
            totalPower += APPUNTI_POWER_BONUS;
        }
        return totalPower;
    }

    /**
     * Called when the notes are used in an exam attempt, wearing them out.
     */
    public void onExamAttempt() {
        if (hasBuff(StudyBuffType.APPUNTI)) {
            int durability = getBuffValue(StudyBuffType.APPUNTI);
            if (durability > 1) {
                addBuff(StudyBuffType.APPUNTI, durability - 1);
            } else {
                removeBuff(StudyBuffType.APPUNTI);
            }
        }
    }

    /**
     * Absorbs stress using the study-group buff. Returns the remaining stress
     * after absorption.
     *
     * @param amount The incoming stress amount.
     * @return The stress remaining after the study group absorbs part of it.
     */
    public int absorbStress(int amount) {
        int shield = getBuffValue(StudyBuffType.GRUPPO_STUDIO);
        if (shield > 0) {
            if (shield >= amount) {
                addBuff(StudyBuffType.GRUPPO_STUDIO, shield - amount);
                return 0;
            } else {
                int remaining = amount - shield;
                removeBuff(StudyBuffType.GRUPPO_STUDIO);
                return remaining;
            }
        }
        return amount;
    }
}
