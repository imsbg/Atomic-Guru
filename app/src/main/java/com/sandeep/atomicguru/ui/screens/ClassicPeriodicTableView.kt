package com.sandeep.atomicguru.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.sandeep.atomicguru.data.Element
import com.sandeep.atomicguru.navigation.Screen
import com.sandeep.atomicguru.ui.components.ElementGridItem
import com.sandeep.atomicguru.ui.components.ElementPreviewDialog
import com.sandeep.atomicguru.ui.theme.getCategoryColor
import com.sandeep.atomicguru.viewmodel.MainViewModel
import kotlin.math.max

// Define constants for scale limits
private const val ABSOLUTE_MIN_SCALE = 0.5f
private const val MAX_SCALE = 5f

@Composable
fun ClassicPeriodicTableView(
    elements: List<Element>,
    navController: NavController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var previewedElement by remember { mutableStateOf<Element?>(null) }

    val startPadding = 16.dp
    val endPadding = 250.dp
    val topPadding = 16.dp
    val bottomPadding = 250.dp

    // --- NEW LAYOUT: COLUMN ---
    // The screen is now a Column to hold the table and the footer.
    Column(modifier.fillMaxSize()) {
        // --- THIS BOX NOW TAKES UP THE AVAILABLE SPACE ---
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            val cellSize = 80.dp
            val tableWidth = cellSize * 18
            val tableHeight = cellSize * 10

            val density = LocalDensity.current
            val tableWidthPx = with(density) { tableWidth.toPx() }
            val tableHeightPx = with(density) { tableHeight.toPx() }
            val startPaddingPx = with(density) { startPadding.toPx() }
            val endPaddingPx = with(density) { endPadding.toPx() }
            val topPaddingPx = with(density) { topPadding.toPx() }
            val bottomPaddingPx = with(density) { bottomPadding.toPx() }

            val state = rememberTransformableState { zoomChange, panChange, _ ->
                val newScale = (scale * zoomChange).coerceIn(ABSOLUTE_MIN_SCALE, MAX_SCALE)
                val scaledWidth = tableWidthPx * newScale
                val scaledHeight = tableHeightPx * newScale

                val horizontalOverhang = (scaledWidth - (constraints.maxWidth - startPaddingPx - endPaddingPx)).coerceAtLeast(0f)
                val horizontalShift = (startPaddingPx - endPaddingPx) / 2
                val minBoundsX = -horizontalOverhang / 2 + horizontalShift
                val maxBoundsX = horizontalOverhang / 2 + horizontalShift

                val verticalOverhang = (scaledHeight - (constraints.maxHeight - topPaddingPx - bottomPaddingPx)).coerceAtLeast(0f)
                val verticalShift = (topPaddingPx - bottomPaddingPx) / 2
                val minBoundsY = -verticalOverhang / 2 + verticalShift
                val maxBoundsY = verticalOverhang / 2 + verticalShift

                val newOffset = offset + panChange
                val correctedOffset = Offset(
                    x = newOffset.x.coerceIn(minBoundsX, maxBoundsX),
                    y = newOffset.y.coerceIn(minBoundsY, maxBoundsY)
                )

                scale = newScale
                offset = correctedOffset
            }

            val minScaleToFitWidth = constraints.maxWidth.toFloat() / tableWidthPx
            val initialScale = max(ABSOLUTE_MIN_SCALE, minScaleToFitWidth)

            LaunchedEffect(initialScale) {
                scale = initialScale
                offset = Offset.Zero
            }

            Box(modifier = Modifier.fillMaxSize().transformable(state))

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
            ) {
                ConstraintLayout(
                    modifier = Modifier.size(width = tableWidth, height = tableHeight)
                ) {
                    elements.forEach { element ->
                        val ref = createRef()
                        ElementGridItem(
                            element = element,
                            onPreviewChange = { shouldShow ->
                                previewedElement = if (shouldShow) element else null
                            },
                            onClick = {
                                navController.navigate(Screen.Detail.createRoute(element.atomicNumber))
                            },
                            modifier = Modifier
                                .constrainAs(ref) {
                                    val startMargin = cellSize * (element.xpos - 1)
                                    val topMargin = cellSize * (element.ypos - 1)
                                    start.linkTo(parent.start, margin = startMargin)
                                    top.linkTo(parent.top, margin = topMargin)
                                }
                                .size(cellSize)
                                .padding(2.dp)
                        )
                    }
                }
            }
        }

        // --- NEW COMPOSABLE: FOOTER LEGEND ---
        // This is added at the bottom of the Column.
        FooterLegend(modifier = Modifier.padding(16.dp))
    }


    if (previewedElement != null) {
        ElementPreviewDialog(
            element = previewedElement!!,
            viewModel = viewModel,
            onDismiss = { previewedElement = null }
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FooterLegend(modifier: Modifier = Modifier) {
    val legendItems = listOf(
        "Alkali metals" to "alkali metal",
        "Alkaline earth metals" to "alkaline earth metal",
        "Transition metals" to "transition metal",
        "Post-transition metals" to "post-transition metal",
        "Metalloids" to "metalloid",
        "Reactive non-metals" to "nonmetal", // "nonmetal" covers this group
        "Noble gases" to "noble gas",
        "Lanthanides" to "lanthanide",
        "Actinides" to "actinide",
        "Unknown properties" to "unknown"
    )

    // FlowRow automatically wraps items to the next line if they don't fit,
    // which is perfect for portrait and landscape modes.
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        legendItems.forEach { (displayName, categoryKey) ->
            LegendItem(
                text = displayName,
                color = getCategoryColor(category = categoryKey)
            )
        }
    }
}

@Composable
private fun LegendItem(text: String, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 12.sp)
    }
}