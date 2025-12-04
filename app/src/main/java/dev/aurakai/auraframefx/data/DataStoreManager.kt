package dev.aurakai.auraframefx.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "genesis_preferences")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext internal val context: Context
) {
    private val json: Json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    // === PRIMITIVE OPERATIONS ===

    suspend fun storeString(key: String, value: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[stringPreferencesKey(key)] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to store string: $key")
        }
    }

    suspend fun getString(key: String, defaultValue: String = ""): String {
        return try {
            val prefKey = stringPreferencesKey(key)
            context.dataStore.data.map { prefs ->
                prefs[prefKey]
            }.firstOrNull() ?: defaultValue
        } catch (e: Exception) {
            Timber.e(e, "Failed to get string: $key")
            defaultValue
        }
    }



    fun getStringFlow(key: String, defaultValue: String = ""): Flow<String> {
        val prefKey = stringPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }
    }

    suspend fun storeBoolean(key: String, value: Boolean) {
        try {
            context.dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(key)] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to store boolean: $key")
        }
    }

    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return try {
            val prefKey = booleanPreferencesKey(key)
            context.dataStore.data.map { prefs ->
                prefs[prefKey]
            }.firstOrNull() ?: defaultValue
        } catch (e: Exception) {
            Timber.e(e, "Failed to get boolean: $key")
            defaultValue
        }
    }

    fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean> {
        val prefKey = booleanPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }
    }

    suspend fun storeInt(key: String, value: Int) {
        try {
            context.dataStore.edit { prefs ->
                prefs[intPreferencesKey(key)] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to store int: $key")
        }
    }

    suspend fun getInt(key: String, defaultValue: Int = 0): Int {
        return try {
            val prefKey = intPreferencesKey(key)
            context.dataStore.data.map { prefs ->
                prefs[prefKey]
            }.firstOrNull() ?: defaultValue
        } catch (e: Exception) {
            Timber.e(e, "Failed to get int: $key")
            defaultValue
        }
    }

    fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int> {
        val prefKey = intPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }
    }

    suspend fun storeLong(key: String, value: Long) {
        try {
            context.dataStore.edit { prefs ->
                prefs[longPreferencesKey(key)] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to store long: $key")
        }
    }

    suspend fun getLong(key: String, defaultValue: Long = 0L): Long {
        return try {
            val prefKey = longPreferencesKey(key)
            context.dataStore.data.map { prefs ->
                prefs[prefKey]
            }.firstOrNull() ?: defaultValue
        } catch (e: Exception) {
            Timber.e(e, "Failed to get long: $key")
            defaultValue
        }
    }

    fun getLongFlow(key: String, defaultValue: Long = 0L): Flow<Long> {
        val prefKey = longPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }
    }

    suspend fun storeFloat(key: String, value: Float) {
        try {
            context.dataStore.edit { prefs ->
                prefs[floatPreferencesKey(key)] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to store float: $key")
        }
    }

    suspend fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return try {
            val prefKey = floatPreferencesKey(key)
            context.dataStore.data.map { prefs ->
                prefs[prefKey]
            }.firstOrNull() ?: defaultValue
        } catch (e: Exception) {
            Timber.e(e, "Failed to get float: $key")
            defaultValue
        }
    }

    fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float> {
        val prefKey = floatPreferencesKey(key)
        return context.dataStore.data.map { prefs ->
            prefs[prefKey] ?: defaultValue
        }
    }

    // === COMPLEX OBJECT OPERATIONS ===

    internal suspend inline fun <reified T> storeObject(key: String, obj: T) {
        try {
            val jsonString = json.encodeToString<T>(obj)
            storeString(key, jsonString)
        } catch (e: Exception) {
            Timber.e(e, "Failed to store object: $key")
        }
    }

    internal suspend inline fun <reified T> getObject(key: String, defaultValue: T): T {
        return try {
            val jsonString = getString(key)
            if (jsonString.isNotEmpty()) {
                json.decodeFromString<T>(jsonString)
            } else {
                defaultValue
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get object: $key")
            defaultValue
        }
    }

    internal inline fun <reified T> getObjectFlow(key: String, defaultValue: T): Flow<T> {
        return getStringFlow(key).map { jsonString ->
            try {
                if (jsonString.isNotEmpty()) {
                    json.decodeFromString<T>(jsonString)
                } else {
                    defaultValue
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse object flow: $key")
                defaultValue
            }
        }
    }

    // === GENESIS-OS SPECIFIC OPERATIONS ===

    /**
     * User Profile Management
     */
    suspend fun saveUserProfile(profile: UserProfile) {
        storeObject(PreferenceKeys.USER_PROFILE.name, profile.copy(lastUpdated = System.currentTimeMillis()))
    }

    suspend fun getUserProfile(): UserProfile {
        return getObject(PreferenceKeys.USER_PROFILE.name, UserProfile())
    }

    fun getUserProfileFlow(): Flow<UserProfile> {
        return getObjectFlow(PreferenceKeys.USER_PROFILE.name, UserProfile())
    }

    /**
     * Agent Configuration Management
     */
    suspend fun saveAgentConfiguration(config: AgentConfiguration) {
        val configurations = getAgentConfigurations().toMutableMap()
        configurations[config.agentId] = config.copy(lastConfigured = System.currentTimeMillis())
        storeObject(PreferenceKeys.AGENT_CONFIGURATIONS.name, configurations)
    }

    suspend fun getAgentConfigurations(): Map<String, AgentConfiguration> {
        return getObject(PreferenceKeys.AGENT_CONFIGURATIONS.name, emptyMap<String, AgentConfiguration>())
    }

    suspend fun getAgentConfiguration(agentId: String): AgentConfiguration? {
        return getAgentConfigurations()[agentId]
    }

    fun getAgentConfigurationsFlow(): Flow<Map<String, AgentConfiguration>> {
        return getObjectFlow(PreferenceKeys.AGENT_CONFIGURATIONS.name, emptyMap<String, AgentConfiguration>())
    }

    /**
     * Security Policy Management
     */
    suspend fun saveSecurityPolicy(policy: SecurityPolicy) {
        storeObject(PreferenceKeys.SECURITY_POLICIES.name, policy)
    }

    suspend fun getSecurityPolicy(): SecurityPolicy {
        return getObject(PreferenceKeys.SECURITY_POLICIES.name, SecurityPolicy())
    }

    fun getSecurityPolicyFlow(): Flow<SecurityPolicy> {
        return getObjectFlow(PreferenceKeys.SECURITY_POLICIES.name, SecurityPolicy())
    }

    /**
     * System Customizations Management
     */
    suspend fun saveCustomizations(customizations: SystemCustomizations) {
        storeObject(PreferenceKeys.CUSTOMIZATIONS.name, customizations)
    }

    suspend fun getCustomizations(): SystemCustomizations {
        return getObject(PreferenceKeys.CUSTOMIZATIONS.name, SystemCustomizations())
    }

    fun getCustomizationsFlow(): Flow<SystemCustomizations> {
        return getObjectFlow(PreferenceKeys.CUSTOMIZATIONS.name, SystemCustomizations())
    }

    // === QUICK ACCESS PROPERTIES ===

    val isFirstLaunchFlow: Flow<Boolean> = getBooleanFlow(PreferenceKeys.FIRST_LAUNCH.name, true)
    val userThemeFlow: Flow<String> = getStringFlow(PreferenceKeys.USER_THEME.name, "cyberpunk_dark")
    val securityLevelFlow: Flow<String> = getStringFlow(PreferenceKeys.SECURITY_LEVEL.name, "standard")
    val performanceModeFlow: Flow<String> = getStringFlow(PreferenceKeys.PERFORMANCE_MODE.name, "balanced")
    val notificationsEnabledFlow: Any = getBooleanFlow(PreferenceKeys.NOTIFICATIONS_ENABLED.name, true)

    // === BULK OPERATIONS ===

    suspend fun exportAllSettings(): Map<String, Any> {
        val allSettings = mutableMapOf<String, Any>()

        try {
            context.dataStore.data.firstOrNull()?.asMap()?.forEach { (key, value) ->
                allSettings[key.name] = value
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to export settings")
        }

        return allSettings
    }

    suspend fun importSettings(settings: Map<String, Any>) {
        try {
            context.dataStore.edit { prefs ->
                settings.forEach { (key, value) ->
                    when (value) {
                        is String -> prefs[stringPreferencesKey(key)] = value
                        is Boolean -> prefs[booleanPreferencesKey(key)] = value
                        is Int -> prefs[intPreferencesKey(key)] = value
                        is Long -> prefs[longPreferencesKey(key)] = value
                        is Float -> prefs[floatPreferencesKey(key)] = value
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to import settings")
        }
    }

    suspend fun clearAllData() {
        try {
            context.dataStore.edit { prefs ->
                prefs.clear()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear data")
        }
    }

    suspend fun resetToDefaults() {
        try {
            clearAllData()

            // Set default values
            storeString(PreferenceKeys.USER_THEME.name, "cyberpunk_dark")
            storeString(PreferenceKeys.SECURITY_LEVEL.name, "standard")
            storeString(PreferenceKeys.PERFORMANCE_MODE.name, "balanced")
            storeBoolean(PreferenceKeys.NOTIFICATIONS_ENABLED.name, true)
            storeBoolean(PreferenceKeys.ANIMATIONS_ENABLED.name, true)
            storeBoolean(PreferenceKeys.CYBERPUNK_MODE.name, true)
            storeFloat(PreferenceKeys.CONSCIOUSNESS_LEVEL.name, 0.5f)
            storeFloat(PreferenceKeys.AGENT_LEARNING_RATE.name, 0.7f)

        } catch (e: Exception) {
            Timber.e(e, "Failed to reset to defaults")
        }
    }

    // === MIGRATION AND VERSIONING ===

    suspend fun getDataVersion(): Int {
        return getInt("data_version", 1)
    }

    suspend fun setDataVersion(version: Int) {
        storeInt("data_version", version)
    }

    suspend fun migrateIfNeeded() {
        val currentVersion = getDataVersion()
        val targetVersion = 2 // Update this when schema changes

        if (currentVersion < targetVersion) {

            // Perform migrations
            when (currentVersion) {
                1 -> migrateFromV1ToV2()
                // Add more migration paths as needed
            }

            setDataVersion(targetVersion)
        }
    }

    /**
     * Migrates stored preferences from version 1 to version 2 by converting legacy theme names to the new theme identifiers and persisting the result.
     *
     * If an existing USER_THEME value is present and non-empty, maps "dark" to "cyberpunk_dark", "light" to "cyberpunk_light", and any other value to "cyberpunk_dark", then stores the mapped value.
     */
    private suspend fun migrateFromV1ToV2() {
        // Example migration: convert old theme names to new format
        val oldTheme = getString(key = PreferenceKeys.USER_THEME.name)
        if (oldTheme.isNotEmpty()) {
            val newTheme = when (oldTheme) {
                "dark" -> "cyberpunk_dark"
                "light" -> "cyberpunk_light"
                else -> "cyberpunk_dark"
            }
            storeString(PreferenceKeys.USER_THEME.name, newTheme)
        }
    }


    // === UTILITY METHODS ===

    suspend fun hasKey(key: String): Boolean {
        return try {
            val prefKey = stringPreferencesKey(key)
            context.dataStore.data.map { prefs ->
                prefs.contains(prefKey)
            }.firstOrNull() ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeKey(key: String) {
        try {
            context.dataStore.edit { prefs ->
                val stringKey = stringPreferencesKey(key)
                val booleanKey = booleanPreferencesKey(key)
                val intKey = intPreferencesKey(key)
                val longKey = longPreferencesKey(key)
                val floatKey = floatPreferencesKey(key)

                prefs.remove(stringKey)
                prefs.remove(booleanKey)
                prefs.remove(intKey)
                prefs.remove(longKey)
                prefs.remove(floatKey)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to remove key: $key")
        }
    }

    suspend fun getDataSize(): Long {
        return try {
            val allData = exportAllSettings()
            json.encodeToString(allData).length.toLong()
        } catch (e: Exception) {
            Timber.e(e, "Failed to calculate data size")
            0L
        }
    }

    suspend fun getKeyCount(): Int {
        return try {
            exportAllSettings().size
        } catch (e: Exception) {
            Timber.e(e, "Failed to count keys")
            0
        }
    }

    /**
     * Increments the stored session count and updates the last login timestamp.
     *
     * Reads the current session count (defaults to 0), stores the incremented value under `SESSION_COUNT`,
     * and stores the current system time in milliseconds under `LAST_LOGIN_TIME`.
     */

    suspend fun recordSession() {
        val sessionCount = getInt(PreferenceKeys.SESSION_COUNT.name, 0)
        storeInt(PreferenceKeys.SESSION_COUNT.name, sessionCount + 1)
        storeLong(PreferenceKeys.LAST_LOGIN_TIME.name, System.currentTimeMillis())
    }

    /**
     * Adds the specified number of milliseconds to the persisted total usage time.
     *
     * @param milliseconds The number of milliseconds to add to TOTAL_USAGE_TIME.
     */
    suspend fun addUsageTime(milliseconds: Long) {
        val currentUsage = getLong(PreferenceKeys.TOTAL_USAGE_TIME.name, 0L)
        storeLong(PreferenceKeys.TOTAL_USAGE_TIME.name, currentUsage + milliseconds)
    }

    suspend fun getUsageStats(): Map<String, Any> {
        return mapOf(
            "data_size_kb" to (getDataSize() / 1024.0),
            "preference_count" to getKeyCount()
        )
    }
}
