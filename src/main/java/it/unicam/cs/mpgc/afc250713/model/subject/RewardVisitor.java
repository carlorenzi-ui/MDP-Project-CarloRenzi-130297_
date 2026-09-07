package it.unicam.cs.mpgc.afc250713.model.subject;

/**
 * Visitor pattern interface for reward variants, decoupling rendering/other
 * behavior from the concrete reward classes.
 */
public interface RewardVisitor {
    /**
     * Handles item-based reward logic.
     *
     * @param reward The item reward instance.
     */
    void visit(ItemReward reward);
}
