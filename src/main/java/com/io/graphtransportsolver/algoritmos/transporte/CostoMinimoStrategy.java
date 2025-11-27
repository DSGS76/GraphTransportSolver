package com.io.graphtransportsolver.algoritmos.transporte;

import com.io.graphtransportsolver.models.transporte.Celda;
import com.io.graphtransportsolver.models.transporte.ProblemaTransporte;
import com.io.graphtransportsolver.models.transporte.SolucionTransporte;
import com.io.graphtransportsolver.models.transporte.enums.MetodoSolucionInicial;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Implementación del método de Costo Mínimo para encontrar
 * una solución básica factible inicial del problema de transporte.
 *
 * Este método selecciona en cada iteración la celda con el menor costo
 * y le asigna la mayor cantidad posible, repitiendo el proceso hasta
 * satisfacer todas las ofertas y demandas.
 */
@Component
public class CostoMinimoStrategy implements SolucionInicialStrategy {

    @Override
    public SolucionTransporte encontrarSolucionInicial(ProblemaTransporte problema) {

        int m = problema.getOfertas().length;  // número de orígenes
        int n = problema.getDemandas().length; // número de destinos

        // Crear matriz de asignaciones
        double[][] asignaciones = new double[m][n];

        // Copias de ofertas y demandas para no modificar el original
        double[] ofertasDisponibles = Arrays.copyOf(problema.getOfertas(), m);
        double[] demandasRestantes = Arrays.copyOf(problema.getDemandas(), n);

        // Arrays para marcar filas y columnas agotadas
        boolean[] filaAgotada = new boolean[m];
        boolean[] columnaAgotada = new boolean[n];

        int celdasAsignadas = 0;
        int celdasEsperadas = m + n - 1;

        // Algoritmo de Costo Mínimo
        while (celdasAsignadas < celdasEsperadas) {
            // Encontrar la celda con el menor costo no agotada
            Celda celdaMin = encontrarCeldaMinimaDisponible(
                problema.getCostos(),
                filaAgotada,
                columnaAgotada,
                m,
                n
            );

            if (celdaMin == null) {
                break; // No hay más celdas disponibles
            }

            int i = celdaMin.getFila();
            int j = celdaMin.getColumna();

            // Asignar el mínimo entre oferta disponible y demanda restante
            double asignacion = Math.min(ofertasDisponibles[i], demandasRestantes[j]);
            asignaciones[i][j] = asignacion;

            // Actualizar ofertas y demandas
            ofertasDisponibles[i] -= asignacion;
            demandasRestantes[j] -= asignacion;

            // Marcar filas o columnas agotadas
            if (Math.abs(ofertasDisponibles[i]) < 1e-6) {
                filaAgotada[i] = true;
            }
            if (Math.abs(demandasRestantes[j]) < 1e-6) {
                columnaAgotada[j] = true;
            }

            celdasAsignadas++;
        }

        // Crear y retornar la solución
        SolucionTransporte solucion = SolucionTransporte.builder()
                .asignaciones(asignaciones)
                .metodoUtilizado(MetodoSolucionInicial.COSTO_MINIMO)
                .build();

        // Calcular el costo total
        solucion.calcularCostoTotal(problema.getCostos());

        return solucion;
    }

    /**
     * Encuentra la celda con el menor costo que aún no ha sido agotada.
     */
    private Celda encontrarCeldaMinimaDisponible(
            double[][] costos,
            boolean[] filaAgotada,
            boolean[] columnaAgotada,
            int m,
            int n) {

        double costoMinimo = Double.MAX_VALUE;
        int filaMin = -1;
        int colMin = -1;

        for (int i = 0; i < m; i++) {
            if (filaAgotada[i]) continue;

            for (int j = 0; j < n; j++) {
                if (columnaAgotada[j]) continue;

                if (costos[i][j] < costoMinimo) {
                    costoMinimo = costos[i][j];
                    filaMin = i;
                    colMin = j;
                }
            }
        }

        if (filaMin == -1 || colMin == -1) {
            return null;
        }

        return Celda.builder()
                .fila(filaMin)
                .columna(colMin)
                .costo(costoMinimo)
                .build();
    }
}
