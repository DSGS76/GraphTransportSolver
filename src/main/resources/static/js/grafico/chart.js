/**
 * Módulo para renderizar gráficas con Chart.js
 * Maneja la visualización profesional de la región factible y el punto óptimo
 *
 * @author Duvan Gil
 * @version 2.0
 */
const ChartManager = (() => {
    let chartInstance = null;

    /**
     * Renderiza la gráfica con los resultados
     * @param {Object} solucion - Datos de la solución (SolucionGraficoDTO)
     */
    const renderChart = (solucion) => {
        console.log('🎨 Renderizando gráfica...');

        const canvas = document.getElementById('graficoChart');
        if (!canvas) {
            console.error('❌ Canvas no encontrado');
            return;
        }

        const ctx = canvas.getContext('2d');

        // Destruir gráfica anterior si existe
        if (chartInstance) {
            chartInstance.destroy();
            chartInstance = null;
        }

        // Preparar datos para la gráfica
        const datasets = prepararDatasets(solucion);
        const limits = calcularLimites(solucion);

        // Configuración de la gráfica
        const config = {
            type: 'line',  // Cambiado a 'line' para que fill funcione correctamente
            data: { datasets },
            options: crearOpciones(solucion, limits)
        };

        // Crear gráfica
        try {
            chartInstance = new Chart(ctx, config);
            console.log('✅ Gráfica creada exitosamente');
        } catch (error) {
            console.error('❌ Error al crear gráfica:', error);
            throw error;
        }
    };

    /**
     * Prepara los datasets para Chart.js
     */
    const prepararDatasets = (solucion) => {
        const datasets = [];
        const limits = calcularLimites(solucion);

        console.log('📊 Preparando datasets...');
        console.log('Límites calculados:', limits);
        console.log('Restricciones recibidas:', solucion.restricciones);

        // 0. Líneas de restricciones (primero, para que queden atrás)
        if (solucion.restricciones && solucion.restricciones.length > 0) {
            console.log(`✅ Procesando ${solucion.restricciones.length} restricciones...`);
            solucion.restricciones.forEach((restriccion, index) => {
                const puntos = calcularPuntosRestriccion(restriccion, limits);
                console.log(`Restricción ${index + 1}:`, restriccion, '→ Puntos:', puntos);
                if (puntos && puntos.length > 0) {
                    // Generar color usando HSL para distribución uniforme en el espectro
                    const hue = (index * 360 / Math.max(solucion.restricciones.length, 6)) % 360;
                    const color = `hsla(${hue}, 70%, 55%, 0.85)`;
                    const label = formatearRestriccion(restriccion, index + 1);

                    datasets.push({
                        label: label,
                        data: puntos,
                        type: 'line',  // Tipo línea explícito
                        borderColor: color,
                        backgroundColor: 'transparent',
                        borderWidth: 2,
                        borderDash: [5, 5],  // Línea punteada
                        pointRadius: 0,
                        showLine: true,
                        fill: false,
                        order: 10  // Orden alto para que esté atrás
                    });
                    console.log(`✅ Restricción ${index + 1} agregada:`, label);
                } else {
                    console.warn(`⚠️ Restricción ${index + 1} no generó puntos válidos`);
                }
            });
        } else {
            console.warn('⚠️ No hay restricciones para graficar');
        }

        // 1. Región factible (polígono) - Ya viene ordenada del backend
        if (solucion.regionFactible && solucion.regionFactible.length > 0) {
            const regionData = solucion.regionFactible.map(p => ({ x: p.x1, y: p.x2 }));

            // Cerrar el polígono
            if (regionData.length > 0) {
                regionData.push(regionData[0]);
            }

            datasets.push({
                type: 'line',  // Tipo line explícito para soportar fill
                label: 'Región Factible',
                data: regionData,
                backgroundColor: 'rgba(37, 99, 235, 0.35)',  // Azul con 35% opacidad
                borderColor: 'rgba(37, 99, 235, 1)',         // Azul sólido
                borderWidth: 3,
                pointRadius: 0,
                pointHoverRadius: 0,
                fill: true,  // Activar relleno
                showLine: true,
                tension: 0,
                order: 3
            });
        }

        // 2. Vértices (todos los puntos)
        if (solucion.vertices && solucion.vertices.length > 0) {
            datasets.push({
                type: 'scatter',  // Tipo scatter para mostrar solo puntos
                label: 'Vértices',
                data: solucion.vertices.map(p => ({ x: p.x1, y: p.x2 })),
                backgroundColor: 'rgba(100, 116, 139, 0.8)',  // Gris azulado
                borderColor: 'rgba(71, 85, 105, 1)',          // Gris oscuro
                borderWidth: 2,
                pointRadius: 8,
                pointHoverRadius: 10,
                pointStyle: 'circle',
                showLine: false,
                order: 2
            });
        }

        // 3. Punto óptimo (destacado)
        if (solucion.puntoOptimo) {
            datasets.push({
                type: 'scatter',  // Tipo scatter para mostrar solo el punto
                label: '⭐ Punto Óptimo',
                data: [{ x: solucion.puntoOptimo.x1, y: solucion.puntoOptimo.x2 }],
                backgroundColor: 'rgb(50,220,38)',      // Verde brillante
                borderColor: 'rgb(38,185,28)',          // Verde oscuro
                borderWidth: 4,
                pointRadius: 14,
                pointHoverRadius: 16,
                pointStyle: 'star',
                showLine: false,
                order: 1
            });
        }

        console.log(`📊 Total datasets preparados: ${datasets.length}`);
        console.log('Datasets:', datasets);
        return datasets;
    };

    /**
     * Crea las opciones de configuración de Chart.js
     */
    const crearOpciones = (solucion, limits) => ({
        responsive: true,
        maintainAspectRatio: false,
        animation: {
            duration: 1000,
            easing: 'easeInOutQuart'
        },
        plugins: {
            title: {
                display: true,
                text: getTitulo(solucion),
                font: {
                    size: 20,
                    weight: 'bold',
                    family: "'Segoe UI', Arial, sans-serif"
                },
                color: '#2c3e50',
                padding: {
                    top: 10,
                    bottom: 20
                }
            },
            legend: {
                display: true,
                position: 'top',
                align: 'center',
                labels: {
                    usePointStyle: true,
                    padding: 20,
                    font: {
                        size: 13,
                        family: "'Segoe UI', Arial, sans-serif"
                    },
                    color: '#34495e',
                    generateLabels: (chart) => {
                        const original = Chart.defaults.plugins.legend.labels.generateLabels(chart);
                        // Ordenar: Región -> Vértices -> Óptimo
                        return original.sort((a, b) => b.datasetIndex - a.datasetIndex);
                    }
                }
            },
            tooltip: {
                enabled: true,
                backgroundColor: 'rgba(0, 0, 0, 0.85)',
                titleColor: '#fff',
                bodyColor: '#fff',
                borderColor: 'rgba(255, 255, 255, 0.3)',
                borderWidth: 1,
                padding: 15,
                titleFont: {
                    size: 15,
                    weight: 'bold'
                },
                bodyFont: {
                    size: 14
                },
                displayColors: true,
                callbacks: {
                    title: function(context) {
                        return context[0].dataset.label || 'Punto';
                    },
                    label: function(context) {
                        const punto = context.raw;
                        const labels = [];

                        labels.push(`Coordenadas: (${punto.x.toFixed(4)}, ${punto.y.toFixed(4)})`);

                        // Buscar valor Z si es un vértice
                        const vertice = solucion.vertices?.find(v =>
                            Math.abs(v.x1 - punto.x) < 0.0001 &&
                            Math.abs(v.x2 - punto.y) < 0.0001
                        );

                        if (vertice) {
                            labels.push(`Valor Z: ${vertice.valorZ.toFixed(4)}`);

                            // Indicar si es óptimo
                            if (solucion.puntoOptimo &&
                                Math.abs(vertice.x1 - solucion.puntoOptimo.x1) < 0.0001 &&
                                Math.abs(vertice.x2 - solucion.puntoOptimo.x2) < 0.0001) {
                                labels.push('⭐ Punto Óptimo');
                            }
                        }

                        return labels;
                    }
                }
            }
        },
        scales: {
            x: {
                type: 'linear',
                position: 'bottom',
                min: limits.minX,
                max: limits.maxX,
                title: {
                    display: true,
                    text: 'x₁',
                    font: {
                        size: 18,
                        weight: 'bold',
                        family: "'Segoe UI', Arial, sans-serif"
                    },
                    color: '#34495e',
                    padding: { top: 10 }
                },
                grid: {
                    color: 'rgba(0, 0, 0, 0.08)',
                    lineWidth: 1
                },
                ticks: {
                    font: {
                        size: 13
                    },
                    color: '#555',
                    callback: function(value) {
                        return value.toFixed(2);
                    },
                    maxTicksLimit: 10
                }
            },
            y: {
                type: 'linear',
                min: limits.minY,
                max: limits.maxY,
                title: {
                    display: true,
                    text: 'x₂',
                    font: {
                        size: 18,
                        weight: 'bold',
                        family: "'Segoe UI', Arial, sans-serif"
                    },
                    color: '#34495e',
                    padding: { bottom: 10 }
                },
                grid: {
                    color: 'rgba(0, 0, 0, 0.08)',
                    lineWidth: 1
                },
                ticks: {
                    font: {
                        size: 13
                    },
                    color: '#555',
                    callback: function(value) {
                        return value.toFixed(2);
                    },
                    maxTicksLimit: 10
                }
            }
        },
        interaction: {
            mode: 'nearest',
            intersect: true
        }
    });

    /**
     * Obtiene el título según el tipo de solución
     */
    const getTitulo = (solucion) => {
        const titulos = {
            UNICA: '✅ Región Factible y Punto Óptimo',
            MULTIPLE: '⚠️ Región Factible - Soluciones Múltiples',
            NO_FACTIBLE: '❌ Región No Factible',
            NO_ACOTADO: '⚠️ Región Factible No Acotada'
        };

        return titulos[solucion.tipoSolucion] || 'Gráfica del Problema';
    };

    /**
     * Calcula los límites inteligentes del gráfico
     */
    const calcularLimites = (solucion) => {
        let minX = 0, maxX = 10, minY = 0, maxY = 10;

        const allPoints = [
            ...(solucion.regionFactible || []),
            ...(solucion.vertices || [])
        ];

        if (solucion.puntoOptimo) {
            allPoints.push(solucion.puntoOptimo);
        }

        if (allPoints.length > 0) {
            const xValues = allPoints.map(p => p.x1).filter(v => !isNaN(v));
            const yValues = allPoints.map(p => p.x2).filter(v => !isNaN(v));

            if (xValues.length > 0 && yValues.length > 0) {
                minX = Math.min(...xValues, 0);
                maxX = Math.max(...xValues, 1);
                minY = Math.min(...yValues, 0);
                maxY = Math.max(...yValues, 1);

                // Añadir margen inteligente del 15%
                const marginX = Math.max((maxX - minX) * 0.15, 1);
                const marginY = Math.max((maxY - minY) * 0.15, 1);

                minX = Math.max(0, minX - marginX);
                maxX = maxX + marginX;
                minY = Math.max(0, minY - marginY);
                maxY = maxY + marginY;

                // Redondear a números "bonitos"
                minX = Math.floor(minX);
                maxX = Math.ceil(maxX);
                minY = Math.floor(minY);
                maxY = Math.ceil(maxY);
            }
        }

        console.log('📏 Límites calculados:', { minX, maxX, minY, maxY });

        return { minX, maxX, minY, maxY };
    };

    /**
     * Destruye la gráfica actual
     */
    const destroy = () => {
        if (chartInstance) {
            chartInstance.destroy();
            chartInstance = null;
            console.log('🗑️ Gráfica destruida');
        }
    };

    /**
     * Exporta la gráfica como imagen
     */
    const exportarImagen = (filename = 'grafica-metodo-grafico.png') => {
        if (!chartInstance) {
            console.warn('No hay gráfica para exportar');
            return;
        }

        const url = chartInstance.toBase64Image();
        const link = document.createElement('a');
        link.download = filename;
        link.href = url;
        link.click();

        console.log('💾 Gráfica exportada:', filename);
    };


    /**
     * Calcula dos puntos para dibujar la línea de una restricción
     * Usa la ecuación: coef1*x1 + coef2*x2 = ladoDerecho
     */
    const calcularPuntosRestriccion = (restriccion, limits) => {
        const { coeficienteX1, coeficienteX2, ladoDerecho } = restriccion;

        // Casos especiales
        if (Math.abs(coeficienteX1) < 1e-10 && Math.abs(coeficienteX2) < 1e-10) {
            return []; // Restricción inválida
        }

        // Caso: x1 = constante (línea vertical)
        if (Math.abs(coeficienteX2) < 1e-10) {
            const x = ladoDerecho / coeficienteX1;
            return [
                { x: x, y: limits.minY },
                { x: x, y: limits.maxY }
            ];
        }

        // Caso: x2 = constante (línea horizontal)
        if (Math.abs(coeficienteX1) < 1e-10) {
            const y = ladoDerecho / coeficienteX2;
            return [
                { x: limits.minX, y: y },
                { x: limits.maxX, y: y }
            ];
        }

        // Caso general: calcular y para x = minX y x = maxX
        // coef1*x + coef2*y = ladoDerecho
        // y = (ladoDerecho - coef1*x) / coef2
        const y1 = (ladoDerecho - coeficienteX1 * limits.minX) / coeficienteX2;
        const y2 = (ladoDerecho - coeficienteX1 * limits.maxX) / coeficienteX2;

        return [
            { x: limits.minX, y: y1 },
            { x: limits.maxX, y: y2 }
        ];
    };

    /**
     * Formatea la restricción para mostrar en la leyenda
     */
    const formatearRestriccion = (restriccion, numero) => {
        const { coeficienteX1, coeficienteX2, ladoDerecho, tipo } = restriccion;

        // Símbolo de desigualdad
        let simbolo = '=';
        if (tipo === 'MENOR_IGUAL') simbolo = '≤';
        if (tipo === 'MAYOR_IGUAL') simbolo = '≥';
        if (tipo === 'IGUAL') simbolo = '=';

        // Formatear coeficientes
        let termino1 = '';
        if (Math.abs(coeficienteX1) > 1e-10) {
            termino1 = Math.abs(coeficienteX1) === 1 ? 'x₁' : `${Math.abs(coeficienteX1)}x₁`;
        }

        let termino2 = '';
        if (Math.abs(coeficienteX2) > 1e-10) {
            const signo = coeficienteX2 > 0 ? ' + ' : ' - ';
            termino2 = Math.abs(coeficienteX2) === 1 ?
                `${termino1 ? signo : ''}x₂` :
                `${termino1 ? signo : ''}${Math.abs(coeficienteX2)}x₂`;
        }

        return `R${numero}: ${termino1}${termino2} ${simbolo} ${ladoDerecho}`;
    };

    // API Pública
    return {
        renderChart,
        destroy,
        exportarImagen
    };
})();