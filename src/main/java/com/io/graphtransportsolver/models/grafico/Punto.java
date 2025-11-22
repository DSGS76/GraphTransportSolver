package com.io.graphtransportsolver.models.grafico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un punto en el plano cartesiano (x₁, x₂).
 * Es un vértice de la región factible.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Punto {

    /**
     * Coordenada x₁.
     */
    private double x1;

    /**
     * Coordenada x₂.
     */
    private double x2;

    /**
     * Valor de la función objetivo evaluada en este punto.
     */
    private double valorZ;

    /**
     * Indica si el punto pertenece a la región factible.
     */
    private boolean esFactible;

    /**
     * Constructor conveniente para crear un punto sin evaluar función objetivo.
     */
    public Punto(double x1, double x2) {
        this.x1 = x1;
        this.x2 = x2;
        this.valorZ = 0;
        this.esFactible = false;
    }

    /**
     * Verifica si las coordenadas del punto son no negativas.
     *
     * @return true si x₁ ≥ 0 y x₂ ≥ 0
     */
    public boolean esNoNegativo() {
        return x1 >= -1e-6 && x2 >= -1e-6;
    }

    /**
     * Calcula la distancia euclidiana desde el origen.
     *
     * @return distancia √(x₁² + x₂²)
     */
    public double distanciaOrigen() {
        return Math.sqrt(x1 * x1 + x2 * x2);
    }

    /**
     * Verifica si este punto es aproximadamente igual a otro.
     *
     * @param otro punto a comparar
     * @param epsilon tolerancia para la comparación
     * @return true si los puntos son iguales dentro de la tolerancia
     */
    public boolean esIgual(Punto otro, double epsilon) {
        return Math.abs(this.x1 - otro.x1) < epsilon &&
                Math.abs(this.x2 - otro.x2) < epsilon;
    }

}
