# SynthLens

**AI-powered synthesizer analyzer for Android.**

SynthLens listens to audio input and identifies synthesizers, analyzing their oscillators, filters, modulation, and patch settings in real-time.

## Features

- **Real-time Audio Analysis** - Analyze audio from microphone or system input
- **Synth Detection** - Identify synthesizer models with high confidence
- **Parameter Extraction** - Detect oscillators, filters, envelopes, LFOs
- **Patch Database** - Save and recall analyzed patches
- **Spectrum Analyzer** - Visual frequency analysis
- **Dark Theme** - Modern synth-rack aesthetic UI

## Screenshots

*(Add screenshots here)*

## Installation

### From Source

```bash
# Clone the repository
git clone https://github.com/dixi3stdgdl-design/synthlens.git

# Open in Android Studio
# Build and run on device or emulator
```

### APK Download

Download the latest APK from [Releases](https://github.com/dixi3stdgdl-design/synthlens/releases)

## Requirements

- Android 8.0+ (API 26)
- Microphone permission for audio input
- 2GB+ RAM recommended

## Architecture

```
app/
├── src/main/java/com/synthlens/app/
│   ├── MainActivity.kt          # Entry point
│   ├── engine/                  # Audio engine, FFT, ML classifier
│   ├── data/                    # Database, entities, repository
│   ├── viewmodel/               # MVVM view models
│   ├── ui/
│   │   ├── screens/             # All 12+ screens
│   │   ├── components/          # Reusable UI components
│   │   ├── navigation/          # Navigation routes
│   │   └── theme/               # Colors, typography, theme
│   └── ...
├── src/main/res/
│   ├── layout/                  # XML layouts
│   ├── values/                  # Colors, strings, themes
│   └── drawable/                # Icons and graphics
└── build.gradle.kts             # Dependencies
```

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + XML
- **Audio:** Android AudioRecord API
- **ML:** On-device inference for synth detection
- **Architecture:** MVVM

## Development

### Prerequisites

- Android Studio Hedgehog+
- JDK 17
- Android SDK 34

### Build

```bash
./gradlew assembleDebug
```

### Run Tests

```bash
./gradlew test
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Android Audio API documentation
- Synthesizer parameter analysis research
- Open source audio processing libraries

## Contact

- **GitHub:** [dixi3stdgdl-design](https://github.com/dixi3stdgdl-design)

---

Built with by MiMo Team
