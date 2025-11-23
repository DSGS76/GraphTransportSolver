package com.io.graphtransportsolver.algoritmos.grafico;

import com.io.graphtransportsolver.models.grafico.FuncionObjetivo;
import com.io.graphtransportsolver.models.grafico.Punto;
import com.io.graphtransportsolver.models.grafico.enums.TipoOptimizacion;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Clase encargada de evaluar la función objetivo en puntos.
 * Solo realiza cálculos.
 */
@Component
public class EvaluadorFuncionObjetivo {

    /**
     * Evalúa la función objetivo en un punto dado.
     *
     * @param funcion función objetivo
     * @param punto   punto a evaluar
     * @return valor de Z en el punto
     */
    public double evaluar(FuncionObjetivo funcion, Punto punto) {
        return funcion.evaluar(punto.getX1(), punto.getX2());
    }

    /**
     * Evalúa la función objetivo en una lista de puntos.
     * Actualiza el valorZ de cada punto.
     *
     * @param funcion función objetivo
     * @param puntos  lista de puntos
     */
    public void evaluarPuntos(FuncionObjetivo funcion, List<Punto> puntos) {
        for (Punto punto : puntos) {
            double valorZ = evaluar(funcion, punto);
            punto.setValorZ(valorZ);
        }
    }

    /**
     * Encuentra el punto óptimo según el tipo de optimización.
     *
     * @param puntos          lista de puntos evaluados
     * @param tipoOptimizacion MAXIMIZAR o MINIMIZAR
     * @return punto óptimo
     */
    public Punto encontrarOptimo(List<Punto> puntos, TipoOptimizacion tipoOptimizacion) {
        if (puntos == null || puntos.isEmpty()) {
            return null;
        }

        Punto optimo = puntos.getFirst();

        for (Punto punto : puntos) {
            if (tipoOptimizacion == TipoOptimizacion.MAXIMIZAR) {
                if (punto.getValorZ() > optimo.getValorZ()) {
                    optimo = punto;
                }
            } else {
                if (punto.getValorZ() < optimo.getValorZ()) {
                    optimo = punto;
                }
            }
        }

        return optimo;
    }

    /**
     * Calcula el valor óptimo de la función objetivo.
     *
     * @param puntos          lista de puntos
     * @param tipoOptimizacion MAXIMIZAR o MINIMIZAR
     * @return valor óptimo de Z
     */
    public double calcularValorOptimo(List<Punto> puntos, TipoOptimizacion tipoOptimizacion) {
        Punto optimo = encontrarOptimo(puntos, tipoOptimizacion);
        return optimo != null ? optimo.getValorZ() : 0.0;
    }

    /**
     * Verifica si hay soluciones múltiples.
     * Cuando 2 o más vértices tienen el mismo valor óptimo, existen infinitas soluciones
     * (todos los puntos del segmento entre esos vértices son óptimos).
     *
     * @param puntos          lista de puntos evaluados
     * @param tipoOptimizacion MAXIMIZAR o MINIMIZAR
     * @return true si hay 2 o más vértices con el valor óptimo
     */
    public boolean esSolucionMultiple(List<Punto> puntos, TipoOptimizacion tipoOptimizacion) {
        if (puntos == null || puntos.size() < 2) {
            return false;
        }

        double valorOptimo = calcularValorOptimo(puntos, tipoOptimizacion);

        int contadorOptimos = 0;
        for (Punto punto : puntos) {
            if (Math.abs(punto.getValorZ() - valorOptimo) < 1e-10) {
                contadorOptimos++;
                if (contadorOptimos > 1) {
                    return true;  // Ya encontramos al menos 2 vértices óptimos
                }
            }
        }

        return false;
    }
}
