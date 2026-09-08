package it.unicam.cs.mpgc.afc250713.model.exam;

import it.unicam.cs.mpgc.afc250713.model.challenge.ChallengeStats;
import it.unicam.cs.mpgc.afc250713.utils.ChallengeStatsService;

/**
 * Data-driven factory for exam instantiation.
 */
public class ExamFactory {

    private final ChallengeStatsService statsService;

    public ExamFactory(ChallengeStatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * Instantiates an exam based on its textual subject identifier.
     *
     * @param subjectId Textual identifier (e.g. "analisi_1").
     * @param cfuReward CFU awarded when this exam is passed.
     * @return Fully configured exam instance.
     */
    public Exam create(final String subjectId, final int cfuReward) {
        if (subjectId == null || subjectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject ID cannot be null or empty.");
        }

        final ChallengeStats stats = this.statsService.getStatsFor(subjectId);
        return new Exam(stats, subjectId.toUpperCase(), cfuReward);
    }
}
