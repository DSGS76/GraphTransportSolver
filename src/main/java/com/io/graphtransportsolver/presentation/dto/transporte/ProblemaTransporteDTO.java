package com.io.graphtransportsolver.presentation.dto.transporte;

import com.io.graphtransportsolver.models.transporte.enums.MetodoSolucionInicial;

/**
 * DTO para recibir un problema de transporte desde el frontend (Request).
 * Solo transporta datos. Las validaciones se realizan en el controlador o en el modelo de dominio.
 *
 * @param costos            matriz de costos unitarios de transporte
 * @param ofertas           array de ofertas (capacidades) de los orígenes
 * @param demandas          array de demandas (requerimientos) de los destinos
 * @param nombresOrigenes   nombres opcionales de los orígenes
 * @param nombresDestinos   nombres opcionales de los destinos
 * @param metodoInicial     método a utilizar para encontrar la solución inicial
 */
public record ProblemaTransporteDTO(
        double[][] costos,
        double[] ofertas,
        double[] demandas,
        String[] nombresOrigenes,
        String[] nombresDestinos,
        MetodoSolucionInicial metodoInicial
) {
}

