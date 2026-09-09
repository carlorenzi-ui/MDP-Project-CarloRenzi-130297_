package it.unicam.cs.mpgc.afc250713.view;

import it.unicam.cs.mpgc.afc250713.controller.core.GameManager;
import it.unicam.cs.mpgc.afc250713.model.challenge.ChallengeStats;
import it.unicam.cs.mpgc.afc250713.model.exam.ExamFactory;
import it.unicam.cs.mpgc.afc250713.model.item.ItemRegistry;
import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;
import it.unicam.cs.mpgc.afc250713.model.student.Fuoricorso;
import it.unicam.cs.mpgc.afc250713.model.subject.Subject;
import it.unicam.cs.mpgc.afc250713.model.subject.SubjectFactory;
import it.unicam.cs.mpgc.afc250713.persistence.FileStorageService;
import it.unicam.cs.mpgc.afc250713.persistence.SaveManager;
import it.unicam.cs.mpgc.afc250713.utils.ChallengeStatsService;
import it.unicam.cs.mpgc.afc250713.utils.SubjectDTO;
import it.unicam.cs.mpgc.afc250713.utils.SubjectLoader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Main JavaFX Application class. Handles stage setup, FXML loading, and
 * core dependency wiring ("L'Ultimo Appello").
 */
public class JavaFXApp extends Application {

    private static final int STARTING_BUDGET = 100;

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        restartGame();
    }

    private void restartGame() {
        ChallengeStatsService statsService = loadStatsService();
        if (statsService == null) {
            return;
        }

        ItemRegistry itemRegistry = ItemRegistry.createDefault();
        AbstractStudent student = createStudent(statsService, itemRegistry);
        Map<String, Subject> worldMap = loadWorldMap(itemRegistry);
        ExamFactory examFactory = new ExamFactory(statsService);
        GameManager gameManager = new GameManager(student, worldMap, examFactory);

        buildAndShowScene(gameManager, itemRegistry);
    }

    private ChallengeStatsService loadStatsService() {
        try (Reader statsReader = new InputStreamReader(getClass().getResourceAsStream("/stats.json"))) {
            return new ChallengeStatsService(statsReader);
        } catch (Exception e) {
            System.err.println("Impossibile caricare le statistiche: " + e.getMessage());
            return null;
        }
    }

    private AbstractStudent createStudent(ChallengeStatsService statsService, ItemRegistry itemRegistry) {
        ChallengeStats studentStats = statsService.getStatsFor("studente");
        AbstractStudent student = new Fuoricorso(studentStats, STARTING_BUDGET);

        student.addItem(itemRegistry.get("REDBULL"), 2);
        student.addItem(itemRegistry.get("PASTO_CALDO"), 2);

        return student;
    }

    private Map<String, Subject> loadWorldMap(ItemRegistry itemRegistry) {
        Map<String, Subject> worldMap = new HashMap<>();
        try (Reader subjectsReader = new InputStreamReader(getClass().getResourceAsStream("/subjects.json"))) {
            Map<String, SubjectDTO> dtos = SubjectLoader.parseSubjects(subjectsReader);
            dtos.forEach((id, dto) -> worldMap.put(id, SubjectFactory.createSubject(id, dto, itemRegistry)));
        } catch (Exception e) {
            System.err.println("Impossibile caricare le materie: " + e.getMessage());
        }
        return worldMap;
    }

    private void buildAndShowScene(GameManager gameManager, ItemRegistry itemRegistry) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
            BorderPane root = loader.load();
            GameController controller = loader.getController();

            String saveDir = System.getProperty("user.home") + File.separator + ".annofuoricorso";
            new File(saveDir).mkdirs();
            String saveFile = saveDir + File.separator + "savegame.json";
            SaveManager saveManager = new SaveManager(new FileStorageService(saveFile));

            controller.setSaveManager(saveManager);
            controller.setGameManager(gameManager, itemRegistry, this::restartGame);

            Scene scene = new Scene(root, 1100, 760);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            primaryStage.setTitle("Anno Fuori Corso - L'Ultimo Appello");
            primaryStage.setScene(scene);
            primaryStage.show();

            gameManager.logEvent("Benvenuto! Un altro anno fuori corso comincia. Scegli come affrontare la settimana.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore critico all'avvio dell'applicazione: " + e.getMessage());
        }
    }
}
