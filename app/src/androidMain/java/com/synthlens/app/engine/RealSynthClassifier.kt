package com.synthlens.app.engine
import kotlin.math.*
class RealSynthClassifier {
    data class ClassificationResult(val topMatch: MatchResult?, val allMatches: List<MatchResult>, val quality: Float, val notes: List<String>)
    data class MatchResult(val synthId: String, val name: String, val brand: String, val category: String, val confidence: Float, val level: String, val synthType: String, val failures: List<String>)
    fun classify(features: SynthFeatureVector): ClassificationResult {
        val notes = mutableListOf<String>(); val quality = assessQuality(features, notes)
        if (quality < 0.3f) return ClassificationResult(null, emptyList(), quality, notes)
        val matches = SynthSignatureDB.sigs.values.mapNotNull { sig -> scoreSynth(features, sig, quality).takeIf { it.confidence >= 0.35f } }.sortedByDescending { it.confidence }
        if (matches.size >= 2 && matches[0].confidence - matches[1].confidence < 0.1f) notes.add("Ambiguous: ${matches[0].name} vs ${matches[1].name}")
        val top = matches.firstOrNull()?.takeIf { it.confidence >= 0.50f }
        return ClassificationResult(top, matches.take(5), quality, notes)
    }
    private fun scoreSynth(f: SynthFeatureVector, sig: SynthSignature, quality: Float): MatchResult {
        val w = sig.weights; var tw = 0f; var ws = 0f; val fails = mutableListOf<String>()
        val sl = matchSlope(f.filterSlope, sig.filterSlope); tw+=(w["filterSlope"]?:0.20f); ws+=sl*tw
        val cs = sig.centroidRange.matchScore(f.spectralCentroid); tw+=(w["centroid"]?:0.15f); ws+=cs*tw
        val hs = matchHarm(f, sig.harmProfile); tw+=(w["harm"]?:0.20f); ws+=hs*tw
        val ds = sig.driftRange.matchScore(estDrift(f)); tw+=(w["drift"]?:0.10f); ws+=ds*tw
        val wv = 0.5f; tw+=(w["waveform"]?:0.10f); ws+=wv*tw
        val rs = sig.resRange.matchScore(f.resonance); tw+=(w["resonance"]?:0.10f); ws+=rs*tw
        val os = if(estOsc(f)==sig.oscCount) 1f else 0.5f; tw+=(w["osc"]?:0.10f); ws+=os*tw
        val raw = if(tw>0) ws/tw else 0f; val qp = if(quality<0.7f) 0.85f+quality*0.15f else 1f; val final = (raw*qp).coerceIn(0f,1f)
        val level = when{final>=0.85f->"Matched";final>=0.70f->"Likely";final>=0.50f->"Possible";else->"Uncertain"}
        return MatchResult(sig.id, sig.name, sig.brand, sig.category, final, level, sig.synthType, fails)
    }
    private fun matchSlope(m: Float, e: Float): Float { val d=abs(m-e); return when{d<1f->1f;d<3f->0.85f;d<6f->0.6f;d<10f->0.3f;else->0.1f} }
    private fun matchHarm(f: SynthFeatureVector, sig: HarmonicSig): Float { val br=estBright(f); val w=1f-br; val oe=estOE(f); val th=1f-f.harmonicToNoiseRatio; return (sig.brightness.matchScore(br)+sig.warmth.matchScore(w)+sig.oddEven.matchScore(oe)+sig.thd.matchScore(th))/4f }
    private fun estBright(f: SynthFeatureVector): Float { val hi=f.mfccs.drop(3).sumOf{abs(it).toDouble()}.toFloat(); val t=f.mfccs.sumOf{abs(it).toDouble()}.toFloat(); val r=if(t>0.001f)hi/t else 0.3f; val cn=(f.spectralCentroid/5000f).coerceIn(0f,1f); return (r*0.6f+cn*0.4f).coerceIn(0f,1f) }
    private fun estOE(f: SynthFeatureVector): Float { val o=listOf(0,4,7,11).sumOf{f.chroma.getOrElse(it){0f}.toDouble()}.toFloat(); val e=listOf(2,5,9).sumOf{f.chroma.getOrElse(it){0f}.toDouble()}.toFloat(); val t=o+e; return if(t>0.001f)o/t else 0.5f }
    private fun estDrift(f: SynthFeatureVector): Float { val s=f.harmonicToNoiseRatio*(1f-f.zeroCrossingRate*5f).coerceIn(0f,1f); return when{s>0.8f->0.3f;s>0.6f->1.5f;s>0.4f->4f;s>0.2f->8f;else->15f} }
    private fun estOsc(f: SynthFeatureVector): Int { val v=f.mfccs.map{it*it}.average().toFloat(); return when{v>10f->3;v>5f->2;else->1} }
    private fun assessQuality(f: SynthFeatureVector, n: MutableList<String>): Float { var q=1f; if(f.mfccs.sumOf{abs(it).toDouble()}.toFloat()<0.01f){q-=0.4f;n.add("Low MFCC")}; if(f.harmonicToNoiseRatio<0.1f){q-=0.3f;n.add("Low HNR")}; if(f.zeroCrossingRate>0.5f){q-=0.2f;n.add("High ZCR")}; if(f.spectralCentroid<0.002f){q-=0.3f;n.add("Low centroid")}; return q.coerceIn(0f,1f) }
}
