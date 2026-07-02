package com.example.myapplication.data.local

import com.example.myapplication.data.*

fun DetectedSynthEntity.toDomain() = DetectedSynth(
    id = id,
    name = name,
    brand = brand,
    category = category,
    detectedAt = detectedAt,
    confidence = confidence,
    frequencySignature = frequencySignature,
    waveformType = waveformType,
    octaveRange = octaveRange,
    filterType = filterType,
    extraInfo = extraInfo,
    isFavorite = isFavorite
)

fun DetectedSynth.toEntity() = DetectedSynthEntity(
    id = id,
    name = name,
    brand = brand,
    category = category,
    detectedAt = detectedAt,
    confidence = confidence,
    frequencySignature = frequencySignature,
    waveformType = waveformType,
    octaveRange = octaveRange,
    filterType = filterType,
    extraInfo = extraInfo,
    isFavorite = isFavorite
)

fun SynthLibraryEntity.toDomain() = SynthLibraryItem(
    id = id,
    name = name,
    brand = brand,
    category = category,
    yearReleased = yearReleased,
    yearDiscontinued = yearDiscontinued,
    description = description,
    waveformTypes = waveformTypes,
    filterTypes = filterTypes,
    polyphony = polyphony,
    oscillators = oscillators,
    notableFeatures = notableFeatures,
    frequencySignature = frequencySignature,
    isDetected = isDetected,
    detectionCount = detectionCount,
    imageUrl = imageUrl,
    purchaseUrl = purchaseUrl,
    priceRange = priceRange,
    officialSite = officialSite,
    soundDemos = soundDemos,
    studioUse = studioUse,
    famousUsers = famousUsers,
    pros = pros,
    cons = cons,
    alternatives = alternatives,
    signalChain = signalChain,
    powerType = powerType,
    dimensions = dimensions,
    weight = weight,
    connectivity = connectivity,
    presets = presets,
    genre = genre,
    countryOfOrigin = countryOfOrigin,
    keyboardType = keyboardType,
    bestFor = bestFor,
    soundCharacter = soundCharacter,
    isClone = isClone,
    clones = clones
)

fun SynthLibraryItem.toEntity() = SynthLibraryEntity(
    id = id,
    name = name,
    brand = brand,
    category = category,
    yearReleased = yearReleased,
    yearDiscontinued = yearDiscontinued,
    description = description,
    waveformTypes = waveformTypes,
    filterTypes = filterTypes,
    polyphony = polyphony,
    oscillators = oscillators,
    notableFeatures = notableFeatures,
    frequencySignature = frequencySignature,
    isDetected = isDetected,
    detectionCount = detectionCount,
    imageUrl = imageUrl,
    purchaseUrl = purchaseUrl,
    priceRange = priceRange,
    officialSite = officialSite,
    soundDemos = soundDemos,
    studioUse = studioUse,
    famousUsers = famousUsers,
    pros = pros,
    cons = cons,
    alternatives = alternatives,
    signalChain = signalChain,
    powerType = powerType,
    dimensions = dimensions,
    weight = weight,
    connectivity = connectivity,
    presets = presets,
    genre = genre,
    countryOfOrigin = countryOfOrigin,
    keyboardType = keyboardType,
    bestFor = bestFor,
    soundCharacter = soundCharacter,
    isClone = isClone,
    clones = clones
)

fun DetectionHistoryEntity.toDomain() = DetectionHistory(
    id = id,
    synthName = synthName,
    brand = brand,
    category = category,
    confidence = confidence,
    waveformType = waveformType,
    frequencyHz = frequencyHz,
    octave = octave,
    stemBreakdown = stemBreakdown,
    detectedAt = detectedAt,
    durationMs = durationMs,
    audioFingerprint = audioFingerprint,
    isFavorite = isFavorite,
    notes = notes
)

fun DetectionHistory.toEntity() = DetectionHistoryEntity(
    id = id,
    synthName = synthName,
    brand = brand,
    category = category,
    confidence = confidence,
    waveformType = waveformType,
    frequencyHz = frequencyHz,
    octave = octave,
    stemBreakdown = stemBreakdown,
    detectedAt = detectedAt,
    durationMs = durationMs,
    audioFingerprint = audioFingerprint,
    isFavorite = isFavorite,
    notes = notes
)

fun AudioRecordingEntity.toDomain() = AudioRecording(
    id = id,
    filePath = filePath,
    durationMs = durationMs,
    sampleRate = sampleRate,
    detectedSynth = detectedSynth,
    confidence = confidence,
    createdAt = createdAt,
    title = title,
    tags = tags
)

fun AudioRecording.toEntity() = AudioRecordingEntity(
    id = id,
    filePath = filePath,
    durationMs = durationMs,
    sampleRate = sampleRate,
    detectedSynth = detectedSynth,
    confidence = confidence,
    createdAt = createdAt,
    title = title,
    tags = tags
)

fun ABComparisonEntity.toDomain() = ABComparison(
    id = id,
    synthAName = synthAName,
    synthABrand = synthABrand,
    synthAWaveform = synthAWaveform,
    synthAFrequency = synthAFrequency,
    synthAConfidence = synthAConfidence,
    synthBName = synthBName,
    synthBBrand = synthBBrand,
    synthBWaveform = synthBWaveform,
    synthBFrequency = synthBFrequency,
    synthBConfidence = synthBConfidence,
    createdAt = createdAt,
    notes = notes
)

fun ABComparison.toEntity() = ABComparisonEntity(
    id = id,
    synthAName = synthAName,
    synthABrand = synthABrand,
    synthAWaveform = synthAWaveform,
    synthAFrequency = synthAFrequency,
    synthAConfidence = synthAConfidence,
    synthBName = synthBName,
    synthBBrand = synthBBrand,
    synthBWaveform = synthBWaveform,
    synthBFrequency = synthBFrequency,
    synthBConfidence = synthBConfidence,
    createdAt = createdAt,
    notes = notes
)
