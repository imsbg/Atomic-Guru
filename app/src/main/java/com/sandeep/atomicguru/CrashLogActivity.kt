package com.sandeep.atomicguru

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.sandeep.atomicguru.ui.theme.AtomicGuruTheme

class CrashLogActivity : ComponentActivity() {

    companion object {
        const val EXTRA_CRASH_LOG = "extra_crash_log"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crashLog = intent.getStringExtra(EXTRA_CRASH_LOG) ?: "No crash log available."

        setContent {
            AtomicGuruTheme {
                CrashLogScreen(crashLog = crashLog) {
                    // Restart the app by launching MainActivity again
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashLogScreen(crashLog: String, onRestartClick: () -> Unit) {
    // 1. Get the current context to access system services
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("An Error Occurred") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    titleContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "The application has crashed. Please copy this error log and report it.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = crashLog,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall
            )

            // --- THIS IS THE NEW PART ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // COPY BUTTON
                OutlinedButton(
                    onClick = {
                        // 2. Get the ClipboardManager service
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        // 3. Create a ClipData object
                        val clip = ClipData.newPlainText("Crash Log", crashLog)
                        // 4. Set the data to the clipboard
                        clipboard.setPrimaryClip(clip)
                        // 5. Show a confirmation toast
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Copy Log")
                }

                // RESTART BUTTON
                Button(
                    onClick = onRestartClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Restart App")
                }
            }
            // --- END OF NEW PART ---
        }
    }
}