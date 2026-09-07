package it.unicam.cs.mpgc.afc250713.model.action;

import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;

/**
 * Takes on a part-time job for the week: replenishes the budget, at the
 * cost of morale (stress, tiredness).
 */
public class PartTimeJobAction implements WeeklyAction {

    public static final int BUDGET_GAIN = 40;
    public static final int MORALE_COST = 10;

    @Override
    public String execute(AbstractStudent student) {
        student.modifyBudget(BUDGET_GAIN);
        if (student.getMorale() > MORALE_COST) {
            student.loseMorale(MORALE_COST);
        } else {
            student.setMorale(0);
        }
        return "Un turno al bar sotto casa: il portafoglio respira, ma le energie no.";
    }

    @Override
    public String getLabel() {
        return "Lavoretto part-time";
    }
}
