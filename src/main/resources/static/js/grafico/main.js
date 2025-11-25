/**
 * Módulo principal del método gráfico
 * Coordina todos los módulos y maneja el flujo principal
 *
 * @author Duvan Gil
 * @version 2.0
 */
const MainApp = (() => {

    // Mensajes según tipo de solución (responsabilidad del frontend)
    const MENSAJES_SOLUCION = {
        UNICA: (z, x1, x2) => ({
            titulo: '✅ Solución Óptima Única',
            descripcion: `Se encontró una solución óptima única en el punto (${x1}, ${x2}) con valor Z = ${z}`,
            clase: 'success'
        }),
        MULTIPLE: (z, x1, x2) => ({
            titulo: '⚠️ Soluciones Múltiples (Infinitas)',
            descripcion: `Existen infinitas soluciones óptimas. Una de ellas es (${x1}, ${x2}) con Z = ${z}. Todos los puntos en el segmento entre vértices óptimos son solución.`,
            clase: 'warning'
        }),
        NO_FACTIBLE: () => ({
            titulo: '❌ Región No Factible',
            descripcion: 'El problema no tiene solución. Las restricciones son contradictorias y no existe ningún punto que las satisfaga simultáneamente.',
            clase: 'error'
        }),
        NO_ACOTADO: () => ({
            titulo: '⚠️ Problema No Acotado',
            descripcion: 'La función objetivo puede mejorar indefinidamente. La región factible se extiende al infinito en la dirección de mejora.',
            clase: 'warning'
        })
    };

    /**
     * Inicializa la aplicación
     */
    const init = () => {
        setupEventListeners();
        verificarConexion();
        console.log('✅ Aplicación inicializada correctamente');
    };

    /**
     * Verifica conexión con el backend
     */
    const verificarConexion = async () => {
        try {
            const health = await ApiService.checkHealth();
            if (health.success) {
                console.log('💚 Conexión con backend: OK');
            } else {
                console.warn('⚠️ Backend no responde correctamente');
            }
        } catch (error) {
            console.error('❌ No se puede conectar con el backend:', error);
        }
    };

    /**
     * Configura los event listeners principales
     */
    const setupEventListeners = () => {
        document.getElementById('btnResolver').addEventListener('click', resolverProblema);

        // Detectar Enter en inputs numéricos
        document.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && e.target.type === 'number') {
                e.preventDefault();
                resolverProblema();
            }
        });
    };

    /**
     * Resuelve el problema de programación lineal
     */
    const resolverProblema = async () => {
        try {
            console.log('═══════════════════════════════════════');
            console.log('🚀 INICIANDO RESOLUCIÓN DE PROBLEMA');
            console.log('═══════════════════════════════════════');

            // 1. Obtener datos del formulario
            const problemaDTO = FormManager.obtenerDatos();
            console.log('📋 Problema formulado:', problemaDTO);

            // 2. Mostrar loading
            showLoading(true);

            // 3. Llamar a la API
            const apiResponse = await ApiService.resolverProblema(problemaDTO);

            console.log('📦 ApiResponseDTO completo:', apiResponse);

            // 4. Ocultar loading
            showLoading(false);

            // 5. Verificar respuesta
            if (apiResponse.success && apiResponse.data) {
                console.log('✅ Solución obtenida:', apiResponse.data);
                mostrarResultados(apiResponse.data);
                FormManager.showToast('¡Problema resuelto exitosamente!', 'success');
            } else {
                throw new Error(apiResponse.message || 'Error al resolver el problema');
            }

            console.log('═══════════════════════════════════════');
            console.log('✅ RESOLUCIÓN COMPLETADA');
            console.log('═══════════════════════════════════════');

        } catch (error) {
            showLoading(false);
            console.error('═══════════════════════════════════════');
            console.error('❌ ERROR EN RESOLUCIÓN');
            console.error('═══════════════════════════════════════');
            console.error('Error:', error);
            console.error('Mensaje:', error.message);
            console.error('Stack:', error.stack);

            FormManager.showToast(error.message || 'Error al resolver el problema', 'error');

            // Mostrar error detallado en consola para debugging
            console.error('Detalles del error:', {
                mensaje: error.message,
                tipo: error.name,
                timestamp: new Date().toISOString()
            });
        }
    };

    /**
     * Muestra los resultados en la interfaz
     */
    const mostrarResultados = (solucionDTO) => {
        console.log('🎨 Renderizando resultados...');

        const section = document.getElementById('resultadosSection');
        section.style.display = 'block';

        // Scroll suave a resultados
        setTimeout(() => {
            section.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }, 100);

        // Renderizar componentes
        try {
            ChartManager.renderChart(solucionDTO);
            console.log('✅ Gráfica renderizada');
        } catch (error) {
            console.error('❌ Error al renderizar gráfica:', error);
        }

        mostrarInfoSolucion(solucionDTO);
        mostrarTablaVertices(solucionDTO);

        console.log('✅ Resultados mostrados correctamente');
    };

    /**
     * Muestra información de la solución
     */
    const mostrarInfoSolucion = (solucion) => {
        const container = document.getElementById('solucionInfo');
        const tipoSolucion = solucion.tipoSolucion;

        let mensaje;

        // Generar mensaje según tipo de solución
        if (tipoSolucion === 'UNICA' && solucion.puntoOptimo) {
            mensaje = MENSAJES_SOLUCION.UNICA(
                solucion.puntoOptimo.valorZ.toFixed(4),
                solucion.puntoOptimo.x1.toFixed(4),
                solucion.puntoOptimo.x2.toFixed(4)
            );
        } else if (tipoSolucion === 'MULTIPLE' && solucion.puntoOptimo) {
            mensaje = MENSAJES_SOLUCION.MULTIPLE(
                solucion.puntoOptimo.valorZ.toFixed(4),
                solucion.puntoOptimo.x1.toFixed(4),
                solucion.puntoOptimo.x2.toFixed(4)
            );
        } else if (tipoSolucion === 'NO_FACTIBLE') {
            mensaje = MENSAJES_SOLUCION.NO_FACTIBLE();
        } else if (tipoSolucion === 'NO_ACOTADO') {
            mensaje = MENSAJES_SOLUCION.NO_ACOTADO();
        } else {
            mensaje = {
                titulo: '❓ Tipo de Solución Desconocido',
                descripcion: `Tipo de solución: ${tipoSolucion}`,
                clase: 'info'
            };
        }

        let html = `
            <div class="solution-header">
                <div class="solution-badge badge-${mensaje.clase}">
                    ${mensaje.titulo}
                </div>
                <p class="solution-description">${mensaje.descripcion}</p>
            </div>
        `;

        // Mostrar detalles del punto óptimo si existe
        if (solucion.puntoOptimo) {
            html += `
                <div class="solution-optimal">
                    <h3 class="solution-subtitle">🎯 Detalles de la Solución</h3>
                    <div class="solution-details">
                        <div class="solution-detail-item">
                            <span class="detail-label">x₁ =</span>
                            <span class="detail-value">${solucion.puntoOptimo.x1.toFixed(6)}</span>
                        </div>
                        <div class="solution-detail-item">
                            <span class="detail-label">x₂ =</span>
                            <span class="detail-value">${solucion.puntoOptimo.x2.toFixed(6)}</span>
                        </div>
                        <div class="solution-detail-item highlight">
                            <span class="detail-label">Z* =</span>
                            <span class="detail-value">${solucion.puntoOptimo.valorZ.toFixed(6)}</span>
                        </div>
                    </div>
                </div>
            `;
        }

        // Información adicional según tipo
        if (tipoSolucion === 'MULTIPLE') {
            html += `
                <div class="solution-note">
                    <span class="note-icon">💡</span>
                    <p><strong>Nota:</strong> En soluciones múltiples, todos los puntos en el segmento 
                    que une los vértices óptimos son también soluciones óptimas con el mismo valor de Z.</p>
                </div>
            `;
        }

        container.innerHTML = html;
    };

    /**
     * Muestra la tabla de vértices
     */
    const mostrarTablaVertices = (solucion) => {
        const container = document.getElementById('verticesTableContainer');

        if (!solucion.vertices || solucion.vertices.length === 0) {
            container.innerHTML = '<p class="no-vertices">No se encontraron vértices de la región factible.</p>';
            return;
        }

        let html = `
            <h3 class="section-subtitle">
                <span class="subtitle-icon">📊</span>
                Evaluación en los Vértices
                <span class="vertex-count">(${solucion.vertices.length} vértice${solucion.vertices.length !== 1 ? 's' : ''})</span>
            </h3>
            <div class="table-responsive">
                <table class="vertices-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>x₁</th>
                            <th>x₂</th>
                            <th>Z</th>
                            <th>Estado</th>
                        </tr>
                    </thead>
                    <tbody>
        `;

        solucion.vertices.forEach((vertice, index) => {
            const esOptimo = solucion.puntoOptimo &&
                Math.abs(vertice.x1 - solucion.puntoOptimo.x1) < 0.0001 &&
                Math.abs(vertice.x2 - solucion.puntoOptimo.x2) < 0.0001;

            html += `
                <tr class="${esOptimo ? 'row-optimal' : ''}">
                    <td class="vertex-number">V${index + 1}</td>
                    <td>${vertice.x1.toFixed(6)}</td>
                    <td>${vertice.x2.toFixed(6)}</td>
                    <td class="value-z ${esOptimo ? 'value-optimal' : ''}">${vertice.valorZ.toFixed(6)}</td>
                    <td>
                        ${esOptimo ? '<span class="badge-optimal">⭐ Óptimo</span>' :
                vertice.esFactible ? '<span class="badge-feasible">✓ Factible</span>' :
                    '<span class="badge-infeasible">✗ No Factible</span>'}
                    </td>
                </tr>
            `;
        });

        html += `
                    </tbody>
                </table>
            </div>
        `;

        container.innerHTML = html;
    };

    /**
     * Muestra/oculta el overlay de loading
     */
    const showLoading = (show) => {
        const overlay = document.getElementById('loadingOverlay');
        if (overlay) {
            overlay.style.display = show ? 'flex' : 'none';
        }
    };

    // API Pública
    return {
        init
    };
})();

// Inicializar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', MainApp.init);
} else {
    MainApp.init();
}