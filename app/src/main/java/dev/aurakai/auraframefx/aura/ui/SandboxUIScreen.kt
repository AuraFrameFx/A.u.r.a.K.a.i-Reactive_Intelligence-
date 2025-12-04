package dev.aurakai.auraframefx.aura.ui

import androidx.compose.runtime.Composable

@Composable
fun SandboxUIScreen(onBack: () -> Unit) {
    PlaceholderScreen(title = "Aura's Sandbox UI", onBack = {
        onBack()
        true
    })

}
