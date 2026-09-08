package it.unicam.cs.mpgc.afc250713.model.challenge;

/**
 * Mutable state tracker for an entity's morale and pressure output.
 * Reused both for the student (morale, study power) and for an exam
 * (resistance, stress inflicted).
 */
public class ChallengeStats {
    private int morale;
    private final int power;

    /**
     * Initializes the challenge stats.
     *
     * @param morale Initial morale / resistance points (1-100).
     * @param power  Base pressure output (study power, or stress inflicted).
     */
    public ChallengeStats(final int morale, final int power) {
        if (morale <= 0 || morale > 100) {
            throw new IllegalArgumentException("Morale must be between 1 and 100 inclusive.");
        }
        if (power < 0) {
            throw new IllegalArgumentException("Power cannot be negative.");
        }
        this.morale = morale;
        this.power = power;
    }

    public int getMorale() {
        return this.morale;
    }

    public void setMorale(int morale) {
        if (morale < 0 || morale > 100) {
            throw new IllegalArgumentException("Morale must be between 0 and 100 inclusive.");
        }
        this.morale = morale;
    }

    public int getPower() {
        return this.power;
    }

    public void loseMorale(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Pressure amount must be greater than zero.");
        }
        this.morale -= amount;
        if (this.morale < 0) {
            this.morale = 0;
        }
    }

    public boolean isMotivated() {
        return this.morale > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeStats that = (ChallengeStats) o;
        return morale == that.morale && power == that.power;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(morale, power);
    }
}
