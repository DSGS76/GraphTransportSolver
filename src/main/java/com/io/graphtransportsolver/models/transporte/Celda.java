package com.io.graphtransportsolver.models.transporte;

import com.io.graphtransportsolver.models.transporte.enums.EstadoCelda;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una celda individual en la matriz de transporte.
 * Cada celda contiene información sobre el costo, la asignación y su estado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Celda {

    /**
     * Fila de la celda (índice del origen).
     */
    private int fila;

    /**
     * Columna de la celda (índice del destino).
     */
    private int columna;

    /**
     * Costo unitario de transporte desde el origen i al destino j.
     */
    private double costo;

    /**
     * Cantidad asignada en esta celda.
     */
    @Builder.Default
    private double asignacion = 0.0;

    /**
     * Verifica si la celda es básica (tiene asignación).
     *
     * @return true si la celda es básica
     */
    public boolean esBasica() {
        return Math.abs(asignacion) > 1e-6;
    }


    /**
     * Calcula el costo total de esta celda.
     *
     * @return costo * asignación
     */
    public double calcularCostoTotal() {
        return costo * asignacion;
    }

    /**
     * Obtiene el estado de la celda.
     *
     * @return BASICA si tiene asignación, NO_BASICA si no
     */
    public EstadoCelda getEstado() {
        return esBasica() ? EstadoCelda.BASICA : EstadoCelda.NO_BASICA;
    }
}
