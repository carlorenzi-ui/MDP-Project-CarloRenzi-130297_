package it.unicam.cs.mpgc.afc250713.model.student;

import it.unicam.cs.mpgc.afc250713.model.challenge.ChallengeStats;

/**
 * Concrete playable student: a bachelor's student stuck fuori corso.
 * Provides no additional logic beyond {@link AbstractStudent}.
 */
public class Fuoricorso extends AbstractStudent {
    public Fuoricorso(final ChallengeStats stats, final int startingBudget) {
        super(stats, startingBudget);
    }
}
