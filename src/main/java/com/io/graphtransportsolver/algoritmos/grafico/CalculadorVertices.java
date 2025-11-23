package com.io.graphtransportsolver.algoritmos.grafico;

import com.io.graphtransportsolver.models.grafico.*;
import com.io.graphtransportsolver.models.grafico.enums.TipoOptimizacion;
import com.io.graphtransportsolver.models.grafico.enums.TipoSolucion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase principal que coordina el cálculo de la solución gráfica.
 * Detecta el tipo de solución (única, múltiple, no factible, no acotada).
 */
@Component
@RequiredArgsConstructor
public class CalculadorVertices {

    private static final double EPSILON = 1e-10;

    private final CalculadorInterseccion calculadorInterseccion;
    private final EvaluadorFuncionObjetivo evaluadorFuncion;

    /**
     * Calcula la solución completa del problema gráfico.
     *
     * @param problema problema de programación lineal
     * @return resultado con tipo de solución, vértices y punto óptimo
     */
    public ResultadoGrafico calcular(ProblemaGrafico problema) {

        // 1. Calcular todas las intersecciones
        List<Punto> intersecciones = calculadorInterseccion
                .calcularTodasIntersecciones(problema.getRestricciones());

        // 2. Eliminar duplicados
        intersecciones = calculadorInterseccion.eliminarDuplicados(intersecciones);

        // 3. Filtrar puntos factibles
        List<Punto> verticesFactibles = filtrarFactibles(intersecciones, problema);

        // 4. Detectar si no hay región factible
        if (verticesFactibles.isEmpty()) {
            return construirResultadoNoFactible(intersecciones);
        }

        // 5. Evaluar función objetivo en vértices factibles
        evaluadorFuncion.evaluarPuntos(problema.getFuncionObjetivo(), verticesFactibles);

        // 6. Detectar si es no acotado
        if (esNoAcotado(problema, verticesFactibles)) {
            return construirResultadoNoAcotado(verticesFactibles);
        }

        // 7. Encontrar punto óptimo
        Punto optimo = evaluadorFuncion.encontrarOptimo(
                verticesFactibles,
                problema.getFuncionObjetivo().getTipo()
        );

        // 8. Detectar si hay soluciones múltiples
        boolean esMultiple = evaluadorFuncion.esSolucionMultiple(
                verticesFactibles,
                problema.getFuncionObjetivo().getTipo()
        );

        TipoSolucion tipo = esMultiple ? TipoSolucion.MULTIPLE : TipoSolucion.UNICA;

        return ResultadoGrafico.builder()
                .puntoOptimo(optimo)
                .vertices(verticesFactibles)
                .regionFactible(verticesFactibles) // Podrías ordenarlos para formar el polígono
                .tipoSolucion(tipo)
                .build();
    }

    /**
     * Filtra los puntos que pertenecen a la región factible.
     */
    private List<Punto> filtrarFactibles(List<Punto> puntos, ProblemaGrafico problema) {
        return puntos.stream()
                .filter(punto -> {
                    // Verificar no negatividad
                    if (problema.isRestriccionesNoNegatividad()) {
                        if (punto.getX1() < -EPSILON || punto.getX2() < -EPSILON) {
                            return false;
                        }
                    }

                    // Verificar todas las restricciones
                    for (Restriccion restriccion : problema.getRestricciones()) {
                        if (!restriccion.esSatisfecha(punto.getX1(), punto.getX2())) {
                            return false;
                        }
                    }

                    punto.setEsFactible(true);
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Detecta si el problema es no acotado.
     * Un problema es no acotado si la región factible se extiende al infinito
     * en la dirección de mejora de la función objetivo.
     */
    private boolean esNoAcotado(ProblemaGrafico problema, List<Punto> vertices) {
        if (vertices.isEmpty()) {
            return false;
        }

        FuncionObjetivo fo = problema.getFuncionObjetivo();

        // Verificar si hay restricciones que "cierren" la región
        boolean tieneMenorIgual = problema.getRestricciones().stream()
                .anyMatch(r -> r.getTipo() == com.io.graphtransportsolver.models.grafico.enums.TipoDesigualdad.MENOR_IGUAL);

        // Para maximización
        if (fo.getTipo() == TipoOptimizacion.MAXIMIZAR) {
            // Si no hay restricciones ≤ (o muy pocas) y los coeficientes son positivos
            if (!tieneMenorIgual || problema.getRestricciones().size() < 2) {
                if (fo.getCoeficienteX1() > 0 || fo.getCoeficienteX2() > 0) {
                    return true;
                }
            }
        }

        // Para minimización
        if (fo.getTipo() == TipoOptimizacion.MINIMIZAR) {
            boolean tieneMayorIgual = problema.getRestricciones().stream()
                    .anyMatch(r -> r.getTipo() == com.io.graphtransportsolver.models.grafico.enums.TipoDesigualdad.MAYOR_IGUAL);

            if (!tieneMayorIgual || problema.getRestricciones().size() < 2) {
                if (fo.getCoeficienteX1() < 0 || fo.getCoeficienteX2() < 0) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Construye resultado para problema no factible.
     */
    private ResultadoGrafico construirResultadoNoFactible(List<Punto> intersecciones) {
        return ResultadoGrafico.builder()
                .puntoOptimo(null)
                .vertices(intersecciones)
                .regionFactible(List.of())
                .tipoSolucion(TipoSolucion.NO_FACTIBLE)
                .build();
    }

    /**
     * Construye resultado para problema no acotado.
     */
    private ResultadoGrafico construirResultadoNoAcotado(List<Punto> vertices) {
        return ResultadoGrafico.builder()
                .puntoOptimo(null)
                .vertices(vertices)
                .regionFactible(vertices)
                .tipoSolucion(TipoSolucion.NO_ACOTADO)
                .build();
    }

}
