package com.io.graphtransportsolver.services.grafico;

import com.io.graphtransportsolver.algoritmos.grafico.CalculadorVertices;
import com.io.graphtransportsolver.models.grafico.*;
import com.io.graphtransportsolver.models.grafico.enums.TipoDesigualdad;
import com.io.graphtransportsolver.presentation.dto.grafico.ProblemaGraficoDTO;
import com.io.graphtransportsolver.presentation.dto.grafico.SolucionGraficoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio principal para resolver problemas de programación lineal
 * mediante el método gráfico.
 *
 * Responsabilidades:
 * - Validar entrada
 * - Convertir DTOs a modelos de dominio
 * - Coordinar algoritmos
 * - Convertir resultados a DTOs de respuesta
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetodoGraficoService {

    private final CalculadorVertices calculadorVertices;

    /**
     * Resuelve un problema de programación lineal usando el método gráfico.
     *
     * @param problemaDTO problema recibido desde el frontend
     * @return solución con punto óptimo, vértices y tipo de solución
     * @throws IllegalArgumentException si el problema no es válido
     */
    public SolucionGraficoDTO resolverProblema(ProblemaGraficoDTO problemaDTO) {
        log.info("Iniciando resolución de problema gráfico");

        // 1. Validar entrada
        validarProblemaDTO(problemaDTO);

        // 2. Convertir DTO a modelo de dominio
        ProblemaGrafico problema = convertirDTOaModelo(problemaDTO);

        // 3. Validar problema de dominio
        if (!problema.esValido()) {
            throw new IllegalArgumentException("El problema no está correctamente formulado");
        }

        // 4. Resolver usando algoritmos
        ResultadoGrafico resultado = calculadorVertices.calcular(problema);

        log.info("Problema resuelto. Tipo de solución: {}", resultado.getTipoSolucion());

        // 5. Convertir resultado a DTO
        return convertirResultadoADTO(resultado);
    }

    /**
     * Valida que el DTO del problema contenga información correcta.
     */
    private void validarProblemaDTO(ProblemaGraficoDTO problemaDTO) {
        if (problemaDTO == null) {
            throw new IllegalArgumentException("El problema no puede ser nulo");
        }

        if (problemaDTO.funcionObjetivo() == null) {
            throw new IllegalArgumentException("La función objetivo es obligatoria");
        }

        if (problemaDTO.restricciones() == null || problemaDTO.restricciones().isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos una restricción");
        }

        // Validar que la función objetivo tenga al menos un coeficiente no cero
        ProblemaGraficoDTO.FuncionObjetivoDTO fo = problemaDTO.funcionObjetivo();
        if (Math.abs(fo.coeficienteX1()) < 1e-10 && Math.abs(fo.coeficienteX2()) < 1e-10) {
            throw new IllegalArgumentException("La función objetivo debe tener al menos un coeficiente diferente de cero");
        }

        // Validar restricciones
        for (int i = 0; i < problemaDTO.restricciones().size(); i++) {
            ProblemaGraficoDTO.RestriccionDTO r = problemaDTO.restricciones().get(i);

            if (Math.abs(r.coeficienteX1()) < 1e-10 && Math.abs(r.coeficienteX2()) < 1e-10) {
                throw new IllegalArgumentException(
                        String.format("La restricción %d debe tener al menos un coeficiente diferente de cero", i + 1)
                );
            }

            if (r.tipo() == null) {
                throw new IllegalArgumentException(
                        String.format("La restricción %d debe tener un tipo de desigualdad", i + 1)
                );
            }
        }
    }

    /**
     * Convierte el DTO recibido del frontend al modelo de dominio.
     */
    private ProblemaGrafico convertirDTOaModelo(ProblemaGraficoDTO dto) {
        // Convertir función objetivo
        FuncionObjetivo funcionObjetivo = FuncionObjetivo.builder()
                .coeficienteX1(dto.funcionObjetivo().coeficienteX1())
                .coeficienteX2(dto.funcionObjetivo().coeficienteX2())
                .tipo(dto.funcionObjetivo().tipo())
                .build();

        // Convertir restricciones
        List<Restriccion> restricciones = dto.restricciones().stream()
                .map(r -> Restriccion.builder()
                        .coeficienteX1(r.coeficienteX1())
                        .coeficienteX2(r.coeficienteX2())
                        .tipo(r.tipo())
                        .ladoDerecho(r.ladoDerecho())
                        .build())
                .collect(Collectors.toList());

        // Si se incluyen restricciones de no negatividad explícitas, filtrarlas
        // ya que el modelo las maneja internamente
        restricciones = filtrarRestriccionesNoNegatividad(restricciones);

        return ProblemaGrafico.builder()
                .funcionObjetivo(funcionObjetivo)
                .restricciones(restricciones)
                .restriccionesNoNegatividad(dto.incluirNoNegatividad())
                .build();
    }

    /**
     * Filtra restricciones de no negatividad explícitas (x1 >= 0, x2 >= 0)
     * ya que el modelo las maneja con el flag restriccionesNoNegatividad.
     */
    private List<Restriccion> filtrarRestriccionesNoNegatividad(List<Restriccion> restricciones) {
        List<Restriccion> filtradas = new ArrayList<>();

        for (Restriccion r : restricciones) {
            // Detectar x1 >= 0 (coef x1 = 1, coef x2 = 0, lado derecho = 0)
            boolean esX1NoNegativa = Math.abs(r.getCoeficienteX1() - 1) < 1e-10 &&
                    Math.abs(r.getCoeficienteX2()) < 1e-10 &&
                    Math.abs(r.getLadoDerecho()) < 1e-10 &&
                    r.getTipo() == TipoDesigualdad.MAYOR_IGUAL;

            // Detectar x2 >= 0
            boolean esX2NoNegativa = Math.abs(r.getCoeficienteX1()) < 1e-10 &&
                    Math.abs(r.getCoeficienteX2() - 1) < 1e-10 &&
                    Math.abs(r.getLadoDerecho()) < 1e-10 &&
                    r.getTipo() == TipoDesigualdad.MAYOR_IGUAL;

            if (!esX1NoNegativa && !esX2NoNegativa) {
                filtradas.add(r);
            }
        }

        return filtradas;
    }

    /**
     * Convierte el resultado del algoritmo a DTO para el frontend.
     */
    private SolucionGraficoDTO convertirResultadoADTO(ResultadoGrafico resultado) {
        // Convertir punto óptimo
        SolucionGraficoDTO.PuntoDTO puntoOptimoDTO = null;
        if (resultado.getPuntoOptimo() != null) {
            puntoOptimoDTO = convertirPuntoADTO(resultado.getPuntoOptimo());
        }

        // Convertir vértices
        List<SolucionGraficoDTO.PuntoDTO> verticesDTO = resultado.getVertices().stream()
                .map(this::convertirPuntoADTO)
                .collect(Collectors.toList());

        // Convertir región factible
        List<SolucionGraficoDTO.PuntoDTO> regionFactibleDTO = resultado.getRegionFactible().stream()
                .map(this::convertirPuntoADTO)
                .collect(Collectors.toList());

        return new SolucionGraficoDTO(
                puntoOptimoDTO,
                verticesDTO,
                regionFactibleDTO,
                resultado.getTipoSolucion()
        );
    }

    /**
     * Convierte un punto del modelo a DTO.
     */
    private SolucionGraficoDTO.PuntoDTO convertirPuntoADTO(Punto punto) {
        return new SolucionGraficoDTO.PuntoDTO(
                punto.getX1(),
                punto.getX2(),
                punto.getValorZ(),
                punto.isEsFactible()
        );
    }

    /**
     * Valida y formatea valores numéricos para evitar problemas de precisión.
     */
    private double formatearValor(double valor) {
        // Redondear a 10 decimales para evitar errores de precisión
        return Math.round(valor * 1e10) / 1e10;
    }
}