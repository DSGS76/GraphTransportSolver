package com.io.graphtransportsolver.models.transporte;

import com.io.graphtransportsolver.models.transporte.enums.TipoBalance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * Representa un problema completo de transporte.
 * Incluye orígenes, destinos, costos y tipo de balance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemaTransporte {

    /**
     * Array de ofertas (capacidades) de los orígenes.
     */
    private double[] ofertas;

    /**
     * Array de demandas (requerimientos) de los destinos.
     */
    private double[] demandas;

    /**
     * Matriz de costos unitarios de transporte.
     * costos[i][j] = costo de transportar de origen i a destino j.
     */
    private double[][] costos;

    /**
     * Nombres de los orígenes (opcional).
     */
    @Builder.Default
    private String[] nombresOrigenes = null;

    /**
     * Nombres de los destinos (opcional).
     */
    @Builder.Default
    private String[] nombresDestinos = null;

    /**
     * Indica si se agregó un origen/destino ficticio para balancear.
     */
    @Builder.Default
    private boolean tieneFicticio = false;

    /**
     * Calcula la oferta total disponible.
     *
     * @return suma de todas las ofertas
     */
    public double getOfertaTotal() {
        return Arrays.stream(ofertas).sum();
    }

    /**
     * Calcula la demanda total requerida.
     *
     * @return suma de todas las demandas
     */
    public double getDemandaTotal() {
        return Arrays.stream(demandas).sum();
    }

    /**
     * Verifica si el problema está balanceado.
     *
     * @return true si la suma de ofertas igual suma de demandas
     */
    public boolean esBalanceado() {
        return Math.abs(getOfertaTotal() - getDemandaTotal()) < 1e-6;
    }

    /**
     * Obtiene el costo de una celda específica.
     *
     * @param i índice del origen
     * @param j índice del destino
     * @return costo unitario de transporte
     */
    public double getCosto(int i, int j) {
        return costos[i][j];
    }

    /**
     * Obtiene la oferta de un origen específico.
     *
     * @param i índice del origen
     * @return oferta del origen i
     */
    public double getOferta(int i) {
        return ofertas[i];
    }

    /**
     * Obtiene la demanda de un destino específico.
     *
     * @param j índice del destino
     * @return demanda del destino j
     */
    public double getDemanda(int j) {
        return demandas[j];
    }

    /**
     * Calcula el desbalance del problema.
     *
     * @return diferencia entre oferta total y demanda total
     */
    public double calcularDesbalance() {
        return getOfertaTotal() - getDemandaTotal();
    }

    /**
     * Determina el tipo de desbalance.
     *
     * @return "BALANCEADO", "EXCESO_OFERTA" o "EXCESO_DEMANDA"
     */
    public TipoBalance getTipoBalance() {
        double desbalance = calcularDesbalance();
        if (Math.abs(desbalance) < 1e-6) {
            return TipoBalance.BALANCEADO;
        }
        return desbalance > 0 ? TipoBalance.EXCESO_OFERTA : TipoBalance.EXCESO_DEMANDA;
    }
}
