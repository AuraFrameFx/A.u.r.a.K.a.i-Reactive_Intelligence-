package dev.aurakai.auraframefx.gradle

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Unit tests for build.gradle.kts configuration validation.
 * Tests various aspects of the Gradle build configuration to ensure
 * consistency, compatibility, and correctness.
 *
 * Testing Framework: JUnit 4 (as identified from dependencies)
 */
class BuildConfigurationTest {

    private lateinit var buildFile: File
    private lateinit var buildContent: String

    @Before
    fun setup() {
        buildFile = File("app/build.gradle.kts")
        buildContent = if (buildFile.exists()) {
            buildFile.readText()
        } else {
            // Fallback for test environment - use relative path
            val fallbackFile = File("../app/build.gradle.kts")
            if (fallbackFile.exists()) {
                fallbackFile.readText()
            } else {
                ""
            }
        }
    }

    @Test
    fun `test Android configuration values are properly set`() {
        // Test namespace is correctly configured
        assertTrue(
            "Namespace should be set to dev.aurakai.auraframefx",
            buildContent.contains("namespace = \"dev.aurakai.auraframefx\"")
        )

        // Test SDK versions are compatible
        assertTrue(
            "CompileSdk should be set to 36",
            buildContent.contains("compileSdk = 36")
        )
        assertTrue(
            "TargetSdk should be set to 36",
            buildContent.contains("targetSdk = 36")
        )
        assertTrue(
            "MinSdk should be set to 33",
            buildContent.contains("minSdk = 33")
        )

        // Test application ID is correctly set
        assertTrue(
            "Application ID should be properly configured",
            buildContent.contains("applicationId = \"dev.aurakai.auraframefx\"")
        )
    }

    @Test
    fun `test essential plugins are applied`() {
        // Test core Android plugins
        assertTrue(
            "Android Application plugin should be applied",
            buildContent.contains("libs.plugins.androidApplication")
        )
        assertTrue(
            "Kotlin Android plugin should be applied",
            buildContent.contains("libs.plugins.kotlinAndroid")
        )

        // Test dependency injection plugin
        assertTrue(
            "Hilt Android plugin should be applied",
            buildContent.contains("libs.plugins.hiltAndroid")
        )
        assertTrue(
            "KSP plugin should be applied",
            buildContent.contains("libs.plugins.ksp")
        )

        // Test serialization plugin
        assertTrue(
            "Kotlin serialization plugin should be applied",
            buildContent.contains("libs.plugins.kotlin.serialization")
        )
    }

    @Test
    fun `test Firebase plugins are properly configured`() {
        assertTrue(
            "Google Services plugin should be applied",
            buildContent.contains("libs.plugins.google.services")
        )
        assertTrue(
            "Firebase Crashlytics plugin should be applied",
            buildContent.contains("libs.plugins.firebase.crashlytics")
        )
        assertTrue(
            "Firebase Performance plugin should be applied",
            buildContent.contains("libs.plugins.firebase.perf")
        )
    }

    @Test
    fun `test Compose configuration is correct`() {
        // Test Compose is enabled
        assertTrue(
            "Compose should be enabled in buildFeatures",
            buildContent.contains("compose = true")
        )

        // Test Compose compiler configuration
        assertTrue(
            "Compose compiler extension version should be configured",
            buildContent.contains("kotlinCompilerExtensionVersion")
        )

        // Test Compose plugin is applied
        assertTrue(
            "Compose plugin should be applied",
            buildContent.contains("org.jetbrains.kotlin.plugin.compose")
        )
    }

    @Test
    fun `test Java compatibility is set to correct version`() {
        assertTrue(
            "Source compatibility should be Java 21",
            buildContent.contains("sourceCompatibility = JavaVersion.VERSION_21")
        )
        assertTrue(
            "Target compatibility should be Java 21",
            buildContent.contains("targetCompatibility = JavaVersion.VERSION_21")
        )
        assertTrue(
            "JVM target should be set to JVM_21",
            buildContent.contains("jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21")
        )
    }

    @Test
    fun `test NDK configuration is properly set`() {
        assertTrue(
            "NDK version should be specified",
            buildContent.contains("ndkVersion = \"27.0.12077973\"")
        )

        // Test ABI filters are configured
        assertTrue(
            "ARM64 ABI should be included",
            buildContent.contains("arm64-v8a")
        )
        assertTrue(
            "ARMv7 ABI should be included",
            buildContent.contains("armeabi-v7a")
        )
        assertTrue(
            "x86_64 ABI should be included",
            buildContent.contains("x86_64")
        )
    }

    @Test
    fun `test CMake configuration is valid`() {
        assertTrue(
            "CMake path should be configured",
            buildContent.contains("path = file(\"src/main/cpp/CMakeLists.txt\")")
        )
        assertTrue(
            "CMake version should be specified",
            buildContent.contains("version = \"3.22.1\"")
        )

        // Test CMake arguments
        assertTrue(
            "STL should be configured",
            buildContent.contains("-DANDROID_STL=c++_shared")
        )
        assertTrue(
            "CPP features should be configured",
            buildContent.contains("-DANDROID_CPP_FEATURES=rtti exceptions")
        )
    }

    @Test
    fun `test build types are properly configured`() {
        // Test release build type
        assertTrue(
            "Release build should have minify disabled initially",
            buildContent.contains("isMinifyEnabled = false")
        )
        assertTrue(
            "Proguard files should be configured",
            buildContent.contains("proguard-rules.pro")
        )

        // Test debug/release specific configurations
        assertTrue(
            "Debug build should have DEBUG flag",
            buildContent.contains("cppFlags += \"-DEBUG\"")
        )
        assertTrue(
            "Release build should have NDEBUG flag",
            buildContent.contains("cppFlags += \"-DNDEBUG\"")
        )
    }

    @Test
    fun `test OpenAPI generator configuration is valid`() {
        assertTrue(
            "OpenAPI generator should be configured for Kotlin",
            buildContent.contains("generatorName.set(\"kotlin\")")
        )
        assertTrue(
            "API package should be properly set",
            buildContent.contains("apiPackage.set(\"dev.aurakai.auraframefx.api.client.apis\")")
        )
        assertTrue(
            "Model package should be properly set",
            buildContent.contains("modelPackage.set(\"dev.aurakai.auraframefx.api.client.models\")")
        )

        // Test configuration options
        assertTrue(
            "Date library should be java8",
            buildContent.contains("\"dateLibrary\" to \"java8\"")
        )
        assertTrue(
            "Coroutines should be enabled",
            buildContent.contains("\"useCoroutines\" to \"true\"")
        )
    }

    @Test
    fun `test KSP configuration is present`() {
        assertTrue(
            "Room schema location should be configured",
            buildContent.contains("room.schemaLocation")
        )
    }

    @Test
    fun `test essential dependencies are included`() {
        // Test core Android dependencies
        assertTrue(
            "AndroidX Core KTX should be included",
            buildContent.contains("implementation(libs.androidxCoreKtx)")
        )
        assertTrue(
            "AndroidX AppCompat should be included",
            buildContent.contains("implementation(libs.androidxAppcompat)")
        )

        // Test Compose dependencies
        assertTrue(
            "Compose BOM should be included",
            buildContent.contains("platform(libs.composeBom)")
        )
        assertTrue(
            "AndroidX UI should be included",
            buildContent.contains("implementation(libs.androidxUi)")
        )
        assertTrue(
            "Material 3 should be included",
            buildContent.contains("implementation(libs.androidxMaterial3)")
        )

        // Test Hilt dependencies
        assertTrue(
            "Hilt Android should be included",
            buildContent.contains("implementation(libs.hiltAndroid)")
        )
        assertTrue(
            "Hilt Compiler should be included",
            buildContent.contains("ksp(libs.hiltCompiler)")
        )

        // Test testing dependencies
        assertTrue(
            "JUnit should be included for testing",
            buildContent.contains("testImplementation(libs.testJunit)")
        )
        assertTrue(
            "Espresso should be included for Android testing",
            buildContent.contains("androidTestImplementation(libs.espressoCore)")
        )
    }

    @Test
    fun `test Firebase dependencies are configured`() {
        assertTrue(
            "Firebase BOM should be included",
            buildContent.contains("implementation(platform(libs.firebaseBom))")
        )
        assertTrue(
            "Firebase Analytics should be included",
            buildContent.contains("implementation(libs.firebaseAnalyticsKtx)")
        )
        assertTrue(
            "Firebase Crashlytics should be included",
            buildContent.contains("implementation(libs.firebaseCrashlyticsKtx)")
        )
        assertTrue(
            "Firebase Performance should be included",
            buildContent.contains("implementation(libs.firebasePerfKtx)")
        )
    }

    @Test
    fun `test Room database dependencies are configured`() {
        assertTrue(
            "Room Runtime should be included",
            buildContent.contains("implementation(libs.androidxRoomRuntime)")
        )
        assertTrue(
            "Room KTX should be included",
            buildContent.contains("implementation(libs.androidxRoomKtx)")
        )
        assertTrue(
            "Room Compiler should be included with KSP",
            buildContent.contains("ksp(libs.androidxRoomCompiler)")
        )
    }

    @Test
    fun `test network dependencies are configured`() {
        assertTrue(
            "Retrofit should be included",
            buildContent.contains("implementation(libs.retrofit)")
        )
        assertTrue(
            "OkHttp should be included",
            buildContent.contains("implementation(libs.okhttp)")
        )
        assertTrue(
            "Gson converter should be included",
            buildContent.contains("implementation(libs.converterGson)")
        )
        assertTrue(
            "Logging interceptor should be included",
            buildContent.contains("implementation(libs.okhttpLoggingInterceptor)")
        )
    }

    @Test
    fun `test Kotlin compiler options are set`() {
        assertTrue(
            "Context receivers should be enabled",
            buildContent.contains("-Xcontext-receivers")
        )
        assertTrue(
            "JVM default should be set to all",
            buildContent.contains("-Xjvm-default=all")
        )
        assertTrue(
            "RequiresOptIn should be opted in",
            buildContent.contains("-opt-in=kotlin.RequiresOptIn")
        )
    }

    @Test
    fun `test resource configuration is valid`() {
        assertTrue(
            "Vector drawables support library should be enabled",
            buildContent.contains("useSupportLibrary = true")
        )
        assertTrue(
            "Proto files should not be compressed",
            buildContent.contains("\"proto\"")
        )
        assertTrue(
            "JSON files should not be compressed",
            buildContent.contains("\"json\"")
        )
    }

    @Test
    fun `test exclusions are properly configured`() {
        assertTrue(
            "Kotlin stdlib common should be excluded",
            buildContent.contains("kotlin-stdlib-common")
        )
        assertTrue(
            "Coroutines core common should be excluded",
            buildContent.contains("kotlinx-coroutines-core-common")
        )
        assertTrue(
            "Serialization core common should be excluded",
            buildContent.contains("kotlinx-serialization-core-common")
        )
    }

    @Test
    fun `test task dependencies are configured`() {
        assertTrue(
            "PreBuild should depend on OpenAPI generation",
            buildContent.contains("dependsOn(\"openApiGenerate\")")
        )
    }

    @Test
    fun `test version consistency for critical dependencies`() {
        // Since this uses version catalogs, we test that the pattern is consistent
        val libsPattern = Regex("libs\\.[a-zA-Z0-9\\.]+")
        val matches = libsPattern.findAll(buildContent)
        assertTrue(
            "Should have multiple library references using version catalog",
            matches.count() > 10
        )
    }

    @Test
    fun `test build features are enabled`() {
        assertTrue(
            "BuildConfig should be enabled",
            buildContent.contains("buildConfig = true")
        )
        assertTrue(
            "ViewBinding should be enabled",
            buildContent.contains("viewBinding = true")
        )
    }

    @Test
    fun `test test runner configuration`() {
        assertTrue(
            "Custom Hilt test runner should be configured",
            buildContent.contains("testInstrumentationRunner = \"dev.aurakai.auraframefx.HiltTestRunner\"")
        )
    }

    @Test
    fun `test multidex configuration`() {
        assertTrue(
            "MultiDex should be enabled",
            buildContent.contains("multiDexEnabled = true")
        )
    }

    @Test
    fun `test debug dependencies are properly configured`() {
        assertTrue(
            "Compose UI tooling should be available in debug",
            buildContent.contains("debugImplementation(libs.composeUiTooling)")
        )
        assertTrue(
            "Compose test manifest should be available in debug",
            buildContent.contains("debugImplementation(libs.composeUiTestManifest)")
        )
    }

    @Test
    fun `test duplicate buildFeatures configuration`() {
        // Check for duplicate buildFeatures blocks
        val buildFeaturesCount = Regex("buildFeatures\\s*\\{").findAll(buildContent).count()
        assertTrue(
            "BuildFeatures should be configured (found $buildFeaturesCount occurrences)",
            buildFeaturesCount >= 1
        )

        // Note: Having multiple buildFeatures blocks is valid in Gradle
        // but we should be aware of them
    }

    @Test
    fun `test plugin application order`() {
        // KSP should be applied before Hilt as noted in the comment
        val kspIndex = buildContent.indexOf("libs.plugins.ksp")
        val hiltIndex = buildContent.indexOf("libs.plugins.hiltAndroid")

        if (kspIndex != -1 && hiltIndex != -1) {
            assertTrue("KSP plugin should be applied before Hilt plugin", kspIndex < hiltIndex)
        }
    }

    @Test
    fun `test required testing libraries are present`() {
        // Test MockK for unit tests
        assertTrue(
            "MockK should be included for unit testing",
            buildContent.contains("testImplementation(libs.mockkAgent)")
        )

        // Test MockK for Android tests
        assertTrue(
            "MockK Android should be included for instrumentation testing",
            buildContent.contains("androidTestImplementation(libs.mockkAndroid)")
        )

        // Test Coroutines testing
        assertTrue(
            "Coroutines test should be included",
            buildContent.contains("testImplementation(libs.kotlinxCoroutinesTest)")
        )

        // Test Compose UI testing
        assertTrue(
            "Compose UI test should be included",
            buildContent.contains("androidTestImplementation(libs.composeUiTestJunit4)")
        )
    }

    @Test
    fun `test security and encryption dependencies`() {
        assertTrue(
            "Security crypto should be included",
            buildContent.contains("implementation(libs.androidxSecurityCrypto)")
        )
    }

    @Test
    fun `test data storage dependencies`() {
        assertTrue(
            "DataStore preferences should be included",
            buildContent.contains("implementation(libs.androidxDatastorePreferences)")
        )
        assertTrue(
            "DataStore core should be included",
            buildContent.contains("implementation(libs.androidxDatastoreCore)")
        )
    }

    @Test
    fun `test work manager dependency`() {
        assertTrue(
            "WorkManager runtime should be included",
            buildContent.contains("implementation(libs.androidxWorkRuntimeKtx)")
        )
        assertTrue(
            "Hilt Work should be included",
            buildContent.contains("implementation(libs.hiltWork)")
        )
    }

    @Test
    fun `test AI and ML dependencies`() {
        assertTrue(
            "Generative AI should be included",
            buildContent.contains("implementation(libs.generativeai)")
        )
    }

    @Test
    fun `test image loading dependency`() {
        assertTrue(
            "Coil Compose should be included",
            buildContent.contains("implementation(libs.coilCompose)")
        )
    }

    @Test
    fun `test logging dependency`() {
        assertTrue(
            "Timber should be included",
            buildContent.contains("implementation(libs.timber)")
        )
    }

    @Test
    fun `test desugaring configuration`() {
        assertTrue(
            "Core library desugaring should be enabled",
            buildContent.contains("coreLibraryDesugaring(libs.desugarJdkLibs)")
        )
    }
}