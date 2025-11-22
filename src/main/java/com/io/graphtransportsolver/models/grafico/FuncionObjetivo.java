package com.io.graphtransportsolver.models.grafico;

import com.io.graphtransportsolver.models.grafico.enums.TipoOptimizacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa la función objetivo del problema de programación lineal.
 * Formato: Z = coeficienteX1 * x1 + coeficienteX2 * x2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuncionObjetivo {

    /**
     * Coeficiente de la variable x₁.
     */
    private double coeficienteX1;

    /**
     * Coeficiente de la variable x₂.
     */
    private double coeficienteX2;

    /**
     * Tipo de optimización (MAXIMIZAR o MINIMIZAR).
     */
    private TipoOptimizacion tipo;

    /**
     * Evalúa la función objetivo en un punto dado.
     *
     * @param x1 valor de la primera variable
     * @param x2 valor de la segunda variable
     * @return valor de Z = c₁x₁ + c₂x₂
     */
    public double evaluar(double x1, double x2) {
        return coeficienteX1 * x1 + coeficienteX2 * x2;
    }

    /**
     * Verifica si la función objetivo es válida.
     *
     * @return true si al menos un coeficiente es diferente de cero
     */
    public boolean esValida() {
        return Math.abs(coeficienteX1) > 1e-10 || Math.abs(coeficienteX2) > 1e-10;
    }

    /**
     * Calcula la pendiente de las líneas de isovalor.
     *
     * @return pendiente -c₁/c₂
     * @throws ArithmeticException si c₂ = 0
     */
    public double calcularPendiente() {
        if (Math.abs(coeficienteX2) < 1e-10) {
            throw new ArithmeticException("Coeficiente X2 es cero, pendiente indefinida");
        }
        return -coeficienteX1 / coeficienteX2;
    }
}
