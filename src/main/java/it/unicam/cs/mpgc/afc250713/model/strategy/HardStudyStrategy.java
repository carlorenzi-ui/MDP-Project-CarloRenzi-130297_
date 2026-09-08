package it.unicam.cs.mpgc.afc250713.model.strategy;

import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;

import java.util.Random;

/**
 * "Studio Matto e Disperatissimo": high success probability, but very
 * costly in terms of morale.
 */
public class HardStudyStrategy implements ExamStrategy {

    public static final double SUCCESS_PROBABILITY = 0.8;
    public static final int MORALE_COST = 40;
    /** Bonus success chance granted by having good lecture notes equipped. */
    public static final double APPUNTI_BONUS = 0.1;

    private final Random random;

    public HardStudyStrategy() {
        this(new Random());
    }

    public HardStudyStrategy(Random random) {
        this.random = random;
    }

    @Override
    public ExamAttemptResult attempt(AbstractStudent student) {
        boolean hasNotes = student.isAppuntiEquipped();
        double probability = Math.min(0.95, SUCCESS_PROBABILITY + (hasNotes ? APPUNTI_BONUS : 0));
        if (hasNotes) {
            student.getStudyEquipment().onExamAttempt();
        }

        boolean passed = this.random.nextDouble() < probability;
        String narration = passed
                ? "Studio matto e disperatissimo: hai ripassato tutto all'ultimo secondo e ce l'hai fatta."
                : "Studio matto e disperatissimo: nonostante gli sforzi, la mente ha ceduto durante la prova.";
        return new ExamAttemptResult(passed, MORALE_COST, false, narration);
    }

    @Override
    public boolean isAvailable(AbstractStudent student) {
        return true;
    }

    @Override
    public String getLabel() {
        return "Ripasso dell'ultimo minuto";
    }
}
