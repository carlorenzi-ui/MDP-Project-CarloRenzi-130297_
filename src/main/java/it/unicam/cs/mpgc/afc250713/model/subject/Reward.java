package it.unicam.cs.mpgc.afc250713.model.subject;

import it.unicam.cs.mpgc.afc250713.model.resource.ResourceCollector;

/**
 * Something a student can obtain by passing an exam.
 */
public interface Reward {
    /**
     * Triggers the reward effect on the student.
     *
     * @param collector Target student.
     */
    void applyTo(ResourceCollector collector);

    /**
     * Dispatches the visitor to the concrete reward type.
     *
     * @param visitor The visiting logic instance.
     */
    void accept(RewardVisitor visitor);
}
