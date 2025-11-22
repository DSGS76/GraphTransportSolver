package com.io.graphtransportsolver.models.grafico.enums;

/**
 * Enum que representa el tipo de solución obtenida en el método gráfico.
 */
public enum TipoSolucion {
    /**
     * Existe una única solución óptima.
     */
    UNICA,

    /**
     * Existen múltiples soluciones óptimas (infinitas).
     * La función objetivo es paralela a una restricción activa.
     */
    MULTIPLE,

    /**
     * No existe región factible.
     * Las restricciones son contradictorias.
     */
    NO_FACTIBLE,

    /**
     * La región factible no está acotada.
     * La función objetivo puede mejorar indefinidamente.
     */
    NO_ACOTADO
}
