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
 * - Validar lógica de negocio
 * - Convertir DTOs a modelos de dominio
 * - Coordinar algoritmos
 * - Convertir resultados a DTOs de respuesta
 *
 * Nota: Las validaciones estructurales (nulls, tipos) son manejadas
 * por Jakarta Validation en el controller.
 *
 * @author Duvan Gil
 * @version 1.0
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
     * @return ApiResponseDTO con la solución o información de error
     */
    public ApiResponseDTO<SolucionGraficoDTO> resolverProblema(ProblemaGraficoDTO problemaDTO) {
        log.info("{}", Constants.Message.START_SERVICE);
        log.debug("{}{}", Constants.Message.REQUEST, problemaDTO);

        ApiResponseDTO<SolucionGraficoDTO> response = new ApiResponseDTO<>();

        try {
            // 1. Validar lógica de negocio
            validarLogicaNegocio(problemaDTO);

            // 2. Convertir DTO a modelo de dominio
            ProblemaGrafico problema = convertirDTOaModelo(problemaDTO);

            // 3. Validar problema de dominio
            if (!problema.esValido()) {
                log.warn("El problema no está correctamente formulado");
                response.BadOperation();
                return response;
            }

            // 4. Resolver usando algoritmos
            ResultadoGrafico resultado = calculadorVertices.calcular(problema);

            log.info("Problema resuelto. Tipo de solución: {}", resultado.getTipoSolucion());

            // 5. Convertir resultado a DTO
            SolucionGraficoDTO solucionDTO = convertirResultadoADTO(resultado);

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
     * Válida reglas de lógica de negocio del problema.
     * Las validaciones estructurales (nulls, tipos) son manejadas por Jakarta Validation.
     *
     * @param problemaDTO problema a validar
     * @throws IllegalArgumentException sí hay errores de lógica de negocio
     */
    private void validarLogicaNegocio(ProblemaGraficoDTO problemaDTO) {
        // Validar que la función objetivo tenga al menos un coeficiente no cero
        ProblemaGraficoDTO.FuncionObjetivoDTO fo = problemaDTO.funcionObjetivo();
        if (Math.abs(fo.coeficienteX1()) < 1e-10 && Math.abs(fo.coeficienteX2()) < 1e-10) {
            throw new IllegalArgumentException("La función objetivo debe tener al menos un coeficiente diferente de cero");
        }

        // Validar que cada restricción tenga al menos un coeficiente no cero
        for (int i = 0; i < problemaDTO.restricciones().size(); i++) {
            ProblemaGraficoDTO.RestriccionDTO r = problemaDTO.restricciones().get(i);

            if (Math.abs(r.coeficienteX1()) < 1e-10 && Math.abs(r.coeficienteX2()) < 1e-10) {
                throw new IllegalArgumentException(
                        String.format("La restricción %d debe tener al menos un coeficiente diferente de cero", i + 1)
                );
            }
        }

        log.debug("Validación de lógica de negocio completada exitosamente");
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
        log.debug("Convirtiendo resultado a DTO");

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