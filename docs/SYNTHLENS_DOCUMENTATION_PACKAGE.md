# SYNTHLENS — Paquete de Documentación Completo

> **Versión**: 2.0  
> **Fecha**: 23 de Junio de 2026  
> **Plataforma**: Android (Kotlin + Jetpack Compose)  
> **Estado**: Desarrollo Activo

---

# ═══════════════════════════════════════════════════════════════════════════════
# PARTE 1: HANDOFF PACKAGE
# ═══════════════════════════════════════════════════════════════════════════════

## 1.1 Resumen Ejecutivo

**SynthLens** es una aplicación Android que detecta sintetizadores de audio en tiempo real mediante análisis de señales acústicas. Utiliza procesamiento de señales (FFT, autocorrelación, clasificación de forma de onda), separación de stems espectrales y clasificación ML (TFLite) para identificar más de 33 modelos de sintetizadores de 12+ marcas comerciales, además de detectar handpans.

**Propuesta de Valor**: La app funciona como un "Shazam para sintetizadores" — apunta el micrófono a un sintetizador sonando y lo identifica con su configuración técnica completa.

**Modelo de Negocio**: Biblioteca de referencia con enlaces de compra oficiales → las marcas promocionan la app.

## 1.2 Stack Tecnológico

| Componente | Tecnología | Versión |
|---|---|---|
| Lenguaje | Kotlin | 2.2.10 |
| UI Framework | Jetpack Compose + Material3 | BOM 2026.02.01 |
| Base de Datos | Room + KSP | 2.8.0 |
| Cámara | CameraX | 1.4.1 |
| ML Runtime | TensorFlow Lite | 2.16.1 |
| Build | Gradle Kotlin DSL | AGP 9.2.1 |
| SDK Target | Android API 36 | minSdk 33 |
| Navigation | Navigation Compose | 2.7.7 |

## 1.3 Estructura del Proyecto

```
MyApplication/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/
│       │   └── synth_model.tflite          # Modelo ML (placeholder)
│       └── java/com/example/myapplication/
│           ├── MainActivity.kt              # Entry point + NavHost
│           ├── engine/                      # Motor de análisis de audio
│           │   ├── AudioEngine.kt           # Core: grabación + FFT + detección (1187 líneas)
│           │   ├── StemSeparator.kt         # Separación espectral 4-band (454 líneas)
│           │   ├── StemAnalyzer.kt          # Detección por-stem (341 líneas)
│           │   ├── SynthMLClassifier.kt     # Clasificación TFLite + heurísticas (463 líneas)
│           │   └── HandpanDetector.kt       # Detección de handpans (410 líneas)
│           ├── data/                        # Capa de persistencia
│           │   ├── local/
│           │   │   ├── Entities.kt          # 6 entidades Room (131 líneas)
│           │   │   ├── Daos.kt              # 6 DAOs (157 líneas)
│           │   │   ├── SynthDatabase.kt     # DB singleton v4 + migraciones (145 líneas)
│           │   │   └── Mappers.kt           # Entity ↔ Domain (205 líneas)
│           │   ├── SynthRepository.kt       # Repositorio central (202 líneas)
│           │   ├── SynthEntities.kt         # Modelos de dominio (102 líneas)
│           │   ├── SynthDatabaseSeeder.kt   # Seeder con 30+ sintetizadores (701+ líneas)
│           │   └── AchievementRepository.kt # 20 logros en 7 categorías (235 líneas)
│           ├── viewmodel/
│           │   └── SynthViewModel.kt        # AndroidViewModel (227 líneas)
│           ├── ui/
│           │   ├── navigation/
│           │   │   └── Screen.kt            # 13 rutas
│           │   ├── screens/                 # 14 pantallas
│           │   │   ├── AnalyzerScreen.kt
│           │   │   ├── CameraScannerScreen.kt
│           │   │   ├── SpectralScreen.kt
│           │   │   ├── LibraryScreen.kt
│           │   │   ├── SynthDetailScreen.kt
│           │   │   ├── ProfileScreen.kt
│           │   │   ├── AnalysisDetailsScreen.kt
│           │   │   ├── HistoryScreen.kt
│           │   │   ├── ABCompareScreen.kt
│           │   │   ├── ExportScreen.kt
│           │   │   ├── StageModeScreen.kt
│           │   │   ├── DAWIntegrationScreen.kt
│           │   │   ├── SettingsScreen.kt
│           │   │   └── AchievementsScreen.kt
│           │   ├── components/              # Componentes UI reutilizables
│           │   │   ├── SynthComponents.kt   # Panel, Knob, Slider, Oscilloscope, Spectrum, LED, Patch
│           │   │   ├── GlassComponents.kt   # Glassmorphism: Panel, Card, Button, Chip
│           │   │   ├── SpectralVisualizer.kt # 6 visualizadores espectrales
│           │   │   ├── StemVisualizer.kt    # Visualización neon de stems
│           │   │   ├── ReactiveEffects.kt   # Efectos reactivos al audio
│           │   │   ├── SynthVisualIcon.kt   # Iconos por marca (6 patrones)
│           │   │   ├── HandpanInfoPanel.kt  # Panel detallado de handpan
│           │   │   ├── BrightnessDetector.kt # Sensor de luz ambiental
│           │   │   └── ...
│           │   └── theme/
│           │       ├── Color.kt             # Paleta completa (44 colores)
│           │       ├── Theme.kt             # Tema oscuro + LocalIsBright
│           │       └── Type.kt              # Tipografía (10 estilos)
```

## 1.4 Pantallas y Navegación

**Bottom Navigation (5 tabs principales)**:
1. **AUDIO** (default) → AnalyzerScreen — Análisis en tiempo real
2. **SCAN** → CameraScannerScreen — Escáner con cámara
3. **SPECTRUM** → SpectralScreen — 3 modos de visualización
4. **LIBRARY** → LibraryScreen — Biblioteca de sintetizadores
5. **SPECS** → ProfileScreen — Perfil del usuario + acceso a features

**Sub-pantallas (accesibles desde ProfileScreen)**:
- AnalysisDetailsScreen — Detalle de análisis técnico
- HistoryScreen — Historial de detecciones (estilo Shazam)
- ABCompareScreen — Comparación lado a lado
- ExportScreen — Exportación a redes sociales
- StageModeScreen — Modo escenario (botones grandes)
- DAWIntegrationScreen — Integración MIDI/OSC
- SettingsScreen — Configuración de audio
- AchievementsScreen — Logros gamificados

**Patrón de Navegación**: `selectedSynth` overlay pattern — el detalle se muestra cuando state != null, pop back lo pone en null (evita serialización de argumentos NavHost).

## 1.5 Motor de Detección — Flujo de Datos

```
Micrófono (AudioRecord 44.1kHz mono 16-bit)
    ↓
DC Offset Removal → Noise Gate → Gain Normalization
    ↓
┌─────────────────────────────────────────────────┐
│  Análisis Paralelo:                             │
│  ├── RMS/Peak/Frecuencia (YIN + autocorrelación)│
│  ├── Clasificación de Forma de Onda (crest+duty) │
│  ├── FFT 2048-point (Hamming window)            │
│  ├── Detección de Armónicos (16 armónicos)       │
│  ├── THD, HNR, Flatness, Rolloff, Bandwidth     │
│  ├── Perfil Armónico (oddEvenRatio, decay, etc.) │
│  ├── 33 Scorers Heurísticos (por sintetizador)   │
│  ├── ML Classifier (TFLite, 47 labels, 18 feats) │
│  ├── Stem Separation (cada 8 frames)            │
│  │   └── 4-band → StemAnalyzer (22 profiles)     │
│  └── Handpan Detector (12 instrument profiles)   │
└─────────────────────────────────────────────────┘
    ↓
AudioAnalysis (StateFlow) → UI Composables
```

## 1.6 Base de Datos — Esquema

**Versión**: 4 | **Entidades**: 6 | **Migraciones**: 3 (sin destructive migration)

| Entidad | Tabla | Propósito |
|---|---|---|
| DetectedSynthEntity | `detected_synths` | Detecciones guardadas |
| SynthLibraryEntity | `synth_library` | Catálogo de 30+ sintetizadores |
| DetectionHistoryEntity | `detection_history` | Historial tipo Shazam |
| AudioRecordingEntity | `audio_recordings` | Grabaciones offline |
| ABComparisonEntity | `ab_comparisons` | Comparaciones A/B |
| AchievementEntity | `achievements` | 20 logros gamificados |

**Migraciones**:
- MIGRATION_1_2: ALTER TABLE (8 columnas a synth_library + 5 a detected_synths)
- MIGRATION_2_3: CREATE TABLE (history, recordings, comparisons)
- MIGRATION_3_4: CREATE TABLE (achievements)

## 1.7 Sintetizadores en Base de Datos

| Marca | Modelos | Categoría |
|---|---|---|
| **Moog** | Minimoog Model D, Grandmother, Matriarch, Subsequent 37, Subsequent 25, One | Analógico |
| **Korg** | MS-20, Minilogue XD, Prologue, wavestate, opsix, Monologue | Analógico/Digital/FM |
| **Arturia** | MatrixBrute, MiniBrute 2S, PolyBrute, MicroFreak | Analógico/Híbrido |
| **Novation** | Peak, Summit, Bass Station II | Analógico/Híbrido |
| **Roland** | JUNO-106, SH-101, TB-303, JUPITER-8, SYSTEM-8 | Analógico |
| **Sequential** | Prophet-6, Prophet-5, OB-X8 | Analógico |
| **Behringer** | TD-3, DeepMind 12, Model D | Analógico Clon |
| **Elektron** | Analog Four, Digitone | Analógico/FM |

## 1.8 Dependencias Críticas

```kotlin
// Compose
implementation(platform("androidx.compose:compose-bom:2026.02.01"))
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.7.7")

// Room + KSP
implementation("androidx.room:room-runtime:2.8.0")
implementation("androidx.room:room-ktx:2.8.0")
ksp("androidx.room:room-compiler:2.8.0")

// CameraX
implementation("androidx.camera:camera-core:1.4.1")
implementation("androidx.camera:camera-camera2:1.4.1")
implementation("androidx.camera:camera-lifecycle:1.4.1")
implementation("androidx.camera:camera-view:1.4.1")

// ML
implementation("org.tensorflow:tensorflow-lite:2.16.1")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
```

## 1.9 Instrucciones de Build

```bash
# Compilar APK debug
JAVA_HOME=/home/DexTer/android-studio/jbr ./gradlew assembleDebug

# Instalar en emulador
adb install app/build/outputs/apk/debug/app-debug.apk

# Emulador disponible
# AVD: SynthLens_Test (Pixel 7, Android 34, x86_64)
```

## 1.10 Pendientes / Known Issues

| # | Issue | Prioridad |
|---|---|---|
| 1 | Tabs griseados en navegación (SCAN, SPECTRUM, LIBRARY, SPECS) | Alta |
| 2 | Crash por división por cero en AnalyzerScreen.kt:209 | Alta |
| 3 | Race condition en AudioEngine.stopRecording() | Alta |
| 4 | Falta `synth_model.tflite` real (placeholder) | Media |
| 5 | Visibilidad en luz solar directa (tema oscuro invisible) | Media |
| 6 | Tests unitarios pendientes (AudioEngine, StemSeparator, StemAnalyzer) | Media |
| 7 | GC pressure en calculateHarmonicToNoiseRatio() | Baja |

---

# ═══════════════════════════════════════════════════════════════════════════════
# PARTE 2: SOFTWARE DESIGN DOCUMENT
# ═══════════════════════════════════════════════════════════════════════════════

## 2.1 Objetivo del Diseño

SynthLens está diseñado como una aplicación Android nativa que captura audio en tiempo real, procesa señales mediante algoritmos de DSP (Digital Signal Processing), y presenta resultados de detección de sintetizadores en una interfaz inspirada en hardware de sintetización musical.

## 2.2 Principios de Diseño

1. **Separación de Capas**: Engine → Data → ViewModel → UI (unidireccional)
2. **Reactividad Total**: StateFlow + collectAsState — el UI reacciona a datos, nunca los muta
3. **Persistencia Robusta**: Room con migraciones explícitas (sin destructive migration)
4. **Degradación Graciosa**: ML funciona solo si hay modelo .tflite; heurísticas siempre disponibles
5. **Aesthetic-First UI**: Todos los componentes replican hardware real (knobs, sliders, LEDs, osciloscopios)
6. **Brightness-Aware**: LocalIsBright adapta la UI a condiciones de iluminación

## 2.3 Arquitectura de Capas

```
┌──────────────────────────────────────────────────────────┐
│                     UI LAYER                             │
│  Composables → Screens → Components (Synth/Glass)        │
│  ← collectAsState() / remember / derivedStateOf          │
├──────────────────────────────────────────────────────────┤
│                   VIEWMODEL LAYER                        │
│  SynthViewModel : AndroidViewModel                       │
│  StateFlow<List<SynthLibraryItem>> / MutableStateFlow    │
├──────────────────────────────────────────────────────────┤
│                    DATA LAYER                            │
│  SynthRepository (singleton)                             │
│  ├── Room Database (synthlens_database v4)               │
│  │   ├── 6 DAOs → 6 Entity Types                        │
│  │   └── Mappers (Entity ↔ Domain)                      │
│  ├── SynthDatabaseSeeder (30+ synths on first launch)   │
│  └── AchievementRepository (20 achievements)             │
├──────────────────────────────────────────────────────────┤
│                    ENGINE LAYER                          │
│  AudioEngine (core orchestrator)                         │
│  ├── AudioRecord (44.1kHz mono 16-bit)                   │
│  ├── DSP Pipeline (FFT, YIN, Harmonics, THD, HNR)       │
│  ├── 33 Heuristic Scorers (per-synth)                    │
│  ├── SynthMLClassifier (TFLite + 13 brand heuristics)   │
│  ├── StemSeparator (4-band STFT + NMF)                   │
│  ├── StemAnalyzer (22 per-stem profiles)                 │
│  └── HandpanDetector (12 instrument profiles)            │
└──────────────────────────────────────────────────────────┘
```

## 2.4 Diseño del Motor de Audio

### 2.4.1 Pipeline de Procesamiento

El AudioEngine ejecuta un pipeline asíncrono en un CoroutineScope:

1. **Captura**: `AudioRecord.read()` → ShortArray (2048 samples)
2. **Pre-procesamiento**: DC offset removal → Noise gate (threshold adaptativo) → Gain normalization
3. **Análisis Espectral**: FFT Radix-2 Cooley-Tukey con ventana Hamming
4. **Detección de Frecuencia**: YIN algorithm con interpolación parabólica + fallback autocorrelación
5. **Clasificación de Forma de Onda**: Crest factor (sine<1.414, triangle<2.0, saw<2.5) + duty cycle
6. **Análisis de Armónicos**: 16 armónicos con búsqueda ±3 bins por pico
7. **Métricas Espectrales**: Flatness, Rolloff (85%), Bandwidth, Flux, HNR (dB)
8. **Detección Heurística**: 33 scorers paralelos con pesos:
   - waveform (0.20) + freq_range (0.25) + harmonics (0.25) + amplitude (0.15) + spectral_centroid (0.15)
9. **ML Classification**: 18 features → TFLite → argmax + confidence (compite con heurística)
10. **Stem Separation**: Cada 8 frames → 4-band STFT → NMF energy weighting
11. **Stem Analysis**: Per-stem scoring contra 22 profiles
12. **Handpan Detection**: 12 instrument profiles + timbre evaluation

### 2.4.2 Frecuencia de Actualización

- **Audio Analysis**: ~48ms por frame (2048 samples @ 44.1kHz ≈ 46ms)
- **Stem Separation**: ~384ms (cada 8 frames)
- **UI Rendering**: 60fps (Compose recomposition)

### 2.4.3 Patrón Competitivo ML vs Heurística

```kotlin
// ML gana solo cuando confianza > heurística
val mlResult = mlClassifier.classify(features)
val heuristicResult = matchSynthSignature(analysis)
if (mlResult.confidence > heuristicResult.confidence) {
    // Usar ML
} else {
    // Usar heurística (fallback siempre disponible)
}
```

## 2.5 Diseño de la Interfaz

### 2.5.1 Sistema de Componentes

**Synth Components** (hardware-themed):
- `SynthPanel`: Panel oscuro con glow sombra
- `SynthKnob`: Knob rotatorio Canvas con arco indicador + 24 puntos
- `SynthSlider`: Slider vertical con gradiente + tick marks
- `OscilloscopeDisplay`: Display de onda con grid, scan line, multi-pass glow
- `SpectrumAnalyzer`: 64 barras con gradiente HSL
- `LEDIndicator`: LED dot con label
- `PatchCable`: Indicador de ruta de señal
- `SynthButton`: Botón LED con glow

**Glass Components** (glassmorphism):
- `GlassPanel`: Panel translúcido (40-60% alpha) + gradiente diagonal + borde gradient
- `GlassCard`: Panel reactivo al amplitud
- `GlassFloatingButton`: Botón con estado activo
- `GlassChip`: Chip seleccionable

**Spectral Visualizers** (6 modos):
- `WaveformDisplay`: Onda con Bezier cúbica + fill gradient + glow multi-pass
- `SpectrumBars`: 64 barras con peak shimmer
- `SpectrogramWaterfall`: Heatmap tiempo-frecuencia (64 columnas)
- `TerrainWaterfall`: 3D perspective waterfall (28×48, color shift verde→rojo)
- `RadialOrbitalSphere`: 12 anillos orbitales con modulación spectrum
- `SpeakerVisualization`: Speaker animado con anillos concéntricos

### 2.5.2 Paleta de Color

```
Primary:    SynthCyan    #00E5FF  (neon cyan)
Secondary:  SynthMagenta #FF00FF  (neon magenta)
Tertiary:   SynthPurple  #7C4DFF  (neon purple)
Background: #000000       (pure black)
Surface:    #0A0A0F       (near-black)
Card:       #121218       (dark card)
Border:     #1A1A2E       (subtle border)
Accent:     SynthGreen    #76FF03  (neon green)
Warning:    SynthAmber    #FFD740  (amber)
Error:      SynthRed      #FF1744  (red)
```

### 2.5.3 Tipografía

- **displayLarge** (40sp, Light): Títulos principales
- **headlineLarge** (32sp, Light): Secciones
- **titleLarge** (22sp, Medium): Cards
- **bodyLarge** (16sp, Normal): Contenido
- **labelLarge** (14sp, Medium): Chips, badges
- **labelSmall** (10sp, Light): Metadata

## 2.6 Diseño de Persistencia

### 2.6.1 Patrón Entity-DAO-Database-Mapper

```
Domain Model ←→ Entity ←→ DAO ←→ Room Database
     ↕
  Mappers (extension functions bidireccionales)
```

### 2.6.2 Repository como Singleton

```kotlin
class SynthRepository(context: Context) {
    companion object {
        @Volatile private var INSTANCE: SynthRepository? = null
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: SynthRepository(context.applicationContext).also { INSTANCE = it }
        }
    }
    // Flow-based data access
    val allSynths: StateFlow<List<SynthLibraryItem>>
    val detectedSynths: StateFlow<List<DetectedSynth>>
    val allHistory: StateFlow<List<DetectionHistory>>
    // ...
}
```

## 2.7 Decisiones de Diseño Clave

| Decisión | Alternativa Considerada | Justificación |
|---|---|---|
| AndroidViewModel (no ViewModel) | Plain ViewModel | SynthRepository necesita Context para Room.databaseBuilder() |
| Flow en Repository | mutableStateListOf | Desacoplar datos de la UI de Compose |
| MutableStateFlow + collectAsState | LiveData | Mejor soporte Kotlin, operators, testeo |
| KSP (no kapt) | kapt | KSP es más rápido, soportado oficialmente por Room 2.8 |
| 33 heurísticos paralelos | Solo ML | Degradación graciosa cuando no hay modelo .tflite |
| StateFlow over Channel | LiveData | Cold flow, Kotlin-native, better operators |
| Canvas personalizado | Bibliotecas de gráficos | Control total sobre estética synth |
| selectedSynth overlay | NavHost arguments | Evita serialización de argumentos complejos |

---

# ═══════════════════════════════════════════════════════════════════════════════
# PARTE 3: ESPECIFICACIÓN FUNCIONAL
# ═══════════════════════════════════════════════════════════════════════════════

## 3.1 Alcance del Producto

SynthLens es una aplicación Android que permite a músicos, productores y entusiastas identificar sintetizadores de hardware y software a partir de su sonido, proporcionando información técnica detallada del instrumento detectado.

## 3.2 Usuarios Objetivo

1. **Músicos/ Productores**: Identificar sintetizadores en estudios o tiendas
2. **Coleccionistas**: Catalogar y comparar instrumentos
3. **Estudiantes de audio**: Aprender sobre sintetizadores
4. **Vendedores/Compradores**: Verificar autenticidad y especificaciones

## 3.3 Funcionalidades Principales

### 3.3.1 F1: Análisis de Audio en Tiempo Real

**Criterios de Aceptación**:
- [ ] Captura audio del micrófono a 44.1kHz mono 16-bit
- [ ] Muestra frecuencia fundamental en Hz y nota musical
- [ ] Muestra tipo de forma de onda (sine, triangle, saw, square, pulse)
- [ ] Muestra RMS level, peak level, THD
- [ ] Muestra datos FFT en spectrum analyzer de 64 barras
- [ ] Detección de armónicos (16 armónicos)
- [ ] Perfil espectral: flatness, rolloff, bandwidth, HNR
- [ ] Indicadores LED: PWR, REC, SIG
- [ ] Osciloscopio display con onda en tiempo real

### 3.3.2 F2: Detección de Sintetizadores

**Criterios de Aceptación**:
- [ ] Identifica 33+ modelos de sintetizadores
- [ ] Muestra marca, modelo, categoría (analógico/digital/FM/wavetable)
- [ ] Muestra nivel de confianza (%)
- [ ] Muestra frecuencia signature, waveform type, filter type
- [ ] Muestra configuración: osciladores, modulación, patrón de sonido
- [ ] Score heurístico ponderado: waveform(0.20), freq(0.25), harmonics(0.25), amplitude(0.15), spectral(0.15)
- [ ] ML classifier compite con heurística (ML gana solo cuando confidence > heurística)

### 3.3.3 F3: Separación de Stems

**Criterios de Aceptación**:
- [ ] Separa audio en 4 bandas: Sub-Bass (20-300Hz), Bass (300-1.2kHz), Mids (1.2-6kHz), Highs (6-20kHz)
- [ ] Análisis per-stem con 22 perfiles de sintetizador
- [ ] Muestra stem dominante con indicador visual
- [ ] NMF energy-weighted separation con confidence score
- [ ] Visualización neon de stems con energy bars

### 3.3.4 F4: Detección de Handpan

**Criterios de Aceptación**:
- [ ] Identifica 12 instrumentos: Hang, Halo, Rav Vast, Halo+, Saraz, Halo Mini, Caisa, Guda, Halo Pro, Yata, Panart, Generic
- [ ] Muestra nota detectada, octava, cents de desviación
- [ ] Análisis de armónicos con overtone ratio e inharmonicity
- [ ] Evaluación de sustain y ataque
- [ ] Perfil espectral: brightness index, warmth index

### 3.3.5 F5: Escáner con Cámara

**Criterios de Aceptación**:
- [ ] Preview de cámara con CameraX
- [ ] Overlay de viewfinder con esquinas animadas
- [ ] Scan line animada
- [ ] Indicador TARGET_ACQUIRED
- [ ] Muestra frecuencia, waveform analysis, osc count, filter type

### 3.3.6 F6: Visualización Espectral (3 modos)

**Modo TERRAIN_3D**:
- [ ] Waterfall 3D perspectiva (28 rows × 48 cols)
- [ ] Color shift verde→rojo según energía
- [ ] Perspectiva skewed

**Modo RADIAL_ORBITAL**:
- [ ] 12 anillos orbitales × 120 segmentos
- [ ] Modulación por spectrum data
- [ ] Animación de breathing + inner glow core
- [ ] Dots orbitantes

**Modo CLASSIC**:
- [ ] 64 barras de spectrum con peak shimmer
- [ ] Gradiente HSL por barra

### 3.3.7 F7: Biblioteca de Sintetizadores

**Criterios de Aceptación**:
- [ ] 30+ sintetizadores pre-cargados en primera ejecución
- [ ] Búsqueda por nombre, marca, categoría
- [ ] Filtros: ALL, ANALOG, DIGITAL, MODULAR
- [ ] Filtros por marca (chips)
- [ ] Cards estilo "System Patch Bay" con:
  - SynthVisualIcon por marca (6 patrones: knobs, keys, matrix, patch, digital, waveform)
  - Badge DETECTED si ha sido detectado
  - Tags OSC_TYPE y CIRCUIT
  - Canvas animado de forma de onda
  - Precio y "best for"
- [ ] Detail screen (SynthDetailScreen) con:
  - ID card con icono visual
  - Technical Manifest: oscillators, filter, polyphony, modulation, waveforms
  - Oscillator Bank con mini canvases por forma de onda
  - Filter Core con knob visualization
  - Mod Matrix (3 rutas de modulación)
  - History & Legacy
  - Notable Users
  - Technical Specs: signal chain, power, dimensions, weight, connectivity, presets
  - Precio + botón COMPRAR OFICIAL
  - Sitio oficial + demos

### 3.3.8 F8: Historial de Detecciones

**Criterios de Aceptación**:
- [ ] Estilo Shazam: timestamp, nombre del sintetizador, confianza
- [ ] Toggle ALL/FAVORITES
- [ ] Búsqueda de texto
- [ ] Chips de waveform, frequency, octave
- [ ] Acciones: favorito, eliminar
- [ ] Estadísticas: total scans, synthos, marcas

### 3.3.9 F9: Comparación A/B

**Criterios de Aceptación**:
- [ ] Selector dual de sintetizadores
- [ ] Dual waveform canvas animado
- [ ] Specs comparison: BRAND, CATEGORY, OSCILLATORS, FILTERS, WAVEFORMS, YEAR, POLYPHONY, SOUND, BEST_FOR
- [ ] Veredicto automático (analog vs digital, polyphony, sound character)

### 3.3.10 F10: Exportación Social

**Criterios de Aceptación**:
- [ ] Selección de detección
- [ ] Compartir a: WhatsApp, Instagram, Twitter, Facebook
- [ ] Copiar texto al portapapeles
- [ ] Preview del texto con hashtags
- [ ] Intent-based sharing con fallback chooser

### 3.3.11 F11: Modo Escenario

**Criterios de Aceptación**:
- [ ] Full-screen fondo negro
- [ ] Nombre de marca/grande grande
- [ ] Frecuencia, waveform, octava en valores grandes
- [ ] Visualización animada (grid, rings, core glow, stem orbs)
- [ ] Confianza, stem confidence, RMS/Peak/THD

### 3.3.12 F12: Integración DAW

**Criterios de Aceptación**:
- [ ] Live MIDI Output: NOTE, MIDI NOTE, VELOCITY
- [ ] CH, FREQ, WAVE chips
- [ ] Detected Synth Profile: SYNTH, FILTER, OSC, MOD, FX, PATTERN
- [ ] OSC Protocol docs para 5 DAWs (Ableton, FL Studio, Logic, Reaper, Bitwig)
- [ ] MIDI CC Map: CC 1/7/10/11/74

### 3.3.13 F13: Logros Gamificados

**Criterios de Aceptación**:
- [ ] 20 logros en 7 categorías
- [ ] Progreso automático (detect→unlock)
- [ ] Barras de progreso
- [ ] Badges de rareza (Common, Rare, Epic)
- [ ] Header con total unlocked/total

### 3.3.14 F14: Configuración

**Criterios de Aceptación**:
- [ ] Sample Rate selector (8000-48000 Hz)
- [ ] Buffer Size selector (512-8192)
- [ ] Stem Separation toggle
- [ ] Auto-Detect toggle
- [ ] Dark Mode toggle
- [ ] About info: version, engine, DB version, synths count, stem bands

---

# ═══════════════════════════════════════════════════════════════════════════════
# PARTE 4: METODOLOGÍA DE DESARROLLO
# ═══════════════════════════════════════════════════════════════════════════════

## 4.1 Metodología Adoptada

**Iterative-Incremental Development** con ciclos de 4 fases:

```
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│  FASE 1  │ → │  FASE 2  │ → │  FASE 3  │ → │  FASE 4  │
│  Motor   │   │  Data    │   │  UI      │   │  Polish  │
│  Engine  │   │  Layer   │   │  Screens │   │  + Test  │
└──────────┘   └──────────┘   └──────────┘   └──────────┘
```

### Fase 1: Motor de Audio
- AudioEngine con pipeline DSP completo
- Algoritmos: FFT, YIN, waveform classification, harmonics
- 33 scorers heurísticos
- ML classifier (TFLite)

### Fase 2: Capa de Datos
- Room database con 6 entidades
- DAOs + Mappers
- Repository pattern
- Database seeder con 30+ synths
- Migraciones explícitas

### Fase 3: Interfaces
- 14 pantallas Compose
- Componentes synth-themed
- Glassmorphism components
- 6 visualizadores espectrales
- Navegación con bottom nav

### Fase 4: Pulido + Testing
- Fix de bugs conocidos
- Tests unitarios
- Optimización de performance
- Integración ML real

## 4.2 Convenciones de Código

### Kotlin Style
- Funciones composables: PascalCase (`AnalyzerScreen`, `SynthPanel`)
- Data classes: PascalCase (`AudioAnalysis`, `StemProfile`)
- Extensiones: lowercase (`toSynthFeatures()`, `toDomain()`)
- Constantes: SCREAMING_SNAKE (`SAMPLE_RATE`, `FFT_SIZE`)

### Architecture Pattern
```
UI (Composable) ← ViewModel (StateFlow) ← Repository (Flow) ← DAO (Room Query)
                                      ↕
                              Engine (StateFlow<AudioAnalysis>)
```

### Naming Conventions
- Screens: `*Screen.kt` (e.g., `AnalyzerScreen.kt`)
- Components: `*Components.kt` or descriptive name (e.g., `SynthComponents.kt`)
- Entities: `*Entity.kt` (Room), `*Item.kt` or `*` (Domain)
- DAOs: `*Dao.kt`
- Engine: Descriptive names (e.g., `AudioEngine.kt`, `StemSeparator.kt`)

### File Organization
```
ui/
├── screens/          # Full-screen composables (one per navigation route)
├── components/       # Reusable UI components
├── navigation/       # Route definitions
└── theme/            # Color, Typography, Theme
engine/               # Pure Kotlin, no Android framework dependency (except Context for ML)
data/
├── local/            # Room entities, DAOs, database, mappers
├── SynthRepository   # Central data access
├── SynthEntities     # Domain models
├── SynthDatabaseSeeder # Seed data
└── AchievementRepository # Achievement logic
viewmodel/            # AndroidViewModel subclasses
```

## 4.3 Flujo de Trabajo del Desarrollador

1. **Feature Development**: Crear branch → implementar en capas (engine → data → viewmodel → ui) → compilar → test visual en emulador
2. **Bug Fix**: Reproducir → identificar causa raíz → fix mínimo → verificar fix
3. **Database Migration**: Crear Migration(oldVersion, newVersion) → bump version → test upgrade path
4. **UI Enhancement**: Modificar componente existente o crear nuevo en `ui/components/` → integrar en pantalla

## 4.4 Testing Strategy

| Tipo | Herramienta | Cobertura |
|---|---|---|
| Unit Tests | JUnit 5 | Engine algorithms, Repository, ViewModel |
| UI Tests | Compose Testing | Screen rendering, navigation, interactions |
| Integration Tests | Android Test | DB migrations, AudioRecord → Engine pipeline |
| Manual Testing | Emulator + Physical Device | Visual verification de UI synth-themed |

**Archivos de test pendientes**:
- `AudioEngineFeatureTest.kt` (25 tests, fix applied)
- `StemSeparatorTest.kt`
- `StemAnalyzerTest.kt`
- `SynthMLClassifierTest.kt`
- `RepositoryTest.kt`

## 4.5 Criterios de Calidad

- **Compilación**: APK debug debe compilar sin errores
- **Performance**: UI a 60fps, audio processing sin drops
- **Memoria**: Sin memory leaks en coroutines o AudioRecord
- **Degradación**: App funcional sin modelo .tflite (solo heurísticas)
- **Navegación**: Todas las 13 rutas accesibles desde bottom nav o ProfileScreen
- **Persistencia**: Migraciones sin pérdida de datos

---

# ═══════════════════════════════════════════════════════════════════════════════
# PARTE 5: DOCUMENTACIÓN DE ARQUITECTURA DE SOFTWARE
# ═══════════════════════════════════════════════════════════════════════════════

## 5.1 Visión General de Arquitectura

SynthLens sigue una arquitectura **Clean Architecture simplificada** con 4 capas principales:

```
┌─────────────────────────────────────────────────────────────────────┐
│                        UI LAYER (Presentation)                      │
│  ┌─────────────┐ ┌──────────────┐ ┌─────────────┐ ┌────────────┐  │
│  │   Screens   │ │  Components  │ │   Theme     │ │Navigation  │  │
│  │  (14 total) │ │ (Synth/Glass)│ │(Color/Type) │ │(13 routes) │  │
│  └──────┬──────┘ └──────┬───────┘ └──────┬──────┘ └─────┬──────┘  │
│         │               │                │              │          │
│         └───────────────┴────────┬───────┴──────────────┘          │
│                                  │                                 │
│                    collectAsState() ← StateFlow                    │
├──────────────────────────────────┼─────────────────────────────────┤
│                     VIEWMODEL LAYER                                │
│                    ┌─────────────┴──────────────┐                  │
│                    │      SynthViewModel        │                  │
│                    │  (AndroidViewModel)        │                  │
│                    │  MutableStateFlow<T>       │                  │
│                    └───────┬──────────┬────────┘                  │
│                            │          │                            │
│              ┌─────────────┘          └─────────────┐              │
│              ↓                                     ↓              │
├──────────────┼─────────────────────────────────────┼──────────────┤
│                     DATA LAYER                                    │
│  ┌────────────────┐    ┌─────────────────────────────────────┐    │
│  │SynthRepository │    │         AudioEngine                  │    │
│  │ (Singleton)    │    │    (StateFlow<AudioAnalysis>)        │    │
│  └───────┬────────┘    └─────────────────────────────────────┘    │
│          │                                                        │
│  ┌───────┴──────────────────────────────────────┐                 │
│  │              Room Database                    │                 │
│  │  ┌─────────┐ ┌──────────┐ ┌──────────────┐  │                 │
│  │  │ 6 DAOs  │ │ 6 Entities│ │  Migrations  │  │                 │
│  │  └─────────┘ └──────────┘ └──────────────┘  │                 │
│  └──────────────────────────────────────────────┘                 │
├───────────────────────────────────────────────────────────────────┤
│                        ENGINE LAYER                                │
│  ┌──────────────┐ ┌───────────────┐ ┌────────────────────────┐   │
│  │  AudioEngine │ │ StemSeparator │ │  SynthMLClassifier     │   │
│  │  (1187 lines)│ │ (454 lines)   │ │  (463 lines)           │   │
│  └──────┬───────┘ └───────┬───────┘ └────────────────────────┘   │
│         │                 │                                       │
│  ┌──────┴───────┐ ┌───────┴───────┐ ┌────────────────────────┐   │
│  │ StemAnalyzer │ │HandpanDetector│ │  Signal Processing     │   │
│  │ (341 lines)  │ │ (410 lines)   │ │  FFT, YIN, Harmonics   │   │
│  └──────────────┘ └───────────────┘ └────────────────────────┘   │
└───────────────────────────────────────────────────────────────────┘
```

## 5.2 Diagrama de Componentes

```
                    ┌─────────────────────┐
                    │     MainActivity     │
                    │  (Permission, Init)  │
                    └──────────┬──────────┘
                               │
                    ┌──────────┴──────────┐
                    │    SynthLensApp      │
                    │  (NavHost, Scaffold) │
                    └──────────┬──────────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
    ┌─────┴─────┐    ┌────────┴────────┐    ┌─────┴─────┐
    │   Audio   │    │    CameraX      │    │   Room    │
    │  Engine   │    │    Camera       │    │ Database  │
    │ (mic)     │    │   Scanner       │    │  (6 tbl)  │
    └───────────┘    └─────────────────┘    └───────────┘
```

## 5.3 Diagrama de Secuencia — Detección de Sintetizador

```
User        AudioEngine      DSP Pipeline     Heuristic      ML Classifier   UI
 │              │                │              │                │            │
 │  START       │                │              │                │            │
 │─────────────>│                │              │                │            │
 │              │ AudioRecord.read()            │                │            │
 │              │──────────────>│               │                │            │
 │              │               │ DC+Gate+Gain  │                │            │
 │              │               │──────────────>│               │            │
 │              │               │  FFT (2048)   │                │            │
 │              │               │──────────────>│               │            │
 │              │               │  YIN Freq     │                │            │
 │              │               │──────────────>│               │            │
 │              │               │  Waveform     │                │            │
 │              │               │──────────────>│               │            │
 │              │               │  Harmonics    │                │            │
 │              │               │──────────────>│               │            │
 │              │               │  33 Scorers   │                │            │
 │              │               │──────────────>│               │            │
 │              │               │               │  ML classify   │            │
 │              │               │               │──────────────>│            │
 │              │               │               │  Compare scores│            │
 │              │<──────────────│───────────────│               │            │
 │              │ AudioAnalysis (StateFlow)     │               │            │
 │  recompose   │                │              │                │            │
 │<─────────────│                │              │                │            │
 │  show result │                │              │                │            │
```

## 5.4 Diagrama de Datos

```
┌─────────────────────────────────────────────────────────────┐
│                    AudioAnalysis                             │
│  frequency: Float        amplitude: Float                    │
│  waveformType: String    octaves: Int                        │
│  rmsLevel: Float         peakLevel: Float                    │
│  thd: Float              spectrumData: FloatArray (1024)     │
│  waveformPoints: List<Float>   harmonics: List<Float>        │
│  isDetecting: Boolean    noteName: String                    │
│  harmonicCount: Int      spectralFlatness: Float             │
│  spectralRolloff: Float  spectralBandwidth: Float            │
│  harmonicToNoiseRatio: Float                                 │
│  detectedSynth: DetectedSynthResult?                         │
│  detectedHandpan: DetectedHandpanResult?                     │
│  stemAnalysis: StemAnalysis?                                 │
│  stemProfiles: List<StemSynthProfile>                        │
│  dominantStemName: String?                                   │
└───────────────────┬─────────────────────────────────────────┘
                    │
    ┌───────────────┼───────────────┐
    │               │               │
    ▼               ▼               ▼
┌────────┐   ┌───────────┐   ┌───────────┐
│Detected│   │  Handpan  │   │   Stem    │
│ Synth  │   │  Result   │   │ Analysis  │
│ Result │   │           │   │           │
└────────┘   └───────────┘   └───────────┘
    │                           │
    ▼                           ▼
┌────────────────┐    ┌────────────────┐
│ SynthLibrary   │    │   StemSynth    │
│    Entity      │    │    Profile     │
│ (36 fields)    │    │ (22 profiles)  │
└────────────────┘    └────────────────┘
```

## 5.5 Patrones de Arquitectura

| Patrón | Implementación | Ubicación |
|---|---|---|
| **Repository** | SynthRepository (singleton) | `data/SynthRepository.kt` |
| **Singleton** | SynthRepository, SynthDatabase | `data/local/SynthDatabase.kt` |
| **Observer** | StateFlow + collectAsState | Engine → ViewModel → UI |
| **Strategy** | 33 scorers heurísticos intercambiables | `engine/AudioEngine.kt` |
| **Factory** | Room.databaseBuilder pattern | `data/local/SynthDatabase.kt` |
| **Facade** | AudioEngine orquesta todo el pipeline | `engine/AudioEngine.kt` |
| **Mapper** | Extension functions Entity↔Domain | `data/local/Mappers.kt` |
| **Composition** | SynthViewModel compone Repository | `viewmodel/SynthViewModel.kt` |

## 5.6 Decisiones Arquitectónicas

### AD-01: AndroidViewModel sobre ViewModel
- **Contexto**: SynthRepository necesita `Context` para `Room.databaseBuilder()`
- **Decisión**: SynthViewModel extiende `AndroidViewModel(application)`
- **Trade-off**: Acoplamiento a Android framework vs simplicidad de DI manual

### AD-02: StateFlow sobre LiveData
- **Contexto**: Kotlin-first, mejor soporte de operators, testeo
- **Decisión**: `MutableStateFlow` + `collectAsState()` en Compose
- **Trade-off**: Sin lifecycle awareness automática vs mejor integración Kotlin

### AD-03: Canvas personalizado sobre bibliotecas de gráficos
- **Contexto**: UI debe verse como hardware real de sintetizador
- **Decisión**: Todo dibujado con Canvas/Path/DrawScope
- **Trade-off**: Más código vs control total sobre estética

### AD-04: Heurísticas + ML (competitivo)
- **Contexto**: No hay modelo ML entrenado disponible inicialmente
- **Decisión**: 33 scorers heurísticos + ML como competidor que gana solo cuando es mejor
- **Trade-off**: Código más complejo vs degradación graciosa

### AD-05: Room sin destructive migration
- **Contexto**: Producción no puede perder datos del usuario
- **Decisión**: Migraciones explícitas MIGRATION_1_2, 2_3, 3_4
- **Trade-off**: Más trabajo inicial vs integridad de datos

### AD-06: selectedSynth overlay pattern
- **Contexto**: NavHost argument serialization es engorroso para objetos complejos
- **Decisión**: `selectedSynth: SynthLibraryItem?` como state, detail screen como overlay
- **Trade-off**: Navegación no es "deep linkable" vs simplicidad

---

# ═══════════════════════════════════════════════════════════════════════════════
# PARTE 6: MEMORIA TÉCNICA
# ═══════════════════════════════════════════════════════════════════════════════

## 6.1 Configuración del Entorno

| Parámetro | Valor |
|---|---|
| Working Directory | `/home/DexTer/AndroidStudioProjects/MyApplication` |
| Android SDK | `/home/DexTer/Android/Sdk` |
| Android Studio JBR | `/home/DexTer/android-studio/jbr` |
| Build Command | `JAVA_HOME=/home/DexTer/android-studio/jbr ./gradlew assembleDebug` |
| Emulador | SynthLens_Test (Pixel 7, Android 34, x86_64) |
| Git | No (no es repo git) |

## 6.2 Constantes del Motor

```kotlin
// AudioEngine
SAMPLE_RATE = 44100
CHANNEL_CONFIG = CHANNEL_IN_MONO
AUDIO_FORMAT = ENCODING_PCM_16BIT
FFT_SIZE = 2048

// StemSeparator
STFTWindowSize = 2048
STFTHopSize = 512
SUB_BASS_RANGE = 20f..300f
BASS_RANGE = 300f..1200f
MID_RANGE = 1200f..6000f
HIGH_RANGE = 6000f..20000f
STEM_NAMES = ["Sub-Bass", "Bass", "Mids", "Highs"]

// Detección
SYNTH_THRESHOLD = 0.55  // confidence mínima para match
HANDPAN_THRESHOLD = 0.45
STEM_SEPARATION_INTERVAL = 8  // cada N frames

// Heuristic Weights
WEIGHT_WAVEFORM = 0.20
WEIGHT_FREQ = 0.25
WEIGHT_HARMONICS = 0.25
WEIGHT_AMPLITUDE = 0.15
WEIGHT_SPECTRAL = 0.15
```

## 6.3 Funciones DSP Clave

### YIN Frequency Detection
```kotlin
// Autocorrelation-based pitch detection
// Fallback chain: YIN → parabolic interpolation → autocorrelation → zero-crossing
// Precision: ±1 Hz en rango 20Hz-20kHz
```

### FFT (Radix-2 Cooley-Tukey)
```kotlin
// 2048-point FFT con ventana Hamming
// Output: magnitude spectrum (1024 bins)
// Usado para: spectrum display, spectral analysis, stem separation
```

### Waveform Classification
```kotlin
// Crest factor + duty cycle
// Sine: crest < 1.414
// Triangle: crest < 2.0
// Saw: crest < 2.5
// Square: duty cycle ~50%, crest > 2.5
// Pulse: duty cycle != 50%, crest > 2.5
```

### Harmonics Detection
```kotlin
// 16 armónicos buscados en ±3 bins del pico esperado
// Used for: THD calculation, harmonic profile, synth matching
```

### NMF-style Separation
```kotlin
// Energy-weighted separation confidence
// Entropy-based scoring
// Per-stem energy calculation
```

## 6.4 Bugs Conocidos y Soluciones

### Bug 1: Division by Zero
- **Archivo**: `AnalyzerScreen.kt:209`
- **Problema**: `(analysis.rmsLevel / 0f)` → Infinity/NaN → Compose crash
- **Solución**: Cambiar `/0f` a un divisor válido o usar default seguro
- **rmsLevel es dB (puede ser negativo)**

### Bug 2: Race Condition
- **Archivo**: `AudioEngine.kt` — `stopRecording()`
- **Problema**: `audioRecord?.read()` sin try-catch, AudioRecord released mid-read → IllegalStateException
- **Solución**: Wrap read() in try-catch, check isActive before read

### Bug 3: GC Pressure
- **Archivo**: `AudioEngine.kt` — `calculateHarmonicToNoiseRatio()`
- **Problema**: Allocates `(1..8).map{}.toList()` every 3rd frame in hot loop
- **Solución**: Reutilizar buffer, alloc once outside loop

## 6.5 Patrones de UI Reutilizables

### Glassmorphism
```kotlin
GlassPanel(
    alpha = 0.4f,           // 40-60% translucidez
    cornerRadius = 16.dp,
    borderAlpha = 0.12f,     // borde gradiente diagonal
    glowColor = SynthCyan,
    glowIntensity = 0.3f
) { /* content */ }
```

### Synth Knob
```kotlin
SynthKnob(
    value = 0.5f,
    onValueChange = { /* callback */ },
    label = "CUTOFF",
    color = SynthCyan,
    knobSize = 80.dp,
    min = 0f, max = 1f
)
```

### Oscilloscope
```kotlin
OscilloscopeDisplay(
    waveformData = analysis.waveformPoints,
    color = SynthCyan,
    secondaryColor = SynthMagenta
)
```

## 6.6 Brand Visual Mapping

| Marca | Color Primario | Patrón |
|---|---|---|
| Moog | SynthCyan | KNOBS |
| Korg | SynthMagenta | KEYS |
| Roland | SynthPurple | KEYS |
| Sequential | SynthGreen | KNOBS |
| Novation | SynthAmber | DIGITAL |
| Arturia | SynthCyan | MATRIX |
| Behringer | SynthMagenta | KNOBS |
| Yamaha | SynthPurple | DIGITAL |
| Elektron | SynthGreen | DIGITAL |
| Waldorf | SynthAmber | WAVEFORM |
| Teenage | SynthRed | PATCH |

## 6.7 Logros Gamificados (20 total)

| Categoría | Logros | Requisitos |
|---|---|---|
| Detection | First Contact, Synth Spotter, Synth Hunter, Synth Legend | 1, 10, 50, 100 detecciones |
| Brands | Moog Master, Roland Royal, Korg King, Sequential Sensei | 3 de cada marca |
| Waveforms | Sawtooth Specialist, Square Wizard, Waveform Connoisseur | Detectar 3 tipos |
| Sessions | Dedicated Listener, Audio Addict | 5, 20 sesiones |
| Quality | Crystal Clear, Perfect Pitch | Confidence >80%, >95% |
| Special | Night Owl, Early Bird | After 10PM, Before 6AM |
| Exploration | Library Legend | Ver todos los synths |
| Collection | Top Ten | 10 detections guardadas |

## 6.8 Instrucciones de Instalación

```bash
# 1. Verificar SDK
ls /home/DexTer/Android/Sdk/platforms/

# 2. Compilar
cd /home/DexTer/AndroidStudioProjects/MyApplication
JAVA_HOME=/home/DexTer/android-studio/jbr ./gradlew assembleDebug

# 3. Instalar en emulador
adb install app/build/outputs/apk/debug/app-debug.apk

# 4. Lanzar
adb shell am start -n com.example.myapplication/.MainActivity
```

## 6.9 Comandos Útiles

```bash
# Clean build
JAVA_HOME=/home/DexTer/android-studio/jbr ./gradlew clean assembleDebug

# Instalar en dispositivo físico
adb -s <device_serial> install app/build/outputs/apk/debug/app-debug.apk

# Ver logs
adb logcat -s "SynthLens" | grep -i "audio\|detect\|error"

# Emulador
emulator -avd SynthLens_Test -no-window -no-audio
```

## 6.10 Referencia Rápida de Archivos

| Archivo | Líneas | Propósito |
|---|---|---|
| `AudioEngine.kt` | 1187 | Core: grabación + detección |
| `StemSeparator.kt` | 454 | Separación 4-band |
| `StemAnalyzer.kt` | 341 | Detección per-stem |
| `SynthMLClassifier.kt` | 463 | ML TFLite + heurísticas |
| `HandpanDetector.kt` | 410 | Detección de handpans |
| `SpectralVisualizer.kt` | 719 | 6 visualizadores |
| `SynthComponents.kt` | 557 | Componentes synth-themed |
| `StemVisualizer.kt` | 404 | Visualización neon stems |
| `SynthDetailScreen.kt` | 668 | Pantalla detalle completa |
| `SynthDatabaseSeeder.kt` | 701+ | 30+ synths seed data |
| `CameraScannerScreen.kt` | 571 | Escáner con cámara |
| `SpectralScreen.kt` | 514 | 3 modos de visualización |
| `AnalysisDetailsScreen.kt` | 512 | Detalle técnico |
| `AnalyzerScreen.kt` | 550 | Análisis principal |
| `HandpanInfoPanel.kt` | 458 | Panel detallado handpan |
| `LibraryScreen.kt` | 446 | Biblioteca |
| `GlassComponents.kt` | 175 | Glassmorphism |
| `ReactiveEffects.kt` | 83 | Efectos reactivos |
| `SynthViewModel.kt` | 227 | ViewModel |
| `SynthRepository.kt` | 202 | Repositorio |
| `Mappers.kt` | 205 | Entity↔Domain |
| `Entities.kt` | 131 | 6 entidades Room |
| `Daos.kt` | 157 | 6 DAOs |
| `SynthDatabase.kt` | 145 | DB singleton + migrations |
| `AchievementRepository.kt` | 235 | 20 logros |
| `MainActivity.kt` | 412 | Entry + NavHost + Splash |

---

# ═══════════════════════════════════════════════════════════════════════════════
# APÉNDICE A: FLUJO DE NAVEGACIÓN COMPLETO
# ═══════════════════════════════════════════════════════════════════════════════

```
                    ┌─────────────┐
                    │   SPLASH    │
                    │ (2.2s anim) │
                    └──────┬──────┘
                           │
                    ┌──────┴──────┐
                    │   AUDIO     │ ← DEFAULT
                    │  (tab 1)   │
                    └──┬───┬───┬──┘
                       │   │   │
        ┌──────────────┘   │   └──────────────┐
        │                  │                  │
   ┌────┴────┐      ┌─────┴─────┐     ┌──────┴──────┐
   │  SCAN   │      │ SPECTRUM  │     │   LIBRARY   │
   │ (tab 2) │      │ (tab 3)   │     │  (tab 4)   │
   └─────────┘      └───────────┘     └──────┬──────┘
                                             │
                                      ┌──────┴──────┐
                                      │   SPECS     │
                                      │  (tab 5)   │
                                      └──────┬──────┘
                                             │
                    ┌────────────────────────┼────────────────────┐
                    │                        │                    │
              ┌─────┴─────┐          ┌───────┴──────┐     ┌──────┴──────┐
              │  HISTORY  │          │ A/B COMPARE  │     │  ACHIEVE-   │
              │           │          │              │     │  MENTS      │
              └───────────┘          └──────────────┘     └─────────────┘
                    │                        │
              ┌─────┴─────┐          ┌───────┴──────┐
              │  EXPORT   │          │  STAGE MODE  │
              └───────────┘          └──────────────┘
                    │
              ┌─────┴─────┐          ┌──────────────┐
              │    DAW    │          │   SETTINGS   │
              │INTEGRATION│          │              │
              └───────────┘          └──────────────┘

OVERLAY (cualquier screen):
   SynthDetailScreen (cuando selectedSynth != null)
```

---

# APÉNDICE B: INVENTARIO COMPLETO DE COMPONENTES

## B.1 Synth Components (SynthComponents.kt)
1. `SynthPanel` — Panel oscuro con glow
2. `SynthKnob` — Knob rotatorio Canvas
3. `SynthSlider` — Slider vertical
4. `OscilloscopeDisplay` — Display de onda
5. `SpectrumAnalyzer` — 64 barras spectrum
6. `LEDIndicator` — LED dot + label
7. `PatchCable` — Ruta de señal
8. `SynthButton` — Botón LED
9. `DynamicBackground` — Gradiente radial reactivo

## B.2 Glass Components (GlassComponents.kt)
1. `GlassPanel` — Panel glassmorphism
2. `GlassCard` — Card reactiva
3. `GlassFloatingButton` — Botón flotante
4. `GlassChip` — Chip seleccionable
5. `Modifier.glassBackground` — Extension

## B.3 Spectral Visualizers (SpectralVisualizer.kt)
1. `WaveformDisplay` — Onda Bezier + glow
2. `SpectrumBars` — 64 barras + peak shimmer
3. `SpectrogramWaterfall` — Heatmap tiempo-freq
4. `SpeakerVisualization` — Speaker animado
5. `TerrainWaterfall` — 3D waterfall (28×48)
6. `RadialOrbitalSphere` — 12 anillos orbitales

## B.4 Stem Visualizer (StemVisualizer.kt)
1. `StemSeparationVisualizer` — Full neon visualization
2. `StemProgressIndicator` — Dot + progress bar

## B.5 Reactive Effects (ReactiveEffects.kt)
1. `rememberReactiveAmplitude` — EMA smoothing
2. `rememberReactiveGlow` — Amplitude→glow
3. `rememberReactiveBorderAlpha` — Amplitude→alpha
4. `rememberBreathingAlpha` — Breathing + reactive
5. `Modifier.reactiveScale` — GraphicsLayer scale
6. `Modifier.reactiveGlow` — GraphicsLayer shadow

---

> **Fin del Paquete de Documentación**  
> Generado por MiMo Code Agent — 23 de Junio de 2026  
> Proyecto: SynthLens Android App
