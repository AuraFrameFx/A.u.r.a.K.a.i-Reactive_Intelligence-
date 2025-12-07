package dev.aurakai.auraframefx.system.quicksettings

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.aurakai.auraframefx.ui.QuickSettingsConfig
import dev.aurakai.auraframefx.ui.QuickSettingsTileConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

/**
 * Manages Quick Settings configuration including loading, saving, and applying user preferences.
 * Uses the UI data classes defined in `dev.aurakai.auraframefx.ui` as the canonical data model.
 */
class QuickSettingsConfigManager private constructor(context: Context) {
    private val tag = "QuickSettingsConfigManager"
    private val gson = Gson()
    private val configFile = File(context.filesDir, "quick_settings_config.json")

    // Default configuration uses the companion DEFAULT from the UI model
    private val defaultConfig: QuickSettingsConfig = QuickSettingsConfig.DEFAULT

    // Cache for the current configuration
    private var currentConfig: QuickSettingsConfig = defaultConfig

    /**
     * Loads the Quick Settings configuration from storage.
     * If no saved configuration exists, returns the default configuration.
     */
    suspend fun loadConfig(): QuickSettingsConfig = withContext(Dispatchers.IO) {
        try {
            if (!configFile.exists()) {
                Timber.tag(tag).d("No saved config found, using default")
                currentConfig = defaultConfig
                return@withContext currentConfig
            }

            val json = configFile.readText()
            if (json.isBlank()) {
                Timber.tag(tag).d("Empty config file, using default")
                currentConfig = defaultConfig
                return@withContext currentConfig
            }

            val type = object : TypeToken<QuickSettingsConfig>() {}.type
            val parsed: QuickSettingsConfig? = gson.fromJson(json, type)
            currentConfig = parsed ?: defaultConfig
            return@withContext currentConfig
        } catch (e: Exception) {
            Timber.tag(tag).e(e, "Error loading config, using default")
            currentConfig = defaultConfig
            return@withContext currentConfig
        }
    }

    /**
     * Saves the Quick Settings configuration to storage.
     */
    suspend fun saveConfig(config: QuickSettingsConfig): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = gson.toJson(config)
            configFile.writeText(json)
            currentConfig = config
            true
        } catch (e: Exception) {
            Timber.tag(tag).e(e, "Error saving config")
            false
        }
    }

    /**
     * Updates a specific tile's configuration, returning true on success.
     */
    suspend fun updateTileConfig(
        tileId: String,
        update: (QuickSettingsTileConfig) -> QuickSettingsTileConfig
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val tiles = currentConfig.tiles.toMutableList()
            val index = tiles.indexOfFirst { it.id == tileId }
            if (index == -1) return@withContext false

            val updated = update(tiles[index])
            tiles[index] = updated
            val newConfig = currentConfig.copy(tiles = tiles)
            saveConfig(newConfig)
        } catch (e: Exception) {
            Timber.tag(tag).e(e, "Error updating tile config")
            false
        }
    }

    /** Resets the configuration to default values. */
    suspend fun resetToDefault(): Boolean = saveConfig(defaultConfig)

    /** Gets the current configuration. */
    fun getCurrentConfig(): QuickSettingsConfig = currentConfig

    /** Applies the current configuration to the Quick Settings panel (platform-specific, stubbed). */
    fun applyConfig(panel: Any) {
        // Platform-specific application code goes here (e.g., Xposed hooks)
    }

    companion object {
        @Volatile
        private var instance: QuickSettingsConfigManager? = null

        fun getInstance(context: Context): QuickSettingsConfigManager {
            return instance ?: synchronized(this) {
                instance ?: QuickSettingsConfigManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
