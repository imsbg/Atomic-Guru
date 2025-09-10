package com.sandeep.atomicguru.data

import android.content.Context
import com.sandeep.atomicguru.viewmodel.Language

class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
        private const val KEY_LANGUAGE = "language"
    }

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchCompleted() {
        prefs.edit().putBoolean(KEY_IS_FIRST_LAUNCH, false).apply()
    }

    fun saveLanguage(language: Language) {
        prefs.edit().putString(KEY_LANGUAGE, language.name).apply()
    }

    fun getLanguage(): Language {
        // Default to Odia as requested
        val langName = prefs.getString(KEY_LANGUAGE, Language.OD.name)
        return Language.valueOf(langName ?: Language.OD.name)
    }
}