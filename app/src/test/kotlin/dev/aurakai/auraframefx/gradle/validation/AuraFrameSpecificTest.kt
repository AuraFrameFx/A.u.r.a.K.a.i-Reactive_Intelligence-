package dev.aurakai.auraframefx.gradle.validation

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.File

/**
 * Tests specific to AuraFrame FX project requirements and dependencies.
 */
class AuraFrameSpecificTest {

    private lateinit var tomlContent: String
    private val versionMap = mutableMapOf<String, String>()

    @Before
    fun setUp() {
        val tomlFile = File("gradle/libs.versions.toml")
        tomlContent = tomlFile.readText()
        parseVersions()
    }

    private fun parseVersions() {
        val versionLines = tomlContent.lines()
            .filter { it.contains(" = \"") && !it.trim().startsWith("#") }

        versionLines.forEach { line ->
            val parts = line.split(" = \"")
            if (parts.size == 2) {
                val key = parts[0].trim()
                val value = parts[1].removeSuffix("\"")
                versionMap[key] = value
            }
        }
    }

    @Test
    fun `test AI and generative dependencies are present`() {
        // Google Generative AI
        val generativeAiVersion = versionMap["generativeai"]
        assertNotNull("Generative AI version should be defined", generativeAiVersion)

        assertTrue(
            "Generative AI library should be defined",
            tomlContent.contains("com.google.ai.client.generativeai")
        )
    }

    @Test
    fun `test Firebase services required for AuraFrame are configured`() {
        val requiredFirebaseServices = listOf(
            "firebase-analytics-ktx",
            "firebase-crashlytics-ktx",
            "firebase-perf-ktx",
            "firebase-messaging-ktx",
            "firebase-config-ktx",
            "firebase-storage-ktx"
        )

        requiredFirebaseServices.forEach { service ->
            assertTrue(
                "Firebase service '$service' should be configured",
                tomlContent.contains(service)
            )
        }
    }

    @Test
    fun `test navigation and UI framework versions support AuraFrame requirements`() {
        // Navigation Compose
        val navigationVersion = versionMap["navigationCompose"]
        assertNotNull("Navigation Compose version should be defined", navigationVersion)
        assertTrue(
            "Navigation Compose should be version 2.9+",
            navigationVersion!!.startsWith("2.9") || navigationVersion.compareTo("2.9") >= 0
        )

        // Material 3 Adaptive
        val material3AdaptiveVersion = versionMap["material3Adaptive"]
        assertNotNull(
            "Material 3 Adaptive should be defined for responsive UI",
            material3AdaptiveVersion
        )

        // Window Size Class
        val windowSizeClassVersion = versionMap["windowSizeClass"]
        assertNotNull(
            "Window Size Class should be defined for responsive layouts",
            windowSizeClassVersion
        )
    }

    @Test
    fun `test data persistence and security libraries are configured`() {
        // Room for local database
        val roomVersion = versionMap["room"]
        assertNotNull("Room version should be defined", roomVersion)
        assertTrue(
            "Room should be version 2.7+",
            roomVersion!!.startsWith("2.7") || roomVersion.compareTo("2.7") >= 0
        )

        // DataStore for preferences
        val datastoreVersion = versionMap["datastore"]
        assertNotNull("DataStore version should be defined", datastoreVersion)

        // Security Crypto for encrypted storage
        assertTrue(
            "Security Crypto should be configured",
            tomlContent.contains("security-crypto")
        )
    }

    @Test
    fun `test networking and serialization libraries are modern`() {
        // Retrofit for networking
        val retrofitVersion = versionMap["retrofit"]
        assertNotNull("Retrofit version should be defined", retrofitVersion)
        assertTrue("Retrofit should be version 3.0+", retrofitVersion!!.startsWith("3."))

        // Kotlin Serialization
        val serializationVersion = versionMap["kotlinxSerializationJson"]
        assertNotNull("Kotlin Serialization should be defined", serializationVersion)

        // DateTime handling
        val datetimeVersion = versionMap["kotlinxDatetime"]
        assertNotNull("Kotlin DateTime should be defined", datetimeVersion)
    }

    @Test
    fun `test work manager and background processing support`() {
        val workManagerVersion = versionMap["workManager"]
        assertNotNull("Work Manager should be defined for background tasks", workManagerVersion)
        assertTrue(
            "Work Manager should be version 2.10+",
            workManagerVersion!!.startsWith("2.10") || workManagerVersion.compareTo("2.10") >= 0
        )

        // Hilt Work integration
        val hiltWorkVersion = versionMap["hiltWork"]
        assertNotNull("Hilt Work integration should be defined", hiltWorkVersion)
    }

    @Test
    fun `test image loading and UI utilities are configured`() {
        // Coil for image loading
        val coilVersion = versionMap["coilCompose"]
        assertNotNull("Coil Compose should be defined", coilVersion)
        assertTrue(
            "Coil should be version 2.7+",
            coilVersion!!.startsWith("2.7") || coilVersion.compareTo("2.7") >= 0
        )

        // Timber for logging
        val timberVersion = versionMap["timber"]
        assertNotNull("Timber should be defined for logging", timberVersion)
    }

    @Test
    fun `test testing infrastructure supports AuraFrame development`() {
        // Core testing libraries
        assertTrue("JUnit should be configured", tomlContent.contains("testJunit"))
        assertTrue("MockK should be configured", tomlContent.contains("mockkAndroid"))
        assertTrue(
            "Coroutines Test should be configured",
            tomlContent.contains("kotlinxCoroutinesTest")
        )

        // UI testing
        assertTrue("Compose UI Test should be configured", tomlContent.contains("ui-test-junit4"))
        assertTrue("Espresso should be configured", tomlContent.contains("espressoCore"))

        // Hilt testing
        assertTrue("Hilt Testing should be configured", tomlContent.contains("hiltAndroidTesting"))
    }

    @Test
    fun `test build and development plugins are properly configured`() {
        val requiredPlugins = listOf(
            "com.android.application",
            "org.jetbrains.kotlin.android",
            "com.google.devtools.ksp",
            "com.google.dagger.hilt.android",
            "com.google.gms.google-services",
            "org.jetbrains.kotlin.plugin.serialization",
            "com.google.firebase.crashlytics",
            "com.google.firebase.firebase-perf"
        )

        requiredPlugins.forEach { plugin ->
            assertTrue(
                "Required plugin '$plugin' should be configured",
                tomlContent.contains(plugin)
            )
        }
    }
}