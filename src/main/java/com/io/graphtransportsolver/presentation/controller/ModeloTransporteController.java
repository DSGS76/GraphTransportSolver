package com.io.graphtransportsolver.presentation.controller;

import com.io.graphtransportsolver.presentation.dto.ApiResponseDTO;
import com.io.graphtransportsolver.presentation.dto.transporte.ComparacionMetodosDTO;
import com.io.graphtransportsolver.presentation.dto.transporte.ProblemaTransporteDTO;
import com.io.graphtransportsolver.presentation.dto.transporte.SolucionTransporteDTO;
import com.io.graphtransportsolver.services.transporte.ModeloTransporteService;
import com.io.graphtransportsolver.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el modelo de transporte.
 * Expone endpoints para resolver problemas de transporte con diferentes métodos.
 *
 * @author Duvan Gil
 * @version 1.0
 */
@RestController
@RequestMapping(Constants.Global.API_BASE_PATH + Constants.Global.API_VERSION + Constants.Transporte.TRANSPORTE_SERVICE_PATH)
@RequiredArgsConstructor
@Slf4j
public class ModeloTransporteController {

    private final ModeloTransporteService modeloTransporteService;

    /**
     * Resuelve un problema de transporte usando el método especificado.
     *
     * @param problemaDTO datos del problema
     * @return ApiResponseDTO con la solución
     */
    @PostMapping(Constants.Transporte.TRANSPORTE_SERVICE_PATH_RESOLVE)
    public ResponseEntity<?> resolverProblema(@RequestBody ProblemaTransporteDTO problemaDTO) {
        log.info("=== {} ===", Constants.Message.START_SERVICE);
        log.debug("{}{}", Constants.Message.REQUEST, problemaDTO);

        ApiResponseDTO<SolucionTransporteDTO> result = modeloTransporteService.resolverProblema(problemaDTO);

        log.debug("{}{}", Constants.Message.RESPONSE, result);
        log.info("=== {} ===", Constants.Message.FINISH_SERVICE);

        return new ResponseEntity<>(result, HttpStatusCode.valueOf(result.getStatus()));
    }

    /**
     * Compara los tres métodos de solución inicial para el mismo problema.
     *
     * @param problemaDTO datos del problema
     * @return ApiResponseDTO con la comparación de los tres métodos
     */
    @PostMapping(Constants.Transporte.TRANSPORTE_SERVICE_PATH_COMPARE)
    public ResponseEntity<?> compararMetodos(@RequestBody ProblemaTransporteDTO problemaDTO) {
        log.info("=== {} ===", Constants.Message.START_SERVICE);
        log.debug("{}{}", Constants.Message.REQUEST, problemaDTO);

        ApiResponseDTO<ComparacionMetodosDTO> response = modeloTransporteService.compararMetodos(problemaDTO);

        log.debug("{}{}", Constants.Message.RESPONSE, response);
        log.info("=== {} ===", Constants.Message.FINISH_SERVICE);

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }
}