/**
 * Módulo para renderizar gráficas con Chart.js
 * Maneja la visualización profesional de la región factible y el punto óptimo
 *
 * @author Duvan Gil
 * @version 2.2 - Correcciones: región sombreada, no acotada, función objetivo
 */
const ChartManager = (() => {
    let chartInstance = null;
    let regionOpacity = 1.0; // Opacidad actual de la región factible (para animaciones)

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

        // Resetear opacidad de región factible
        regionOpacity = 1.0;

        // Preparar datos para la gráfica
        const datasets = prepararDatasets(solucion);
        const limits = calcularLimites(solucion);

        // Configuración de la gráfica
        const config = {
            type: 'scatter',
            data: { datasets },
            options: crearOpciones(solucion, limits),
            plugins: [regionFactiblePlugin]
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
        console.log('Tipo de solución:', solucion.tipoSolucion);
        console.log('Vértices región factible:', solucion.regionFactible);

        // 0. Líneas de restricciones (primero, para que queden atrás)
        if (solucion.restricciones && solucion.restricciones.length > 0) {
            console.log(`✅ Procesando ${solucion.restricciones.length} restricciones...`);
            solucion.restricciones.forEach((restriccion, index) => {
                const puntos = calcularPuntosRestriccion(restriccion, limits);
                if (puntos && puntos.length > 0) {
                    const hue = (index * 360 / Math.max(solucion.restricciones.length, 6)) % 360;
                    const color = `hsla(${hue}, 70%, 55%, 0.85)`;
                    const label = formatearRestriccion(restriccion, index + 1);

                    datasets.push({
                        label: label,
                        data: puntos,
                        type: 'line',
                        borderColor: color,
                        backgroundColor: 'transparent',
                        borderWidth: 2,
                        borderDash: [5, 5],
                        pointRadius: 0,
                        showLine: true,
                        fill: false,
                        order: 10
                    });
                }
            });
        }

        // Manejo mejorado de la región factible
        if (solucion.regionFactible && solucion.regionFactible.length > 0) {
            const regionDataset = crearDatasetRegionFactible(solucion, limits);
            if (regionDataset) {
                console.log('📐 Dataset región factible:', regionDataset);
                datasets.push(regionDataset);
            }
        }

        // Función objetivo pasando por el punto óptimo
        if (solucion.puntoOptimo && (solucion.tipoSolucion === 'UNICA' || solucion.tipoSolucion === 'MULTIPLE')) {
            const funcionObjetivoData = calcularLineaFuncionObjetivo(
                solucion.puntoOptimo,
                limits
            );

            if (funcionObjetivoData.length > 0) {
                const coefX1 = parseFloat(document.getElementById('coefZ_x1')?.value || 0);
                const coefX2 = parseFloat(document.getElementById('coefZ_x2')?.value || 0);
                const valorZ = solucion.puntoOptimo.valorZ;
                const labelFO = formatearFuncionObjetivo(coefX1, coefX2, valorZ);

                datasets.push({
                    type: 'line',
                    label: labelFO,
                    data: funcionObjetivoData,
                    borderColor: 'rgba(139, 0, 139, 0.9)',
                    backgroundColor: 'transparent',
                    borderWidth: 4,
                    borderDash: [15, 8],
                    pointRadius: 0,
                    showLine: true,
                    fill: false,
                    order: 5
                });
            }
        }

        // Vértices (todos los puntos)
        if (solucion.vertices && solucion.vertices.length > 0) {
            datasets.push({
                type: 'scatter',
                label: 'Vértices',
                data: solucion.vertices.map(p => ({ x: p.x1, y: p.x2 })),
                backgroundColor: 'rgba(100, 116, 139, 0.8)',
                borderColor: 'rgba(71, 85, 105, 1)',
                borderWidth: 2,
                pointRadius: 8,
                pointHoverRadius: 10,
                pointStyle: 'circle',
                showLine: false,
                order: 2
            });
        }

        // Punto óptimo (destacado)
        if (solucion.puntoOptimo) {
            datasets.push({
                type: 'scatter',
                label: '⭐ Punto Óptimo',
                data: [{ x: solucion.puntoOptimo.x1, y: solucion.puntoOptimo.x2 }],
                backgroundColor: 'rgb(50,220,38)',
                borderColor: 'rgb(38,185,28)',
                borderWidth: 4,
                pointRadius: 14,
                pointHoverRadius: 16,
                pointStyle: 'star',
                showLine: false,
                order: 1
            });
        }

        console.log(`📊 Total datasets preparados: ${datasets.length}`);
        return datasets;
    };

    /**
     * Analiza geométricamente si los vértices forman un polígono cerrado
     */
    const analizarGeometriaRegion = (vertices) => {
        if (!vertices || vertices.length < 3) {
            return {
                esPoligonoCerrado: false,
                esConvexa: false,
                razon: 'Menos de 3 vértices'
            };
        }

        // 1. CÁLCULO CORRECTO: Calcular TODAS las distancias entre vértices consecutivos
        const distancias = [];

        for (let i = 0; i < vertices.length - 1; i++) {
            const v1 = vertices[i];
            const v2 = vertices[i + 1];
            const dist = Math.sqrt(
                Math.pow(v2.x - v1.x, 2) +
                Math.pow(v2.y - v1.y, 2)
            );
            distancias.push(dist);
        }

        // Distancia de cierre (último → primero)
        const primerVertice = vertices[0];
        const ultimoVertice = vertices[vertices.length - 1];
        const distanciaCierre = Math.sqrt(
            Math.pow(ultimoVertice.x - primerVertice.x, 2) +
            Math.pow(ultimoVertice.y - primerVertice.y, 2)
        );

        // ✅ CORRECCIÓN: Incluir distancia de cierre en el cálculo del promedio
        const todasLasDistancias = [...distancias, distanciaCierre];
        const sumaDistancias = todasLasDistancias.reduce((sum, d) => sum + d, 0);
        const longitudPromedioLado = sumaDistancias / todasLasDistancias.length;

        // Ratio: ¿El cierre es similar a los demás lados?
        const ratioDistancia = distanciaCierre / longitudPromedioLado;

        // Un polígono está cerrado si el lado de cierre es <= 5x el promedio
        const esPoligonoCerrado = ratioDistancia <= 5.0;

        console.log('📐 Análisis de cierre:', {
            numVertices: vertices.length,
            distancias: distancias.map(d => d.toFixed(2)),
            distanciaCierre: distanciaCierre.toFixed(2),
            longitudPromedio: longitudPromedioLado.toFixed(2),
            ratio: ratioDistancia.toFixed(3),
            resultado: esPoligonoCerrado ? 'CERRADO ✅' : 'ABIERTO ❌'
        });

        // 2. Verificar convexidad usando productos cruzados
        let esConvexa = true;
        if (vertices.length >= 3) {
            let signoPrevio = 0;

            for (let i = 0; i < vertices.length; i++) {
                const p1 = vertices[i];
                const p2 = vertices[(i + 1) % vertices.length];
                const p3 = vertices[(i + 2) % vertices.length];

                // Producto cruzado de vectores (p1->p2) y (p2->p3)
                const dx1 = p2.x - p1.x;
                const dy1 = p2.y - p1.y;
                const dx2 = p3.x - p2.x;
                const dy2 = p3.y - p2.y;

                const productoCruzado = dx1 * dy2 - dy1 * dx2;

                if (Math.abs(productoCruzado) > 1e-6) {
                    const signoActual = Math.sign(productoCruzado);

                    if (signoPrevio !== 0 && signoActual !== signoPrevio) {
                        esConvexa = false;
                        break;
                    }
                    signoPrevio = signoActual;
                }
            }
        }

        // 3. Verificar si hay vértices en el "infinito" (región no acotada)
        const UMBRAL_INFINITO = 1e6;
        const hayVerticesInfinitos = vertices.some(v =>
            Math.abs(v.x) > UMBRAL_INFINITO || Math.abs(v.y) > UMBRAL_INFINITO
        );

        return {
            esPoligonoCerrado: esPoligonoCerrado && !hayVerticesInfinitos,
            esConvexa,
            distanciaCierre,
            ratioDistancia,
            longitudPromedioLado,
            hayVerticesInfinitos,
            razon: esPoligonoCerrado
                ? `Polígono cerrado (ratio: ${ratioDistancia.toFixed(3)})`
                : `Polígono abierto (ratio: ${ratioDistancia.toFixed(3)})`
        };
    };

    /**
     * Crea el dataset de región factible con análisis geométrico
     */
    const crearDatasetRegionFactible = (solucion, limits) => {
        const regionData = solucion.regionFactible.map(p => ({ x: p.x1, y: p.x2 }));

        if (regionData.length === 0) {
            return null; // No hay vértices
        }

        // ✅ CASO ESPECIAL: Solo 1 punto - Región no acotada desde un punto hacia el infinito
        if (regionData.length === 1) {
            const puntoInicial = regionData[0];

            // Crear un área que se extiende desde el punto hacia el infinito
            // (en la práctica, hacia los límites del gráfico)
            const areaNoAcotada = [
                puntoInicial,
                { x: limits.maxX, y: puntoInicial.y },
                { x: limits.maxX, y: limits.maxY },
                { x: puntoInicial.x, y: limits.maxY },
                puntoInicial // Cerrar el polígono
            ];

            console.log('🔍 Región no acotada desde punto único:', {
                puntoInicial,
                vertices: areaNoAcotada.length,
                tipoSolucion: solucion.tipoSolucion
            });

            return {
                type: 'line',
                label: 'Región Factible (No Acotada)',
                data: areaNoAcotada,
                backgroundColor: 'rgba(37, 99, 235, 0.15)',
                borderColor: 'rgba(37, 99, 235, 0.6)',
                borderWidth: 2,
                borderDash: [10, 5],
                pointRadius: 0,
                pointHoverRadius: 0,
                fill: {
                    target: 'origin',
                    above: 'rgba(37, 99, 235, 0.15)'
                },
                showLine: true,
                tension: 0,
                order: 3
            };
        }

        // Análisis geométrico exhaustivo para 2+ vértices
        const analisis = analizarGeometriaRegion(regionData);

        console.log('🔍 Análisis geométrico de región:', {
            vertices: regionData.length,
            esPoligonoCerrado: analisis.esPoligonoCerrado,
            esConvexa: analisis.esConvexa,
            razon: analisis.razon,
            tipoSolucion: solucion.tipoSolucion
        });

        // Decisión inteligente sobre cómo renderizar
        if (analisis.esPoligonoCerrado) {
            // CASO 1: Polígono cerrado - asegurar cierre explícito duplicando primer vértice
            const polygonData = [...regionData, regionData[0]];

            return {
                type: 'line',
                label: 'Región Factible',
                data: polygonData,
                backgroundColor: 'transparent', // El plugin maneja el relleno
                borderColor: 'rgba(37, 99, 235, 0.8)',
                borderWidth: 3,
                pointRadius: 0,
                pointHoverRadius: 0,
                fill: false, // Desactivar fill nativo
                showLine: true,
                tension: 0,
                order: 3
            };
        } else {
            // CASO 2: Región abierta o no acotada
            // Estrategia: crear un polígono auxiliar hacia el "infinito"

            if (regionData.length === 2) {
                // Caso especial: solo 2 vértices (línea)
                return {
                    type: 'line',
                    label: 'Región Factible (No Acotada)',
                    data: regionData,
                    backgroundColor: 'rgba(37, 99, 235, 0.15)',
                    borderColor: 'rgba(37, 99, 235, 0.6)',
                    borderWidth: 2,
                    borderDash: [10, 5],
                    pointRadius: 0,
                    fill: false,
                    showLine: true,
                    order: 3
                };
            }

            // Crear polígono extendido hasta los límites del gráfico
            const polygonExtendido = crearPoligonoExtendido(regionData, limits);

            return {
                type: 'line',
                label: 'Región Factible (No Acotada)',
                data: polygonExtendido,
                backgroundColor: 'rgba(37, 99, 235, 0.15)',
                borderColor: 'rgba(37, 99, 235, 0.6)',
                borderWidth: 2,
                pointRadius: 0,
                pointHoverRadius: 0,
                fill: {
                    target: 'origin',
                    above: 'rgba(37, 99, 235, 0.15)'
                },
                showLine: true,
                tension: 0,
                order: 3
            };
        }
    };

    /**
     * Extiende un polígono abierto hasta los límites del gráfico
     */
    const crearPoligonoExtendido = (vertices, limits) => {
        // Encontrar los vértices más extremos
        const verticeInferiorIzq = { x: limits.minX, y: limits.minY };
        const verticeInferiorDer = { x: limits.maxX, y: limits.minY };
        const verticeSuperiorDer = { x: limits.maxX, y: limits.maxY };
        const verticeSuperiorIzq = { x: limits.minX, y: limits.maxY };

        // Identificar qué bordes del gráfico están "abiertos"
        const xMin = Math.min(...vertices.map(v => v.x));
        const xMax = Math.max(...vertices.map(v => v.x));
        const yMin = Math.min(...vertices.map(v => v.y));
        const yMax = Math.max(...vertices.map(v => v.y));

        const margen = 0.1;
        const tocaBordeIzq = (xMin - limits.minX) < margen;
        const tocaBordeDer = (limits.maxX - xMax) < margen;
        const tocaBordeInf = (yMin - limits.minY) < margen;
        const tocaBordeSup = (limits.maxY - yMax) < margen;

        // Construir polígono extendido
        const poligonoExtendido = [...vertices];

        // Conectar al borde correspondiente
        if (tocaBordeDer && tocaBordeInf) {
            poligonoExtendido.push(verticeInferiorDer);
        }
        if (tocaBordeInf && tocaBordeIzq) {
            poligonoExtendido.push(verticeInferiorIzq);
        }
        if (tocaBordeIzq && tocaBordeSup) {
            poligonoExtendido.push(verticeSuperiorIzq);
        }
        if (tocaBordeSup && tocaBordeDer) {
            poligonoExtendido.push(verticeSuperiorDer);
        }

        return poligonoExtendido;
    };

    /**
     * Calcula la línea de la función objetivo que pasa por el punto óptimo
     */
    const calcularLineaFuncionObjetivo = (puntoOptimo, limits) => {
        const coefX1 = parseFloat(document.getElementById('coefZ_x1')?.value || 0);
        const coefX2 = parseFloat(document.getElementById('coefZ_x2')?.value || 0);
        const valorZ = puntoOptimo.valorZ;

        if (Math.abs(coefX1) < 1e-10 && Math.abs(coefX2) < 1e-10) {
            return [];
        }

        // Caso 1: Línea vertical (coefX2 ≈ 0)
        if (Math.abs(coefX2) < 1e-10) {
            const x = valorZ / coefX1;
            return [
                { x: x, y: limits.minY },
                { x: x, y: limits.maxY }
            ];
        }

        // Caso 2: Línea horizontal (coefX1 ≈ 0)
        if (Math.abs(coefX1) < 1e-10) {
            const y = valorZ / coefX2;
            return [
                { x: limits.minX, y: y },
                { x: limits.maxX, y: y }
            ];
        }

        // Caso 3: Línea general
        const y1 = (valorZ - coefX1 * limits.minX) / coefX2;
        const y2 = (valorZ - coefX1 * limits.maxX) / coefX2;

        return [
            { x: limits.minX, y: y1 },
            { x: limits.maxX, y: y2 }
        ];
    };

    /**
     * Formatea la función objetivo para mostrar en la leyenda
     */
    const formatearFuncionObjetivo = (coefX1, coefX2, valorZ) => {
        let ecuacion = 'Z = ';

        if (Math.abs(coefX1) > 1e-10) {
            if (Math.abs(coefX1) === 1) {
                ecuacion += coefX1 > 0 ? 'x₁' : '-x₁';
            } else {
                ecuacion += `${coefX1}x₁`;
            }
        }

        if (Math.abs(coefX2) > 1e-10) {
            if (ecuacion !== 'Z = ') {
                ecuacion += coefX2 > 0 ? ' + ' : ' - ';
                ecuacion += Math.abs(coefX2) === 1 ? 'x₂' : `${Math.abs(coefX2)}x₂`;
            } else {
                ecuacion += Math.abs(coefX2) === 1 ?
                    (coefX2 > 0 ? 'x₂' : '-x₂') :
                    `${coefX2}x₂`;
            }
        }

        ecuacion += ` = ${valorZ.toFixed(2)}`;
        return ecuacion;
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

                const marginX = Math.max((maxX - minX) * 0.15, 1);
                const marginY = Math.max((maxY - minY) * 0.15, 1);

                minX = Math.max(0, minX - marginX);
                maxX = maxX + marginX;
                minY = Math.max(0, minY - marginY);
                maxY = maxY + marginY;

                minX = Math.floor(minX);
                maxX = Math.ceil(maxX);
                minY = Math.floor(minY);
                maxY = Math.ceil(maxY);
            }
        }

        return { minX, maxX, minY, maxY };
    };

    /**
     * Plugin personalizado para dibujar la región factible sombreada con animación suave
     */
    const regionFactiblePlugin = {
        id: 'regionFactiblePlugin',

        beforeDatasetsDraw: (chart) => {
            const { ctx, scales } = chart;

            // Buscar el índice del dataset de región factible (cerrada o no acotada)
            let regionDatasetIndex = chart.data.datasets.findIndex(ds => ds.label === 'Región Factible');

            // Si no hay región cerrada, buscar región no acotada
            if (regionDatasetIndex === -1) {
                regionDatasetIndex = chart.data.datasets.findIndex(ds => ds.label === 'Región Factible (No Acotada)');
            }

            if (regionDatasetIndex === -1) {
                regionOpacity = 0; // Resetear opacidad
                return; // No hay dataset de región factible
            }

            const regionDataset = chart.data.datasets[regionDatasetIndex];
            const meta = chart.getDatasetMeta(regionDatasetIndex);

            if (!regionDataset.data || regionDataset.data.length < 3) {
                regionOpacity = 0; // Resetear opacidad
                return; // No hay suficientes puntos para formar un polígono
            }

            // ✅ DETERMINAR OPACIDAD OBJETIVO
            const targetOpacity = (meta && meta.hidden) ? 0 : 1;

            // ✅ ANIMAR SUAVEMENTE LA OPACIDAD
            const opacitySpeed = 0.15; // Velocidad de transición (más alto = más rápido)

            if (Math.abs(regionOpacity - targetOpacity) > 0.01) {
                // Interpolar hacia la opacidad objetivo
                regionOpacity += (targetOpacity - regionOpacity) * opacitySpeed;

                // Forzar actualización continua durante la animación
                setTimeout(() => chart.update('none'), 16); // ~60fps
            } else {
                regionOpacity = targetOpacity;
            }

            // No dibujar si la opacidad es muy baja
            if (regionOpacity < 0.01) {
                return;
            }

            ctx.save();
            ctx.beginPath();

            // Dibujar el polígono
            regionDataset.data.forEach((point, index) => {
                const x = scales.x.getPixelForValue(point.x);
                const y = scales.y.getPixelForValue(point.y);

                if (index === 0) {
                    ctx.moveTo(x, y);
                } else {
                    ctx.lineTo(x, y);
                }
            });

            ctx.closePath();

            // Rellenar con color semitransparente y opacidad animada suavemente
            const fillOpacity = 0.25 * regionOpacity;
            ctx.fillStyle = `rgba(37, 99, 235, ${fillOpacity})`;
            ctx.fill();

            ctx.restore();
        }
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

                        const vertice = solucion.vertices?.find(v =>
                            Math.abs(v.x1 - punto.x) < 0.0001 &&
                            Math.abs(v.x2 - punto.y) < 0.0001
                        );

                        if (vertice) {
                            labels.push(`Valor Z: ${vertice.valorZ.toFixed(4)}`);

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
     * Calcula dos puntos para dibujar la línea de una restricción
     */
    const calcularPuntosRestriccion = (restriccion, limits) => {
        const { coeficienteX1, coeficienteX2, ladoDerecho } = restriccion;

        if (Math.abs(coeficienteX1) < 1e-10 && Math.abs(coeficienteX2) < 1e-10) {
            return [];
        }

        if (Math.abs(coeficienteX2) < 1e-10) {
            const x = ladoDerecho / coeficienteX1;
            return [
                { x: x, y: limits.minY },
                { x: x, y: limits.maxY }
            ];
        }

        if (Math.abs(coeficienteX1) < 1e-10) {
            const y = ladoDerecho / coeficienteX2;
            return [
                { x: limits.minX, y: y },
                { x: limits.maxX, y: y }
            ];
        }

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

        let simbolo = '=';
        if (tipo === 'MENOR_IGUAL') simbolo = '≤';
        if (tipo === 'MAYOR_IGUAL') simbolo = '≥';
        if (tipo === 'IGUAL') simbolo = '=';

        let termino1 = '';
        if (Math.abs(coeficienteX1) > 1e-10) {
            if (coeficienteX1 === 1) {
                termino1 = 'x₁';
            } else if (coeficienteX1 === -1) {
                termino1 = '-x₁';
            } else {
                termino1 = `${coeficienteX1}x₁`;
            }
        }

        let termino2 = '';
        if (Math.abs(coeficienteX2) > 1e-10) {
            if (termino1) {
                // Si ya hay un primer término, agregar el signo
                termino2 = coeficienteX2 > 0 ? ' + ' : ' - ';
            } else {
                // Si es el primer término, mostrar el signo negativo si aplica
                termino2 = coeficienteX2 < 0 ? '-' : '';
            }

            if (Math.abs(coeficienteX2) === 1) {
                termino2 += 'x₂';
            } else {
                termino2 += `${Math.abs(coeficienteX2)}x₂`;
            }
        }

        return `R${numero}: ${termino1}${termino2} ${simbolo} ${ladoDerecho}`;
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
            if (typeof Notificaciones !== 'undefined') {
                Notificaciones.warning('No hay gráfica disponible para exportar');
            }
            return;
        }

        try {
            const url = chartInstance.toBase64Image();
            const link = document.createElement('a');
            link.download = filename;
            link.href = url;
            link.click();

            console.log('💾 Gráfica exportada:', filename);
            if (typeof Notificaciones !== 'undefined') {
                Notificaciones.success('Gráfica exportada exitosamente como ' + filename);
            }
        } catch (error) {
            console.error('❌ Error al exportar:', error);
            if (typeof Notificaciones !== 'undefined') {
                Notificaciones.error('Error al exportar la gráfica: ' + error.message);
            }
        }
    };

    // API Pública
    return {
        renderChart,
        destroy,
        exportarImagen
    };
})();