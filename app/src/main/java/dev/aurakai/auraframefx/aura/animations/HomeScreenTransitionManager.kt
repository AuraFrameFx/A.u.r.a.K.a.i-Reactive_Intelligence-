package dev.aurakai.auraframefx.system.homescreen

import dev.aurakai.auraframefx.system.quicksettings.YukiHookModulePrefs
import dev.aurakai.auraframefx.services.YukiHookServiceManager
import dev.aurakai.auraframefx.system.common.ImageResourceManager
import dev.aurakai.auraframefx.system.homescreen.HomeScreenTransitionType
import dev.aurakai.auraframefx.system.homescreen.model.HomeScreenTransitionConfig
import dev.aurakai.auraframefx.system.ui.ShapeManager
import dev.aurakai.auraframefx.system.ui.SystemOverlayManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeScreenTransitionManager @Inject constructor(
    private val overlayManager: SystemOverlayManager,
    private val shapeManager: ShapeManager,
    private val imageManager: ImageResourceManager,
    private val prefs: YukiHookModulePrefs,
    private val overlayService: YukiHookServiceManager,
) {
    private val _currentConfig =
        MutableStateFlow(HomeScreenTransitionConfig()) // Initialize with default
    val currentConfig: StateFlow<HomeScreenTransitionConfig?> =
        _currentConfig // Kept nullable for safety

    private val defaultConfig = HomeScreenTransitionConfig(
        type = HomeScreenTransitionType.GLOBE_ROTATE,
        duration = 500,
        // easing = "easeInOut", // Removed, not in HomeScreenTransitionConfig
        properties = mapOf(
            "angle" to 360f,
            "scale" to 1.2f,
            "offset" to 0f,
            "amplitude" to 0.1f,
            "frequency" to 0.5f,
            "color" to "#00FFCC",
            "blur" to 20f,
            "spread" to 0.2f
        )
    )

    init {
        loadConfig()
    }

    /**
     * Loads the home screen transition configuration from YukiHook preferences.
     *
     * Attempts to load saved configuration JSON from preferences. If found, parses it into
     * a HomeScreenTransitionConfig object. Falls back to default configuration on failure.
     */
    private fun loadConfig() {
        try {
            // Load saved config from YukiHook preferences
            val savedConfigJson = prefs.getString("home_screen_transition_config", null)

            if (savedConfigJson != null) {
                Timber.d("HomeScreenTransitionManager: Loading saved config")
                val parsedConfig = parseConfigFromJson(savedConfigJson)

                if (parsedConfig != null) {
                    _currentConfig.value = parsedConfig
                    Timber.i("HomeScreenTransitionManager: Config loaded successfully - type: ${parsedConfig.type}")
                } else {
                    Timber.w("HomeScreenTransitionManager: Failed to parse config, using default")
                    _currentConfig.value = defaultConfig
                }
            } else {
                Timber.d("HomeScreenTransitionManager: No saved config found, using default")
                _currentConfig.value = defaultConfig
            }
        } catch (e: Exception) {
            Timber.e(e, "HomeScreenTransitionManager: Error loading config, using default")
            _currentConfig.value = defaultConfig
        }
    }

    /**
     * Parses a JSON string into a HomeScreenTransitionConfig object.
     *
     * Expected JSON structure:
     * {
     *   "type": "DIGITAL_DECONSTRUCT",
     *   "duration": 500,
     *   "properties": {
     *     "angle": 360.0,
     *     "scale": 1.2,
     *     "color": "#00FFCC"
     *   }
     * }
     *
     * @param json The JSON string to parse
     * @return Parsed configuration, or null if parsing fails
     */
    private fun parseConfigFromJson(json: String): HomeScreenTransitionConfig? {
        return try {
            val jsonObject = JSONObject(json)

            // Parse transition type
            val typeString = jsonObject.optString("type", "GLOBE_ROTATE")
            val transitionType = try {
                HomeScreenTransitionType.valueOf(typeString)
            } catch (e: IllegalArgumentException) {
                Timber.w("HomeScreenTransitionManager: Unknown transition type: $typeString, using default")
                HomeScreenTransitionType.GLOBE_ROTATE
            }

            // Parse duration
            val duration = jsonObject.optInt("duration", 500)

            // Parse properties map
            val propertiesJson = jsonObject.optJSONObject("properties")
            val properties = if (propertiesJson != null) {
                parsePropertiesMap(propertiesJson)
            } else {
                emptyMap()
            }

            HomeScreenTransitionConfig(
                type = transitionType,
                duration = duration,
                properties = properties
            )
        } catch (e: JSONException) {
            Timber.e(e, "HomeScreenTransitionManager: JSON parsing error")
            null
        }
    }

    /**
     * Parses a JSON object into a properties map.
     *
     * Converts JSON values to appropriate Kotlin types (Float, Int, String, Boolean).
     *
     * @param jsonObject The JSON object containing properties
     * @return Map of property keys to values
     */
    private fun parsePropertiesMap(jsonObject: JSONObject): Map<String, Any> {
        val properties = mutableMapOf<String, Any>()

        jsonObject.keys().forEach { key ->
            val value = when {
                jsonObject.isNull(key) -> return@forEach

                // Try to parse as various types
                jsonObject.optJSONObject(key) != null -> jsonObject.optString(key) // Nested object as string
                jsonObject.optJSONArray(key) != null -> jsonObject.optString(key) // Array as string

                else -> {
                    // Try numeric types first
                    val rawValue = jsonObject.get(key)
                    when (rawValue) {
                        is Number -> rawValue.toFloat()
                        is Boolean -> rawValue
                        is String -> rawValue
                        else -> rawValue.toString()
                    }
                }
            }

            properties[key] = value
        }

        return properties
    }

    /**
     * Saves the current configuration to YukiHook preferences as JSON.
     *
     * Serializes the current configuration and persists it for future sessions.
     */
    fun saveConfig() {
        try {
            val config = _currentConfig.value ?: return
            val json = configToJson(config)

            prefs.putString("home_screen_transition_config", json)
            Timber.i("HomeScreenTransitionManager: Config saved successfully")
        } catch (e: Exception) {
            Timber.e(e, "HomeScreenTransitionManager: Failed to save config")
        }
    }

    /**
     * Converts a HomeScreenTransitionConfig to JSON string.
     *
     * @param config The configuration to serialize
     * @return JSON string representation
     */
    private fun configToJson(config: HomeScreenTransitionConfig): String {
        val jsonObject = JSONObject().apply {
            put("type", config.type.name)
            put("duration", config.duration)

            if (config.properties.isNotEmpty()) {
                val propertiesJson = JSONObject()
                config.properties.forEach { (key, value) ->
                    propertiesJson.put(key, value)
                }
                put("properties", propertiesJson)
            }
        }

        return jsonObject.toString()
    }

    /**
     * Applies the provided home screen transition configuration and updates the current state.
     *
     * @param config The new transition configuration to apply.
     */
    fun applyConfig(config: HomeScreenTransitionConfig) {
        _currentConfig.value = config
        // TODO: Implement Xposed hooking for beta
        // overlayService.hook {
        //     // TODO: Implement transition hooking
        // }
    }

    /**
     * Restores the home screen transition configuration to its default settings.
     */
    fun resetToDefault() {
        applyConfig(defaultConfig)
    }

    fun updateTransitionType(type: HomeScreenTransitionType) {
        val current = _currentConfig.value ?: return
        val newConfig = current.copy(
            type = type
        )
        applyConfig(newConfig)
    }

    fun updateTransitionDuration(duration: Int) {
        val current = _currentConfig.value ?: return
        val newConfig = current.copy(
            duration = duration
        )
        applyConfig(newConfig)
    }

    fun updateTransitionProperties(properties: Map<String, Any>) {
        val current = _currentConfig.value ?: return
        val newConfig = current.copy(
            properties = properties
        )
        applyConfig(newConfig)
    }
}
