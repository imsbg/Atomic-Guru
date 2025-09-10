package com.sandeep.atomicguru.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash") // ADDED BACK
    object LanguageSelection : Screen("language_selection")
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
    object Detail : Screen("detail/{atomicNumber}") {
        fun createRoute(atomicNumber: Int) = "detail/$atomicNumber"
    }
}