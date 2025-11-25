/**
 * Módulo para la comunicación con la API del método gráfico.
 * Maneja las peticiones HTTP al backend.
 *
 * @author Duvan Gil
 * @version 2.0
 */
const ApiService = (() => {
    // Configuración base con context-path
    const CONTEXT_PATH = '/graphtransportsolver';
    const API_BASE = '/api';
    const API_VERSION = '/v1';
    const SERVICE_PATH = '/grafico';
    const BASE_URL = `${CONTEXT_PATH}${API_BASE}${API_VERSION}${SERVICE_PATH}`;  // /graphtransportsolver/api/v1/grafico

    /**
     * Resuelve un problema de programación lineal
     * @param {Object} problemaData - Datos del problema
     * @returns {Promise<Object>} - ApiResponseDTO con la solución
     */
    const resolverProblema = async (problemaData) => {
        try {
            console.log('🚀 Enviando petición a:', `${BASE_URL}/resolver`);
            console.log('📦 Datos del problema:', JSON.stringify(problemaData, null, 2));

            const response = await fetch(`${BASE_URL}/resolver`, {  // CORREGIDO: sintaxis correcta
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(problemaData)
            });

            console.log('📡 Status HTTP:', response.status, response.statusText);

            // Verificar content-type
            const contentType = response.headers.get('content-type');
            if (!contentType || !contentType.includes('application/json')) {
                const textResponse = await response.text();
                console.error('❌ Respuesta no JSON:', textResponse);
                throw new Error(`El servidor devolvió ${contentType || 'contenido no válido'}. Status: ${response.status}`);
            }

            // Parsear respuesta
            const apiResponse = await response.json();
            console.log('✅ Respuesta JSON recibida:', apiResponse);
            console.log('🔍 Tipo de apiResponse:', typeof apiResponse);
            console.log('🔍 Keys de apiResponse:', Object.keys(apiResponse));
            console.log('🔍 apiResponse.success:', apiResponse.success);
            console.log('🔍 typeof apiResponse.success:', typeof apiResponse.success);

            // Verificar si es un objeto válido
            if (!apiResponse || typeof apiResponse !== 'object') {
                console.error('❌ La respuesta no es un objeto válido:', apiResponse);
                throw new Error('El servidor devolvió una respuesta con formato inválido');
            }

            // Verificar estructura del ApiResponseDTO
            if (typeof apiResponse.success === 'undefined' && !apiResponse.hasOwnProperty('success')) {
                console.error('❌ La respuesta no tiene la propiedad "success":', apiResponse);
                console.error('❌ Propiedades disponibles:', Object.keys(apiResponse));
                throw new Error('El servidor devolvió una respuesta con formato inválido (falta propiedad "success")');
            }

            // Si success es false, lanzar error con el mensaje del backend
            if (apiResponse.success === false) {
                throw new Error(apiResponse.message || 'Error desconocido del servidor');
            }

            return apiResponse;

        } catch (error) {
            console.error('❌ Error en la petición:', error);

            // Re-lanzar error con mensaje más descriptivo
            if (error.message.includes('Failed to fetch')) {
                throw new Error('No se puede conectar con el servidor. Verifique que el backend esté ejecutándose.');
            }

            throw error;  // Re-lanzar el error original si no es de conexión
        }
    };

    /**
     * Verifica la salud del servicio
     * @returns {Promise<Object>} - Estado del servicio
     */
    const checkHealth = async () => {
        try {
            const response = await fetch(`${BASE_URL}/health`);
            const data = await response.json();
            console.log('💚 Health check:', data);
            return data;
        } catch (error) {
            console.error('❌ Health check falló:', error);
            return { success: false, message: 'Servicio no disponible' };
        }
    };

    /**
     * Obtiene la configuración actual de la API
     * @returns {Object} - Configuración de la API
     */
    const getConfig = () => ({
        baseUrl: BASE_URL,
        endpoints: {
            resolver: `${BASE_URL}/resolver`,
            health: `${BASE_URL}/health`
        }
    });

    // API Pública
    return {
        resolverProblema,
        checkHealth,
        getConfig
    };
})();

// Log de inicialización
console.log('✅ ApiService inicializado');
console.log('📍 Configuración:', ApiService.getConfig());