package it.unicam.cs.mpgc.afc250713.model.strategy;

import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;

import java.util.Random;

/**
 * "Domanda a piacere": low morale cost, but only reliable for students with
 * a solid grade average. Unlocked if the student has been frequenting
 * lectures (represented here by a high enough media voti).
 */
public class LazyStudyStrategy implements ExamStrategy {

    /** Minimum grade average required for this strategy to have real odds. */
    public static final double MIN_MEDIA_REQUIRED = 24.0;
    public static final int MORALE_COST = 10;
    /** Success probability when the media requirement is met. */
    public static final double SUCCESS_PROBABILITY_QUALIFIED = 0.5;
    /** Almost-certain failure probability when attempted without enough media. */
    public static final double SUCCESS_PROBABILITY_UNQUALIFIED = 0.05;

    private final Random random;

    public LazyStudyStrategy() {
        this(new Random());
    }

    public LazyStudyStrategy(Random random) {
        this.random = random;
    }

    @Override
    public ExamAttemptResult attempt(AbstractStudent student) {
        boolean qualified = student.getMedia() > MIN_MEDIA_REQUIRED;
        double probability = qualified ? SUCCESS_PROBABILITY_QUALIFIED : SUCCESS_PROBABILITY_UNQUALIFIED;

        if (student.isAppuntiEquipped()) {
            probability = Math.min(0.95, probability + HardStudyStrategy.APPUNTI_BONUS);
            student.getStudyEquipment().onExamAttempt();
        }

        boolean passed = this.random.nextDouble() < probability;

        String narration;
        if (passed) {
            narration = "Domanda a piacere: la tua preparazione pregressa ha pagato.";
        } else if (qualified) {
            narration = "Domanda a piacere: sfortuna, la domanda scelta non era il tuo forte.";
        } else {
            narration = "Domanda a piacere: senza basi solide, l'improvvisazione non basta.";
        }
        return new ExamAttemptResult(passed, MORALE_COST, false, narration);
    }

    @Override
    public boolean isAvailable(AbstractStudent student) {
        // Always selectable: an unprepared attempt is simply a near-certain failure,
        // matching "fallimento quasi certo se sei pigro" from the design spec.
        return true;
    }

    @Override
    public String getLabel() {
        return "Domanda a piacere";
    }
}
