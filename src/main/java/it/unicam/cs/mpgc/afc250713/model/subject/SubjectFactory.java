package it.unicam.cs.mpgc.afc250713.model.subject;

import it.unicam.cs.mpgc.afc250713.model.item.Item;
import it.unicam.cs.mpgc.afc250713.model.item.ItemRegistry;
import it.unicam.cs.mpgc.afc250713.utils.SubjectDTO;

/**
 * Assembler for Subject domain entities from parsed DTOs.
 */
public final class SubjectFactory {

    private SubjectFactory() {
        // Prevents instantiation
    }

    /**
     * Builds a concrete subject entity from transfer data.
     *
     * @param subjectId    Unique subject identifier.
     * @param dto          Parsed data transfer object.
     * @param itemRegistry The registry to resolve item IDs for rewards.
     * @return Fully populated subject instance.
     */
    public static Subject createSubject(String subjectId, SubjectDTO dto, ItemRegistry itemRegistry) {
        if (dto == null) {
            throw new IllegalArgumentException("SubjectDTO cannot be null for ID: " + subjectId);
        }

        final Subject.Difficulty difficulty = dto.difficulty != null ?
                Subject.Difficulty.valueOf(dto.difficulty.toUpperCase()) :
                Subject.Difficulty.FACILE;

        final Subject subject = new Subject(subjectId, dto.name, dto.description, difficulty, dto.cfuReward);

        if (dto.rewards != null) {
            dto.rewards.forEach((typeString, amount) -> {
                Item item = itemRegistry.get(typeString);
                subject.addReward(new ItemReward(item, amount));
            });
        }

        if (dto.examSpawns != null) {
            dto.examSpawns.forEach(subject::addExamSpawn);
        }

        return subject;
    }
}
