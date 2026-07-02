╔══════════════════════════════════════════════════════════════════╗
║                    SYNTHLENS - DISEÑO UI/UX                    ║
║                   Wireframes de las 5 Pantallas                ║
╚══════════════════════════════════════════════════════════════════╝


═══════════════════════════════════════════════════════════════════
  PANTALLA 1: ANALIZADOR PRINCIPAL (Al abrir la app)
═══════════════════════════════════════════════════════════════════

  ┌─────────────────────────────────────┐
  │  ≡  SYNTHLENS              🔊  ⚙️   │  ← Top bar
  ├─────────────────────────────────────┤
  │                                     │
  │        ╭───────────────────╮        │
  │       │  ╭─────────────╮   │        │
  │       │  │             │   │        │  ← Bocina central animada
  │       │  │    ◉    ◉   │   │        │    con ondas de sonido
  │       │  │      ◡      │   │        │    expandiéndose
  │       │  │             │   │        │
  │       │  ╰─────────────╯   │        │
  │        ╰───────────────────╯        │
  │                                     │
  │     ○ ○ ○ ○ ○ ○ ○ ○ ○ ○ ○ ○       │  ← Indicador de ondas
  │                                     │    (pulso de audio en vivo)
  │  ┌─────────────────────────────┐    │
  │  │  ▶ ESCUCHANDO AUDIO...     │    │  ← Estado del análisis
  │  │  ━━━━━━━━━━━━━━━━━━━━━━━━  │    │  ← Barra de progreso
  │  └─────────────────────────────┘    │
  │                                     │
  │  ┌─────────────┐ ┌─────────────┐   │
  │  │ 🎤 MIC      │ │ 🔊 INPUT   │   │  ← Fuentes de audio
  │  │  EN VIVO    │ │  SISTEMA   │   │
  │  └─────────────┘ └─────────────┘   │
  │                                     │
  │  ┌─────────────────────────────┐    │
  │  │   DETECTANDO PATRONES...   │    │  ← Motor de detección
  │  │   Hz: 440 | BPM: 120      │    │    muestra frecuencia
  │  │   Forma: Saw | Oct: 4     │    │    y forma de onda
  │  └─────────────────────────────┘    │
  │                                     │
  │  ┌─────────────────────────────┐    │
  │  │  ★ SINTETIZADOR DETECTADO  │    │  ← ALERTA de detección
  │  │  ┌─────────────────────┐   │    │    (aparece con animación)
  │  │  │  🎹 MOOG GRANDMOTHER│   │    │
  │  │  │  Confianza: 94.2%   │   │    │
  │  │  │  Saw Wave @ 220Hz   │   │    │
  │  │  └─────────────────────┘   │    │
  │  │        [ VER DETALLES → ]  │    │  ← Botón para Pantalla 2
  │  └─────────────────────────────┘    │
  │                                     │
  ├─────────────────────────────────────┤
  │  🎹      📊      🔊      📚   👤   │  ← Bottom nav
  │  HOME   ANÁLISIS  ESPECTRO BIBL PERF│
  └─────────────────────────────────────┘


═══════════════════════════════════════════════════════════════════
  PANTALLA 2: DETALLES DEL ANÁLISIS
═══════════════════════════════════════════════════════════════════

  ┌─────────────────────────────────────┐
  │  ←  ANÁLISIS DETALLADO     📤 💾   │
  ├─────────────────────────────────────┤
  │                                     │
  │  ╭─────────────────────────────╮    │
  │  │  🎹 MOOG GRANDMOTHER       │    │  ← Header del sinte detectado
  │  │  Semi-Modular Analog 2018  │    │
  │  │  ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░ 94%  │    │  ← Confianza de detección
  │  ╰─────────────────────────────╯    │
  │                                     │
  │  ┌─ OSCILADORES ─────────────────┐  │
  │  │  VCO 1: ██████ Saw @ 220Hz   │  │  ← Análisis de osciladores
  │  │  VCO 2: ████░░ Square @ 110Hz│  │
  │  │  Sub:   ██░░░░ Triangle 55Hz │  │
  │  │  Noise: ░░░░░░ Off           │  │
  │  └───────────────────────────────┘  │
  │                                     │
  │  ┌─ FILTROS ─────────────────────┐  │
  │  │  Tipo: Moog Ladder LP 24dB   │  │  ← Análisis de filtros
  │  │  Cutoff: ████████░░ 1.2kHz   │  │
  │  │  Resonance: █████░░░░ 45%    │  │
  │  │  Envelope: ADSR [||||]       │  │
  │  │  A:10ms D:200ms S:70% R:300ms│  │
  │  └───────────────────────────────┘  │
  │                                     │
  │  ┌─ MODULACIÓN ──────────────────┐  │
  │  │  LFO 1: ████░░░ 4Hz → Pitch  │  │  ← Modulaciones detectadas
  │  │  LFO 2: ██░░░░░ 0.5Hz → Cutoff│ │
  │  │  VCF EG: Attack 15ms         │  │
  │  │  Glide: ON (portamento 30ms) │  │
  │  └───────────────────────────────┘  │
  │                                     │
  │  ┌─ SECUENCIA / PATRÓN ─────────┐  │
  │  │  Notas: C2-E2-G2-C3-G2-E2   │  │  ← Patrón melódico
  │  │  Duración media: 250ms       │  │
  │  │  Velocity: 80-127            │  │
  │  │  Swing: 52%                  │  │
  │  └───────────────────────────────┘  │
  │                                     │
  │  ┌─ DAW / PRODUCCIÓN ───────────┐   │
  │  │  DAW Detectado: Ableton 92%  │   │  ← DAW detectado
  │  │  Plugins: Serum, FabFilter   │   │
  │  │  Estilo: Deep House / Melodic│   │
  │  └───────────────────────────────┘  │
  │                                     │
  │  ┌─ EFECTOS ────────────────────┐   │
  │  │  Chorus: ON (Roland CE-1)    │   │  ← Efectos detectados
  │  │  Reverb: Hall 3.2s           │   │
  │  │  Delay: 1/8 ping-pong        │   │
  │  │  Compression: -6dB threshold │   │
  │  └───────────────────────────────┘  │
  │                                     │
  │  ╭─────────────────────────────╮    │
  │  │  💾 GUARDAR EN LIBRERÍA     │    │  ← Botón guardar
  │  ╰─────────────────────────────╯    │
  │                                     │
  ├─────────────────────────────────────┤
  │  🎹      📊      🔊      📚   👤   │
  └─────────────────────────────────────┘


═══════════════════════════════════════════════════════════════════
  PANTALLA 3: ANALIZADOR ESPECTRAL
═══════════════════════════════════════════════════════════════════

  ┌─────────────────────────────────────┐
  │  ←  ANALIZADOR ESPECTRAL    📷 📏  │
  ├─────────────────────────────────────┤
  │                                     │
  │         ╭───────────────────╮       │
  │        │  ╭─────────────╮   │       │
  │        │  │             │   │       │  ← Bocina de fondo
  │        │  │  ◉       ◉  │   │       │    (decorativa)
  │        │  │     ◡       │   │       │
  │        │  │             │   │       │
  │        │  ╰─────────────╯   │       │
  │         ╰───────────────────╯       │
  │                                     │
  │  ┌─ FORMA DE ONDA (TIMPO REAL) ───┐│
  │  │                                 ││
  │  │  ╱╲    ╱╲    ╱╲    ╱╲    ╱╲   ││  ← Onda sinusoidal/saw
  │  │ ╱  ╲  ╱  ╲  ╱  ╲  ╱  ╲  ╱  ╲ ││    en color cian brillante
  │  │╱    ╲╱    ╲╱    ╲╱    ╲╱    ╲││    sobre fondo oscuro
  │  │▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓││
  │  │                                 ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ┌─ ESPECTRO DE FRECUENCIA ────────┐│
  │  │                                 ││
  │  │  ▐█▌                            ││  ← Barras de espectro
  │  │  ▐█▌ ▐█▌                       ││    de baja frecuencia
  │  │  ▐█▌ ▐█▌ ▐█▌                  ││    (graves a agudos)
  │  │  ▐█▌ ▐█▌ ▐█▌ ▐█▌             ││
  │  │  ▐█▌ ▐█▌ ▐█▌ ▐█▌ ▐█▌ ▐█▌    ││
  │  │  ▐█▌ ▐█▌ ▐█▌ ▐█▌ ▐█▌ ▐█▌ ▐█▌ ││
  │  │  ▐█▌ ▐█▌ ▐█▌ ▐█▌ ▐█▌ ▐█▌ ▐█▌ ││
  │  │  20Hz   200Hz  1kHz  5kHz 20kHz││
  │  │                                 ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ┌─ WATERFALL / SPECTROGRAMA ─────┐│
  │  │  ░░░▓▓▓███▓▓▓░░░░░░░░░░░░░░░░││  ← Espectrograma en cascada
  │  │  ░░▓▓█████▓▓▓░░░░░░░░░░░░░░░░││    muestra historial de freq
  │  │  ░▓▓████████▓▓░░░░░░░░░░░░░░░││    con colores de intensidad
  │  │  ▓▓▓████████████▓▓▓░░░░░░░░░░││
  │  │  ▓█████████████████▓▓▓░░░░░░░││
  │  │  █████████████████████▓▓▓░░░░││
  │  │  20Hz                    20kHz││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ┌─ MEDIDAS EN VIVO ───────────────┐│
  │  │  Freq Fundamental: 220.0 Hz (A3)││
  │  │  RMS Level: -12.4 dB           ││  ← Métricas en tiempo real
  │  │  Peak Level: -6.2 dB           ││
  │  │  THD: 0.08%                    ││
  │  └─────────────────────────────────┘│
  │                                     │
  ├─────────────────────────────────────┤
  │  🎹      📊      🔊      📚   👤   │
  └─────────────────────────────────────┘


═══════════════════════════════════════════════════════════════════
  PANTALLA 4: BIBLIOTECA / LIBRERÍA
═══════════════════════════════════════════════════════════════════

  ┌─────────────────────────────────────┐
  │  ≡  BIBLIOTECA SYNTH      🔍 📊   │
  ├─────────────────────────────────────┤
  │                                     │
  │  ┌─────────────────────────────┐    │
  │  │  🔍 Buscar sintetizador... │    │  ← Barra de búsqueda
  │  └─────────────────────────────┘    │
  │                                     │
  │  ┌──────┐ ┌──────┐ ┌──────┐        │
  │  │ 🏆   │ │ 📋   │ │ 🏷️   │        │  ← Filtros:
  │  │TODOS │ │DETECT│ │MARCA │        │    Todos, Detectados, Marcas
  │  └──────┘ └──────┘ └──────┘        │
  │                                     │
  │  ┌─ MARCAS ────────────────────────┐│
  │  │ ○ Moog (6)  ○ Korg (9)         ││  ← Marcas con conteo
  │  │ ○ Roland (12) ○ Arturia (5)    ││
  │  │ ○ Sequential (6) ○ Novation (5)││
  │  │ ○ Behringer (5) ○ Waldorf (3)  ││
  │  │ ○ Elektron (3) ○ ASM (2)       ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ╭─────────────────────────────╮    │
  │  │ 🏆 LOGRO DESBLOQUEADO!     │    │  ← Notificación de trofeo
  │  │ "Primer Synth Detectado"    │    │    (aparece con confetti)
  │  │ 🥇 ★☆☆☆☆☆☆☆☆☆ 1/50      │    │
  │  ╰─────────────────────────────╯    │
  │                                     │
  │  ┌─ DETECTADOS RECIENTEMENTE ─────┐│
  │  │  ┌─────────────────────────┐   ││
  │  │  │ 🏆 MOOG GRANDMOTHER    │   ││  ← Synth detectado con trofeo
  │  │  │ Semi-Modular | 2018    │   ││
  │  │  │ Detectado: hace 2 min  │   ││
  │  │  │ ★ SYNTHLENS VERIFIED   │   ││  ← Badge de verificación
  │  │  └─────────────────────────┘   ││
  │  │  ┌─────────────────────────┐   ││
  │  │  │ 🏆 KORG MS-20          │   ││
  │  │  │ Semi-Modular | 1978    │   ││
  │  │  │ Detectado: hace 15 min │   ││
  │  │  │ ★ SYNTHLENS VERIFIED   │   ││
  │  │  └─────────────────────────┘   ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ┌─ TODOS LOS DISPOSITIVOS ────────┐│
  │  │  ┌─────────────────────────┐   ││
  │  │  │ 🎹 Moog                │   ││  ← Lista de todos los synths
  │  │  │ ┌─ Minimoog Model D ──┐│   ││
  │  │  │ │ Monophonic | 1970   ││   ││    Los detectados tienen
  │  │  │ │ ⭕ No detectado      ││   ││    badge dorado
  │  │  │ └──────────────────────┘│   ││
  │  │  │ ┌─ Grandmother ───────┐│   ││
  │  │  │ │ Semi-Modular | 2018 ││   ││
  │  │  │ │ 🏆 DETECTADO x3     ││   ││
  │  │  │ └──────────────────────┘│   ││
  │  │  │ ┌─ Subsequent 37 ─────┐│   ││
  │  │  │ │ Paraphonic | 2015   ││   ││
  │  │  │ │ ⭕ No detectado      ││   ││
  │  │  │ └──────────────────────┘│   ││
  │  │  └─────────────────────────┘   ││
  │  │                                 ││
  │  │  ┌─ Roland ──────────────────┐  ││
  │  │  │ ┌─ JUNO-106 ──────────┐  │  ││
  │  │  │ │ Polyphonic | 1984   │  │  ││
  │  │  │ │ ⭕ No detectado      │  │  ││
  │  │  │ └──────────────────────┘  │  ││
  │  │  └───────────────────────────┘  ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ┌─ PROGRESO ──────────────────────┐│
  │  │  2/50 Sints Detectados          ││
  │  │  ▓▓▓▓▓░░░░░░░░░░░░░░░ 4%       ││  ← Barra de progreso
  │  │  12/50 Brands Cubiertas         ││
  │  │  ▓▓▓▓▓▓▓▓▓▓░░░░░░░░░ 24%      ││
  │  └─────────────────────────────────┘│
  │                                     │
  ├─────────────────────────────────────┤
  │  🎹      📊      🔊      📚   👤   │
  └─────────────────────────────────────┘


═══════════════════════════════════════════════════════════════════
  PANTALLA 5: PANEL DE USUARIO / AJUSTES
═══════════════════════════════════════════════════════════════════

  ┌─────────────────────────────────────┐
  │  ≡  MI CUENTA              🚪     │
  ├─────────────────────────────────────┤
  │                                     │
  │         ╭───────────────────╮       │
  │        │                   │       │
  │        │    🎵 SYNTHL     │       │  ← Logo / Avatar
  │        │                   │       │
  │        │   DJ_MusicLover   │       │
  │        │   synth@gmail.com │       │
  │        │                   │       │
  │         ╰───────────────────╯       │
  │                                     │
  │  ┌─ ESTADÍSTICAS ──────────────────┐│
  │  │  🏆 Sints Detectados: 2        ││
  │  │  📊 Análisis Realizados: 47    ││
  │  │  ⏱️ Tiempo de Uso: 12h 34min   ││
  │  │  🔥 Racha Actual: 5 días       ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ┌─ LOGROS ────────────────────────┐│
  │  │  🥇 "Primer Synth"  ✅          ││  ← Logros desbloqueados
  │  │  🥈 "5 Diferentes"  🔒          ││
  │  │  🥉 "10 Marcas"     🔒          ││
  │  │  🏅 "Maestro Audio" 🔒          ││
  │  │  🎖️ "100 Análisis"  🔒          ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ┌─ CONFIGURACIÓN ─────────────────┐│
  │  │                                 ││
  │  │  🎤 Fuente de Audio            ││
  │  │  ┌─────────────────────────┐   ││
  │  │  │ ● Micrófono del Dispositivo│ ││  ← Selector de fuente
  │  │  │ ○ Audio del Sistema      │  ││
  │  │  └─────────────────────────┘   ││
  │  │                                 ││
  │  │  🎯 Sensibilidad del Detector  ││
  │  │  ━━━━━━━━━●━━━━━━━━━━━━━━━━   ││  ← Slider de sensibilidad
  │  │  Baja      Media      Alta     ││
  │  │                                 ││
  │  │  🌊 Mostrar Espectro           ││
  │  │  ┌──────┐                       ││
  │  │  │ ON   │                       ││  ← Toggle
  │  │  └──────┘                       ││
  │  │                                 ││
  │  │  🔊 Notificaciones de Detección││
  │  │  ┌──────┐                       ││
  │  │  │ ON   │                       ││
  │  │  └──────┘                       ││
  │  │                                 ││
  │  │  🌙 Tema                        ││
  │  │  ○ Oscuro (Actual)             ││
  │  │  ○ Claro                       ││
  │  │  ○ Auto                        ││
  │  │                                 ││
  │  │  🗑️ Limpiar Historial          ││
  │  │  [  Borrar Todos los Datos  ]  ││
  │  │                                 ││
  │  └─────────────────────────────────┘│
  │                                     │
  │  ╭─────────────────────────────╮    │
  │  │     Sobre SynthLens v1.0    │    │
  │  │  © 2026 SynthLens Labs      │    │  ← Info de la app
  │  ╰─────────────────────────────╯    │
  │                                     │
  ├─────────────────────────────────────┤
  │  🎹      📊      🔊      📚   👤   │
  └─────────────────────────────────────┘


═══════════════════════════════════════════════════════════════════
  PALETA DE COLORES SYNTHLENS
═══════════════════════════════════════════════════════════════════

  Fondo:         #0A0A0F (negro profundo)
  Superficie:    #12121A (azul muy oscuro)
  Card:          #16162A (azul oscuro)
  Borde:         #2A2A3E (gris-azulado)
  
  Primario:      #00E5FF (Cian brillante - osciloscopio)
  Secundario:    #FF00FF (Magenta - synth pop)
  Terciario:     #7C4DFF (Púrpura - modulación)
  
  Acentos:       #76FF03 (Verde neon - waveform)
                 #FFD740 (Ámbar - alertas)
                 #FF1744 (Rojo - errores)
                 #FF9100 (Naranja - warnings)
  
  Texto:         #E0E0E0 (blanco suave)
  Texto débil:   #9E9E9E (gris medio)
  
  Waveform:      #00E676 (verde osciloscopio)


═══════════════════════════════════════════════════════════════════
  FLUJO DE NAVEGACIÓN
═══════════════════════════════════════════════════════════════════

  [ABRIR APP]
       │
       ▼
  ┌─────────────┐
  │ PANTALLA 1   │  ← Al abrir, escucha audio automáticamente
  │ ANALIZADOR   │
  └──────┬──────┘
         │ Al detectar sintetizador
         ▼
  ┌─────────────┐
  │ PANTALLA 2   │  ← Muestra detalles completos del análisis
  │ DETALLES     │
  └──────┬──────┘
         │ Usuario guarda en librería
         ▼
  ┌─────────────┐
  │ PANTALLA 4   │  ← Aparece con trofeo/badge
  │ LIBRERÍA     │
  └─────────────┘

  NAVEGACIÓN PARALELA:
  
  Bottom Nav:
  🎹 → Pantalla 1 (Home/Analizador)
  📊 → Pantalla 2 (Detalles del último análisis)
  🔊 → Pantalla 3 (Espectro en vivo)
  📚 → Pantalla 4 (Biblioteca)
  👤 → Pantalla 5 (Perfil/Ajustes)
