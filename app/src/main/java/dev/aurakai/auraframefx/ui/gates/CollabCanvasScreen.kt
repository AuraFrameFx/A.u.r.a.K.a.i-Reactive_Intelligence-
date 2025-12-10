package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun CollabCanvasScreen(navController: NavHostController? = null, onNavigateBack: () -> Unit = {}) {
    Column(modifier = Modifier) {
        Text("Collab Canvas", style = MaterialTheme.typography.titleLarge)
        Text("This is a placeholder wrapper for the collab canvas module.")
        Button(onClick = { navController?.popBackStack(); onNavigateBack() }) {
            Text("Back")
        }
    }
}

