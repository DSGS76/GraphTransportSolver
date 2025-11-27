package com.io.graphtransportsolver.algoritmos.transporte;

import com.io.graphtransportsolver.models.transporte.ProblemaTransporte;
import com.io.graphtransportsolver.models.transporte.SolucionTransporte;

/**
 * Interfaz Strategy para los diferentes métodos de solución inicial
 * del problema de transporte.
 */
public interface SolucionInicialStrategy {

    /**
     * Encuentra una solución básica factible inicial para el problema de transporte.
     *
     * @param problema el problema de transporte a resolver
     * @return la solución inicial encontrada
     */
    SolucionTransporte encontrarSolucionInicial(ProblemaTransporte problema);
}
