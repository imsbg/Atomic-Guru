package com.sandeep.atomicguru.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.sandeep.atomicguru.R
import com.sandeep.atomicguru.data.UserPreferences
import com.sandeep.atomicguru.navigation.Screen
import com.sandeep.atomicguru.ui.theme.AnekOdia
import com.sandeep.atomicguru.viewmodel.Language
import com.sandeep.atomicguru.viewmodel.MainViewModel

@Composable
fun LanguageSelectionScreen(
    navController: NavController,
    viewModel: MainViewModel,
    userPreferences: UserPreferences
) {
    var selectedLanguage by remember { mutableStateOf(Language.OD) }

    // This still updates the language in the background so the text changes
    LaunchedEffect(selectedLanguage) {
        // --- THIS IS THE FIX ---
        // We now pass both the language and the userPreferences object
        viewModel.setLanguage(selectedLanguage, userPreferences)
        // --- END OF FIX ---
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.welcome),
                    style = MaterialTheme.typography.displaySmall
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.select_language),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(48.dp))

                LanguageButton(
                    text = stringResource(R.string.language_odia),
                    svgFileName = "in.svg",
                    isSelected = selectedLanguage == Language.OD,
                    onClick = { selectedLanguage = Language.OD }
                )
                Spacer(Modifier.height(16.dp))
                LanguageButton(
                    text = stringResource(R.string.language_english),
                    svgFileName = "us.svg",
                    isSelected = selectedLanguage == Language.EN,
                    onClick = { selectedLanguage = Language.EN }
                )
            }

            Button(
                onClick = {
                    // This is where the final choice is saved.
                    // The LaunchedEffect just updates the preview on this screen.
                    userPreferences.saveLanguage(selectedLanguage)
                    userPreferences.setFirstLaunchCompleted()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    stringResource(R.string.lets_start),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun LanguageButton(
    text: String,
    svgFileName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/$svgFileName")
            .decoderFactory(SvgDecoder.Factory())
            .build()
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, label = "bgColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground, label = "contentColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, label = "borderColor"
    )

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}