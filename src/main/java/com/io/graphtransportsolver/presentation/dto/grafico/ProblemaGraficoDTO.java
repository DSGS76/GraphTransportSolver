package com.io.graphtransportsolver.presentation.dto.grafico;

import com.io.graphtransportsolver.models.grafico.enums.TipoDesigualdad;
import com.io.graphtransportsolver.models.grafico.enums.TipoOptimizacion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
        @NotNull(message = "La función objetivo es obligatoria")
        FuncionObjetivoDTO funcionObjetivo,
        @NotNull(message = "Las restricciones son obligatorias")
        @NotEmpty(message = "Debe haber al menos una restricción")
        @Valid
        List<RestriccionDTO> restricciones,
        @NotNull(message = "Debe especificar si incluir restricciones de no negatividad")
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
            @NotNull(message = "Debe especificar el tipo de optimización")
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
            @NotNull(message = "Debe especificar el tipo de desigualdad")
            TipoDesigualdad tipo,
            double ladoDerecho
    ) {
    }
}
