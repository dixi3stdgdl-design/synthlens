package com.synthlens.app.engine

import kotlin.math.*

class RealSynthClassifier {

    data class ClassificationResult(val topMatch: MatchResult?, val allMatches: List<MatchResult>, val quality: Float, val notes: List<String>)
    
    data class MatchResult(val synthId: String, val name: String, val brand: String, val category: String, val confidence: Float, val level: String, val synthType: String, val failures: List<String>)

    fun classify(features: SynthFeatureVector): ClassificationResult {
        val notes = mutableListOf<String>()
        val quality = assessQuality(features, notes)

        // Lower quality threshold to be more permissive with microphone noise
        if (quality < 0.2f) return ClassificationResult(null, emptyList(), quality, notes)

        val inferredProfile = inferAcousticProfile(features)
        
        // Iterate over the ENTIRE catalog instead of static signatures
        val matches = SynthCatalogDB.catalog.values.mapNotNull { info ->
            scoreCatalogSynth(inferredProfile, info, quality).takeIf { it.confidence >= 0.25f }
        }.sortedByDescending { it.confidence }

        if (matches.size >= 2 && matches[0].confidence - matches[1].confidence < 0.05f) {
            notes.add("Ambiguous: ${matches[0].name} vs ${matches[1].name}")
        }

        val top = matches.firstOrNull()?.takeIf { it.confidence >= 0.40f }
        return ClassificationResult(top, matches.take(5), quality, notes)
    }

    private data class InferredProfile(
        val isAnalog: Float,
        val isFM: Float,
        val isDigital: Float,
        val oscCount: Int,
        val hasResonance: Float
    )

    private fun inferAcousticProfile(f: SynthFeatureVector): InferredProfile {
        val drift = estDrift(f)
        val inharmonicity = estInharmonicity(f)
        
        val isAnalog = (drift / 10f).coerceIn(0f, 1f) * (1f - inharmonicity * 0.5f)
        val isFM = inharmonicity.coerceIn(0f, 1f) * (1f - drift / 15f).coerceIn(0f, 1f)
        val isDigital = (1f - isAnalog) * (1f - isFM * 0.5f)

        return InferredProfile(
            isAnalog = isAnalog.coerceIn(0f, 1f),
            isFM = isFM.coerceIn(0f, 1f),
            isDigital = isDigital.coerceIn(0f, 1f),
            oscCount = estOsc(f),
            hasResonance = if (f.resonance > 0.4f) 1f else 0f
        )
    }

    private fun scoreCatalogSynth(prof: InferredProfile, info: SynthInfo, quality: Float): MatchResult {
        var score = 0f
        var weightTotal = 0f

        // 1. Synthesis Type Matching
        val typeScore = when (info.synthesisType) {
            SynthType.ANALOG_HARDWARE -> prof.isAnalog
            SynthType.ANALOG_MODELING -> prof.isAnalog * 0.8f + prof.isDigital * 0.2f
            SynthType.DIGITAL_HARDWARE -> prof.isDigital
            SynthType.VIRTUAL_ANALOG -> prof.isAnalog * 0.7f + prof.isDigital * 0.3f
            else -> if (info.category == SynthCategory.FM_SYNTH) prof.isFM else 0.5f
        }
        score += typeScore * 3f; weightTotal += 3f

        // 2. Oscillator Count
        val oscScore = if (info.oscillatorProfile.count == prof.oscCount) 1f else if (abs(info.oscillatorProfile.count - prof.oscCount) == 1) 0.6f else 0.2f
        score += oscScore * 2f; weightTotal += 2f

        // 3. Resonance
        if (prof.hasResonance > 0.5f) {
            val resScore = if (info.filterProfile.hasResonance) 1f else 0f
            score += resScore * 1.5f; weightTotal += 1.5f
        }

        val raw = if (weightTotal > 0f) score / weightTotal else 0f
        
        // Quality penalty is less severe now
        val qp = if (quality < 0.6f) 0.85f + quality * 0.15f else 1f 
        val final = (raw * qp).coerceIn(0f, 1f)

        val level = when {
            final >= 0.80f -> "Matched"
            final >= 0.60f -> "Likely"
            final >= 0.40f -> "Possible"
            else -> "Uncertain"
        }

        return MatchResult(info.id, info.name, info.brand, info.category.name, final, level, info.synthesisType.name, emptyList())
    }

    private fun estDrift(f: SynthFeatureVector): Float {
        // HNR and ZCR combo to estimate pitch drift/imperfections
        val s = f.harmonicToNoiseRatio * (1f - f.zeroCrossingRate * 5f).coerceIn(0f, 1f)
        return when {
            s > 0.8f -> 0.3f
            s > 0.6f -> 1.5f
            s > 0.4f -> 4f
            s > 0.2f -> 8f
            else -> 12f // reduced from 15f to prevent over-penalizing
        }
    }

    private fun estInharmonicity(f: SynthFeatureVector): Float {
        // High Spectral centroid + low HNR generally implies metallic/inharmonic tones common in FM
        val centroidNormalized = (f.spectralCentroid / 4000f).coerceIn(0f, 1f)
        val noiseComponent = (1f - f.harmonicToNoiseRatio).coerceIn(0f, 1f)
        return (centroidNormalized * 0.6f + noiseComponent * 0.4f)
    }

    private fun estOsc(f: SynthFeatureVector): Int {
        val v = f.mfccs.map { it * it }.average().toFloat()
        return when {
            v > 8f -> 3
            v > 4f -> 2
            else -> 1
        }
    }

    private fun assessQuality(f: SynthFeatureVector, n: MutableList<String>): Float {
        var q = 1f
        val mfccSum = f.mfccs.sumOf { abs(it).toDouble() }.toFloat()
        if (mfccSum < 0.005f) { // lowered threshold from 0.01f
            q -= 0.3f
            n.add("Low MFCC")
        }
        if (f.harmonicToNoiseRatio < 0.05f) { // lowered threshold from 0.1f
            q -= 0.2f
            n.add("Low HNR")
        }
        if (f.zeroCrossingRate > 0.6f) { // raised threshold
            q -= 0.2f
            n.add("High ZCR")
        }
        if (f.spectralCentroid < 0.001f) {
            q -= 0.2f
            n.add("Low centroid")
        }
        return q.coerceIn(0f, 1f)
    }
}
