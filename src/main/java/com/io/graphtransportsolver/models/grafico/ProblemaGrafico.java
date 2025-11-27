package com.io.graphtransportsolver.models.grafico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un problema completo de programación lineal para el método gráfico.
 * Incluye la función objetivo, las restricciones y las restricciones de no negatividad.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemaGrafico {

    /**
     * Función objetivo a optimizar.
     */
    private FuncionObjetivo funcionObjetivo;

    /**
     * Lista de restricciones del problema.
     */
    @Builder.Default
    private List<Restriccion> restricciones = new ArrayList<>();

    /**
     * Indica si se incluyen restricciones de no negatividad (x₁, x₂ ≥ 0).
     */
    @Builder.Default
    private boolean restriccionesNoNegatividad = true;

    /**
     * Verifica si un punto es factible (satisface todas las restricciones).
     *
     * @param punto punto a verificar
     * @return true si el punto satisface todas las restricciones
     */
    public boolean esFactible(Punto punto) {
        // Verificar no negatividad si está habilitada
        if (restriccionesNoNegatividad && !punto.esNoNegativo()) {
            return false;
        }

        // Verificar todas las restricciones
        for (Restriccion restriccion : restricciones) {
            if (!restriccion.esSatisfecha(punto.getX1(), punto.getX2())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Valida que el problema esté correctamente formulado.
     *
     * @return true si el problema es válido
     */
    public boolean esValido() {
        // Verificar función objetivo
        if (funcionObjetivo == null || !funcionObjetivo.esValida()) {
            return false;
        }

        // Verificar que haya al menos dos restricciones
        if (getNumeroRestricciones() < 2) {
            return false;
        }

        // Verificar que todas las restricciones sean válidas
        for (Restriccion restriccion : restricciones) {
            if (!restriccion.esValida()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Obtiene el número de restricciones (sin contar no negatividad).
     *
     * @return cantidad de restricciones
     */
    public int getNumeroRestricciones() {
        return restricciones.size();
    }
}