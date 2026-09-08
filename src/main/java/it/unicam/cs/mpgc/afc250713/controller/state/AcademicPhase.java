package it.unicam.cs.mpgc.afc250713.controller.state;

/**
 * Identifiers for the core game loop phases.
 */
public enum AcademicPhase {
    /** On campus, free to allocate the week or pick an exam. */
    CAMPUS,
    /** Actively attempting an exam. */
    IN_EXAM,
    /** The run has ended (graduation, drop-out, or change of faculty). */
    ENDING
}
