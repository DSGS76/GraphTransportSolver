package com.io.graphtransportsolver.algoritmos.transporte;

import com.io.graphtransportsolver.models.transporte.Celda;
import com.io.graphtransportsolver.models.transporte.ProblemaTransporte;
import com.io.graphtransportsolver.models.transporte.SolucionTransporte;
import com.io.graphtransportsolver.models.transporte.enums.MetodoSolucionInicial;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Implementación del método de Aproximación de Vogel (VAM) para encontrar
 * una solución básica factible inicial del problema de transporte.
 *
 * Este método calcula penalizaciones (diferencias entre los dos menores costos)
 * para cada fila y columna, selecciona la fila/columna con mayor penalización,
 * y asigna en la celda de menor costo de esa fila/columna.
 */
@Component
public class VogelStrategy implements SolucionInicialStrategy {

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

        // Algoritmo de Vogel
        while (celdasAsignadas < celdasEsperadas) {
            // Calcular penalizaciones para filas
            double[] penalizacionesFila = calcularPenalizacionesFila(
                problema.getCostos(),
                filaAgotada,
                columnaAgotada,
                m,
                n
            );

            // Calcular penalizaciones para columnas
            double[] penalizacionesColumna = calcularPenalizacionesColumna(
                problema.getCostos(),
                filaAgotada,
                columnaAgotada,
                m,
                n
            );

            // Encontrar la máxima penalización
            Penalizacion maxPenalizacion = encontrarMaximaPenalizacion(
                penalizacionesFila,
                penalizacionesColumna,
                filaAgotada,
                columnaAgotada
            );

            if (maxPenalizacion == null) {
                break; // No hay más penalizaciones válidas
            }

            // Encontrar la celda de menor costo en la fila/columna seleccionada
            Celda celdaMinima = encontrarCeldaMinima(
                problema.getCostos(),
                maxPenalizacion,
                filaAgotada,
                columnaAgotada,
                m,
                n
            );

            if (celdaMinima == null) {
                break;
            }

            int i = celdaMinima.getFila();
            int j = celdaMinima.getColumna();

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
                .metodoUtilizado(MetodoSolucionInicial.VOGEL)
                .build();

        // Calcular el costo total
        solucion.calcularCostoTotal(problema.getCostos());

        return solucion;
    }

    /**
     * Calcula las penalizaciones para cada fila.
     * La penalización es la diferencia entre los dos menores costos.
     */
    private double[] calcularPenalizacionesFila(
            double[][] costos,
            boolean[] filaAgotada,
            boolean[] columnaAgotada,
            int m,
            int n) {

        double[] penalizaciones = new double[m];
        Arrays.fill(penalizaciones, -1);

        for (int i = 0; i < m; i++) {
            if (filaAgotada[i]) continue;

            double min1 = Double.MAX_VALUE;
            double min2 = Double.MAX_VALUE;

            for (int j = 0; j < n; j++) {
                if (columnaAgotada[j]) continue;

                double costo = costos[i][j];
                if (costo < min1) {
                    min2 = min1;
                    min1 = costo;
                } else if (costo < min2) {
                    min2 = costo;
                }
            }

            if (min2 != Double.MAX_VALUE) {
                penalizaciones[i] = min2 - min1;
            } else if (min1 != Double.MAX_VALUE) {
                // Solo hay una columna disponible
                penalizaciones[i] = min1;
            }
        }

        return penalizaciones;
    }

    /**
     * Calcula las penalizaciones para cada columna.
     */
    private double[] calcularPenalizacionesColumna(
            double[][] costos,
            boolean[] filaAgotada,
            boolean[] columnaAgotada,
            int m,
            int n) {

        double[] penalizaciones = new double[n];
        Arrays.fill(penalizaciones, -1);

        for (int j = 0; j < n; j++) {
            if (columnaAgotada[j]) continue;

            double min1 = Double.MAX_VALUE;
            double min2 = Double.MAX_VALUE;

            for (int i = 0; i < m; i++) {
                if (filaAgotada[i]) continue;

                double costo = costos[i][j];
                if (costo < min1) {
                    min2 = min1;
                    min1 = costo;
                } else if (costo < min2) {
                    min2 = costo;
                }
            }

            if (min2 != Double.MAX_VALUE) {
                penalizaciones[j] = min2 - min1;
            } else if (min1 != Double.MAX_VALUE) {
                // Solo hay una fila disponible
                penalizaciones[j] = min1;
            }
        }

        return penalizaciones;
    }

    /**
     * Encuentra la penalización máxima entre filas y columnas.
     */
    private Penalizacion encontrarMaximaPenalizacion(
            double[] penalizacionesFila,
            double[] penalizacionesColumna,
            boolean[] filaAgotada,
            boolean[] columnaAgotada) {

        double maxPenalizacion = -1;
        int indiceMax = -1;
        boolean esFila = true;

        // Buscar en filas
        for (int i = 0; i < penalizacionesFila.length; i++) {
            if (!filaAgotada[i] && penalizacionesFila[i] > maxPenalizacion) {
                maxPenalizacion = penalizacionesFila[i];
                indiceMax = i;
            }
        }

        // Buscar en columnas
        for (int j = 0; j < penalizacionesColumna.length; j++) {
            if (!columnaAgotada[j] && penalizacionesColumna[j] > maxPenalizacion) {
                maxPenalizacion = penalizacionesColumna[j];
                indiceMax = j;
                esFila = false;
            }
        }

        if (indiceMax == -1) {
            return null;
        }

        return new Penalizacion(indiceMax, maxPenalizacion, esFila);
    }

    /**
     * Encuentra la celda de menor costo en la fila o columna seleccionada.
     */
    private Celda encontrarCeldaMinima(
            double[][] costos,
            Penalizacion penalizacion,
            boolean[] filaAgotada,
            boolean[] columnaAgotada,
            int m,
            int n) {

        double costoMinimo = Double.MAX_VALUE;
        int filaMin = -1;
        int colMin = -1;

        if (penalizacion.esFila) {
            // Buscar en la fila seleccionada
            int i = penalizacion.indice;
            for (int j = 0; j < n; j++) {
                if (!columnaAgotada[j] && costos[i][j] < costoMinimo) {
                    costoMinimo = costos[i][j];
                    filaMin = i;
                    colMin = j;
                }
            }
        } else {
            // Buscar en la columna seleccionada
            int j = penalizacion.indice;
            for (int i = 0; i < m; i++) {
                if (!filaAgotada[i] && costos[i][j] < costoMinimo) {
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
                .build();
    }

    /**
     * Clase auxiliar para representar una penalización.
     */
    private static class Penalizacion {
        int indice;
        double valor;
        boolean esFila;

        Penalizacion(int indice, double valor, boolean esFila) {
            this.indice = indice;
            this.valor = valor;
            this.esFila = esFila;
        }
    }
}
