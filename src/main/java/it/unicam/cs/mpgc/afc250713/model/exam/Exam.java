package it.unicam.cs.mpgc.afc250713.model.exam;

import it.unicam.cs.mpgc.afc250713.model.challenge.AbstractRival;
import it.unicam.cs.mpgc.afc250713.model.challenge.ChallengeStats;

import java.util.Objects;

/**
 * Concrete exam entity ("boss battle"). Its morale represents how much
 * resistance/prova is left before the exam is passed, while its power
 * represents how much stress it inflicts on the student per failed round.
 */
public class Exam extends AbstractRival {

    private final String subjectCode;
    private final int cfuReward;

    /**
     * Creates an exam with the given stats and subject identifier.
     *
     * @param stats       The base challenge stats.
     * @param subjectCode The subject variant name (e.g. "ANALISI_1").
     * @param cfuReward   CFU awarded when this exam is passed.
     */
    public Exam(final ChallengeStats stats, final String subjectCode, final int cfuReward) {
        super(stats);
        if (subjectCode == null) {
            throw new NullPointerException("Subject code cannot be null.");
        }
        if (cfuReward <= 0) {
            throw new IllegalArgumentException("CFU reward must be greater than zero.");
        }
        this.subjectCode = subjectCode;
        this.cfuReward = cfuReward;
    }

    /**
     * @return The exam's subject variant.
     */
    public String getSubjectCode() {
        return this.subjectCode;
    }

    /**
     * @return CFU awarded when this exam is passed.
     */
    public int getCfuReward() {
        return this.cfuReward;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exam exam = (Exam) o;
        return subjectCode.equals(exam.subjectCode) && getMorale() == exam.getMorale()
                && getPower() == exam.getPower();
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectCode, getMorale(), getPower());
    }
}
