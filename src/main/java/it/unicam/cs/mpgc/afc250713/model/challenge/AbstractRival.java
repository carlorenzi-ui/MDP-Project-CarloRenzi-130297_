package it.unicam.cs.mpgc.afc250713.model.challenge;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Base wrapper bridging challenge logic (morale/pressure) and property change events.
 */
public abstract class AbstractRival implements Rival {
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final ChallengeStats stats;

    /**
     * Creates a rival with deep-copied stats.
     *
     * @param stats The base stats to copy.
     */
    public AbstractRival(final ChallengeStats stats) {
        if (stats == null) {
            throw new NullPointerException("Challenge stats cannot be null.");
        }
        // Deep copy the stats so this entity has its own mutable state
        this.stats = new ChallengeStats(stats.getMorale(), stats.getPower());
    }

    @Override
    public void engage(Vulnerable target) {
        if (!isMotivated()) {
            throw new IllegalStateException("This rival has given up and cannot act.");
        }
        if (target == null) {
            throw new NullPointerException("Target cannot be null.");
        }
        if (!target.isMotivated()) {
            throw new IllegalArgumentException("Target has already given up.");
        }

        target.loseMorale(getPower());
    }

    @Override
    public void loseMorale(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Pressure amount must be greater than zero.");
        }
        if (!isMotivated()) {
            throw new IllegalStateException("Entity has already given up.");
        }

        int oldMorale = this.stats.getMorale();
        this.stats.loseMorale(amount);
        support.firePropertyChange("morale", oldMorale, this.stats.getMorale());
    }

    @Override
    public boolean isMotivated() {
        return this.stats.isMotivated();
    }

    public int getMorale() {
        return this.stats.getMorale();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * Overrides current morale and triggers property listeners.
     *
     * @param morale The new morale value.
     */
    public void setMorale(final int morale) {
        int oldMorale = this.stats.getMorale();
        this.stats.setMorale(morale);
        support.firePropertyChange("morale", oldMorale, this.stats.getMorale());
    }

    public int getPower() {
        return this.stats.getPower();
    }
}
