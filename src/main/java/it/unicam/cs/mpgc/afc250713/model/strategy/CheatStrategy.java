package it.unicam.cs.mpgc.afc250713.model.strategy;

import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;

import java.util.Random;

/**
 * "Copiare": the ethical dilemma. Guarantees passing the exam at no morale
 * cost, but carries a real risk of being caught, which annuls the exam and
 * crushes morale to zero.
 */
public class CheatStrategy implements ExamStrategy {

    public static final double CAUGHT_PROBABILITY = 0.3;
    /** Large enough to floor morale to zero regardless of current value. */
    private static final int MORALE_CRASH = 1000;

    private final Random random;

    public CheatStrategy() {
        this(new Random());
    }

    public CheatStrategy(Random random) {
        this.random = random;
    }

    @Override
    public ExamAttemptResult attempt(AbstractStudent student) {
        boolean caught = this.random.nextDouble() < CAUGHT_PROBABILITY;

        if (caught) {
            String narration = "Copiare: il professore ti ha beccato. L'esame e' annullato e il morale crolla.";
            return new ExamAttemptResult(false, MORALE_CRASH, true, narration);
        }

        String narration = "Copiare: nessuno se n'e' accorto. L'esame e' passato senza fatica... "
                + "ma tu sai come.";
        return new ExamAttemptResult(true, 0, false, narration);
    }

    @Override
    public boolean isAvailable(AbstractStudent student) {
        return true;
    }

    @Override
    public String getLabel() {
        return "Copiare";
    }
}
