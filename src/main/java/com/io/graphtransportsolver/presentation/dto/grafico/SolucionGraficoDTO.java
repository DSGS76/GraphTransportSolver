package com.io.graphtransportsolver.presentation.dto.grafico;

import com.io.graphtransportsolver.models.grafico.enums.TipoSolucion;

import java.util.List;

/**
 * DTO para enviar la solución de un problema gráfico al frontend (Response).
 * Contiene el punto óptimo, los vértices, la región factible y las restricciones para graficar.
 *
 * @param puntoOptimo    punto óptimo encontrado (puede ser null si múltiples soluciones o sin solución)
 * @param vertices       lista de todos los vértices de la región factible
 * @param regionFactible puntos que forman el polígono de la región factible (ordenados)
 * @param restricciones  lista de restricciones para graficar las líneas
 * @param tipoSolucion   tipo de solución encontrada
 */
public record SolucionGraficoDTO(
        PuntoDTO puntoOptimo,
        List<PuntoDTO> vertices,
        List<PuntoDTO> regionFactible,
        List<RestriccionDTO> restricciones,
        TipoSolucion tipoSolucion
) {
    /**
     * Record anidado para representar un punto en el plano cartesiano.
     *
     * @param x1         coordenada x₁
     * @param x2         coordenada x₂
     * @param valorZ     valor de la función objetivo evaluada en este punto
     * @param esFactible indica si el punto pertenece a la región factible
     */
    public record PuntoDTO(
            double x1,
            double x2,
            double valorZ,
            boolean esFactible
    ) {
    }

    /**
     * Record anidado para representar una restricción para graficar.
     *
     * @param coeficienteX1 coeficiente de x₁
     * @param coeficienteX2 coeficiente de x₂
     * @param ladoDerecho   término independiente
     * @param tipo          tipo de desigualdad (para mostrar en la leyenda)
     */
    public record RestriccionDTO(
            double coeficienteX1,
            double coeficienteX2,
            double ladoDerecho,
            String tipo
    ) {
    }
}

