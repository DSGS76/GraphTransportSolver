/**
 * Módulo para el manejo del formulario del modelo de transporte.
 * Gestiona tablas dinámicas y validaciones.
 *
 * @author Duvan Gil
 * @version 1.0
 */
const TransporteFormManager = (() => {
    let m = 3; // Número de orígenes
    let n = 4; // Número de destinos

    /**
     * Inicializa el gestor del formulario
     */
    const init = () => {
        setupEventListeners();
        generarTablas();
        console.log('✅ TransporteFormManager inicializado');
    };

    /**
     * Configura los event listeners del formulario
     */
    const setupEventListeners = () => {
        document.getElementById('btnGenerarTablas').addEventListener('click', generarTablas);
        document.getElementById('btnLimpiar').addEventListener('click', limpiarFormulario);
        document.getElementById('btnEjemplo').addEventListener('click', cargarEjemplo);

        // Validación en tiempo real de dimensiones
        document.getElementById('numOrigenes').addEventListener('change', validarDimensiones);
        document.getElementById('numDestinos').addEventListener('change', validarDimensiones);
    };

    /**
     * Valida las dimensiones ingresadas
     */
    const validarDimensiones = () => {
        const origenesInput = document.getElementById('numOrigenes');
        const destinosInput = document.getElementById('numDestinos');

        let origenes = parseInt(origenesInput.value);
        let destinos = parseInt(destinosInput.value);

        // Validar rangos
        if (origenes < 2) origenes = 2;
        if (origenes > 50) origenes = 50;
        if (destinos < 2) destinos = 2;
        if (destinos > 50) destinos = 50;

        origenesInput.value = origenes;
        destinosInput.value = destinos;

        return { origenes, destinos };
    };

    /**
     * Genera todas las tablas del problema
     */
    const generarTablas = () => {
        const { origenes, destinos } = validarDimensiones();
        m = origenes;
        n = destinos;

        generarOfertas();
        generarDemandas();
        generarMatrizCostos();

        if (typeof Notificaciones !== 'undefined') {
            Notificaciones.info(`Tablas generadas: ${m} orígenes × ${n} destinos`, 2000);
        }
    };

    /**
     * Genera los inputs de ofertas
     */
    const generarOfertas = () => {
        const container = document.getElementById('ofertasContainer');
        container.innerHTML = '';

        for (let i = 0; i < m; i++) {
            const div = document.createElement('div');
            div.className = 'supply-item';
            div.innerHTML = `
                <div class="item-header">
                    <span class="item-label">Origen ${i + 1}</span>
                </div>
                <div class="item-input-group">
                    <input type="text" 
                           id="nombreOrigen${i}" 
                           class="item-name-input" 
                           placeholder="Nombre (opcional)"
                           value="O${i + 1}">
                    <input type="number" 
                           id="oferta${i}" 
                           class="item-value-input" 
                           value="100" 
                           min="0" 
                           step="any"
                           placeholder="Capacidad">
                </div>
            `;
            container.appendChild(div);

            // ✅ Agregar listener para actualizar la matriz dinámicamente
            const nombreInput = div.querySelector(`#nombreOrigen${i}`);
            nombreInput.addEventListener('input', () => actualizarNombresMatriz());
        }
    };

    /**
     * Genera los inputs de demandas
     */
    const generarDemandas = () => {
        const container = document.getElementById('demandasContainer');
        container.innerHTML = '';

        for (let j = 0; j < n; j++) {
            const div = document.createElement('div');
            div.className = 'demand-item';
            div.innerHTML = `
                <div class="item-header">
                    <span class="item-label">Destino ${j + 1}</span>
                </div>
                <div class="item-input-group">
                    <input type="text" 
                           id="nombreDestino${j}" 
                           class="item-name-input" 
                           placeholder="Nombre (opcional)"
                           value="D${j + 1}">
                    <input type="number" 
                           id="demanda${j}" 
                           class="item-value-input" 
                           value="75" 
                           min="0" 
                           step="any"
                           placeholder="Requerimiento">
                </div>
            `;
            container.appendChild(div);

            // ✅ Agregar listener para actualizar la matriz dinámicamente
            const nombreInput = div.querySelector(`#nombreDestino${j}`);
            nombreInput.addEventListener('input', () => actualizarNombresMatriz());
        }
    };

    /**
     * Actualiza los nombres en los encabezados de la matriz de costos
     */
    const actualizarNombresMatriz = () => {
        const table = document.querySelector('.costs-table');
        if (!table) return;

        // Actualizar encabezados de columnas (destinos)
        const headerCells = table.querySelectorAll('thead th');
        for (let j = 0; j < n; j++) {
            const nombreInput = document.getElementById(`nombreDestino${j}`);
            if (nombreInput && headerCells[j + 1]) { // +1 porque el primero es "De \ A"
                headerCells[j + 1].textContent = nombreInput.value.trim() || `D${j + 1}`;
            }
        }

        // Actualizar encabezados de filas (orígenes)
        const rowHeaders = table.querySelectorAll('tbody .row-header');
        for (let i = 0; i < m; i++) {
            const nombreInput = document.getElementById(`nombreOrigen${i}`);
            if (nombreInput && rowHeaders[i]) {
                rowHeaders[i].textContent = nombreInput.value.trim() || `O${i + 1}`;
            }
        }
    };

    /**
     * Genera la matriz de costos
     */
    const generarMatrizCostos = () => {
        const container = document.getElementById('costosTableContainer');

        let html = '<table class="costs-table"><thead><tr><th>De \\ A</th>';

        // Encabezados de columnas (destinos)
        for (let j = 0; j < n; j++) {
            const nombreInput = document.getElementById(`nombreDestino${j}`);
            const nombre = nombreInput ? (nombreInput.value.trim() || `D${j + 1}`) : `D${j + 1}`;
            html += `<th id="headerDestino${j}">${nombre}</th>`;
        }
        html += '</tr></thead><tbody>';

        // Filas (orígenes)
        for (let i = 0; i < m; i++) {
            const nombreInput = document.getElementById(`nombreOrigen${i}`);
            const nombre = nombreInput ? (nombreInput.value.trim() || `O${i + 1}`) : `O${i + 1}`;
            html += `<tr><td class="row-header" id="headerOrigen${i}">${nombre}</td>`;

            for (let j = 0; j < n; j++) {
                html += `<td><input type="number" id="costo${i}_${j}" class="cost-input" value="10" min="0" step="any"></td>`;
            }

            html += '</tr>';
        }

        html += '</tbody></table>';
        container.innerHTML = html;
    };

    /**
     * Obtiene los datos del formulario
     * @returns {Object} - Datos formateados para el backend
     */
    const obtenerDatos = () => {
        // Validar dimensiones actuales
        const origenesInput = document.getElementById('numOrigenes');
        const destinosInput = document.getElementById('numDestinos');

        const mActual = parseInt(origenesInput.value);
        const nActual = parseInt(destinosInput.value);

        if (mActual !== m || nActual !== n) {
            throw new Error('Las dimensiones han cambiado. Debe regenerar las tablas primero.');
        }

        // Obtener ofertas
        const ofertas = [];
        const nombresOrigenes = [];

        for (let i = 0; i < m; i++) {
            const oferta = parseFloat(document.getElementById(`oferta${i}`).value);
            const nombre = document.getElementById(`nombreOrigen${i}`).value.trim();
            const nombreMostrar = nombre || `Origen ${i + 1}`;

            if (isNaN(oferta) || oferta < 0) {
                throw new Error(`La oferta de ${nombreMostrar} debe ser un número no negativo`);
            }

            ofertas.push(oferta);
            nombresOrigenes.push(nombre || `O${i + 1}`);
        }

        // Obtener demandas
        const demandas = [];
        const nombresDestinos = [];

        for (let j = 0; j < n; j++) {
            const demanda = parseFloat(document.getElementById(`demanda${j}`).value);
            const nombre = document.getElementById(`nombreDestino${j}`).value.trim();
            const nombreMostrar = nombre || `Destino ${j + 1}`;

            if (isNaN(demanda) || demanda < 0) {
                throw new Error(`La demanda de ${nombreMostrar} debe ser un número no negativo`);
            }

            demandas.push(demanda);
            nombresDestinos.push(nombre || `D${j + 1}`);
        }

        // Obtener matriz de costos
        const costos = [];

        for (let i = 0; i < m; i++) {
            costos[i] = [];
            for (let j = 0; j < n; j++) {
                const costo = parseFloat(document.getElementById(`costo${i}_${j}`).value);

                if (isNaN(costo) || costo < 0) {
                    // Usar los nombres personalizados en lugar de los nombres por defecto
                    const nombreOrigen = nombresOrigenes[i];
                    const nombreDestino = nombresDestinos[j];
                    throw new Error(`El costo de ${nombreOrigen} a ${nombreDestino} debe ser un número no negativo`);
                }

                costos[i][j] = costo;
            }
        }

        // Validar sumas
        const sumaOfertas = ofertas.reduce((a, b) => a + b, 0);
        const sumaDemandas = demandas.reduce((a, b) => a + b, 0);

        if (sumaOfertas === 0) {
            throw new Error('La suma de ofertas debe ser mayor a cero');
        }

        if (sumaDemandas === 0) {
            throw new Error('La suma de demandas debe ser mayor a cero');
        }

        // Obtener método seleccionado
        const metodoInicial = document.getElementById('metodoSolucion').value;

        const datos = {
            costos,
            ofertas,
            demandas,
            nombresOrigenes,
            nombresDestinos,
            metodoInicial
        };

        console.log('✅ Datos validados:', {
            dimensiones: `${m}×${n}`,
            sumaOfertas,
            sumaDemandas,
            desbalance: sumaOfertas - sumaDemandas,
            metodoInicial
        });

        return datos;
    };

    /**
     * Limpia el formulario
     */
    const limpiarFormulario = async () => {
        const confirmado = await Notificaciones.confirm(
            '¿Está seguro de limpiar el problema?',
            'Se perderán todos los datos ingresados.',
            'Limpiar Problema',
            'Sí, limpiar',
            'Cancelar'
        );

        if (!confirmado) {
            return;
        }

        // Resetear dimensiones
        document.getElementById('numOrigenes').value = '2';
        document.getElementById('numDestinos').value = '2';
        document.getElementById('metodoSolucion').value = 'COSTO_MINIMO';

        // Regenerar tablas
        generarTablas();

        // Ocultar resultados
        document.getElementById('resultadosSection').style.display = 'none';
        document.getElementById('comparacionSection').style.display = 'none';

        if (typeof Notificaciones !== 'undefined') {
            Notificaciones.info('Formulario limpiado correctamente');
        }
    };

    /**
     * Carga un ejemplo predefinido
     */
    const cargarEjemplo = () => {
        // Ejemplo: 3 orígenes × 4 destinos
        document.getElementById('numOrigenes').value = '3';
        document.getElementById('numDestinos').value = '4';

        m = 3;
        n = 4;

        generarTablas();

        // Ofertas
        document.getElementById('oferta0').value = '250';
        document.getElementById('oferta1').value = '300';
        document.getElementById('oferta2').value = '400';

        // Demandas
        document.getElementById('demanda0').value = '200';
        document.getElementById('demanda1').value = '225';
        document.getElementById('demanda2').value = '275';
        document.getElementById('demanda3').value = '250';

        // Matriz de costos
        const costosEjemplo = [
            [10, 2, 20, 11],
            [12, 7, 9, 20],
            [4, 14, 16, 18]
        ];

        for (let i = 0; i < 3; i++) {
            for (let j = 0; j < 4; j++) {
                document.getElementById(`costo${i}_${j}`).value = costosEjemplo[i][j];
            }
        }

        // Método
        document.getElementById('metodoSolucion').value = 'COSTO_MINIMO';

        // Ocultar resultados previos
        document.getElementById('resultadosSection').style.display = 'none';
        document.getElementById('comparacionSection').style.display = 'none';

        if (typeof Notificaciones !== 'undefined') {
            Notificaciones.success('💡 Ejemplo cargado: 3 orígenes × 4 destinos');
        }
    };

    /**
     * Obtiene las dimensiones actuales
     */
    const getDimensiones = () => ({ m, n });

    // API Pública
    return {
        init,
        obtenerDatos,
        getDimensiones
    };
})();

// Inicializar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', TransporteFormManager.init);
} else {
    TransporteFormManager.init();
}