package it.unicam.cs.mpgc.afc250713.view;

import java.util.Map;

import it.unicam.cs.mpgc.afc250713.controller.core.GameManager;
import it.unicam.cs.mpgc.afc250713.controller.state.AcademicPhase;
import it.unicam.cs.mpgc.afc250713.model.action.PartTimeJobAction;
import it.unicam.cs.mpgc.afc250713.model.action.SocialOutingAction;
import it.unicam.cs.mpgc.afc250713.model.action.StudyAction;
import it.unicam.cs.mpgc.afc250713.model.action.WeeklyAction;
import it.unicam.cs.mpgc.afc250713.model.ending.EndingType;
import it.unicam.cs.mpgc.afc250713.model.exam.Exam;
import it.unicam.cs.mpgc.afc250713.model.item.Item;
import it.unicam.cs.mpgc.afc250713.model.item.ItemRegistry;
import it.unicam.cs.mpgc.afc250713.model.strategy.CheatStrategy;
import it.unicam.cs.mpgc.afc250713.model.strategy.ExamStrategy;
import it.unicam.cs.mpgc.afc250713.model.strategy.HardStudyStrategy;
import it.unicam.cs.mpgc.afc250713.model.strategy.LazyStudyStrategy;
import it.unicam.cs.mpgc.afc250713.model.subject.Subject;
import it.unicam.cs.mpgc.afc250713.persistence.SaveManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Central JavaFX controller. Swaps between the campus, exam and ending
 * screens depending on the current {@link AcademicPhase}, and reflects the
 * game's event log at the bottom of the window.
 */
public class GameController {

    @FXML
    private StackPane contentArea;
    @FXML
    private StudentStatsController studentStatsController;
    @FXML
    private Button saveButton;
    @FXML
    private Button loadButton;
    @FXML
    private Button restartButton;
    @FXML
    private Button rulesButton;
    @FXML
    private Label weekLabel;
    @FXML
    private VBox logContainer;
    @FXML
    private ScrollPane logScroll;

    private GameManager gameManager;
    private ItemRegistry itemRegistry;
    private SaveManager saveManager;
    private Runnable onRestart;

    public void setSaveManager(SaveManager saveManager) {
        this.saveManager = saveManager;
    }

    /**
     * Wires the controller to the game state and starts rendering.
     */
    public void setGameManager(GameManager gameManager, ItemRegistry itemRegistry, Runnable onRestart) {
        this.gameManager = gameManager;
        this.itemRegistry = itemRegistry;
        this.onRestart = onRestart;

        this.studentStatsController.init(gameManager.getStudent(), this::onUseItem);

        this.gameManager.addPropertyChangeListener(evt -> Platform.runLater(() -> {
            if ("state".equals(evt.getPropertyName())) {
                refreshView();
            } else if ("eventLogAdded".equals(evt.getPropertyName())) {
                appendLogLine((String) evt.getNewValue());
            } else if ("rewardsGranted".equals(evt.getPropertyName())) {
                showRewards((Subject) evt.getNewValue());
            }
        }));

        this.gameManager.getEventLog().forEach(this::appendLogLine);
        refreshView();
    }

    // ---------------------------------------------------------------
    // Screen dispatch
    // ---------------------------------------------------------------

    private void refreshView() {
        weekLabel.setText("Settimana " + this.gameManager.getWeekNumber()
                + (this.gameManager.isExamSessionWeek() ? " - Sessione d'esame!" : ""));

        AcademicPhase phase = this.gameManager.getCurrentState().getType();
        switch (phase) {
            case CAMPUS -> contentArea.getChildren().setAll(buildCampusView());
            case IN_EXAM -> contentArea.getChildren().setAll(buildExamView());
            case ENDING -> contentArea.getChildren().setAll(buildEndingView());
        }
    }

    private VBox buildCampusView() {
        VBox root = new VBox(16);
        root.getStyleClass().add("afc-panel");

        Label title = new Label("CAMPUS");
        title.getStyleClass().add("afc-panel-title");
        root.getChildren().add(title);

        Label actionsTitle = new Label("Come vuoi passare la settimana?");
        actionsTitle.getStyleClass().add("afc-section-label");
        HBox actionRow = new HBox(10,
                buildActionButton(new StudyAction()),
                buildActionButton(new PartTimeJobAction()),
                buildActionButton(new SocialOutingAction()));
        root.getChildren().addAll(actionsTitle, actionRow);

        Label examsTitle = new Label(this.gameManager.isExamSessionWeek()
                ? "E' sessione d'esame! Scegli un appello:"
                : "Materie disponibili (torna in sessione per sostenerle):");
        examsTitle.getStyleClass().add("afc-section-label");
        root.getChildren().add(examsTitle);

        VBox subjectList = new VBox(8);
        for (Map.Entry<String, Subject> entry : this.gameManager.getWorldMap().entrySet()) {
            subjectList.getChildren().add(buildSubjectRow(entry.getKey(), entry.getValue()));
        }
        ScrollPane subjectScroll = new ScrollPane(subjectList);
        subjectScroll.setFitToWidth(true);
        subjectScroll.getStyleClass().add("afc-log-scroll");
        subjectScroll.setPrefHeight(260);
        root.getChildren().add(subjectScroll);

        boolean canChangeFacolta = this.gameManager.getStudent().getMorale()
                <= GameManager.CAMBIO_FACOLTA_MORALE_THRESHOLD
                && this.gameManager.getStudent().getBudget() > GameManager.CAMBIO_FACOLTA_BUDGET_THRESHOLD;
        if (canChangeFacolta) {
            Button cambioButton = new Button("Cambia Facolta' (finale alternativo)");
            cambioButton.getStyleClass().add("afc-button-danger");
            cambioButton.setOnAction(e -> handleAction(() -> this.gameManager.chooseCambioFacolta()));
            root.getChildren().add(cambioButton);
        }

        return root;
    }

    private Button buildActionButton(WeeklyAction action) {
        Button button = new Button(action.getLabel());
        button.getStyleClass().add("afc-button");
        button.setOnAction(e -> handleAction(() -> this.gameManager.performWeeklyAction(action)));
        return button;
    }

    private HBox buildSubjectRow(String subjectId, Subject subject) {
        HBox row = new HBox(10);
        row.getStyleClass().add("afc-subject-row");

        VBox info = new VBox(2);
        Label name = new Label(subject.getName() + "  [" + subject.getDifficulty() + ", "
                + subject.getCfuReward() + " CFU]");
        name.getStyleClass().add("afc-stat-val-big");
        Label desc = new Label(subject.getDescription());
        desc.getStyleClass().add("afc-stat-val");
        desc.setWrapText(true);
        info.getChildren().addAll(name, desc);
        info.setPrefWidth(480);

        Button attemptButton = new Button(this.gameManager.isSubjectCompleted(subjectId) ? "Superato" : "Sostieni");
        attemptButton.getStyleClass().add("afc-button-small");
        attemptButton.setDisable(this.gameManager.isSubjectCompleted(subjectId)
                || !this.gameManager.isExamSessionWeek());
        attemptButton.setOnAction(e -> handleAction(() -> this.gameManager.attemptExam(subjectId)));

        row.getChildren().addAll(info, attemptButton);
        return row;
    }

    private VBox buildExamView() {
        VBox root = new VBox(16);
        root.getStyleClass().add("afc-panel");

        Subject subject = this.gameManager.getCurrentSubject();
        Exam exam = this.gameManager.getActiveExam().getExam();

        Label title = new Label("APPELLO DI " + subject.getName().toUpperCase());
        title.getStyleClass().add("afc-panel-title");
        Label subtitle = new Label("Resistenza dell'esame: " + exam.getMorale()
                + "  -  Stress potenziale: " + exam.getPower());
        subtitle.getStyleClass().add("afc-section-label");

        root.getChildren().addAll(title, subtitle);

        boolean lazyQualified = this.gameManager.getStudent().getMedia() > LazyStudyStrategy.MIN_MEDIA_REQUIRED;

        VBox strategyBox = new VBox(10);
        strategyBox.getChildren().addAll(
                buildStrategyButton(new HardStudyStrategy(),
                        "80% di successo, -40 morale, nessun rischio speciale."),
                buildStrategyButton(new LazyStudyStrategy(),
                        lazyQualified
                                ? "Sei preparato: buone probabilita', solo -10 morale."
                                : "Media troppo bassa: fallimento quasi certo, ma costa poco morale."),
                buildStrategyButton(new CheatStrategy(),
                        "100% di successo, 0 morale... ma il 30% delle volte ti beccano e crolli."));
        root.getChildren().add(strategyBox);

        Button abandonButton = new Button("Rinuncia e torna al campus");
        abandonButton.getStyleClass().add("afc-button-small");
        abandonButton.setOnAction(e -> handleAction(() -> this.gameManager.abandonExam()));
        root.getChildren().add(abandonButton);

        return root;
    }

    private Button buildStrategyButton(ExamStrategy strategy, String description) {
        Button button = new Button(strategy.getLabel() + "\n" + description);
        button.getStyleClass().add("afc-button-strategy");
        button.setWrapText(true);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> handleAction(() -> this.gameManager.chooseExamStrategy(strategy)));
        return button;
    }

    private VBox buildEndingView() {
        VBox root = new VBox(16);
        root.getStyleClass().add("afc-panel");

        EndingType ending = this.gameManager.getCurrentEndingType();
        String[] titleAndText = describeEnding(ending);

        Label title = new Label(titleAndText[0]);
        title.getStyleClass().add("afc-panel-title");
        Label text = new Label(titleAndText[1]);
        text.getStyleClass().add("afc-section-label");
        text.setWrapText(true);

        Button restart = new Button("Ricomincia");
        restart.getStyleClass().add("afc-button");
        restart.setOnAction(e -> {
            if (this.onRestart != null) {
                this.onRestart.run();
            }
        });

        root.getChildren().addAll(title, text, restart);
        return root;
    }

    private String[] describeEnding(EndingType ending) {
        if (ending == null) {
            return new String[]{"FINE", "La partita e' terminata."};
        }
        return switch (ending) {
            case LAUREA_CON_LODE -> new String[]{"LAUREA CON LODE",
                    "Hai completato tutti i CFU richiesti con una media eccellente. "
                            + "La commissione si alza in piedi. Anni di fuoricorso, ripagati."};
            case IL_PEZZO_DI_CARTA -> new String[]{"IL PEZZO DI CARTA",
                    "Hai finito, ma sei esausto e con una media modesta. "
                            + "Laureato e' laureato: ora puoi finalmente dormire."};
            case ABBANDONO -> new String[]{"ABBANDONO",
                    "Il morale ha ceduto prima dei CFU. Lasci l'universita', almeno per ora."};
            case CAMBIO_FACOLTA -> new String[]{"CAMBIO FACOLTA'",
                    "Con il morale a terra ma il portafoglio ancora sano, scegli di ripartire "
                            + "altrove. Non e' una sconfitta: e' un altro inizio."};
        };
    }

    // ---------------------------------------------------------------
    // Item usage / actions / errors
    // ---------------------------------------------------------------

    private void onUseItem(Item item) {
        try {
            this.gameManager.getStudent().useItem(item);
            this.gameManager.logEvent("Hai usato: " + item.getName() + ".");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleAction(Runnable action) {
        try {
            action.run();
            refreshView();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showRewards(Subject subject) {
        if (subject.getRewards().isEmpty()) {
            return;
        }
        RewardRendererVisitor renderer = new RewardRendererVisitor();
        subject.getRewards().forEach(reward -> reward.accept(renderer));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Ricompense per " + subject.getName());
        alert.getDialogPane().setContent(renderer.getGraphic());
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void appendLogLine(String line) {
        Label label = new Label(line);
        label.getStyleClass().add("afc-log-line");
        label.setWrapText(true);
        logContainer.getChildren().add(label);
        logContainer.setPadding(new Insets(4));
        Platform.runLater(() -> logScroll.setVvalue(1.0));
    }

    // ---------------------------------------------------------------
    // Save / Load / Restart
    // ---------------------------------------------------------------

    @FXML
    private void onSaveClicked() {
        try {
            this.saveManager.save(this.gameManager);
            this.gameManager.logEvent("Partita salvata.");
        } catch (Exception ex) {
            showError("Impossibile salvare: " + ex.getMessage());
        }
    }

    @FXML
    private void onLoadClicked() {
        try {
            boolean loaded = this.saveManager.load(this.gameManager, this.itemRegistry);
            if (loaded) {
                this.gameManager.logEvent("Partita caricata.");
                this.studentStatsController.refresh();
                refreshView();
            } else {
                showError("Nessun salvataggio trovato.");
            }
        } catch (Exception ex) {
            showError("Impossibile caricare: " + ex.getMessage());
        }
    }

    @FXML
    private void onRestartClicked() {
        if (this.onRestart != null) {
            this.onRestart.run();
        }
    }

    @FXML
    private void onRulesClicked() {
        VBox content = new VBox(6);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("afc-panel");

        addRulesTitle(content, "COME FUNZIONA IL GIOCO");
        addRulesParagraph(content,
                "Sei uno studente fuori corso da 3 anni. Devi laurearti prima che finiscano "
                        + "il morale, il budget, o la pazienza. Il gioco procede a settimane: ogni "
                        + "settimana scegli un'azione, e ogni " + GameManager.EXAM_SESSION_INTERVAL_WEEKS
                        + " settimane si apre una sessione d'esame in cui puoi tentare le materie.");

        addRulesTitle(content, "MORALE (0 - " + it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent.MAX_MORALE + ")");
        addRulesParagraph(content,
                "Rappresenta la tua salute mentale. Studiare e affrontare esami la consuma, "
                        + "uscire con gli amici la ripristina. Se il morale arriva a 0, abbandoni "
                        + "gli studi: e' un finale di sconfitta (Abbandono).");

        addRulesTitle(content, "BUDGET (0 - " + it.unicam.cs.mpgc.afc250713.model.student.BudgetSystem.MAX_BUDGET + ")");
        addRulesParagraph(content,
                "Sono i tuoi soldi: quelli di famiglia piu' quelli guadagnati con i lavoretti. "
                        + "Ogni azione settimanale puo' farlo salire o scendere. Se il budget arriva "
                        + "a 0, sei in bancarotta e il morale crolla immediatamente a 0.");

        addRulesTitle(content, "STANCHEZZA (0 - " + it.unicam.cs.mpgc.afc250713.model.student.FatigueSystem.MAX_FATIGUE + ")");
        addRulesParagraph(content,
                "Aumenta di " + it.unicam.cs.mpgc.afc250713.model.student.FatigueSystem.EXAM_SESSION_FATIGUE
                        + " punti ogni volta che affronti (o abbandoni) un esame. Se raggiunge il "
                        + "massimo vai in burnout: il morale crolla a 0. Usa un Pasto alla Mensa per "
                        + "azzerarla.");

        addRulesTitle(content, "CFU E MEDIA");
        addRulesParagraph(content,
                "I CFU misurano quanto manca alla laurea: l'obiettivo e' raggiungerne "
                        + it.unicam.cs.mpgc.afc250713.model.student.AcademicProgress.CFU_TARGET_LAUREA
                        + ". La media dei voti (18-30) determina il finale: se ti laurei con una "
                        + "media di almeno " + GameManager.LODE_MEDIA_THRESHOLD
                        + " ottieni la Laurea con Lode, altrimenti Il Pezzo di Carta.");

        addRulesTitle(content, "AZIONI SETTIMANALI (dal Campus)");
        addRulesParagraph(content,
                "- Studia: -" + StudyAction.MORALE_COST + " morale, -" + StudyAction.BUDGET_COST
                        + " budget, ma rinforza i tuoi appunti (bonus in esame).\n"
                        + "- Lavoretto part-time: +" + PartTimeJobAction.BUDGET_GAIN + " budget, -"
                        + PartTimeJobAction.MORALE_COST + " morale.\n"
                        + "- Esci con gli amici: +" + SocialOutingAction.MORALE_GAIN + " morale, -"
                        + SocialOutingAction.BUDGET_COST + " budget.");

        addRulesTitle(content, "SESSIONI D'ESAME");
        addRulesParagraph(content,
                "Ogni " + GameManager.EXAM_SESSION_INTERVAL_WEEKS + " settimane si apre una sessione: "
                        + "solo in questi turni puoi sostenere un esame. Ogni materia puo' essere "
                        + "superata una sola volta. Durante l'esame scegli una tattica:");
        addRulesParagraph(content,
                "- Ripasso dell'ultimo minuto (studio matto): " + (int) (HardStudyStrategy.SUCCESS_PROBABILITY * 100)
                        + "% di successo, -" + HardStudyStrategy.MORALE_COST + " morale, nessun rischio speciale.\n"
                        + "- Domanda a piacere (studio pigro): buone probabilita' solo se la tua "
                        + "media supera " + LazyStudyStrategy.MIN_MEDIA_REQUIRED + ", costa solo -"
                        + LazyStudyStrategy.MORALE_COST + " morale, ma senza basi solide il fallimento "
                        + "e' quasi certo.\n"
                        + "- Copiare (dilemma etico): 100% di successo e 0 costo in morale, ma il "
                        + (int) (CheatStrategy.CAUGHT_PROBABILITY * 100) + "% delle volte vieni beccato: "
                        + "l'esame viene annullato e il morale crolla a 0. La scelta resta comunque "
                        + "registrata nel tuo storico.\n"
                        + "Un appunto in piu': se hai gli Appunti equipaggiati, le tattiche di studio "
                        + "hanno qualche probabilita' in piu' di successo.");

        addRulesTitle(content, "ZAINO E OGGETTI");
        addRulesParagraph(content,
                "- Lattina di energy drink: ripristina tutto il morale mancante.\n"
                        + "- Pasto alla mensa: azzera la stanchezza accumulata.\n"
                        + "- Appunti di un fuoricorso piu' bravo: aumentano le probabilita' di "
                        + "successo nelle tattiche di studio per un numero limitato di esami.\n"
                        + "- Gruppo di studio: assorbe parte dello stress del prossimo esame, "
                        + "proteggendo il morale.\n"
                        + "Gli oggetti si ottengono superando esami e si usano dal pannello a "
                        + "sinistra con il pulsante Usa.");

        addRulesTitle(content, "CAMBIO FACOLTA'");
        addRulesParagraph(content,
                "Se il morale scende a " + GameManager.CAMBIO_FACOLTA_MORALE_THRESHOLD
                        + " o meno ma il budget e' ancora superiore a "
                        + GameManager.CAMBIO_FACOLTA_BUDGET_THRESHOLD
                        + ", puoi scegliere volontariamente di cambiare facolta' dal Campus: "
                        + "non e' una sconfitta, e' un finale alternativo.");

        addRulesTitle(content, "I QUATTRO FINALI");
        addRulesParagraph(content,
                "- Laurea con Lode: CFU completati con media alta.\n"
                        + "- Il Pezzo di Carta: CFU completati, ma esausto e con media bassa.\n"
                        + "- Abbandono: il morale (o il budget) e' arrivato a 0.\n"
                        + "- Cambio Facolta': scelta volontaria a morale basso ma budget sano.");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("afc-log-scroll");

        StackPane wrapper = new StackPane(scrollPane);
        wrapper.getStyleClass().add("afc-content-area");
        wrapper.setPadding(new Insets(10));

        Scene scene = new Scene(wrapper, 640, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        Stage rulesStage = new Stage();
        rulesStage.setTitle("Regole del gioco");
        rulesStage.setScene(scene);
        rulesStage.show();
    }

    private void addRulesTitle(VBox content, String text) {
        Label label = new Label(text);
        label.getStyleClass().add("afc-rules-heading");
        content.getChildren().add(label);
    }

    private void addRulesParagraph(VBox content, String text) {
        Label label = new Label(text);
        label.getStyleClass().add("afc-rules-text");
        label.setWrapText(true);
        label.setMaxWidth(580);
        content.getChildren().add(label);
    }
}
