/**
 * Módulo para el manejo del formulario del método gráfico.
 * Gestiona restricciones dinámicas y validaciones robustas.
 *
 * @author Duvan Gil
 * @version 2.0
 */
const FormManager = (() => {
    let restriccionCount = 0;

    /**
     * Inicializa el gestor del formulario
     */
    const init = () => {
        setupEventListeners();
        // Agregar dos restricciones iniciales
        agregarRestriccion();
        agregarRestriccion();
        console.log('✅ FormManager inicializado');
    };

    /**
     * Configura los event listeners del formulario
     */
    const setupEventListeners = () => {
        document.getElementById('btnAgregarRestriccion').addEventListener('click', agregarRestriccion);
        document.getElementById('btnLimpiar').addEventListener('click', limpiarFormulario);
        document.getElementById('btnEjemplo').addEventListener('click', cargarEjemplo);

        // Validación en tiempo real de la función objetivo
        document.getElementById('coefZ_x1').addEventListener('input', validarFuncionObjetivo);
        document.getElementById('coefZ_x2').addEventListener('input', validarFuncionObjetivo);
    };

    /**
     * Válida la función objetivo en tiempo real
     */
    const validarFuncionObjetivo = () => {
        const x1 = parseFloat(document.getElementById('coefZ_x1').value) || 0;
        const x2 = parseFloat(document.getElementById('coefZ_x2').value) || 0;

        const warning = document.getElementById('foWarning');

        if (Math.abs(x1) < 1e-10 && Math.abs(x2) < 1e-10) {
            if (!warning) {
                const warningDiv = document.createElement('div');
                warningDiv.id = 'foWarning';
                warningDiv.className = 'validation-warning';
                warningDiv.innerHTML = '⚠️ Al menos un coeficiente debe ser diferente de cero';
                document.querySelector('.objective-function').appendChild(warningDiv);
            }
        } else if (warning) {
            warning.remove();
        }
    };

    /**
     * Agrega una nueva restricción al formulario
     */
    const agregarRestriccion = () => {
        restriccionCount++;
        const container = document.getElementById('restriccionesContainer');

        const restriccionDiv = document.createElement('div');
        restriccionDiv.className = 'restriction-item';
        restriccionDiv.id = `restriccion-${restriccionCount}`;
        restriccionDiv.setAttribute('data-id', restriccionCount);

        restriccionDiv.innerHTML = `
            <div class="restriction-header">
                <span class="restriction-number">R${restriccionCount}</span>
                <button type="button" class="btn-remove" onclick="FormManager.eliminarRestriccion(${restriccionCount})" 
                        title="Eliminar restricción" aria-label="Eliminar restricción ${restriccionCount}">
                    <span>✕</span>
                </button>
            </div>
            <div class="equation-display">
                <input type="number" 
                       id="coefR${restriccionCount}_x1" 
                       class="equation-input" 
                       value="1" 
                       step="any" 
                       placeholder="0"
                       title="Coeficiente de x₁">
                <span class="equation-var">x₁</span>
                <span class="equation-operator">+</span>
                <input type="number" 
                       id="coefR${restriccionCount}_x2" 
                       class="equation-input" 
                       value="1" 
                       step="any" 
                       placeholder="0"
                       title="Coeficiente de x₂">
                <span class="equation-var">x₂</span>
                <select id="tipoR${restriccionCount}" 
                        class="equation-select"
                        title="Tipo de desigualdad">
                    <option value="MENOR_IGUAL">≤</option>
                    <option value="MAYOR_IGUAL">≥</option>
                    <option value="IGUAL">=</option>
                </select>
                <input type="number" 
                       id="ladoR${restriccionCount}" 
                       class="equation-input" 
                       value="10" 
                       step="any" 
                       placeholder="0"
                       title="Lado derecho">
            </div>
        `;

        container.appendChild(restriccionDiv);
        animateElement(restriccionDiv);

        // Actualizar contador de restricciones
        actualizarContadorRestricciones();
    };

    /**
     * Elimina una restricción del formulario
     */
    const eliminarRestriccion = (id) => {
        const restricciones = document.querySelectorAll('.restriction-item');

        // No permitir eliminar si hay 2 o menos restricción
        if (restricciones.length <= 2) {
            showToast('Debe haber al menos dos restricción', 'warning');
            return;
        }

        const restriccion = document.getElementById(`restriccion-${id}`);
        if (restriccion) {
            restriccion.style.animation = 'slideOut 0.3s ease-out';
            setTimeout(() => {
                restriccion.remove();
                renumerarRestricciones();
                actualizarContadorRestricciones();
                showToast('Restricción eliminada', 'info');
            }, 300);
        }
    };

    /**
     * Renumera las restricciones después de eliminar una
     */
    const renumerarRestricciones = () => {
        const restricciones = document.querySelectorAll('.restriction-item');
        restricciones.forEach((restriccion, index) => {
            const numero = restriccion.querySelector('.restriction-number');
            if (numero) {
                numero.textContent = `R${index + 1}`;
            }
        });
    };

    /**
     * Actualiza el contador de restricciones en el botón
     */
    const actualizarContadorRestricciones = () => {
        const restricciones = document.querySelectorAll('.restriction-item');
        const titulo = document.querySelector('.section-header h3');
        if (titulo) {
            const count = titulo.querySelector('.restriction-count') || document.createElement('span');
            count.className = 'restriction-count';
            count.textContent = ` (${restricciones.length})`;
            if (!titulo.querySelector('.restriction-count')) {
                titulo.appendChild(count);
            }
        }
    };

    /**
     * Obtiene los datos del formulario con validaciones robustas
     */
    const obtenerDatos = () => {
        // Validar que haya al menos dos restriccones
        const restricciones = document.querySelectorAll('.restriction-item');
        if (restricciones.length < 2) {
            throw new Error('❌ Debe agregar al menos dos restricción');
        }

        // Obtener función objetivo
        const coefX1 = parseFloat(document.getElementById('coefZ_x1').value);
        const coefX2 = parseFloat(document.getElementById('coefZ_x2').value);

        // Validar coeficientes de función objetivo
        if (isNaN(coefX1) || isNaN(coefX2)) {
            throw new Error('Los coeficientes de la función objetivo deben ser números válidos');
        }

        const datos = {
            funcionObjetivo: {
                coeficienteX1: coefX1,
                coeficienteX2: coefX2,
                tipo: document.getElementById('tipoOptimizacion').value
            },
            restricciones: [],
            incluirNoNegatividad: true  // Siempre true en problemas de PL
        };

        // Recopilar restricciones
        let restriccionesValidas = 0;
        restricciones.forEach((restriccion, index) => {
            const id = restriccion.getAttribute('data-id');
            const coefX1 = parseFloat(document.getElementById(`coefR${id}_x1`).value);
            const coefX2 = parseFloat(document.getElementById(`coefR${id}_x2`).value);
            const tipo = document.getElementById(`tipoR${id}`).value;
            const ladoDerecho = parseFloat(document.getElementById(`ladoR${id}`).value);

            // Validar valores numéricos
            if (isNaN(coefX1) || isNaN(coefX2) || isNaN(ladoDerecho)) {
                throw new Error(`La restricción ${index + 1} tiene valores no numéricos`);
            }

            datos.restricciones.push({
                coeficienteX1: coefX1,
                coeficienteX2: coefX2,
                tipo: tipo,
                ladoDerecho: ladoDerecho
            });

            restriccionesValidas++;
        });

        console.log('✅ Datos validados:', {
            funcionObjetivo: datos.funcionObjetivo,
            restricciones: restriccionesValidas,
            incluirNoNegatividad: datos.incluirNoNegatividad
        });

        return datos;
    };

    /**
     * Limpia el formulario completamente
     */
    const limpiarFormulario = async () => {
        // Usar modal de confirmación personalizado
        const confirmado = await Notificaciones.confirm(
            '¿Está seguro de limpiar el problema?',
            'Se perderán todos los datos ingresados y no podrás recuperarlos.',
            'Limpiar Problema',
            'Sí, limpiar',
            'Cancelar'
        );

        if (!confirmado) {
            return;
        }

        // Limpiar función objetivo
        document.getElementById('coefZ_x1').value = '';
        document.getElementById('coefZ_x2').value = '';
        document.getElementById('tipoOptimizacion').value = 'MAXIMIZAR';

        // Eliminar warning si existe
        const warning = document.getElementById('foWarning');
        if (warning) warning.remove();

        // Eliminar todas las restricciones
        document.getElementById('restriccionesContainer').innerHTML = '';
        restriccionCount = 0;

        // Agregar dos restricciones vacías
        agregarRestriccion();
        agregarRestriccion();

        // Ocultar resultados
        const resultadosSection = document.getElementById('resultadosSection');
        if (resultadosSection) {
            resultadosSection.style.display = 'none';
        }

        // Ocultar botón de exportar
        const btnExportar = document.getElementById('btnExportarGrafica');
        if (btnExportar) {
            btnExportar.style.display = 'none';
        }

        showToast('Formulario limpiado correctamente', 'info');
    };

    /**
     * Carga un ejemplo predefinido
     */
    const cargarEjemplo = () => {
        // Función objetivo: Max Z = 3x1 + 5x2
        document.getElementById('coefZ_x1').value = '3';
        document.getElementById('coefZ_x2').value = '5';
        document.getElementById('tipoOptimizacion').value = 'MAXIMIZAR';

        // Limpiar restricciones existentes
        document.getElementById('restriccionesContainer').innerHTML = '';
        restriccionCount = 0;

        // Restricción 1: x1 <= 4
        agregarRestriccion();
        document.getElementById(`coefR${restriccionCount}_x1`).value = '1';
        document.getElementById(`coefR${restriccionCount}_x2`).value = '0';
        document.getElementById(`tipoR${restriccionCount}`).value = 'MENOR_IGUAL';
        document.getElementById(`ladoR${restriccionCount}`).value = '4';

        // Restricción 2: 2x2 <= 12
        agregarRestriccion();
        document.getElementById(`coefR${restriccionCount}_x1`).value = '0';
        document.getElementById(`coefR${restriccionCount}_x2`).value = '2';
        document.getElementById(`tipoR${restriccionCount}`).value = 'MENOR_IGUAL';
        document.getElementById(`ladoR${restriccionCount}`).value = '12';

        // Restricción 3: 3x1 + 2x2 <= 18
        agregarRestriccion();
        document.getElementById(`coefR${restriccionCount}_x1`).value = '3';
        document.getElementById(`coefR${restriccionCount}_x2`).value = '2';
        document.getElementById(`tipoR${restriccionCount}`).value = 'MENOR_IGUAL';
        document.getElementById(`ladoR${restriccionCount}`).value = '18';

        // Ocultar resultados previos
        const resultadosSection = document.getElementById('resultadosSection');
        if (resultadosSection) {
            resultadosSection.style.display = 'none';
        }

        showToast('💡 Ejemplo cargado: Maximizar Z = 3x₁ + 5x₂', 'success');
    };

    /**
     * Anima la entrada de un elemento
     */
    const animateElement = (element) => {
        element.style.animation = 'slideIn 0.3s ease-out';
    };

    /**
     * Muestra una notificación usando el sistema global
     */
    const showToast = (message, type = 'info') => {
        // Usar el sistema de notificaciones global
        if (typeof Notificaciones !== 'undefined') {
            Notificaciones.show(message, type);
        } else {
            console.error('Sistema de notificaciones no disponible');
            console.log(`[${type.toUpperCase()}] ${message}`);
        }
    };

    // API Pública
    return {
        init,
        obtenerDatos,
        eliminarRestriccion,
        showToast
    };
})();

// Inicializar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', FormManager.init);
} else {
    FormManager.init();
}