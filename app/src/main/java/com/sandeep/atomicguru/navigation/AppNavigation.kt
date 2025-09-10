package com.sandeep.atomicguru.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.sandeep.atomicguru.data.UserPreferences
import com.sandeep.atomicguru.ui.screens.*
import com.sandeep.atomicguru.viewmodel.MainViewModel

@Composable
fun AppNavigation(
    viewModel: MainViewModel,
    startDestination: String,
    userPreferences: UserPreferences
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it }) },
        exitTransition = { slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { -it }) },
        popEnterTransition = { slideInHorizontally(animationSpec = tween(300), initialOffsetX = { -it }) },
        popExitTransition = { slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it }) }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.LanguageSelection.route) {
            LanguageSelectionScreen(
                navController = navController,
                viewModel = viewModel,
                userPreferences = userPreferences
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("atomicNumber") { type = NavType.IntType }),
            // This tells the navigation graph that this screen can be opened via a deep link.
            // The actual URI parsing and intent handling is done in MainActivity.
            deepLinks = listOf(navDeepLink { uriPattern = "atomicguru://detail/{atomicNumber}" })
        ) { backStackEntry ->
            val atomicNumber = backStackEntry.arguments?.getInt("atomicNumber")
            if (atomicNumber != null) {
                DetailScreen(
                    navController = navController,
                    viewModel = viewModel,
                    atomicNumber = atomicNumber
                )
            }
        }
    }
}