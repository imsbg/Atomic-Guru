package com.sandeep.atomicguru.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sandeep.atomicguru.R
import com.sandeep.atomicguru.data.Element
import com.sandeep.atomicguru.navigation.Screen
import com.sandeep.atomicguru.ui.components.GlassmorphicCard
import com.sandeep.atomicguru.viewmodel.Language
import com.sandeep.atomicguru.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, viewModel: MainViewModel) {
    val favoriteElements = viewModel.getFavoriteElements()
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_favorites)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (favoriteElements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_favorites),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteElements, key = { it.atomicNumber }) { element ->
                    FavoriteListItem(element = element, language = state.currentLanguage) {
                        navController.navigate(Screen.Detail.createRoute(element.atomicNumber))
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteListItem(element: Element, language: Language, onClick: () -> Unit) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = element.symbol,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                // --- UI ENHANCEMENT ---
                // Determine primary and secondary names based on current language
                val primaryName = if (language == Language.EN) element.name else element.detailsOdia.generalInfo.elementName
                val secondaryName = if (language == Language.EN) element.detailsOdia.generalInfo.elementName else element.name

                Text(primaryName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    secondaryName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalContentColor.current.copy(alpha = 0.7f)
                )
                // --- END OF ENHANCEMENT ---
            }
        }
    }
}