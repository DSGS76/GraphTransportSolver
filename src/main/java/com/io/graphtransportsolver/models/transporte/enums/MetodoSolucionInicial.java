package com.io.graphtransportsolver.models.transporte.enums;

/**
 * Enum que representa los métodos para encontrar la solución inicial
 * del problema de transporte.
 */
public enum MetodoSolucionInicial {
    /**
     * Método de la Esquina Noroeste.
     * Comienza en la esquina superior izquierda y asigna valores secuencialmente.
     */
    ESQUINA_NOROESTE,

    /**
     * Método del Costo Mínimo.
     * Asigna valores comenzando por las celdas de menor costo.
     */
    COSTO_MINIMO,

    /**
     * Método de Vogel (Aproximación de Vogel - VAM).
     * Considera las penalizaciones para optimizar la solución inicial.
     */
    VOGEL
}
