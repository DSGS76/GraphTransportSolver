/**
 * Módulo principal del modelo de transporte.
 * Coordina todos los módulos y maneja el flujo principal.
 *
 * @author Duvan Gil
 * @version 1.0
 */
const TransporteMainApp = (() => {

    // Mensajes según el método
    const MENSAJES_METODO = {
        ESQUINA_NOROESTE: (costo) => ({
            titulo: '✅ Solución con Esquina Noroeste',
            descripcion: `Solución inicial encontrada con costo total de $${formatNumber(costo)}. Este método es simple y rápido, pero no siempre produce la mejor solución.`,
            clase: 'success'
        }),
        COSTO_MINIMO: (costo) => ({
            titulo: '✅ Solución con Costo Mínimo',
            descripcion: `Solución inicial encontrada con costo total de $${formatNumber(costo)}. Este método tiende a producir mejores soluciones que Esquina Noroeste.`,
            clase: 'success'
        }),
        VOGEL: (costo) => ({
            titulo: '✅ Solución con Vogel (VAM)',
            descripcion: `Solución inicial encontrada con costo total de $${formatNumber(costo)}. Vogel generalmente produce la mejor solución inicial entre los tres métodos.`,
            clase: 'success'
        })
    };

    /**
     * Inicializa la aplicación
     */
    const init = () => {
        setupEventListeners();
        console.log('✅ TransporteMainApp inicializado correctamente');
    };

    /**
     * Configura los event listeners principales
     */
    const setupEventListeners = () => {
        document.getElementById('btnResolver').addEventListener('click', resolverProblema);
        document.getElementById('btnComparar').addEventListener('click', compararMetodos);

        // Detectar Enter en inputs numéricos
        document.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && e.target.type === 'number') {
                e.preventDefault();
                resolverProblema();
            }
        });
    };

    /**
     * Resuelve el problema de transporte con el método seleccionado
     */
    const resolverProblema = async () => {
        try {
            console.log('╔═══════════════════════════════════════╗');
            console.log('🚀 INICIANDO RESOLUCIÓN DE PROBLEMA');
            console.log('╚═══════════════════════════════════════╝');

            // 1. Obtener datos del formulario
            const problemaDTO = TransporteFormManager.obtenerDatos();
            console.log('📋 Problema formulado:', problemaDTO);

            // 2. Mostrar loading
            showLoading(true);

            // 3. Llamar a la API
            const apiResponse = await TransporteApiService.resolverProblema(problemaDTO);

            console.log('📦 ApiResponseDTO completo:', apiResponse);

            // 4. Ocultar loading
            showLoading(false);

            // 5. Verificar respuesta
            if (apiResponse.success && apiResponse.data) {
                console.log('✅ Solución obtenida:', apiResponse.data);
                TransporteRenderer.renderSolucion(apiResponse.data);

                // Notificación de éxito
                if (typeof Notificaciones !== 'undefined') {
                    const solucion = apiResponse.data;
                    const metodo = solucion.metodoUtilizado;
                    const costo = solucion.costoTotal;

                    const mensaje = MENSAJES_METODO[metodo]
                        ? MENSAJES_METODO[metodo](costo)
                        : {
                            titulo: 'Éxito',
                            descripcion: `Problema resuelto con costo $${formatNumber(costo)}`,
                            clase: 'success'
                        };

                    Notificaciones.showWithTitle(
                        mensaje.titulo,
                        mensaje.descripcion,
                        mensaje.clase,
                        6000
                    );
                }
            } else {
                throw new Error(apiResponse.message || 'Error al resolver el problema');
            }

            console.log('╔═══════════════════════════════════════╗');
            console.log('✅ RESOLUCIÓN COMPLETADA');
            console.log('╚═══════════════════════════════════════╝');

        } catch (error) {
            showLoading(false);
            console.error('╔═══════════════════════════════════════╗');
            console.error('❌ ERROR EN RESOLUCIÓN');
            console.error('╚═══════════════════════════════════════╝');
            console.error('Error:', error);
            console.error('Mensaje:', error.message);

            // Notificación de error
            if (typeof Notificaciones !== 'undefined') {
                Notificaciones.showWithTitle(
                    'Error al resolver',
                    error.message || 'Ocurrió un error al procesar el problema',
                    'error',
                    5000
                );
            }

            console.error('Detalles del error:', {
                mensaje: error.message,
                tipo: error.name,
                timestamp: new Date().toISOString()
            });
        }
    };

    /**
     * Compara los tres métodos de solución inicial
     */
    const compararMetodos = async () => {
        try {
            console.log('╔═══════════════════════════════════════╗');
            console.log('🚀 INICIANDO COMPARACIÓN DE MÉTODOS');
            console.log('╚═══════════════════════════════════════╝');

            // 1. Obtener datos del formulario
            const problemaDTO = TransporteFormManager.obtenerDatos();

            // El método no importa en la comparación, pero lo enviamos de todas formas
            problemaDTO.metodoInicial = 'COSTO_MINIMO';

            console.log('📋 Problema para comparación:', problemaDTO);

            // 2. Mostrar loading
            showLoading(true, 'Comparando los tres métodos...');

            // 3. Llamar a la API de comparación
            const apiResponse = await TransporteApiService.compararMetodos(problemaDTO);

            console.log('📦 Comparación completa:', apiResponse);

            // 4. Ocultar loading
            showLoading(false);

            // 5. Verificar respuesta
            if (apiResponse.success && apiResponse.data) {
                console.log('✅ Comparación obtenida:', apiResponse.data);
                TransporteRenderer.renderComparacion(apiResponse.data);

                // Notificación de éxito con resumen
                if (typeof Notificaciones !== 'undefined') {
                    const comparacion = apiResponse.data;

                    const costos = [
                        comparacion.esquinaNoroeste.costoTotal,
                        comparacion.costoMinimo.costoTotal,
                        comparacion.vogel.costoTotal
                    ];

                    const costoMin = Math.min(...costos);
                    const costoMax = Math.max(...costos);
                    const diferencia = costoMax - costoMin;
                    const porcentaje = ((diferencia / costoMax) * 100).toFixed(1);

                    Notificaciones.showWithTitle(
                        '📊 Comparación Completada',
                        `Los tres métodos han sido comparados. La diferencia entre el mejor y peor es de $${formatNumber(diferencia)} (${porcentaje}%).`,
                        'info',
                        6000
                    );
                }
            } else {
                throw new Error(apiResponse.message || 'Error al comparar los métodos');
            }

            console.log('╔═══════════════════════════════════════╗');
            console.log('✅ COMPARACIÓN COMPLETADA');
            console.log('╚═══════════════════════════════════════╝');

        } catch (error) {
            showLoading(false);
            console.error('╔═══════════════════════════════════════╗');
            console.error('❌ ERROR EN COMPARACIÓN');
            console.error('╚═══════════════════════════════════════╝');
            console.error('Error:', error);
            console.error('Mensaje:', error.message);

            // Notificación de error
            if (typeof Notificaciones !== 'undefined') {
                Notificaciones.showWithTitle(
                    'Error al comparar',
                    error.message || 'Ocurrió un error al comparar los métodos',
                    'error',
                    5000
                );
            }
        }
    };

    /**
     * Muestra/oculta el overlay de loading
     */
    const showLoading = (show, texto = 'Resolviendo problema de transporte...') => {
        const overlay = document.getElementById('loadingOverlay');
        if (overlay) {
            const loadingText = overlay.querySelector('.loading-text');
            if (loadingText) {
                loadingText.textContent = texto;
            }
            overlay.style.display = show ? 'flex' : 'none';
        }
    };

    /**
     * Formatea un número con separadores de miles
     */
    const formatNumber = (num) => {
        return num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    };

    // API Pública
    return {
        init
    };
})();

// Inicializar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', TransporteMainApp.init);
} else {
    TransporteMainApp.init();
}