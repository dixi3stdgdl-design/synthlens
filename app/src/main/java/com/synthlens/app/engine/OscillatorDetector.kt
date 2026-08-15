package com.synthlens.app.engine
import kotlin.math.*
class OscillatorDetector {
    companion object { const val HIST = 128; const val SR = 44100; const val FFT = 2048 }
    private val ampH = FloatArray(HIST); private val freqH = FloatArray(HIST); private val brightH = FloatArray(HIST)
    private var hIdx = 0; private var frames = 0
    fun detect(frequency: Float, amplitude: Float, harmonics: List<Float>, spectrum: FloatArray, waveformType: String, spectralFlatness: Float, spectralRolloff: Float, spectralBandwidth: Float, hnr: Float, thd: Float): OscillatorPatternResult {
        val i = hIdx % HIST; ampH[i] = amplitude; freqH[i] = frequency
        brightH[i] = if (harmonics.isNotEmpty() && harmonics.sum() > 0) harmonics.drop(4).sum() / harmonics.sum() else 0f
        hIdx++; frames++
        if (frames < 16) return OscillatorPatternResult()
        val lfo = detectLFO(); val am = detectAM(); val fm = detectFM(spectrum, frequency, harmonics)
        val drift = detectDrift(frequency); val oscCount = estimateOscCount(frequency, spectrum, harmonics, thd)
        val env = detectEnvelope(amplitude); val stability = calcStability()
        val oscType = classifyType(harmonics, thd, drift, oscCount, waveformType)
        return OscillatorPatternResult(lfo=lfo, amModulation=am, fmModulation=fm, drift=drift, activeOscillators=oscCount, envelope=env, modulationDepth=maxOf(lfo.depth,am.depth,fm.sidebandRatio*0.5f), oscillatorType=oscType, isModulated=lfo.isDetected||am.isDetected||fm.isDetected, stability=stability)
    }
    private fun detectLFO(): LFOResult {
        val n = minOf(frames, HIST); val ap = findPer(ampH, n, 0.1f, 30f); val pp = findPer(freqH, n, 0.1f, 30f); val fp = findPer(brightH, n, 0.1f, 30f)
        val det = ap.isP || pp.isP || fp.isP; val type = when { ap.isP&&!pp.isP->"Tremolo"; pp.isP&&!ap.isP->"Vibrato"; fp.isP->"Filter Sweep"; det->"Combined LFO"; else->"None" }
        return LFOResult(det, maxOf(ap.freq,pp.freq,fp.freq), maxOf(ap.dep,pp.dep,fp.dep), type, "Sine", maxOf(ap.freq,pp.freq,fp.freq))
    }
    private data class PR(val isP: Boolean=false, val freq: Float=0f, val dep: Float=0f)
    private fun findPer(buf: FloatArray, n: Int, mnF: Float, mxF: Float): PR {
        if (n<16) return PR(); var mean=0f; for(j in 0 until n) mean+=buf[(hIdx-n+j+HIST)%HIST]; mean/=n
        val sig=FloatArray(n){buf[(hIdx-n+it+HIST)%HIST]-mean}; var vari=0f; for(v in sig)vari+=v*v; vari/=n; if(vari<1e-6f)return PR()
        val fr=60f; val mnL=(fr/mxF).toInt().coerceAtLeast(2); val mxL=(fr/mnF).toInt().coerceAtMost(n/2); if(mnL>=mxL)return PR()
        var bC=0f; var bL=0; for(lag in mnL..mxL){var c=0f; for(j in 0 until n-lag)c+=sig[j]*sig[j+lag]; c/=(n-lag)*vari; if(c>bC){bC=c;bL=lag}}
        if(bC<0.35f||bL<=0)return PR(); val dF=fr/bL; var mn=Float.MAX_VALUE; var mx=Float.MIN_VALUE
        for(j in 0 until n){val v=buf[(hIdx-n+j+HIST)%HIST]; if(v<mn)mn=v; if(v>mx)mx=v}
        val ct=(mx+mn)/2f; val dep=if(ct>0.001f)((mx-mn)/2f)/ct else 0f; return PR(true, dF, dep.coerceIn(0f,1f))
    }
    private fun detectAM(): AMResult {
        val n=minOf(frames,HIST); if(n<16)return AMResult(); var am=0f; for(j in 0 until n)am+=ampH[(hIdx-n+j+HIST)%HIST]; am/=n; if(am<0.01f)return AMResult()
        var ms=0f; for(j in 0 until n){val d=ampH[(hIdx-n+j+HIST)%HIST]-am; ms+=d*d}; val mi=sqrt(ms/n)/am; val per=findPer(ampH,n,0.5f,20f)
        val det=mi>0.15f&&per.isP; val type=when{mi>0.8f->"Deep AM";mi>0.4f->"Medium AM";mi>0.15f->"Subtle AM";else->"None"}
        return AMResult(det,mi.coerceIn(0f,2f),(mi*0.5f).coerceIn(0f,1f),if(per.isP)per.freq else 0f,type)
    }
    private fun detectFM(spectrum: FloatArray, fund: Float, harmonics: List<Float>): FMResult {
        if(spectrum.isEmpty()||fund<=0)return FMResult(); val bw=SR.toFloat()/FFT; val fb=(fund/bw).toInt().coerceIn(0,spectrum.size-1)
        val fp=peakIn(spectrum,fb,3); if(fp<0.001f)return FMResult(); var se=0f; val sr=(2000f/bw).toInt()
        for(b in maxOf(1,fb-sr)..minOf(spectrum.size-2,fb+sr)){if(b==fb)continue; if(spectrum[b]>spectrum[b-1]&&spectrum[b]>spectrum[b+1]&&spectrum[b]>fp*0.03f){val freq=b*bw; val nh=round(freq/fund); val dev=abs(freq-nh*fund)/fund; if(dev>0.05f||nh<1.5f)se+=spectrum[b]}}
        val ratio=if(fp>0)se/fp else 0f; val pv=pitchVar(); val det=ratio>0.1f&&pv>2f; val type=when{ratio>0.5f->"Deep FM";ratio>0.25f->"Medium FM";ratio>0.1f->"Subtle FM";else->"None"}
        return FMResult(det,ratio.coerceIn(0f,2f),(ratio*2f).coerceIn(0f,5f),type)
    }
    private fun detectDrift(freq: Float): DriftResult {
        val n=minOf(frames,50); if(n<8)return DriftResult(); val fs=mutableListOf<Float>(); for(j in 0 until n){val f=freqH[(hIdx-n+j+HIST)%HIST]; if(f in 20f..20000f&&f.isFinite())fs.add(f)}; if(fs.size<4)return DriftResult()
        val mean=fs.average().toFloat(); if(mean<=0)return DriftResult(); var vs=0f; for(f in fs)vs+=(f-mean)*(f-mean); val cents=if(mean>0)1200f*log2((mean+sqrt(vs/fs.size))/mean) else 0f
        val isA=cents>3f; val type=when{cents>15f->"Slow Analog Drift";cents>8f->"Fast Jitter";cents>5f->"Mild Instability";cents>2f->"Slight Drift";else->"Stable"}
        return DriftResult(cents>2f,cents,type,isA,(1f-(cents/30f).coerceIn(0f,1f)))
    }
    private fun estimateOscCount(freq: Float, spectrum: FloatArray, harmonics: List<Float>, thd: Float): OscillatorCountResult {
        if(spectrum.isEmpty()||freq<=0)return OscillatorCountResult(); val bw=SR.toFloat()/FFT; var pk=0; val th=(spectrum.maxOrNull()?:0f)*0.08f; val se=minOf(spectrum.size-1,(4000f/bw).toInt())
        for(i in 2 until se){if(spectrum[i]>spectrum[i-1]&&spectrum[i]>spectrum[i+1]&&spectrum[i]>th){val isH=(1..8).any{abs(i-(it*freq/bw).toInt())<=2}; if(!isH)pk++}}
        val bt=harmonics.size>=3&&harmonics.zipWithNext().count{(a,b)->b>a*1.5f||b<a*0.3f}>harmonics.size*0.4f
        val c=when{pk>=3&&bt->3;pk>=2||bt->2;else->1}; val cfg=when(c){1->"Single";2->if(bt)"Dual Detuned" else "Dual";3->"Triple";else->"Multi"}
        return OscillatorCountResult(c,bt,cfg)
    }
    private fun detectEnvelope(amp: Float): EnvelopeResult {
        val n=minOf(frames,64); if(n<8)return EnvelopeResult(); val sm=FloatArray(n){ampH[(hIdx-n+it+HIST)%HIST]}; var pV=0f; var pI=0; for(i in 0 until n){if(sm[i]>pV){pV=sm[i];pI=i}}
        val phase=if(n>=3){val cur=sm[n-1]; val trend=sm[n-1]-sm[n-3]; when{cur<pV*0.05f->"Idle";trend>0.01f->"Attack";trend<-0.005f&&cur>pV*0.3f->"Decay";abs(trend)<0.005f&&cur>0.01f->"Sustain";trend<-0.005f->"Release";else->"Unknown"}}else"Unknown"
        val shape=when{pI<n*0.1f&&pV>0.01f->"Percussive";pI<n*0.05f->"Pluck";pI>n*0.5f->"Pad";else->"Standard ADSR"}
        return EnvelopeResult(true,phase,shape,pI*16.67f,if(pV>0)sm.getOrElse(n-1){0f}/pV else 0f)
    }
    private fun classifyType(h: List<Float>, thd: Float, d: DriftResult, o: OscillatorCountResult, w: String): String {
        val isA=d.isAnalogBehavior; val cx=o.count>1||thd>0.3f; return when{isA&&o.count>=3->"Multi-VCO Analog";isA&&o.count==2->"Dual-VCO Analog";isA->"Single VCO Analog";cx->"Multi-Osc Digital";w=="Sine"->"Pure Osc";w=="Saw"->"Rich Osc";else->"Standard"}
    }
    private fun pitchVar(): Float { val n=minOf(frames,32); if(n<4)return 0f; val fs=mutableListOf<Float>(); for(j in 0 until n){val f=freqH[(hIdx-n+j+HIST)%HIST]; if(f in 20f..20000f&&f.isFinite())fs.add(f)}; if(fs.size<3)return 0f; val m=fs.average().toFloat(); if(m<=0)return 0f; var vs=0f; for(f in fs){val c=1200f*log2(f/m); vs+=c*c}; return sqrt(vs/fs.size) }
    private fun calcStability(): Float { val n=minOf(frames,64); if(n<8)return 0.5f; var aS=0f;var aM=0f; for(j in 0 until n){val v=ampH[(hIdx-n+j+HIST)%HIST];aM+=v;aS+=v*v}; aM/=n; val aStd=sqrt(aS/n-aM*aM); val aStab=if(aM>0.001f)1f-(aStd/aM).coerceIn(0f,1f) else 1f; val pStab=1f-(pitchVar()/30f).coerceIn(0f,1f); return (aStab*0.4f+pStab*0.6f).coerceIn(0f,1f) }
    private fun peakIn(s: FloatArray, c: Int, r: Int): Float { var p=0f; for(i in maxOf(0,c-r)..minOf(s.size-1,c+r)) if(s[i]>p)p=s[i]; return p }
    fun reset() { ampH.fill(0f); freqH.fill(0f); brightH.fill(0f); hIdx=0; frames=0 }
}
data class OscillatorPatternResult(val lfo: LFOResult=LFOResult(), val amModulation: AMResult=AMResult(), val fmModulation: FMResult=FMResult(), val drift: DriftResult=DriftResult(), val activeOscillators: OscillatorCountResult=OscillatorCountResult(), val envelope: EnvelopeResult=EnvelopeResult(), val modulationDepth: Float=0f, val oscillatorType: String="Unknown", val isModulated: Boolean=false, val stability: Float=0.5f)
data class LFOResult(val isDetected: Boolean=false, val frequency: Float=0f, val depth: Float=0f, val type: String="None", val waveform: String="None", val rateHz: Float=0f)
data class AMResult(val isDetected: Boolean=false, val modulationIndex: Float=0f, val depth: Float=0f, val rate: Float=0f, val type: String="None")
data class FMResult(val isDetected: Boolean=false, val sidebandRatio: Float=0f, val modulationIndex: Float=0f, val type: String="None")
data class DriftResult(val isDetected: Boolean=false, val driftCents: Float=0f, val type: String="Stable", val isAnalogBehavior: Boolean=false, val stabilityScore: Float=1f)
data class OscillatorCountResult(val count: Int=1, val isBeating: Boolean=false, val configuration: String="Single Oscillator")
data class EnvelopeResult(val isDetected: Boolean=false, val currentPhase: String="Unknown", val shape: String="Unknown", val attackTimeMs: Float=0f, val sustainLevel: Float=0f)
