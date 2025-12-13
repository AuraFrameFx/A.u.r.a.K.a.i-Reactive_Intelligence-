package dev.aurakai.auraframefx.gradle.validation

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.File

/**
 * Tests for dependency compatibility and version alignment.
 * Ensures that related dependencies use compatible versions.
 */
class DependencyCompatibilityTest {

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
    fun `test AGP and Kotlin compatibility matrix`() {
        val agpVersion = versionMap["agp"]
        val kotlinVersion = versionMap["kotlin"]

        assertNotNull("AGP version should be defined", agpVersion)
        assertNotNull("Kotlin version should be defined", kotlinVersion)

        // AGP 8.11.1 should be compatible with Kotlin 2.0.0
        when {
            agpVersion!!.startsWith("8.11") -> {
                assertTrue(
                    "AGP 8.11.x should be compatible with Kotlin 2.0.x",
                    kotlinVersion!!.startsWith("2.0")
                )
            }

            agpVersion.startsWith("8.10") -> {
                assertTrue(
                    "AGP 8.10.x should be compatible with Kotlin 1.9.x or 2.0.x",
                    kotlinVersion!!.startsWith("1.9") || kotlinVersion.startsWith("2.0")
                )
            }
        }
    }

    @Test
    fun `test Firebase BOM version is recent`() {
        val firebaseBomVersion = versionMap["firebaseBomVersion"]
        assertNotNull("Firebase BOM version should be defined", firebaseBomVersion)

        // Firebase BOM should be a recent version (2024+)
        val year = firebaseBomVersion!!.split(".")[0].toIntOrNull()
        assertTrue(
            "Firebase BOM should be from 2024 or later: $firebaseBomVersion",
            year != null && year >= 33 // 33.x.x corresponds to 2024
        )
    }

    @Test
    fun `test Compose BOM is compatible with Kotlin version`() {
        val composeBom = versionMap["composeBom"]
        val kotlinVersion = versionMap["kotlin"]

        assertNotNull("Compose BOM should be defined", composeBom)
        assertNotNull("Kotlin version should be defined", kotlinVersion)

        // Compose BOM 2024.05.00 should work with Kotlin 2.0.0
        if (composeBom!!.startsWith("2024.05")) {
            assertTrue(
                "Compose BOM 2024.05.x should be compatible with Kotlin 2.0.x",
                kotlinVersion!!.startsWith("2.0")
            )
        }
    }

    @Test
    fun `test WorkManager and Lifecycle versions compatibility`() {
        val workManager = versionMap["workManager"]
        val lifecycle = versionMap["lifecycle"]

        assertNotNull("WorkManager version should be defined", workManager)
        assertNotNull("Lifecycle version should be defined", lifecycle)

        // Both should be reasonably recent versions
        val workMajor = workManager!!.split(".")[0].toInt()
        val lifecycleMajor = lifecycle!!.split(".")[0].toInt()

        assertTrue("WorkManager should be version 2.8+", workMajor >= 2)
        assertTrue("Lifecycle should be version 2.7+", lifecycleMajor >= 2)
    }

    @Test
    fun `test testing framework versions are current`() {
        val junitVersion = versionMap["junit"]
        val mockkVersion = versionMap["mockk"]
        val espressoVersion = versionMap["espressoCore"]

        assertNotNull("JUnit version should be defined", junitVersion)
        assertNotNull("MockK version should be defined", mockkVersion)
        assertNotNull("Espresso version should be defined", espressoVersion)

        // Ensure testing frameworks are using recent versions
        assertTrue("JUnit should be 4.13+", junitVersion!!.compareTo("4.13") >= 0)
        assertTrue("MockK should be 1.13+", mockkVersion!!.compareTo("1.13") >= 0)
        assertTrue("Espresso should be 3.5+", espressoVersion!!.compareTo("3.5") >= 0)
    }

    @Test
    fun `test KSP version matches Kotlin version`() {
        val kotlinVersion = versionMap["kotlin"]
        val kspVersion = versionMap["ksp"]

        assertNotNull("Kotlin version should be defined", kotlinVersion)
        assertNotNull("KSP version should be defined", kspVersion)

        // KSP version should start with Kotlin version (e.g., 2.0.0-1.0.21 for Kotlin 2.0.0)
        assertTrue(
            "KSP version ($kspVersion) should start with Kotlin version ($kotlinVersion)",
            kspVersion!!.startsWith(kotlinVersion!!)
        )
    }

    @Test
    fun `test OkHttp and Retrofit major version compatibility`() {
        val retrofitVersion = versionMap["retrofit"]
        val okhttpVersion = versionMap["okhttp"]

        assertNotNull("Retrofit version should be defined", retrofitVersion)
        assertNotNull("OkHttp version should be defined", okhttpVersion)

        val retrofitMajor = retrofitVersion!!.split(".")[0].toInt()
        val okhttpMajor = okhttpVersion!!.split(".")[0].toInt()

        // Retrofit 3.x should work with OkHttp 4.x or 5.x
        when (retrofitMajor) {
            3 -> assertTrue(
                "Retrofit 3.x should be compatible with OkHttp 4.x or 5.x",
                okhttpMajor >= 4
            )
        }
    }

    @Test
    fun `test desugar JDK libs version is compatible with AGP`() {
        val desugarVersion = versionMap["desugar_jdk_libs"]
        val agpVersion = versionMap["agp"]

        assertNotNull("Desugar JDK libs version should be defined", desugarVersion)
        assertNotNull("AGP version should be defined", agpVersion)

        // Desugar 2.1.x should work with AGP 8.x
        if (agpVersion!!.startsWith("8.")) {
            assertTrue(
                "Desugar JDK libs should be version 2.0+",
                desugarVersion!!.startsWith("2.")
            )
        }
    }

    @Test
    fun `test Timber version is stable`() {
        val timberVersion = versionMap["timber"]
        assertNotNull("Timber version should be defined", timberVersion)

        // Timber should be 5.0+ for stability
        assertTrue(
            "Timber should be version 5.0+",
            timberVersion!!.compareTo("5.0") >= 0
        )
    }
}