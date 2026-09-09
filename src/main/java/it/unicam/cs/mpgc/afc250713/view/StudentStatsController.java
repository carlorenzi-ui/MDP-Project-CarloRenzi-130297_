package it.unicam.cs.mpgc.afc250713.view;

import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.function.Consumer;

import it.unicam.cs.mpgc.afc250713.model.item.Item;
import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;
import it.unicam.cs.mpgc.afc250713.model.student.BudgetSystem;
import it.unicam.cs.mpgc.afc250713.model.student.FatigueSystem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controls the left-side "libretto universitario" panel, keeping it in sync
 * with the student's stats via property change events.
 */
public class StudentStatsController {

    @FXML
    private ProgressBar moraleBar;
    @FXML
    private Label moraleValue;
    @FXML
    private ProgressBar budgetBar;
    @FXML
    private Label budgetValue;
    @FXML
    private ProgressBar fatigueBar;
    @FXML
    private Label fatigueValue;
    @FXML
    private Label cfuValue;
    @FXML
    private Label mediaValue;
    @FXML
    private Label annoValue;
    @FXML
    private VBox zainoContainer;

    private AbstractStudent student;
    private Consumer<Item> onUseItem;

    /**
     * Wires the panel to the given student and registers for live updates.
     */
    public void init(AbstractStudent student, Consumer<Item> onUseItem) {
        this.student = student;
        this.onUseItem = onUseItem;

        PropertyChangeListener listener = evt -> Platform.runLater(this::refresh);
        student.addPropertyChangeListener(listener);

        refresh();
    }

    /**
     * Refreshes all displayed values from the current student state.
     */
    public void refresh() {
        if (this.student == null) {
            return;
        }

        int morale = this.student.getMorale();
        moraleBar.setProgress(morale / (double) AbstractStudent.MAX_MORALE);
        moraleValue.setText(morale + " / " + AbstractStudent.MAX_MORALE);

        int budget = this.student.getBudget();
        budgetBar.setProgress(budget / (double) BudgetSystem.MAX_BUDGET);
        budgetValue.setText(budget + " / " + BudgetSystem.MAX_BUDGET);

        int fatigue = this.student.getFatigue();
        fatigueBar.setProgress(fatigue / (double) FatigueSystem.MAX_FATIGUE);
        fatigueValue.setText(fatigue + " / " + FatigueSystem.MAX_FATIGUE);

        cfuValue.setText(this.student.getCfu() + " / "
                + it.unicam.cs.mpgc.afc250713.model.student.AcademicProgress.CFU_TARGET_LAUREA);
        mediaValue.setText(this.student.getEsamiSuperati() == 0
                ? "-" : String.format("%.1f", this.student.getMedia()));
        annoValue.setText(String.valueOf(this.student.getAnnoFuoriCorso()));

        refreshZaino();
    }

    private void refreshZaino() {
        zainoContainer.getChildren().clear();
        Map<Item, Integer> items = this.student.getZainoContents();

        boolean hasAny = items.values().stream().anyMatch(amount -> amount > 0);
        if (!hasAny) {
            Label empty = new Label("Vuoto.");
            empty.getStyleClass().add("afc-stat-val");
            zainoContainer.getChildren().add(empty);
            return;
        }

        items.forEach((item, amount) -> {
            if (amount <= 0) {
                return;
            }

            VBox card = new VBox(4);
            card.getStyleClass().add("afc-zaino-item");

            Label nameLabel = new Label(item.getName());
            nameLabel.getStyleClass().add("afc-stat-val");
            nameLabel.setWrapText(true);
            nameLabel.setMaxWidth(220);

            HBox bottomRow = new HBox(8);
            bottomRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label amountLabel = new Label("Quantita': " + amount);
            amountLabel.getStyleClass().add("afc-stat-label");

            Button useButton = new Button("Usa");
            useButton.getStyleClass().add("afc-button-tiny");
            useButton.setOnAction(e -> {
                if (this.onUseItem != null) {
                    this.onUseItem.accept(item);
                }
            });

            HBox spacer = new HBox();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            bottomRow.getChildren().addAll(amountLabel, spacer, useButton);

            card.getChildren().addAll(nameLabel, bottomRow);
            zainoContainer.getChildren().add(card);
        });
    }
}
