package com.sandeep.atomicguru

import android.content.Intent
import android.net.Uri
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
import androidx.core.app.TaskStackBuilder
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

        if (handleIntent(intent)) {
            finish()
            return
        }

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

                viewModel.setLanguage(userPreferences.getLanguage(), userPreferences)
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
                        // --- THIS IS THE FIX ---
                        // The `deepLinkRoute` parameter has been removed from this call.
                        AppNavigation(
                            viewModel = viewModel,
                            startDestination = startDestination,
                            userPreferences = userPreferences
                        )
                        // --- END OF FIX ---
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent): Boolean {
        val repository = ElementRepository(applicationContext)
        val data: Uri? = intent.data
        var targetIntent: Intent? = null

        if (data != null && data.host == "atomicguru.netlify.app" && data.path == "/open") {
            val symbol = data.queryParameterNames.firstOrNull()

            if (symbol != null) {
                val element = repository.allElements.find { it.symbol.equals(symbol, ignoreCase = true) }
                if (element != null) {
                    // Use a custom scheme for the internal deep link URI
                    targetIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("atomicguru://detail/${element.atomicNumber}"),
                        this,
                        MainActivity::class.java
                    )
                }
            }
        }

        if (targetIntent != null) {
            TaskStackBuilder.create(this).run {
                addNextIntent(Intent(this@MainActivity, MainActivity::class.java))
                addNextIntent(targetIntent)
                startActivities()
            }
            return true
        }
        return false
    }
}