package it.unicam.cs.mpgc.afc250713.model.action;

import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;
import it.unicam.cs.mpgc.afc250713.model.student.StudyBuffType;

/**
 * Spends the week buried in books: drains morale and a little budget
 * (books, printouts, coffee), but sharpens the student's notes for the
 * next exam attempt.
 */
public class StudyAction implements WeeklyAction {

    public static final int MORALE_COST = 15;
    public static final int BUDGET_COST = 5;

    @Override
    public String execute(AbstractStudent student) {
        if (student.getMorale() > MORALE_COST) {
            student.loseMorale(MORALE_COST);
        } else {
            student.setMorale(0);
        }
        student.modifyBudget(-BUDGET_COST);

        int currentAppunti = student.getStudyEquipment().getBuffValue(StudyBuffType.APPUNTI);
        student.getStudyEquipment().addBuff(StudyBuffType.APPUNTI, currentAppunti + 1);

        return "Settimana in biblioteca: il morale ne risente, ma i tuoi appunti sono un po' piu' solidi.";
    }

    @Override
    public String getLabel() {
        return "Studia";
    }
}
