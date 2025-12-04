package dev.aurakai.auraframefx.ui.customization

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.ui.theme.CyberpunkPink
import dev.aurakai.auraframefx.ui.theme.CyberpunkPurple
import dev.aurakai.auraframefx.utils.GyroscopeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for 3D Gyroscope Customization Editor
 *
 * Manages:
 * - Gyroscope sensor data
 * - Phone customization state (colors, themes)
 * - AI prompt processing for customizations
 * - Integration with ChromaCore and Theme Engine
 */
@HiltViewModel
class CustomizationViewModel @Inject constructor(
    application: Application,
    private val gyroscopeManager: GyroscopeManager
) : AndroidViewModel(application) {

    private val _customizationState = MutableStateFlow(CustomizationState())
    val customizationState: StateFlow<CustomizationState> = _customizationState.asStateFlow()

    private val _rotationAngles = MutableStateFlow(RotationAngles(0f, 0f, 0f))
    val rotationAngles: StateFlow<RotationAngles> = _rotationAngles.asStateFlow()

    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()

    /**
     * Start gyroscope sensor
     */
    fun startGyroscope() {
        if (!gyroscopeManager.isGyroscopeAvailable()) {
            Timber.w("Gyroscope not available - using fallback orientation")
            viewModelScope.launch {
                gyroscopeManager.getOrientationFlow().collect { angles ->
                    _rotationAngles.value = angles
                }
            }
            return
        }

        viewModelScope.launch {
            Timber.i("✅ Starting gyroscope sensor")
            gyroscopeManager.getRotationFlow().collect { angles ->
                _rotationAngles.value = angles
            }
        }
    }

    /**
     * Stop gyroscope sensor
     */
    fun stopGyroscope() {
        Timber.i("Stopping gyroscope sensor")
        gyroscopeManager.reset()
    }

    /**
     * Reset rotation angles to zero
     */
    fun resetRotation() {
        gyroscopeManager.reset()
        _rotationAngles.value = RotationAngles(0f, 0f, 0f)
        Timber.d("Rotation reset to zero")
    }

    /**
     * Process AI prompt for customizations
     *
     * Examples:
     * - "Make it cyberpunk pink"
     * - "Dark neon theme"
     * - "Minimal white and gray"
     * - "Futuristic blue glow"
     */
    fun processAIPrompt(prompt: String) {
        viewModelScope.launch {
            try {
                _customizationState.value = _customizationState.value.copy(isProcessing = true)
                _aiResponse.value = null

                Timber.d("Processing AI prompt: $prompt")

                // Parse prompt and apply customization
                val customization = parsePromptToCustomization(prompt)

                _customizationState.value = customization
                _aiResponse.value = "Applied: ${customization.description}"

                Timber.i("✅ Customization applied: ${customization.description}")

            } catch (e: Exception) {
                Timber.e(e, "Failed to process AI prompt")
                _aiResponse.value = "Error: ${e.message}"
                _customizationState.value = _customizationState.value.copy(isProcessing = false)
            }
        }
    }

    /**
     * Parse AI prompt to customization
     *
     * Simple rule-based system for now
     * TODO: Replace with actual AI model (Gemini/Claude)
     */
    private fun parsePromptToCustomization(prompt: String): CustomizationState {
        val lowercasePrompt = prompt.lowercase()

        return when {
            // Cyberpunk themes
            "cyberpunk" in lowercasePrompt && "pink" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = CyberpunkPink,
                    secondaryColor = CyberpunkPurple,
                    accentColor = CyberpunkCyan,
                    description = "Cyberpunk Pink Theme",
                    themeName = "cyberpunk_pink",
                    isProcessing = false
                )
            }
            "cyberpunk" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = CyberpunkCyan,
                    secondaryColor = CyberpunkPurple,
                    accentColor = CyberpunkPink,
                    description = "Cyberpunk Neon Theme",
                    themeName = "cyberpunk_neon",
                    isProcessing = false
                )
            }

            // Dark themes
            "dark" in lowercasePrompt && ("neon" in lowercasePrompt || "purple" in lowercasePrompt) -> {
                CustomizationState(
                    primaryColor = Color(0xFF1A0033),
                    secondaryColor = CyberpunkPurple,
                    accentColor = Color(0xFFFF00FF),
                    description = "Dark Neon Purple Theme",
                    themeName = "dark_neon",
                    isProcessing = false
                )
            }
            "dark" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = Color(0xFF0A0A0A),
                    secondaryColor = Color(0xFF1A1A1A),
                    accentColor = Color(0xFF3A3A3A),
                    description = "Pure Dark Theme",
                    themeName = "pure_dark",
                    isProcessing = false
                )
            }

            // Light/Minimal themes
            "minimal" in lowercasePrompt || "white" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = Color.White,
                    secondaryColor = Color(0xFFF5F5F5),
                    accentColor = Color(0xFF333333),
                    description = "Minimal Light Theme",
                    themeName = "minimal_light",
                    isProcessing = false
                )
            }

            // Futuristic/Blue themes
            "futuristic" in lowercasePrompt || "blue" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = Color(0xFF0A1F3D),
                    secondaryColor = Color(0xFF1E3A5F),
                    accentColor = Color(0xFF00B4D8),
                    description = "Futuristic Blue Theme",
                    themeName = "futuristic_blue",
                    isProcessing = false
                )
            }

            // Matrix/Green themes
            "matrix" in lowercasePrompt || "green" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = Color(0xFF0D0D0D),
                    secondaryColor = Color(0xFF001F00),
                    accentColor = Color(0xFF00FF41),
                    description = "Matrix Green Theme",
                    themeName = "matrix_green",
                    isProcessing = false
                )
            }

            // Sunset/Warm themes
            "sunset" in lowercasePrompt || "warm" in lowercasePrompt || "orange" in lowercasePrompt -> {
                CustomizationState(
                    primaryColor = Color(0xFF332200),
                    secondaryColor = Color(0xFFFF6B35),
                    accentColor = Color(0xFFFFD700),
                    description = "Sunset Warm Theme",
                    themeName = "sunset_warm",
                    isProcessing = false
                )
            }

            // Default: Cyberpunk theme
            else -> {
                _aiResponse.value = "Applied default cyberpunk theme. Try: 'Dark neon theme', 'Minimal white', 'Matrix green', etc."
                CustomizationState(
                    primaryColor = CyberpunkCyan,
                    secondaryColor = CyberpunkPurple,
                    accentColor = CyberpunkPink,
                    description = "Default Cyberpunk Theme",
                    themeName = "default_cyberpunk",
                    isProcessing = false
                )
            }
        }
    }

    /**
     * Apply custom colors manually
     */
    fun applyCustomColors(
        primary: Color,
        secondary: Color,
        accent: Color,
        description: String = "Custom Theme"
    ) {
        _customizationState.value = CustomizationState(
            primaryColor = primary,
            secondaryColor = secondary,
            accentColor = accent,
            description = description,
            themeName = "custom",
            isProcessing = false
        )
        Timber.i("Applied custom colors: $description")
    }

    /**
     * Load predefined theme
     */
    fun loadTheme(themeName: String) {
        val prompt = when (themeName) {
            "cyberpunk_pink" -> "Cyberpunk pink theme"
            "cyberpunk_neon" -> "Cyberpunk neon theme"
            "dark_neon" -> "Dark neon purple theme"
            "pure_dark" -> "Pure dark theme"
            "minimal_light" -> "Minimal white theme"
            "futuristic_blue" -> "Futuristic blue theme"
            "matrix_green" -> "Matrix green theme"
            "sunset_warm" -> "Sunset warm theme"
            else -> "Cyberpunk theme"
        }
        processAIPrompt(prompt)
    }
}

/**
 * Customization state
 */
data class CustomizationState(
    val primaryColor: Color = CyberpunkCyan,
    val secondaryColor: Color = CyberpunkPurple,
    val accentColor: Color = CyberpunkPink,
    val description: String = "Default Cyberpunk Theme",
    val themeName: String = "default",
    val isProcessing: Boolean = false
)

/**
 * Rotation angles from gyroscope
 */
data class RotationAngles(
    val pitch: Float,  // X-axis (forward/backward tilt)
    val roll: Float,   // Y-axis (left/right tilt)
    val yaw: Float     // Z-axis (twist)
)
