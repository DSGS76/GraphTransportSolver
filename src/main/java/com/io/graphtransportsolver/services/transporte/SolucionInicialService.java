package com.io.graphtransportsolver.services.transporte;

import com.io.graphtransportsolver.algoritmos.transporte.CostoMinimoStrategy;
import com.io.graphtransportsolver.algoritmos.transporte.EsquinaNoroesteStrategy;
import com.io.graphtransportsolver.algoritmos.transporte.SolucionInicialStrategy;
import com.io.graphtransportsolver.algoritmos.transporte.VogelStrategy;
import com.io.graphtransportsolver.models.transporte.ProblemaTransporte;
import com.io.graphtransportsolver.models.transporte.SolucionTransporte;
import com.io.graphtransportsolver.models.transporte.enums.MetodoSolucionInicial;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio que coordina la resolución de problemas de transporte
 * utilizando diferentes métodos de solución inicial (Strategy Pattern).
 */
@Service
@RequiredArgsConstructor
public class SolucionInicialService {

    private final EsquinaNoroesteStrategy esquinaNoroesteStrategy;
    private final CostoMinimoStrategy costoMinimoStrategy;
    private final VogelStrategy vogelStrategy;
    private final BalanceadorService balanceadorService;

    /**
     * Encuentra una solución inicial utilizando el método especificado.
     *
     * @param problema el problema de transporte a resolver
     * @param metodo el método a utilizar
     * @return la solución inicial encontrada
     */
    public SolucionTransporte encontrarSolucionInicial(
            ProblemaTransporte problema,
            MetodoSolucionInicial metodo) {

        // Validaciones de lógica de negocio
        validarProblema(problema);

        if (metodo == null) {
            throw new IllegalArgumentException("El método no puede ser nulo");
        }

        // Balancear el problema si es necesario
        ProblemaTransporte problemaBalanceado = balanceadorService.balancear(problema);

        // Seleccionar la estrategia según el método
        SolucionInicialStrategy strategy = obtenerEstrategia(metodo);

        // Resolver usando la estrategia seleccionada
        return strategy.encontrarSolucionInicial(problemaBalanceado);
    }

    /**
     * Obtiene la estrategia correspondiente al método especificado.
     */
    private SolucionInicialStrategy obtenerEstrategia(MetodoSolucionInicial metodo) {
        return switch (metodo) {
            case ESQUINA_NOROESTE -> esquinaNoroesteStrategy;
            case COSTO_MINIMO -> costoMinimoStrategy;
            case VOGEL -> vogelStrategy;
        };
    }

    /**
     * Compara los tres métodos de solución inicial para un mismo problema.
     *
     * @param problema el problema a resolver
     * @return array con las tres soluciones (Esquina Noroeste, Costo Mínimo, Vogel)
     */
    public SolucionTransporte[] compararMetodos(ProblemaTransporte problema) {
        // Validaciones de lógica de negocio
        validarProblema(problema);

        ProblemaTransporte problemaBalanceado = balanceadorService.balancear(problema);

        SolucionTransporte[] soluciones = new SolucionTransporte[3];
        soluciones[0] = esquinaNoroesteStrategy.encontrarSolucionInicial(problemaBalanceado);
        soluciones[1] = costoMinimoStrategy.encontrarSolucionInicial(problemaBalanceado);
        soluciones[2] = vogelStrategy.encontrarSolucionInicial(problemaBalanceado);

        return soluciones;
    }

    /**
     * Válida que el problema de transporte sea válido.
     *
     * @param problema el problema a validar
     * @throws IllegalArgumentException si el problema no es válido
     */
    private void validarProblema(ProblemaTransporte problema) {
        if (problema == null) {
            throw new IllegalArgumentException("El problema no puede ser nulo");
        }

        if (problema.getOfertas() == null || problema.getOfertas().length == 0) {
            throw new IllegalArgumentException("El problema debe tener al menos un origen");
        }

        if (problema.getDemandas() == null || problema.getDemandas().length == 0) {
            throw new IllegalArgumentException("El problema debe tener al menos un destino");
        }

        if (problema.getCostos() == null || problema.getCostos().length == 0) {
            throw new IllegalArgumentException("La matriz de costos no puede ser nula o vacía");
        }

        int m = problema.getOfertas().length;
        int n = problema.getDemandas().length;

        // Validar dimensiones de la matriz de costos
        if (problema.getCostos().length != m) {
            throw new IllegalArgumentException(
                "La matriz de costos debe tener " + m + " filas (orígenes)"
            );
        }

        for (int i = 0; i < m; i++) {
            if (problema.getCostos()[i] == null || problema.getCostos()[i].length != n) {
                throw new IllegalArgumentException(
                    "La fila " + i + " de la matriz de costos debe tener " + n + " columnas (destinos)"
                );
            }
        }

        // Validar que ofertas y demandas sean positivas
        for (int i = 0; i < m; i++) {
            if (problema.getOfertas()[i] < 0) {
                throw new IllegalArgumentException(
                    "La oferta del origen " + i + " no puede ser negativa"
                );
            }
        }

        for (int j = 0; j < n; j++) {
            if (problema.getDemandas()[j] < 0) {
                throw new IllegalArgumentException(
                    "La demanda del destino " + j + " no puede ser negativa"
                );
            }
        }

        // Validar que haya al menos algo de oferta y demanda
        if (problema.getOfertaTotal() == 0) {
            throw new IllegalArgumentException("La oferta total debe ser mayor a cero");
        }

        if (problema.getDemandaTotal() == 0) {
            throw new IllegalArgumentException("La demanda total debe ser mayor a cero");
        }
    }
}
