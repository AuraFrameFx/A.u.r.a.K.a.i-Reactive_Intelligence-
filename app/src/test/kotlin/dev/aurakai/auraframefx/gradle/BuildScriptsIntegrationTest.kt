package dev.aurakai.auraframefx.gradle

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.UnexpectedBuildFailure
import java.io.File
import java.nio.file.Path
import java.nio.file.Files
import java.util.stream.Stream
import kotlin.io.path.writeText
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

/**
 * Comprehensive integration tests for build scripts functionality.
 *
 * This test class validates the behavior of Gradle build scripts in various scenarios
 * including successful builds, failure conditions, edge cases, and Android-specific configurations.
 *
 * Testing Framework: JUnit 5 with Gradle TestKit
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Build Scripts Integration Tests")
class BuildScriptsIntegrationTest {

    @TempDir
    lateinit var testProjectDir: Path

    private lateinit var buildFile: File
    private lateinit var settingsFile: File
    private lateinit var gradlePropertiesFile: File
    private lateinit var androidManifestFile: File

    companion object {
        private const val GRADLE_VERSION = "8.1"

        private const val ANDROID_BUILD_SCRIPT = """
            plugins {
                id 'com.android.application'
                id 'org.jetbrains.kotlin.android'
                id 'kotlin-kapt'
                id 'dagger.hilt.android.plugin'
            }

            android {
                namespace = "dev.aurakai.auraframefx"
                compileSdk 34

                defaultConfig {
                    applicationId "dev.aurakai.auraframefx"
                    minSdk 26
                    targetSdk 34
                    versionCode 1
                    versionName "1.0"
                    testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
                }

                buildTypes {
                    release {
                        minifyEnabled false
                        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
                    }
                }

                compileOptions {
                    sourceCompatibility JavaVersion.VERSION_1_8
                    targetCompatibility JavaVersion.VERSION_1_8
                }

                kotlinOptions {
                    jvmTarget = '1.8'
                }

                buildFeatures {
                    compose = true
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = '1.5.0'
                }
            }

            dependencies {
                implementation 'androidx.core:core-ktx:1.10.1'
                implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.1'
                implementation 'androidx.activity:activity-compose:1.7.2'
                implementation platform('androidx.compose:compose-bom:2023.06.01')
                implementation 'androidx.compose.ui:ui'
                implementation 'androidx.compose.ui:ui-tooling-preview'
                implementation 'androidx.compose.material3:material3'

                testImplementation 'junit:junit:4.13.2'
                testImplementation 'org.junit.jupiter:junit-jupiter:5.8.2'
                androidTestImplementation 'androidx.test.ext:junit:1.1.5'
                androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
                androidTestImplementation platform('androidx.compose:compose-bom:2023.06.01')
                androidTestImplementation 'androidx.compose.ui:ui-test-junit4'
                debugImplementation 'androidx.compose.ui:ui-tooling'
                debugImplementation 'androidx.compose.ui:ui-test-manifest'
            }
        """

        private const val BASIC_JAVA_BUILD_SCRIPT = """
            plugins {
                id 'java'
                id 'application'
            }

            repositories {
                mavenCentral()
                google()
            }

            dependencies {
                implementation 'org.slf4j:slf4j-api:1.7.36'
                testImplementation 'org.junit.jupiter:junit-jupiter:5.8.2'
            }

            application {
                mainClass = 'dev.aurakai.Main'
            }

            test {
                useJUnitPlatform()
            }
        """

        private const val KOTLIN_BUILD_SCRIPT = """
            plugins {
                id 'org.jetbrains.kotlin.jvm' version '1.9.0'
                id 'application'
            }

            repositories {
                mavenCentral()
            }

            dependencies {
                implementation 'org.jetbrains.kotlin:kotlin-stdlib'
                implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0'
                testImplementation 'org.junit.jupiter:junit-jupiter:5.8.2'
                testImplementation 'org.jetbrains.kotlin:kotlin-test'
            }

            application {
                mainClass = 'dev.aurakai.MainKt'
            }

            test {
                useJUnitPlatform()
            }
        """

        private const val MINIMAL_SETTINGS = """
            rootProject.name = 'test-project'
        """

        private const val ANDROID_MANIFEST = """
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                package="dev.aurakai.auraframefx">

                <application
                    android:allowBackup="true"
                    android:icon="@mipmap/ic_launcher"
                    android:label="@string/app_name"
                    android:theme="@style/AppTheme">
                    <activity
                        android:name=".MainActivity"
                        android:exported="true">
                        <intent-filter>
                            <action android:name="android.intent.action.MAIN" />
                            <category android:name="android.intent.category.LAUNCHER" />
                        </intent-filter>
                    </activity>
                </application>
            </manifest>
        """

        @JvmStatic
        fun gradleVersionProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("7.6"),
                Arguments.of("8.0"),
                Arguments.of("8.1"),
                Arguments.of("8.2")
            )
        }

        @JvmStatic
        fun buildTaskProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("build", true),
                Arguments.of("clean", true),
                Arguments.of("assemble", true),
                Arguments.of("test", true),
                Arguments.of("check", true),
                Arguments.of("compileJava", true),
                Arguments.of("compileKotlin", true),
                Arguments.of("invalidTask", false)
            )
        }

        @JvmStatic
        fun kotlinVersionProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("1.8.22"),
                Arguments.of("1.9.0"),
                Arguments.of("1.9.10")
            )
        }
    }

    @BeforeEach
    fun setUp() {
        buildFile = testProjectDir.resolve("build.gradle").toFile()
        settingsFile = testProjectDir.resolve("settings.gradle").toFile()
        gradlePropertiesFile = testProjectDir.resolve("gradle.properties").toFile()

        // Create basic project structure
        testProjectDir.resolve("src/main/java").createDirectories()
        testProjectDir.resolve("src/test/java").createDirectories()
        testProjectDir.resolve("src/main/kotlin").createDirectories()
        testProjectDir.resolve("src/test/kotlin").createDirectories()

        // Create Android-specific structure
        testProjectDir.resolve("src/main/res/values").createDirectories()
        androidManifestFile = testProjectDir.resolve("src/main/AndroidManifest.xml").toFile()

        // Create minimal settings file
        settingsFile.writeText(MINIMAL_SETTINGS)

        // Create basic gradle.properties
        gradlePropertiesFile.writeText(
            """
            org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
            org.gradle.parallel=true
            org.gradle.caching=true
            android.useAndroidX=true
            android.enableJetifier=true
        """.trimIndent()
        )
    }

    @AfterEach
    fun tearDown() {
        // Clean up any temporary files or resources
        // TestKit and @TempDir handle most cleanup automatically
    }

    @Nested
    @DisplayName("Basic Build Script Tests")
    inner class BasicBuildScriptTests {

        @Test
        @DisplayName("Should successfully build Java project with default configuration")
        fun shouldBuildJavaProjectWithDefaultConfiguration() {
            // Given
            buildFile.writeText(BASIC_JAVA_BUILD_SCRIPT)
            createMainJavaClass()

            // When
            val result = runGradleTask("build")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
            assertTrue(result.output.contains("BUILD SUCCESSFUL"))
        }

        @Test
        @DisplayName("Should successfully build Kotlin project")
        fun shouldBuildKotlinProjectSuccessfully() {
            // Given
            buildFile.writeText(KOTLIN_BUILD_SCRIPT)
            createMainKotlinClass()

            // When
            val result = runGradleTask("build")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
            assertTrue(result.output.contains("BUILD SUCCESSFUL"))
        }

        @Test
        @DisplayName("Should handle empty build script gracefully")
        fun shouldHandleEmptyBuildScript() {
            // Given
            buildFile.writeText("")

            // When
            val result = runGradleTask("tasks")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":tasks")?.outcome)
        }

        @Test
        @DisplayName("Should fail with invalid syntax in build script")
        fun shouldFailWithInvalidSyntaxInBuildScript() {
            // Given
            buildFile.writeText("invalid gradle syntax {{{ unclosed braces")

            // When & Then
            assertThrows<UnexpectedBuildFailure> {
                runGradleTask("build")
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["build", "clean", "assemble", "tasks", "help"])
        @DisplayName("Should execute common Gradle tasks successfully")
        fun shouldExecuteCommonTasksSuccessfully(taskName: String) {
            // Given
            buildFile.writeText(BASIC_JAVA_BUILD_SCRIPT)
            if (taskName in listOf("build", "assemble")) {
                createMainJavaClass()
            }

            // When
            val result = runGradleTask(taskName)

            // Then
            if (result.task(":$taskName") != null) {
                assertNotEquals(TaskOutcome.FAILED, result.task(":$taskName")?.outcome)
            }
            assertTrue(result.output.contains("BUILD SUCCESSFUL"))
        }

        @Test
        @DisplayName("Should handle build script with comments and complex formatting")
        fun shouldHandleBuildScriptWithCommentsAndFormatting() {
            // Given
            buildFile.writeText(
                """
                // This is a test build script
                plugins {
                    id 'java' // Java plugin
                    /*
                     * Application plugin for running the app
                     */
                    id 'application'
                }

                /* Repositories configuration */
                repositories {
                    mavenCentral() // Central Maven repository
                }

                // Dependencies block
                dependencies {
                    implementation 'org.slf4j:slf4j-api:1.7.36' // Logging API
                }

                // Application configuration
                application {
                    mainClass = 'dev.aurakai.Main'
                }
            """
            )
            createMainJavaClass()

            // When
            val result = runGradleTask("build")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
        }
    }

    @Nested
    @DisplayName("Plugin Configuration Tests")
    inner class PluginConfigurationTests {

        @Test
        @DisplayName("Should apply Java plugin successfully")
        fun shouldApplyJavaPluginSuccessfully() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                repositories {
                    mavenCentral()
                }
            """
            )

            // When
            val result = runGradleTask("compileJava")

            // Then
            assertEquals(TaskOutcome.NO_SOURCE, result.task(":compileJava")?.outcome)
        }

        @Test
        @DisplayName("Should apply Kotlin plugin successfully")
        fun shouldApplyKotlinPluginSuccessfully() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'org.jetbrains.kotlin.jvm' version '1.9.0'
                }

                repositories {
                    mavenCentral()
                }
            """
            )

            // When
            val result = runGradleTask("compileKotlin")

            // Then
            assertEquals(TaskOutcome.NO_SOURCE, result.task(":compileKotlin")?.outcome)
        }

        @Test
        @DisplayName("Should apply application plugin successfully")
        fun shouldApplyApplicationPluginSuccessfully() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                    id 'application'
                }

                repositories {
                    mavenCentral()
                }

                application {
                    mainClass = 'dev.aurakai.Main'
                }
            """
            )
            createMainJavaClass()

            // When
            val result = runGradleTask("installDist")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":installDist")?.outcome)
        }

        @Test
        @DisplayName("Should fail when applying non-existent plugin")
        fun shouldFailWhenApplyingNonExistentPlugin() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'non-existent-plugin-that-does-not-exist'
                }
            """
            )

            // When & Then
            assertThrows<UnexpectedBuildFailure> {
                runGradleTask("build")
            }
        }

        @Test
        @DisplayName("Should handle multiple plugin applications")
        fun shouldHandleMultiplePluginApplications() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                    id 'application'
                    id 'jacoco'
                    id 'checkstyle'
                }

                repositories {
                    mavenCentral()
                }

                application {
                    mainClass = 'dev.aurakai.Main'
                }
            """
            )
            createMainJavaClass()

            // When
            val result = runGradleTask("build")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
        }
    }

    @Nested
    @DisplayName("Dependency Management Tests")
    inner class DependencyManagementTests {

        @Test
        @DisplayName("Should resolve dependencies from Maven Central")
        fun shouldResolveDependenciesFromMavenCentral() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation 'org.slf4j:slf4j-api:1.7.36'
                    implementation 'com.google.guava:guava:31.1-jre'
                    testImplementation 'org.junit.jupiter:junit-jupiter:5.8.2'
                }
            """
            )

            // When
            val result = runGradleTask("dependencies", "--configuration", "runtimeClasspath")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies")?.outcome)
            assertTrue(result.output.contains("org.slf4j:slf4j-api:1.7.36"))
            assertTrue(result.output.contains("com.google.guava:guava:31.1-jre"))
        }

        @Test
        @DisplayName("Should resolve dependencies from multiple repositories")
        fun shouldResolveDependenciesFromMultipleRepositories() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                repositories {
                    mavenCentral()
                    google()
                    gradlePluginPortal()
                }

                dependencies {
                    implementation 'org.slf4j:slf4j-api:1.7.36'
                    implementation 'androidx.core:core-ktx:1.10.1'
                }
            """
            )

            // When
            val result = runGradleTask("dependencies", "--configuration", "runtimeClasspath")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies")?.outcome)
        }

        @Test
        @DisplayName("Should fail with invalid dependency coordinates")
        fun shouldFailWithInvalidDependencyCoordinates() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation 'invalid:dependency:coordinates:extra:parts:too:many'
                }
            """
            )

            // When & Then
            assertThrows<UnexpectedBuildFailure> {
                runGradleTask("build")
            }
        }

        @Test
        @DisplayName("Should handle missing repository configuration")
        fun shouldHandleMissingRepositoryConfiguration() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                dependencies {
                    implementation 'org.slf4j:slf4j-api:1.7.36'
                }
            """
            )

            // When & Then
            assertThrows<UnexpectedBuildFailure> {
                runGradleTask("build")
            }
        }

        @Test
        @DisplayName("Should handle version conflicts gracefully")
        fun shouldHandleVersionConflictsGracefully() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation 'org.slf4j:slf4j-api:1.7.36'
                    implementation 'org.slf4j:slf4j-api:1.7.30' // Different version
                    implementation 'ch.qos.logback:logback-classic:1.2.12'
                }
            """
            )

            // When
            val result = runGradleTask("dependencies", "--configuration", "runtimeClasspath")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":dependencies")?.outcome)
            // Gradle should resolve to the higher version
            assertTrue(result.output.contains("1.7.36"))
        }
    }

    @Nested
    @DisplayName("Build Configuration Tests")
    inner class BuildConfigurationTests {

        @Test
        @DisplayName("Should respect Java source compatibility settings")
        fun shouldRespectJavaSourceCompatibilitySettings() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                repositories {
                    mavenCentral()
                }

                java {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
            """
            )

            // When
            val result = runGradleTask("compileJava")

            // Then
            assertEquals(TaskOutcome.NO_SOURCE, result.task(":compileJava")?.outcome)
        }

        @Test
        @DisplayName("Should handle custom source sets")
        fun shouldHandleCustomSourceSets() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                repositories {
                    mavenCentral()
                }

                sourceSets {
                    integration {
                        java.srcDir 'src/integration/java'
                        resources.srcDir 'src/integration/resources'
                    }
                    performance {
                        java.srcDir 'src/performance/java'
                        resources.srcDir 'src/performance/resources'
                    }
                }
            """
            )

            // Create integration source directory
            testProjectDir.resolve("src/integration/java").createDirectories()
            testProjectDir.resolve("src/performance/java").createDirectories()

            // When
            val result = runGradleTask("compileIntegrationJava")

            // Then
            assertEquals(TaskOutcome.NO_SOURCE, result.task(":compileIntegrationJava")?.outcome)
        }

        @ParameterizedTest
        @CsvSource(
            "VERSION_1_8, NO_SOURCE",
            "VERSION_11, NO_SOURCE",
            "VERSION_17, NO_SOURCE"
        )
        @DisplayName("Should support different Java versions")
        fun shouldSupportDifferentJavaVersions(javaVersion: String, expectedOutcome: String) {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                repositories {
                    mavenCentral()
                }

                java {
                    sourceCompatibility = JavaVersion.$javaVersion
                    targetCompatibility = JavaVersion.$javaVersion
                }
            """
            )

            // When
            val result = runGradleTask("compileJava")

            // Then
            assertEquals(TaskOutcome.valueOf(expectedOutcome), result.task(":compileJava")?.outcome)
        }

        @Test
        @DisplayName("Should handle Kotlin compiler options")
        fun shouldHandleKotlinCompilerOptions() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'org.jetbrains.kotlin.jvm' version '1.9.0'
                }

                repositories {
                    mavenCentral()
                }

                kotlin {
                    jvmToolchain(11)
                }

                tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile) {
                    kotlinOptions {
                        jvmTarget = "11"
                        freeCompilerArgs += ["-Xjsr305=strict"]
                    }
                }
            """
            )

            // When
            val result = runGradleTask("compileKotlin")

            // Then
            assertEquals(TaskOutcome.NO_SOURCE, result.task(":compileKotlin")?.outcome)
        }
    }

    @Nested
    @DisplayName("Task Execution Tests")
    inner class TaskExecutionTests {

        @ParameterizedTest
        @MethodSource("dev.aurakai.auraframefx.gradle.BuildScriptsIntegrationTest#buildTaskProvider")
        @DisplayName("Should execute various build tasks")
        fun shouldExecuteVariousBuildTasks(taskName: String, shouldSucceed: Boolean) {
            // Given
            buildFile.writeText(BASIC_JAVA_BUILD_SCRIPT)
            createMainJavaClass()

            // When & Then
            if (shouldSucceed) {
                val result = runGradleTask(taskName)
                if (result.task(":$taskName") != null) {
                    assertNotEquals(TaskOutcome.FAILED, result.task(":$taskName")?.outcome)
                }
            } else {
                assertThrows<UnexpectedBuildFailure> {
                    runGradleTask(taskName)
                }
            }
        }

        @Test
        @DisplayName("Should handle parallel task execution")
        fun shouldHandleParallelTaskExecution() {
            // Given
            buildFile.writeText(BASIC_JAVA_BUILD_SCRIPT)
            createMainJavaClass()
            gradlePropertiesFile.writeText(
                """
                org.gradle.parallel=true
                org.gradle.workers.max=4
            """.trimIndent()
            )

            // When
            val result = runGradleTask("build", "--parallel")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
        }

        @Test
        @DisplayName("Should support custom task definitions")
        fun shouldSupportCustomTaskDefinitions() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                repositories {
                    mavenCentral()
                }

                task customTask {
                    doLast {
                        println 'Custom task executed successfully'
                    }
                }

                task dependentTask(dependsOn: customTask) {
                    doLast {
                        println 'Dependent task executed'
                    }
                }
            """
            )

            // When
            val result = runGradleTask("dependentTask")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":customTask")?.outcome)
            assertEquals(TaskOutcome.SUCCESS, result.task(":dependentTask")?.outcome)
            assertTrue(result.output.contains("Custom task executed successfully"))
            assertTrue(result.output.contains("Dependent task executed"))
        }

        @Test
        @DisplayName("Should handle task configuration")
        fun shouldHandleTaskConfiguration() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                repositories {
                    mavenCentral()
                }

                compileJava {
                    options.compilerArgs += ['-Xlint:unchecked', '-Xlint:deprecation']
                    options.encoding = 'UTF-8'
                }

                test {
                    useJUnitPlatform()
                    testLogging {
                        events 'passed', 'skipped', 'failed'
                    }
                }
            """
            )

            // When
            val result = runGradleTask("compileJava")

            // Then
            assertEquals(TaskOutcome.NO_SOURCE, result.task(":compileJava")?.outcome)
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    inner class ErrorHandlingTests {

        @Test
        @DisplayName("Should provide meaningful error messages for compilation failures")
        fun shouldProvideMeaningfulErrorMessagesForCompilationFailures() {
            // Given
            buildFile.writeText(BASIC_JAVA_BUILD_SCRIPT)
            createInvalidJavaClass()

            // When & Then
            val exception = assertThrows<UnexpectedBuildFailure> {
                runGradleTask("compileJava")
            }
            assertTrue(exception.message?.contains("Compilation failed") ?: false)
        }

        @Test
        @DisplayName("Should handle build script syntax errors gracefully")
        fun shouldHandleBuildScriptSyntaxErrorsGracefully() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'

                // Missing closing brace for plugins block
                repositories {
                    mavenCentral()
                }
            """
            )

            // When & Then
            val exception = assertThrows<UnexpectedBuildFailure> {
                runGradleTask("build")
            }
            assertTrue(exception.message?.contains("Could not compile build file") ?: false)
        }

        @Test
        @DisplayName("Should handle missing main class gracefully")
        fun shouldHandleMissingMainClassGracefully() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                    id 'application'
                }

                repositories {
                    mavenCentral()
                }

                application {
                    mainClass = 'com.nonexistent.Main'
                }
            """
            )

            // When & Then
            assertThrows<UnexpectedBuildFailure> {
                runGradleTask("run")
            }
        }

        @Test
        @DisplayName("Should handle circular task dependencies")
        fun shouldHandleCircularTaskDependencies() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                task taskA(dependsOn: 'taskB') {
                    doLast { println 'Task A' }
                }

                task taskB(dependsOn: 'taskA') {
                    doLast { println 'Task B' }
                }
            """
            )

            // When & Then
            assertThrows<UnexpectedBuildFailure> {
                runGradleTask("taskA")
            }
        }

        @Test
        @DisplayName("Should handle invalid plugin versions")
        fun shouldHandleInvalidPluginVersions() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'org.jetbrains.kotlin.jvm' version '99.99.99'
                }
            """
            )

            // When & Then
            assertThrows<UnexpectedBuildFailure> {
                runGradleTask("build")
            }
        }
    }

    @Nested
    @DisplayName("Performance and Resource Tests")
    inner class PerformanceAndResourceTests {

        @Test
        @DisplayName("Should complete build within reasonable time")
        fun shouldCompleteBuildWithinReasonableTime() {
            // Given
            buildFile.writeText(BASIC_JAVA_BUILD_SCRIPT)
            createMainJavaClass()

            // When
            val startTime = System.currentTimeMillis()
            val result = runGradleTask("build")
            val endTime = System.currentTimeMillis()

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
            assertTrue((endTime - startTime) < 60000, "Build should complete within 60 seconds")
        }

        @Test
        @DisplayName("Should handle incremental builds correctly")
        fun shouldHandleIncrementalBuildsCorrectly() {
            // Given
            buildFile.writeText(BASIC_JAVA_BUILD_SCRIPT)
            createMainJavaClass()

            // When - First build
            val firstResult = runGradleTask("build")

            // When - Second build without changes
            val secondResult = runGradleTask("build")

            // Then
            assertEquals(TaskOutcome.SUCCESS, firstResult.task(":build")?.outcome)
            assertEquals(TaskOutcome.UP_TO_DATE, secondResult.task(":compileJava")?.outcome)
        }

        @Test
        @DisplayName("Should handle large number of source files")
        fun shouldHandleLargeNumberOfSourceFiles() {
            // Given
            buildFile.writeText(BASIC_JAVA_BUILD_SCRIPT)

            // Create multiple source files
            repeat(25) { i ->
                createJavaClass("TestClass$i", "dev.aurakai.test")
            }

            // When
            val result = runGradleTask("compileJava")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava")?.outcome)
        }

        @Test
        @DisplayName("Should handle build caching")
        fun shouldHandleBuildCaching() {
            // Given
            buildFile.writeText(BASIC_JAVA_BUILD_SCRIPT)
            createMainJavaClass()
            gradlePropertiesFile.writeText(
                """
                org.gradle.caching=true
                org.gradle.parallel=true
            """.trimIndent()
            )

            // When
            val result = runGradleTask("build", "--build-cache")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
        }
    }

    @Nested
    @DisplayName("Kotlin-Specific Tests")
    inner class KotlinSpecificTests {

        @ParameterizedTest
        @MethodSource("dev.aurakai.auraframefx.gradle.BuildScriptsIntegrationTest#kotlinVersionProvider")
        @DisplayName("Should work with different Kotlin versions")
        fun shouldWorkWithDifferentKotlinVersions(kotlinVersion: String) {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'org.jetbrains.kotlin.jvm' version '$kotlinVersion'
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation 'org.jetbrains.kotlin:kotlin-stdlib'
                }
            """
            )
            createMainKotlinClass()

            // When
            val result = runGradleTask("build")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
        }

        @Test
        @DisplayName("Should handle Kotlin coroutines dependencies")
        fun shouldHandleKotlinCoroutinesDependencies() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'org.jetbrains.kotlin.jvm' version '1.9.0'
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation 'org.jetbrains.kotlin:kotlin-stdlib'
                    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0'
                    testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0'
                }
            """
            )
            createKotlinCoroutinesClass()

            // When
            val result = runGradleTask("build")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
        }

        @Test
        @DisplayName("Should handle Kotlin serialization plugin")
        fun shouldHandleKotlinSerializationPlugin() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'org.jetbrains.kotlin.jvm' version '1.9.0'
                    id 'org.jetbrains.kotlin.plugin.serialization' version '1.9.0'
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation 'org.jetbrains.kotlin:kotlin-stdlib'
                    implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0'
                }
            """
            )
            createKotlinSerializationClass()

            // When
            val result = runGradleTask("build")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
        }
    }

    @Nested
    @DisplayName("Multi-Module Tests")
    inner class MultiModuleTests {

        @Test
        @DisplayName("Should handle simple multi-module project")
        fun shouldHandleSimpleMultiModuleProject() {
            // Given
            settingsFile.writeText(
                """
                rootProject.name = 'multi-module-project'
                include 'core', 'app'
            """
            )

            // Create core module
            val coreDir = testProjectDir.resolve("core")
            coreDir.createDirectories()
            coreDir.resolve("build.gradle").writeText(
                """
                plugins {
                    id 'java-library'
                }

                repositories {
                    mavenCentral()
                }
            """
            )
            coreDir.resolve("src/main/java").createDirectories()

            // Create app module
            val appDir = testProjectDir.resolve("app")
            appDir.createDirectories()
            appDir.resolve("build.gradle").writeText(
                """
                plugins {
                    id 'java'
                    id 'application'
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation project(':core')
                }

                application {
                    mainClass = 'dev.aurakai.App'
                }
            """
            )
            appDir.resolve("src/main/java").createDirectories()

            // Create main class in app module
            createJavaClass(
                "App",
                "dev.aurakai",
                "public static void main(String[] args) { System.out.println(\"Hello\"); }",
                appDir.resolve("src/main/java")
            )

            // When
            val result = runGradleTask("build")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":app:build")?.outcome)
            assertEquals(TaskOutcome.NO_SOURCE, result.task(":core:build")?.outcome)
        }
    }

    @Nested
    @DisplayName("Android-Specific Tests")
    inner class AndroidSpecificTests {

        @Test
        @DisplayName("Should validate Android manifest structure")
        fun shouldValidateAndroidManifestStructure() {
            // Given
            androidManifestFile.writeText(ANDROID_MANIFEST)

            // When
            val manifestExists = androidManifestFile.exists()
            val manifestContent = if (manifestExists) androidManifestFile.readText() else ""

            // Then
            assertTrue(manifestExists)
            assertTrue(manifestContent.contains("package=\"dev.aurakai.auraframefx\""))
            assertTrue(manifestContent.contains("MainActivity"))
            assertTrue(manifestContent.contains("android.intent.action.MAIN"))
        }

        @Test
        @DisplayName("Should handle Android resource structure")
        fun shouldHandleAndroidResourceStructure() {
            // Given
            val valuesDir = testProjectDir.resolve("src/main/res/values")
            valuesDir.createDirectories()

            val stringsXml = valuesDir.resolve("strings.xml")
            stringsXml.writeText(
                """
                <?xml version="1.0" encoding="utf-8"?>
                <resources>
                    <string name="app_name">AuraFrameFX</string>
                    <string name="hello_world">Hello World!</string>
                </resources>
            """
            )

            val colorsXml = valuesDir.resolve("colors.xml")
            colorsXml.writeText(
                """
                <?xml version="1.0" encoding="utf-8"?>
                <resources>
                    <color name="purple_200">#FFBB86FC</color>
                    <color name="purple_500">#FF6200EE</color>
                    <color name="purple_700">#FF3700B3</color>
                </resources>
            """
            )

            // When
            val stringsExists = stringsXml.exists()
            val colorsExists = colorsXml.exists()

            // Then
            assertTrue(stringsExists)
            assertTrue(colorsExists)
            assertTrue(stringsXml.readText().contains("app_name"))
            assertTrue(colorsXml.readText().contains("purple_500"))
        }
    }

    @ParameterizedTest
    @MethodSource("gradleVersionProvider")
    @DisplayName("Should work with different Gradle versions")
    fun shouldWorkWithDifferentGradleVersions(gradleVersion: String) {
        // Given
        buildFile.writeText(BASIC_JAVA_BUILD_SCRIPT)
        createMainJavaClass()

        // When
        val result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.toFile())
            .withArguments("build", "--stacktrace")
            .build()

        // Then
        assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    inner class EdgeCasesAndBoundaryTests {

        @Test
        @DisplayName("Should handle extremely long build script")
        fun shouldHandleExtremelyLongBuildScript() {
            // Given
            val longScript = StringBuilder(BASIC_JAVA_BUILD_SCRIPT)
            repeat(100) { i ->
                longScript.append(
                    """

                    // Comment number $i
                    task customTask$i {
                        doLast {
                            println 'Task $i executed'
                        }
                    }
                """
                )
            }
            buildFile.writeText(longScript.toString())
            createMainJavaClass()

            // When
            val result = runGradleTask("build")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
        }

        @Test
        @DisplayName("Should handle build script with unicode characters")
        fun shouldHandleBuildScriptWithUnicodeCharacters() {
            // Given
            buildFile.writeText(
                """
                plugins {
                    id 'java'
                }

                repositories {
                    mavenCentral()
                }

                // 中文注释测试
                task unicodeTask {
                    description = 'Test with émojis and ünïcödë 🚀'
                    doLast {
                        println 'Únicöde task executed successfully! 🎉'
                    }
                }
            """
            )

            // When
            val result = runGradleTask("unicodeTask")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":unicodeTask")?.outcome)
            assertTrue(result.output.contains("🎉"))
        }

        @Test
        @DisplayName("Should handle deep directory structure")
        fun shouldHandleDeepDirectoryStructure() {
            // Given
            buildFile.writeText(BASIC_JAVA_BUILD_SCRIPT)
            "dev/aurakai/deep/very/deeply/nested/package/structure"
            createJavaClass(
                "DeepClass",
                "dev.aurakai.deep.very.deeply.nested.package.structure",
                "",
                testProjectDir.resolve("src/main/java")
            )

            // When
            val result = runGradleTask("compileJava")

            // Then
            assertEquals(TaskOutcome.SUCCESS, result.task(":compileJava")?.outcome)
        }

        @Test
        @DisplayName("Should handle empty source directories")
        fun shouldHandleEmptySourceDirectories() {
            // Given
            buildFile.writeText(BASIC_JAVA_BUILD_SCRIPT)
            // Create empty source directories
            testProjectDir.resolve("src/main/java/empty1").createDirectories()
            testProjectDir.resolve("src/main/java/empty2").createDirectories()
            testProjectDir.resolve("src/main/resources/empty").createDirectories()

            // When
            val result = runGradleTask("build")

            // Then
            assertEquals(TaskOutcome.NO_SOURCE, result.task(":compileJava")?.outcome)
        }
    }

    // Helper methods

    private fun runGradleTask(vararg arguments: String): BuildResult {
        return GradleRunner.create()
            .withGradleVersion(GRADLE_VERSION)
            .withProjectDir(testProjectDir.toFile())
            .withArguments(arguments.toList() + "--stacktrace")
            .build()
    }

    private fun createMainJavaClass() {
        createJavaClass(
            "Main", "dev.aurakai", """
            public static void main(String[] args) {
                System.out.println("Hello, World from Java!");
            }
        """
        )
    }

    private fun createMainKotlinClass() {
        createKotlinClass(
            "Main", "dev.aurakai", """
            fun main(args: Array<String>) {
                println("Hello, World from Kotlin!")
            }
        """
        )
    }

    private fun createInvalidJavaClass() {
        val javaDir = testProjectDir.resolve("src/main/java/dev/aurakai")
        javaDir.createDirectories()

        javaDir.resolve("Invalid.java").writeText(
            """
            package dev.aurakai;

            public class Invalid {
                // Invalid syntax - missing semicolon
                public void method() {
                    System.out.println("test")
                }

                // Another error - invalid return type
                public void returnsInt() {
                    return 42;
                }
            }
        """
        )
    }

    private fun createKotlinCoroutinesClass() {
        createKotlinClass(
            "CoroutinesExample", "dev.aurakai", """
            import kotlinx.coroutines.*

            suspend fun main() {
                val job = GlobalScope.launch {
                    delay(1000)
                    println("Coroutines work!")
                }
                job.join()
            }
        """
        )
    }

    private fun createKotlinSerializationClass() {
        createKotlinClass(
            "SerializationExample", "dev.aurakai", """
            import kotlinx.serialization.*
            import kotlinx.serialization.json.*

            @Serializable
            data class User(val name: String, val age: Int)

            fun main() {
                val user = User("John", 30)
                val json = Json.encodeToString(user)
                println(json)
            }
        """
        )
    }

    private fun createJavaClass(
        className: String,
        packageName: String,
        additionalMethods: String = "",
        baseDir: Path = testProjectDir.resolve("src/main/java"),
    ) {
        val packagePath = packageName.replace('.', '/')
        val javaDir = baseDir.resolve(packagePath)
        javaDir.createDirectories()

        javaDir.resolve("$className.java").writeText(
            """
            package $packageName;

            public class $className {
                $additionalMethods
            }
        """
        )
    }

    private fun createKotlinClass(
        className: String,
        packageName: String,
        additionalContent: String = "",
        baseDir: Path = testProjectDir.resolve("src/main/kotlin"),
    ) {
        val packagePath = packageName.replace('.', '/')
        val kotlinDir = baseDir.resolve(packagePath)
        kotlinDir.createDirectories()

        kotlinDir.resolve("$className.kt").writeText(
            """
            package $packageName

            class $className {
                // Kotlin class implementation
            }

            $additionalContent
        """
        )
    }
}
