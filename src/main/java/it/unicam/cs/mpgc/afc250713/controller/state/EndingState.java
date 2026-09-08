package it.unicam.cs.mpgc.afc250713.controller.state;

import it.unicam.cs.mpgc.afc250713.controller.core.GameManager;
import it.unicam.cs.mpgc.afc250713.model.ending.EndingType;

/**
 * Terminal state reached when the playthrough ends, one way or another.
 * Blocks all further actions and remembers which of the multiple endings
 * was reached, guaranteeing replayability across playthroughs.
 */
public class EndingState implements AcademicState {

    private final EndingType endingType;

    public EndingState(EndingType endingType) {
        if (endingType == null) {
            throw new NullPointerException("Ending type cannot be null.");
        }
        this.endingType = endingType;
    }

    public EndingType getEndingType() {
        return this.endingType;
    }

    @Override
    public void attemptExam(GameManager context, String subjectId) {
        throw new IllegalStateException("La partita e' finita. Non puoi piu' dare esami.");
    }

    @Override
    public void abandonExam(GameManager context) {
        throw new IllegalStateException("La partita e' finita.");
    }

    @Override
    public void resolveExamEnd(GameManager context) {
        // Already ended, nothing to resolve.
    }

    @Override
    public void chooseCambioFacolta(GameManager context) {
        throw new IllegalStateException("La partita e' gia' finita.");
    }

    @Override
    public AcademicPhase getType() {
        return AcademicPhase.ENDING;
    }
}
