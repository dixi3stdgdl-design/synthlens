package com.synthlens.app.data

object SynthDatabaseSeeder {
    fun getAllSynths(): List<SynthLibraryItem> = listOf(
        SynthLibraryItem(
            name = "Matriarch",
            brand = "Moog",
            category = "Semi-Modular Analog",
            yearReleased = 2019,
            description = "4-Voice Paraphonic Analog - In Production",
            priceRange = "2199 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Grandmother",
            brand = "Moog",
            category = "Semi-Modular Analog",
            yearReleased = 2018,
            description = "Monophonic Analog - In Production",
            priceRange = "1149 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Subsequent 37",
            brand = "Moog",
            category = "Paraphonic Analog",
            yearReleased = 2017,
            description = "2-Note Paraphonic Analog - In Production",
            priceRange = "1899 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "DFAM (Drummer From Another Mother)",
            brand = "Moog",
            category = "Semi-Modular Drum Machine",
            yearReleased = 2018,
            description = "Analog Percussion - In Production",
            priceRange = "699 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Mother-32",
            brand = "Moog",
            category = "Semi-Modular Analog",
            yearReleased = 2015,
            description = "Monophonic Analog - In Production",
            priceRange = "599 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Subharmonicon",
            brand = "Moog",
            category = "Semi-Modular Polyrhythmic Analog",
            yearReleased = 2020,
            description = "Subharmonic Analog - In Production",
            priceRange = "699 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Muse",
            brand = "Moog",
            category = "Polyphonic Analog",
            yearReleased = 2024,
            description = "8-Voice Polyphonic Analog - In Production",
            priceRange = "3499 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Spectravox",
            brand = "Moog",
            category = "Semi-Modular Spectral Processor",
            yearReleased = 2024,
            description = "Analog Filter Bank Synthesizer - In Production",
            priceRange = "599 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Prophet-6",
            brand = "Sequential",
            category = "Polyphonic Analog",
            yearReleased = 2015,
            description = "6-Voice Discrete VCO Analog - In Production",
            priceRange = "2999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "OB-6",
            brand = "Sequential",
            category = "Polyphonic Analog",
            yearReleased = 2016,
            description = "6-Voice Discrete VCO Analog - In Production",
            priceRange = "3199 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Take 5",
            brand = "Sequential",
            category = "Polyphonic Analog",
            yearReleased = 2021,
            description = "5-Voice Polyphonic Analog - In Production",
            priceRange = "1499 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Prophet-5 (Rev 4)",
            brand = "Sequential",
            category = "Polyphonic Analog",
            yearReleased = 2020,
            description = "5-Voice Polyphonic Analog (SSM/Curtis) - In Production",
            priceRange = "3999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Prophet-10 (Rev 4)",
            brand = "Sequential",
            category = "Polyphonic Analog",
            yearReleased = 2020,
            description = "10-Voice Polyphonic Analog - In Production",
            priceRange = "4599 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Prophet Rev2 (16-Voice)",
            brand = "Sequential",
            category = "Polyphonic Analog",
            yearReleased = 2017,
            description = "16-Voice DCO Analog - In Production",
            priceRange = "2499 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Pro 3",
            brand = "Sequential",
            category = "Hybrid Synthesizer",
            yearReleased = 2020,
            description = "2 VCOs + 1 Wavetable Digital - In Production",
            priceRange = "1899 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Trigon-6",
            brand = "Sequential",
            category = "Polyphonic Analog",
            yearReleased = 2022,
            description = "6-Voice 3-VCO Ladder Filter Analog - In Production",
            priceRange = "3499 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "OB-X8",
            brand = "Oberheim",
            category = "Polyphonic Analog",
            yearReleased = 2022,
            description = "8-Voice Multi-Filter Analog - In Production",
            priceRange = "4999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "OB-X8 Desktop",
            brand = "Oberheim",
            category = "Polyphonic Analog Desktop",
            yearReleased = 2023,
            description = "8-Voice Multi-Filter Analog - In Production",
            priceRange = "3999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "TEO-5",
            brand = "Oberheim",
            category = "Polyphonic Analog",
            yearReleased = 2024,
            description = "5-Voice SEM-based Polyphonic Analog - In Production",
            priceRange = "1499 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "PolyBrute 12",
            brand = "Arturia",
            category = "Polyphonic Analog Morphing",
            yearReleased = 2024,
            description = "12-Voice Expressive Analog - In Production",
            priceRange = "3999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "PolyBrute 6",
            brand = "Arturia",
            category = "Polyphonic Analog Morphing",
            yearReleased = 2020,
            description = "6-Voice Dual-Filter Analog - In Production",
            priceRange = "2699 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "MatrixBrute",
            brand = "Arturia",
            category = "Paraphonic Analog Matrix",
            yearReleased = 2016,
            description = "3-VCO Multi-Filter Analog - In Production",
            priceRange = "1999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "MiniFreak",
            brand = "Arturia",
            category = "Polyphonic Hybrid Synthesizer",
            yearReleased = 2022,
            description = "6-Voice Digital Algorithmic / Analog Filter - In Production",
            priceRange = "599 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "MicroFreak",
            brand = "Arturia",
            category = "Algorithmic Hybrid Synthesizer",
            yearReleased = 2019,
            description = "Digital Multi-Engine / Analog Filter - In Production",
            priceRange = "349 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "DrumBrute Impact",
            brand = "Arturia",
            category = "Analog Drum Machine",
            yearReleased = 2018,
            description = "10-Voice Analog Drum - In Production",
            priceRange = "299 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "MiniBrute 2",
            brand = "Arturia",
            category = "Semi-Modular Analog",
            yearReleased = 2018,
            description = "Monophonic Analog Patchable - In Production",
            priceRange = "549 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "MiniBrute 2S",
            brand = "Arturia",
            category = "Semi-Modular Sequencer Analog",
            yearReleased = 2018,
            description = "Monophonic Analog Patchable - In Production",
            priceRange = "549 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Astrolab",
            brand = "Arturia",
            category = "Stage Synthesizer Workstation",
            yearReleased = 2024,
            description = "Multi-Engine Software Architecture Hardware - In Production",
            priceRange = "1999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Digitakt II",
            brand = "Elektron",
            category = "Digital Drum Machine & Sampler",
            yearReleased = 2024,
            description = "16-Track Stereo Digital Sampler - In Production",
            priceRange = "999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Digitone II",
            brand = "Elektron",
            category = "Polyphonic FM Synthesizer",
            yearReleased = 2024,
            description = "16-Voice Multi-Engine FM/Wavetable - In Production",
            priceRange = "999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Syntakt",
            brand = "Elektron",
            category = "Hybrid Drum Machine & Synthesizer",
            yearReleased = 2022,
            description = "12-Track Analog/Digital Drum & Synth - In Production",
            priceRange = "999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Analog Rytm MKII",
            brand = "Elektron",
            category = "Hybrid Drum Machine",
            yearReleased = 2017,
            description = "8-Voice Dual-Engine Analog Drum + Sampler - In Production",
            priceRange = "1899 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Analog Four MKII",
            brand = "Elektron",
            category = "Polyphonic Analog",
            yearReleased = 2017,
            description = "4-Voice Discrete Analog Synthesizer - In Production",
            priceRange = "1649 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Octatrack MKII",
            brand = "Elektron",
            category = "Dynamic Performance Sampler",
            yearReleased = 2017,
            description = "8-Track Real-Time Sampler/Sequencer - In Production",
            priceRange = "1599 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Model:Cycles",
            brand = "Elektron",
            category = "FM Groovebox",
            yearReleased = 2020,
            description = "6-Track FM Percussion & Melodic - In Production",
            priceRange = "399 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Model:Samples",
            brand = "Elektron",
            category = "Sample Groovebox",
            yearReleased = 2019,
            description = "6-Track Digital Sample Player - In Production",
            priceRange = "399 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Minilogue XD",
            brand = "Korg",
            category = "Hybrid Synthesizer",
            yearReleased = 2019,
            description = "4-Voice 2-VCO + Digital Multi-Engine - In Production",
            priceRange = "649 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Minilogue",
            brand = "Korg",
            category = "Polyphonic Analog",
            yearReleased = 2016,
            description = "4-Voice True Analog - In Production",
            priceRange = "529 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Monologue",
            brand = "Korg",
            category = "Monophonic Analog",
            yearReleased = 2016,
            description = "Monophonic Analog with Drive - In Production",
            priceRange = "329 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "microKORG 2",
            brand = "Korg",
            category = "Virtual Analog Modeling",
            yearReleased = 2024,
            description = "Digital Modeling + Vocal Processor - In Production",
            priceRange = "549 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "microKORG",
            brand = "Korg",
            category = "Virtual Analog",
            yearReleased = 2002,
            description = "4-Voice DSP Modeling - In Production",
            priceRange = "429 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Opsix MKII",
            brand = "Korg",
            category = "Altered FM Synthesizer",
            yearReleased = 2024,
            description = "64-Voice 6-Op Altered FM - In Production",
            priceRange = "799 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Wavestate MKII",
            brand = "Korg",
            category = "Wave Sequencing Digital",
            yearReleased = 2023,
            description = "96-Voice Wave Sequencing 2.0 - In Production",
            priceRange = "799 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Modwave MKII",
            brand = "Korg",
            category = "Wavetable Synthesizer",
            yearReleased = 2023,
            description = "60-Voice Wavetable / Kaoss Physics - In Production",
            priceRange = "799 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "multi/poly",
            brand = "Korg",
            category = "Virtual Analog Modeling",
            yearReleased = 2024,
            description = "60-Voice Analog Modeling (Mono/Poly Architecture) - In Production",
            priceRange = "899 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "MS-20 Mini",
            brand = "Korg",
            category = "Semi-Modular Analog",
            yearReleased = 2013,
            description = "Duophonic Patchable Analog - In Production",
            priceRange = "599 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Volca Drum",
            brand = "Korg",
            category = "Digital Percussion Synthesizer",
            yearReleased = 2019,
            description = "6-Part DSP Resonator Drum - In Production",
            priceRange = "169 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Volca Bass",
            brand = "Korg",
            category = "Analog Bass Synthesizer",
            yearReleased = 2013,
            description = "3-VCO Analog Filter - In Production",
            priceRange = "169 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Volca Keys",
            brand = "Korg",
            category = "Analog Loop Synthesizer",
            yearReleased = 2013,
            description = "3-Voice Polyphonic Analog - In Production",
            priceRange = "169 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Volca FM 2",
            brand = "Korg",
            category = "FM Synthesizer",
            yearReleased = 2022,
            description = "6-Voice 6-Op FM - In Production",
            priceRange = "199 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "TR-8S Rhythm Performer",
            brand = "Roland",
            category = "Digital Drum Machine",
            yearReleased = 2018,
            description = "ACB Modeling + Sample Playback - In Production",
            priceRange = "749 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "TR-6S Rhythm Performer",
            brand = "Roland",
            category = "Compact Drum Machine",
            yearReleased = 2020,
            description = "6-Track ACB / FM / Sample - In Production",
            priceRange = "399 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Jupiter-X",
            brand = "Roland",
            category = "Digital Performance Synthesizer",
            yearReleased = 2019,
            description = "ZEN-Core Synthesizer System - In Production",
            priceRange = "2699 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Jupiter-Xm",
            brand = "Roland",
            category = "Compact Digital Synthesizer",
            yearReleased = 2019,
            description = "ZEN-Core Synthesizer System - In Production",
            priceRange = "1499 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "FANTOM-08",
            brand = "Roland",
            category = "Synthesizer Workstation",
            yearReleased = 2022,
            description = "ZEN-Core + SuperNATURAL - In Production",
            priceRange = "1999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "SH-4d",
            brand = "Roland",
            category = "Desktop Synthesizer & Drum Machine",
            yearReleased = 2023,
            description = "11 Multi-Oscillator Models - In Production",
            priceRange = "649 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "S-1 Tweak Synth",
            brand = "Roland",
            category = "Pocket Synthesizer (Aira Compact)",
            yearReleased = 2023,
            description = "4-Voice ACB Modeling (SH-101 based) - In Production",
            priceRange = "199 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "T-8 Beat Machine",
            brand = "Roland",
            category = "Pocket Drum & Bass Machine",
            yearReleased = 2022,
            description = "ACB Modeling (TR-808/909 + TB-303) - In Production",
            priceRange = "199 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "AIRA Compact P-6",
            brand = "Roland",
            category = "Creative Sampler & Synthesizer",
            yearReleased = 2024,
            description = "Granular & Micro-Sample DSP - In Production",
            priceRange = "219 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Hydrasynth Keyboard",
            brand = "ASM",
            category = "Wave Morphing Synthesizer",
            yearReleased = 2019,
            description = "8-Voice Polyphonic WaveScan Digital - In Production",
            priceRange = "1299 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Hydrasynth Desktop",
            brand = "ASM",
            category = "Wave Morphing Synthesizer Desktop",
            yearReleased = 2019,
            description = "8-Voice Polyphonic WaveScan Digital - In Production",
            priceRange = "799 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Hydrasynth Deluxe",
            brand = "ASM",
            category = "Wave Morphing Synthesizer",
            yearReleased = 2021,
            description = "16-Voice Dual-Engine WaveScan Digital - In Production",
            priceRange = "1799 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Hydrasynth Explorer",
            brand = "ASM",
            category = "Compact Wave Morphing Synthesizer",
            yearReleased = 2021,
            description = "8-Voice Polyphonic WaveScan Digital - In Production",
            priceRange = "599 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Iridium Keyboard",
            brand = "Waldorf",
            category = "Polyphonic Digital Synthesizer",
            yearReleased = 2022,
            description = "16-Voice 5-Engine Digital Synthesizer - In Production",
            priceRange = "3299 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Iridium Desktop",
            brand = "Waldorf",
            category = "Polyphonic Digital Synthesizer Desktop",
            yearReleased = 2020,
            description = "16-Voice 5-Engine Digital Synthesizer - In Production",
            priceRange = "2699 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Quantum MK2",
            brand = "Waldorf",
            category = "Hybrid Synthesizer",
            yearReleased = 2023,
            description = "16-Voice Analog Filter / Multi-Engine Digital - In Production",
            priceRange = "4999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "M",
            brand = "Waldorf",
            category = "Classic Wavetable Synthesizer",
            yearReleased = 2021,
            description = "8-Voice Classic PPG/Microwave Wavetable + Analog Filter - In Production",
            priceRange = "1899 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Blofeld Keyboard",
            brand = "Waldorf",
            category = "Virtual Analog / Wavetable",
            yearReleased = 2009,
            description = "25-Voice Virtual Analog / Wavetable - In Production",
            priceRange = "899 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Blofeld Desktop",
            brand = "Waldorf",
            category = "Virtual Analog / Wavetable",
            yearReleased = 2007,
            description = "25-Voice Virtual Analog / Wavetable - In Production",
            priceRange = "549 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Nord Wave 2",
            brand = "Clavia Nord",
            category = "Performance Synthesizer",
            yearReleased = 2020,
            description = "48-Voice Sample / VA / FM / Wavetable - In Production",
            priceRange = "2899 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Nord Lead A1",
            brand = "Clavia Nord",
            category = "Virtual Analog Modeling",
            yearReleased = 2014,
            description = "26-Voice Virtual Analog - In Production",
            priceRange = "1400 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Nord Drum 3P",
            brand = "Clavia Nord",
            category = "Percussion Synthesizer",
            yearReleased = 2016,
            description = "6-Channel Modeling Drum Synthesizer - In Production",
            priceRange = "899 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Nord Stage 4",
            brand = "Clavia Nord",
            category = "Stage Keyboard / Synthesizer",
            yearReleased = 2023,
            description = "Wave 2 Engine + Piano + Organ - In Production",
            priceRange = "5699 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Typhon",
            brand = "Dreadbox",
            category = "Monophonic Analog",
            yearReleased = 2020,
            description = "Monophonic Analog with Sinevibes DSP - In Production",
            priceRange = "399 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "Nymphes",
            brand = "Dreadbox",
            category = "Polyphonic Analog",
            yearReleased = 2021,
            description = "6-Voice All-Analog Discrete Voice - In Production",
            priceRange = "549 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "Erebus Reissue",
            brand = "Dreadbox",
            category = "Semi-Modular Analog",
            yearReleased = 2023,
            description = "Duophonic Analog Patchable - In Production",
            priceRange = "499 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "Hades Reissue",
            brand = "Dreadbox",
            category = "Semi-Modular Analog Bass",
            yearReleased = 2023,
            description = "Monophonic Analog Bass - In Production",
            priceRange = "399 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "Telepathy",
            brand = "Dreadbox",
            category = "Modular Analog Voice Module",
            yearReleased = 2023,
            description = "Full Voice Eurorack Analog Voice - In Production",
            priceRange = "299 USD",
            purchaseUrl = "Global Retailers (Perfect Circuit, Thomann)"
        ),
        SynthLibraryItem(
            name = "Artemis",
            brand = "Dreadbox",
            category = "Polyphonic Analog Desktop",
            yearReleased = 2024,
            description = "6-Voice Discrete Polyphonic Analog - In Production",
            priceRange = "1199 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "DeepMind 12",
            brand = "Behringer",
            category = "Polyphonic Analog",
            yearReleased = 2016,
            description = "12-Voice Discrete Analog DCO - In Production",
            priceRange = "899 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Model D",
            brand = "Behringer",
            category = "Semi-Modular Monophonic Analog",
            yearReleased = 2018,
            description = "3-VCO Ladder Filter Analog - In Production",
            priceRange = "299 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Neutron",
            brand = "Behringer",
            category = "Semi-Modular Paraphonic Analog",
            yearReleased = 2018,
            description = "2-VCO Dual-Filter Analog - In Production",
            priceRange = "329 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Crave",
            brand = "Behringer",
            category = "Semi-Modular Monophonic Analog",
            yearReleased = 2019,
            description = "1-VCO 3340 Oscillator Analog - In Production",
            priceRange = "219 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "TD-3",
            brand = "Behringer",
            category = "Monophonic Analog Bass Line",
            yearReleased = 2019,
            description = "Analog Transistor Ladder Bass - In Production",
            priceRange = "149 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "RD-8 MKII",
            brand = "Behringer",
            category = "Analog Drum Machine",
            yearReleased = 2021,
            description = "Analog Drum Voice (808 recreation) - In Production",
            priceRange = "349 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "RD-9",
            brand = "Behringer",
            category = "Analog Drum Machine",
            yearReleased = 2021,
            description = "Analog Drum Voice + Sample HiHats (909 recreation) - In Production",
            priceRange = "369 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "2600",
            brand = "Behringer",
            category = "Semi-Modular Analog",
            yearReleased = 2020,
            description = "3-VCO Multi-Filter Analog - In Production",
            priceRange = "599 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "UB-Xa",
            brand = "Behringer",
            category = "Polyphonic Analog",
            yearReleased = 2023,
            description = "16-Voice Dual-Engine Analog (OB-Xa recreation) - In Production",
            priceRange = "1199 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Pro-800",
            brand = "Behringer",
            category = "Polyphonic Analog",
            yearReleased = 2023,
            description = "8-Voice Dual-VCO Analog (Prophet 600 recreation) - In Production",
            priceRange = "399 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Edge",
            brand = "Behringer",
            category = "Semi-Modular Percussion Synthesizer",
            yearReleased = 2023,
            description = "Dual-Oscillator Analog Percussion - In Production",
            priceRange = "229 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Poly D",
            brand = "Behringer",
            category = "Paraphonic Analog",
            yearReleased = 2019,
            description = "4-VCO Analog Synthesizer - In Production",
            priceRange = "699 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "OP-1 Field",
            brand = "Teenage Engineering",
            category = "Portable Digital Synthesizer",
            yearReleased = 2022,
            description = "Multi-Engine 32-Bit DSP Synthesizer & 4-Track - In Production",
            priceRange = "1999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Direct)"
        ),
        SynthLibraryItem(
            name = "OP-XY",
            brand = "Teenage Engineering",
            category = "Advanced Digital Synthesizer & Sequencer",
            yearReleased = 2024,
            description = "Multi-Core Digital Synthesis & Dynamic Step Sequencing - In Production",
            priceRange = "2299 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Direct)"
        ),
        SynthLibraryItem(
            name = "EP-133 K.O. II",
            brand = "Teenage Engineering",
            category = "Sampler & Groovebox",
            yearReleased = 2023,
            description = "Stereo 64MB Digital Sampler & Sequencer - In Production",
            priceRange = "299 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Direct)"
        ),
        SynthLibraryItem(
            name = "EP-1320 Medieval",
            brand = "Teenage Engineering",
            category = "Thematic Electronic Instrument",
            yearReleased = 2024,
            description = "Medieval Sample Engine & Synthesizer - In Production",
            priceRange = "299 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Direct)"
        ),
        SynthLibraryItem(
            name = "PO-33 K.O.!",
            brand = "Teenage Engineering",
            category = "Pocket Sampler & Drum Synthesizer",
            yearReleased = 2018,
            description = "Micro Digital Sampler - In Production",
            priceRange = "99 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Direct)"
        ),
        SynthLibraryItem(
            name = "0-COAST",
            brand = "Make Noise",
            category = "Semi-Modular Patchable Synthesizer",
            yearReleased = 2016,
            description = "West Coast Analog Wavefolding / Slope - In Production",
            priceRange = "499 USD",
            purchaseUrl = "Global Retailers (Perfect Circuit, Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "0-CTRL",
            brand = "Make Noise",
            category = "Voltage-Controlled Sequencer",
            yearReleased = 2020,
            description = "Touch-Plate Analog Controller - In Production",
            priceRange = "399 USD",
            purchaseUrl = "Global Retailers (Perfect Circuit, Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Strega",
            brand = "Make Noise",
            category = "Experimental Semi-Modular Synthesizer",
            yearReleased = 2021,
            description = "Alchemical Feedback / Delay Analog - In Production",
            priceRange = "599 USD",
            purchaseUrl = "Global Retailers (Perfect Circuit, Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Spectraphon",
            brand = "Make Noise",
            category = "Dual Spectral Oscillator Module",
            yearReleased = 2023,
            description = "Digital Real-Time Spectral Resynthesis - In Production",
            priceRange = "599 USD",
            purchaseUrl = "Global Retailers (Perfect Circuit, Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Maths",
            brand = "Make Noise",
            category = "Eurorack Dual Function Generator",
            yearReleased = 2009,
            description = "Analog Logic & Slew Rate Processor - In Production",
            priceRange = "290 USD",
            purchaseUrl = "Global Retailers (Perfect Circuit, Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Morphagene",
            brand = "Make Noise",
            category = "Eurorack Granular Processing Module",
            yearReleased = 2017,
            description = "Tape Microsound & Granular DSP - In Production",
            priceRange = "529 USD",
            purchaseUrl = "Global Retailers (Perfect Circuit, Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Cascadia",
            brand = "Intellijel",
            category = "Semi-Modular Analog Desktop",
            yearReleased = 2023,
            description = "East/West Coast Dual-Architecture Analog - In Production",
            priceRange = "2149 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "Atlantix",
            brand = "Intellijel",
            category = "Advanced Analog Voice Eurorack Module",
            yearReleased = 2024,
            description = "Dual-VCO Analog Subtractive Voice - In Production",
            priceRange = "749 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "Plonk",
            brand = "Intellijel",
            category = "Physical Modeling Percussion Module",
            yearReleased = 2017,
            description = "AAS Physical Modeling DSP - In Production",
            priceRange = "349 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "Rainmaker",
            brand = "Intellijel",
            category = "Eurorack Comb Resonator Module",
            yearReleased = 2016,
            description = "16-Tap Multi-Delay Resonator DSP - In Production",
            priceRange = "679 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "A-100 Basic System 2",
            brand = "Doepfer",
            category = "Modular Analog Eurorack System",
            yearReleased = 2010,
            description = "Modular Analog Subtractive - In Production",
            priceRange = "2850 USD",
            purchaseUrl = "Global Retailers (Thomann, SchneiderLaden)"
        ),
        SynthLibraryItem(
            name = "Dark Energy III",
            brand = "Doepfer",
            category = "Monophonic Analog Desktop",
            yearReleased = 2018,
            description = "1-VCO 12dB Multimode Filter Analog - In Production",
            priceRange = "599 USD",
            purchaseUrl = "Global Retailers (Thomann, SchneiderLaden)"
        ),
        SynthLibraryItem(
            name = "Carbon8",
            brand = "Modal Electronics",
            category = "Experimental Wavetable Synthesizer",
            yearReleased = 2024,
            description = "8-Voice Real-Time Phase Distortion / Wavetable - In Production",
            priceRange = "999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Argon8",
            brand = "Modal Electronics",
            category = "Wavetable Synthesizer",
            yearReleased = 2019,
            description = "8-Voice 32-Oscillator Wavetable Digital - In Production",
            priceRange = "699 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Cobalt8",
            brand = "Modal Electronics",
            category = "Extended Virtual Analog",
            yearReleased = 2020,
            description = "8-Voice 64-Oscillator Virtual Analog - In Production",
            priceRange = "699 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Peak",
            brand = "Novation",
            category = "Hybrid Synthesizer",
            yearReleased = 2017,
            description = "8-Voice Oxford FPGA Oscillators / Analog Filter - In Production",
            priceRange = "1399 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Summit",
            brand = "Novation",
            category = "Hybrid Synthesizer",
            yearReleased = 2019,
            description = "16-Voice Oxford FPGA Oscillators / Analog Filter - In Production",
            priceRange = "2199 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Bass Station II",
            brand = "Novation",
            category = "Monophonic Analog",
            yearReleased = 2013,
            description = "2-VCO Dual-Filter Analog Synthesizer - In Production",
            priceRange = "499 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Circuit Tracks",
            brand = "Novation",
            category = "Groovebox & Synthesizer",
            yearReleased = 2021,
            description = "2-Synth Tracks + 4-Drum Tracks Digital - In Production",
            priceRange = "399 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Circuit Rhythm",
            brand = "Novation",
            category = "Sample Groovebox",
            yearReleased = 2021,
            description = "8-Track Sample Record & Playback - In Production",
            priceRange = "399 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Super 6",
            brand = "UDO Audio",
            category = "Binaural Hybrid Synthesizer",
            yearReleased = 2020,
            description = "12-Voice FPGA Binaural / Analog Filter - In Production",
            priceRange = "2895 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "Super Gemini",
            brand = "UDO Audio",
            category = "Dual-Layer Binaural Hybrid Synthesizer",
            yearReleased = 2023,
            description = "20-Voice Dual-Layer FPGA / Analog Filter - In Production",
            priceRange = "4495 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "3rd Wave",
            brand = "Groove Synthesis",
            category = "Advanced Wavetable Synthesizer",
            yearReleased = 2022,
            description = "24-Voice 3-Oscillator PPG/Advanced Wavetable / Analog Filter - In Production",
            priceRange = "4995 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "Nina",
            brand = "Melbourne Instruments",
            category = "Motorized Hybrid Synthesizer",
            yearReleased = 2022,
            description = "12-Voice Motorized Knobs / Analog/Digital Oscillators - In Production",
            priceRange = "3599 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "LYRA-8",
            brand = "SOMA Laboratory",
            category = "Organismic Analog Synthesizer",
            yearReleased = 2016,
            description = "8 Non-Linear Cross-Modulated Analog Voices - In Production",
            priceRange = "750 USD",
            purchaseUrl = "Global Retailers (Thomann, Perfect Circuit, SOMA Direct)"
        ),
        SynthLibraryItem(
            name = "PULSAR-23",
            brand = "SOMA Laboratory",
            category = "Semi-Modular Drum Machine",
            yearReleased = 2020,
            description = "23-Module Semi-Modular Analog Drum Machine - In Production",
            priceRange = "2150 USD",
            purchaseUrl = "Global Retailers (Thomann, Perfect Circuit, SOMA Direct)"
        ),
        SynthLibraryItem(
            name = "Terra",
            brand = "SOMA Laboratory",
            category = "Microtonal Digital Synthesizer",
            yearReleased = 2022,
            description = "Wood-Cast Microtonal Digital Multi-Algorithm - In Production",
            priceRange = "1350 USD",
            purchaseUrl = "Global Retailers (Thomann, Perfect Circuit, SOMA Direct)"
        ),
        SynthLibraryItem(
            name = "DRM1 MKIV",
            brand = "Vermona",
            category = "Analog Drum Synthesizer",
            yearReleased = 2021,
            description = "8-Channel Dedicated Analog Drum Synthesis - In Production",
            priceRange = "899 USD",
            purchaseUrl = "Global Retailers (Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "Perfourmer MKII",
            brand = "Vermona",
            category = "Quad Analog Synthesizer",
            yearReleased = 2012,
            description = "4 Discrete Analog Voice Synthesizer - In Production",
            priceRange = "1750 USD",
            purchaseUrl = "Global Retailers (Thomann, Perfect Circuit)"
        ),
        SynthLibraryItem(
            name = "Tracker+",
            brand = "Polyend",
            category = "Standalone Audio Tracker & Synthesizer",
            yearReleased = 2024,
            description = "Tracker Workflow + 4 Synth Engines + Stereo Sampler - In Production",
            priceRange = "799 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Play+",
            brand = "Polyend",
            category = "Sample & Synth Groovebox",
            yearReleased = 2023,
            description = "Grid Step-Sequencer + 4 Synth Engines - In Production",
            priceRange = "799 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Easel Command (208C)",
            brand = "Buchla",
            category = "Semi-Modular Analog",
            yearReleased = 2021,
            description = "West Coast Complex Oscillator / Lopass Gate - In Production",
            priceRange = "2999 USD",
            purchaseUrl = "Global Retailers (Perfect Circuit, Sweetwater)"
        ),
        SynthLibraryItem(
            name = "Montage M8x",
            brand = "Yamaha",
            category = "Hybrid Digital Workstation",
            yearReleased = 2023,
            description = "AN-X (Analog Modeling) + FM-X + AWM2 - In Production",
            priceRange = "4499 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Reface CS",
            brand = "Yamaha",
            category = "Virtual Analog Modeling",
            yearReleased = 2015,
            description = "8-Voice AN Modeling - In Production",
            priceRange = "449 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Reface DX",
            brand = "Yamaha",
            category = "FM Synthesizer",
            yearReleased = 2015,
            description = "8-Voice 4-Op FM with Continuous Feedback - In Production",
            priceRange = "449 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "K2700",
            brand = "Kurzweil",
            category = "Performance Synthesizer Workstation",
            yearReleased = 2021,
            description = "V.A.S.T. / 6-Op FM / Flash-Play - In Production",
            priceRange = "2999 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "MPC Live II",
            brand = "Akai",
            category = "Standalone Sampler & Drum Machine",
            yearReleased = 2020,
            description = "Multi-Engine Standalone Music Production System - In Production",
            priceRange = "1299 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "MPC One+",
            brand = "Akai",
            category = "Standalone Sampler & Drum Machine",
            yearReleased = 2023,
            description = "Multi-Engine Standalone Music Production System - In Production",
            priceRange = "699 USD",
            purchaseUrl = "Global Retailers (Sweetwater, Thomann)"
        ),
        SynthLibraryItem(
            name = "Minimoog Model D (Original)",
            brand = "Moog",
            category = "Monophonic Analog",
            yearReleased = 1970,
            description = "3-VCO Discrete Transistor Ladder Filter - Vintage Collector",
            priceRange = "5500 USD",
            purchaseUrl = "Secondary Market (Reverb, Vintage Dealers)"
        ),
        SynthLibraryItem(
            name = "Modular System 55",
            brand = "Moog",
            category = "Modular Analog",
            yearReleased = 1973,
            description = "Discrete Transistor Modular System - Vintage Collector",
            priceRange = "35000 USD",
            purchaseUrl = "Private Auctions, Specialized Boutiques"
        ),
        SynthLibraryItem(
            name = "Memorymoog",
            brand = "Moog",
            category = "Polyphonic Analog",
            yearReleased = 1982,
            description = "6-Voice 3-VCO Curtis CEM Architecture - Vintage Collector",
            priceRange = "8500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Polymoog 203a",
            brand = "Moog",
            category = "Polyphonic Analog",
            yearReleased = 1975,
            description = "71-Key Fully Polyphonic Divide-Down Analog - Vintage Collector",
            priceRange = "2200 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Minimoog Voyager",
            brand = "Moog",
            category = "Monophonic Analog",
            yearReleased = 2002,
            description = "3-VCO Dual-Filter Analog Synthesizer - Discontinued",
            priceRange = "3200 USD",
            purchaseUrl = "Secondary Market (Reverb, eBay)"
        ),
        SynthLibraryItem(
            name = "Moog One",
            brand = "Moog",
            category = "Polyphonic Analog",
            yearReleased = 2018,
            description = "16-Voice 3-VCO Dual-Filter Analog - Discontinued",
            priceRange = "9999 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Jupiter-8",
            brand = "Roland",
            category = "Polyphonic Analog",
            yearReleased = 1981,
            description = "8-Voice 2-VCO Discrete Custom Filter - Vintage Collector",
            priceRange = "25000 USD",
            purchaseUrl = "Secondary Market (Reverb, Tokyo Vintage Shops)"
        ),
        SynthLibraryItem(
            name = "Juno-60",
            brand = "Roland",
            category = "Polyphonic Analog",
            yearReleased = 1982,
            description = "6-Voice DCO IR3109 Filter Analog - Vintage Collector",
            priceRange = "3500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Juno-106",
            brand = "Roland",
            category = "Polyphonic Analog",
            yearReleased = 1984,
            description = "6-Voice DCO 80017A VCF/VCA Analog - Vintage Collector",
            priceRange = "2000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "TB-303 Bass Line",
            brand = "Roland",
            category = "Monophonic Analog Bass",
            yearReleased = 1981,
            description = "1-VCO Diode Ladder Filter Bass Machine - Vintage Collector",
            priceRange = "3800 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "TR-808 Rhythm Composer",
            brand = "Roland",
            category = "Analog Drum Machine",
            yearReleased = 1980,
            description = "16-Instrument Transistor Bridged-T Resonator Drum - Vintage Collector",
            priceRange = "5000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "TR-909 Rhythm Composer",
            brand = "Roland",
            category = "Hybrid Drum Machine",
            yearReleased = 1983,
            description = "Analog Voices + 6-Bit ROM Samples - Vintage Collector",
            priceRange = "6500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "TR-707 Rhythm Composer",
            brand = "Roland",
            category = "Digital Drum Machine",
            yearReleased = 1985,
            description = "8-Bit PCM Digital Drum Samples - Vintage Collector",
            priceRange = "750 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "SH-101",
            brand = "Roland",
            category = "Monophonic Analog",
            yearReleased = 1982,
            description = "1-VCO Sub-Oscillator CEM3340 Analog - Vintage Collector",
            priceRange = "1400 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "System-100m",
            brand = "Roland",
            category = "Modular Analog",
            yearReleased = 1979,
            description = "Semi-Standardized Japanese Modular Analog - Vintage Collector",
            priceRange = "4500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "System-700",
            brand = "Roland",
            category = "Modular Analog",
            yearReleased = 1976,
            description = "Massive Professional Studio Modular Analog - Vintage Collector",
            priceRange = "28000 USD",
            purchaseUrl = "Private Auctions"
        ),
        SynthLibraryItem(
            name = "D-50",
            brand = "Roland",
            category = "Linear Arithmetic Digital",
            yearReleased = 1987,
            description = "LA Synthesis (Sample Attack + Resonant Waveform) - Vintage / Classic",
            priceRange = "650 USD",
            purchaseUrl = "Secondary Market (Reverb, eBay)"
        ),
        SynthLibraryItem(
            name = "JD-800",
            brand = "Roland",
            category = "Digital Synthesizer",
            yearReleased = 1991,
            description = "24-Voice ROM Waveform / Hands-on Sliders - Vintage / Classic",
            priceRange = "1200 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "JP-8000",
            brand = "Roland",
            category = "Virtual Analog",
            yearReleased = 1996,
            description = "8-Voice Analog Modeling (SuperSAW origin) - Discontinued",
            priceRange = "900 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "MS-20 (Vintage 1978)",
            brand = "Korg",
            category = "Semi-Modular Analog",
            yearReleased = 1978,
            description = "2-VCO Dual-Filter (Korg35) Analog - Vintage Collector",
            priceRange = "1200 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Polysix",
            brand = "Korg",
            category = "Polyphonic Analog",
            yearReleased = 1981,
            description = "6-Voice 1-VCO SSM2044 Filter Analog - Vintage Collector",
            priceRange = "1600 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Mono/Poly",
            brand = "Korg",
            category = "Paraphonic Analog",
            yearReleased = 1981,
            description = "4-VCO SSM2044 Filter Analog - Vintage Collector",
            priceRange = "2200 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "M1",
            brand = "Korg",
            category = "Digital Workstation",
            yearReleased = 1988,
            description = "16-Bit AI Synthesis PCM ROM Workstation - Vintage / Classic",
            priceRange = "450 USD",
            purchaseUrl = "Secondary Market (Reverb, eBay)"
        ),
        SynthLibraryItem(
            name = "Wavestation",
            brand = "Korg",
            category = "Vector / Wave Sequencing",
            yearReleased = 1990,
            description = "Advanced Vector Synthesis & Wave Sequencing - Vintage / Classic",
            priceRange = "550 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Prologue 16",
            brand = "Korg",
            category = "Hybrid Synthesizer",
            yearReleased = 2018,
            description = "16-Voice 2-VCO + Multi-Engine Digital Hybrid - Discontinued",
            priceRange = "1499 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Prophet-5 (Rev 2/3)",
            brand = "Sequential Circuits",
            category = "Polyphonic Analog",
            yearReleased = 1978,
            description = "5-Voice Dual-VCO Analog (SSM/CEM) - Vintage Collector",
            priceRange = "7500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Prophet-10",
            brand = "Sequential Circuits",
            category = "Dual-Manual Polyphonic Analog",
            yearReleased = 1980,
            description = "10-Voice Dual-Manual CEM Analog - Vintage Collector",
            priceRange = "9000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Pro-One",
            brand = "Sequential Circuits",
            category = "Monophonic Analog",
            yearReleased = 1981,
            description = "2-VCO CEM3340/3320 Analog - Vintage Collector",
            priceRange = "1800 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Prophet VS",
            brand = "Sequential Circuits",
            category = "Vector Synthesizer / Hybrid",
            yearReleased = 1986,
            description = "4-Waveform Vector Crossfading / Curtis Filter - Vintage Collector",
            priceRange = "5500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "DrumTraks",
            brand = "Sequential Circuits",
            category = "Digital Drum Machine",
            yearReleased = 1984,
            description = "8-Bit Sample Playback with Pitch/Vol Controls - Vintage Collector",
            priceRange = "1100 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "SEM (Original 1974)",
            brand = "Oberheim",
            category = "Monophonic Analog",
            yearReleased = 1974,
            description = "Discrete 2-VCO Multimode State-Variable Filter - Vintage Collector",
            priceRange = "2500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Four Voice (FVS-1)",
            brand = "Oberheim",
            category = "Polyphonic Analog",
            yearReleased = 1975,
            description = "4 Independent SEM Voice Modules - Vintage Collector",
            priceRange = "12000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Eight Voice (EVS-1)",
            brand = "Oberheim",
            category = "Polyphonic Analog",
            yearReleased = 1977,
            description = "8 Independent SEM Voice Modules - Vintage Collector",
            priceRange = "22000 USD",
            purchaseUrl = "Private Auctions"
        ),
        SynthLibraryItem(
            name = "OB-X",
            brand = "Oberheim",
            category = "Polyphonic Analog",
            yearReleased = 1979,
            description = "Discrete Transistor 8-Voice Polyphonic Analog - Vintage Collector",
            priceRange = "9500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "OB-Xa",
            brand = "Oberheim",
            category = "Polyphonic Analog",
            yearReleased = 1980,
            description = "Curtis CEM Integrated Circuit Polyphonic Analog - Vintage Collector",
            priceRange = "7000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "OB-8",
            brand = "Oberheim",
            category = "Polyphonic Analog",
            yearReleased = 1983,
            description = "8-Voice CEM-based Polyphonic Analog with Page 2 - Vintage Collector",
            priceRange = "5500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Matrix-12",
            brand = "Oberheim",
            category = "Polyphonic Analog",
            yearReleased = 1985,
            description = "12-Voice CEM Modulation Matrix Analog - Vintage Collector",
            priceRange = "8000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "DMX",
            brand = "Oberheim",
            category = "Digital Drum Machine",
            yearReleased = 1981,
            description = "8-Bit Companded Sample Playback - Vintage Collector",
            priceRange = "2800 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "2600 (Vintage)",
            brand = "ARP",
            category = "Semi-Modular Analog",
            yearReleased = 1971,
            description = "3-VCO 4012/4072 Filter Semi-Modular - Vintage Collector",
            priceRange = "11000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Odyssey (Rev 1/2/3)",
            brand = "ARP",
            category = "Duophonic Analog",
            yearReleased = 1972,
            description = "2-VCO Dual-Filter Duophonic Analog - Vintage Collector",
            priceRange = "2500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "2500 Modular Synthesizer",
            brand = "ARP",
            category = "Modular Analog",
            yearReleased = 1970,
            description = "Matrix Sliding Switch Modular Analog - Vintage Collector",
            priceRange = "40000 USD",
            purchaseUrl = "Museum & High-End Private Auctions"
        ),
        SynthLibraryItem(
            name = "Solina String Ensemble",
            brand = "ARP",
            category = "Analog String Synthesizer",
            yearReleased = 1974,
            description = "Divide-Down + Bucket-Brigade Ensemble Chorus - Vintage Collector",
            priceRange = "1800 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Quadra",
            brand = "ARP",
            category = "Analog Multi-Synthesizer",
            yearReleased = 1978,
            description = "4-Section Bass/Strings/Poly/Lead Synthesizer - Vintage Collector",
            priceRange = "5500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "VCS3",
            brand = "EMS",
            category = "Semi-Modular Analog",
            yearReleased = 1969,
            description = "Pin Matrix Patchboard 3-VCO Diode Filter - Vintage Collector",
            priceRange = "18000 USD",
            purchaseUrl = "Secondary Market (Reverb, Vintage Auctions)"
        ),
        SynthLibraryItem(
            name = "Synthi A",
            brand = "EMS",
            category = "Portable Semi-Modular Analog",
            yearReleased = 1971,
            description = "Suitcase Form Factor VCS3 Architecture - Vintage Collector",
            priceRange = "16000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Synthi 100",
            brand = "EMS",
            category = "Modular Analog",
            yearReleased = 1971,
            description = "Massive Dual-Matrix Studio Modular System - Museum Tier",
            priceRange = "75000 USD",
            purchaseUrl = "Rare Institutional Sales"
        ),
        SynthLibraryItem(
            name = "Series 100",
            brand = "Buchla",
            category = "Modular Analog",
            yearReleased = 1965,
            description = "Original West Coast Modular Voltage Controlled System - Museum Tier",
            priceRange = "45000 USD",
            purchaseUrl = "Institutional / Private Collector"
        ),
        SynthLibraryItem(
            name = "Music Easel (Vintage 1973)",
            brand = "Buchla",
            category = "Semi-Modular Analog",
            yearReleased = 1973,
            description = "Model 208 Stored Program Source + Model 218 Touch Keyboard - Vintage Collector",
            priceRange = "15000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Series 200 Electric Music Box",
            brand = "Buchla",
            category = "Modular Analog",
            yearReleased = 1970,
            description = "Discrete Functional Modules (Complex Osc, LPG, Source of Uncertainty) - Vintage Collector",
            priceRange = "30000 USD",
            purchaseUrl = "Specialized Collector Auctions"
        ),
        SynthLibraryItem(
            name = "CS-80",
            brand = "Yamaha",
            category = "Polyphonic Analog",
            yearReleased = 1977,
            description = "8-Voice Dual-Channel Polyphonic Analog with Polyphonic Aftertouch - Vintage Collector",
            priceRange = "60000 USD",
            purchaseUrl = "Secondary Market (High-End Vintage Dealers)"
        ),
        SynthLibraryItem(
            name = "CS-30",
            brand = "Yamaha",
            category = "Monophonic Analog",
            yearReleased = 1977,
            description = "2-VCO Dual-Filter Analog with 8-Step Sequencer - Vintage Collector",
            priceRange = "1600 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "DX7",
            brand = "Yamaha",
            category = "FM Synthesizer",
            yearReleased = 1983,
            description = "16-Voice 6-Operator 32-Algorithm Pure Digital FM - Vintage / Classic",
            priceRange = "600 USD",
            purchaseUrl = "Secondary Market (Reverb, eBay, Local)"
        ),
        SynthLibraryItem(
            name = "DX1",
            brand = "Yamaha",
            category = "FM Synthesizer",
            yearReleased = 1983,
            description = "Dual-Engine 6-Op FM with Wooden Cabinet & Poly Aftertouch - Vintage Collector",
            priceRange = "20000 USD",
            purchaseUrl = "Private Collector Auctions"
        ),
        SynthLibraryItem(
            name = "TX81Z",
            brand = "Yamaha",
            category = "FM Synthesizer Module",
            yearReleased = 1987,
            description = "4-Operator Multi-Waveform FM (Lately Bass origin) - Vintage / Classic",
            priceRange = "300 USD",
            purchaseUrl = "Secondary Market (Reverb, eBay)"
        ),
        SynthLibraryItem(
            name = "SY77",
            brand = "Yamaha",
            category = "Advanced FM / Sample Hybrid",
            yearReleased = 1989,
            description = "RCM (Realtime Convolution & Modulation) AFM + AWM2 - Vintage / Classic",
            priceRange = "550 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "AN1x",
            brand = "Yamaha",
            category = "Virtual Analog",
            yearReleased = 1997,
            description = "10-Voice Physical Modeling Analog DSP - Discontinued",
            priceRange = "700 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "CZ-101",
            brand = "Casio",
            category = "Phase Distortion Synthesizer",
            yearReleased = 1984,
            description = "8-Stage Envelope Phase Distortion (PD) - Vintage / Classic",
            priceRange = "350 USD",
            purchaseUrl = "Secondary Market (Reverb, eBay)"
        ),
        SynthLibraryItem(
            name = "CZ-1",
            brand = "Casio",
            category = "Phase Distortion Synthesizer",
            yearReleased = 1986,
            description = "Velocity/Aftertouch Flagship Phase Distortion - Vintage / Classic",
            priceRange = "700 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "VZ-1",
            brand = "Casio",
            category = "Interactive Phase Distortion",
            yearReleased = 1988,
            description = "iPD Modular Digital Phase Modulation - Vintage / Classic",
            priceRange = "500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "FZ-1",
            brand = "Casio",
            category = "Sampling Synthesizer",
            yearReleased = 1987,
            description = "16-Bit Variable Sample Rate / Digital Resonant Filters - Vintage / Classic",
            priceRange = "600 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "VL-1 (VL-Tone)",
            brand = "Casio",
            category = "Digital Synthesizer",
            yearReleased = 1979,
            description = "Walsh Function Monophonic Digital Synthesis - Vintage / Classic",
            priceRange = "100 USD",
            purchaseUrl = "Secondary Market (eBay, Reverb)"
        ),
        SynthLibraryItem(
            name = "K5000S",
            brand = "Kawai",
            category = "Additive Synthesizer",
            yearReleased = 1996,
            description = "128 Harmonic Formant Additive Synthesis + PCM - Vintage Collector",
            priceRange = "1100 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "K1",
            brand = "Kawai",
            category = "Vector Digital Synthesizer",
            yearReleased = 1988,
            description = "16-Voice 8-Bit VM (Variable Memory) Vector Synth - Vintage / Classic",
            priceRange = "250 USD",
            purchaseUrl = "Secondary Market (Reverb, eBay)"
        ),
        SynthLibraryItem(
            name = "SX-240",
            brand = "Kawai",
            category = "Polyphonic Analog",
            yearReleased = 1984,
            description = "8-Voice Dual-DCO SSM2044 Filter Polyphonic Analog - Vintage Collector",
            priceRange = "1500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Modular System",
            brand = "E-mu Systems",
            category = "Modular Analog",
            yearReleased = 1972,
            description = "Precision Solid-State Modular Subtractive - Museum Tier",
            priceRange = "25000 USD",
            purchaseUrl = "Specialized Collector Auctions"
        ),
        SynthLibraryItem(
            name = "Emulator II",
            brand = "E-mu Systems",
            category = "Sampling Synthesizer",
            yearReleased = 1984,
            description = "8-Voice 8-Bit Companded SSM2045 Analog Filter - Vintage Collector",
            priceRange = "4500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "SP-1200",
            brand = "E-mu Systems",
            category = "Sampling Drum Machine",
            yearReleased = 1987,
            description = "12-Bit 26.04kHz SSM2044 Dynamic Filtering - Vintage Collector",
            priceRange = "6500 USD",
            purchaseUrl = "Secondary Market (Reverb, Beatmaker Boutiques)"
        ),
        SynthLibraryItem(
            name = "Morpheus",
            brand = "E-mu Systems",
            category = "Z-Plane Filter Digital",
            yearReleased = 1993,
            description = "14-Pole 3D Z-Plane Interpolating Digital Filter - Discontinued",
            priceRange = "600 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Proteus/1",
            brand = "E-mu Systems",
            category = "Digital Sound Module",
            yearReleased = 1989,
            description = "16-Bit Sample Playback ROM Engine - Vintage / Classic",
            priceRange = "200 USD",
            purchaseUrl = "Secondary Market (Reverb, eBay)"
        ),
        SynthLibraryItem(
            name = "Wave 2.2",
            brand = "PPG",
            category = "Wavetable Synthesizer",
            yearReleased = 1982,
            description = "8-Voice 8-Bit Wavetable + SSM2044 Analog Filter - Vintage Collector",
            priceRange = "14000 USD",
            purchaseUrl = "Secondary Market (Reverb, High-End Studios)"
        ),
        SynthLibraryItem(
            name = "Wave 2.3",
            brand = "PPG",
            category = "Wavetable Synthesizer",
            yearReleased = 1984,
            description = "8-Voice 12-Bit Wavetable / Sample Transient + SSM2044 - Vintage Collector",
            priceRange = "16000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Realizer",
            brand = "PPG",
            category = "Digital Workstation Prototype",
            yearReleased = 1986,
            description = "First All-DSP Virtual Synthesizer / Sampler Workstation - Museum Prototype",
            priceRange = "60000 USD",
            purchaseUrl = "Museum Collection Only"
        ),
        SynthLibraryItem(
            name = "Virus TI2 Desktop",
            brand = "Access Music",
            category = "Virtual Analog / Wavetable",
            yearReleased = 2009,
            description = "Dual-DSP 110-Voice Total Integration Synthesizer - Discontinued",
            priceRange = "1600 USD",
            purchaseUrl = "Secondary Market (Reverb, eBay)"
        ),
        SynthLibraryItem(
            name = "Virus C",
            brand = "Access Music",
            category = "Virtual Analog",
            yearReleased = 2002,
            description = "32-Voice 3-Oscillator Virtual Analog - Discontinued",
            priceRange = "1000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Virus B",
            brand = "Access Music",
            category = "Virtual Analog",
            yearReleased = 1999,
            description = "24-Voice 3-Oscillator Virtual Analog - Discontinued",
            priceRange = "800 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Virus A",
            brand = "Access Music",
            category = "Virtual Analog",
            yearReleased = 1997,
            description = "12-Voice Virtual Analog - Discontinued",
            priceRange = "600 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Mirage",
            brand = "Ensoniq",
            category = "Sampling Synthesizer",
            yearReleased = 1984,
            description = "8-Voice 8-Bit Companded CEM3328 Analog Filter - Vintage / Classic",
            priceRange = "500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "ESQ-1",
            brand = "Ensoniq",
            category = "Hybrid Synthesizer",
            yearReleased = 1986,
            description = "8-Voice 32-Wavetable Oscillator / CEM3379 Analog Filter - Vintage / Classic",
            priceRange = "650 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "SQ-80",
            brand = "Ensoniq",
            category = "Cross Wave Hybrid",
            yearReleased = 1987,
            description = "8-Voice Cross-Waveform Engine / Curtis Analog Filter - Vintage / Classic",
            priceRange = "900 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "ASR-10",
            brand = "Ensoniq",
            category = "Sampling Workstation",
            yearReleased = 1992,
            description = "16-Bit Stereo Digital Sampling Workstation + DP/4 FX - Vintage / Classic",
            priceRange = "1500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "Fizmo",
            brand = "Ensoniq",
            category = "Transwave Wavetable",
            yearReleased = 1998,
            description = "48-Voice Real-Time Transwave Animated Synthesis - Vintage Collector",
            priceRange = "3200 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "LM-1 Drum Computer",
            brand = "Linn Electronics",
            category = "Sample-Based Drum Machine",
            yearReleased = 1980,
            description = "28kHz 8-Bit Companded EPROM Samples - Vintage Collector",
            priceRange = "8500 USD",
            purchaseUrl = "Specialized Collector Auctions"
        ),
        SynthLibraryItem(
            name = "LinnDrum",
            brand = "Linn Electronics",
            category = "Sample-Based Drum Machine",
            yearReleased = 1982,
            description = "35kHz 8-Bit EPROM Sample Playback - Vintage Collector",
            priceRange = "4500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "CMI Series IIx",
            brand = "Fairlight",
            category = "Sampling / Additive Workstation",
            yearReleased = 1983,
            description = "8-Voice 8-Bit Microprocessor Sampling & Lightpen Wave Draw - Museum Tier",
            priceRange = "35000 USD",
            purchaseUrl = "Specialized Studio Auctions"
        ),
        SynthLibraryItem(
            name = "CMI Series III",
            brand = "Fairlight",
            category = "Digital Sampling Workstation",
            yearReleased = 1985,
            description = "16-Voice 16-Bit Polyphonic Digital Sampler - Museum Tier",
            priceRange = "40000 USD",
            purchaseUrl = "Specialized Studio Auctions"
        ),
        SynthLibraryItem(
            name = "Synclavier II",
            brand = "New England Digital",
            category = "FM / Additive Synthesizer",
            yearReleased = 1980,
            description = "High-Speed Hardware FM & 24-Harmonic Additive Synthesis - Museum Tier",
            priceRange = "45000 USD",
            purchaseUrl = "Specialized Studio Auctions"
        ),
        SynthLibraryItem(
            name = "MPC60",
            brand = "Akai",
            category = "Sampling Drum Machine",
            yearReleased = 1988,
            description = "16-Voice 12-Bit 40kHz Non-Interpolating Roger Linn OS - Vintage Collector",
            priceRange = "3500 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "MPC3000",
            brand = "Akai",
            category = "Sampling Drum Machine",
            yearReleased = 1994,
            description = "16-Bit 44.1kHz Stereo Sampling Drum Computer - Vintage Collector",
            priceRange = "4000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "AX80",
            brand = "Akai",
            category = "Polyphonic Analog",
            yearReleased = 1984,
            description = "8-Voice Dual-DCO Curtis Filter Analog - Vintage / Classic",
            priceRange = "1200 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "S950",
            brand = "Akai",
            category = "Digital Sampler Rack",
            yearReleased = 1988,
            description = "12-Bit Variable Clock Sampler with Time-Stretch - Vintage / Classic",
            priceRange = "1200 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "K250",
            brand = "Kurzweil",
            category = "Sample-Playback Synthesizer",
            yearReleased = 1984,
            description = "Rompler / Additive Resynthesis Architecture - Vintage Collector",
            priceRange = "3000 USD",
            purchaseUrl = "Secondary Market (Reverb)"
        ),
        SynthLibraryItem(
            name = "K2000",
            brand = "Kurzweil",
            category = "V.A.S.T. Synthesizer",
            yearReleased = 1991,
            description = "Variable Architecture Synthesis Technology (V.A.S.T.) - Vintage / Classic",
            priceRange = "650 USD",
            purchaseUrl = "Secondary Market (Reverb, eBay)"
        ),
        SynthLibraryItem(
            name = "Plaits",
            brand = "Mutable Instruments",
            category = "Eurorack Macro-Oscillator Module",
            yearReleased = 2018,
            description = "16 Digital Synthesis Models (FM, VA, Additive, Speech) - Discontinued (Open-Hardware Clones Active)",
            priceRange = "250 USD",
            purchaseUrl = "Secondary Market & Third-Party Makers"
        ),
        SynthLibraryItem(
            name = "Clouds",
            brand = "Mutable Instruments",
            category = "Eurorack Granular Module",
            yearReleased = 2014,
            description = "Granular Audio Texture & Pitch-Shifter DSP - Discontinued (Open-Hardware Clones Active)",
            priceRange = "350 USD",
            purchaseUrl = "Secondary Market & Third-Party Makers"
        ),
        SynthLibraryItem(
            name = "Rings",
            brand = "Mutable Instruments",
            category = "Eurorack Resonator Module",
            yearReleased = 2015,
            description = "Physical Resonator / Modal Synthesis DSP - Discontinued (Open-Hardware Clones Active)",
            priceRange = "280 USD",
            purchaseUrl = "Secondary Market & Third-Party Makers"
        ),
        SynthLibraryItem(
            name = "S1 MK2",
            brand = "Cwejman",
            category = "Semi-Modular Analog",
            yearReleased = 2006,
            description = "Ultra-Precision Hand-Calibrated Discrete Analog Voice - Vintage Collector",
            priceRange = "6500 USD",
            purchaseUrl = "Private Specialized Auctions"
        ),
    )
}
