package com.io.graphtransportsolver.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de vistas para la gestión y visualización de grafos.
 * Permite acceder a la página principal y a la vista de creación de grafos.
 *
 * @author Duvan Gil
 * @version 1.0
 */
@Controller
public class ViewController {

    /**
     * Muestra la página principal de la aplicación.
     *
     * @param model Modelo de Spring MVC
     * @return Nombre de la plantilla index.html
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("page", "index");
        return "index";
    }

    /**
     * Muestra la vista del método gráfico.
     *
     * @param model Modelo de Spring MVC
     * @return Nombre de la plantilla grafico/grafico.html
     */
    @GetMapping("/grafico")
    public String metodoGrafico(Model model) {
        model.addAttribute("page", "grafico");
        model.addAttribute("title", "Método Gráfico - Graph Transport Solver");
        return "grafico/grafico";
    }

    /**
     * Muestra la vista del modelo de transporte.
     *
     * @param model Modelo de Spring MVC
     * @return Nombre de la plantilla transporte/transporte.html
     */
    @GetMapping("/transporte")
    public String transporte(Model model) {
        model.addAttribute("page", "transporte");
        model.addAttribute("title", "Modelo de Transporte - Graph Transport Solver");
        return "transporte/transporte";
    }

}
