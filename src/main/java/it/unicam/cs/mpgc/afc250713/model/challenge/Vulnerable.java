package it.unicam.cs.mpgc.afc250713.model.challenge;

/**
 * Entity capable of losing morale under pressure and having a morale state.
 */
public interface Vulnerable {

    /**
     * Applies mental/academic pressure, reducing the current morale pool.
     *
     * @param amount Amount of pressure to inflict.
     */
    void loseMorale(int amount);

    /**
     * Checks if morale is above zero.
     *
     * @return True if still motivated (alive in the run).
     */
    boolean isMotivated();

    /**
     * Returns the current morale of the entity.
     *
     * @return Current morale.
     */
    int getMorale();
}
