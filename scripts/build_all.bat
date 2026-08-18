@echo off
echo ========================================================
echo Compiling SynthLens Android Application
echo ========================================================

echo 1. Cleaning project...
call gradlew clean

echo 2. Building Release APK...
call gradlew assembleRelease

echo 3. Building Release AAB (Android App Bundle)...
call gradlew bundleRelease

echo 4. Creating Output Directories...
mkdir build_outputs\Android 2>nul
mkdir build_outputs\Windows 2>nul
mkdir build_outputs\iOS_Mac 2>nul
mkdir build_outputs\Linux 2>nul

echo 5. Copying Android Artifacts...
copy /Y app\build\outputs\apk\release\app-release.apk build_outputs\Android\SynthLens.apk
copy /Y app\build\outputs\bundle\release\app-release.aab build_outputs\Android\SynthLens.aab

echo ========================================================
echo Build Complete!
echo Android files are in build_outputs\Android
echo Note: Windows, Mac, and Linux builds require migrating
echo this codebase to Kotlin Multiplatform (KMP) or similar.
echo ========================================================
