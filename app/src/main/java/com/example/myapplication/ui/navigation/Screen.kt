package com.example.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object Analyzer : Screen("analyzer")
    object Scanner : Screen("scanner")
    object Spectral : Screen("spectral")
    object Library : Screen("library")
    object Profile : Screen("profile")
    object AnalysisDetails : Screen("analysis_details")
    object History : Screen("history")
    object ABCompare : Screen("ab_compare")
    object Export : Screen("export")
    object StageMode : Screen("stage_mode")
    object DAWIntegration : Screen("daw_integration")
    object Settings : Screen("settings")
    object Achievements : Screen("achievements")
}
