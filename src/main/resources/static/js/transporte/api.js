/**
 * Módulo para la comunicación con la API del modelo de transporte.
 * Maneja las peticiones HTTP al backend.
 *
 * @author Duvan Gil
 * @version 1.0
 */
const TransporteApiService = (() => {
    // Configuración base con context-path
    const CONTEXT_PATH = '/graphtransportsolver';
    const API_BASE = '/api';
    const API_VERSION = '/v1';
    const SERVICE_PATH = '/transporte';
    const BASE_URL = `${CONTEXT_PATH}${API_BASE}${API_VERSION}${SERVICE_PATH}`;

    /**
     * Resuelve un problema de transporte con el método especificado
     * @param {Object} problemaData - Datos del problema
     * @returns {Promise<Object>} - ApiResponseDTO con la solución
     */
    const resolverProblema = async (problemaData) => {
        try {
            console.log('🚀 Enviando petición a:', `${BASE_URL}/resolver`);
            console.log('📦 Datos del problema:', JSON.stringify(problemaData, null, 2));

            const response = await fetch(`${BASE_URL}/resolver`, {
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

            // Verificar si es un objeto válido
            if (!apiResponse || typeof apiResponse !== 'object') {
                console.error('❌ La respuesta no es un objeto válido:', apiResponse);
                throw new Error('El servidor devolvió una respuesta con formato inválido');
            }

            // Verificar estructura del ApiResponseDTO
            if (typeof apiResponse.success === 'undefined') {
                console.error('❌ La respuesta no tiene la propiedad "success":', apiResponse);
                throw new Error('El servidor devolvió una respuesta con formato inválido');
            }

            // Si success es false, lanzar error con el mensaje del backend
            if (apiResponse.success === false) {
                throw new Error(apiResponse.message || 'Error desconocido del servidor');
            }

            return apiResponse;

        } catch (error) {
            console.error('❌ Error en la petición:', error);

            // Relanzar error con mensaje más descriptivo
            if (error.message.includes('Failed to fetch')) {
                throw new Error('No se puede conectar con el servidor. Verifique que el backend esté ejecutándose.');
            }

            throw error;
        }
    };

    /**
     * Compara los tres métodos de solución inicial
     * @param {Object} problemaData - Datos del problema
     * @returns {Promise<Object>} - ApiResponseDTO con la comparación
     */
    const compararMetodos = async (problemaData) => {
        try {
            console.log('🚀 Enviando petición de comparación a:', `${BASE_URL}/comparar`);
            console.log('📦 Datos del problema:', JSON.stringify(problemaData, null, 2));

            const response = await fetch(`${BASE_URL}/comparar`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(problemaData)
            });

            console.log('📡 Status HTTP:', response.status, response.statusText);

            const contentType = response.headers.get('content-type');
            if (!contentType || !contentType.includes('application/json')) {
                const textResponse = await response.text();
                console.error('❌ Respuesta no JSON:', textResponse);
                throw new Error(`El servidor devolvió ${contentType || 'contenido no válido'}. Status: ${response.status}`);
            }

            const apiResponse = await response.json();
            console.log('✅ Respuesta de comparación recibida:', apiResponse);

            if (!apiResponse || typeof apiResponse !== 'object') {
                console.error('❌ La respuesta no es un objeto válido:', apiResponse);
                throw new Error('El servidor devolvió una respuesta con formato inválido');
            }

            if (typeof apiResponse.success === 'undefined') {
                console.error('❌ La respuesta no tiene la propiedad "success":', apiResponse);
                throw new Error('El servidor devolvió una respuesta con formato inválido');
            }

            if (apiResponse.success === false) {
                throw new Error(apiResponse.message || 'Error desconocido del servidor');
            }

            return apiResponse;

        } catch (error) {
            console.error('❌ Error en la petición de comparación:', error);

            if (error.message.includes('Failed to fetch')) {
                throw new Error('No se puede conectar con el servidor. Verifique que el backend esté ejecutándose.');
            }

            throw error;
        }
    };

    // API Pública
    return {
        resolverProblema,
        compararMetodos
    };
})();

// Log de inicialización
console.log('✅ TransporteApiService inicializado');