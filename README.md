# 📊 Graph Transport Solver - Investigación de Operaciones I

Aplicación web completa para resolver problemas de Investigación de Operaciones I mediante dos métodos fundamentales: **Método Gráfico** (programación lineal con 2 variables) y **Modelo de Transporte** (optimización de distribución). Desarrollada con Spring Boot y una interfaz web moderna e interactiva.

## 📋 Descripción

Esta aplicación educativa permite resolver y visualizar problemas clásicos de Investigación de Operaciones:
- **Método Gráfico**: Resolución visual de problemas de programación lineal con dos variables
- **Modelo de Transporte**: Optimización de costos de distribución con múltiples métodos de solución inicial

Ambos módulos incluyen visualizaciones interactivas, validaciones inteligentes y explicaciones paso a paso del proceso de solución.

## ✨ Características Principales

### 🎯 Método Gráfico
- ✅ **Resolución visual** de problemas de programación lineal (2 variables)
- ✅ **Múltiples tipos de solución**: Única, infinitas, no factible, no acotada
- ✅ **Gráfica interactiva** con Chart.js mostrando región factible
- ✅ **Detección automática** de vértices y punto óptimo
- ✅ **Soporte completo** para restricciones ≤, ≥, =
- ✅ **Validación de región factible** con análisis geométrico
- ✅ **Graficación de función objetivo** en casos de soluciones múltiples
- ✅ **Exportación de la gráfica** en formato PNG

### 🚚 Modelo de Transporte
- ✅ **Tres métodos de solución inicial**: Esquina Noroeste, Costo Mínimo, Vogel
- ✅ **Comparación de métodos** lado a lado con análisis de costos
- ✅ **Balanceo automático** de oferta y demanda
- ✅ **Validación completa** de costos, ofertas y demandas
- ✅ **Visualización en tablas** con códigos de colores
- ✅ **Soporte hasta 50 orígenes/destinos**
- ✅ **Identificación de celdas ficticias** en casos de desbalanceo

### 🎨 Experiencia de Usuario
- ✅ **Diseño moderno** con gradientes azul-rojo
- ✅ **Notificaciones inteligentes** tipo toast con animaciones
- ✅ **Confirmaciones personalizadas** sin alertas del navegador
- ✅ **Header responsive** con transparencia y efectos visuales
- ✅ **Límite de notificaciones** para evitar saturación
- ✅ **Carga dinámica** con indicadores visuales
- ✅ **Scroll automático** en secciones largas

## 🎯 Método Gráfico - Casos de Solución

### 1. **Solución Única**
Un único punto óptimo en un vértice de la región factible

**Ejemplo:**
```
Maximizar: Z = 3x₁ + 5x₂
Sujeto a:
  x₁ ≤ 4
  x₂ ≤ 6
  3x₁ + 2x₂ ≤ 18
  x₁, x₂ ≥ 0
```
**Resultado:** Punto óptimo único en (2, 6) con Z = 36

### 2. **Soluciones Múltiples (Infinitas)**
La función objetivo es paralela a una restricción activa

**Características:**
- Infinitos puntos óptimos en un segmento
- Se grafica la función objetivo para mostrar la coincidencia
- Se muestran los dos vértices extremos del segmento

### 3. **No Factible**
Las restricciones no tienen región de intersección

**Características:**
- Se grafican las restricciones para mostrar la inconsistencia
- Sin región factible sombreada
- Mensaje explicativo del problema

### 4. **No Acotado**
La región factible se extiende al infinito

**Características:**
- Región abierta que permite valores ilimitados
- Detección automática de falta de restricciones de cierre
- Visualización con polígono auxiliar extendido

## 🚚 Modelo de Transporte - Métodos

### 1. **Esquina Noroeste**
Método sistemático que comienza desde la esquina superior izquierda

**Características:**
- Rápido y simple
- No considera costos en la asignación inicial
- Útil para problemas pequeños o introductorios

### 2. **Costo Mínimo**
Asigna priorizando las rutas de menor costo

**Características:**
- Considera costos desde el inicio
- Generalmente mejor solución inicial que Esquina Noroeste
- Óptimo para casos con variación significativa de costos

### 3. **Aproximación de Vogel (VAM)**
Método heurístico basado en penalizaciones

**Características:**
- Calcula penalizaciones (diferencia entre dos menores costos)
- Asigna en rutas con mayor penalización
- Frecuentemente, produce la mejor solución inicial
- Más complejo computacionalmente

### Comparación de Métodos
La aplicación permite resolver el mismo problema con los tres métodos y comparar:
- Costo total de cada solución
- Diferencia porcentual entre métodos
- Visualización lado a lado de asignaciones
- Análisis de eficiencia

## 🛠️ Tecnologías

- **Backend:** Spring Boot 4.0.0, Maven, Java 25
- **Frontend:** Thymeleaf, JavaScript, CSS3, HTML5
- **Gráficas:** Chart.js 4.5.1 (vía WebJars)
- **Arquitectura:** MVC, REST API, Strategy Pattern
- **Diseño:** Gradientes modernos, Glassmorphism, Responsive Design

## 📦 Instalación

### Prerrequisitos
- Java 25 o superior
- Maven 4.0.0+
- Git

### Opción 1: Con IntelliJ IDEA (Recomendado)
*Proyecto desarrollado originalmente en IntelliJ IDEA*

1. **Clonar el repositorio**
```bash
git clone https://github.com/DSGS76/GraphTransportSolver.git
```

2. **Abrir en IntelliJ IDEA**
    - Abre IntelliJ IDEA
    - File → Open → Selecciona la carpeta del proyecto
    - El IDE detectará automáticamente Maven y configurará el proyecto

3. **Ejecutar la aplicación**
    - Ejecuta la clase `GraphTransportSolverApplication`
    - O usa el botón de Run en la interfaz

### Opción 2: Instalación General

1. **Clonar el repositorio**
```bash
git clone https://github.com/DSGS76/GraphTransportSolver.git
cd GraphTransportSolver
```

2. **Instalar dependencias**
```bash
mvn clean install
```

3. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

### Acceso a la aplicación
Una vez ejecutada la aplicación, accede a:
```
http://localhost:5000/graphtransportsolver
```

## 🎯 Uso

### 📊 Método Gráfico

1. **Definir el Problema**
    - Accede desde el menú: "Método Gráfico"
    - Define la función objetivo (coeficientes y tipo: MAX/MIN)
    - Agrega restricciones (mínimo 2) con tipo ≤, ≥ o =
    - La no-negatividad (x₁, x₂ ≥ 0) siempre está activa

2. **Validaciones Automáticas**
    - Coeficientes no nulos en restricciones
    - Mínimo 2 restricciones para formar región
    - Valores del lado derecho válidos

3. **Resolver**
    - Click en "Resolver Problema"
    - Visualización instantánea con gráfica interactiva
    - Resultados detallados:
        - Punto óptimo (si existe)
        - Valor de la función objetivo
        - Vértices de la región factible
        - Restricciones activas
        - Tipo de solución

4. **Interpretar la Gráfica**
    - **Líneas de colores**: Restricciones
    - **Región sombreada azul**: Región factible
    - **Punto verde grande**: Punto óptimo
    - **Puntos grises**: Vértices de la región
    - **Leyenda interactiva**: Clic para ocultar/mostrar elementos

5. **Ejemplos Rápidos**
    - Botón "Cargar Ejemplo" para probar funcionalidad
    - Botón "Limpiar" para reiniciar el formulario

### 🚚 Modelo de Transporte

1. **Configurar el Problema**
    - Accede desde el menú: "Modelo de Transporte"
    - Define número de orígenes (1-50)
    - Define número de destinos (1-50)
    - Personaliza nombres de orígenes y destinos (opcional)

2. **Ingresar Datos**
    - **Matriz de costos**: Costo de envío de cada origen a cada destino
    - **Ofertas**: Capacidad disponible en cada origen
    - **Demandas**: Requerimientos en cada destino

3. **Validaciones**
    - Costos no negativos
    - Ofertas y demandas positivas
    - Balanceo automático si oferta total ≠ demanda total

4. **Resolver**
    - Selecciona método: Esquina Noroeste, Costo Mínimo o Vogel
    - Click en "Resolver Problema"
    - Visualización de la tabla solución:
        - **Celdas verdes**: Asignaciones básicas
        - **Celdas grises**: Celdas no utilizadas
        - **Etiqueta "(Ficticio)"**: Origen/destino agregado por balanceo

5. **Comparar Métodos**
    - Clic en "Comparar Métodos"
    - Visualización lado a lado de los tres métodos
    - Análisis comparativo de costos
    - Identificación del método más eficiente

6. **Ejemplos Rápidos**
    - Botón "Cargar Ejemplo" con problema predefinido
    - Botón "Limpiar" para reiniciar

## 🏗️ Estructura del Proyecto

```
src/main/
├── java/com/io/graphtransportsolver/
│   ├── models/                      # Modelos de dominio
│   │   ├── grafico/                # Punto, Restriccion, ProblemaGrafico, etc.
│   │   └── transporte/             # ProblemaTransporte, SolucionTransporte, Celda
│   ├── algoritmos/                  # Algoritmos de resolución
│   │   ├── grafico/                # CalculadorVertices, EvaluadorFuncionObjetivo
│   │   └── transporte/             # EsquinaNoroesteStrategy, CostoMinimoStrategy, VogelStrategy
│   ├── services/                    # Lógica de negocio
│   │   ├── grafico/                # MetodoGraficoService
│   │   └── transporte/             # ModeloTransporteService, SolucionInicialService
│   ├── presentation/                # Capa de presentación
│   │   ├── controller/             # REST y View Controllers
│   │   └── dto/                    # DTOs de transferencia
│   └── utils/                      # Constantes y utilidades
└── resources/
    ├── static/                      # Recursos estáticos
    │   ├── css/                    # Estilos (main.css, grafico.css, transporte.css)
    │   └── js/                     # JavaScript modular
    │       ├── grafico/            # api.js, chart.js, form.js, main.js
    │       └── transporte/         # api.js, form.js, render.js, main.js
    └── templates/                   # Plantillas Thymeleaf
        ├── fragments/              # Layout components (head, header, footer)
        ├── grafico/                # grafico.html
        ├── transporte/             # transporte.html
        └── index.html              # Página principal
```

## 🔗 API Endpoints

### Base URL
```
http://localhost:5000/graphtransportsolver/api/v1
```

### Método Gráfico

#### Resolver problema gráfico
```http
POST /grafico/resolver
Content-Type: application/json

{
  "funcionObjetivo": {
    "coeficienteX1": 3,
    "coeficienteX2": 5,
    "tipo": "MAXIMIZAR"
  },
  "restricciones": [
    {
      "coeficienteX1": 1,
      "coeficienteX2": 0,
      "tipo": "MENOR_IGUAL",
      "ladoDerecho": 4
    }
  ],
  "incluirNoNegatividad": true
}
```

**Respuesta:**
```json
{
  "success": true,
  "status": 200,
  "message": "OPERACION EXITOSA",
  "data": {
    "puntoOptimo": {
      "x1": 2.0,
      "x2": 6.0,
      "valorZ": 36.0,
      "esFactible": true
    },
    "vertices": [...],
    "regionFactible": [...],
    "restricciones": [...],
    "tipoSolucion": "UNICA"
  },
  "timestamp": "2025-11-27T10:30:00"
}
```

### Modelo de Transporte

#### Resolver con método específico
```http
POST /transporte/resolver
Content-Type: application/json

{
  "origenes": ["O1", "O2"],
  "destinos": ["D1", "D2", "D3"],
  "costos": [[8, 6, 10], [9, 12, 13]],
  "ofertas": [150, 250],
  "demandas": [200, 100, 100],
  "metodo": "VOGEL"
}
```

#### Comparar todos los métodos
```http
POST /transporte/comparar
Content-Type: application/json

{
  "origenes": ["O1", "O2"],
  "destinos": ["D1", "D2", "D3"],
  "costos": [[8, 6, 10], [9, 12, 13]],
  "ofertas": [150, 250],
  "demandas": [200, 100, 100]
}
```

## 🎲 Patrones de Diseño

### Strategy Pattern (Modelo de Transporte)

El modelo de transporte implementa el patrón Strategy para los diferentes métodos de solución inicial:

```java
public interface SolucionInicialStrategy {
    SolucionTransporte resolverSolucionInicial(ProblemaTransporte problema);
}
```

**Implementaciones:**
- `EsquinaNoroesteStrategy`: Método sistemático desde esquina superior izquierda
- `CostoMinimoStrategy`: Asignación basada en costos mínimos
- `VogelStrategy`: Método de penalizaciones (VAM)

### Ventajas del Patrón Strategy
- ✅ **Intercambiabilidad**: Cambiar de método sin modificar código cliente
- ✅ **Extensibilidad**: Agregar nuevos métodos fácilmente
- ✅ **Separación de responsabilidades**: Cada método en su propia clase
- ✅ **Testing**: Pruebas unitarias independientes por método
- ✅ **Comparación**: Facilita la comparación entre métodos

## 📐 Algoritmos Implementados

### Método Gráfico

#### 1. Cálculo de Intersecciones
- Resuelve sistemas de ecuaciones 2x2
- Maneja casos especiales (paralelas, coincidentes)
- Precisión con umbral de tolerancia (1e-9)

#### 2. Determinación de Vértices
- Encuentra todos los puntos de intersección
- Válida factibilidad de cada punto
- Ordena vértices en sentido antihorario
- Elimina duplicados con tolerancia numérica

#### 3. Evaluación de Función Objetivo
- Evalúa Z en cada vértice factible
- Determina punto óptimo según MAX/MIN
- Detecta casos de soluciones múltiples
- Identifica restricciones activas

#### 4. Análisis Geométrico
- Detección de región cerrada vs. abierta
- Cálculo de convexidad
- Análisis de cierre mediante ratios de distancia
- Validación de polígono factible

### Modelo de Transporte

#### 1. Balanceo Automático
- Detecta exceso de oferta o demanda
- Agrega origen/destino ficticio con costo 0
- Marca el problema como balanceado
- Mantiene trazabilidad del balanceo

#### 2. Esquina Noroeste
- Iteración sistemática desde (0,0)
- Asignación máxima posible en cada celda
- Actualización de ofertas/demandas residuales
- O(m + n) complejidad temporal

#### 3. Costo Mínimo
- Ordenación de celdas por costo
- Asignación greedy en orden de menor costo
- Validación de factibilidad en cada paso
- O(mn log(mn)) complejidad temporal

#### 4. Vogel (VAM)
- Cálculo de penalizaciones por fila y columna
- Selección de máxima penalización
- Asignación en celda de mínimo costo de esa fila/columna
- Recalculo iterativo de penalizaciones
- O(m²n² ) complejidad temporal peor caso

## 🎨 Características de UI/UX

### Diseño Visual
- **Paleta de colores**: Azul (#2563eb) y Rojo (#ef4444) como principales
- **Gradientes**: Fondos degradados en header y secciones
- **Glassmorphism**: Efectos de vidrio esmerilado con backdrop-filter
- **Sombras**: Elevación mediante box-shadow sutiles
- **Bordes redondeados**: 12-15px de border-radius

### Notificaciones Inteligentes
```javascript
Notificaciones.mostrar(mensaje, tipo)
```
- **Tipos**: success, error, warning, info
- **Iconos contextuales**: ✅ ❌ ⚠️ ℹ️
- **Barra de progreso**: Indica tiempo restante
- **Autocierre**: 4 segundos con animación
- **Límite de stack**: Máximo 5 notificaciones simultáneas
- **Animaciones**: Slide-in desde la derecha

### Confirmaciones Personalizadas
```javascript
Notificaciones.mostrarConfirmacion(titulo, mensaje, opciones)
```
- Modal con overlay blur
- Botones personalizables
- Escape para cancelar
- Promesas para manejo asíncrono
- Animaciones suaves de entrada/salida

### Responsive Design
- **Breakpoints**: 1200px, 768px, 480px
- **Menu hamburguesa**: En móviles (<768px)
- **Grid adaptativo**: Auto-fit en cards y formularios
- **Scroll inteligente**: En tablas grandes del modelo de transporte
- **Touch-friendly**: Botones con mínimo 44x44px

### Chart.js - Configuración Avanzada
- **Aspect ratio dinámico**: Según tamaño de región
- **Zoom y pan**: Interacción con la gráfica
- **Tooltips personalizados**: Información detallada al hover
- **Leyenda interactiva**: Click para ocultar/mostrar datasets
- **Animaciones**: Transiciones suaves al actualizar
- **Responsive**: Redimensionamiento automático

## 🔧 Configuración

### Límites y Restricciones
- **Método Gráfico**: Exactamente 2 variables de decisión
- **Restricciones**: Mínimo 2, sin máximo definido
- **Modelo de Transporte**: 1-50 orígenes, 1-50 destinos
- **Costos**: No negativos (≥ 0)
- **Ofertas/Demandas**: Positivos (> 0)
- **Precisión numérica**: 1e-9 para comparaciones

## 🧪 Casos de Prueba

### Método Gráfico

#### Caso 1: Solución Única Básica
```
Maximizar: Z = 3x₁ + 5x₂
Restricciones:
  x₁ ≤ 4
  x₂ ≤ 6
  3x₁ + 2x₂ ≤ 18
  x₁, x₂ ≥ 0
```
**Esperado:** Punto óptimo (2, 6), Z = 36

#### Caso 2: Soluciones Múltiples
```
Maximizar: Z = 2x₁ + 2x₂
Restricciones:
  x₁ + x₂ ≤ 10
  x₁ ≤ 6
  x₂ ≤ 8
  x₁, x₂ ≥ 0
```
**Esperado:** Infinitas soluciones en segmento [(2, 8), (6, 4)]

#### Caso 3: No Factible
```
Maximizar: Z = x₁ + x₂
Restricciones:
  x₁ + x₂ ≤ 5
  x₁ + x₂ ≥ 10
  x₁, x₂ ≥ 0
```
**Esperado:** No factible (restricciones contradictorias)

#### Caso 4: No Acotado
```
Maximizar: Z = 3x₁ + 4x₂
Restricciones:
  x₁ + x₂ ≥ 10
  x₁, x₂ ≥ 0
```
**Esperado:** No acotado (región se extiende al infinito)

### Modelo de Transporte

#### Caso 1: Problema Balanceado
```
Orígenes: [O1, O2]
Destinos: [D1, D2, D3]
Costos: [[8, 6, 10], [9, 12, 13]]
Ofertas: [150, 175]
Demandas: [125, 100, 100]
```
**Esperado:** Oferta total (325) = Demanda total (325)

#### Caso 2: Exceso de Oferta
```
Ofertas: [200, 200]
Demandas: [150, 150, 50]
```
**Esperado:** Destino ficticio con demanda 50

#### Caso 3: Déficit de Oferta
```
Ofertas: [100, 100]
Demandas: [150, 100, 100]
```
**Esperado:** Origen ficticio con oferta 150

## 🔒 Validaciones

### Frontend (JavaScript)
```javascript
// Validación de restricciones mínimas
if (restricciones.length < 2) {
    throw new Error('Debe agregar al menos 2 restricciones');
}

// Validación de costos no negativos
if (costo < 0) {
    throw new Error('Los costos no pueden ser negativos');
}
```
### Validaciones de Negocio
- **Región factible**: Debe existir al menos un punto factible
- **Ofertas/Demandas**: Se balancea automáticamente si es necesario
- **Degeneración**: Manejo de soluciones básicas degeneradas
- **Precisión numérica**: Tolerancia para comparaciones de punto flotante

## 📈 Conceptos de Investigación de Operaciones

### Programación Lineal
La aplicación implementa resolución gráfica del problema general:
```
Optimizar: Z = c₁x₁ + c₂x₂
Sujeto a:
  a₁₁x₁ + a₁₂x₂ {≤, ≥, =} b₁
  a₂₁x₁ + a₂₂x₂ {≤, ≥, =} b₂
  ...
  xⱼ ≥ 0, ∀j
```

**Teorema Fundamental:** Si existe solución óptima, esta se encuentra en un vértice de la región factible.

### Problema de Transporte
Caso especial de programación lineal:
```
Minimizar: Z = Σᵢ Σⱼ cᵢⱼxᵢⱼ
Sujeto a:
  Σⱼ xᵢⱼ = sᵢ, ∀i (restricciones de oferta)
  Σᵢ xᵢⱼ = dⱼ, ∀j (restricciones de demanda)
  xᵢⱼ ≥ 0, ∀i,j
```

**Propiedad:** Un problema balanceado de m orígenes y n destinos tiene exactamente m+n-1 variables básicas en la solución.

## 👨‍💻 Autor

**Duvan Gil** - [GitHub](https://github.com/DSGS76)

## 🙏 Agradecimientos

- Spring Boot Team por el excelente framework
- Chart.js por la biblioteca de gráficas interactivas
- Comunidad de Investigación de Operaciones
- Profesores y estudiantes que inspiran mejoras continuas

## 📚 Referencias

- **Taha, H. A.** (2017). *Investigación de Operaciones* (10ª ed.). Pearson.
- **Hillier, F. S., & Lieberman, G. J.** (2015). *Introducción a la Investigación de Operaciones* (10ª ed.). McGraw-Hill.
- **Winston, W. L.** (2022). *Operations Research: Applications and Algorithms* (5ª ed.). Cengage Learning.

---

⭐ **Si este proyecto te fue útil para aprender o enseñar Investigación de Operaciones, ¡no olvides darle una estrella!**

🎓 **Uso educativo:** Este proyecto está diseñado con fines educativos y puede ser usado libremente en cursos de Investigación de Operaciones

