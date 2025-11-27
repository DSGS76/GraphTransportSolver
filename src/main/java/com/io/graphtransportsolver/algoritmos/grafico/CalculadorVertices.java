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
            return construirResultadoNoFactible(intersecciones, problema);
        }

        // 5. Evaluar función objetivo en vértices factibles
        evaluadorFuncion.evaluarPuntos(problema.getFuncionObjetivo(), verticesFactibles);

        // 6. Detectar si es no acotado
        if (esNoAcotado(problema, verticesFactibles)) {
            return construirResultadoNoAcotado(verticesFactibles, problema);
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

        // Ordenar vértices en sentido antihorario para formar el polígono correctamente
        List<Punto> regionOrdenada = ordenarVerticesAntihorario(verticesFactibles);

        return ResultadoGrafico.builder()
                .puntoOptimo(optimo)
                .vertices(verticesFactibles)
                .regionFactible(regionOrdenada)
                .restricciones(problema.getRestricciones())
                .tipoSolucion(tipo)
                .build();
    }

    /**
     * Filtra los puntos que pertenecen a la región factible.
     * Usa el método esFactible() del modelo ProblemaGrafico.
     */
    private List<Punto> filtrarFactibles(List<Punto> puntos, ProblemaGrafico problema) {
        return puntos.stream()
                .filter(punto -> {
                    boolean factible = problema.esFactible(punto);
                    punto.setEsFactible(factible);
                    return factible;
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
            if (!tieneMenorIgual || problema.getNumeroRestricciones() < 2) {
                if (fo.getCoeficienteX1() > 0 || fo.getCoeficienteX2() > 0) {
                    return true;
                }
            }
        }

        // Para minimización
        if (fo.getTipo() == TipoOptimizacion.MINIMIZAR) {
            boolean tieneMayorIgual = problema.getRestricciones().stream()
                    .anyMatch(r -> r.getTipo() == com.io.graphtransportsolver.models.grafico.enums.TipoDesigualdad.MAYOR_IGUAL);

            if (!tieneMayorIgual || problema.getNumeroRestricciones() < 2) {
                if (fo.getCoeficienteX1() < 0 || fo.getCoeficienteX2() < 0) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Construye resultado para problema no factible.
     * IMPORTANTE: Incluye restricciones para que se puedan graficar y el usuario
     * pueda ver visualmente por qué el problema no tiene solución factible.
     */
    private ResultadoGrafico construirResultadoNoFactible(List<Punto> intersecciones, ProblemaGrafico problema) {
        return ResultadoGrafico.builder()
                .puntoOptimo(null)
                .vertices(intersecciones)
                .regionFactible(List.of())
                .restricciones(problema.getRestricciones())  // ✅ Para graficar
                .tipoSolucion(TipoSolucion.NO_FACTIBLE)
                .build();
    }

    /**
     * Construye resultado para problema no acotado.
     */
    private ResultadoGrafico construirResultadoNoAcotado(List<Punto> vertices, ProblemaGrafico problema) {
        List<Punto> regionOrdenada = ordenarVerticesAntihorario(vertices);

        return ResultadoGrafico.builder()
                .puntoOptimo(null)
                .vertices(vertices)
                .regionFactible(regionOrdenada)
                .restricciones(problema.getRestricciones())
                .tipoSolucion(TipoSolucion.NO_ACOTADO)
                .build();
    }

    /**
     * Ordena los vértices en sentido antihorario alrededor del centroide.
     * Esto es necesario para que el frontend pueda dibujar correctamente
     * el polígono de la región factible sin líneas cruzadas.
     *
     * @param vertices lista de vértices a ordenar
     * @return lista ordenada en sentido antihorario
     */
    private List<Punto> ordenarVerticesAntihorario(List<Punto> vertices) {
        if (vertices == null || vertices.size() < 3) {
            return vertices;
        }

        // 1. Calcular el centroide (centro geométrico)
        double centroX = vertices.stream().mapToDouble(Punto::getX1).average().orElse(0.0);
        double centroY = vertices.stream().mapToDouble(Punto::getX2).average().orElse(0.0);

        // 2. Ordenar por ángulo polar respecto al centroide
        return vertices.stream()
                .sorted((p1, p2) -> {
                    double angulo1 = Math.atan2(p1.getX2() - centroY, p1.getX1() - centroX);
                    double angulo2 = Math.atan2(p2.getX2() - centroY, p2.getX1() - centroX);
                    return Double.compare(angulo1, angulo2);
                })
                .collect(Collectors.toList());
    }

}
