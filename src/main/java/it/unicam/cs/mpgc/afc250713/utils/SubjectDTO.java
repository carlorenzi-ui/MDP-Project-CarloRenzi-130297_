package it.unicam.cs.mpgc.afc250713.utils;

import java.util.Map;

/**
 * Data Transfer Object for JSON parsing of university subjects.
 */
public class SubjectDTO {
    public String name;
    public String description;
    public String difficulty;
    public int cfuReward;
    public Map<String, Integer> rewards;
    public Map<String, Integer> examSpawns;
}
