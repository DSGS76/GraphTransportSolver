package com.io.graphtransportsolver.models.transporte.enums;

/**
 * Enum que representa el estado de una celda en el problema de transporte.
 */
public enum EstadoCelda {
    /**
     * Celda básica: participa en la solución actual con asignación mayor a cero.
     */
    BASICA,

    /**
     * Celda no básica: no participa en la solución actual (asignación igual a cero).
     */
    NO_BASICA,
}
