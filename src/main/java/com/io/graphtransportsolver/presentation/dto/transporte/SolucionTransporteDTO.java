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
 * @param nombresOrigenes    nombres de los orígenes (si se proporcionaron)
 * @param nombresDestinos    nombres de los destinos (si se proporcionaron)
 */
public record SolucionTransporteDTO(
        double[][] asignaciones,
        double costoTotal,
        MetodoSolucionInicial metodoUtilizado,
        boolean seBalanceo,
        TipoBalance tipoBalance,
        String[] nombresOrigenes,
        String[] nombresDestinos
) {
}

