package it.unicam.cs.mpgc.afc250713.controller.state;

import it.unicam.cs.mpgc.afc250713.controller.core.GameManager;

/**
 * Base contract for the state machine pattern managing the game loop.
 * Defines how actions behave depending on what the student is currently doing.
 */
public interface AcademicState {
    /**
     * Attempts to start an exam attempt for the given subject.
     */
    void attemptExam(GameManager context, String subjectId);

    /**
     * Attempts to walk away from the exam room before the attempt is
     * resolved. May apply penalties or be blocked.
     */
    void abandonExam(GameManager context);

    /**
     * Handles cleanup after an exam attempt is resolved (CFU, rewards,
     * morale/budget checks, graduation checks).
     */
    void resolveExamEnd(GameManager context);

    /**
     * Voluntarily gives up on this degree and changes faculty. Only
     * available under specific conditions from the campus.
     */
    void chooseCambioFacolta(GameManager context);

    AcademicPhase getType();
}
