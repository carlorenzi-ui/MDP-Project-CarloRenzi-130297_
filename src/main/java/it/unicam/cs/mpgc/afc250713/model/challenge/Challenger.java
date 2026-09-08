package it.unicam.cs.mpgc.afc250713.model.challenge;

/**
 * Entity capable of putting pressure on another entity (a student answering
 * a question, or an exam grilling the student).
 */
public interface Challenger {

    /**
     * Engages the given target, inflicting pressure on it.
     *
     * @param target The target receiving the pressure.
     */
    void engage(Vulnerable target);
}
