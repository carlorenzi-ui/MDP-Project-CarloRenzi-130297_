package it.unicam.cs.mpgc.afc250713.model.student;

import java.beans.PropertyChangeSupport;

/**
 * Tracks accumulated fatigue from exam sessions, triggering a burnout
 * when the maximum fatigue is reached.
 */
public class FatigueSystem {
    public static final int MAX_FATIGUE = 100;
    public static final int EXAM_SESSION_FATIGUE = 25;

    private int fatigue = 0;
    private final PropertyChangeSupport support;
    private final Runnable onBurnout;

    public FatigueSystem(PropertyChangeSupport support, Runnable onBurnout) {
        this.support = support;
        this.onBurnout = onBurnout;
    }

    public int getFatigue() {
        return this.fatigue;
    }

    public void setFatigue(int fatigue) {
        if (fatigue < 0 || fatigue > MAX_FATIGUE) {
            throw new IllegalArgumentException("Fatigue must be between 0 and " + MAX_FATIGUE);
        }
        int old = this.fatigue;
        this.fatigue = fatigue;
        support.firePropertyChange("fatigue", old, this.fatigue);
    }

    public void modifyFatigue(int amount) {
        setFatigue(Math.max(0, Math.min(MAX_FATIGUE, this.fatigue + amount)));
    }

    public void addFatigue(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Fatigue increase cannot be negative.");
        }
        int old = this.fatigue;
        this.fatigue += amount;
        if (this.fatigue >= MAX_FATIGUE) {
            this.fatigue = MAX_FATIGUE;
            support.firePropertyChange("fatigue", old, this.fatigue);
            if (this.onBurnout != null) {
                this.onBurnout.run();
            }
        } else {
            support.firePropertyChange("fatigue", old, this.fatigue);
        }
    }

    public void sufferExamSession() {
        this.addFatigue(EXAM_SESSION_FATIGUE);
    }
}
