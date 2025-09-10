package com.sandeep.atomicguru.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.sandeep.atomicguru.viewmodel.Language
import java.util.*

// This is the key Composable that will wrap our entire app.
// It listens to the selected language and updates the app's configuration.
@Composable
fun LanguageController(
    selectedLanguage: Language,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val updatedContext = remember(selectedLanguage) {
        val locale = when (selectedLanguage) {
            Language.EN -> Locale.ENGLISH
            Language.OD -> Locale("or") // "or" is the ISO code for Odia
        }
        updateResources(context, locale)
    }
    // We provide the updated context down the composable tree, but the configuration change is what matters.
    content()
}

// Helper function to update the configuration
private fun updateResources(context: Context, locale: Locale): Context {
    Locale.setDefault(locale)
    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(locale)
    // This is the magic line that makes the app reload string resources.
    context.createConfigurationContext(configuration)
    context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    return context
}