package com.io.graphtransportsolver.presentation.dto.transporte;

import com.io.graphtransportsolver.models.transporte.enums.MetodoSolucionInicial;
import com.io.graphtransportsolver.models.transporte.enums.TipoBalance;

/**
 * DTO para enviar la solución de un problema de transporte al frontend (Response).
 * Contiene la información de la solución inicial encontrada con uno de los tres métodos.
 *
 * @param asignaciones matriz de asignaciones resultante [orígenes][destinos]
 * @param costoTotal         costo total de transporte de la solución
 * @param metodoUtilizado    método usado para encontrar la solución
 * @param seBalanceo         indica si se aplicó balanceo al problema
 * @param tipoBalance        tipo de balance del problema (BALANCEADO, EXCESO_OFERTA, EXCESO_DEMANDA)
 * @param mensaje            mensaje descriptivo del resultado
 * @param nombresOrigenes    nombres de los orígenes (si se proporcionaron)
 * @param nombresDestinos    nombres de los destinos (si se proporcionaron)
 */
public record SolucionTransporteDTO(
        double[][] asignaciones,
        double costoTotal,
        MetodoSolucionInicial metodoUtilizado,
        boolean seBalanceo,
        TipoBalance tipoBalance,
        String mensaje,
        String[] nombresOrigenes,
        String[] nombresDestinos
) {
    /**
     * Obtiene el número de orígenes.
     *
     * @return cantidad de orígenes
     */
    public int numOrigenes() {
        return asignaciones != null ? asignaciones.length : 0;
    }

    /**
     * Obtiene el número de destinos.
     *
     * @return cantidad de destinos
     */
    public int numDestinos() {
        return asignaciones != null && asignaciones.length > 0
                ? asignaciones[0].length : 0;
    }

    /**
     * Obtiene el número de celdas con asignación mayor a cero (básicas).
     *
     * @return cantidad de celdas básicas
     */
    public int celdasBasicas() {
        if (asignaciones == null) return 0;
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
     * Verifica si la solución tiene el número correcto de celdas básicas (m + n - 1).
     *
     * @return true si tiene m + n - 1 celdas básicas
     */
    public boolean tieneCeldasBasicasCorrectas() {
        int m = numOrigenes();
        int n = numDestinos();
        int esperadas = m + n - 1;
        return celdasBasicas() == esperadas;
    }
}

