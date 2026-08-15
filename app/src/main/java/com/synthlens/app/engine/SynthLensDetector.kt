package com.synthlens.app.engine
import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
class SynthLensDetector(private val context: Context) {
    private var interp: Interpreter? = null; private var modelOk = false
    private var labels = listOf<Label>(); private var norms: FloatArray? = null
    private val featExtractor = FeatureExtractor(); private val sigClassifier = RealSynthClassifier()
    data class Label(val id: Int, val name: String, val brand: String, val type: String, val cat: String)
    data class DetectionResult(val topMatch: Match?, val alts: List<Match>, val confidence: Float, val level: String, val timeMs: Long, val method: String, val quality: Float, val oscPattern: OscillatorPatternResult? = null)
    data class Match(val name: String, val brand: String, val cat: String, val conf: Float, val synthType: String = "")
    init { loadModel(); loadLabels(); loadNorms() }
    private fun loadModel() { try { val b = loadFile("synth_model.tflite"); if (b != null) { interp = Interpreter(b, Interpreter.Options().apply { setNumThreads(4) }); modelOk = true } } catch (_: Exception) { modelOk = false } }
    private fun loadFile(n: String): MappedByteBuffer? = try { val fd = context.assets.openFd(n); FileInputStream(fd.fileDescriptor).channel.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength) } catch (_: Exception) { null }
    private fun loadLabels() { try { labels = context.assets.open("label_map.txt").bufferedReader().readLines().filter { !it.startsWith("#") && it.isNotBlank() }.mapNotNull { val p = it.split(","); if (p.size >= 5) Label(p[0].trim().toIntOrNull() ?: return@mapNotNull null, p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim()) else null } } catch (_: Exception) {} }
    private fun loadNorms() { try { val json = context.assets.open("normalization_params.json").bufferedReader().readText(); val params = mutableListOf<Pair<Float, Float>>(); val rx = """"mean"\s*:\s*([-\d.]+).*?"std"\s*:\s*([-\d.]+)""".toRegex(); for (m in rx.findAll(json)) { params.add(Pair(m.groupValues[1].toFloatOrNull() ?: 0f, m.groupValues[2].toFloatOrNull() ?: 1f)) }; if (params.size >= FeatureExtractor.FEATURE_COUNT) { val arr = FloatArray(FeatureExtractor.FEATURE_COUNT * 2); for (i in 0 until FeatureExtractor.FEATURE_COUNT) { arr[i] = params[i].first; arr[i + FeatureExtractor.FEATURE_COUNT] = params[i].second }; norms = arr } } catch (_: Exception) {} }
    fun detect(audio: FloatArray, oscPattern: OscillatorPatternResult? = null): DetectionResult {
        val t0 = System.currentTimeMillis(); val feat = featExtractor.extractFeatures(audio); val q = assessQ(feat)
        DiagnosticService.logFeatures(feat) // Agregado para diagnostico
        val mlRes = if (modelOk) mlClassify(feat) else null; val sigRes = sigClassifier.classify(SynthFeatureVector.fromFloatArray(feat)); val dt = System.currentTimeMillis() - t0
        if (mlRes != null && mlRes.conf >= (sigRes.topMatch?.confidence ?: 0f)) { return DetectionResult(mlRes.top?.let { Match(it.name, it.brand, it.cat, it.conf, it.type) }, mlRes.alts.map { Match(it.name, it.brand, it.cat, it.conf, it.type) }, mlRes.conf, mlRes.level, dt, "ml", q, oscPattern) }
        val top = sigRes.topMatch; return DetectionResult(top?.let { Match(it.name, it.brand, it.category, it.confidence, it.synthType) }, sigRes.allMatches.drop(1).take(3).map { Match(it.name, it.brand, it.category, it.confidence, it.synthType) }, top?.confidence ?: 0f, top?.level ?: "Uncertain", dt, "signature", q, oscPattern)
    }
    private data class MLRes(val top: MLMatch?, val alts: List<MLMatch>, val conf: Float, val level: String)
    private data class MLMatch(val name: String, val brand: String, val cat: String, val conf: Float, val type: String)
    private fun mlClassify(feat: FloatArray): MLRes? {
        val inter = interp ?: return null; val n = FloatArray(FeatureExtractor.FEATURE_COUNT); val params = norms
        if (params != null) { for (i in 0 until FeatureExtractor.FEATURE_COUNT) { val s = params[i + FeatureExtractor.FEATURE_COUNT]; n[i] = if (s > 1e-8f) (feat[i] - params[i]) / s else 0f } } else { for (i in feat.indices) n[i] = feat[i] }
        val inp = ByteBuffer.allocateDirect(4 * FeatureExtractor.FEATURE_COUNT).apply { order(ByteOrder.nativeOrder()); for (v in n) putFloat(v) }
        val nc = if (labels.isNotEmpty()) labels.size else 200; val out = ByteBuffer.allocateDirect(4 * nc).apply { order(ByteOrder.nativeOrder()) }
        try { inp.rewind(); out.rewind(); inter.run(inp, out) } catch (_: Exception) { return null }
        out.rewind(); val probs = FloatArray(nc) { out.float }; val sorted = probs.indices.sortedByDescending { probs[it] }.take(4)
        val topI = sorted[0]; val topC = probs[topI]; if (topC < 0.3f) return null
        val topL = if (topI < labels.size) labels[topI] else null
        val alts = sorted.drop(1).mapNotNull { if (it < labels.size && probs[it] > 0.1f) { val l = labels[it]; MLMatch(l.name, l.brand, l.cat, probs[it], l.type) } else null }
        val lvl = when { topC >= 0.85f -> "Matched"; topC >= 0.70f -> "Likely"; topC >= 0.50f -> "Possible"; else -> "Uncertain" }
        return MLRes(topL?.let { MLMatch(it.name, it.brand, it.cat, topC, it.type) }, alts, topC, lvl)
    }
    private fun assessQ(f: FloatArray): Float { var q = 1f; if (f.take(13).sumOf { kotlin.math.abs(it).toDouble() }.toFloat() < 0.01f) q -= 0.4f; if (f.getOrElse(42) { 0f } < 0.1f) q -= 0.3f; if (f.getOrElse(41) { 0f } > 0.5f) q -= 0.2f; if (f.getOrElse(39) { 0f } < 0.002f) q -= 0.3f; return q.coerceIn(0f, 1f) }
    fun close() { interp?.close(); interp = null; modelOk = false; featExtractor.reset() }
}
