package com.sandeep.atomicguru.ui.screens

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import com.sandeep.atomicguru.R
import com.sandeep.atomicguru.data.Element
import com.sandeep.atomicguru.navigation.Screen
import com.sandeep.atomicguru.ui.components.ElementPreviewDialog
import com.sandeep.atomicguru.ui.components.GlassmorphicCard
import com.sandeep.atomicguru.viewmodel.MainViewModel
import com.sandeep.atomicguru.viewmodel.TableView

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current
    var previewedElement by remember { mutableStateOf<Element?>(null) }

    LaunchedEffect(isLandscape) {
        val window = (context as Activity).window
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (isLandscape) {
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                if (!isLandscape) {
                    TopAppBar(
                        title = { Text(stringResource(id = R.string.app_name)) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        actions = {
                            // --- THIS IS THE FIX ---
                            // The IconButton for toggling the view has been completely removed.
                            // --- END OF FIX ---
                            IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                                Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                if (!isLandscape) {
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.Favorites.route) },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = stringResource(R.string.favorites))
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(if (isLandscape) 0.dp else 16.dp)
                    .fillMaxSize()
            ) {
                // --- THIS IS THE FIX ---
                // The main content area now checks if it's landscape.
                // If it is, show the Classic View.
                // If not, ALWAYS show the Grid View.
                if (isLandscape) {
                    ClassicPeriodicTableView(
                        elements = state.filteredElements,
                        navController = navController,
                        viewModel = viewModel
                    )
                } else {
                    // Search Bar is now inside the portrait-only view
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        label = { Text(stringResource(id = R.string.search_placeholder)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 85.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.filteredElements, key = { it.atomicNumber }) { element ->
                            ElementGridItem(
                                element = element,
                                onPreviewChange = { shouldShow ->
                                    previewedElement = if (shouldShow) element else null
                                },
                                onClick = {
                                    navController.navigate(Screen.Detail.createRoute(element.atomicNumber))
                                },
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                }
                // --- END OF FIX ---
            }
        }

        if (previewedElement != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .pointerInput(Unit) { detectTapGestures { previewedElement = null } }
            )
            ElementPreviewDialog(
                element = previewedElement!!,
                viewModel = viewModel,
                onDismiss = { previewedElement = null }
            )
        }
    }
}


@Composable
fun ElementGridItem(
    element: Element,
    onPreviewChange: (shouldShow: Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gestureModifier = modifier
        .pointerInput(element) {
            detectTapGestures(
                onLongPress = {
                    onPreviewChange(true)
                },
                onTap = {
                    onClick()
                },
                onPress = {
                    awaitRelease()
                    onPreviewChange(false)
                }
            )
        }

    GlassmorphicCard(
        modifier = gestureModifier.aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = element.atomicNumber.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = element.symbol,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = element.name,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}