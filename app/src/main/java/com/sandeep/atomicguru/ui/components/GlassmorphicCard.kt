package com.sandeep.atomicguru.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sandeep.atomicguru.ui.theme.GlassmorphismDark
import com.sandeep.atomicguru.ui.theme.GlassmorphismLight

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val glassColor = if (isSystemInDarkTheme()) GlassmorphismDark else GlassmorphismLight
    val borderColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(glassColor)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
    ) {
        content()
    }
}