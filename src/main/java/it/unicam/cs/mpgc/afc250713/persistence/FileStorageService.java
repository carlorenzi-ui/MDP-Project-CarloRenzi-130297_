package it.unicam.cs.mpgc.afc250713.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Concrete storage strategy: persists the save game to a local JSON file.
 */
public class FileStorageService implements StorageService {
    private final Path filePath;

    public FileStorageService(String fileName) {
        this.filePath = Path.of(fileName);
    }

    @Override
    public void write(String content) throws IOException {
        Files.writeString(this.filePath, content, StandardCharsets.UTF_8);
    }

    @Override
    public String read() throws IOException {
        return Files.readString(this.filePath, StandardCharsets.UTF_8);
    }

    @Override
    public boolean exists() {
        return Files.exists(this.filePath);
    }
}
