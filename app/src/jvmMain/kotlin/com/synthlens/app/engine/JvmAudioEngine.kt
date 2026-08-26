package com.synthlens.app.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JvmAudioEngine : AudioEngine {
    private val _analysis = MutableStateFlow(AudioAnalysis())
    override val analysis: StateFlow<AudioAnalysis> = _analysis.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    override val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    override fun startRecording() {
        _isRecording.value = true
        // Stub implementation for Desktop
    }

    override fun stopRecording() {
        _isRecording.value = false
        // Stub implementation for Desktop
    }

    override fun toggleRecording() {
        if (_isRecording.value) stopRecording() else startRecording()
    }

    override fun destroy() {
        stopRecording()
    }
}
