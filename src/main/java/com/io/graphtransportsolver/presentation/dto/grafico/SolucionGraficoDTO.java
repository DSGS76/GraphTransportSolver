package com.io.graphtransportsolver.presentation.dto.grafico;

import com.io.graphtransportsolver.models.grafico.enums.TipoSolucion;

import java.util.List;

/**
 * DTO para enviar la solución de un problema gráfico al frontend.
 * Contiene el punto óptimo, los vértices y la información de la región factible.
 *
 * @param puntoOptimo       punto óptimo encontrado (puede ser null si no hay solución)
 * @param vertices          lista de todos los vértices de la región factible
 * @param regionFactible    puntos que forman el polígono de la región factible (ordenados)
 * @param tipoSolucion      tipo de solución encontrada
 * @param mensaje           mensaje descriptivo del resultado
 * @param esFactible        indica si existe región factible
 * @param esAcotado         indica si la solución está acotada
 */
public record SolucionGraficoDTO(
        PuntoDTO puntoOptimo,
        List<PuntoDTO> vertices,
        List<PuntoDTO> regionFactible,
        TipoSolucion tipoSolucion,
        String mensaje,
        boolean esFactible,
        boolean esAcotado
) {
    /**
     * Record anidado para representar un punto en el plano cartesiano.
     * Representa un vértice de la región factible o el punto óptimo.
     *
     * @param x1         coordenada x₁
     * @param x2         coordenada x₂
     * @param valorZ     valor de la función objetivo evaluada en este punto
     * @param esFactible indica si el punto pertenece a la región factible
     * @param esOptimo   indica si este punto es el punto óptimo
     */
    public record PuntoDTO(
            double x1,
            double x2,
            double valorZ,
            boolean esFactible,
            boolean esOptimo
    ) {
        /**
         * Constructor conveniente para crear un punto sin evaluar función objetivo.
         *
         * @param x1 coordenada x₁
         * @param x2 coordenada x₂
         */
        public PuntoDTO(double x1, double x2) {
            this(x1, x2, 0.0, false, false);
        }
    }

    /**
     * Verifica si se encontró una solución óptima.
     *
     * @return true si hay punto óptimo y es factible
     */
    public boolean tieneSolucion() {
        return puntoOptimo != null &&
                (tipoSolucion == TipoSolucion.UNICA || tipoSolucion == TipoSolucion.MULTIPLE);
    }

    /**
     * Obtiene el valor óptimo de la función objetivo.
     *
     * @return valor de Z en el punto óptimo, o 0 si no hay solución
     */
    public double getValorOptimo() {
        return puntoOptimo != null ? puntoOptimo.valorZ() : 0.0;
    }

    /**
     * Obtiene el número de vértices de la región factible.
     *
     * @return cantidad de vértices
     */
    public int getNumeroVertices() {
        return vertices != null ? vertices.size() : 0;
    }


    /**
     * Verifica si la solución es óptima única.
     *
     * @return true si la solución es única
     */
    public boolean esSolucionUnica() {
        return tipoSolucion == TipoSolucion.UNICA;
    }

    /**
     * Verifica si hay múltiples soluciones óptimas.
     *
     * @return true si hay múltiples soluciones
     */
    public boolean tieneMultiplesSoluciones() {
        return tipoSolucion == TipoSolucion.MULTIPLE;
    }
}
