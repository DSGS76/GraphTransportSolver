package com.io.graphtransportsolver.algoritmos.grafico;

import com.io.graphtransportsolver.models.grafico.Punto;
import com.io.graphtransportsolver.models.grafico.Restriccion;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de calcular intersecciones entre restricciones.
 * Usa el método de Regla de Cramer para resolver sistemas de ecuaciones lineales.
 */
@Component
public class CalculadorInterseccion {

    private static final double EPSILON = 1e-10;

    /**
     * Calcula la intersección entre dos restricciones.
     * Resuelve el sistema de ecuaciones:
     * a1*x1 + b1*x2 = c1
     * a2*x1 + b2*x2 = c2
     *
     * @param r1 primera restricción
     * @param r2 segunda restricción
     * @return punto de intersección o null si no existe (rectas paralelas)
     */
    public Punto calcularInterseccion(Restriccion r1, Restriccion r2) {
        double a1 = r1.getCoeficienteX1();
        double b1 = r1.getCoeficienteX2();
        double c1 = r1.getLadoDerecho();

        double a2 = r2.getCoeficienteX1();
        double b2 = r2.getCoeficienteX2();
        double c2 = r2.getLadoDerecho();

        // Calcular determinante
        double det = a1 * b2 - a2 * b1;

        // Si el determinante es cero, las rectas son paralelas
        if (Math.abs(det) < EPSILON) {
            return null;
        }

        // Regla de Cramer
        double x1 = (c1 * b2 - c2 * b1) / det;
        double x2 = (a1 * c2 - a2 * c1) / det;

        return Punto.builder()
                .x1(x1)
                .x2(x2)
                .esFactible(false)
                .build();
    }

    /**
     * Calcula todas las intersecciones posibles entre restricciones y ejes.
     * En PL siempre se consideran las restricciones de no negatividad (x₁, x₂ ≥ 0).
     *
     * @param restricciones lista de restricciones
     * @return lista de puntos candidatos a vértices
     */
    public List<Punto> calcularTodasIntersecciones(List<Restriccion> restricciones) {
        List<Punto> intersecciones = new ArrayList<>();

        // Intersecciones entre restricciones
        for (int i = 0; i < restricciones.size(); i++) {
            for (int j = i + 1; j < restricciones.size(); j++) {
                Punto interseccion = calcularInterseccion(restricciones.get(i), restricciones.get(j));
                if (interseccion != null) {
                    intersecciones.add(interseccion);
                }
            }
        }

        // Intersecciones con ejes (usa métodos del modelo Restriccion)
        for (Restriccion restriccion : restricciones) {
            // Intersección con eje X (x₂ = 0)
            Punto interseccionX = restriccion.interseccionEjeX1();
            if (interseccionX != null && interseccionX.getX1() >= -EPSILON) {
                interseccionX.setEsFactible(false);
                intersecciones.add(interseccionX);
            }

            // Intersección con eje Y (x₁ = 0)
            Punto interseccionY = restriccion.interseccionEjeX2();
            if (interseccionY != null && interseccionY.getX2() >= -EPSILON) {
                interseccionY.setEsFactible(false);
                intersecciones.add(interseccionY);
            }
        }

        // Origen (siempre se considera en PL)
        intersecciones.add(Punto.builder()
                .x1(0)
                .x2(0)
                .esFactible(false)
                .build());

        return intersecciones;
    }

    /**
     * Elimina puntos duplicados de una lista.
     *
     * @param puntos lista de puntos
     * @return lista sin duplicados
     */
    public List<Punto> eliminarDuplicados(List<Punto> puntos) {
        List<Punto> resultado = new ArrayList<>();

        for (Punto punto : puntos) {
            boolean esDuplicado = false;
            for (Punto existente : resultado) {
                if (Math.abs(punto.getX1() - existente.getX1()) < EPSILON &&
                    Math.abs(punto.getX2() - existente.getX2()) < EPSILON) {
                    esDuplicado = true;
                    break;
                }
            }
            if (!esDuplicado) {
                resultado.add(punto);
            }
        }

        return resultado;
    }
}
