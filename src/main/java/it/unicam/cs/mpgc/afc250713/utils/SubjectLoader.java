package it.unicam.cs.mpgc.afc250713.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Utility for dynamically parsing university subjects from JSON streams.
 * Isolates parsing logic from the Subject domain class.
 */
public final class SubjectLoader {

    private SubjectLoader() {
        // Prevents instantiation
    }

    /**
     * Parses the JSON stream and returns a map of SubjectDTOs.
     *
     * @param reader The Reader providing the JSON data.
     * @return A map of all parsed subjects, indexed by ID.
     */
    public static Map<String, SubjectDTO> parseSubjects(Reader reader) {
        try {
            final Gson gson = new Gson();
            final Type type = new TypeToken<Map<String, SubjectDTO>>() {
            }.getType();
            Map<String, SubjectDTO> parsedMap = gson.fromJson(reader, type);

            if (parsedMap == null || parsedMap.isEmpty()) {
                throw new IllegalStateException("Subjects JSON stream is empty or malformed.");
            }

            return parsedMap;
        } catch (com.google.gson.JsonSyntaxException e) {
            throw new IllegalStateException("Critical error parsing subjects JSON", e);
        }
    }
}
