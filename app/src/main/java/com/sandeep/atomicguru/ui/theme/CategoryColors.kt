package com.sandeep.atomicguru.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * A centralized, shared function to get the color for a specific element category.
 * This can now be used by any part of the app.
 */
@Composable
fun getCategoryColor(category: String): Color {
    val lowerCategory = category.lowercase()
    return when {
        lowerCategory.contains("alkali metal") -> Color(0xFFA0C4FF)
        lowerCategory.contains("alkaline earth metal") -> Color(0xFFFFADAD)
        lowerCategory.contains("lanthanide") -> Color(0xFFD4F0C9)
        lowerCategory.contains("actinide") -> Color(0xFFFFB3A7)
        lowerCategory.contains("transition metal") -> Color(0xFFE0BBE4)
        lowerCategory.contains("post-transition metal") -> Color(0xFFB5EAD7)
        lowerCategory.contains("metalloid") -> Color(0xFFFFD6A5)
        lowerCategory.contains("noble gas") -> Color(0xFFFFC6FF)
        lowerCategory.contains("nonmetal") || lowerCategory.contains("halogen") -> Color(0xFFC9F0FF)
        lowerCategory.contains("unknown") -> Color(0xFFE0E0E0)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}