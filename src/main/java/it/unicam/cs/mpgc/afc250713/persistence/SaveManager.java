package it.unicam.cs.mpgc.afc250713.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.unicam.cs.mpgc.afc250713.controller.core.GameManager;
import it.unicam.cs.mpgc.afc250713.model.item.ItemRegistry;

/**
 * Facade for saving and loading the game, decoupled from the concrete
 * storage medium via {@link StorageService}.
 */
public class SaveManager {
    private final StorageService storageService;
    private final Gson gson;

    public SaveManager(StorageService storageService) {
        this.storageService = storageService;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Serializes the current game state and writes it to storage.
     */
    public void save(GameManager gameManager) throws Exception {
        StudentSaveDTO dto = StudentMapper.toDTO(gameManager);
        String json = this.gson.toJson(dto);
        this.storageService.write(json);
    }

    /**
     * Reads and applies a previously saved game state, if one exists.
     *
     * @return true if a save was found and applied, false otherwise.
     */
    public boolean load(GameManager gameManager, ItemRegistry itemRegistry) throws Exception {
        if (!this.storageService.exists()) {
            return false;
        }
        String json = this.storageService.read();
        StudentSaveDTO dto = this.gson.fromJson(json, StudentSaveDTO.class);
        if (dto == null) {
            return false;
        }
        StudentMapper.applyToGame(gameManager, dto, itemRegistry);
        return true;
    }
}
