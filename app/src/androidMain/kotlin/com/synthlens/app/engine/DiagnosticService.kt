package com.synthlens.app.engine

import android.util.Log

object DiagnosticService {
    private const val TAG = "SynthLensDiag"

    fun logFeatures(feat: FloatArray) {
        // Log solo algunas features clave para no saturar Logcat
        val sb = StringBuilder()
        sb.append("MFCC[0]: ${feat[0]}, ")
        sb.append("Centroid: ${feat[39]}, ")
        sb.append("HNR: ${feat[42]}, ")
        sb.append("ZCR: ${feat[41]}")
        Log.d(TAG, "Features: $sb")
    }
}
