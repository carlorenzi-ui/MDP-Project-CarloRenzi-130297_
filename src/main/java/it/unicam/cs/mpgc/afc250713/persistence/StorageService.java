package it.unicam.cs.mpgc.afc250713.persistence;

/**
 * Abstraction over the physical storage medium for save games, so the game
 * doesn't know (or care) whether it is saving to a local file, a database,
 * or the cloud (Dependency Inversion Principle).
 */
public interface StorageService {
    void write(String content) throws Exception;

    String read() throws Exception;

    boolean exists();
}
