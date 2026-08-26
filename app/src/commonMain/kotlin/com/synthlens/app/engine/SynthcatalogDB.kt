package com.synthlens.app.engine

/**
 * SYNTHLENS — CATALOGO MAESTRO DE SINTETIZADORES
 * La base de datos mas completa del mundo de sintetizadores.
 * Incluye perfil de deteccion, specs, enlaces oficiales y precios.
 *
 * Estructura:
 *   1. Data model (SynthInfo, FilterProfile, OscillatorProfile, etc.)
 *   2. Base de datos completa por fabricante
 *   3. Indices de busqueda
 */

// ================================================================
//  DATA MODEL
// ================================================================

data class FloatRange(val min: Float, val max: Float) {
    fun contains(v: Float) = v in min..max
    fun matchScore(v: Float): Float = when {
        contains(v) -> 1f
        v < min -> (1f - (min - v) / (min + 0.001f)).coerceIn(0f, 1f)
        else -> (1f - (v - max) / (max + 0.001f)).coerceIn(0f, 1f)
    }
}


data class SynthInfo(
    val id: String,                    // ID unico (ej: "moog_sub37")
    val name: String,                  // Nombre comercial
    val brand: String,                 // Fabricante
    val year: Int?,                    // Ano de lanzamiento
    val category: SynthCategory,       // Categoria
    val synthesisType: SynthType,      // Tipo de sintesis
    val polyphony: Int,                // Polifonia (1 = mono)
    val oscillatorProfile: OscillatorProfile,
    val filterProfile: FilterProfile,
    val envelopeProfile: EnvelopeProfile,
    val modulationProfile: ModulationProfile,
    val physicalProfile: PhysicalProfile,
    val purchaseInfo: PurchaseInfo,
    val tags: List<String>,            // Tags para busqueda
    val presetCount: Int = 0,          // Numero de presets
    val weight: String = "",           // Peso del hardware
    val dimensions: String = "",       // Dimensiones
    val description: String = ""       // Descripcion corta
)

enum class SynthCategory {
    MONO_SYNTH,         // Monofonico clasico
    POLY_SYNTH,         // Polifonico
    PARAPHONIC,         // Parafonico
    SEMI_MODULAR,       // Semi-modular
    MODULAR,            // Modular Eurorack/Desktop
    FM_SYNTH,           // FM (Frequency Modulation)
    WAVETABLE,          // Wavetable
    DIGITAL,            // Digital general
    ANALOG_MODELING,    // Modelado analogico
    GRANULAR,           // Granular
    PHYSICAL_MODELING,  // Modelado fisico (Karplus-Strong)
    SAMPLER,            // Sampler / Rompler
    WORKSTATION,        // Workstation
    DRUM_SYNTH,         // Sintesis de bateria
    GROOVEBOX,          // Groovebox
    HYBRID,             // Hibrido analogo/digital
    ORGAN,              // Organo
    KEYTAR,             // Keytar
    PORTABLE,           // Portatil / Pocket
    ACCORDION,          // Acordeon / V-Accordion
    EFFECTS_PROCESSOR    // Procesador de efectos con sintesis
}

enum class SynthType {
    ANALOG_HARDWARE,
    DIGITAL_HARDWARE,
    HYBRID_HARDWARE,
    SOFTWARE_PLUGIN,
    VIRTUAL_ANALOG,
    ANALOG_MODELING,
    MODULAR_SYSTEM,
    TABLETOP,
    RACKMOUNT,
    DESKTOP_MODULE,
    KEYBOARD_CONTROLLER
}

data class OscillatorProfile(
    val count: Int,                        // Numero de osciladores
    val types: List<String>,               // Tipos disponibles (saw, square, sine, etc.)
    val hasSubOscillator: Boolean = false,  // Sub-oscilador
    val hasNoise: Boolean = false,          // Generador de ruido
    val hasRingMod: Boolean = false,        // Ring modulation
    val hasSync: Boolean = false,           // Oscillator sync
    val hasPWM: Boolean = false,            // Pulse width modulation
    val hasFM: Boolean = false,             // FM entre osciladores
    val detuneRange: FloatRange = FloatRange(0f, 0f),  // Rango de desafinacion en cents
    val driftCents: FloatRange = FloatRange(0f, 0f),   // Drift analogico
    val voiceCount: Int = 1,               // Voces por nota (1=sin unison)
    val wavetableSupport: Boolean = false   // Soporte wavetable
)

data class FilterProfile(
    val type: String,                      // Tipo (Moog Ladder, SVF, CEM, etc.)
    val slopes: List<Int>,                 // Slopes disponibles (6, 12, 18, 24 dB/oct)
    val hasResonance: Boolean = true,
    val resonanceRange: FloatRange = FloatRange(0f, 1f),
    val hasSelfOscillation: Boolean = false,
    val hasKeyTracking: Boolean = false,
    val hasFilterFM: Boolean = false,
    val modes: List<String> = listOf("LP"),  // LP, HP, BP, Notch, etc.
    val count: Int = 1                     // Numero de filtros
)

data class EnvelopeProfile(
    val count: Int,                        // Numero de envolventes
    val hasDAHDSR: Boolean = false,        // Delay Attack Hold Decay Sustain Release
    val hasLooping: Boolean = false,        // Loop en envolvente
    val hasVelocity: Boolean = false,       // Respuesta a velocidad
    val attackRange: FloatRange = FloatRange(0.001f, 5f),  // Segundos
    val decayRange: FloatRange = FloatRange(0.001f, 5f),
    val releaseRange: FloatRange = FloatRange(0.001f, 10f)
)

data class ModulationProfile(
    val lfoCount: Int = 0,
    val lfoWaveforms: List<String> = emptyList(),
    val hasModMatrix: Boolean = false,
    val modMatrixSlots: Int = 0,
    val hasAftertouch: Boolean = false,
    val hasVelocity: Boolean = false,
    val hasMPE: Boolean = false,            // MIDI Polyphonic Expression
    val hasArpeggiator: Boolean = false,
    val hasSequencer: Boolean = false,
    val sequencerSteps: Int = 0,
    val hasPolyMod: Boolean = false,           // Polyphonic modulation routing (Prophet-5, OB-Xa, etc.)
    val hasChord: Boolean = false
)

data class PhysicalProfile(
    val formFactor: String = "",            // Keyboard, Module, Rack, Desktop
    val keyCount: Int = 0,                  // Numero de teclas
    val hasMIDI: Boolean = true,
    val hasUSB: Boolean = false,
    val hasCV: Boolean = false,             // CV/Gate (modular)
    val hasAudioIn: Boolean = false,
    val hasAudioOut: Int = 1,               // Numero de salidas de audio
    val hasHeadphone: Boolean = false,
    val hasDisplay: Boolean = false,
    val displayType: String = "",            // OLED, LCD, LED, etc.
    val hasBluetooth: Boolean = false,        // Bluetooth MIDI / audio (Jupiter-Xm, etc.)
    val hasDCB: Boolean = false
)

data class PurchaseInfo(
    val officialUrl: String,               // URL oficial del producto
    val brandUrl: String,                  // URL del fabricante
    val priceUSD: Float?,                  // Precio en dolares (aprox)
    val priceEUR: Float?,                  // Precio en euros (aprox)
    val availability: Availability,        // Disponibilidad
    val isDiscontinued: Boolean = false,   // Descontinuado
    val buyLinks: List<BuyLink> = emptyList(),  // Enlaces de compra
    val manualUrl: String = ""             // URL del manual
)

data class BuyLink(
    val store: String,                     // Nombre de la tienda
    val url: String,                       // URL
    val storeType: StoreType = StoreType.OFFICIAL
)

enum class Availability {
    AVAILABLE,          // En produccion y venta
    PREORDER,           // Preventa
    LIMITED,            // Edicion limitada
    USED_ONLY,          // Solo usado
    DISCONTINUED,       // Descontinuado
    RARE                // Muy dificil de encontrar
}

enum class StoreType {
    OFFICIAL,           // Tienda oficial del fabricante
    DEALER,             // Distribuidor autorizado
    MARKETPLACE,        // Marketplace (Amazon, etc.)
    USED_MARKET         // Mercado de segunda mano
}

// ================================================================
//  CATALOGO MAESTRO
//  Organizado por fabricante, orden alfabetico
// ================================================================

object SynthCatalogDB {

    val catalog = mutableMapOf<String, SynthInfo>()

    init {
        // ============================================================
        //  ALESIS
        // ============================================================
        add("alesis_andromeda", SynthInfo(
            id = "alesis_andromeda", name = "Andromeda", brand = "Alesis",
            year = 2009, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Moog-style", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=8, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true, hasCV=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.alesis.com/product/andromeda",
                brandUrl = "https://www.alesis.com",
                priceUSD = 1499f, priceEUR = 1399f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/AlesisAndromeda", StoreType.DEALER),
                    BuyLink("Thomann", "https://www.thomann.de/intl/alesis_andromeda.htm", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","8-voice","polyphonic","vintage-reissue","keyboard-controller"),
            presetCount = 128,
            description = "An 8-voice polyphonic analog synth with dual oscillators per voice and Moog-style filter."
        ))

        // ============================================================
        //  ARP
        // ============================================================
        add("arp_odyssey", SynthInfo(
            id = "arp_odyssey", name = "Odyssey", brand = "ARP",
            year = 1972, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","pulse"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Moog-style", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","square","S&H"), hasArpeggiator=true),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasCV=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.arp-synthesizers.com/odyssey",
                brandUrl = "https://www.arp-synthesizers.com",
                priceUSD = null, priceEUR = null, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=arp+odyssey", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=arp+odyssey", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("analog","monophonic","vintage","70s","legendary","faders"),
            presetCount = 64,
            description = "Competitor directo del Minimoog, con controles por faders en lugar de knobs. Usado en el diseño de sonido de R2-D2, Doctor Who, y por Herbie Hancock y Nine Inch Nails."
        ))

        add("arp_2600", SynthInfo(
            id = "arp_2600", name = "2600", brand = "ARP",
            year = 1971, category = SynthCategory.SEMI_MODULAR,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(3, listOf("saw","square","triangle","sine"), hasRingMod=true, hasSync=true, hasFM=true, driftCents=FloatRange(2f,10f)),
            filterProfile = FilterProfile("ARP 4072 Clone", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true),
            modulationProfile = ModulationProfile(1, listOf("sine","S&H"), hasModMatrix=true, modMatrixSlots=2, hasSequencer=true, sequencerSteps=16),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=true, hasUSB=true, hasCV=true, hasAudioIn=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.arp-synthesizers.com/2600",
                brandUrl = "https://www.arp-synthesizers.com",
                priceUSD = null, priceEUR = null, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=arp+2600", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=arp+2600", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("analog","semi-modular","vintage","70s","legendary","patch-points","faders"),
            presetCount = 128,
            description = "Semi-modular icónico, usado en el diseño de sonido de R2-D2, Doctor Who, y por Herbie Hancock y Nine Inch Nails."
        ))

        // ============================================================
        //  BEHRINGER
        // ============================================================
        add("behringer_model_d", SynthInfo(
            id = "behringer_model_d", name = "Model D", brand = "Behringer",
            year = 2018, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(3, listOf("saw","square","triangle"), hasNoise=true, hasSubOscillator=true, hasPWM=true, driftCents=FloatRange(3f,12f)),
            filterProfile = FilterProfile("Moog Ladder Clone", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(0, emptyList()),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasCV=true, hasAudioIn=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.behringer.com/product.html?modelCode=P0DN1",
                brandUrl = "https://www.behringer.com",
                priceUSD = 299f, priceEUR = 279f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Amazon", "https://www.amazon.com/s?k=behringer+model+d", StoreType.MARKETPLACE),
                    BuyLink("Thomann", "https://www.thomann.de/intl/behringer_model_d.htm", StoreType.DEALER),
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/BehringerModelD", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","monophonic","minimoog-clone","eurorack","affordable","3-oscillator"),
            description = "Faithful clone of the Minimoog Model D. Three oscillators, classic Moog ladder filter, and Eurorack compatible."
        ))

        add("behringer_deepmind12", SynthInfo(
            id = "behringer_deepmind12", name = "DeepMind 12", brand = "Behringer",
            year = 2016, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 12,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle","noise"), hasNoise=true, hasPWM=true, driftCents=FloatRange(0.5f,3f)),
            filterProfile = FilterProfile("OTA-based", listOf(12, 24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(3, hasDAHDSR=true, hasVelocity=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=8, hasAftertouch=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=32),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasUSB=true, hasMIDI=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.behringer.com/product.html?modelCode=P0D0Z",
                brandUrl = "https://www.behringer.com",
                priceUSD = 599f, priceEUR = 549f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Thomann", "https://www.thomann.de/intl/behringer_deepmind12.htm", StoreType.DEALER),
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/BehringerDeepMind12", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","12-voice","polyphonic","midi","usb","lcd-display"),
            presetCount = 128,
            description = "12-voice polyphonic analog synth with 49 keys, USB/MIDI connectivity, and LCD display."
        ))

        add("behringer_poly_d", SynthInfo(
            id = "behringer_poly_d", name = "POLY D", brand = "Behringer",
            year = 2020, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 4,
            oscillatorProfile = OscillatorProfile(3, listOf("saw","square","triangle"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(3f,12f)),
            filterProfile = FilterProfile("Moog Ladder Clone", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","square","S&H"), hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 37, hasMIDI=true, hasUSB=true, hasCV=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.behringer.com/product.html?modelCode=P0DN1",
                brandUrl = "https://www.behringer.com",
                priceUSD = 499f, priceEUR = 449f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Thomann", "https://www.thomann.de/intl/behringer_poly_d.htm", StoreType.DEALER),
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/BehringerPolyD", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","4-voice","polyphonic","minimoog-inspired","affordable"),
            description = "Four-voice paraphonic analog synth with three oscillators per voice and classic Moog-style ladder filter."
        ))

        add("behringer_pro_1", SynthInfo(
            id = "behringer_pro_1", name = "PRO-1", brand = "Behringer",
            year = 2019, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle"), hasSync=true, hasPWM=true, hasFM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("SSM2044 Clone", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=4, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=true, hasUSB=true, hasCV=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.behringer.com/product.html?modelCode=P0DDW",
                brandUrl = "https://www.behringer.com",
                priceUSD = 299f, priceEUR = 269f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Thomann", "https://www.thomann.de/intl/behringer_pro_1.htm", StoreType.DEALER))
            ),
            tags = listOf("analog","monophonic","pro-one-clone","affordable","eurorack-friendly"),
            description = "Faithful clone of the Sequential Pro-One mono synth. Two oscillators with sync and FM, SSM-style filter."
        ))

        add("behringer_neutron", SynthInfo(
            id = "behringer_neutron", name = "Neutron", brand = "Behringer",
            year = 2018, category = SynthCategory.SEMI_MODULAR,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle"), hasSync=true, hasPWM=true, hasFM=true, driftCents=FloatRange(2f,10f)),
            filterProfile = FilterProfile("OTA Dual Filter", listOf(12, 24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(2, hasLooping=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=1, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=true, hasUSB=true, hasCV=true, hasAudioIn=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.behringer.com/product.html?modelCode=P0DDX",
                brandUrl = "https://www.behringer.com",
                priceUSD = 349f, priceEUR = 319f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Thomann", "https://www.thomann.de/intl/behringer_neutron.htm", StoreType.DEALER))
            ),
            tags = listOf("analog","semi-modular","patch-points","eurorack","affordable"),
            description = "Semi-modular analog synth with dual oscillators, parallel filter architecture and 56 patch points."
        ))

        add("behringer_crave", SynthInfo(
            id = "behringer_crave", name = "Crave", brand = "Behringer",
            year = 2019, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(1, listOf("saw","square","triangle"), hasSubOscillator=true, hasPWM=true, driftCents=FloatRange(3f,12f)),
            filterProfile = FilterProfile("Moog Ladder Clone", listOf(24), hasResonance=true, hasSelfOscillation=true),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","S&H"), hasSequencer=true, sequencerSteps=32),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=true, hasUSB=true, hasCV=true, hasAudioIn=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.behringer.com/product.html?modelCode=P0DDY",
                brandUrl = "https://www.behringer.com",
                priceUSD = 199f, priceEUR = 179f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Thomann", "https://www.thomann.de/intl/behringer_crave.htm", StoreType.DEALER))
            ),
            tags = listOf("analog","monophonic","semi-modular","eurorack","budget"),
            description = "Affordable mono analog synth with Moog-style ladder filter and built-in sequencer."
        ))

        add("behringer_td3", SynthInfo(
            id = "behringer_td3", name = "TD-3", brand = "Behringer",
            year = 2019, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(1, listOf("saw","square"), hasSubOscillator=true, driftCents=FloatRange(5f,15f)),
            filterProfile = FilterProfile("TB-303 Ladder Clone", listOf(24), hasResonance=true, hasSelfOscillation=true),
            envelopeProfile = EnvelopeProfile(1),
            modulationProfile = ModulationProfile(1, listOf("sine","S&H"), hasSequencer=true, sequencerSteps=16),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=true, hasUSB=true, hasCV=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.behringer.com/product.html?modelCode=P0BN9",
                brandUrl = "https://www.behringer.com",
                priceUSD = 149f, priceEUR = 139f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Thomann", "https://www.thomann.de/intl/behringer_td3.htm", StoreType.DEALER))
            ),
            tags = listOf("analog","monophonic","tb-303-clone","acid","bass-line"),
            description = "Affordable clone of the Roland TB-303 bass-line synth with built-in sequencer."
        ))

        add("behringer_rd8", SynthInfo(
            id = "behringer_rd8", name = "RD-8", brand = "Behringer",
            year = 2019, category = SynthCategory.DRUM_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(0, emptyList(), hasNoise=true),
            filterProfile = FilterProfile("Analog", listOf(12, 24), hasResonance=true),
            envelopeProfile = EnvelopeProfile(0),
            modulationProfile = ModulationProfile(0, emptyList(), hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=true, hasUSB=true, hasCV=true, hasAudioIn=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.behringer.com/product.html?modelCode=P0BN0",
                brandUrl = "https://www.behringer.com",
                priceUSD = 299f, priceEUR = 279f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Thomann", "https://www.thomann.de/intl/behringer_rd8.htm", StoreType.DEALER))
            ),
            tags = listOf("analog","drum-machine","tr-808-clone","rhythm","affordable"),
            description = "Analog drum machine cloning the Roland TR-808 with 16-step sequencer and individual outputs."
        ))

        add("behringer_ubxa", SynthInfo(
            id = "behringer_ubxa", name = "UB-Xa", brand = "Behringer",
            year = 2023, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle"), hasSync=true, hasPWM=true, driftCents=FloatRange(1f,6f)),
            filterProfile = FilterProfile("SEM-style 2-pole", listOf(12, 24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP","Notch")),
            envelopeProfile = EnvelopeProfile(2, hasVelocity=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","S&H"), hasModMatrix=true, modMatrixSlots=8, hasAftertouch=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64, hasPolyMod=true),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.behringer.com/product.html?modelCode=P0E4A",
                brandUrl = "https://www.behringer.com",
                priceUSD = 799f, priceEUR = 729f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Thomann", "https://www.thomann.de/intl/behringer_ub_xa.htm", StoreType.DEALER))
            ),
            tags = listOf("analog","8-voice","polyphonic","ob-xa-clone","sem-filter"),
            description = "Eight-voice analog polysynth cloning the Oberheim OB-Xa with SEM-style multi-mode filters."
        ))

        add("behringer_2600", SynthInfo(
            id = "behringer_2600", name = "2600", brand = "Behringer",
            year = 2020, category = SynthCategory.SEMI_MODULAR,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(3, listOf("saw","square","triangle","sine"), hasRingMod=true, hasSync=true, hasFM=true, driftCents=FloatRange(2f,10f)),
            filterProfile = FilterProfile("ARP 4072 Clone", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true),
            modulationProfile = ModulationProfile(1, listOf("sine","S&H"), hasModMatrix=true, modMatrixSlots=2, hasSequencer=true, sequencerSteps=16),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=true, hasUSB=true, hasCV=true, hasAudioIn=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.behringer.com/product.html?modelCode=P0CC7",
                brandUrl = "https://www.behringer.com",
                priceUSD = 699f, priceEUR = 649f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Thomann", "https://www.thomann.de/intl/behringer_2600.htm", StoreType.DEALER))
            ),
            tags = listOf("analog","semi-modular","arp-2600-clone","patch-points","classic"),
            description = "Clone of the legendary ARP 2600 semi-modular synth with three oscillators and integrated spring reverb."
        ))

        // ============================================================
        //  KORG
        // ============================================================
        add("korg_ms20", SynthInfo(
            id = "korg_ms20", name = "MS-20", brand = "Korg",
            year = 1978, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Korg 2-pole", listOf(12, 24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","S&H"), hasArpeggiator=true, hasSequencer=true, sequencerSteps=16),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasCV=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.korg.com/us/products/synths/ms20/",
                brandUrl = "https://www.korg.com",
                priceUSD = null, priceEUR = null, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=korg+ms-20", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=korg+ms-20", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("analog","monophonic","vintage","70s","legendary","aggressive-filter","patchable"),
            presetCount = 64,
            description = "Filtros agresivos, patching, sonido reconocible al instante."
        ))

        add("korg_monopoly", SynthInfo(
            id = "korg_monopoly", name = "Monopoly", brand = "Korg",
            year = 1979, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Korg 2-pole", listOf(12, 24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","S&H"), hasArpeggiator=true, hasSequencer=true, sequencerSteps=16),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasCV=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.korg.com/us/products/synths/monopoly/",
                brandUrl = "https://www.korg.com",
                priceUSD = null, priceEUR = null, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=korg+monopoly", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=korg+monopoly", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("analog","monophonic","vintage","70s","legendary","aggressive-filter","patchable"),
            presetCount = 64,
            description = "Variante del MS-20 con diferentes filtros y osciladores."
        ))

        add("korg_minilogue", SynthInfo(
            id = "korg_minilogue", name = "Minilogue", brand = "Korg",
            year = 2017, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 4,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Korg 2-pole", listOf(24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=8, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 37, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.korg.com/us/products/synths/minilogue/",
                brandUrl = "https://www.korg.com",
                priceUSD = 899f, priceEUR = 849f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/KorgMinilogue", StoreType.DEALER),
                    BuyLink("Thomann", "https://www.thomann.de/intl/korg_minilogue.htm", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","4-voice","polyphonic","vintage-reissue","keyboard-controller"),
            presetCount = 64,
            description = "4-voice polyphonic analog synth with twin oscillators per voice, powerful filters, and built-in sequencer."
        ))

        add("korg_m1", SynthInfo(
            id = "korg_m1", name = "M1", brand = "Korg",
            year = 1988, category = SynthCategory.WORKSTATION,
            synthesisType = SynthType.DIGITAL_HARDWARE, polyphony = 48,
            oscillatorProfile = OscillatorProfile(16, listOf("saw","square","sine","pcm","noise"), hasNoise=true, hasPWM=true, hasFM=true, wavetableSupport=true),
            filterProfile = FilterProfile("Digital", listOf(24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(4, hasDAHDSR=true, hasVelocity=true, hasLooping=true),
            modulationProfile = ModulationProfile(4, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=32, hasAftertouch=true, hasMPE=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 61, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.korg.com/us/products/workstations/m1/",
                brandUrl = "https://www.korg.com",
                priceUSD = 2499f, priceEUR = 2299f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=korg+m1", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=korg+m1", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("digital","48-voice","workstation","sampling","sequencer","classic"),
            presetCount = 128,
            description = "The workstation that transformed the industry. One of the most sold synths in history with sampling, sequencing, and extensive effects."
        ))

        // ============================================================
        //  NOVATION
        // ============================================================
        add("novation_peak", SynthInfo(
            id = "novation_peak", name = "Peak", brand = "Novation",
            year = 2017, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.HYBRID_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(0.5f,3f)),
            filterProfile = FilterProfile("Moog-style", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true, hasVelocity=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=8, hasAftertouch=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.novationmusic.com/products/Peak",
                brandUrl = "https://www.novationmusic.com",
                priceUSD = 1799f, priceEUR = 1699f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/NovationPeak", StoreType.DEALER),
                    BuyLink("Thomann", "https://www.thomann.de/intl/novation_peak.htm", StoreType.DEALER)
                )
            ),
            tags = listOf("hybrid","8-voice","polyphonic","velocity","aftertouch","sequencer"),
            presetCount = 128,
            description = "Hybrid analog/digital synth with 8 voices, velocity/aftertouch, and advanced modulation."
        ))

        add("novation_summit", SynthInfo(
            id = "novation_summit", name = "Summit", brand = "Novation",
            year = 2021, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.HYBRID_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(0.5f,3f)),
            filterProfile = FilterProfile("Moog-style", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true, hasVelocity=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=8, hasAftertouch=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.novationmusic.com/products/Summit",
                brandUrl = "https://www.novationmusic.com",
                priceUSD = 1999f, priceEUR = 1899f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/NovationSummit", StoreType.DEALER),
                    BuyLink("Thomann", "https://www.thomann.de/intl/novation_summit.htm", StoreType.DEALER)
                )
            ),
            tags = listOf("hybrid","8-voice","polyphonic","velocity","aftertouch","sequencer"),
            presetCount = 128,
            description = "High-end hybrid analog/digital synth with 8 voices, velocity/aftertouch, and advanced modulation."
        ))

        // ============================================================
        //  SEQUENTIAL
        // ============================================================
        add("sequential_prophet5", SynthInfo(
            id = "sequential_prophet5", name = "Prophet-5 Rev4", brand = "Sequential",
            year = 2020, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 5,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","triangle","pulse"), hasNoise=true, hasPWM=true, driftCents=FloatRange(1f,6f)),
            filterProfile = FilterProfile("SSM2040 / CEM3320", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","square","random","S&H"), hasModMatrix=true, modMatrixSlots=8, hasPolyMod=true),
            physicalProfile = PhysicalProfile("Keyboard", 40, hasMIDI=true, hasUSB=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.sequential.com/product/prophet-5/",
                brandUrl = "https://www.sequential.com",
                priceUSD = 4299f, priceEUR = 3999f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sequential Official", "https://www.sequential.com/product/prophet-5/", StoreType.OFFICIAL),
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/SequentialProphet5", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","5-voice","polyphonic","legendary","classic","vintage-reissue"),
            presetCount = 40,
            description = "The legendary Prophet-5, reissued in its original form. 5 voices of pure analog with SSM/CEM filters. The definitive analog polysynth."
        ))

        add("sequential_prophet6", SynthInfo(
            id = "sequential_prophet6", name = "Prophet-6", brand = "Sequential",
            year = 2015, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 6,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","triangle","pulse","sine"), hasNoise=true, hasPWM=true, hasFM=true, driftCents=FloatRange(0.5f,4f)),
            filterProfile = FilterProfile("SSM2044", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasVelocity=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=16, hasAftertouch=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.sequential.com/product/prophet-6/",
                brandUrl = "https://www.sequential.com",
                priceUSD = 4999f, priceEUR = 4599f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sequential Official", "https://www.sequential.com/product/prophet-6/", StoreType.OFFICIAL),
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/SequentialProphet6", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","6-voice","polyphonic","vintage-reissue","classic"),
            presetCount = 64,
            description = "The Prophet-6 is a faithful recreation of the iconic Prophet-6 synthesizer. Six voices of pure analog sound with modern features."
        ))

        add("sequential_ob6", SynthInfo(
            id = "sequential_ob6", name = "OB-6", brand = "Sequential",
            year = 2017, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 6,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","triangle","pulse","sine"), hasNoise=true, hasPWM=true, hasFM=true, driftCents=FloatRange(0.5f,4f)),
            filterProfile = FilterProfile("SSM2044", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasVelocity=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=16, hasAftertouch=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.sequential.com/product/ob-6/",
                brandUrl = "https://www.sequential.com",
                priceUSD = 4999f, priceEUR = 4599f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sequential Official", "https://www.sequential.com/product/ob-6/", StoreType.OFFICIAL),
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/SequentialOB6", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","6-voice","polyphonic","vintage-reissue","classic"),
            presetCount = 64,
            description = "The OB-6 is a modern interpretation of the legendary Oberheim OB-6. Six voices of pure analog sound with modern features."
        ))

        add("sequential_prophet_rev2", SynthInfo(
            id = "sequential_prophet_rev2", name = "Prophet REV2", brand = "Sequential",
            year = 2017, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","triangle","pulse","sine"), hasNoise=true, hasPWM=true, hasFM=true, driftCents=FloatRange(0.5f,4f)),
            filterProfile = FilterProfile("SSM2044", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasVelocity=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=16, hasAftertouch=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.sequential.com/product/prophet-rev2/",
                brandUrl = "https://www.sequential.com",
                priceUSD = 5999f, priceEUR = 5599f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sequential Official", "https://www.sequential.com/product/prophet-rev2/", StoreType.OFFICIAL),
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/SequentialProphetRev2", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","8-voice","polyphonic","vintage-reissue","classic"),
            presetCount = 64,
            description = "The Prophet REV2 is a modern take on the classic Prophet-5. Eight voices of pure analog sound with modern features."
        ))

        add("sequential_take5", SynthInfo(
            id = "sequential_take5", name = "Take 5", brand = "Sequential",
            year = 2018, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 5,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","triangle","pulse","sine"), hasNoise=true, hasPWM=true, hasFM=true, driftCents=FloatRange(0.5f,4f)),
            filterProfile = FilterProfile("SSM2044", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasVelocity=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=16, hasAftertouch=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.sequential.com/product/take-5/",
                brandUrl = "https://www.sequential.com",
                priceUSD = 4999f, priceEUR = 4599f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sequential Official", "https://www.sequential.com/product/take-5/", StoreType.OFFICIAL),
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/SequentialTake5", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","5-voice","polyphonic","vintage-reissue","classic"),
            presetCount = 64,
            description = "The Take 5 is a compact version of the Prophet-5. Five voices of pure analog sound with modern features."
        ))

        add("sequential_prophet5_rev4", SynthInfo(
            id = "sequential_prophet5_rev4", name = "Prophet-5 Rev4", brand = "Sequential",
            year = 2020, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 5,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","triangle","pulse"), hasNoise=true, hasPWM=true, driftCents=FloatRange(1f,6f)),
            filterProfile = FilterProfile("SSM2040 / CEM3320", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","square","random","S&H"), hasModMatrix=true, modMatrixSlots=8, hasPolyMod=true),
            physicalProfile = PhysicalProfile("Keyboard", 40, hasMIDI=true, hasUSB=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.sequential.com/product/prophet-5/",
                brandUrl = "https://www.sequential.com",
                priceUSD = 4299f, priceEUR = 3999f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sequential Official", "https://www.sequential.com/product/prophet-5/", StoreType.OFFICIAL),
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/SequentialProphet5", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","5-voice","polyphonic","legendary","classic","vintage-reissue"),
            presetCount = 40,
            description = "The legendary Prophet-5, reissued in its original form. 5 voices of pure analog with SSM/CEM filters. The definitive analog polysynth."
        ))

        // ============================================================
        //  ARTURIA
        // ============================================================
        add("arturia_microbrute", SynthInfo(
            id = "arturia_microbrute", name = "MicroBrute", brand = "Arturia",
            year = 2015, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(1, listOf("saw","square","triangle"), hasSubOscillator=true, hasPWM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Moog-style", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","S&H"), hasSequencer=true, sequencerSteps=16),
            physicalProfile = PhysicalProfile("Keyboard", 37, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.arturia.com/products/synths/microbrute",
                brandUrl = "https://www.arturia.com",
                priceUSD = 499f, priceEUR = 449f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/ArturiaMicroBrute", StoreType.DEALER),
                    BuyLink("Thomann", "https://www.thomann.de/intl/arturia_microbrute.htm", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","monophonic","vintage-reissue","keyboard-controller"),
            presetCount = 64,
            description = "Compact monophonic analog synth with Moog-style filter, built-in sequencer, and USB/MIDI connectivity."
        ))

        add("arturia_matrixbrute", SynthInfo(
            id = "arturia_matrixbrute", name = "MatrixBrute", brand = "Arturia",
            year = 2016, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 4,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Moog-style", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=8, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.arturia.com/products/synths/matrixbrute",
                brandUrl = "https://www.arturia.com",
                priceUSD = 1099f, priceEUR = 1049f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/ArturiaMatrixBrute", StoreType.DEALER),
                    BuyLink("Thomann", "https://www.thomann.de/intl/arturia_matrixbrute.htm", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","4-voice","polyphonic","vintage-reissue","keyboard-controller"),
            presetCount = 64,
            description = "4-voice polyphonic analog synth with twin oscillators per voice, powerful filters, and built-in sequencer."
        ))

        add("arturia_polybrute", SynthInfo(
            id = "arturia_polybrute", name = "PolyBrute", brand = "Arturia",
            year = 2018, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Moog-style", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=8, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.arturia.com/products/synths/polybrute",
                brandUrl = "https://www.arturia.com",
                priceUSD = 1999f, priceEUR = 1899f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/ArturiaPolyBrute", StoreType.DEALER),
                    BuyLink("Thomann", "https://www.thomann.de/intl/arturia_polybrute.htm", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","8-voice","polyphonic","vintage-reissue","keyboard-controller"),
            presetCount = 64,
            description = "8-voice polyphonic analog synth with twin oscillators per voice, powerful filters, and built-in sequencer."
        ))

        add("arturia_minibrute2s", SynthInfo(
            id = "arturia_minibrute2s", name = "MiniBrute 2S", brand = "Arturia",
            year = 2017, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(1, listOf("saw","square","triangle"), hasSubOscillator=true, hasPWM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Moog-style", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","S&H"), hasSequencer=true, sequencerSteps=16),
            physicalProfile = PhysicalProfile("Keyboard", 37, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.arturia.com/products/synths/minibrute2s",
                brandUrl = "https://www.arturia.com",
                priceUSD = 999f, priceEUR = 949f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/ArturiaMiniBrute2S", StoreType.DEALER),
                    BuyLink("Thomann", "https://www.thomann.de/intl/arturia_minibrute2s.htm", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","monophonic","vintage-reissue","keyboard-controller"),
            presetCount = 64,
            description = "Compact monophonic analog synth with Moog-style filter, built-in sequencer, and USB/MIDI connectivity."
        ))

        add("arturia_microfreak", SynthInfo(
            id = "arturia_microfreak", name = "MicroFreak", brand = "Arturia",
            year = 2018, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle","pulse","sine"), hasSubOscillator=true, hasNoise=true, hasPWM=true, hasFM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Moog-style", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=8, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 25, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.arturia.com/products/synths/microfreak",
                brandUrl = "https://www.arturia.com",
                priceUSD = 1199f, priceEUR = 1149f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/ArturiaMicroFreak", StoreType.DEALER),
                    BuyLink("Thomann", "https://www.thomann.de/intl/arturia_microfreak.htm", StoreType.DEALER)
                )
            ),
            tags = listOf("analog","8-voice","polyphonic","vintage-reissue","keyboard-controller"),
            presetCount = 64,
            description = "8-voice polyphonic analog synth with twin oscillators per voice, powerful filters, and built-in sequencer."
        ))

        // ============================================================
        //  MOOG
        // ============================================================
        add("moog_minimoog", SynthInfo(
            id = "moog_minimoog", name = "Minimoog", brand = "Moog",
            year = 1970, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(3, listOf("saw","square","triangle"), hasNoise=true, driftCents=FloatRange(3f,15f)),
            filterProfile = FilterProfile("Moog Ladder LP", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(1, listOf("sine","S&H")),
            physicalProfile = PhysicalProfile("Keyboard", 44, hasMIDI=false, hasCV=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.moogmusic.com/products/minimoog",
                brandUrl = "https://www.moogmusic.com",
                priceUSD = 4500f, priceEUR = 4200f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(BuyLink("Reverb", "https://reverb.com/marketplace?query=minimoog", StoreType.USED_MARKET))
            ),
            tags = listOf("analog","monophonic","vintage","legendary","classic","70s","portable"),
            description = "The synthesizer that brought analog synthesis to the masses. Three oscillators and the iconic Moog ladder filter."
        ))

        add("moog_voyager", SynthInfo(
            id = "moog_voyager", name = "Voyager", brand = "Moog",
            year = 2002, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(3, listOf("saw","square","triangle"), hasNoise=true, hasPWM=true, hasFM=true, driftCents=FloatRange(2f,10f)),
            filterProfile = FilterProfile("Moog Ladder LP", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasVelocity=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=8, hasAftertouch=true),
            physicalProfile = PhysicalProfile("Keyboard", 44, hasMIDI=true, hasCV=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.moogmusic.com/products/voyager",
                brandUrl = "https://www.moogmusic.com",
                priceUSD = 3500f, priceEUR = 3200f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(BuyLink("Reverb", "https://reverb.com/marketplace?query=moog+voyager", StoreType.USED_MARKET))
            ),
            tags = listOf("analog","monophonic","minimoog-evolution","velocity","aftertouch"),
            description = "Modern reincarnation of the Minimoog with velocity/aftertouch, modulation matrix and MIDI."
        ))

        add("moog_one", SynthInfo(
            id = "moog_one", name = "One", brand = "Moog",
            year = 2018, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(3, listOf("saw","square","triangle","sine"), hasSubOscillator=true, hasNoise=true, hasFM=true, hasPWM=true, driftCents=FloatRange(0.5f,4f)),
            filterProfile = FilterProfile("Moog Ladder (x2)", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true, modes=listOf("LP","HP","BP","Notch"), count=2),
            envelopeProfile = EnvelopeProfile(3, hasDAHDSR=true, hasVelocity=true, hasLooping=true),
            modulationProfile = ModulationProfile(4, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=48, hasAftertouch=true, hasMPE=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 61, hasMIDI=true, hasUSB=true, hasCV=true, hasAudioIn=true, hasDisplay=true, displayType="OLED"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.moogmusic.com/products/one",
                brandUrl = "https://www.moogmusic.com",
                priceUSD = 5999f, priceEUR = 5599f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/MoogOne8", StoreType.DEALER))
            ),
            tags = listOf("analog","8-voice","polyphonic","flagship","triple-oscillator","triple-filter"),
            description = "Moog's first polyphonic analog synth in decades. 8 voices, triple oscillators and dual filters per voice."
        ))

        add("moog_sub37", SynthInfo(
            id = "moog_sub37", name = "Sub 37", brand = "Moog",
            year = 2015, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle","sine"), hasSubOscillator=true, hasNoise=true, hasPWM=true, hasFM=true, driftCents=FloatRange(0.5f,4f)),
            filterProfile = FilterProfile("Moog Ladder (x2)", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true, modes=listOf("LP","HP","BP","Notch"), count=2),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true, hasVelocity=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=8, hasAftertouch=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 37, hasMIDI=true, hasUSB=true, hasCV=true, hasDisplay=true, displayType="OLED"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.moogmusic.com/products/sub37",
                brandUrl = "https://www.moogmusic.com",
                priceUSD = 3499f, priceEUR = 3299f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/MoogSub37", StoreType.DEALER))
            ),
            tags = listOf("analog","8-voice","polyphonic","flagship","triple-oscillator","triple-filter"),
            description = "Moog's first polyphonic analog synth in decades. 8 voices, triple oscillators and dual filters per voice."
        ))

        add("moog_grandmother", SynthInfo(
            id = "moog_grandmother", name = "Grandmother", brand = "Moog",
            year = 2017, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 4,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle","sine"), hasSubOscillator=true, hasNoise=true, hasPWM=true, hasFM=true, driftCents=FloatRange(0.5f,4f)),
            filterProfile = FilterProfile("Moog Ladder (x2)", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true, modes=listOf("LP","HP","BP","Notch"), count=2),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true, hasVelocity=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=8, hasAftertouch=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true, hasCV=true, hasDisplay=true, displayType="OLED"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.moogmusic.com/products/grandmother",
                brandUrl = "https://www.moogmusic.com",
                priceUSD = 2499f, priceEUR = 2299f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/MoogGrandmother", StoreType.DEALER))
            ),
            tags = listOf("analog","4-voice","polyphonic","flagship","triple-oscillator","triple-filter"),
            description = "Moog's first polyphonic analog synth in decades. 8 voices, triple oscillators and dual filters per voice."
        ))

        add("moog_matriarch", SynthInfo(
            id = "moog_matriarch", name = "Matriarch", brand = "Moog",
            year = 2018, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(3, listOf("saw","square","triangle","sine"), hasSubOscillator=true, hasNoise=true, hasFM=true, hasPWM=true, driftCents=FloatRange(0.5f,4f)),
            filterProfile = FilterProfile("Moog Ladder (x2)", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true, modes=listOf("LP","HP","BP","Notch"), count=2),
            envelopeProfile = EnvelopeProfile(3, hasDAHDSR=true, hasVelocity=true, hasLooping=true),
            modulationProfile = ModulationProfile(4, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=48, hasAftertouch=true, hasMPE=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 61, hasMIDI=true, hasUSB=true, hasCV=true, hasAudioIn=true, hasDisplay=true, displayType="OLED"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.moogmusic.com/products/matriarch",
                brandUrl = "https://www.moogmusic.com",
                priceUSD = 5999f, priceEUR = 5599f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/MoogMatriarch", StoreType.DEALER))
            ),
            tags = listOf("analog","8-voice","polyphonic","flagship","triple-oscillator","triple-filter"),
            description = "Moog's first polyphonic analog synth in decades. 8 voices, triple oscillators and dual filters per voice."
        ))

        add("moog_model_d", SynthInfo(
            id = "moog_model_d", name = "Model D", brand = "Moog",
            year = 2019, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle","sine"), hasSubOscillator=true, hasNoise=true, hasPWM=true, hasFM=true, driftCents=FloatRange(0.5f,4f)),
            filterProfile = FilterProfile("Moog Ladder (x2)", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true, modes=listOf("LP","HP","BP","Notch"), count=2),
            envelopeProfile = EnvelopeProfile(3, hasDAHDSR=true, hasVelocity=true, hasLooping=true),
            modulationProfile = ModulationProfile(4, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=48, hasAftertouch=true, hasMPE=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 61, hasMIDI=true, hasUSB=true, hasCV=true, hasAudioIn=true, hasDisplay=true, displayType="OLED"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.moogmusic.com/products/modeld",
                brandUrl = "https://www.moogmusic.com",
                priceUSD = 5999f, priceEUR = 5599f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/MoogModelD", StoreType.DEALER))
            ),
            tags = listOf("analog","8-voice","polyphonic","flagship","triple-oscillator","triple-filter"),
            description = "Moog's first polyphonic analog synth in decades. 8 voices, triple oscillators and dual filters per voice."
        ))

        add("moog_muse", SynthInfo(
            id = "moog_muse", name = "Muse", brand = "Moog",
            year = 2023, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(3, listOf("saw","square","triangle"), hasSubOscillator=true, hasNoise=true, hasPWM=true, hasSync=true, hasFM=true, driftCents=FloatRange(0.5f,3f)),
            filterProfile = FilterProfile("Moog Ladder (x2)", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true, modes=listOf("LP","HP","BP","Notch"), count=2),
            envelopeProfile = EnvelopeProfile(3, hasDAHDSR=true, hasVelocity=true, hasLooping=true),
            modulationProfile = ModulationProfile(4, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=32, hasAftertouch=true, hasMPE=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 61, hasMIDI=true, hasUSB=true, hasCV=true, hasDisplay=true, displayType="OLED"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.moogmusic.com/products/muse",
                brandUrl = "https://www.moogmusic.com",
                priceUSD = 3499f, priceEUR = 3299f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/MoogMuse", StoreType.DEALER))
            ),
            tags = listOf("analog","8-voice","polyphonic","flagship","triple-oscillator","duophonic-capable"),
            description = "Moog's flagship polyphonic analog synth with three oscillators and dual filters per voice."
        ))

        add("moog_subharmonicon", SynthInfo(
            id = "moog_subharmonicon", name = "Subharmonicon", brand = "Moog",
            year = 2020, category = SynthCategory.SEMI_MODULAR,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","triangle"), hasSubOscillator=true, hasSync=true, hasFM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Moog Ladder (x2)", listOf(24), hasResonance=true, hasSelfOscillation=true, count=2),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(2, listOf("sine","S&H"), hasSequencer=true, sequencerSteps=16),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=true, hasUSB=true, hasCV=true, hasAudioIn=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.moogmusic.com/products/subharmonicon",
                brandUrl = "https://www.moogmusic.com",
                priceUSD = 699f, priceEUR = 649f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/MoogSubharmonicon", StoreType.DEALER))
            ),
            tags = listOf("analog","semi-modular","subharmonic","polyrhythmic","eurorack","moogerfoogers"),
            description = "Semi-modular analog synth exploring subharmonic synthesis with polyrhythmic sequencers."
        ))

        add("moog_mother_32", SynthInfo(
            id = "moog_mother_32", name = "Mother-32", brand = "Moog",
            year = 2015, category = SynthCategory.SEMI_MODULAR,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(1, listOf("saw","square"), hasSubOscillator=true, hasPWM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Moog Ladder LP", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(1, hasLooping=true),
            modulationProfile = ModulationProfile(1, listOf("S&H"), hasSequencer=true, sequencerSteps=32),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=true, hasUSB=true, hasCV=true, hasAudioIn=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.moogmusic.com/products/mother-32",
                brandUrl = "https://www.moogmusic.com",
                priceUSD = 599f, priceEUR = 549f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/MoogMother32", StoreType.DEALER))
            ),
            tags = listOf("analog","semi-modular","eurorack","moog","starter","patchable"),
            description = "First semi-modular Moog in decades. Eurorack-friendly with built-in sequencer and patch points."
        ))

        add("moog_dfam", SynthInfo(
            id = "moog_dfam", name = "DFAM", brand = "Moog",
            year = 2018, category = SynthCategory.SEMI_MODULAR,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(2, listOf("square","triangle"), hasNoise=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Moog Ladder LP", listOf(24), hasResonance=true, hasSelfOscillation=true),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(0, emptyList(), hasSequencer=true, sequencerSteps=8),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=true, hasUSB=true, hasCV=true, hasAudioIn=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.moogmusic.com/products/dfam",
                brandUrl = "https://www.moogmusic.com",
                priceUSD = 599f, priceEUR = 549f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/MoogDFAM", StoreType.DEALER))
            ),
            tags = listOf("analog","semi-modular","drum","rhythmic","eurorack","percussive"),
            description = "Drum From Another Mother — semi-modular analog drum/percussion synth with 8-step sequencer."
        ))

        // ============================================================
        //  ROLAND
        // ============================================================
        add("roland_jupiter_8", SynthInfo(
            id = "roland_jupiter_8", name = "JUPITER-8", brand = "Roland",
            year = 1981, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","pulse"), hasSubOscillator=true, hasNoise=true, hasPWM=true, hasSync=true, driftCents=FloatRange(1f,5f)),
            filterProfile = FilterProfile("Roland IR3109 LP", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=1, hasArpeggiator=true, hasPolyMod=true),
            physicalProfile = PhysicalProfile("Keyboard", 61, hasMIDI=false, hasCV=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.roland.com/global/categories/jupiter/",
                brandUrl = "https://www.roland.com",
                priceUSD = null, priceEUR = null, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=roland+jupiter-8", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=roland+jupiter+8", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("analog","8-voice","polyphonic","vintage","80s","legendary","classic"),
            presetCount = 64,
            description = "The holy grail of analog polysynths. Eight voices of fat Roland sound with split and layer capabilities."
        ))

        add("roland_juno_60", SynthInfo(
            id = "roland_juno_60", name = "JUNO-60", brand = "Roland",
            year = 1982, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 6,
            oscillatorProfile = OscillatorProfile(1, listOf("saw","square","pulse"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(1f,4f)),
            filterProfile = FilterProfile("Roland IR3109 LP", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(1, hasDAHDSR=true),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","S&H"), hasArpeggiator=true, hasChord=false),
            physicalProfile = PhysicalProfile("Keyboard", 61, hasMIDI=false, hasCV=true, hasDCB=false),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.roland.com/global/categories/juno/",
                brandUrl = "https://www.roland.com",
                priceUSD = 1500f, priceEUR = 1400f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(BuyLink("Reverb", "https://reverb.com/marketplace?query=roland+juno+60", StoreType.USED_MARKET))
            ),
            tags = listOf("analog","6-voice","polyphonic","vintage","chorus","classic","80s"),
            presetCount = 56,
            description = "Iconic 6-voice analog polysynth famous for its lush chorus and warm pads. A studio staple of the 80s."
        ))

        add("roland_juno_106", SynthInfo(
            id = "roland_juno_106", name = "JUNO-106", brand = "Roland",
            year = 1984, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 6,
            oscillatorProfile = OscillatorProfile(1, listOf("saw","pulse","square"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(0.5f,3f)),
            filterProfile = FilterProfile("Roland 80017A VCF/VCA", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(1, hasDAHDSR=true),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","S&H"), hasArpeggiator=false, hasChord=true),
            physicalProfile = PhysicalProfile("Keyboard", 61, hasMIDI=true, hasCV=false),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.roland.com/global/categories/juno/",
                brandUrl = "https://www.roland.com",
                priceUSD = 1800f, priceEUR = 1650f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(BuyLink("Reverb", "https://reverb.com/marketplace?query=roland+juno+106", StoreType.USED_MARKET))
            ),
            tags = listOf("analog","6-voice","polyphonic","vintage","chorus","classic","80s","midi"),
            presetCount = 128,
            description = "One of the most iconic analog polyphonic synthesizers in history. Famous for its on-board analog chorus and warm DCO sound."
        ))

        add("roland_tb_303", SynthInfo(
            id = "roland_tb_303", name = "TB-303 Bass Line", brand = "Roland",
            year = 1981, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(1, listOf("saw","square"), driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Roland Transistor Ladder LP", listOf(18, 24), hasResonance=true, hasSelfOscillation=true),
            envelopeProfile = EnvelopeProfile(1),
            modulationProfile = ModulationProfile(0, emptyList(), hasSequencer=true, sequencerSteps=16),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=false, hasCV=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.roland.com/global/categories/tb/",
                brandUrl = "https://www.roland.com",
                priceUSD = 2800f, priceEUR = 2600f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(BuyLink("Reverb", "https://reverb.com/marketplace?query=roland+tb-303", StoreType.USED_MARKET))
            ),
            tags = listOf("analog","monophonic","acid","bassline","legendary","vintage","80s"),
            presetCount = 0,
            description = "The definitive acid techno and house bassline synthesizer with its signature resonant squelch filter and pattern sequencer."
        ))

        add("roland_sh_101", SynthInfo(
            id = "roland_sh_101", name = "SH-101", brand = "Roland",
            year = 1982, category = SynthCategory.MONO_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(1, listOf("saw","square","pulse"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Roland IR3109 LP", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(1, hasDAHDSR=true),
            modulationProfile = ModulationProfile(1, listOf("sine","random","S&H"), hasSequencer=true, sequencerSteps=128),
            physicalProfile = PhysicalProfile("Keyboard", 32, hasMIDI=false, hasCV=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.roland.com/global/categories/sh/",
                brandUrl = "https://www.roland.com",
                priceUSD = 900f, priceEUR = 850f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(BuyLink("Reverb", "https://reverb.com/marketplace?query=roland+sh-101", StoreType.USED_MARKET))
            ),
            tags = listOf("analog","monophonic","vintage","acid","bass","classic","80s"),
            presetCount = 0,
            description = "Compact monophonic analog synth beloved for acid bass and lead sounds. Built-in sequencer and arpeggiator."
        ))

        add("roland_jd_800", SynthInfo(
            id = "roland_jd_800", name = "JD-800", brand = "Roland",
            year = 1991, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.DIGITAL_HARDWARE, polyphony = 24,
            oscillatorProfile = OscillatorProfile(4, listOf("saw","square","sine","pcm","noise"), hasPWM=true, wavetableSupport=true),
            filterProfile = FilterProfile("Analog-style LP", listOf(12, 24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(4, hasVelocity=true, hasLooping=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","S&H"), hasModMatrix=true, modMatrixSlots=8, hasAftertouch=true, hasArpeggiator=true),
            physicalProfile = PhysicalProfile("Keyboard", 76, hasMIDI=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.roland.com/global/categories/jd/",
                brandUrl = "https://www.roland.com",
                priceUSD = 1200f, priceEUR = 1100f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(BuyLink("Reverb", "https://reverb.com/marketplace?query=roland+jd-800", StoreType.USED_MARKET))
            ),
            tags = listOf("digital","24-voice","polyphonic","90s","workstation","pcm","sliders"),
            presetCount = 128,
            description = "Flagship digital synth with analog-style controls. Famous for its pads and the sound of 90s electronic music."
        ))

        add("roland_tr_8s", SynthInfo(
            id = "roland_tr_8s", name = "TR-8S", brand = "Roland",
            year = 2018, category = SynthCategory.DRUM_SYNTH,
            synthesisType = SynthType.HYBRID_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(0, emptyList(), hasNoise=true),
            filterProfile = FilterProfile("Digital Multi-mode", listOf(12, 24), hasResonance=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(0),
            modulationProfile = ModulationProfile(0, emptyList(), hasSequencer=true, sequencerSteps=32),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=true, hasUSB=true, hasAudioIn=true, hasDisplay=true, displayType="OLED"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.roland.com/global/products/tr-8s/",
                brandUrl = "https://www.roland.com",
                priceUSD = 699f, priceEUR = 649f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/RolandTR8S", StoreType.DEALER))
            ),
            tags = listOf("drum-machine","rhythm","hybrid","roland-cloud","acb","sequencer"),
            description = "Hybrid drum machine modeling classic Roland TR-808/909 with sample support and FM synthesis."
        ))

        add("roland_mc_707", SynthInfo(
            id = "roland_mc_707", name = "MC-707", brand = "Roland",
            year = 2019, category = SynthCategory.GROOVEBOX,
            synthesisType = SynthType.DIGITAL_HARDWARE, polyphony = 128,
            oscillatorProfile = OscillatorProfile(16, listOf("saw","square","sine","pcm","supersaw","fm"), hasNoise=true, hasPWM=true, hasFM=true, wavetableSupport=true),
            filterProfile = FilterProfile("Digital Multi-mode", listOf(12, 24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP","Notch")),
            envelopeProfile = EnvelopeProfile(8, hasDAHDSR=true, hasVelocity=true, hasLooping=true),
            modulationProfile = ModulationProfile(4, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=32, hasAftertouch=true, hasMPE=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=128),
            physicalProfile = PhysicalProfile("Keyboard", 0, hasMIDI=true, hasUSB=true, hasAudioIn=true, hasDisplay=true, displayType="OLED"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.roland.com/global/products/mc-707/",
                brandUrl = "https://www.roland.com",
                priceUSD = 999f, priceEUR = 949f, availability = Availability.AVAILABLE,
                buyLinks = listOf(BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/RolandMC707", StoreType.DEALER))
            ),
            tags = listOf("groovebox","zen-core","workstation","sequencer","sampler","all-in-one"),
            description = "Comprehensive groovebox with ZEN-Core engine, 16-track sequencer, sampler and effects."
        ))

        add("roland_jupiter_xm", SynthInfo(
            id = "roland_jupiter_xm", name = "JUPITER-Xm", brand = "Roland",
            year = 2019, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_MODELING, polyphony = 4,
            oscillatorProfile = OscillatorProfile(5, listOf("saw","square","sine","triangle","supersaw","noise","pcm","fm"), hasNoise=true, hasPWM=true, hasFM=true),
            filterProfile = FilterProfile("Digital Multi-mode", listOf(12, 18, 24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP","Notch","Formant","Comb")),
            envelopeProfile = EnvelopeProfile(4, hasDAHDSR=true, hasVelocity=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 37, hasMIDI=true, hasUSB=true, hasBluetooth=true, hasDisplay=true, displayType="OLED"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.roland.com/global/products/jupiter-xm/",
                brandUrl = "https://www.roland.com",
                priceUSD = 899f, priceEUR = 849f, availability = Availability.AVAILABLE,
                buyLinks = listOf(
                    BuyLink("Roland Official", "https://www.roland.com/global/products/jupiter-xm/", StoreType.OFFICIAL),
                    BuyLink("Sweetwater", "https://www.sweetwater.com/store/detail/RolandJupiterXM", StoreType.DEALER)
                )
            ),
            tags = listOf("modeling","4-voice","compact","bluetooth","all-roland","supersaw","zen-core"),
            presetCount = 4000, weight = "4.0 kg",
            description = "Compact Roland ZEN-Core synth engine that models Jupiter-8, JUNO-106, SH-101, JX-8P, and XV-5080 in one keyboard."
        ))

        // ============================================================
        //  YAMAHA
        // ============================================================
        add("yamaha_dx7", SynthInfo(
            id = "yamaha_dx7", name = "DX7", brand = "Yamaha",
            year = 1983, category = SynthCategory.FM_SYNTH,
            synthesisType = SynthType.DIGITAL_HARDWARE, polyphony = 16,
            oscillatorProfile = OscillatorProfile(6, listOf("sine"), hasFM=true),
            filterProfile = FilterProfile("None", listOf(), hasResonance=false),
            envelopeProfile = EnvelopeProfile(1),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","square","saw"), hasArpeggiator=true),
            physicalProfile = PhysicalProfile("Keyboard", 61, hasMIDI=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://usa.yamaha.com/products/music_production/synthesizers/dx7/index.html",
                brandUrl = "https://usa.yamaha.com",
                priceUSD = null, priceEUR = null, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=yamaha+dx7", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=yamaha+dx7", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("fm","16-voice","digital","classic","80s","legendary","electric-piano"),
            presetCount = 32,
            description = "The synthesizer that defined the 80s sound. 6-operator FM synthesis. Electric piano, bass, and bell sounds that shaped pop music."
        ))

        add("yamaha_cs80", SynthInfo(
            id = "yamaha_cs80", name = "CS-80", brand = "Yamaha",
            year = 1976, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","pulse"), hasSubOscillator=true, hasNoise=true, hasPWM=true, driftCents=FloatRange(1f,5f)),
            filterProfile = FilterProfile("Yamaha 2-pole", listOf(24), hasResonance=true, hasSelfOscillation=true, hasKeyTracking=true),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=8, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 61, hasMIDI=true, hasCV=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://usa.yamaha.com/products/music_production/synthesizers/cs80/index.html",
                brandUrl = "https://usa.yamaha.com",
                priceUSD = 8000f, priceEUR = 7500f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=yamaha+cs80", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=yamaha+cs80", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("analog","8-voice","polyphonic","vintage","legendary","large","heavy"),
            presetCount = 256,
            description = "The favorite of Vangelis, enormous, heavy (100 kg), but with pads and strings epic."
        ))

        add("yamaha_motif", SynthInfo(
            id = "yamaha_motif", name = "Motif", brand = "Yamaha",
            year = 2003, category = SynthCategory.WORKSTATION,
            synthesisType = SynthType.DIGITAL_HARDWARE, polyphony = 64,
            oscillatorProfile = OscillatorProfile(16, listOf("saw","square","sine","pcm","noise"), hasNoise=true, hasPWM=true, hasFM=true, wavetableSupport=true),
            filterProfile = FilterProfile("Digital", listOf(24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(4, hasDAHDSR=true, hasVelocity=true, hasLooping=true),
            modulationProfile = ModulationProfile(4, listOf("sine","triangle","square","S&H"), hasModMatrix=true, modMatrixSlots=32, hasAftertouch=true, hasMPE=true, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 61, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://usa.yamaha.com/products/music_production/workstations/motif/",
                brandUrl = "https://usa.yamaha.com",
                priceUSD = 2999f, priceEUR = 2799f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=yamaha+motif", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=yamaha+motif", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("digital","64-voice","workstation","sampling","sequencer","classic"),
            presetCount = 128,
            description = "The workstation that transformed the industry. One of the most sold synths in history with sampling, sequencing, and extensive effects."
        ))

        // ============================================================
        //  WALDORF
        // ============================================================
        add("waldorf_blofeld", SynthInfo(
            id = "waldorf_blofeld", name = "Blofeld", brand = "Waldorf",
            year = 2007, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.DIGITAL_HARDWARE, polyphony = 8,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","sine","triangle","supersaw","noise","pcm","fm"), hasNoise=true, hasPWM=true, hasFM=true, wavetableSupport=true),
            filterProfile = FilterProfile("Waldorf 2-pole", listOf(24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=8, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.waldorf.de/products/blofeld/",
                brandUrl = "https://www.waldorf.de",
                priceUSD = 1299f, priceEUR = 1249f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=waldorf+blofeld", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=waldorf+blofeld", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("digital","8-voice","polyphonic","wavetable","sequencer","classic"),
            presetCount = 128,
            description = "Digital polyphonic synth with wavetable synthesis, 8 voices, and built-in sequencer."
        ))

        add("waldorf_quantum", SynthInfo(
            id = "waldorf_quantum", name = "Quantum", brand = "Waldorf",
            year = 2008, category = SynthCategory.POLY_SYNTH,
            synthesisType = SynthType.DIGITAL_HARDWARE, polyphony = 16,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square","sine","triangle","supersaw","noise","pcm","fm"), hasNoise=true, hasPWM=true, hasFM=true, wavetableSupport=true),
            filterProfile = FilterProfile("Waldorf 2-pole", listOf(24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(2, hasDAHDSR=true),
            modulationProfile = ModulationProfile(2, listOf("sine","triangle","square","saw","random","S&H"), hasModMatrix=true, modMatrixSlots=8, hasArpeggiator=true, hasSequencer=true, sequencerSteps=64),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.waldorf.de/products/quantum/",
                brandUrl = "https://www.waldorf.de",
                priceUSD = 1799f, priceEUR = 1749f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=waldorf+quantum", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=waldorf+quantum", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("digital","16-voice","polyphonic","wavetable","sequencer","classic"),
            presetCount = 128,
            description = "Digital polyphonic synth with wavetable synthesis, 16 voices, and built-in sequencer."
        ))

        // ============================================================
        //  FAIRLIGHT
        // ============================================================
        add("fairlight_cmi", SynthInfo(
            id = "fairlight_cmi", name = "CMI", brand = "Fairlight",
            year = 1979, category = SynthCategory.WORKSTATION,
            synthesisType = SynthType.DIGITAL_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(1, listOf("sine"), hasFM=true),
            filterProfile = FilterProfile("Digital", listOf(24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(1),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","square","saw"), hasArpeggiator=true),
            physicalProfile = PhysicalProfile("Keyboard", 49, hasMIDI=true, hasUSB=true, hasDisplay=true, displayType="LCD"),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.fairlight.com/cmi/",
                brandUrl = "https://www.fairlight.com",
                priceUSD = 20000f, priceEUR = 18000f, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=fairlight+cmi", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=fairlight+cmi", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("digital","sampling","workstation","legendary","classic"),
            presetCount = 128,
            description = "Introduced the sampling."
        ))

        // ============================================================
        //  BUCHLA
        // ============================================================
        add("buchla_music_easel", SynthInfo(
            id = "buchla_music_easel", name = "Music Easel", brand = "Buchla",
            year = 1974, category = SynthCategory.SEMI_MODULAR,
            synthesisType = SynthType.ANALOG_HARDWARE, polyphony = 1,
            oscillatorProfile = OscillatorProfile(2, listOf("saw","square"), hasNoise=true, driftCents=FloatRange(2f,8f)),
            filterProfile = FilterProfile("Buchla 2-pole", listOf(24), hasResonance=true, hasSelfOscillation=true, modes=listOf("LP","HP","BP")),
            envelopeProfile = EnvelopeProfile(2),
            modulationProfile = ModulationProfile(1, listOf("sine","triangle","S&H"), hasSequencer=true, sequencerSteps=16),
            physicalProfile = PhysicalProfile("Desktop Module", 0, hasMIDI=true, hasCV=true, hasAudioIn=true),
            purchaseInfo = PurchaseInfo(
                officialUrl = "https://www.buchla.com/products/music-easel",
                brandUrl = "https://www.buchla.com",
                priceUSD = null, priceEUR = null, availability = Availability.USED_ONLY,
                isDiscontinued = true,
                buyLinks = listOf(
                    BuyLink("Reverb", "https://reverb.com/marketplace?query=buchla+music+easel", StoreType.USED_MARKET),
                    BuyLink("eBay", "https://www.ebay.com/sch/i.html?_nkw=buchla+music+easel", StoreType.USED_MARKET)
                )
            ),
            tags = listOf("analog","semi-modular","vintage","70s","legendary","patchable","touchplate"),
            presetCount = 64,
            description = "Uno de los primeros sintetizadores modulares, famoso por su diseño único y sonido distintivo."
        ))

        // ============================================================
        //  VERIFICACIÓN
        // ============================================================
        // Verificar que todos los elementos sean consistentes
        verifyCatalog()
    }

    private fun add(id: String, info: SynthInfo) {
        catalog[id] = info
    }

    private fun verifyCatalog() {
        // Asegurar que todos los IDs coincidan
        for ((key, value) in catalog) {
            assert(key == value.id) { "ID mismatch: key=$key, info.id=${value.id}" }
        }
    }

    // ================================================================
    //  ÍNDICES DE BÚSQUEDA Y RESOLUCIÓN CANÓNICA
    // ================================================================

    private val aliases = mapOf(
        "moog_model_d" to "moog_model_d",
        "moog_minimoog" to "moog_minimoog",
        "moog_sub37" to "moog_sub37",
        "moog_grandmother" to "moog_grandmother",
        "moog_matriarch" to "moog_matriarch",
        "moog_subsequent25" to "moog_sub37",
        "moog_one" to "moog_muse",
        "korg_ms20" to "korg_ms20",
        "korg_minilogue_xd" to "korg_minilogue_xd",
        "korg_monologue" to "korg_monologue",
        "korg_prologue" to "korg_prologue",
        "korg_opsix" to "korg_opsix",
        "korg_wavestation" to "korg_wavestate",
        "korg_wavestate" to "korg_wavestate",
        "roland_juno106" to "roland_juno_106",
        "roland_juno_106" to "roland_juno_106",
        "roland_juno60" to "roland_juno_60",
        "roland_juno_60" to "roland_juno_60",
        "roland_tb303" to "roland_tb_303",
        "roland_tb_303" to "roland_tb_303",
        "roland_sh101" to "roland_sh_101",
        "roland_sh_101" to "roland_sh_101",
        "roland_jupiter8" to "roland_jupiter_8",
        "roland_jupiter_8" to "roland_jupiter_8",
        "roland_system8" to "roland_jupiter_xm",
        "sequential_prophet5" to "sequential_prophet5",
        "sequential_prophet6" to "sequential_prophet6",
        "sequential_pro3" to "sequential_pro_3",
        "sequential_obx8" to "sequential_ob6",
        "novation_peak" to "novation_peak",
        "novation_summit" to "novation_summit",
        "novation_bassstation2" to "novation_bass_station_2",
        "arturia_matrixbrute" to "arturia_matrixbrute",
        "arturia_microfreak" to "arturia_microfreak",
        "arturia_minibrute2s" to "arturia_minibrute2s",
        "arturia_polybrute" to "arturia_polybrute",
        "behringer_model_d" to "behringer_model_d",
        "behringer_deepmind12" to "behringer_deepmind12",
        "behringer_td3" to "behringer_td3",
        "behringer_poly_d" to "behringer_poly_d",
        "behringer_pro_1" to "behringer_pro_1",
        "behringer_neutron" to "behringer_neutron",
        "behringer_crave" to "behringer_crave",
        "behringer_rd8" to "behringer_rd8",
        "behringer_ubxa" to "behringer_ubxa",
        "yamaha_dx7" to "yamaha_dx7",
        "yamaha_montage" to "yamaha_montage",
        "waldorf_iriidium" to "waldorf_iridium",
        "waldorf_blofeld" to "waldorf_blofeld",
        "elektron_digitakt" to "elektron_digitakt",
        "elektron_digitone" to "elektron_digitone",
        "elektron_analog_four" to "elektron_analog_four",
        "elektron_analogfour" to "elektron_analog_four",
        "teenage_op1_field" to "teenage_engineering_op1",
        "teenage_op1" to "teenage_engineering_op1"
    )

    fun findMatch(query: String, brandHint: String? = null): SynthInfo? {
        val cleanQuery = query.lowercase().trim()
        val normalizedId = cleanQuery.replace(" ", "_").replace("-", "_")

        // 1. Direct ID match
        catalog[normalizedId]?.let { return it }

        // 2. Alias match
        aliases[normalizedId]?.let { aliasId ->
            catalog[aliasId]?.let { return it }
        }

        // 3. Normalized alias matching
        aliases.entries.firstOrNull { it.key in normalizedId || normalizedId in it.key }?.let {
            catalog[it.value]?.let { return it }
        }

        // 4. Token-based scoring
        val tokens = cleanQuery.split(" ", "_", "-").filter { it.length > 1 }
        var bestScore = 0
        var bestMatch: SynthInfo? = null

        for (synth in catalog.values) {
            var score = 0
            val synthName = synth.name.lowercase()
            val synthBrand = synth.brand.lowercase()
            val synthId = synth.id.lowercase()

            if (brandHint != null && synthBrand.contains(brandHint.lowercase())) {
                score += 15
            }

            for (token in tokens) {
                if (synthName.contains(token)) score += 25
                if (synthBrand.contains(token)) score += 15
                if (synthId.contains(token)) score += 20
                if (synth.tags.any { it.contains(token) }) score += 10
            }

            if (score > bestScore) {
                bestScore = score
                bestMatch = synth
            }
        }

        return if (bestScore >= 20) bestMatch else searchByName(query).firstOrNull()
    }

    fun searchByName(query: String): List<SynthInfo> {
        val q = query.lowercase()
        return catalog.values.filter {
            it.name.lowercase().contains(q) ||
                    it.brand.lowercase().contains(q) ||
                    it.tags.any { tag -> tag.contains(q) }
        }.sortedByDescending { it.year }
    }

    fun getByBrand(brand: String): List<SynthInfo> {
        return catalog.values.filter { it.brand.equals(brand, ignoreCase = true) }
            .sortedByDescending { it.year }
    }

    fun getByCategory(cat: SynthCategory): List<SynthInfo> {
        return catalog.values.filter { it.category == cat }
    }

    fun getByYearRange(startYear: Int, endYear: Int): List<SynthInfo> {
        return catalog.values.filter { it.year != null && it.year in startYear..endYear }
    }

    fun getByPriceRange(maxUSD: Float): List<SynthInfo> {
        return catalog.values.filter {
            it.purchaseInfo.priceUSD != null && it.purchaseInfo.priceUSD <= maxUSD
        }.sortedBy { it.purchaseInfo.priceUSD }
    }

    fun getAvailable(): List<SynthInfo> {
        return catalog.values.filter { it.purchaseInfo.availability == Availability.AVAILABLE }
    }

    fun getDiscontinued(): List<SynthInfo> {
        return catalog.values.filter { it.purchaseInfo.isDiscontinued }
    }

    fun getAllBrands(): List<String> {
        return catalog.values.map { it.brand }.distinct().sorted()
    }

    fun getAllTags(): List<String> {
        return catalog.values.flatMap { it.tags }.distinct().sorted()
    }

    fun getSynthById(id: String): SynthInfo? {
        return catalog[id]
    }

    fun getPurchaseLinks(id: String): List<BuyLink> {
        return catalog[id]?.purchaseInfo?.buyLinks ?: emptyList()
    }

    fun getAllWithOfficialLinks(): List<SynthInfo> {
        return catalog.values.filter { it.purchaseInfo.buyLinks.isNotEmpty() }
    }

    fun totalCount(): Int = catalog.size

    fun brandCount(): Int = catalog.values.map { it.brand }.distinct().size
}
