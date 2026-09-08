package it.unicam.cs.mpgc.afc250713.controller.core;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.Random;

import it.unicam.cs.mpgc.afc250713.controller.events.EventDispatcher;
import it.unicam.cs.mpgc.afc250713.controller.state.AcademicPhase;
import it.unicam.cs.mpgc.afc250713.controller.state.AcademicState;
import it.unicam.cs.mpgc.afc250713.controller.state.CampusState;
import it.unicam.cs.mpgc.afc250713.model.action.WeeklyAction;
import it.unicam.cs.mpgc.afc250713.model.ending.EndingType;
import it.unicam.cs.mpgc.afc250713.model.exam.Exam;
import it.unicam.cs.mpgc.afc250713.model.exam.ExamFactory;
import it.unicam.cs.mpgc.afc250713.model.strategy.CheatStrategy;
import it.unicam.cs.mpgc.afc250713.model.strategy.ExamStrategy;
import it.unicam.cs.mpgc.afc250713.model.strategy.HardStudyStrategy;
import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;
import it.unicam.cs.mpgc.afc250713.model.subject.Subject;

/**
 * Single coordinator of the whole game loop ("L'Ultimo Appello").
 * Owns the student, the world of subjects, the current {@link AcademicState}
 * and delegates all rule enforcement to it, following the State pattern.
 */
public class GameManager {
    /** How many weeks pass between one exam session and the next. */
    public static final int EXAM_SESSION_INTERVAL_WEEKS = 4;
    /** Morale threshold under which "cambio facolta'" becomes selectable. */
    public static final int CAMBIO_FACOLTA_MORALE_THRESHOLD = 30;
    /** Minimum budget required to still afford changing faculty. */
    public static final int CAMBIO_FACOLTA_BUDGET_THRESHOLD = 50;
    /** Grade average required to unlock "Laurea con Lode" once graduated. */
    public static final double LODE_MEDIA_THRESHOLD = 27.0;

    private final AbstractStudent student;
    private final Map<String, Subject> worldMap;
    private final ExamFactory examFactory;
    private final EventDispatcher eventDispatcher;
    private final Random random;

    private AcademicState currentState;
    private Subject currentSubject;
    private ExamManager activeExam;
    private ExamStrategy lastStrategyUsed;
    private int weekNumber;
    private final java.util.List<String> ethicalChoiceHistory;
    private final java.util.Set<String> completedSubjects;

    public GameManager(final AbstractStudent student, final Map<String, Subject> worldMap,
                        final ExamFactory examFactory) {
        this.student = student;
        this.worldMap = worldMap;
        this.examFactory = examFactory;
        this.eventDispatcher = new EventDispatcher();
        this.random = new Random();
        this.currentState = new CampusState();
        this.weekNumber = 1;
        this.ethicalChoiceHistory = new java.util.ArrayList<>();
        this.completedSubjects = new java.util.HashSet<>();
    }

    // ---------------------------------------------------------------
    // Weekly loop
    // ---------------------------------------------------------------

    /**
     * Spends the current week on the given action, then advances time.
     *
     * @param action The chosen weekly action.
     */
    public void performWeeklyAction(final WeeklyAction action) {
        if (this.currentState.getType() != AcademicPhase.CAMPUS) {
            throw new IllegalStateException("Puoi allocare la settimana solo dal campus.");
        }
        String narration = action.execute(this.student);
        logEvent("Settimana " + this.weekNumber + " (" + action.getLabel() + "): " + narration);
        this.weekNumber++;

        if (!this.student.isMotivated()) {
            changeState(new it.unicam.cs.mpgc.afc250713.controller.state.EndingState(EndingType.ABBANDONO));
        }
    }

    public boolean isExamSessionWeek() {
        return this.weekNumber % EXAM_SESSION_INTERVAL_WEEKS == 0;
    }

    // ---------------------------------------------------------------
    // Exam flow (delegated to the current state)
    // ---------------------------------------------------------------

    public void attemptExam(final String subjectId) {
        this.currentState.attemptExam(this, subjectId);
    }

    public void abandonExam() {
        this.currentState.abandonExam(this);
    }

    public void chooseCambioFacolta() {
        this.currentState.chooseCambioFacolta(this);
    }

    /**
     * Resolves the active exam attempt using the chosen strategy, logging
     * the ethical dilemma when cheating is picked, then hands control back
     * to the current state for cleanup.
     *
     * @param strategy The tactic chosen by the player.
     */
    public void chooseExamStrategy(final ExamStrategy strategy) {
        if (this.currentState.getType() != AcademicPhase.IN_EXAM || this.activeExam == null) {
            throw new IllegalStateException("Non sei in un esame al momento.");
        }

        this.lastStrategyUsed = strategy;
        if (strategy instanceof CheatStrategy) {
            recordEthicalChoice("Hai scelto di copiare all'esame di " + this.currentSubject.getName() + ".");
        }

        this.activeExam.resolveAttempt(strategy);

        if (this.activeExam.getLastResult() != null) {
            logEvent(this.activeExam.getLastResult().getNarration());
            if (strategy instanceof CheatStrategy && this.activeExam.getLastResult().isCaughtCheating()) {
                recordEthicalChoice("Sei stato beccato a copiare: l'esame e' annullato.");
            }
        }

        this.currentState.resolveExamEnd(this);
    }

    public void startExamAttempt() {
        String variant = this.currentSubject.getNextExamVariant();
        String statsId = variant != null ? variant : this.currentSubject.getId();
        Exam exam = this.examFactory.create(statsId, this.currentSubject.getCfuReward());
        this.activeExam = new ExamManager(this.student, exam);
        logEvent("Sessione d'esame: ti siedi per l'appello di " + this.currentSubject.getName() + ".");
    }

    public void applyWeeklyFatigueAndCheckBurnout() {
        this.student.sufferExamSession();
    }

    public int rollVotoFor(ExamStrategy strategy) {
        if (strategy instanceof HardStudyStrategy) {
            return 24 + this.random.nextInt(7); // 24-30
        }
        if (strategy instanceof it.unicam.cs.mpgc.afc250713.model.strategy.LazyStudyStrategy) {
            return 18 + this.random.nextInt(7); // 18-24
        }
        return 18; // Cheat: a purely nominal pass.
    }

    // ---------------------------------------------------------------
    // State machine plumbing
    // ---------------------------------------------------------------

    public void changeState(final AcademicState newState) {
        AcademicState old = this.currentState;
        this.currentState = newState;
        this.eventDispatcher.firePropertyChange("state", old, newState);
    }

    public void setCurrentSubject(final Subject subject) {
        this.currentSubject = subject;
    }

    public Subject getCurrentSubject() {
        return this.currentSubject;
    }

    public boolean isSubjectCompleted(String subjectId) {
        return this.completedSubjects.contains(subjectId);
    }

    public void markSubjectCompleted(String subjectId) {
        this.completedSubjects.add(subjectId);
    }

    public java.util.Set<String> getCompletedSubjects() {
        return java.util.Collections.unmodifiableSet(this.completedSubjects);
    }

    public void restoreCompletedSubjects(java.util.Collection<String> subjectIds) {
        this.completedSubjects.clear();
        if (subjectIds != null) {
            this.completedSubjects.addAll(subjectIds);
        }
    }

    public void clearActiveExam() {
        this.activeExam = null;
        this.currentSubject = null;
        this.lastStrategyUsed = null;
    }

    public ExamManager getActiveExam() {
        return this.activeExam;
    }

    public ExamStrategy getLastStrategyUsed() {
        return this.lastStrategyUsed;
    }

    public AcademicState getCurrentState() {
        return this.currentState;
    }

    public EndingType getCurrentEndingType() {
        if (this.currentState instanceof it.unicam.cs.mpgc.afc250713.controller.state.EndingState) {
            return ((it.unicam.cs.mpgc.afc250713.controller.state.EndingState) this.currentState).getEndingType();
        }
        return null;
    }

    // ---------------------------------------------------------------
    // Accessors and event log
    // ---------------------------------------------------------------

    public AbstractStudent getStudent() {
        return this.student;
    }

    public Map<String, Subject> getWorldMap() {
        return this.worldMap;
    }

    public int getWeekNumber() {
        return this.weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public void logEvent(String message) {
        this.eventDispatcher.logEvent(message);
    }

    /**
     * Notifies listeners (the UI) that a subject's rewards were just granted,
     * so they can be rendered (e.g. via a Visitor-based renderer).
     */
    public void fireRewardsGranted(Subject subject) {
        this.eventDispatcher.firePropertyChange("rewardsGranted", null, subject);
    }

    public void recordEthicalChoice(String message) {
        this.ethicalChoiceHistory.add(message);
        logEvent(message);
    }

    public List<String> getEthicalChoiceHistory() {
        return java.util.Collections.unmodifiableList(this.ethicalChoiceHistory);
    }

    public List<String> getEventLog() {
        return this.eventDispatcher.getEventLog();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.eventDispatcher.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.eventDispatcher.removePropertyChangeListener(listener);
    }
}
