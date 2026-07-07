package com.synthlens.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.synthlens.app.data.*
import com.synthlens.app.data.SynthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SynthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SynthRepository.getInstance(application)

    private val _allSynths = MutableStateFlow<List<SynthLibraryItem>>(emptyList())
    val allSynths: StateFlow<List<SynthLibraryItem>> = _allSynths.asStateFlow()

    private val _detectedSynths = MutableStateFlow<List<DetectedSynth>>(emptyList())
    val detectedSynths: StateFlow<List<DetectedSynth>> = _detectedSynths.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("ALL")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _selectedBrand = MutableStateFlow<String?>(null)
    val selectedBrand: StateFlow<String?> = _selectedBrand.asStateFlow()

    private val _filteredSynths = MutableStateFlow<List<SynthLibraryItem>>(emptyList())
    val filteredSynths: StateFlow<List<SynthLibraryItem>> = _filteredSynths.asStateFlow()

    private val _history = MutableStateFlow<List<DetectionHistory>>(emptyList())
    val history: StateFlow<List<DetectionHistory>> = _history.asStateFlow()

    private val _recordings = MutableStateFlow<List<AudioRecording>>(emptyList())
    val recordings: StateFlow<List<AudioRecording>> = _recordings.asStateFlow()

    private val _comparisons = MutableStateFlow<List<ABComparison>>(emptyList())
    val comparisons: StateFlow<List<ABComparison>> = _comparisons.asStateFlow()

    private val _achievements = MutableStateFlow<List<com.synthlens.app.data.local.AchievementEntity>>(emptyList())
    val achievements: StateFlow<List<com.synthlens.app.data.local.AchievementEntity>> = _achievements.asStateFlow()

    private val _unlockedCount = MutableStateFlow(0)
    val unlockedCount: StateFlow<Int> = _unlockedCount.asStateFlow()

    private val _totalCount = MutableStateFlow(0)
    val totalAchievementsCount: StateFlow<Int> = _totalCount.asStateFlow()

    val detectedCount: StateFlow<Int> = repository.detectedCount

    val totalCount: StateFlow<Int> = repository.totalCount

    init {
        loadSynths()
        loadHistory()
        loadRecordings()
        loadComparisons()
        loadAchievements()
    }

    private fun loadSynths() {
        viewModelScope.launch {
            repository.allSynths.collect { synths ->
                _allSynths.value = synths
                applyFilters()
            }
        }
        viewModelScope.launch {
            repository.detectedSynths.collect { synths ->
                _detectedSynths.value = synths
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            repository.allHistory.collect { entries ->
                _history.value = entries
            }
        }
    }

    private fun loadRecordings() {
        viewModelScope.launch {
            repository.allRecordings.collect { recs ->
                _recordings.value = recs
            }
        }
    }

    private fun loadComparisons() {
        viewModelScope.launch {
            repository.allComparisons.collect { comps ->
                _comparisons.value = comps
            }
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            repository.getAllAchievements().collect { achievements ->
                _achievements.value = achievements
                _unlockedCount.value = achievements.count { it.isUnlocked }
                _totalCount.value = achievements.size
            }
        }
    }

    fun updateAchievementProgress(id: String, progress: Int) {
        viewModelScope.launch {
            repository.updateAchievementProgress(id, progress)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun updateFilter(filter: String) {
        _selectedFilter.value = filter
        if (filter != "BRAND") {
            _selectedBrand.value = null
        }
        applyFilters()
    }

    fun updateBrand(brand: String?) {
        _selectedBrand.value = brand
        applyFilters()
    }

    private fun applyFilters() {
        val query = _searchQuery.value
        val filter = _selectedFilter.value
        val brand = _selectedBrand.value

        val filtered = _allSynths.value.filter { synth ->
            val matchesSearch = query.isEmpty() ||
                    synth.name.contains(query, ignoreCase = true) ||
                    synth.brand.contains(query, ignoreCase = true) ||
                    synth.category.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "DETECTED" -> synth.isDetected
                "ANALOG" -> synth.category.contains("Analog", ignoreCase = true)
                "DIGITAL" -> synth.category.contains("Digital", ignoreCase = true) || synth.category.contains("Hybrid", ignoreCase = true)
                "MODULAR" -> synth.category.contains("Modular", ignoreCase = true) || synth.category.contains("Semi", ignoreCase = true)
                else -> true
            }

            val matchesBrand = brand == null || synth.brand == brand

            matchesSearch && matchesFilter && matchesBrand
        }

        _filteredSynths.value = filtered
    }

    fun addDetectedSynth(synth: DetectedSynth) {
        repository.addDetectedSynth(synth)
        repository.addDetectionHistory(
            DetectionHistory(
                synthName = "${synth.brand} ${synth.name}",
                brand = synth.brand,
                category = synth.category,
                confidence = synth.confidence,
                waveformType = synth.waveformType
            )
        )
    }

    fun markAsDetected(brand: String, name: String) {
        repository.markAsDetected(brand, name)
    }

    fun toggleHistoryFavorite(id: Long, isFavorite: Boolean) {
        repository.toggleHistoryFavorite(id, isFavorite)
    }

    fun deleteHistoryEntry(entry: DetectionHistory) {
        repository.deleteHistoryEntry(entry)
    }

    fun addRecording(recording: AudioRecording) {
        repository.addRecording(recording)
    }

    fun deleteRecording(id: Long) {
        repository.deleteRecording(id)
    }

    fun addComparison(comparison: ABComparison) {
        repository.addComparison(comparison)
    }

    fun deleteComparison(comparison: ABComparison) {
        repository.deleteComparison(comparison)
    }

    fun getSynthsByBrand(brand: String): Flow<List<SynthLibraryItem>> {
        return repository.getSynthsByBrand(brand)
    }

    fun searchSynths(query: String): Flow<List<SynthLibraryItem>> {
        return repository.searchSynths(query)
    }

    fun getDetectedSynthItems(): Flow<List<SynthLibraryItem>> {
        return repository.getDetectedSynthItems()
    }

    fun getBrands(): List<String> {
        return _allSynths.value.map { it.brand }.distinct().sorted()
    }

    fun searchHistory(query: String): Flow<List<DetectionHistory>> {
        return repository.searchHistory(query)
    }
}
