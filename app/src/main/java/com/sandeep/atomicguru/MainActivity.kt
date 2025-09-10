package com.sandeep.atomicguru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.sandeep.atomicguru.data.ElementRepository
import com.sandeep.atomicguru.data.UserPreferences
import com.sandeep.atomicguru.navigation.AppNavigation
import com.sandeep.atomicguru.navigation.Screen
import com.sandeep.atomicguru.ui.LanguageController
import com.sandeep.atomicguru.ui.theme.AtomicGuruTheme
import com.sandeep.atomicguru.viewmodel.MainViewModel
import com.sandeep.atomicguru.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AtomicGuruTheme {
                val context = LocalContext.current
                val repository = ElementRepository(context)
                val viewModelFactory = ViewModelFactory(repository)
                val viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)

                val userPreferences = UserPreferences(context)
                val isFirstLaunch = userPreferences.isFirstLaunch()

                val startDestination = if (isFirstLaunch) {
                    Screen.LanguageSelection.route
                } else {
                    Screen.Splash.route
                }

                // --- THIS IS THE FIX ---
                // We now pass both the language AND the userPreferences object to the function.
                viewModel.setLanguage(
                    newLanguage = userPreferences.getLanguage(),
                    userPreferences = userPreferences
                )
                // --- END OF FIX ---

                val state by viewModel.state.collectAsState()
                val backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                )

                LanguageController(selectedLanguage = state.currentLanguage) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundBrush)
                    ) {
                        AppNavigation(
                            viewModel = viewModel,
                            startDestination = startDestination,
                            userPreferences = userPreferences
                        )
                    }
                }
            }
        }
    }
}