package it.unicam.cs.mpgc.afc250713.persistence;

import java.util.List;
import java.util.Map;

/**
 * Flat, serialization-friendly representation of a student's game state.
 */
public class StudentSaveDTO {
    public int morale;
    public int fatigue;
    public int budget;
    public int cfu;
    public int annoFuoriCorso;
    public double media;
    public int esamiSuperati;
    public int weekNumber;
    public int appuntiDurability;
    public int gruppoStudioShield;
    public Map<String, Integer> zaino;
    public List<String> ethicalChoiceHistory;
    public List<String> completedSubjects;
}
