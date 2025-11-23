package com.io.graphtransportsolver.presentation.dto.grafico;

import com.io.graphtransportsolver.models.grafico.enums.TipoDesigualdad;
import com.io.graphtransportsolver.models.grafico.enums.TipoOptimizacion;

import java.util.List;

/**
 * DTO para recibir un problema de programación lineal (método gráfico) desde el frontend.
 * Utiliza records para una representación inmutable y concisa de los datos.
 *
 * @param funcionObjetivo función objetivo a optimizar
 * @param restricciones   lista de restricciones del problema
 * @param incluirNoNegatividad indica si se incluyen restricciones de no negatividad (x₁, x₂ ≥ 0)
 */
public record ProblemaGraficoDTO(
        FuncionObjetivoDTO funcionObjetivo,
        List<RestriccionDTO> restricciones,
        boolean incluirNoNegatividad
) {
    /**
     * Record anidado para representar la función objetivo.
     * Formato: Z = coeficienteX1 * x1 + coeficienteX2 * x2
     *
     * @param coeficienteX1 coeficiente de la variable x₁
     * @param coeficienteX2 coeficiente de la variable x₂
     * @param tipo          tipo de optimización (MAXIMIZAR o MINIMIZAR)
     */
    public record FuncionObjetivoDTO(
            double coeficienteX1,
            double coeficienteX2,
            TipoOptimizacion tipo
    ) {
    }

    /**
     * Record anidado para representar una restricción.
     * Formato: coeficienteX1 * x1 + coeficienteX2 * x2 {<=, >=, =} ladoDerecho
     *
     * @param coeficienteX1 coeficiente de la variable x₁
     * @param coeficienteX2 coeficiente de la variable x₂
     * @param tipo          tipo de desigualdad (MENOR_IGUAL, MAYOR_IGUAL, IGUAL)
     * @param ladoDerecho   término independiente (lado derecho de la restricción)
     */
    public record RestriccionDTO(
            double coeficienteX1,
            double coeficienteX2,
            TipoDesigualdad tipo,
            double ladoDerecho
    ) {
    }

    /**
     * Obtiene el número de restricciones (sin contar no negatividad).
     *
     * @return cantidad de restricciones
     */
    public int getNumeroRestricciones() {
        return restricciones != null ? restricciones.size() : 0;
    }
}
