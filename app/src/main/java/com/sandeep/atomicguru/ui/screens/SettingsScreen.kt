package com.sandeep.atomicguru.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.sandeep.atomicguru.R
import com.sandeep.atomicguru.data.UserPreferences
import com.sandeep.atomicguru.viewmodel.Language
import com.sandeep.atomicguru.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: MainViewModel) {
    val userPreferences = UserPreferences(LocalContext.current)
    val state by viewModel.state.collectAsState()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- LANGUAGE SECTION ---
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            SettingsLanguageButton(
                text = stringResource(R.string.language_odia),
                svgFileName = "in.svg",
                isSelected = state.currentLanguage == Language.OD,
                onClick = { viewModel.setLanguage(Language.OD, userPreferences) }
            )
            SettingsLanguageButton(
                text = stringResource(R.string.language_english),
                svgFileName = "us.svg",
                isSelected = state.currentLanguage == Language.EN,
                onClick = { viewModel.setLanguage(Language.EN, userPreferences) }
            )

            // --- LINKS SECTION ---
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.more_apps_and_links),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            // Using the updated LinkButton with built-in icons
            LinkButton(
                text = stringResource(R.string.download_ganita_bingya),
                icon = Icons.Default.Download, // Built-in icon
                onClick = { uriHandler.openUri("https://ganitabingya.netlify.app/open") }
            )
            LinkButton(
                text = stringResource(R.string.play_online_games),
                icon = Icons.Default.Games, // Built-in icon
                onClick = { uriHandler.openUri("https://odiagames.netlify.app/?lang=odia") }
            )
            LinkButton(
                text = stringResource(R.string.follow_on_instagram),
                icon = Icons.Default.PersonPin, // Good alternative for a "follow me" action
                onClick = { uriHandler.openUri("https://www.instagram.com/sandeepbiswalg/") }
            )
        }
    }
}

@Composable
private fun SettingsLanguageButton(
    // ... This composable remains unchanged ...
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
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), label = "bgColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground, label = "contentColor"
    )
    val border = if(isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor, contentColor = contentColor),
        border = border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}


/**
 * A reusable Composable for the link buttons, now using ImageVector for built-in icons.
 */
@Composable
private fun LinkButton(
    text: String,
    icon: ImageVector, // Changed from @DrawableRes iconRes: Int
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon, // Changed from painterResource
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}