package com.sandeep.atomicguru.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sandeep.atomicguru.R

val AnekOdia = FontFamily(
    Font(R.font.anekodia_semiexpanded_bold, FontWeight.Normal),
    Font(R.font.anekodia_semiexpanded_bold, FontWeight.Bold)
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = AnekOdia, fontWeight = FontWeight.Bold, fontSize = 57.sp),
    displaySmall = TextStyle(fontFamily = AnekOdia, fontWeight = FontWeight.Bold, fontSize = 36.sp),
    headlineMedium = TextStyle(fontFamily = AnekOdia, fontWeight = FontWeight.Bold, fontSize = 28.sp),
    // --- THIS IS THE FIX ---
    // This is the style used by TopAppBar titles
    titleLarge = TextStyle(
        fontFamily = AnekOdia,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    // --- END OF FIX ---
    titleMedium = TextStyle(fontFamily = AnekOdia, fontWeight = FontWeight.Bold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = AnekOdia, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodySmall = TextStyle(fontFamily = AnekOdia, fontWeight = FontWeight.Normal, fontSize = 12.sp)
)