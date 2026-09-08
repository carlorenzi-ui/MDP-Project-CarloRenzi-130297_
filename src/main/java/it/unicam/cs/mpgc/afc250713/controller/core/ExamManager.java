package it.unicam.cs.mpgc.afc250713.controller.core;

import it.unicam.cs.mpgc.afc250713.model.exam.Exam;
import it.unicam.cs.mpgc.afc250713.model.strategy.ExamAttemptResult;
import it.unicam.cs.mpgc.afc250713.model.strategy.ExamStrategy;
import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;

import java.util.Objects;

/**
 * Coordinates a single exam attempt between a student and an exam.
 * Unlike a classic turn-based fight, the "boss battle" here is resolved by
 * picking one {@link ExamStrategy} (Hard Study, Lazy Study, or Cheat): the
 * chosen tactic decides in one shot whether the exam's resistance is broken.
 */
public class ExamManager {
    private final AbstractStudent student;
    private final Exam exam;
    private boolean examStarted;
    private ExamAttemptResult lastResult;

    public ExamManager(final AbstractStudent student, final Exam exam) {
        this.student = Objects.requireNonNull(student, "Student cannot be null");
        this.exam = Objects.requireNonNull(exam, "Exam cannot be null");
        this.examStarted = false;
    }

    /**
     * Resolves the exam attempt using the chosen strategy: applies the
     * morale cost to the student and, if the attempt succeeds, breaks the
     * exam's resistance.
     *
     * @param strategy The tactic chosen by the player for this attempt.
     */
    public void resolveAttempt(ExamStrategy strategy) {
        if (isExamOver()) {
            return;
        }
        this.examStarted = true;

        ExamAttemptResult result = strategy.attempt(this.student);
        this.lastResult = result;

        if (result.getMoraleCost() > 0 && this.student.isMotivated()) {
            int cappedCost = Math.min(result.getMoraleCost(), this.student.getMorale());
            this.student.loseMorale(cappedCost);
        }

        if (result.isPassed()) {
            this.exam.setMorale(0);
        }
    }

    /**
     * @return The outcome of the last resolved attempt, or null if none yet.
     */
    public ExamAttemptResult getLastResult() {
        return this.lastResult;
    }

    /**
     * @return true if the exam attempt has been resolved (student gave up,
     *         or the exam's resistance was broken by a successful strategy).
     */
    public boolean isExamOver() {
        return !this.student.isMotivated() || !this.exam.isMotivated();
    }

    /**
     * @return true if the student passed the exam.
     */
    public boolean isStudentVictorious() {
        return this.lastResult != null && this.lastResult.isPassed();
    }

    /**
     * @return true if this attempt has already been resolved once.
     */
    public boolean hasExamStarted() {
        return this.examStarted;
    }

    public AbstractStudent getStudent() {
        return this.student;
    }

    public Exam getExam() {
        return this.exam;
    }
}
