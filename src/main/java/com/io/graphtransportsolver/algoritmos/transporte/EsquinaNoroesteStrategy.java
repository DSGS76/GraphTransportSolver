package com.io.graphtransportsolver.algoritmos.transporte;

import com.io.graphtransportsolver.models.transporte.ProblemaTransporte;
import com.io.graphtransportsolver.models.transporte.SolucionTransporte;
import com.io.graphtransportsolver.models.transporte.enums.MetodoSolucionInicial;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Implementación del método de Esquina Noroeste para encontrar
 * una solución básica factible inicial del problema de transporte.
 *
 * Este método comienza en la esquina superior izquierda (noroeste)
 * y va asignando la mayor cantidad posible a cada celda, moviéndose
 * hacia la derecha o hacia abajo según se agoten ofertas o demandas.
 */
@Component
public class EsquinaNoroesteStrategy implements SolucionInicialStrategy {

    @Override
    public SolucionTransporte encontrarSolucionInicial(ProblemaTransporte problema) {

        int m = problema.getOfertas().length;  // número de orígenes
        int n = problema.getDemandas().length; // número de destinos

        // Crear matriz de asignaciones
        double[][] asignaciones = new double[m][n];

        // Copias de ofertas y demandas para no modificar el original
        double[] ofertasDisponibles = Arrays.copyOf(problema.getOfertas(), m);
        double[] demandasRestantes = Arrays.copyOf(problema.getDemandas(), n);

        // Índices para recorrer la matriz
        int i = 0; // índice de origen actual
        int j = 0; // índice de destino actual

        // Algoritmo de Esquina Noroeste
        while (i < m && j < n) {
            // Asignar el mínimo entre oferta disponible y demanda restante
            double asignacion = Math.min(ofertasDisponibles[i], demandasRestantes[j]);
            asignaciones[i][j] = asignacion;

            // Actualizar ofertas y demandas
            ofertasDisponibles[i] -= asignacion;
            demandasRestantes[j] -= asignacion;

            // Decidir hacia dónde moverse
            // Si se agotó la oferta del origen actual, moverse hacia abajo
            if (Math.abs(ofertasDisponibles[i]) < 1e-6) {
                i++;
            }
            // Si se satisfizo la demanda del destino actual, moverse hacia la derecha
            if (Math.abs(demandasRestantes[j]) < 1e-6) {
                j++;
            }
        }

        // Crear y retornar la solución
        SolucionTransporte solucion = SolucionTransporte.builder()
                .asignaciones(asignaciones)
                .metodoUtilizado(MetodoSolucionInicial.ESQUINA_NOROESTE)
                .build();

        // Calcular el costo total
        solucion.calcularCostoTotal(problema.getCostos());

        return solucion;
    }
}
