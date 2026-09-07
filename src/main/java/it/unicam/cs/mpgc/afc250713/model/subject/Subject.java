package it.unicam.cs.mpgc.afc250713.model.subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import it.unicam.cs.mpgc.afc250713.model.resource.ResourceCollector;

/**
 * A university subject that can be attempted during an exam session.
 * Holds the rewards granted on passing and the exam variant(s) it can spawn.
 */
public class Subject {
    public enum Difficulty {
        FACILE, DIFFICILE
    }

    private final String id;
    private final String name;
    private final String description;
    private final Difficulty difficulty;
    private final int cfuReward;
    private final List<Reward> rewards;
    private final Map<String, Integer> examSpawns;

    public Subject(final String id, final String name, final String description,
                    final Difficulty difficulty, final int cfuReward) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name cannot be null or empty.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject description cannot be null or empty.");
        }
        if (difficulty == null) {
            throw new NullPointerException("Difficulty cannot be null.");
        }
        if (cfuReward <= 0) {
            throw new IllegalArgumentException("CFU reward must be greater than zero.");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.cfuReward = cfuReward;
        this.rewards = new ArrayList<>();
        this.examSpawns = new java.util.HashMap<>();
    }

    /**
     * Appends a reward to the exam's pool.
     *
     * @param reward Reward to add.
     */
    public void addReward(final Reward reward) {
        if (reward == null) {
            throw new NullPointerException("Reward cannot be null.");
        }
        this.rewards.add(reward);
    }

    /**
     * Configures spawn amounts for a specific exam variant.
     *
     * @param examVariant Exam variant.
     * @param quantity    Spawn count.
     */
    public void addExamSpawn(final String examVariant, final int quantity) {
        if (examVariant == null) {
            throw new NullPointerException("Exam variant cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.examSpawns.put(examVariant, this.examSpawns.getOrDefault(examVariant, 0) + quantity);
    }

    /**
     * Distributes all collected rewards to the given collector.
     *
     * @param collector Target student receiving the rewards.
     */
    public void claimRewards(final ResourceCollector collector) {
        if (collector == null) {
            throw new NullPointerException("Collector cannot be null.");
        }
        this.rewards.forEach(reward -> reward.applyTo(collector));
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public int getCfuReward() {
        return this.cfuReward;
    }

    /**
     * @return An unmodifiable list of rewards.
     */
    public List<Reward> getRewards() {
        return Collections.unmodifiableList(this.rewards);
    }

    /**
     * @return An unmodifiable map of exam spawns.
     */
    public Map<String, Integer> getExamSpawns() {
        return Collections.unmodifiableMap(this.examSpawns);
    }

    /**
     * @return The next exam variant to spawn.
     */
    public String getNextExamVariant() {
        if (this.examSpawns.isEmpty()) {
            return null;
        }
        return this.examSpawns.keySet().iterator().next();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Subject subject = (Subject) o;
        return id.equals(subject.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
