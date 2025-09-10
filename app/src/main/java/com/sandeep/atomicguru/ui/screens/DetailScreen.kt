package com.sandeep.atomicguru.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sandeep.atomicguru.R
import com.sandeep.atomicguru.data.Element
import com.sandeep.atomicguru.data.Promotion
import com.sandeep.atomicguru.ui.components.GlassmorphicCard
import com.sandeep.atomicguru.ui.components.InfoRow
import com.sandeep.atomicguru.viewmodel.Language
import com.sandeep.atomicguru.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, viewModel: MainViewModel, atomicNumber: Int) {
    val element = viewModel.getElementByNumber(atomicNumber)
    val state by viewModel.state.collectAsState()
    val isFavorite = state.favoriteIds.contains(atomicNumber.toString())
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val promotion = state.promotionToShow

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(element?.name ?: stringResource(id = R.string.element_details)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (element != null) {
                            shareElement(context, element)
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = stringResource(R.string.share))
                    }
                    IconButton(onClick = {
                        viewModel.toggleFavorite(atomicNumber)
                        val message = if (!isFavorite) {
                            context.getString(R.string.added_to_favorites, element?.name)
                        } else {
                            context.getString(R.string.removed_from_favorites, element?.name)
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = stringResource(R.string.bookmark)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (element != null) {
            val details = if (state.currentLanguage == Language.EN) element.detailsEn else element.detailsOdia

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Header Card
                GlassmorphicCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(details.generalInfo.symbol, style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.width(24.dp))
                        Column {
                            Text(details.generalInfo.elementName, style = MaterialTheme.typography.headlineMedium)
                            Text(details.generalInfo.category, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Detailed Description
                DetailSectionCard(title = stringResource(R.string.about)) {
                    Text(details.detailedDescription, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
                }

                // General Info Section
                DetailSectionCard(title = stringResource(R.string.general_information)) {
                    InfoRow(label = stringResource(R.string.atomic_mass), value = details.generalInfo.atomicMass)
                    InfoRow(label = stringResource(R.string.group_period), value = details.generalInfo.groupPeriod)
                    InfoRow(label = stringResource(R.string.appearance), value = details.generalInfo.appearance)
                }

                // Physical Properties Section
                DetailSectionCard(title = stringResource(R.string.physical_properties)) {
                    InfoRow(label = stringResource(R.string.melting_point), value = details.physicalProperties.meltingPoint)
                    InfoRow(label = stringResource(R.string.boiling_point), value = details.physicalProperties.boilingPoint)
                    InfoRow(label = stringResource(R.string.density), value = details.physicalProperties.density)
                    InfoRow(label = stringResource(R.string.conductivity), value = details.physicalProperties.conductivity)
                }

                // Chemical Properties, Occurrence, and Uses (Lists)
                BulletedListSection(title = stringResource(R.string.chemical_properties), items = details.chemicalProperties)
                BulletedListSection(title = stringResource(R.string.occurrence), items = details.occurrence)
                BulletedListSection(title = stringResource(R.string.uses), items = details.uses)

                // Wikipedia Button
                Button(
                    onClick = { uriHandler.openUri(element.source) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.learn_more_wiki), color = MaterialTheme.colorScheme.onSurface)
                }

                // Promotion Section
                Spacer(modifier = Modifier.height(8.dp))
                PromotionSection(
                    promotion = promotion,
                    onClick = { url -> uriHandler.openUri(url) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * A helper function to create and launch the Android Sharesheet intent.
 */
private fun shareElement(context: Context, element: Element) {
    val shareText = "Check out ${element.name} (${element.symbol}) on Atomic Guru! " +
            "Learn more here: https://atomicguru.netlify.app/open?${element.symbol}"

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Learn about ${element.name}")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    ContextCompat.startActivity(
        context,
        Intent.createChooser(intent, "Share ${element.name} via..."),
        null
    )
}

/**
 * A smart Composable that displays the fetched promotional image if available,
 * otherwise, it shows a static placeholder.
 */
@Composable
private fun PromotionSection(promotion: Promotion?, onClick: (String) -> Unit) {
    if (promotion != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(promotion.destinationUrl) },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AsyncImage(
                model = promotion.imageUrl,
                contentDescription = promotion.altText,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Space for Ad",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * A reusable Composable that creates a titled section within a GlassmorphicCard.
 */
@Composable
private fun DetailSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
            color = MaterialTheme.colorScheme.primary
        )
        GlassmorphicCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content
            )
        }
    }
}

/**
 * A reusable Composable that displays a list of strings as a bulleted list inside a DetailSectionCard.
 */
@Composable
private fun BulletedListSection(title: String, items: List<String>) {
    if (items.isNotEmpty()) {
        DetailSectionCard(title = title) {
            items.forEach { item ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("•  ", color = MaterialTheme.colorScheme.primary)
                    Text(item, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}