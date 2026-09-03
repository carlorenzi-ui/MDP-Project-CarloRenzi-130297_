package it.unicam.cs.mpgc.afc250713.model.student;

import java.beans.PropertyChangeSupport;

/**
 * Handles CFU accumulation, fuoricorso year count, and the running grade
 * average ("media voti"). The CFU target represents the win condition
 * (graduation), while the media determines which ending is unlocked.
 */
public class AcademicProgress {
    /** CFU required to graduate (the CFU still missing for "l'ultimo appello"). */
    public static final int CFU_TARGET_LAUREA = 60;
    /** CFU threshold that, once crossed, adds another year fuoricorso. */
    public static final int CFU_PER_ANNO = 30;

    private int cfu = 0;
    private int annoFuoriCorso = 3;
    private double media = 0.0;
    private int esamiSuperati = 0;
    private final PropertyChangeSupport support;

    public AcademicProgress(PropertyChangeSupport support) {
        this.support = support;
    }

    public int getCfu() {
        return this.cfu;
    }

    public void setCfu(int cfu) {
        int old = this.cfu;
        this.cfu = cfu;
        support.firePropertyChange("cfu", old, this.cfu);
    }

    public int getAnnoFuoriCorso() {
        return this.annoFuoriCorso;
    }

    public void setAnnoFuoriCorso(int anno) {
        int old = this.annoFuoriCorso;
        this.annoFuoriCorso = anno;
        support.firePropertyChange("annoFuoriCorso", old, this.annoFuoriCorso);
    }

    public double getMedia() {
        return this.media;
    }

    public void setMedia(double media) {
        double old = this.media;
        this.media = media;
        support.firePropertyChange("media", old, this.media);
    }

    public int getEsamiSuperati() {
        return this.esamiSuperati;
    }

    public void setEsamiSuperati(int esamiSuperati) {
        this.esamiSuperati = esamiSuperati;
    }

    /**
     * Registers a passed exam: adds CFU, updates the running average with
     * the new grade, and bumps the fuoricorso year count if a new CFU
     * threshold has been crossed.
     *
     * @param cfuGained CFU awarded by the exam.
     * @param voto      Grade obtained (18-30).
     */
    public void registerPassedExam(int cfuGained, int voto) {
        if (cfuGained <= 0) {
            throw new IllegalArgumentException("CFU gained must be greater than zero.");
        }
        if (voto < 18 || voto > 30) {
            throw new IllegalArgumentException("Voto must be between 18 and 30.");
        }

        int previousThreshold = this.cfu / CFU_PER_ANNO;
        setCfu(this.cfu + cfuGained);
        int newThreshold = this.cfu / CFU_PER_ANNO;
        if (newThreshold > previousThreshold) {
            setAnnoFuoriCorso(this.annoFuoriCorso + (newThreshold - previousThreshold));
        }

        double totalPoints = this.media * this.esamiSuperati + voto;
        this.esamiSuperati++;
        setMedia(totalPoints / this.esamiSuperati);
    }

    /**
     * @return True if the CFU target for graduation has been reached.
     */
    public boolean hasGraduated() {
        return this.cfu >= CFU_TARGET_LAUREA;
    }
}
