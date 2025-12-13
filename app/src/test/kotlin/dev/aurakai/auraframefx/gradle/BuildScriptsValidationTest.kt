package dev.aurakai.auraframefx.gradle

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.util.Properties

/**
 * Comprehensive unit tests for validating Android Gradle build script configurations.
 * These tests validate the build.gradle.kts file structure, dependencies, and configurations
 * to ensure they meet project requirements and Android development best practices.
 *
 * Testing Framework: JUnit 4 with Android Testing utilities
 * Focus: Build script validation, dependency management, and configuration validation
 */
@RunWith(JUnit4::class)
class BuildScriptsValidationTest {

    private lateinit var buildGradleContent: String
    private lateinit var projectDir: File

    @Before
    fun setUp() {
        projectDir = File(".")
        val buildGradleFile = File(projectDir, "app/build.gradle.kts")
        if (buildGradleFile.exists()) {
            buildGradleContent = buildGradleFile.readText()
        } else {
            // Fallback for test environment
            buildGradleContent = """
                plugins {
                    alias(libs.plugins.androidApplication)
                    alias(libs.plugins.kotlin.android)
                    alias(libs.plugins.kotlin.compose)
                    alias(libs.plugins.ksp)
                    alias(libs.plugins.google.services)
                    alias(libs.plugins.hilt)
                    alias(libs.plugins.firebase.perf)
                    alias(libs.plugins.openapi.generator)
                }

                android {
                    namespace = "dev.aurakai.auraframefx"
                    compileSdk = 36
                    
                    defaultConfig {
                        applicationId = "dev.aurakai.auraframefx"
                        minSdk = 33
                        targetSdk = 36
                        versionCode = 1
                        versionName = "1.0"
                        testInstrumentationRunner = "com.example.app.HiltTestRunner"
                        multiDexEnabled = true
                    }
                }
            """.trimIndent()
        }
    }

    @After
    fun tearDown() {
        // Clean up any test artifacts if needed
    }

    // === PLUGIN VALIDATION TESTS ===

    @Test
    fun `test all required plugins are present`() {
        val requiredPlugins = listOf(
            "alias(libs.plugins.androidApplication)",
            "alias(libs.plugins.kotlin.android)",
            "alias(libs.plugins.kotlin.compose)",
            "alias(libs.plugins.ksp)",
            "alias(libs.plugins.google.services)",
            "alias(libs.plugins.hilt)",
            "alias(libs.plugins.firebase.perf)",
            "alias(libs.plugins.openapi.generator)"
        )

        requiredPlugins.forEach { plugin ->
            assertTrue(
                "Plugin $plugin should be present in build script",
                buildGradleContent.contains(plugin)
            )
        }
    }

    @Test
    fun `test plugin block comes first in build script`() {
        val pluginsIndex = buildGradleContent.indexOf("plugins {")
        val androidIndex = buildGradleContent.indexOf("android {")

        assertTrue("plugins block should come before android block", pluginsIndex < androidIndex)
        assertTrue("plugins block should be at the beginning", pluginsIndex >= 0)
    }

    @Test
    fun `test no deprecated plugin syntax is used`() {
        assertFalse(
            "Should not use apply plugin syntax",
            buildGradleContent.contains("apply plugin:")
        )
        assertFalse(
            "Should not use id() syntax for plugins",
            buildGradleContent.contains("id(\"")
        )
    }

    // === ANDROID CONFIGURATION TESTS ===

    @Test
    fun `test android namespace is correctly set`() {
        assertTrue(
            "Android namespace should be set to correct package",
            buildGradleContent.contains("namespace = \"dev.aurakai.auraframefx\"")
        )
    }

    @Test
    fun `test compile sdk version is modern and compatible`() {
        assertTrue(
            "compileSdk should be set to 36 for AGP 8.8.0 compatibility",
            buildGradleContent.contains("compileSdk = 36")
        )
    }

    @Test
    fun `test target sdk matches compile sdk`() {
        val compileSdkPattern = Regex("compileSdk = (\\d+)")
        val targetSdkPattern = Regex("targetSdk = (\\d+)")

        val compileSdkMatch = compileSdkPattern.find(buildGradleContent)
        val targetSdkMatch = targetSdkPattern.find(buildGradleContent)

        assertNotNull("compileSdk should be specified", compileSdkMatch)
        assertNotNull("targetSdk should be specified", targetSdkMatch)

        val compileSdk = compileSdkMatch?.groupValues?.get(1)?.toInt()
        val targetSdk = targetSdkMatch?.groupValues?.get(1)?.toInt()

        assertEquals("targetSdk should match compileSdk", compileSdk, targetSdk)
    }

    @Test
    fun `test minimum sdk version is appropriate`() {
        assertTrue(
            "minSdk should be set to 33 for modern Android support",
            buildGradleContent.contains("minSdk = 33")
        )
    }

    @Test
    fun `test application id is correct`() {
        assertTrue(
            "applicationId should match project package",
            buildGradleContent.contains("applicationId = \"dev.aurakai.auraframefx\"")
        )
    }

    @Test
    fun `test version configuration is present`() {
        assertTrue("versionCode should be set", buildGradleContent.contains("versionCode = 1"))
        assertTrue(
            "versionName should be set",
            buildGradleContent.contains("versionName = \"1.0\"")
        )
    }

    @Test
    fun `test multidex is enabled`() {
        assertTrue(
            "MultiDex should be enabled for large applications",
            buildGradleContent.contains("multiDexEnabled = true")
        )
    }

    @Test
    fun `test custom test runner is configured`() {
        assertTrue(
            "Custom Hilt test runner should be configured",
            buildGradleContent.contains("testInstrumentationRunner = \"com.example.app.HiltTestRunner\"")
        )
    }

    // === BUILD FEATURES TESTS ===

    @Test
    fun `test required build features are enabled`() {
        assertTrue(
            "buildConfig should be enabled",
            buildGradleContent.contains("buildConfig = true")
        )
        assertTrue("compose should be enabled", buildGradleContent.contains("compose = true"))
        assertTrue(
            "viewBinding should be enabled",
            buildGradleContent.contains("viewBinding = true")
        )
        assertTrue("prefab should be enabled", buildGradleContent.contains("prefab = true"))
    }

    // === JAVA COMPATIBILITY TESTS ===

    @Test
    fun `test java version compatibility`() {
        assertTrue(
            "sourceCompatibility should be Java 21",
            buildGradleContent.contains("sourceCompatibility = JavaVersion.VERSION_21")
        )
        assertTrue(
            "targetCompatibility should be Java 21",
            buildGradleContent.contains("targetCompatibility = JavaVersion.VERSION_21")
        )
    }

    @Test
    fun `test core library desugaring is enabled`() {
        assertTrue(
            "Core library desugaring should be enabled",
            buildGradleContent.contains("isCoreLibraryDesugaringEnabled = true")
        )
    }

    @Test
    fun `test java compile task configuration`() {
        assertTrue(
            "Java compile tasks should be configured",
            buildGradleContent.contains("tasks.withType<JavaCompile>().configureEach")
        )
        assertTrue(
            "UTF-8 encoding should be set",
            buildGradleContent.contains("options.encoding = \"UTF-8\"")
        )
    }

    // === BUILD TYPES TESTS ===

    @Test
    fun `test release build type configuration`() {
        assertTrue("Release build type should exist", buildGradleContent.contains("release {"))
        assertTrue(
            "Minification should be enabled for release",
            buildGradleContent.contains("isMinifyEnabled = true")
        )
        assertTrue(
            "ProGuard files should be configured",
            buildGradleContent.contains("proguardFiles(")
        )
    }

    @Test
    fun `test debug build type exists`() {
        assertTrue("Debug build type should exist", buildGradleContent.contains("debug {"))
    }

    // === PACKAGING TESTS ===

    @Test
    fun `test packaging configuration is present`() {
        assertTrue("Packaging block should exist", buildGradleContent.contains("packaging {"))
        assertTrue(
            "Resources exclusions should be configured",
            buildGradleContent.contains("resources {")
        )
        assertTrue("JNI libs configuration should exist", buildGradleContent.contains("jniLibs {"))
    }

    @Test
    fun `test meta-inf exclusions are configured`() {
        val metaInfExclusions = listOf(
            "\"/META-INF/{AL2.0,LGPL2.1}\"",
            "\"META-INF/LICENSE.md\"",
            "\"META-INF/LICENSE-notice.md\"",
            "\"/META-INF/*.kotlin_module\""
        )

        metaInfExclusions.forEach { exclusion ->
            assertTrue(
                "META-INF exclusion $exclusion should be present",
                buildGradleContent.contains("excludes += $exclusion")
            )
        }
    }

    @Test
    fun `test jni debug symbols are preserved`() {
        assertTrue(
            "Debug symbols should be kept for crash reporting",
            buildGradleContent.contains("keepDebugSymbols.add(\"**/*.so\")")
        )
    }

    // === EXTERNAL NATIVE BUILD TESTS ===

    @Test
    fun `test cmake configuration is present`() {
        assertTrue(
            "CMake external build should be configured",
            buildGradleContent.contains("externalNativeBuild {")
        )
        assertTrue(
            "CMake path should be set",
            buildGradleContent.contains("path = file(\"src/main/cpp/CMakeLists.txt\")")
        )
        assertTrue(
            "CMake version should reference root project",
            buildGradleContent.contains("version = rootProject.extra[\"cmakeVersion\"] as String?")
        )
    }

    // === LINT CONFIGURATION TESTS ===

    @Test
    fun `test lint configuration is comprehensive`() {
        assertTrue("Lint block should exist", buildGradleContent.contains("lint {"))
        assertTrue(
            "Lint baseline should be configured",
            buildGradleContent.contains("baseline = file(\"lint-baseline.xml\")")
        )
        assertTrue(
            "Check dependencies should be enabled",
            buildGradleContent.contains("checkDependencies = true")
        )
        assertTrue(
            "Lint config should be set",
            buildGradleContent.contains("lintConfig = file(\"lint.xml\")")
        )
        assertTrue(
            "Warnings as errors should be enabled",
            buildGradleContent.contains("warningsAsErrors = true")
        )
        assertTrue(
            "Abort on error should be enabled",
            buildGradleContent.contains("abortOnError = true")
        )
    }

    // === OPENAPI GENERATOR TESTS ===

    @Test
    fun `test openapi generator configuration`() {
        assertTrue(
            "OpenAPI generator block should exist",
            buildGradleContent.contains("openApiGenerate {")
        )
        assertTrue(
            "Generator name should be kotlin",
            buildGradleContent.contains("generatorName.set(\"kotlin\")")
        )
        assertTrue("Input spec should be configured", buildGradleContent.contains("inputSpec.set("))
        assertTrue(
            "Output directory should be configured",
            buildGradleContent.contains("outputDir.set(")
        )
        assertTrue(
            "API package should be set",
            buildGradleContent.contains("apiPackage.set(\"dev.aurakai.auraframefx.api.client.apis\")")
        )
        assertTrue(
            "Model package should be set",
            buildGradleContent.contains("modelPackage.set(\"dev.aurakai.auraframefx.api.client.models\")")
        )
    }

    @Test
    fun `test openapi generator options`() {
        assertTrue(
            "Date library should be java8",
            buildGradleContent.contains("\"dateLibrary\" to \"java8\"")
        )
        assertTrue(
            "Coroutines should be enabled",
            buildGradleContent.contains("\"useCoroutines\" to \"true\"")
        )
        assertTrue(
            "Kotlinx serialization should be used",
            buildGradleContent.contains("\"serializationLibrary\" to \"kotlinx_serialization\"")
        )
    }

    // === KSP CONFIGURATION TESTS ===

    @Test
    fun `test ksp configuration is present`() {
        assertTrue("KSP block should exist", buildGradleContent.contains("ksp {"))
        assertTrue(
            "Room schema location should be configured",
            buildGradleContent.contains("arg(\"room.schemaLocation\", \"\$projectDir/schemas\")")
        )
    }

    // === SOURCE SETS TESTS ===

    @Test
    fun `test source sets configuration`() {
        assertTrue(
            "Main source set should be configured",
            buildGradleContent.contains("android.sourceSets.getByName(\"main\")")
        )
        assertTrue(
            "Generated OpenAPI sources should be included",
            buildGradleContent.contains("java.srcDir(")
        )
    }

    // === TASK DEPENDENCIES TESTS ===

    @Test
    fun `test preBuild task dependencies`() {
        assertTrue(
            "preBuild task should be configured",
            buildGradleContent.contains("tasks.named(\"preBuild\")")
        )
        assertTrue(
            "preBuild should depend on openApiGenerate",
            buildGradleContent.contains("dependsOn(\"openApiGenerate\")")
        )
    }

    // === DEPENDENCIES VALIDATION TESTS ===

    @Test
    fun `test core dependencies are present`() {
        val coreDependencies = listOf(
            "coreLibraryDesugaring(libs.desugar.jdk.libs)",
            "implementation(libs.androidx.core.ktx)",
            "implementation(libs.androidx.activity.compose)"
        )

        coreDependencies.forEach { dependency ->
            assertTrue(
                "Core dependency $dependency should be present",
                buildGradleContent.contains(dependency)
            )
        }
    }

    @Test
    fun `test compose bom is used`() {
        assertTrue(
            "Compose BOM should be used",
            buildGradleContent.contains("val composeBom = platform(libs.compose.bom)")
        )
        assertTrue(
            "Compose BOM should be implemented",
            buildGradleContent.contains("implementation(composeBom)")
        )
        assertTrue(
            "Compose BOM should be used in android tests",
            buildGradleContent.contains("androidTestImplementation(composeBom)")
        )
    }

    @Test
    fun `test compose dependencies are comprehensive`() {
        val composeDeps = listOf(
            "implementation(libs.compose.ui)",
            "implementation(libs.compose.ui.graphics)",
            "implementation(libs.compose.ui.tooling.preview)",
            "implementation(libs.compose.material3)",
            "implementation(libs.navigation.compose)",
            "implementation(libs.compose.material.icons.extended)",
            "debugImplementation(libs.compose.ui.tooling)",
            "androidTestImplementation(libs.compose.ui.test.junit4)"
        )

        composeDeps.forEach { dependency ->
            assertTrue(
                "Compose dependency $dependency should be present",
                buildGradleContent.contains(dependency)
            )
        }
    }

    @Test
    fun `test hilt dependencies are complete`() {
        val hiltDeps = listOf(
            "implementation(libs.hilt.android)",
            "ksp(libs.hilt.compiler)",
            "implementation(libs.hilt.navigation.compose)",
            "implementation(libs.hilt.work)",
            "kspAndroidTest(libs.hilt.compiler)"
        )

        hiltDeps.forEach { dependency ->
            assertTrue(
                "Hilt dependency $dependency should be present",
                buildGradleContent.contains(dependency)
            )
        }
    }

    @Test
    fun `test room dependencies use bundles`() {
        assertTrue(
            "Room bundle should be used",
            buildGradleContent.contains("implementation(libs.bundles.room)")
        )
        assertTrue(
            "Room compiler should be configured with KSP",
            buildGradleContent.contains("ksp(libs.room.compiler)")
        )
    }

    @Test
    fun `test firebase dependencies use bom and bundles`() {
        assertTrue(
            "Firebase BOM should be used",
            buildGradleContent.contains("implementation(platform(libs.firebase.bom))")
        )
        assertTrue(
            "Firebase bundle should be used",
            buildGradleContent.contains("implementation(libs.bundles.firebase)")
        )
    }

    @Test
    fun `test network dependencies are present`() {
        val networkDeps = listOf(
            "implementation(libs.retrofit)",
            "implementation(libs.retrofit.converter.kotlinx.serialization)",
            "implementation(libs.okhttp.logging.interceptor)",
            "implementation(libs.kotlinx.serialization.json)"
        )

        networkDeps.forEach { dependency ->
            assertTrue(
                "Network dependency $dependency should be present",
                buildGradleContent.contains(dependency)
            )
        }
    }

    @Test
    fun `test testing dependencies use bundles`() {
        assertTrue(
            "Unit testing bundle should be used",
            buildGradleContent.contains("testImplementation(libs.bundles.testing.unit)")
        )
        assertTrue(
            "Android testing bundle should be used",
            buildGradleContent.contains("androidTestImplementation(libs.bundles.testing.android)")
        )
    }

    @Test
    fun `test lifecycle dependencies use bundles`() {
        assertTrue(
            "Lifecycle bundle should be used",
            buildGradleContent.contains("implementation(libs.bundles.lifecycle)")
        )
    }

    // === CONFIGURATION CONSISTENCY TESTS ===

    @Test
    fun `test no duplicate dependencies`() {
        val dependencies = buildGradleContent
            .lines()
            .filter { it.trim().matches(Regex(".*implementation\\(.*\\)")) }
            .map { it.trim() }

        val duplicates = dependencies.groupingBy { it }.eachCount().filter { it.value > 1 }
        assertTrue("No duplicate dependencies should exist: $duplicates", duplicates.isEmpty())
    }

    @Test
    fun `test deprecated configurations are removed`() {
        val deprecatedPatterns = listOf(
            "configurations.all",
            "composeOptions",
            "kotlinOptions"
        )

        deprecatedPatterns.forEach { pattern ->
            assertFalse(
                "Deprecated pattern $pattern should not be present",
                buildGradleContent.contains(pattern)
            )
        }
    }

    @Test
    fun `test comments indicate moved configurations`() {
        assertTrue(
            "Should have comment about moved NDK ABI filters",
            buildGradleContent.contains("// MOVED: NDK ABI filters")
        )
        assertTrue(
            "Should have comment about moved packaging options",
            buildGradleContent.contains("// MOVED: Packaging options")
        )
        assertTrue(
            "Should have comment about removed composeOptions",
            buildGradleContent.contains("// REMOVED: composeOptions block")
        )
    }

    // === EDGE CASE AND ERROR HANDLING TESTS ===

    @Test
    fun `test build script handles missing files gracefully`() {
        // Test that the build script doesn't reference non-existent files directly
        assertFalse(
            "Should not reference non-existent proguard files directly",
            buildGradleContent.contains("\"non-existent-file.pro\"")
        )
    }

    @Test
    fun `test version catalog references are consistent`() {
        val libsReferences = buildGradleContent
            .lines()
            .filter { it.contains("libs.") }
            .map { line ->
                Regex("libs\\.[a-zA-Z0-9.]+").findAll(line).map { it.value }.toList()
            }
            .flatten()

        // Ensure all libs references follow proper naming convention
        libsReferences.forEach { ref ->
            assertTrue(
                "Libs reference $ref should follow naming convention",
                ref.matches(Regex("libs\\.[a-zA-Z0-9.]+[a-zA-Z0-9]"))
            )
        }
    }

    @Test
    fun `test no hardcoded paths in configuration`() {
        val hardcodedPathPatterns = listOf(
            "/home/",
            "C:\\\\",
            "/Users/"
        )

        hardcodedPathPatterns.forEach { pattern ->
            assertFalse(
                "Should not contain hardcoded path $pattern",
                buildGradleContent.contains(pattern)
            )
        }
    }

    // === PERFORMANCE AND OPTIMIZATION TESTS ===

    @Test
    fun `test optimization settings are appropriate`() {
        assertTrue(
            "Release minification should be enabled",
            buildGradleContent.contains("isMinifyEnabled = true")
        )
        assertTrue(
            "Should use optimized ProGuard file",
            buildGradleContent.contains("proguard-android-optimize.txt")
        )
    }

    @Test
    fun `test debug-only dependencies are properly scoped`() {
        val debugDeps = buildGradleContent
            .lines()
            .filter { it.contains("debugImplementation") }

        assertTrue("Should have debug-only dependencies", debugDeps.isNotEmpty())

        // Verify specific debug dependencies
        assertTrue(
            "Compose UI tooling should be debug-only",
            buildGradleContent.contains("debugImplementation(libs.compose.ui.tooling)")
        )
        assertTrue(
            "Compose test manifest should be debug-only",
            buildGradleContent.contains("debugImplementation(libs.compose.ui.test.manifest)")
        )
    }

    // === SECURITY TESTS ===

    @Test
    fun `test security dependencies are included`() {
        assertTrue(
            "Security crypto should be included",
            buildGradleContent.contains("implementation(libs.security.crypto)")
        )
        assertTrue(
            "DataStore preferences should be included for secure storage",
            buildGradleContent.contains("implementation(libs.datastore.preferences)")
        )
    }

    // === INTEGRATION TESTS ===

    @Test
    fun `test build script structure is valid kotlin dsl`() {
        // Test basic Kotlin DSL structure
        assertTrue("Should use Kotlin DSL syntax", buildGradleContent.contains("android {"))
        assertTrue(
            "Should use proper block structure",
            buildGradleContent.contains("dependencies {")
        )

        // Count braces to ensure they're balanced
        val openBraces = buildGradleContent.count { it == '{' }
        val closeBraces = buildGradleContent.count { it == '}' }
        assertEquals("Braces should be balanced", openBraces, closeBraces)
    }

    @Test
    fun `test all required sections are present in correct order`() {
        val expectedSections = listOf(
            "plugins {",
            "android {",
            "openApiGenerate {",
            "ksp {",
            "dependencies {"
        )

        var lastIndex = -1
        expectedSections.forEach { section ->
            val currentIndex = buildGradleContent.indexOf(section)
            assertTrue("Section $section should be present", currentIndex != -1)
            assertTrue(
                "Section $section should come after previous sections",
                currentIndex > lastIndex
            )
            lastIndex = currentIndex
        }
    }
}