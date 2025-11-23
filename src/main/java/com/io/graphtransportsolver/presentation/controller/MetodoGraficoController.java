package com.io.graphtransportsolver.presentation.controller;

import com.io.graphtransportsolver.presentation.dto.grafico.ProblemaGraficoDTO;
import com.io.graphtransportsolver.presentation.dto.grafico.SolucionGraficoDTO;
import com.io.graphtransportsolver.presentation.dto.ApiResponseDTO;
import com.io.graphtransportsolver.services.grafico.MetodoGraficoService;
import com.io.graphtransportsolver.utils.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el método gráfico de programación lineal.
 * Expone endpoints para resolver problemas de PL con 2 variables.
 */
@RestController
@RequestMapping(value = Constants.Global.API_BASE_PATH
        + Constants.Global.API_VERSION
        + Constants.Grafico.GRAFICO_SERVICE_PATH)
@RequiredArgsConstructor
@Slf4j
public class MetodoGraficoController {

    private final MetodoGraficoService metodoGraficoService;


}