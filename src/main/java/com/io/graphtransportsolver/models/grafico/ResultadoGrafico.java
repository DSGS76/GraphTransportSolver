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
     * Tipo de solución encontrada.
     */
    private TipoSolucion tipoSolucion;

    /**
     * Mensaje descriptivo del resultado.
     */
    private String mensaje;

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

    /**
     * Genera un mensaje descriptivo del resultado.
     *
     * @return mensaje explicativo
     */
    public String generarMensaje() {
        return switch (tipoSolucion) {
            case UNICA -> String.format(
                    "Solución óptima encontrada en (%.4f, %.4f) con Z = %.4f",
                    puntoOptimo.getX1(), puntoOptimo.getX2(), puntoOptimo.getValorZ()
            );
            case MULTIPLE -> String.format(
                    "Soluciones múltiples. Una solución óptima es (%.4f, %.4f) con Z = %.4f",
                    puntoOptimo.getX1(), puntoOptimo.getX2(), puntoOptimo.getValorZ()
            );
            case NO_FACTIBLE ->
                    "El problema no tiene región factible. Las restricciones son contradictorias.";
            case NO_ACOTADO ->
                    "El problema no está acotado. La función objetivo puede mejorar indefinidamente.";
        };
    }

}
