package com.sandeep.atomicguru.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sandeep.atomicguru.R
import com.sandeep.atomicguru.data.Element
import com.sandeep.atomicguru.viewmodel.Language
import com.sandeep.atomicguru.viewmodel.MainViewModel

/**
 * A dialog that shows a quick preview of an element's details.
 * This version has NO buttons and is designed for a press-and-hold interaction.
 * @param element The element to preview.
 * @param viewModel The MainViewModel to get the current language state.
 * @param onDismiss Lambda to be called when the dialog should be closed.
 */
@Composable
fun ElementPreviewDialog(
    element: Element,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val details = if (state.currentLanguage == Language.EN) element.detailsEn else element.detailsOdia

    // --- THIS IS THE FIX ---
    // We use DialogProperties to make the default dialog background transparent.
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // This allows us to control the width freely
        )
    ) {
        // By setting the dialog's container to transparent, our GlassmorphicCard
        // becomes the visible background of the dialog.
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            color = Color.Transparent
        ) {
            GlassmorphicCard {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = details.generalInfo.elementName,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    InfoRow(label = stringResource(R.string.atomic_mass), value = details.generalInfo.atomicMass)
                    InfoRow(label = stringResource(R.string.category), value = details.generalInfo.category)
                    InfoRow(label = stringResource(R.string.appearance), value = details.generalInfo.appearance)
                }
            }
        }
    }
    // --- END OF FIX ---
}