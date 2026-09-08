package it.unicam.cs.mpgc.afc250713.controller.state;

import it.unicam.cs.mpgc.afc250713.controller.core.ExamManager;
import it.unicam.cs.mpgc.afc250713.controller.core.GameManager;
import it.unicam.cs.mpgc.afc250713.model.ending.EndingType;
import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;
import it.unicam.cs.mpgc.afc250713.model.subject.Subject;

/**
 * The student is in the middle of an exam attempt.
 */
public class ExamAttemptState implements AcademicState {

    @Override
    public void attemptExam(GameManager context, String subjectId) {
        throw new IllegalStateException("Sei gia' dentro un esame.");
    }

    @Override
    public void abandonExam(GameManager context) {
        ExamManager examManager = context.getActiveExam();
        if (examManager != null && examManager.hasExamStarted()) {
            throw new IllegalStateException("L'esame e' gia' iniziato: non puoi piu' tirarti indietro.");
        }

        context.applyWeeklyFatigueAndCheckBurnout();
        AbstractStudent student = context.getStudent();

        if (!student.isMotivated()) {
            context.changeState(new EndingState(EndingType.ABBANDONO));
            return;
        }

        context.clearActiveExam();
        context.changeState(new CampusState());
    }

    @Override
    public void resolveExamEnd(GameManager context) {
        ExamManager examManager = context.getActiveExam();
        if (examManager == null || !examManager.isExamOver()) {
            return;
        }

        context.applyWeeklyFatigueAndCheckBurnout();
        AbstractStudent student = context.getStudent();

        if (!student.isMotivated()) {
            context.changeState(new EndingState(EndingType.ABBANDONO));
            return;
        }

        if (examManager.isStudentVictorious()) {
            Subject subject = context.getCurrentSubject();
            int voto = context.rollVotoFor(context.getLastStrategyUsed());
            student.registerPassedExam(subject.getCfuReward(), voto);
            subject.claimRewards(student);
            context.markSubjectCompleted(subject.getId());
            context.logEvent("Esame di " + subject.getName() + " superato con " + voto + "!");
            context.fireRewardsGranted(subject);
        } else {
            context.logEvent("Esame di " + context.getCurrentSubject().getName()
                    + " non superato. Ritenta alla prossima sessione.");
        }

        context.clearActiveExam();

        if (student.hasGraduated()) {
            EndingType ending = student.getMedia() >= GameManager.LODE_MEDIA_THRESHOLD
                    ? EndingType.LAUREA_CON_LODE
                    : EndingType.IL_PEZZO_DI_CARTA;
            context.changeState(new EndingState(ending));
            return;
        }

        context.changeState(new CampusState());
    }

    @Override
    public void chooseCambioFacolta(GameManager context) {
        throw new IllegalStateException("Non puoi cambiare facolta' nel mezzo di un esame.");
    }

    @Override
    public AcademicPhase getType() {
        return AcademicPhase.IN_EXAM;
    }
}
