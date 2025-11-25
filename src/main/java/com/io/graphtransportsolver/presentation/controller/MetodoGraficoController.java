package com.io.graphtransportsolver.presentation.controller;

import com.io.graphtransportsolver.presentation.dto.grafico.ProblemaGraficoDTO;
import com.io.graphtransportsolver.presentation.dto.grafico.SolucionGraficoDTO;
import com.io.graphtransportsolver.presentation.dto.ApiResponseDTO;
import com.io.graphtransportsolver.services.grafico.MetodoGraficoService;
import com.io.graphtransportsolver.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el método gráfico de programación lineal.
 * Expone endpoints para resolver problemas de PL con 2 variables.
 *
 * @author Duvan Gil
 * @version 1.0
 */
@RestController
@RequestMapping(value = Constants.Global.API_BASE_PATH
        + Constants.Global.API_VERSION
        + Constants.Grafico.GRAFICO_SERVICE_PATH)
@RequiredArgsConstructor
@Slf4j
public class MetodoGraficoController {

    private final MetodoGraficoService metodoGraficoService;

    /**
     * Resuelve un problema de programación lineal usando el método gráfico.
     *
     * @param problemaDTO problema a resolver
     * @return ResponseEntity con ApiResponseDTO conteniendo la solución
     */
    @PostMapping(Constants.Grafico.GRAFICO_SERVICE_PATH_RESOLVE)
    public ResponseEntity<?> resolverProblema(@RequestBody ProblemaGraficoDTO problemaDTO) {
        log.info("=== {} ===", Constants.Message.START_SERVICE);
        log.debug("{}{}", Constants.Message.REQUEST, problemaDTO);

        ApiResponseDTO<SolucionGraficoDTO> result = metodoGraficoService.resolverProblema(problemaDTO);

        log.debug("{}{}", Constants.Message.RESPONSE, result);
        log.info("=== {} ===", Constants.Message.FINISH_SERVICE);

        return new ResponseEntity<>(result, HttpStatusCode.valueOf(result.getStatus()));
    }

}