/**
 * Módulo para renderizar los resultados del modelo de transporte.
 * Maneja la visualización de matrices, costos y comparaciones.
 *
 * @author Duvan Gil
 * @version 1.0
 */
const TransporteRenderer = (() => {

    /**
     * Renderiza la solución completa
     * @param {Object} solucion - SolucionTransporteDTO
     */
    const renderSolucion = (solucion) => {
        console.log('🎨 Renderizando solución...');
        console.log('📊 Datos de la solución:', {
            seBalanceo: solucion.seBalanceo,
            tipoBalance: solucion.tipoBalance
        });

        // Mostrar sección de resultados
        const section = document.getElementById('resultadosSection');
        section.style.display = 'block';

        // Ocultar comparación si estaba visible
        document.getElementById('comparacionSection').style.display = 'none';

        // Scroll suave a resultados
        setTimeout(() => {
            section.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }, 100);

        // Renderizar componentes
        renderInfoSolucion(solucion);
        renderMatrizAsignaciones(solucion);
        renderBalanceInfo(solucion);

        // ✅ Mostrar notificación si el problema fue balanceado
        if (solucion.seBalanceo === true) {
            const tipoBalanceoTexto = solucion.tipoBalance === 'EXCESO_OFERTA'
                ? 'Se agregó un destino ficticio para absorber el exceso de oferta'
                : 'Se agregó un origen ficticio para suplir el déficit de oferta';

            if (typeof Notificaciones !== 'undefined') {
                setTimeout(() => {
                    Notificaciones.show(
                        `⚠️ Problema balanceado: ${tipoBalanceoTexto}`,
                        'warning'
                    );
                }, 800);
            }
        }

        console.log('✅ Solución renderizada correctamente');
    };

    /**
     * Renderiza la información de la solución
     */
    const renderInfoSolucion = (solucion) => {
        const container = document.getElementById('solucionInfo');

        const nombreMetodo = getNombreMetodo(solucion.metodoUtilizado);

        container.innerHTML = `
            <div class="solution-header">
                <div class="solution-badge">
                    ✅ Solución Encontrada
                </div>
            </div>
            
            <div class="solution-cost">
                <div class="cost-display">
                    <div class="cost-label">Costo Total de Transporte</div>
                    <div class="cost-value">
                        <span class="cost-currency">$</span>${formatNumber(solucion.costoTotal)}
                    </div>
                    <div class="method-badge">
                        Método: ${nombreMetodo}
                    </div>
                </div>
            </div>
        `;
    };

    /**
     * Renderiza la matriz de asignaciones
     */
    const renderMatrizAsignaciones = (solucion) => {
        const container = document.getElementById('asignacionesContainer');

        const asignaciones = solucion.asignaciones;
        const m = asignaciones.length;
        const n = asignaciones[0].length;

        // Calcular totales de filas y columnas
        const totalesFilas = asignaciones.map(fila =>
            fila.reduce((sum, val) => sum + val, 0)
        );

        const totalesColumnas = [];
        for (let j = 0; j < n; j++) {
            let suma = 0;
            for (let i = 0; i < m; i++) {
                suma += asignaciones[i][j];
            }
            totalesColumnas.push(suma);
        }

        let html = `
            <div class="assignments-header">
                <h3 class="assignments-title">
                    <span>📦</span>
                    Matriz de Asignaciones
                </h3>
            </div>
            <div class="assignments-table-wrapper">
                <table class="assignments-table">
                    <thead>
                        <tr>
                            <th>De \\ A</th>
        `;

        // Encabezados de columnas (destinos)
        for (let j = 0; j < n; j++) {
            const nombreDestino = (solucion.nombresDestinos && solucion.nombresDestinos[j])
                ? solucion.nombresDestinos[j]
                : `D${j + 1}`;
            html += `<th>${nombreDestino}</th>`;
        }
        html += `<th>Oferta</th></tr></thead><tbody>`;

        // Filas (orígenes)
        for (let i = 0; i < m; i++) {
            const nombreOrigen = (solucion.nombresOrigenes && solucion.nombresOrigenes[i])
                ? solucion.nombresOrigenes[i]
                : `O${i + 1}`;

            html += `<tr><td class="supply-cell">${nombreOrigen}</td>`;

            for (let j = 0; j < n; j++) {
                const valor = asignaciones[i][j];
                const esBasica = Math.abs(valor) > 1e-6;
                const claseCell = esBasica ? 'cell-basic' : 'cell-zero';
                const valorMostrar = esBasica ? formatNumber(valor) : '—';

                html += `<td class="${claseCell}">${valorMostrar}</td>`;
            }

            html += `<td class="supply-cell">${formatNumber(totalesFilas[i])}</td></tr>`;
        }

        // Fila de totales (demandas)
        html += '<tr><td class="demand-cell">Demanda</td>';
        for (let j = 0; j < n; j++) {
            html += `<td class="demand-cell">${formatNumber(totalesColumnas[j])}</td>`;
        }

        const totalGeneral = totalesColumnas.reduce((a, b) => a + b, 0);
        html += `<td class="demand-cell">${formatNumber(totalGeneral)}</td></tr>`;

        html += '</tbody></table></div>';

        container.innerHTML = html;
    };

    /**
     * Renderiza la información de balance
     */
    const renderBalanceInfo = (solucion) => {
        const container = document.getElementById('balanceInfo');

        const asignaciones = solucion.asignaciones;
        const m = asignaciones.length;
        const n = asignaciones[0].length;

        // Calcular ofertas y demandas totales
        let ofertaTotal = 0;
        for (let i = 0; i < m; i++) {
            for (let j = 0; j < n; j++) {
                ofertaTotal += asignaciones[i][j];
            }
        }

        const tipoBalance = solucion.tipoBalance || 'BALANCEADO';
        const seBalanceo = solucion.seBalanceo || false;

        const badgeClass = getBadgeClass(tipoBalance);
        const textoBalance = getTextoBalance(tipoBalance);

        let html = `
            <div class="balance-header">
                <span class="balance-icon">⚖️</span>
                <h3 class="balance-title">Balance del Problema</h3>
            </div>
            
            <div class="balance-details">
                <div class="balance-item">
                    <div class="balance-item-label">Celdas Básicas</div>
                    <div class="balance-item-value">${contarCeldasBasicas(asignaciones)}</div>
                </div>
                
                <div class="balance-item">
                    <div class="balance-item-label">Celdas Esperadas</div>
                    <div class="balance-item-value">${m + n - 1}</div>
                </div>
                
                <div class="balance-item">
                    <div class="balance-item-label">Total Transportado</div>
                    <div class="balance-item-value">${formatNumber(ofertaTotal)}</div>
                </div>
            </div>
            
            <div style="text-align: center; margin-top: 1rem;">
                <span class="balance-badge ${badgeClass}">
                    ${textoBalance}
                </span>
        `;

        if (seBalanceo === true) {
            // Determinar el tipo de balanceo
            const tipoBalanceoTexto = tipoBalance === 'EXCESO_OFERTA'
                ? 'Se agregó un <strong>destino ficticio</strong> para absorber el exceso de oferta'
                : 'Se agregó un <strong>origen ficticio</strong> para suplir el déficit de oferta';

            html += `
                <div class="balance-warning">
                    <div class="balance-warning-content">
                        <span class="balance-warning-icon">⚠️</span>
                        <div class="balance-warning-text">
                            <div class="balance-warning-title">
                                Problema Balanceado Automáticamente
                            </div>
                            <div class="balance-warning-description">
                                ${tipoBalanceoTexto}
                            </div>
                        </div>
                    </div>
                </div>
            `;
        }

        html += '</div>';

        container.innerHTML = html;
    };

    /**
     * Renderiza la comparación de métodos
     */
    const renderComparacion = (comparacion) => {
        console.log('🎨 Renderizando comparación...');
        console.log('📊 Datos de comparación:', {
            seBalanceo: comparacion.esquinaNoroeste.seBalanceo,
            tipoBalance: comparacion.esquinaNoroeste.tipoBalance
        });

        // Ocultar resultados individuales
        document.getElementById('resultadosSection').style.display = 'none';

        // Mostrar sección de comparación
        const section = document.getElementById('comparacionSection');
        section.style.display = 'block';

        // Scroll suave
        setTimeout(() => {
            section.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }, 100);

        // ✅ Mostrar notificación si el problema fue balanceado (usar datos de cualquier método, son iguales)
        if (comparacion.esquinaNoroeste.seBalanceo === true) {
            const tipoBalanceoTexto = comparacion.esquinaNoroeste.tipoBalance === 'EXCESO_OFERTA'
                ? 'Se agregó un destino ficticio para absorber el exceso de oferta'
                : 'Se agregó un origen ficticio para suplir el déficit de oferta';

            if (typeof Notificaciones !== 'undefined') {
                setTimeout(() => {
                    Notificaciones.show(
                        `⚠️ Problema balanceado: ${tipoBalanceoTexto}`,
                        'warning'
                    );
                }, 800);
            }
        }

        // Determinar cuál es el mejor (menor costo)
        const costos = [
            comparacion.esquinaNoroeste.costoTotal,
            comparacion.costoMinimo.costoTotal,
            comparacion.vogel.costoTotal
        ];

        const costoMinimo = Math.min(...costos);

        const container = document.getElementById('comparacionContainer');

        container.innerHTML = `
            <div class="comparison-summary">
                <h3 style="text-align: center; color: #2c3e50; margin-bottom: 2rem;">
                    📊 Comparación de los Tres Métodos de Solución Inicial
                </h3>
            </div>
            
            <div class="comparison-grid">
                ${renderCardComparacion('Esquina Noroeste', comparacion.esquinaNoroeste, costoMinimo)}
                ${renderCardComparacion('Costo Mínimo', comparacion.costoMinimo, costoMinimo)}
                ${renderCardComparacion('Vogel (VAM)', comparacion.vogel, costoMinimo)}
            </div>
        `;

        console.log('✅ Comparación renderizada');
    };

    /**
     * Renderiza una card de comparación individual
     */
    const renderCardComparacion = (nombre, solucion, costoMinimo) => {
        const esMejor = Math.abs(solucion.costoTotal - costoMinimo) < 1e-6;
        const classCard = esMejor ? 'comparison-card comparison-card-best' : 'comparison-card';

        let html = `
            <div class="${classCard}">
                <h4 class="comparison-method-name">${nombre}</h4>
                
                <div class="comparison-cost">
                    <div class="comparison-cost-label">Costo Total</div>
                    <div class="comparison-cost-value">
                        $${formatNumber(solucion.costoTotal)}
                    </div>
                </div>
        `;

        if (esMejor) {
            html += `
                <div style="text-align: center;">
                    <span class="comparison-best-badge">
                        ⭐ Mejor Solución
                    </span>
                </div>
            `;
        }

        // Mostrar matriz de asignaciones reducida
        html += `
            <div class="comparison-table-container">
                ${renderMatrizComparacion(solucion)}
            </div>
        `;

        html += '</div>';
        return html;
    };

    /**
     * Renderiza una matriz de asignaciones compacta
     */
    const renderMatrizComparacion = (solucion) => {
        const asignaciones = solucion.asignaciones;
        const m = asignaciones.length;
        const n = asignaciones[0].length;

        let html = '<table class="costs-table" style="font-size: 0.85rem;"><thead><tr><th>De\\A</th>';

        // ✅ Usar nombres de destinos con detección de ficticios
        for (let j = 0; j < n; j++) {
            let nombreDestino = (solucion.nombresDestinos && solucion.nombresDestinos[j])
                ? solucion.nombresDestinos[j]
                : `D${j + 1}`;

            // Detectar si es el último destino y el problema está balanceado (ficticio)
            const esFicticio = solucion.seBalanceo && j === n - 1 &&
                               (solucion.tipoBalance === 'EXCESO_OFERTA');

            if (esFicticio) {
                nombreDestino = `${nombreDestino}`;
            }

            html += `<th>${nombreDestino}</th>`;
        }
        html += '</tr></thead><tbody>';

        // ✅ Usar nombres de orígenes con detección de ficticios
        for (let i = 0; i < m; i++) {
            let nombreOrigen = (solucion.nombresOrigenes && solucion.nombresOrigenes[i])
                ? solucion.nombresOrigenes[i]
                : `O${i + 1}`;

            // Detectar si es el último origen y el problema está balanceado (ficticio)
            const esFicticio = solucion.seBalanceo && i === m - 1 &&
                               (solucion.tipoBalance === 'EXCESO_DEMANDA');

            if (esFicticio) {
                nombreOrigen = `${nombreOrigen}`;
            }

            html += `<tr><td class="row-header">${nombreOrigen}</td>`;

            for (let j = 0; j < n; j++) {
                const valor = asignaciones[i][j];
                const esBasica = Math.abs(valor) > 1e-6;
                const claseCell = esBasica ? 'cell-basic' : 'cell-zero';
                const valorMostrar = esBasica ? formatNumber(valor) : '—';

                html += `<td class="${claseCell}">${valorMostrar}</td>`;
            }

            html += '</tr>';
        }

        html += '</tbody></table>';
        return html;
    };

    // ===== FUNCIONES AUXILIARES =====

    /**
     * Formatea un número con separadores de miles
     */
    const formatNumber = (num) => {
        return num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    };

    /**
     * Cuenta las celdas básicas (asignaciones > 0)
     */
    const contarCeldasBasicas = (asignaciones) => {
        let count = 0;
        for (let i = 0; i < asignaciones.length; i++) {
            for (let j = 0; j < asignaciones[i].length; j++) {
                if (Math.abs(asignaciones[i][j]) > 1e-6) {
                    count++;
                }
            }
        }
        return count;
    };

    /**
     * Obtiene el nombre legible del método
     */
    const getNombreMetodo = (metodo) => {
        const nombres = {
            'ESQUINA_NOROESTE': 'Esquina Noroeste',
            'COSTO_MINIMO': 'Costo Mínimo',
            'VOGEL': 'Vogel (VAM)'
        };
        return nombres[metodo] || metodo;
    };

    /**
     * Obtiene la clase CSS del badge de balance
     */
    const getBadgeClass = (tipoBalance) => {
        const clases = {
            'BALANCEADO': 'balance-badge-balanced',
            'EXCESO_OFERTA': 'balance-badge-excess-supply',
            'EXCESO_DEMANDA': 'balance-badge-excess-demand'
        };
        return clases[tipoBalance] || 'balance-badge-balanced';
    };

    /**
     * Obtiene el texto descriptivo del balance
     */
    const getTextoBalance = (tipoBalance) => {
        const textos = {
            'BALANCEADO': '✓ Problema Balanceado',
            'EXCESO_OFERTA': '⚠ Exceso de Oferta',
            'EXCESO_DEMANDA': '⚠ Exceso de Demanda'
        };
        return textos[tipoBalance] || 'Desconocido';
    };

    // API Pública
    return {
        renderSolucion,
        renderComparacion
    };
})();

// Log de inicialización
console.log('✅ TransporteRenderer inicializado');