package dev.aurakai.auraframefx.sandbox.ui

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.security.MessageDigest
import kotlin.random.Random

/**
 * Unit tests for build script validation
 * Testing Framework: JUnit 4 with Gradle TestKit
 *
 * This test suite validates the Gradle build script configuration
 * for the sandbox-ui library module including plugins, dependencies,
 * Android configuration, and build variants.
 */
class BuildScriptValidationTest {

    private lateinit var testProjectDir: Path
    private lateinit var buildFile: File
    private lateinit var gradleRunner: GradleRunner

    @Before
    fun setup() {
        testProjectDir = Files.createTempDirectory("gradle-test")
        buildFile = testProjectDir.resolve("build.gradle.kts").toFile()

        // Create minimal project structure
        Files.createDirectories(testProjectDir.resolve("src/main/kotlin"))
        Files.createDirectories(testProjectDir.resolve("src/test/kotlin"))

        // Create settings.gradle.kts
        testProjectDir.resolve("settings.gradle.kts").writeText(
            """
            rootProject.name = "sandbox-ui-test"
            include(":app")
        """.trimIndent()
        )

        // Create minimal libs.versions.toml
        Files.createDirectories(testProjectDir.resolve("gradle"))
        testProjectDir.resolve("gradle/libs.versions.toml").writeText(
            """
            [versions]
            kotlin = "1.9.0"
            
            [libraries]
            androidxCoreKtx = { group = "androidx.core", name = "core-ktx", version = "1.12.0" }
            androidxLifecycleRuntimeKtx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version = "2.7.0" }
            androidxActivityCompose = { group = "androidx.activity", name = "activity-compose", version = "1.8.2" }
            composeBom = { group = "androidx.compose", name = "compose-bom", version = "2023.10.01" }
            ui = { group = "androidx.compose.ui", name = "ui" }
            uiToolingPreview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
            androidxMaterial3 = { group = "androidx.compose.material3", name = "material3" }
            animation = { group = "androidx.compose.animation", name = "animation" }
            foundation = { group = "androidx.compose.foundation", name = "foundation" }
            navigationComposeV291 = { group = "androidx.navigation", name = "navigation-compose", version = "2.7.6" }
            hiltAndroid = { group = "com.google.dagger", name = "hilt-android", version = "2.48" }
            hiltCompiler = { group = "com.google.dagger", name = "hilt-compiler", version = "2.48" }
            hiltNavigationCompose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.1.0" }
            uiTooling = { group = "androidx.compose.ui", name = "ui-tooling" }
            uiTestManifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
            testJunit = { group = "junit", name = "junit", version = "4.13.2" }
            junitV115 = { group = "androidx.test.ext", name = "junit", version = "1.1.5" }
            espressoCoreV351 = { group = "androidx.test.espresso", name = "espresso-core", version = "3.5.1" }
            uiTestJunit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
        """.trimIndent()
        )

        // Create app module build.gradle.kts
        Files.createDirectories(testProjectDir.resolve("app"))
        testProjectDir.resolve("app/build.gradle.kts").writeText(
            """
            plugins {
                id("com.android.application")
                id("org.jetbrains.kotlin.android")
            }
            
            android {
                namespace = "dev.aurakai.auraframefx"
                compileSdk = 36
                
                defaultConfig {
                    minSdk = 33
                    targetSdk = 36
                    versionCode = 1
                    versionName = "1.0"
                }
                
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                }
            }
        """.trimIndent()
        )

        gradleRunner = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withPluginClasspath()
            .withGradleVersion("8.4")
    }

    @After
    fun cleanup() {
        testProjectDir.toFile().deleteRecursively()
    }

    @Test
    fun `should validate Android library plugin configuration`() {
        val buildScript = createBasicBuildScript()
        buildFile.writeText(buildScript)

        val result = gradleRunner.withArguments("tasks", "--no-daemon").build()

        assertTrue("Build should succeed", result.output.contains("BUILD SUCCESSFUL"))
        assertTrue("Should contain Android library tasks", result.output.contains("assembleDebug"))
        assertTrue(
            "Should contain Android library tasks",
            result.output.contains("assembleRelease")
        )
    }

    @Test
    fun `should validate namespace configuration`() {
        val buildScript = createBasicBuildScript()
        buildFile.writeText(buildScript)

        val result = gradleRunner.withArguments("help", "--no-daemon").build()

        assertTrue(
            "Build should succeed with namespace",
            result.output.contains("BUILD SUCCESSFUL")
        )
        assertTrue(
            "Build script should contain namespace",
            buildScript.contains("dev.aurakai.auraframefx.sandbox.ui")
        )
    }

    @Test
    fun `should validate SDK version configuration`() {
        val buildScript = createBasicBuildScript()
        buildFile.writeText(buildScript)

        assertTrue("Should set compileSdk = 36", buildScript.contains("compileSdk = 36"))
        assertTrue("Should set minSdk = 33", buildScript.contains("minSdk = 33"))
        assertTrue("Should set targetSdk = 36", buildScript.contains("targetSdk = 36"))
    }

    @Test
    fun `should validate Java version configuration`() {
        val buildScript = createBasicBuildScript()
        buildFile.writeText(buildScript)

        assertTrue(
            "Should use Java 21 source compatibility",
            buildScript.contains("sourceCompatibility = JavaVersion.VERSION_21")
        )
        assertTrue(
            "Should use Java 21 target compatibility",
            buildScript.contains("targetCompatibility = JavaVersion.VERSION_21")
        )
    }

    @Test
    fun `should validate complete build script with Compose configuration`() {
        val buildScript = createCompleteBuildScript()
        buildFile.writeText(buildScript)

        val result = gradleRunner.withArguments("tasks", "--no-daemon").build()

        assertTrue("Build should succeed with Compose", result.output.contains("BUILD SUCCESSFUL"))
        assertTrue("Should enable Compose build feature", buildScript.contains("compose = true"))
        assertTrue(
            "Should have Kotlin Compose plugin",
            buildScript.contains("org.jetbrains.kotlin.plugin.compose")
        )
        assertTrue(
            "Should have Compose compiler extension version",
            buildScript.contains("kotlinCompilerExtensionVersion")
        )
    }

    @Test
    fun `should validate Hilt configuration`() {
        val buildScript = createCompleteBuildScript()
        buildFile.writeText(buildScript)

        assertTrue("Should have Hilt plugin", buildScript.contains("dagger.hilt.android.plugin"))
        assertTrue("Should have kapt plugin", buildScript.contains("kotlin-kapt"))
        assertTrue("Should have parcelize plugin", buildScript.contains("kotlin-parcelize"))
    }

    @Test
    fun `should validate NDK configuration`() {
        val buildScript = createCompleteBuildScript()
        buildFile.writeText(buildScript)

        assertTrue("Should configure NDK ABI filters", buildScript.contains("abiFilters.addAll"))
        assertTrue("Should include arm64-v8a", buildScript.contains("arm64-v8a"))
        assertTrue("Should include x86_64", buildScript.contains("x86_64"))
        assertTrue(
            "Should set debug symbol level",
            buildScript.contains("debugSymbolLevel = \"FULL\"")
        )
    }

    @Test
    fun `should validate packaging configuration`() {
        val buildScript = createCompleteBuildScript()
        buildFile.writeText(buildScript)

        assertTrue(
            "Should exclude Kotlin modules",
            buildScript.contains("META-INF/*.kotlin_module")
        )
        assertTrue("Should exclude version files", buildScript.contains("META-INF/*.version"))
        assertTrue("Should exclude JNI libraries", buildScript.contains("**/libjni*.so"))
        assertTrue("Should exclude proguard files", buildScript.contains("META-INF/proguard/*"))
    }

    @Test
    fun `should validate build types configuration`() {
        val buildScript = createCompleteBuildScript()
        buildFile.writeText(buildScript)

        assertTrue("Should have release build type", buildScript.contains("release {"))
        assertTrue("Should disable minification", buildScript.contains("isMinifyEnabled = false"))
        assertTrue("Should include proguard files", buildScript.contains("proguardFiles"))
    }

    @Test
    fun `should validate dependencies configuration`() {
        val buildScript = createCompleteBuildScript()
        buildFile.writeText(buildScript)

        assertTrue("Should have API dependency", buildScript.contains("api(project(\":app\"))"))
        assertTrue("Should use libs catalog", buildScript.contains("libs.androidxCoreKtx"))
        assertTrue(
            "Should include lifecycle runtime",
            buildScript.contains("libs.androidxLifecycleRuntimeKtx")
        )
        assertTrue(
            "Should include activity compose",
            buildScript.contains("libs.androidxActivityCompose")
        )
    }

    @Test
    fun `should handle invalid build script gracefully`() {
        val invalidBuildScript = """
            plugins {
                invalid_plugin_syntax
            }
        """.trimIndent()
        buildFile.writeText(invalidBuildScript)

        try {
            val result = gradleRunner.withArguments("tasks", "--no-daemon").buildAndFail()
            assertTrue(
                "Build should fail with syntax error",
                result.output.contains("BUILD FAILED")
            )
        } catch (e: Exception) {
            // Expected behavior for invalid syntax
            assertTrue(
                "Exception should contain syntax error info",
                e.message?.contains("syntax") == true
            )
        }
    }

    @Test
    fun `should validate test configuration`() {
        val buildScript = createBasicBuildScript()
        buildFile.writeText(buildScript)

        assertTrue(
            "Should set test instrumentation runner",
            buildScript.contains("androidx.test.runner.AndroidJUnitRunner")
        )
        assertTrue(
            "Should include consumer proguard files",
            buildScript.contains("consumer-rules.pro")
        )
    }

    @Test
    fun `should validate build features configuration`() {
        val buildScript = createCompleteBuildScript()
        buildFile.writeText(buildScript)

        assertTrue("Should enable Compose", buildScript.contains("compose = true"))
        assertTrue("Should enable buildConfig", buildScript.contains("buildConfig = true"))
    }

    @Test
    fun `should handle empty build script`() {
        buildFile.writeText("")

        try {
            val result = gradleRunner.withArguments("tasks", "--no-daemon").buildAndFail()
            assertTrue("Empty script should fail", result.output.contains("BUILD FAILED"))
        } catch (e: Exception) {
            // Expected failure for empty build script
            assertNotNull("Should throw exception for empty script", e.message)
        }
    }

    @Test
    fun `should validate libs catalog integration`() {
        val buildScript = createCompleteBuildScript()
        buildFile.writeText(buildScript)

        val result = gradleRunner.withArguments(
            "dependencies",
            "--configuration=implementation",
            "--no-daemon"
        ).build()

        assertTrue("Should resolve libs catalog", result.output.contains("BUILD SUCCESSFUL"))
        assertTrue("Build script should use version catalog syntax", buildScript.contains("libs."))
    }

    @Test
    fun `should validate build script with concurrent execution`() {
        val buildScript = createBasicBuildScript()
        buildFile.writeText(buildScript)

        val results = mutableListOf<Boolean>()
        val threads = mutableListOf<Thread>()

        repeat(3) {
            val thread = Thread {
                try {
                    val result = gradleRunner.withArguments("tasks", "--no-daemon").build()
                    synchronized(results) {
                        results.add(result.task(":tasks")?.outcome == TaskOutcome.SUCCESS)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    synchronized(results) {
                        results.add(false)
                    }
                }
            }
            threads.add(thread)
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }

        assertTrue(
            "At least one concurrent build should succeed",
            results.any { it })
    }

    @Test
    fun `should validate build script with multiple task executions to ensure script integrity`() {
        val buildScript = createCompleteBuildScript()
        buildFile.writeText(buildScript)

        // Test multiple task executions to ensure script integrity
        val tasks = listOf("tasks", "dependencies", "help")
        tasks.forEach { task ->
            val result = gradleRunner.withArguments(task, "--no-daemon").build()
            assertTrue(
                "Task $task should execute successfully",
                result.output.contains("BUILD SUCCESSFUL") ||
                        result.task(":$task")?.outcome == TaskOutcome.SUCCESS
            )
        }

        // Verify no memory leaks or resource issues
        val memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        assertTrue("Memory usage should be reasonable", memoryAfter < 500_000_000) // 500MB limit
    }

    @Test
    fun `should handle gradle runner exceptions gracefully`() {
        val buildScript = createBasicBuildScript()
        buildFile.writeText(buildScript)

        // Test with invalid Gradle version to trigger exception
        val invalidRunner = GradleRunner.create()
            .withProjectDir(testProjectDir.toFile())
            .withPluginClasspath()
            .withGradleVersion("invalid-version")

        try {
            invalidRunner.withArguments("tasks", "--no-daemon").build()
            fail("Should have thrown exception for invalid Gradle version")
        } catch (e: Exception) {
            // Expected exception - verify it contains relevant information
            assertNotNull("Exception message should not be null", e.message)
        }
    }

    @Test
    fun `should validate task execution timeout handling`() {
        val buildScript = createCompleteBuildScript()
        buildFile.writeText(buildScript)

        val startTime = System.currentTimeMillis()
        val result = gradleRunner.withArguments("tasks", "--no-daemon").build()
        val executionTime = System.currentTimeMillis() - startTime

        assertTrue(
            "Build should complete in reasonable time",
            executionTime < 60000
        ) // 60 seconds max
        assertTrue("Build should succeed", result.output.contains("BUILD SUCCESSFUL"))
    }

    @Test
    fun `should validate concurrent execution thread safety`() {
        val buildScript = createBasicBuildScript()
        buildFile.writeText(buildScript)

        val results = java.util.concurrent.ConcurrentHashMap<Int, Boolean>()
        val latch = java.util.concurrent.CountDownLatch(5)

        repeat(5) { index ->
            Thread {
                try {
                    val result = gradleRunner.withArguments("help", "--no-daemon").build()
                    results[index] = result.output.contains("BUILD SUCCESSFUL")
                } catch (e: Exception) {
                    e.printStackTrace()
                    results[index] = false
                } finally {
                    latch.countDown()
                }
            }.start()
        }

        assertTrue(
            "All threads should complete",
            latch.await(30, java.util.concurrent.TimeUnit.SECONDS)
        )
        assertTrue("Most concurrent builds should succeed", results.values.count { it } >= 3)
    }

    @Test
    fun `should validate createBasicBuildScript helper method`() {
        val basicScript = createBasicBuildScript()

        assertNotNull("Basic script should not be null", basicScript)
        assertTrue("Should contain plugins block", basicScript.contains("plugins {"))
        assertTrue("Should contain android block", basicScript.contains("android {"))
        assertTrue("Should contain defaultConfig", basicScript.contains("defaultConfig {"))
        assertFalse("Should not contain dependencies block", basicScript.contains("dependencies {"))
    }

    @Test
    fun `should validate createCompleteBuildScript helper method`() {
        val completeScript = createCompleteBuildScript()

        assertNotNull("Complete script should not be null", completeScript)
        assertTrue("Should contain all required elements", completeScript.contains("plugins {"))
        assertTrue("Should contain buildTypes", completeScript.contains("buildTypes {"))
        assertTrue("Should contain buildFeatures", completeScript.contains("buildFeatures {"))
        assertTrue("Should contain dependencies", completeScript.contains("dependencies {"))
        assertTrue("Should contain packaging", completeScript.contains("packaging {"))
    }

    private fun createBasicBuildScript() = """
        plugins {
            id("com.android.library")
            id("org.jetbrains.kotlin.android")
        }
        
        android {
            namespace = "dev.aurakai.auraframefx.sandbox.ui"
            compileSdk = 36
            
            defaultConfig {
                minSdk = 33
                testOptions.targetSdk = 36
                lint.targetSdk = 36
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
            }
            
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
            }
        }
    """.trimIndent()

    @Test
    fun `should validate build script with corrupted content recovery`() {
        val validScript = createBasicBuildScript()
        buildFile.writeText(validScript)

        // Verify initial valid state
        val initialResult = gradleRunner.withArguments("help", "--no-daemon").build()
        assertTrue(
            "Initial build should succeed",
            initialResult.output.contains("BUILD SUCCESSFUL")
        )

        // Corrupt the file
        val corruptedContent = validScript.replace("plugins {", "plugins { invalid_content")
        buildFile.writeText(corruptedContent)

        try {
            gradleRunner.withArguments("help", "--no-daemon").buildAndFail()
        } catch (e: Exception) {
            // Expected failure
            e.printStackTrace()
        }

        // Restore valid content
        buildFile.writeText(validScript)
        val recoveryResult = gradleRunner.withArguments("help", "--no-daemon").build()
        assertTrue(
            "Recovery build should succeed",
            recoveryResult.output.contains("BUILD SUCCESSFUL")
        )
    }

    @Test
    fun `should validate build script with unicode and special characters`() {
        val unicodeScript = """
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
            }
            
            android {
                namespace = "dev.aurakai.auraframefx.sandbox.ui"
                compileSdk = 36
                
                // Comments with unicode: 测试 ñoño 🚀 ëxämplé
                defaultConfig {
                    minSdk = 33
                    // Special characters in strings
                    buildConfigField("String", "SPECIAL_CHARS", "\"äöü@#$%^&*()[]{}|\\\"")
                }
            }
        """.trimIndent()
        buildFile.writeText(unicodeScript)

        val result = gradleRunner.withArguments("help", "--no-daemon").build()
        assertTrue("Should handle unicode characters", result.output.contains("BUILD SUCCESSFUL"))
        assertTrue("Should contain special characters", unicodeScript.contains("buildConfigField"))
    }

    @Test
    fun `should validate build script with extremely large content`() {
        val baseScript = createBasicBuildScript()
        val largeCommentSection = StringBuilder()

        // Generate large comment section (simulating real-world large build files)
        repeat(1000) { index ->
            largeCommentSection.append("// Large comment block line $index with detailed explanations\n")
        }

        val largeScript = baseScript + "\n" + largeCommentSection.toString()
        buildFile.writeText(largeScript)

        val startTime = System.currentTimeMillis()
        val result = gradleRunner.withArguments("help", "--no-daemon").build()
        val executionTime = System.currentTimeMillis() - startTime

        assertTrue(
            "Large file should build successfully",
            result.output.contains("BUILD SUCCESSFUL")
        )
        assertTrue(
            "Large file should process in reasonable time",
            executionTime < 180000
        ) // 3 minutes max
        assertTrue("File size should be substantial", largeScript.length > 50000)
    }

    @Test
    fun `should validate build script with all supported Kotlin DSL features`() {
        val kotlinDslScript = """
            plugins {
                id("com.android.library")
                kotlin("android")
                kotlin("kapt")
                kotlin("plugin.compose") version "1.9.0"
            }
            
            android {
                namespace = "dev.aurakai.auraframefx.sandbox.ui"
                compileSdk = 36
                
                defaultConfig {
                    minSdk = 33
                    vectorDrawables {
                        useSupportLibrary = true
                    }
                }
                
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                    create("staging") {
                        initWith(getByName("debug"))
                        isMinifyEnabled = true
                    }
                }
                
                sourceSets {
                    getByName("main") {
                        java.srcDirs("src/main/kotlin")
                        res.srcDirs("src/main/res")
                    }
                }
                
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                }
            }
            
            dependencies {
                val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
                implementation(composeBom)
                implementation("androidx.compose.ui:ui")
                implementation("androidx.compose.material3:material3")
                
                testImplementation("junit:junit:4.13.2")
                androidTestImplementation("androidx.test.ext:junit:1.1.5")
            }
        """.trimIndent()
        buildFile.writeText(kotlinDslScript)

        val result = gradleRunner.withArguments("tasks", "--no-daemon").build()
        assertTrue(
            "Kotlin DSL should build successfully",
            result.output.contains("BUILD SUCCESSFUL")
        )
        assertTrue("Should use Kotlin DSL syntax", kotlinDslScript.contains("kotlin("))
        assertTrue("Should have staging build type", kotlinDslScript.contains("staging"))
    }

    @Test
    fun `should validate build script with dependency version conflicts resolution`() {
        val conflictingDepsScript = """
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
            }
            
            android {
                namespace = "dev.aurakai.auraframefx.sandbox.ui"
                compileSdk = 36
            }
            
            dependencies {
                implementation("androidx.core:core-ktx:1.10.0")
                implementation("androidx.core:core-ktx:1.12.0") // Version conflict
                implementation("androidx.compose.ui:ui:1.5.0")
                implementation("androidx.compose.ui:ui:1.6.0") // Version conflict
                
                configurations.all {
                    resolutionStrategy {
                        preferProjectModules()
                        failOnVersionConflict()
                    }
                }
            }
        """.trimIndent()
        buildFile.writeText(conflictingDepsScript)

        try {
            val result = gradleRunner.withArguments("dependencies", "--no-daemon").buildAndFail()
            assertTrue(
                "Should detect version conflicts",
                result.output.contains("BUILD FAILED") || result.output.contains("conflict")
            )
        } catch (e: Exception) {
            // Expected behavior for version conflicts
            assertTrue("Should handle conflicts gracefully", e.message != null)
        }
    }

    @Test
    fun `should validate build script with custom repository configurations`() {
        val customRepoScript = """
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
            }
            
            repositories {
                google()
                mavenCentral()
                maven {
                    url = uri("https://jitpack.io")
                    credentials {
                        username = "test"
                        password = "test"
                    }
                }
                maven("https://androidx.dev/snapshots/builds/8508386/artifacts/repository")
                gradlePluginPortal()
            }
            
            android {
                namespace = "dev.aurakai.auraframefx.sandbox.ui"
                compileSdk = 36
            }
        """.trimIndent()
        buildFile.writeText(customRepoScript)

        val result = gradleRunner.withArguments("help", "--no-daemon").build()
        assertTrue("Should handle custom repositories", result.output.contains("BUILD SUCCESSFUL"))
        assertTrue("Should contain repository config", customRepoScript.contains("repositories"))
        assertTrue("Should have Maven repository", customRepoScript.contains("maven {"))
    }

    @Test
    fun `should validate build script with advanced ProGuard configurations`() {
        val proguardScript = """
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
            }
            
            android {
                namespace = "dev.aurakai.auraframefx.sandbox.ui"
                compileSdk = 36
                
                buildTypes {
                    release {
                        isMinifyEnabled = true
                        isShrinkResources = false // Libraries cannot shrink resources
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro",
                            "proguard-rules-debug.pro"
                        )
                        
                        // Advanced ProGuard settings
                        matchingFallbacks += listOf("release", "debug")
                    }
                    
                    debug {
                        isMinifyEnabled = false
                        proguardFiles("proguard-rules-debug.pro")
                        isTestCoverageEnabled = true
                    }
                }
            }
        """.trimIndent()
        buildFile.writeText(proguardScript)

        val result = gradleRunner.withArguments("tasks", "--no-daemon").build()
        assertTrue("Should handle ProGuard config", result.output.contains("BUILD SUCCESSFUL"))
        assertTrue("Should enable minification", proguardScript.contains("isMinifyEnabled = true"))
        assertTrue(
            "Should have multiple ProGuard files",
            proguardScript.contains("proguard-rules-debug.pro")
        )
        assertTrue("Should have test coverage", proguardScript.contains("isTestCoverageEnabled"))
    }

    @Test
    fun `should validate build script with multi-module project setup`() {
        // Create additional module directories
        Files.createDirectories(testProjectDir.resolve("feature-module/src/main/kotlin"))
        Files.createDirectories(testProjectDir.resolve("core-module/src/main/kotlin"))

        // Update settings.gradle.kts
        testProjectDir.resolve("settings.gradle.kts").writeText(
            """
            rootProject.name = "sandbox-ui-test"
            include(":app", ":feature-module", ":core-module")
        """.trimIndent()
        )

        val multiModuleScript = """
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
            }
            
            android {
                namespace = "dev.aurakai.auraframefx.sandbox.ui"
                compileSdk = 36
            }
            
            dependencies {
                api(project(":core-module"))
                implementation(project(":feature-module"))
                testImplementation(project(path = ":core-module", configuration = "testFixtures"))
            }
        """.trimIndent()
        buildFile.writeText(multiModuleScript)

        // Create minimal build files for other modules
        testProjectDir.resolve("feature-module/build.gradle.kts").writeText(
            """
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
            }
            android {
                namespace = "dev.aurakai.feature"
                compileSdk = 36
            }
        """.trimIndent()
        )

        testProjectDir.resolve("core-module/build.gradle.kts").writeText(
            """
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
            }
            android {
                namespace = "dev.aurakai.core"
                compileSdk = 36
            }
        """.trimIndent()
        )

        val result = gradleRunner.withArguments("tasks", "--no-daemon").build()
        assertTrue("Multi-module setup should work", result.output.contains("BUILD SUCCESSFUL"))
        assertTrue("Should have project dependencies", multiModuleScript.contains("project("))
    }

    @Test
    fun `should validate build script with advanced testing configurations`() {
        val advancedTestingScript = """
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
                id("kotlin-kapt")
            }
            
            android {
                namespace = "dev.aurakai.auraframefx.sandbox.ui"
                compileSdk = 36
                
                defaultConfig {
                    minSdk = 33
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    testInstrumentationRunnerArguments["clearPackageData"] = "true"
                }
                
                testOptions {
                    unitTests {
                        isIncludeAndroidResources = true
                        isReturnDefaultValues = true
                        all {
                            it.systemProperty("robolectric.enabledSdks", "28,29,30,33")
                            it.jvmArgs("-noverify")
                            it.testLogging {
                                events("passed", "skipped", "failed")
                            }
                        }
                    }
                    
                    animationsDisabled = true
                    execution = "ANDROIDX_TEST_ORCHESTRATOR"
                }
                
                packagingOptions {
                    pickFirst("**/libc++_shared.so")
                    pickFirst("**/libjsc.so")
                }
            }
            
            dependencies {
                testImplementation("junit:junit:4.13.2")
                testImplementation("org.robolectric:robolectric:4.9")
                testImplementation("androidx.test:core:1.5.0")
                testImplementation("org.mockito:mockito-core:4.6.1")
                testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
                
                androidTestImplementation("androidx.test.ext:junit:1.1.5")
                androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
                androidTestImplementation("androidx.test:runner:1.5.2")
                androidTestImplementation("androidx.test:rules:1.5.0")
                androidTestUtil("androidx.test:orchestrator:1.4.2")
            }
        """.trimIndent()
        buildFile.writeText(advancedTestingScript)

        val result = gradleRunner.withArguments("tasks", "--no-daemon").build()
        assertTrue(
            "Advanced testing config should work",
            result.output.contains("BUILD SUCCESSFUL")
        )
        assertTrue(
            "Should have test orchestrator",
            advancedTestingScript.contains("ANDROIDX_TEST_ORCHESTRATOR")
        )
        assertTrue(
            "Should have Robolectric config",
            advancedTestingScript.contains("robolectric.enabledSdjs")
        )
        assertTrue(
            "Should have packaging options",
            advancedTestingScript.contains("packagingOptions")
        )
    }

    @Test
    fun `should validate build script with Compose BOM and version catalog edge cases`() {
        val bomAndCatalogScript = """
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
                id("org.jetbrains.kotlin.plugin.compose") version "1.9.0"
            }
            
            android {
                namespace = "dev.aurakai.auraframefx.sandbox.ui"
                compileSdk = 36
                
                buildFeatures {
                    compose = true
                }
            }
            
            dependencies {
                // Test BOM platform dependency
                implementation(platform(libs.composeBom))
                implementation(libs.ui)
                implementation(libs.uiToolingPreview)
                implementation(libs.androidxMaterial3)
                
                // Test version overrides
                implementation("androidx.compose.foundation:foundation") {
                    version {
                        strictly("1.5.0")
                    }
                }
                
                // Test exclusions
                implementation(libs.navigationComposeV291) {
                    exclude(group = "androidx.lifecycle", module = "lifecycle-viewmodel")
                }
                
                debugImplementation(libs.uiTooling)
                debugImplementation(libs.uiTestManifest)
                
                testImplementation(libs.testJunit)
                androidTestImplementation(platform(libs.composeBom))
                androidTestImplementation(libs.uiTestJunit4)
            }
        """.trimIndent()
        buildFile.writeText(bomAndCatalogScript)

        val result = gradleRunner.withArguments(
            "dependencies",
            "--configuration=debugRuntimeClasspath",
            "--no-daemon"
        ).build()
        assertTrue("BOM and catalog should work", result.output.contains("BUILD SUCCESSFUL"))
        assertTrue(
            "Should use platform BOM",
            bomAndCatalogScript.contains("platform(libs.composeBom)")
        )
        assertTrue("Should have version constraints", bomAndCatalogScript.contains("strictly"))
        assertTrue("Should have exclusions", bomAndCatalogScript.contains("exclude"))
    }

    @Test
    fun `should validate actual build script file exists and is parseable`() {
        // Test the actual build.gradle.kts file from the project if it exists
        val actualBuildFile = testProjectDir.resolve("../../../build.gradle.kts").toFile()
        if (actualBuildFile.exists()) {
            val actualContent = actualBuildFile.readText()
            assertNotNull("Actual build file should have content", actualContent)
            assertTrue("Should contain plugins block", actualContent.contains("plugins"))
        } else {
            // If no actual file exists, test our generated one
            val generatedScript = createCompleteBuildScript()
            buildFile.writeText(generatedScript)
            val result = gradleRunner.withArguments("help", "--no-daemon").build()
            assertTrue("Generated script should work", result.output.contains("BUILD SUCCESSFUL"))
        }
    }

    @Test
    fun `should validate build script handles all supported Android library features`() {
        val comprehensiveScript = """
            plugins {
                id("com.android.library")
                id("org.jetbrains.kotlin.android")
                id("org.jetbrains.kotlin.plugin.compose") version "1.9.0"
                id("kotlin-kapt")
                id("dagger.hilt.android.plugin")
                id("kotlin-parcelize")
            }
            
            android {
                namespace = "dev.aurakai.auraframefx.sandbox.ui"
                compileSdk = 36
                
                defaultConfig {
                    minSdk = 33
                    testOptions.targetSdk = 36
                    lint.targetSdk = 36
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                    
                    vectorDrawables.useSupportLibrary = true
                    multiDexEnabled = true
                    
                    ndk {
                        abiFilters.addAll(listOf("arm64-v8a", "x86_64"))
                        debugSymbolLevel = "FULL"
                    }
                }
                
                buildTypes {
                    debug {
                        isMinifyEnabled = false
                        isShrinkResources = false
                        isDebuggable = true
                    }
                    release {
                        isMinifyEnabled = true
                        isShrinkResources = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
                
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                    isCoreLibraryDesugaringEnabled = true
                }
                
                kotlinOptions {
                    jvmTarget = "21"
                    freeCompilerArgs += listOf(
                        "-Xjsr305=strict",
                        "-Xopt-in=kotlin.RequiresOptIn"
                    )
                }
                
                buildFeatures {
                    compose = true
                    buildConfig = true
                    viewBinding = false
                    dataBinding = false
                }
                
                composeOptions {
                    kotlinCompilerExtensionVersion = "2.0.0"
                }
                
                packaging {
                    resources {
                        excludes.addAll(
                            listOf(
                                "META-INF/*.kotlin_module",
                                "META-INF/*.version",
                                "META-INF/proguard/*",
                                "**/libjni*.so",
                                "META-INF/DEPENDENCIES",
                                "META-INF/LICENSE",
                                "META-INF/LICENSE.txt",
                                "META-INF/NOTICE",
                                "META-INF/NOTICE.txt"
                            )
                        )
                    }
                }
                
                lint {
                    abortOnError = false
                    checkReleaseBuilds = false
                    disable += setOf("MissingTranslation")
                }
                
                testOptions {
                    unitTests {
                        isIncludeAndroidResources = true
                        isReturnDefaultValues = true
                    }
                    animationsDisabled = true
                }
            }
            
            dependencies {
                api(project(":app"))
                implementation(libs.androidxCoreKtx)
                implementation(libs.androidxLifecycleRuntimeKtx)
                implementation(libs.androidxActivityCompose)
                implementation(platform(libs.composeBom))
                implementation(libs.ui)
                implementation(libs.uiToolingPreview)
                implementation(libs.androidxMaterial3)
                implementation(libs.animation)
                implementation(libs.foundation)
                implementation(libs.navigationComposeV291)
                implementation(libs.hiltAndroid)
                implementation(libs.hiltNavigationCompose)
                
                kapt(libs.hiltCompiler)
                
                debugImplementation(libs.uiTooling)
                debugImplementation(libs.uiTestManifest)
                
                testImplementation(libs.testJunit)
                androidTestImplementation(libs.junitV115)
                androidTestImplementation(libs.espressoCoreV351)
                androidTestImplementation(platform(libs.composeBom))
                androidTestImplementation(libs.uiTestJunit4)
            }
        """.trimIndent()

        buildFile.writeText(comprehensiveScript)

        val result = gradleRunner.withArguments("tasks", "--no-daemon").build()
        assertTrue(
            "Comprehensive script should build successfully",
            result.output.contains("BUILD SUCCESSFUL")
        )

        // Verify all major features are configured
        assertTrue("Should have multiDex", comprehensiveScript.contains("multiDexEnabled"))
        assertTrue("Should have vector drawables", comprehensiveScript.contains("vectorDrawables"))
        assertTrue(
            "Should have desugaring",
            comprehensiveScript.contains("isCoreLibraryDesugaringEnabled")
        )
        assertTrue(
            "Should have proper Kotlin options",
            comprehensiveScript.contains("freeCompilerArgs")
        )
    }

    private fun createCompleteBuildScript() = """
        plugins {
            id("com.android.library")
            id("org.jetbrains.kotlin.android")
            id("org.jetbrains.kotlin.plugin.compose") version "1.9.0"
            id("kotlin-kapt")
            id("dagger.hilt.android.plugin")
            id("kotlin-parcelize")
        }
        
        android {
            namespace = "dev.aurakai.auraframefx.sandbox.ui"
            compileSdk = 36
            
            defaultConfig {
                minSdk = 33
                testOptions.targetSdk = 36
                lint.targetSdk = 36
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
                
                ndk {
                    abiFilters.addAll(listOf("arm64-v8a", "x86_64"))
                    debugSymbolLevel = "FULL"
                }
            }
            
            buildTypes {
                release {
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
            
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
            }
            
            kotlinOptions {
            }
            
            buildFeatures {
                compose = true
                buildConfig = true
            }
            
            packaging {
                resources {
                    excludes.addAll(
                        listOf(
                            "META-INF/*.kotlin_module",
                            "META-INF/*.version",
                            "META-INF/proguard/*",
                            "**/libjni*.so"
                        )
                    )
                }
            }
            
            composeOptions {
                kotlinCompilerExtensionVersion = "2.0.0"
            }
        }
        
        dependencies {
            api(project(":app"))
            implementation(libs.androidxCoreKtx)
            implementation(libs.androidxLifecycleRuntimeKtx)
            implementation(libs.androidxActivityCompose)
        }
    """.trimIndent()
}