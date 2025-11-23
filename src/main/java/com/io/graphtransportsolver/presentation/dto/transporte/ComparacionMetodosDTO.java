package com.io.graphtransportsolver.presentation.dto.transporte;

import com.io.graphtransportsolver.models.transporte.enums.MetodoSolucionInicial;

/**
 * DTO para comparar los resultados de los tres métodos de solución inicial (Response).
 * Permite al frontend mostrar una comparación visual entre los métodos.
 *
 * @param esquinaNoroeste solución usando el método de Esquina Noroeste
 * @param costoMinimo     solución usando el método de Costo Mínimo
 * @param vogel           solución usando el método de Vogel (VAM)
 */
public record ComparacionMetodosDTO(
        SolucionTransporteDTO esquinaNoroeste,
        SolucionTransporteDTO costoMinimo,
        SolucionTransporteDTO vogel
) {
}