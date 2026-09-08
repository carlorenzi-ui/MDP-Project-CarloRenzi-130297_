package it.unicam.cs.mpgc.afc250713.model.strategy;

/**
 * Immutable outcome of a single exam strategy attempt.
 */
public final class ExamAttemptResult {
    private final boolean passed;
    private final int moraleCost;
    private final boolean caughtCheating;
    private final String narration;

    public ExamAttemptResult(boolean passed, int moraleCost, boolean caughtCheating, String narration) {
        if (moraleCost < 0) {
            throw new IllegalArgumentException("Morale cost cannot be negative.");
        }
        this.passed = passed;
        this.moraleCost = moraleCost;
        this.caughtCheating = caughtCheating;
        this.narration = narration;
    }

    public boolean isPassed() {
        return this.passed;
    }

    public int getMoraleCost() {
        return this.moraleCost;
    }

    public boolean isCaughtCheating() {
        return this.caughtCheating;
    }

    public String getNarration() {
        return this.narration;
    }
}
