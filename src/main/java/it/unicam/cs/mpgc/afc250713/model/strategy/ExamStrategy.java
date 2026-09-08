package it.unicam.cs.mpgc.afc250713.model.strategy;

import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;

/**
 * Strategy pattern: encapsulates a tactic for tackling an exam attempt
 * (hard study, lazy study, or cheating). New tactics can be added without
 * modifying the game engine (Open/Closed Principle).
 */
public interface ExamStrategy {

    /**
     * Attempts the exam using this strategy's tactic.
     *
     * @param student The student attempting the exam.
     * @return The outcome of the attempt.
     */
    ExamAttemptResult attempt(AbstractStudent student);

    /**
     * @return True if the given student is currently allowed to use this strategy.
     */
    boolean isAvailable(AbstractStudent student);

    /**
     * @return A short display label for this strategy.
     */
    String getLabel();
}
