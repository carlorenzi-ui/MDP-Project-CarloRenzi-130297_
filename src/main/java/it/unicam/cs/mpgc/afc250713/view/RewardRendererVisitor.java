package it.unicam.cs.mpgc.afc250713.view;

import it.unicam.cs.mpgc.afc250713.model.subject.ItemReward;
import it.unicam.cs.mpgc.afc250713.model.subject.RewardVisitor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Generates JavaFX UI components for the different types of exam rewards.
 */
public class RewardRendererVisitor implements RewardVisitor {
    private final VBox container;

    public RewardRendererVisitor() {
        this.container = new VBox(5);
    }

    @Override
    public void visit(ItemReward reward) {
        String name = reward.getItem().getName();
        Label lbl = new Label("- " + name + ": " + reward.getAmount());
        lbl.getStyleClass().add("afc-stat-val");
        this.container.getChildren().add(lbl);
    }

    /**
     * @return Container with all the generated nodes.
     */
    public Node getGraphic() {
        return this.container;
    }
}
