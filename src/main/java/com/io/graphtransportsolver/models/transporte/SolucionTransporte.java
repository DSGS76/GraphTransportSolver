package com.io.graphtransportsolver.models.transporte;

import com.io.graphtransportsolver.models.transporte.enums.MetodoSolucionInicial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa la solución completa de un problema de transporte.
 * Incluye la matriz de asignaciones, el costo total y información adicional.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolucionTransporte {

    /**
     * Matriz de asignaciones resultante.
     * asignaciones[i][j] = cantidad transportada de origen i a destino j.
     */
    private double[][] asignaciones;

    /**
     * Costo total de transporte de la solución.
     */
    private double costoTotal;

    /**
     * Método utilizado para encontrar la solución inicial.
     */
    private MetodoSolucionInicial metodoUtilizado;

    /**
     * Calcula el costo total de la solución.
     *
     * @param costos matriz de costos unitarios
     * @return costo total
     */
    public void calcularCostoTotal(double[][] costos) {
        double total = 0.0;
        for (int i = 0; i < asignaciones.length; i++) {
            for (int j = 0; j < asignaciones[i].length; j++) {
                total += asignaciones[i][j] * costos[i][j];
            }
        }
        this.costoTotal = total;
    }

    /**
     * Obtiene el número de celdas básicas (asignaciones > 0).
     *
     * @return número de celdas básicas
     */
    public int getNumCeldasBasicas() {
        int count = 0;
        for (double[] fila : asignaciones) {
            for (double valor : fila) {
                if (Math.abs(valor) > 1e-6) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Obtiene el número esperado de celdas básicas (m + n - 1).
     *
     * @return número de celdas esperadas
     */
    public int getNumCeldasEsperadas() {
        if (asignaciones == null || asignaciones.length == 0) {
            return 0;
        }
        int m = asignaciones.length;
        int n = asignaciones[0].length;
        return m + n - 1;
    }

    /**
     * Verifica si la solución tiene el número correcto de celdas básicas.
     *
     * @return true si numCeldasBasicas = m + n - 1
     */
    public boolean tieneNumeroCorrectoCeldas() {
        return getNumCeldasBasicas() == getNumCeldasEsperadas();
    }

    /**
     * Obtiene el número de filas de la matriz de asignaciones.
     *
     * @return número de orígenes
     */
    public int getNumOrigenes() {
        return asignaciones != null ? asignaciones.length : 0;
    }

    /**
     * Obtiene el número de columnas de la matriz de asignaciones.
     *
     * @return número de destinos
     */
    public int getNumDestinos() {
        return asignaciones != null && asignaciones.length > 0 ? asignaciones[0].length : 0;
    }
}
