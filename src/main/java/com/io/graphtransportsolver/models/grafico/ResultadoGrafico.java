package com.io.graphtransportsolver.models.grafico;

import com.io.graphtransportsolver.models.grafico.enums.TipoSolucion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el resultado de la solución de un problema de programación lineal
 * mediante el método gráfico.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoGrafico {

    /**
     * Punto óptimo encontrado (puede ser null si no hay solución).
     */
    private Punto puntoOptimo;

    /**
     * Lista de todos los vértices de la región factible.
     */
    @Builder.Default
    private List<Punto> vertices = new ArrayList<>();

    /**
     * Puntos que forman el polígono de la región factible (ordenados).
     */
    @Builder.Default
    private List<Punto> regionFactible = new ArrayList<>();

    /**
     * Lista de restricciones del problema (para graficar y referencia).
     */
    @Builder.Default
    private List<Restriccion> restricciones = new ArrayList<>();

    /**
     * Tipo de solución encontrada.
     */
    private TipoSolucion tipoSolucion;

    /**
     * Verifica si se encontró una solución óptima.
     *
     * @return true si hay punto óptimo y es factible
     */
    public boolean tieneSolucion() {
        return puntoOptimo != null &&
                (tipoSolucion == TipoSolucion.UNICA || tipoSolucion == TipoSolucion.MULTIPLE);
    }

    /**
     * Obtiene el valor óptimo de la función objetivo.
     *
     * @return valor de Z en el punto óptimo, o 0 si no hay solución
     */
    public double getValorOptimo() {
        return puntoOptimo != null ? puntoOptimo.getValorZ() : 0.0;
    }

    /**
     * Obtiene el número de vértices de la región factible.
     *
     * @return cantidad de vértices
     */
    public int getNumeroVertices() {
        return vertices.size();
    }

}
