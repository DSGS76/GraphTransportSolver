package com.io.graphtransportsolver.models.grafico;

import com.io.graphtransportsolver.models.grafico.enums.TipoDesigualdad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa una restricción del problema de programación lineal.
 * Formato: coeficienteX1 * x1 + coeficienteX2 * x2 {<=, >=, =} ladoDerecho
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restriccion {

    private static final double EPSILON = 1e-6;

    /**
     * Coeficiente de la variable x₁.
     */
    private double coeficienteX1;

    /**
     * Coeficiente de la variable x₂.
     */
    private double coeficienteX2;

    /**
     * Tipo de desigualdad (≤, ≥, =).
     */
    private TipoDesigualdad tipo;

    /**
     * Término independiente (lado derecho de la restricción).
     */
    private double ladoDerecho;

    /**
     * Verifica si un punto satisface esta restricción.
     *
     * @param x1 valor de la primera variable
     * @param x2 valor de la segunda variable
     * @return true si el punto satisface la restricción
     */
    public boolean esSatisfecha(double x1, double x2) {
        double ladoIzquierdo = coeficienteX1 * x1 + coeficienteX2 * x2;

        return switch (tipo) {
            case MENOR_IGUAL -> ladoIzquierdo <= ladoDerecho + EPSILON;
            case MAYOR_IGUAL -> ladoIzquierdo >= ladoDerecho - EPSILON;
            case IGUAL -> Math.abs(ladoIzquierdo - ladoDerecho) < EPSILON;
        };
    }

    /**
     * Verifica si un punto está sobre la línea de esta restricción.
     *
     * @param x1 valor de la primera variable
     * @param x2 valor de la segunda variable
     * @return true si el punto está sobre la línea (restricción activa)
     */
    public boolean esActiva(double x1, double x2) {
        double ladoIzquierdo = coeficienteX1 * x1 + coeficienteX2 * x2;
        return Math.abs(ladoIzquierdo - ladoDerecho) < EPSILON;
    }

    /**
     * Calcula la intersección de esta restricción con el eje x₁ (cuando x₂ = 0).
     *
     * @return punto de intersección o null si no existe
     */
    public Punto interseccionEjeX1() {
        if (Math.abs(coeficienteX1) < EPSILON) {
            return null; // Restricción paralela al eje x₁
        }
        double x1 = ladoDerecho / coeficienteX1;
        return Punto.builder()
                .x1(x1)
                .x2(0)
                .build();
    }

    /**
     * Calcula la intersección de esta restricción con el eje x₂ (cuando x₁ = 0).
     *
     * @return punto de intersección o null si no existe
     */
    public Punto interseccionEjeX2() {
        if (Math.abs(coeficienteX2) < EPSILON) {
            return null; // Restricción paralela al eje x₂
        }
        double x2 = ladoDerecho / coeficienteX2;
        return Punto.builder()
                .x1(0)
                .x2(x2)
                .build();
    }

    /**
     * Verifica si la restricción es válida.
     *
     * @return true si al menos un coeficiente es diferente de cero
     */
    public boolean esValida() {
        return Math.abs(coeficienteX1) > EPSILON || Math.abs(coeficienteX2) > EPSILON;
    }

    /**
     * Calcula la pendiente de la recta.
     *
     * @return pendiente -a₁/a₂
     */
    public double calcularPendiente() {
        if (Math.abs(coeficienteX2) < EPSILON) {
            return Double.POSITIVE_INFINITY; // Recta vertical
        }
        return -coeficienteX1 / coeficienteX2;
    }
}
