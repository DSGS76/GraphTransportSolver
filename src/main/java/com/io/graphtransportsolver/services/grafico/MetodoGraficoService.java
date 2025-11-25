package com.io.graphtransportsolver.services.grafico;

import com.io.graphtransportsolver.algoritmos.grafico.CalculadorVertices;
import com.io.graphtransportsolver.models.grafico.*;
import com.io.graphtransportsolver.models.grafico.enums.TipoDesigualdad;
import com.io.graphtransportsolver.presentation.dto.ApiResponseDTO;
import com.io.graphtransportsolver.presentation.dto.grafico.ProblemaGraficoDTO;
import com.io.graphtransportsolver.presentation.dto.grafico.SolucionGraficoDTO;
import com.io.graphtransportsolver.utils.Constants;
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
 * - Validar entrada básica (nulls)
 * - Convertir DTOs a modelos de dominio
 * - Coordinar algoritmos
 * - Convertir resultados a DTOs de respuesta
 *
 * La validación de negocio (coeficientes válidos) se delega a los modelos de dominio.
 *
 * @author Duvan Gil
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetodoGraficoService {

    private static final double EPSILON = 1e-10;

    private final CalculadorVertices calculadorVertices;

    /**
     * Resuelve un problema de programación lineal usando el método gráfico.
     *
     * @param problemaDTO problema recibido desde el frontend
     * @return ApiResponseDTO con la solución o información de error
     */
    public ApiResponseDTO<SolucionGraficoDTO> resolverProblema(ProblemaGraficoDTO problemaDTO) {
        log.info("{}", Constants.Message.START_SERVICE);
        log.debug("{}{}", Constants.Message.REQUEST, problemaDTO);

        ApiResponseDTO<SolucionGraficoDTO> response = new ApiResponseDTO<>();

        try {
            // 1. Validar entrada básica (nulls y vacíos)
            validarEntradaBasica(problemaDTO);

            // 2. Convertir DTO a modelo de dominio
            ProblemaGrafico problema = convertirDTOaModelo(problemaDTO);

            // 3. Validar problema de dominio (usa esValido() del modelo)
            if (!problema.esValido()) {
                log.warn("El problema no está correctamente formulado");
                response.BadOperation();
                response.setMessage("El problema no está correctamente formulado");
                return response;
            }

            // 4. Resolver usando algoritmos
            ResultadoGrafico resultado = calculadorVertices.calcular(problema);

            log.info("Problema resuelto. Tipo de solución: {}", resultado.getTipoSolucion());

            // 5. Convertir resultado a DTO (incluye restricciones para graficar)
            SolucionGraficoDTO solucionDTO = convertirResultadoADTO(resultado, problema);

            // 6. Configurar respuesta exitosa
            response.SuccessOperation(solucionDTO);

            log.debug("{}{}", Constants.Message.RESPONSE, response);
            log.info("{}", Constants.Message.FINISH_SERVICE);

            return response;

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación: {}", e.getMessage());
            response.BadOperation();
            response.setMessage(e.getMessage());
            return response;

        } catch (ArithmeticException e) {
            log.error("Error aritmético: {}", e.getMessage());
            response.BadOperation();
            response.setMessage(e.getMessage());
            return response;

        } catch (Exception e) {
            log.error("Error inesperado al resolver problema gráfico", e);
            response.FailedOperation();
            return response;
        }
    }

    /**
     * Válida la entrada básica del DTO (nulls y colecciones vacías).
     * La validación de negocio (coeficientes válidos) se hace en el modelo de dominio.
     *
     * @throws IllegalArgumentException sí hay errores de validación básica
     */
    private void validarEntradaBasica(ProblemaGraficoDTO problemaDTO) {
        if (problemaDTO == null) {
            throw new IllegalArgumentException("El problema no puede ser nulo");
        }

        if (problemaDTO.funcionObjetivo() == null) {
            throw new IllegalArgumentException("La función objetivo es obligatoria");
        }

        if (problemaDTO.restricciones() == null || problemaDTO.restricciones().isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos una restricción");
        }

        if (problemaDTO.funcionObjetivo().tipo() == null) {
            throw new IllegalArgumentException("Debe especificar el tipo de optimización");
        }

        // Validar que las restricciones tengan tipo
        for (int i = 0; i < problemaDTO.restricciones().size(); i++) {
            ProblemaGraficoDTO.RestriccionDTO r = problemaDTO.restricciones().get(i);
            if (r.tipo() == null) {
                throw new IllegalArgumentException(
                        String.format("La restricción %d debe tener un tipo de desigualdad", i + 1)
                );
            }
        }

        log.debug("Validación básica completada exitosamente");
    }

    /**
     * Convierte el DTO recibido del frontend al modelo de dominio.
     */
    private ProblemaGrafico convertirDTOaModelo(ProblemaGraficoDTO dto) {
        log.debug("Convirtiendo DTO a modelo de dominio");

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

        // Filtrar restricciones de no negatividad explícita
        restricciones = filtrarRestriccionesNoNegatividad(restricciones);

        log.debug("Problema convertido: {} restricciones, FO tipo {}",
                restricciones.size(), funcionObjetivo.getTipo());

        return ProblemaGrafico.builder()
                .funcionObjetivo(funcionObjetivo)
                .restricciones(restricciones)
                .restriccionesNoNegatividad(dto.incluirNoNegatividad())
                .build();
    }

    /**
     * Filtra restricciones de no negatividad explícita (x1 >= 0, x2 >= 0),
     * ya que el modelo las maneja con el flag restriccionesNoNegatividad.
     */
    private List<Restriccion> filtrarRestriccionesNoNegatividad(List<Restriccion> restricciones) {
        List<Restriccion> filtradas = new ArrayList<>();

        for (Restriccion r : restricciones) {
            // Detectar x1 >= 0 (coef x1 = 1, coef x2 = 0, lado derecho = 0)
            boolean esX1NoNegativa = Math.abs(r.getCoeficienteX1() - 1) < EPSILON &&
                    Math.abs(r.getCoeficienteX2()) < EPSILON &&
                    Math.abs(r.getLadoDerecho()) < EPSILON &&
                    r.getTipo() == TipoDesigualdad.MAYOR_IGUAL;

            // Detectar x2 >= 0
            boolean esX2NoNegativa = Math.abs(r.getCoeficienteX1()) < EPSILON &&
                    Math.abs(r.getCoeficienteX2() - 1) < EPSILON &&
                    Math.abs(r.getLadoDerecho()) < EPSILON &&
                    r.getTipo() == TipoDesigualdad.MAYOR_IGUAL;

            if (!esX1NoNegativa && !esX2NoNegativa) {
                filtradas.add(r);
            }
        }

        return filtradas;
    }

    /**
     * Convierte el resultado del algoritmo a DTO para el frontend.
     * Usa las restricciones del modelo ResultadoGrafico.
     */
    private SolucionGraficoDTO convertirResultadoADTO(ResultadoGrafico resultado, ProblemaGrafico problema) {
        log.debug("Convirtiendo resultado a DTO");

        // Convertir punto óptimo (usa método del modelo si existe)
        SolucionGraficoDTO.PuntoDTO puntoOptimoDTO = null;
        if (resultado.tieneSolucion()) {
            puntoOptimoDTO = convertirPuntoADTO(resultado.getPuntoOptimo());
        }

        // Convertir vértices
        List<SolucionGraficoDTO.PuntoDTO> verticesDTO = resultado.getVertices().stream()
                .map(this::convertirPuntoADTO)
                .collect(Collectors.toList());

        // Convertir región factible (ya viene ordenada del algoritmo)
        List<SolucionGraficoDTO.PuntoDTO> regionFactibleDTO = resultado.getRegionFactible().stream()
                .map(this::convertirPuntoADTO)
                .collect(Collectors.toList());

        // Convertir restricciones desde el modelo
        List<SolucionGraficoDTO.RestriccionDTO> restriccionesDTO = resultado.getRestricciones().stream()
                .map(r -> new SolucionGraficoDTO.RestriccionDTO(
                        r.getCoeficienteX1(),
                        r.getCoeficienteX2(),
                        r.getLadoDerecho(),
                        r.getTipo().name()
                ))
                .collect(Collectors.toList());


        return new SolucionGraficoDTO(
                puntoOptimoDTO,
                verticesDTO,
                regionFactibleDTO,
                restriccionesDTO,
                resultado.getTipoSolucion()
        );
    }

    /**
     * Convierte un punto del modelo a DTO.
     */
    private SolucionGraficoDTO.PuntoDTO convertirPuntoADTO(Punto punto) {
        return new SolucionGraficoDTO.PuntoDTO(
                formatearValor(punto.getX1()),
                formatearValor(punto.getX2()),
                formatearValor(punto.getValorZ()),
                punto.isEsFactible()
        );
    }

    /**
     * Formatea valores numéricos para evitar problemas de precisión.
     */
    private double formatearValor(double valor) {
        // Redondear a 10 decimales para evitar errores de precisión
        return Math.round(valor * 1e10) / 1e10;
    }
}