package it.unicam.cs.mpgc.afc250713.model.action;

import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;

/**
 * Spends the week out with friends: recovers morale, at the cost of budget.
 */
public class SocialOutingAction implements WeeklyAction {

    public static final int MORALE_GAIN = 20;
    public static final int BUDGET_COST = 15;

    @Override
    public String execute(AbstractStudent student) {
        student.restoreMorale(MORALE_GAIN);
        student.modifyBudget(-BUDGET_COST);
        return "Una serata fuori con gli amici: il morale torna a respirare, il portafoglio un po' meno.";
    }

    @Override
    public String getLabel() {
        return "Esci con gli amici";
    }
}
