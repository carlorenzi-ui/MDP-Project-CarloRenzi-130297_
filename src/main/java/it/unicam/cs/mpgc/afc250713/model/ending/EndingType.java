package it.unicam.cs.mpgc.afc250713.model.ending;

/**
 * Identifies the possible terminal outcomes of a playthrough, satisfying
 * the "multiple endings" requirement and guaranteeing replayability.
 */
public enum EndingType {
    /** CFU target reached with a high average: the best possible outcome. */
    LAUREA_CON_LODE,
    /** CFU target reached, but exhausted and with a low average. */
    IL_PEZZO_DI_CARTA,
    /** Morale hit zero: the student drops out. */
    ABBANDONO,
    /** Voluntary choice: morale is low, but budget is still healthy enough to restart elsewhere. */
    CAMBIO_FACOLTA
}
