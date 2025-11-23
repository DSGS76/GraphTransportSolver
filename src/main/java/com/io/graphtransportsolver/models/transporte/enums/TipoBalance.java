package com.io.graphtransportsolver.models.transporte.enums;

/**
 * Enum que representa el tipo de balance de un problema de transporte.
 * Indica la relación entre la oferta total y la demanda total.
 */
public enum TipoBalance {

    /**
     * El problema está balanceado.
     * La suma total de ofertas es igual a la suma total de demandas.
     * Este es el caso ideal donde no se requiere agregar orígenes o destinos ficticios.
     */
    BALANCEADO,

    /**
     * Hay exceso de oferta.
     * La suma total de ofertas es mayor que la suma total de demandas.
     * Se requiere agregar un destino ficticio para balancear el problema.
     */
    EXCESO_OFERTA,

    /**
     * Hay exceso de demanda.
     * La suma total de demandas es mayor que la suma total de ofertas.
     * Se requiere agregar un origen ficticio para balancear el problema.
     */
    EXCESO_DEMANDA
}
