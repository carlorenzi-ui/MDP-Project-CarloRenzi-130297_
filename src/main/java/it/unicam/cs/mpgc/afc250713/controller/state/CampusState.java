package it.unicam.cs.mpgc.afc250713.controller.state;

import it.unicam.cs.mpgc.afc250713.controller.core.GameManager;
import it.unicam.cs.mpgc.afc250713.model.ending.EndingType;
import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;
import it.unicam.cs.mpgc.afc250713.model.subject.Subject;

/**
 * The safe zone: the campus. From here, students can spend their week or
 * attempt an exam, provided it is an exam session week.
 */
public class CampusState implements AcademicState {

    @Override
    public void attemptExam(GameManager context, String subjectId) {
        final Subject subject = context.getWorldMap().get(subjectId);
        if (subject == null) {
            throw new IllegalArgumentException("Materia inesistente: " + subjectId);
        }
        if (context.isSubjectCompleted(subjectId)) {
            throw new IllegalStateException("Hai gia' superato " + subject.getName() + ".");
        }
        if (!context.isExamSessionWeek()) {
            throw new IllegalStateException("Non e' ancora sessione d'esame. Continua a prepararti.");
        }

        context.setCurrentSubject(subject);
        context.startExamAttempt();
        context.changeState(new ExamAttemptState());
    }

    @Override
    public void abandonExam(GameManager context) {
        throw new IllegalStateException("Non sei dentro nessun esame.");
    }

    @Override
    public void resolveExamEnd(GameManager context) {
        // Nothing to resolve on campus.
    }

    @Override
    public void chooseCambioFacolta(GameManager context) {
        AbstractStudent student = context.getStudent();
        if (student.getMorale() > GameManager.CAMBIO_FACOLTA_MORALE_THRESHOLD
                || student.getBudget() <= GameManager.CAMBIO_FACOLTA_BUDGET_THRESHOLD) {
            throw new IllegalStateException(
                    "Puoi cambiare facolta' solo se il morale e' basso ma il budget e' ancora sano.");
        }
        context.changeState(new EndingState(EndingType.CAMBIO_FACOLTA));
    }

    @Override
    public AcademicPhase getType() {
        return AcademicPhase.CAMPUS;
    }
}
