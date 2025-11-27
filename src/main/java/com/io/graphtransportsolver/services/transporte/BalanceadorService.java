package com.io.graphtransportsolver.services.transporte;

import com.io.graphtransportsolver.models.transporte.ProblemaTransporte;
import com.io.graphtransportsolver.models.transporte.enums.TipoBalance;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Servicio para balancear problemas de transporte.
 * Agrega orígenes o destinos ficticios cuando hay desbalance.
 */
@Service
public class BalanceadorService {

    private static final double COSTO_FICTICIO = 0.0;

    /**
     * Balancea un problema de transporte agregando origen o destino ficticio si es necesario.
     *
     * @param problema el problema a balancear
     * @return el problema balanceado
     */
    public ProblemaTransporte balancear(ProblemaTransporte problema) {
        if (problema == null) {
            throw new IllegalArgumentException("El problema no puede ser nulo");
        }

        if (problema.esBalanceado()) {
            return problema; // Ya está balanceado
        }

        TipoBalance tipoBalance = problema.getTipoBalance();

        if (tipoBalance == TipoBalance.EXCESO_OFERTA) {
            return agregarDestinoFicticio(problema);
        } else {
            return agregarOrigenFicticio(problema);
        }
    }

    /**
     * Agrega un destino ficticio para absorber el exceso de oferta.
     */
    private ProblemaTransporte agregarDestinoFicticio(ProblemaTransporte problema) {
        double exceso = problema.calcularDesbalance();
        int m = problema.getOfertas().length;
        int n = problema.getDemandas().length;

        // Copiar demandas y agregar el destino ficticio
        double[] nuevasDemandas = Arrays.copyOf(problema.getDemandas(), n + 1);
        nuevasDemandas[n] = exceso;

        // Copiar nombres de destinos si existen
        String[] nuevosNombresDestinos = null;
        if (problema.getNombresDestinos() != null) {
            nuevosNombresDestinos = Arrays.copyOf(problema.getNombresDestinos(), n + 1);
            nuevosNombresDestinos[n] = "Ficticio";
        }

        // Crear nueva matriz de costos con columna adicional
        double[][] nuevosCostos = new double[m][n + 1];
        for (int i = 0; i < m; i++) {
            System.arraycopy(problema.getCostos()[i], 0, nuevosCostos[i], 0, n);
            nuevosCostos[i][n] = COSTO_FICTICIO;
        }

        return ProblemaTransporte.builder()
                .ofertas(Arrays.copyOf(problema.getOfertas(), m))
                .demandas(nuevasDemandas)
                .costos(nuevosCostos)
                .nombresOrigenes(problema.getNombresOrigenes() != null ?
                        Arrays.copyOf(problema.getNombresOrigenes(), m) : null)
                .nombresDestinos(nuevosNombresDestinos)
                .tieneFicticio(true)
                .build();
    }

    /**
     * Agrega un origen ficticio para suplir el déficit de oferta.
     */
    private ProblemaTransporte agregarOrigenFicticio(ProblemaTransporte problema) {
        double deficit = -problema.calcularDesbalance(); // Negativo porque es exceso de demanda
        int m = problema.getOfertas().length;
        int n = problema.getDemandas().length;

        // Copiar ofertas y agregar el origen ficticio
        double[] nuevasOfertas = Arrays.copyOf(problema.getOfertas(), m + 1);
        nuevasOfertas[m] = deficit;

        // Copiar nombres de orígenes si existen
        String[] nuevosNombresOrigenes = null;
        if (problema.getNombresOrigenes() != null) {
            nuevosNombresOrigenes = Arrays.copyOf(problema.getNombresOrigenes(), m + 1);
            nuevosNombresOrigenes[m] = "Ficticio";
        }

        // Crear nueva matriz de costos con fila adicional
        double[][] nuevosCostos = new double[m + 1][n];
        for (int i = 0; i < m; i++) {
            System.arraycopy(problema.getCostos()[i], 0, nuevosCostos[i], 0, n);
        }
        // Llenar la fila ficticia con costo 0
        Arrays.fill(nuevosCostos[m], COSTO_FICTICIO);

        return ProblemaTransporte.builder()
                .ofertas(nuevasOfertas)
                .demandas(Arrays.copyOf(problema.getDemandas(), n))
                .costos(nuevosCostos)
                .nombresOrigenes(nuevosNombresOrigenes)
                .nombresDestinos(problema.getNombresDestinos() != null ?
                        Arrays.copyOf(problema.getNombresDestinos(), n) : null)
                .tieneFicticio(true)
                .build();
    }
}
