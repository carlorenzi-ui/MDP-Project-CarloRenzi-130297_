package it.unicam.cs.mpgc.afc250713.model.action;

import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;

/**
 * Strategy pattern: encapsulates one of the actions the student can spend
 * a week on (study, work, go out). New actions can be plugged in without
 * touching the game engine.
 */
public interface WeeklyAction {

    /**
     * Applies this action's effects to the student.
     *
     * @param student The student performing the action.
     * @return A narration string describing what happened.
     */
    String execute(AbstractStudent student);

    /**
     * @return A short display label for this action.
     */
    String getLabel();
}
