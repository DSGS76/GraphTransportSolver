package com.io.graphtransportsolver.presentation.dto.transporte;

/**
 * DTO para comparar los resultados de los tres métodos de solución inicial (Response).
 * Permite al frontend mostrar una comparación visual entre los métodos.
 *
 * @param esquinaNoroeste solución usando el método de Esquina Noroeste
 * @param costoMinimo     solución usando el método de Costo Mínimo
 * @param vogel           solución usando el método de Vogel (VAM)
 * @param mejorMetodo     nombre del método que obtuvo el mejor costo inicial
 * @param recomendacion   recomendación sobre qué método usar
 */
public record ComparacionMetodosDTO(
        SolucionTransporteDTO esquinaNoroeste,
        SolucionTransporteDTO costoMinimo,
        SolucionTransporteDTO vogel,
        String mejorMetodo,
        String recomendacion
) {
    /**
     * Obtiene la solución con el mejor costo.
     *
     * @return la solución del método ganador
     */
    public SolucionTransporteDTO getMejorSolucion() {
        return switch (mejorMetodo) {
            case "ESQUINA_NOROESTE" -> esquinaNoroeste;
            case "COSTO_MINIMO" -> costoMinimo;
            case "VOGEL" -> vogel;
            default -> vogel; // Por defecto Vogel suele ser el mejor
        };
    }

    /**
     * Calcula la diferencia entre el costo más alto y el más bajo.
     *
     * @return diferencia absoluta entre costos máximo y mínimo
     */
    public double getDiferenciaCostoMaxMin() {
        double max = Math.max(
                Math.max(esquinaNoroeste.costoTotal(), costoMinimo.costoTotal()),
                vogel.costoTotal()
        );
        double min = Math.min(
                Math.min(esquinaNoroeste.costoTotal(), costoMinimo.costoTotal()),
                vogel.costoTotal()
        );
        return max - min;
    }

    /**
     * Calcula el porcentaje de mejora del mejor método respecto al peor.
     *
     * @return porcentaje de mejora (0-100)
     */
    public double getPorcentajeMejora() {
        double max = Math.max(
                Math.max(esquinaNoroeste.costoTotal(), costoMinimo.costoTotal()),
                vogel.costoTotal()
        );
        double min = Math.min(
                Math.min(esquinaNoroeste.costoTotal(), costoMinimo.costoTotal()),
                vogel.costoTotal()
        );
        if (max == 0) return 0.0;
        return ((max - min) / max) * 100.0;
    }

    /**
     * Obtiene el costo más bajo entre los tres métodos.
     *
     * @return costo mínimo encontrado
     */
    public double getCostoMinimo() {
        return Math.min(
                Math.min(esquinaNoroeste.costoTotal(), costoMinimo.costoTotal()),
                vogel.costoTotal()
        );
    }

    /**
     * Obtiene el costo más alto entre los tres métodos.
     *
     * @return costo máximo encontrado
     */
    public double getCostoMaximo() {
        return Math.max(
                Math.max(esquinaNoroeste.costoTotal(), costoMinimo.costoTotal()),
                vogel.costoTotal()
        );
    }
}