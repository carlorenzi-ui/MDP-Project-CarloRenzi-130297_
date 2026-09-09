package it.unicam.cs.mpgc.afc250713.persistence;

import java.util.HashMap;
import java.util.Map;

import it.unicam.cs.mpgc.afc250713.controller.core.GameManager;
import it.unicam.cs.mpgc.afc250713.model.item.Item;
import it.unicam.cs.mpgc.afc250713.model.item.ItemRegistry;
import it.unicam.cs.mpgc.afc250713.model.student.AbstractStudent;

/**
 * Converts between the live game state and its flat DTO representation.
 */
public final class StudentMapper {

    private StudentMapper() {
        // Prevents instantiation
    }

    /**
     * Snapshots the current game state into a DTO ready for serialization.
     */
    public static StudentSaveDTO toDTO(final GameManager gameManager) {
        AbstractStudent student = gameManager.getStudent();
        StudentSaveDTO dto = new StudentSaveDTO();
        dto.morale = student.getMorale();
        dto.fatigue = student.getFatigue();
        dto.budget = student.getBudget();
        dto.cfu = student.getCfu();
        dto.annoFuoriCorso = student.getAnnoFuoriCorso();
        dto.media = student.getMedia();
        dto.esamiSuperati = student.getEsamiSuperati();
        dto.weekNumber = gameManager.getWeekNumber();
        dto.appuntiDurability = student.getAppuntiDurability();
        dto.gruppoStudioShield = student.getGruppoStudioShield();
        dto.ethicalChoiceHistory = new java.util.ArrayList<>(gameManager.getEthicalChoiceHistory());
        dto.completedSubjects = new java.util.ArrayList<>(gameManager.getCompletedSubjects());

        Map<String, Integer> zainoDto = new HashMap<>();
        student.getZainoContents().forEach((item, amount) -> zainoDto.put(item.getId(), amount));
        dto.zaino = zainoDto;

        return dto;
    }

    /**
     * Applies a previously saved DTO onto the current game state.
     * Assumes the game is being resumed from campus.
     */
    public static void applyToGame(final GameManager gameManager, final StudentSaveDTO dto,
                                    final ItemRegistry itemRegistry) {
        AbstractStudent student = gameManager.getStudent();
        student.setMorale(dto.morale);
        student.setFatigue(dto.fatigue);
        student.setBudget(dto.budget);
        student.setCfu(dto.cfu);
        student.setAnnoFuoriCorso(dto.annoFuoriCorso);
        student.setMedia(dto.media);
        student.setEsamiSuperati(dto.esamiSuperati);
        student.setAppuntiDurability(dto.appuntiDurability);
        student.setGruppoStudioShield(dto.gruppoStudioShield);
        gameManager.setWeekNumber(dto.weekNumber);

        student.clearZaino();
        if (dto.zaino != null) {
            for (Map.Entry<String, Integer> entry : dto.zaino.entrySet()) {
                if (entry.getValue() > 0) {
                    Item item = itemRegistry.get(entry.getKey());
                    student.setItemForce(item, entry.getValue());
                }
            }
        }

        if (dto.ethicalChoiceHistory != null) {
            dto.ethicalChoiceHistory.forEach(gameManager::recordEthicalChoice);
        }
        gameManager.restoreCompletedSubjects(dto.completedSubjects);
    }
}
