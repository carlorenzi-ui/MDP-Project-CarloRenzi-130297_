package it.unicam.cs.mpgc.afc250713.model.student;

import java.beans.PropertyChangeSupport;

/**
 * Tracks the student's budget (money provided by the family, plus whatever
 * is earned with part-time jobs), triggering financial ruin when it drops
 * to zero.
 */
public class BudgetSystem {
    public static final int MAX_BUDGET = 200;
    public static final int WEEKLY_UPKEEP = 10;

    private int budget;
    private final PropertyChangeSupport support;
    private final Runnable onBankruptcy;

    public BudgetSystem(PropertyChangeSupport support, Runnable onBankruptcy, int startingBudget) {
        this.support = support;
        this.onBankruptcy = onBankruptcy;
        this.budget = clamp(startingBudget);
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(MAX_BUDGET, value));
    }

    public int getBudget() {
        return this.budget;
    }

    public void setBudget(int budget) {
        if (budget < 0 || budget > MAX_BUDGET) {
            throw new IllegalArgumentException("Budget must be between 0 and " + MAX_BUDGET);
        }
        int old = this.budget;
        this.budget = budget;
        support.firePropertyChange("budget", old, this.budget);
    }

    /**
     * Applies a signed change to the budget, clamping to the valid range and
     * triggering bankruptcy if it hits zero.
     *
     * @param amount Positive to earn/receive money, negative to spend it.
     */
    public void modifyBudget(int amount) {
        int old = this.budget;
        int updated = clamp(this.budget + amount);
        this.budget = updated;
        support.firePropertyChange("budget", old, this.budget);
        if (this.budget <= 0 && this.onBankruptcy != null) {
            this.onBankruptcy.run();
        }
    }

    /**
     * Deducts the standard weekly cost of living.
     */
    public void payWeeklyUpkeep() {
        modifyBudget(-WEEKLY_UPKEEP);
    }
}
