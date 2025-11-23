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
    /**
     * Obtiene el número de orígenes.
     *
     * @return cantidad de orígenes
     */
    public int numOrigenes() {
        return ofertas != null ? ofertas.length : 0;
    }

    /**
     * Obtiene el número de destinos.
     *
     * @return cantidad de destinos
     */
    public int numDestinos() {
        return demandas != null ? demandas.length : 0;
    }

    /**
     * Verifica si el problema está balanceado.
     *
     * @return true si la suma de ofertas es igual a la suma de demandas
     */
    public boolean estaBalanceado() {
        return Math.abs(sumaOfertas() - sumaDemandas()) < 1e-6;
    }

    /**
     * Calcula la oferta total.
     *
     * @return suma de todas las ofertas
     */
    public double sumaOfertas() {
        if (ofertas == null) return 0.0;
        double suma = 0;
        for (double oferta : ofertas) suma += oferta;
        return suma;
    }

    /**
     * Calcula la demanda total.
     *
     * @return suma de todas las demandas
     */
    public double sumaDemandas() {
        if (demandas == null) return 0.0;
        double suma = 0;
        for (double demanda : demandas) suma += demanda;
        return suma;
    }
}

