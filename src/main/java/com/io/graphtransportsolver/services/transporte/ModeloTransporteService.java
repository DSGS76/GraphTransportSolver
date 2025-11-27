package com.io.graphtransportsolver.services.transporte;

import com.io.graphtransportsolver.models.transporte.ProblemaTransporte;
import com.io.graphtransportsolver.models.transporte.SolucionTransporte;
import com.io.graphtransportsolver.presentation.dto.ApiResponseDTO;
import com.io.graphtransportsolver.presentation.dto.transporte.ComparacionMetodosDTO;
import com.io.graphtransportsolver.presentation.dto.transporte.ProblemaTransporteDTO;
import com.io.graphtransportsolver.presentation.dto.transporte.SolucionTransporteDTO;
import com.io.graphtransportsolver.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Servicio principal para resolver problemas de transporte.
 * Coordina la conversión de DTO, validaciones, balanceo y aplicación de algoritmos.
 *
 * @author Duvan Gil
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModeloTransporteService {

    private final SolucionInicialService solucionInicialService;

    /**
     * Resuelve un problema de transporte usando el método especificado.
     *
     * @param problemaDTO problema recibido desde el frontend
     * @return ApiResponseDTO con la solución
     */
    public ApiResponseDTO<SolucionTransporteDTO> resolverProblema(ProblemaTransporteDTO problemaDTO) {
        log.info("{}", Constants.Message.START_SERVICE);
        log.debug("{}{}", Constants.Message.REQUEST, problemaDTO);

        ApiResponseDTO<SolucionTransporteDTO> response = new ApiResponseDTO<>();

        try {
            // 1. Validar entrada básica
            validarEntradaBasica(problemaDTO);

            // 2. Convertir DTO a modelo de dominio
            ProblemaTransporte problema = convertirDTOaModelo(problemaDTO);

            // 3. Resolver usando el método especificado
            SolucionTransporte solucion = solucionInicialService.encontrarSolucionInicial(
                    problema,
                    problemaDTO.metodoInicial()
            );

            log.info("Problema resuelto con método: {}", problemaDTO.metodoInicial());
            log.info("Costo total: {}", solucion.getCostoTotal());

            // 4. Convertir resultado a DTO
            SolucionTransporteDTO solucionDTO = convertirSolucionADTO(
                    solucion,
                    problema,
                    problemaDTO
            );

            // 5. Configurar respuesta exitosa
            response.SuccessOperation(solucionDTO);

            log.debug("{}{}", Constants.Message.RESPONSE, response);
            log.info("{}", Constants.Message.FINISH_SERVICE);

            return response;

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación: {}", e.getMessage());
            response.BadOperation();
            response.setMessage(e.getMessage());
            return response;

        } catch (Exception e) {
            log.error("Error inesperado al resolver problema de transporte", e);
            response.FailedOperation();
            return response;
        }
    }

    /**
     * Compara los tres métodos de solución inicial para el mismo problema.
     *
     * @param problemaDTO problema a resolver
     * @return ApiResponseDTO con la comparación
     */
    public ApiResponseDTO<ComparacionMetodosDTO> compararMetodos(ProblemaTransporteDTO problemaDTO) {
        log.info("{} - Comparación de métodos", Constants.Message.START_SERVICE);
        log.debug("{}{}", Constants.Message.REQUEST, problemaDTO);

        ApiResponseDTO<ComparacionMetodosDTO> response = new ApiResponseDTO<>();

        try {
            // 1. Validar entrada básica
            validarEntradaBasica(problemaDTO);

            // 2. Convertir DTO a modelo de dominio
            ProblemaTransporte problema = convertirDTOaModelo(problemaDTO);

            // 3. Resolver con los tres métodos
            SolucionTransporte[] soluciones = solucionInicialService.compararMetodos(problema);

            log.info("Comparación completada:");
            log.info("  - Esquina Noroeste: Costo = {}", soluciones[0].getCostoTotal());
            log.info("  - Costo Mínimo: Costo = {}", soluciones[1].getCostoTotal());
            log.info("  - Vogel: Costo = {}", soluciones[2].getCostoTotal());

            // 4. Convertir resultados a DTOs
            ComparacionMetodosDTO comparacionDTO = new ComparacionMetodosDTO(
                    convertirSolucionADTO(soluciones[0], problema, problemaDTO),
                    convertirSolucionADTO(soluciones[1], problema, problemaDTO),
                    convertirSolucionADTO(soluciones[2], problema, problemaDTO)
            );

            // 5. Configurar respuesta exitosa
            response.SuccessOperation(comparacionDTO);

            log.debug("{}{}", Constants.Message.RESPONSE, response);
            log.info("{}", Constants.Message.FINISH_SERVICE);

            return response;

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación: {}", e.getMessage());
            response.BadOperation();
            response.setMessage(e.getMessage());
            return response;

        } catch (Exception e) {
            log.error("Error inesperado al comparar métodos", e);
            response.FailedOperation();
            return response;
        }
    }

    /**
     * Válida la entrada básica del DTO.
     */
    private void validarEntradaBasica(ProblemaTransporteDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El problema no puede ser nulo");
        }

        if (dto.costos() == null || dto.costos().length == 0) {
            throw new IllegalArgumentException("La matriz de costos es obligatoria");
        }

        if (dto.ofertas() == null || dto.ofertas().length == 0) {
            throw new IllegalArgumentException("Las ofertas son obligatorias");
        }

        if (dto.demandas() == null || dto.demandas().length == 0) {
            throw new IllegalArgumentException("Las demandas son obligatorias");
        }

        if (dto.metodoInicial() == null) {
            throw new IllegalArgumentException("Debe especificar el método de solución inicial");
        }

        // Validar dimensiones consistentes
        int m = dto.ofertas().length;
        int n = dto.demandas().length;

        if (dto.costos().length != m) {
            throw new IllegalArgumentException(
                    String.format("La matriz de costos debe tener %d filas (orígenes)", m)
            );
        }

        for (int i = 0; i < m; i++) {
            if (dto.costos()[i] == null || dto.costos()[i].length != n) {
                throw new IllegalArgumentException(
                        String.format("La fila %d de costos debe tener %d columnas (destinos)", i, n)
                );
            }
        }

        // Validar nombres si se proporcionan
        if (dto.nombresOrigenes() != null && dto.nombresOrigenes().length != m) {
            throw new IllegalArgumentException(
                    String.format("Debe haber %d nombres de orígenes o ninguno", m)
            );
        }

        if (dto.nombresDestinos() != null && dto.nombresDestinos().length != n) {
            throw new IllegalArgumentException(
                    String.format("Debe haber %d nombres de destinos o ninguno", n)
            );
        }

        log.debug("Validación básica completada exitosamente");
    }

    /**
     * Convierte el DTO recibido del frontend al modelo de dominio.
     */
    private ProblemaTransporte convertirDTOaModelo(ProblemaTransporteDTO dto) {
        log.debug("Convirtiendo DTO a modelo de dominio");

        // Copiar arrays para evitar modificaciones externas
        double[] ofertas = Arrays.copyOf(dto.ofertas(), dto.ofertas().length);
        double[] demandas = Arrays.copyOf(dto.demandas(), dto.demandas().length);

        // Copiar matriz de costos
        double[][] costos = new double[dto.costos().length][];
        for (int i = 0; i < dto.costos().length; i++) {
            costos[i] = Arrays.copyOf(dto.costos()[i], dto.costos()[i].length);
        }

        // Copiar nombres si existen
        String[] nombresOrigenes = dto.nombresOrigenes() != null ?
                Arrays.copyOf(dto.nombresOrigenes(), dto.nombresOrigenes().length) : null;

        String[] nombresDestinos = dto.nombresDestinos() != null ?
                Arrays.copyOf(dto.nombresDestinos(), dto.nombresDestinos().length) : null;

        ProblemaTransporte problema = ProblemaTransporte.builder()
                .ofertas(ofertas)
                .demandas(demandas)
                .costos(costos)
                .nombresOrigenes(nombresOrigenes)
                .nombresDestinos(nombresDestinos)
                .tieneFicticio(false)
                .build();

        log.debug("Problema convertido: {} orígenes, {} destinos",
                problema.getOfertas().length, problema.getDemandas().length);
        log.debug("Tipo de balance: {}", problema.getTipoBalance());

        return problema;
    }

    /**
     * Convierte la solución del modelo de dominio a DTO para el frontend.
     */
    private SolucionTransporteDTO convertirSolucionADTO(
            SolucionTransporte solucion,
            ProblemaTransporte problemaBalanceado,
            ProblemaTransporteDTO problemaOriginal) {

        log.debug("Convirtiendo solución a DTO");

        // Usar nombres del problema original si están disponibles, o del balanceado
        String[] nombresOrigenes = problemaOriginal.nombresOrigenes() != null ?
                problemaOriginal.nombresOrigenes() :
                problemaBalanceado.getNombresOrigenes();

        String[] nombresDestinos = problemaOriginal.nombresDestinos() != null ?
                problemaOriginal.nombresDestinos() :
                problemaBalanceado.getNombresDestinos();

        return new SolucionTransporteDTO(
                solucion.getAsignaciones(),
                solucion.getCostoTotal(),
                solucion.getMetodoUtilizado(),
                problemaBalanceado.isTieneFicticio(),
                problemaBalanceado.getTipoBalance(),
                nombresOrigenes,
                nombresDestinos
        );
    }
}
