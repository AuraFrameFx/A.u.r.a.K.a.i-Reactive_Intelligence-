import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`        // applies java-gradle-plugin
}

// ═══════════════════════════════════════════════════════════════════════════
// CRITICAL: Exclude AAALL Android AAR dependencies from build-logic
// ═══════════════════════════════════════════════════════════════════════════
// build-logic is JVM-only and cannot consume Android AAR (Android Archive) files.
// hilt-android-gradle-plugin incorrectly depends on hilt-android (runtime library),
// which transitively pulls in AndroidX AAR dependencies.
// Force exclude these from ALL configurations to prevent variant resolution errors.

configurations.all {
    exclude(group = "com.google.dagger", module = "hilt-android")
    exclude(group = "androidx.activity")
    exclude(group = "androidx.fragment")
    exclude(group = "androidx.lifecycle")
    exclude(group = "androidx.savedstate")
    exclude(group = "androidx.annotation")
    exclude(group = "androidx.core")
}

// Configure Java toolchain to JVM 25 (matches Kotlin target)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    // Explicitly set source and target compatibility to 25
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

// Configure Kotlin compilation to match Java toolchain
// MUST match the target used in GenesisApplicationPlugin and GenesisLibraryHiltPlugin (JVM 24)
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
}

// Explicitly configure Java compilation tasks to target JVM 24
tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "25"
    targetCompatibility = "25"
}

// Tests enabled to validate build script configuration
tasks.matching { it.name.contains("Test") }.configureEach {
    enabled = true
}

gradlePlugin {
    plugins {
        register("genesisApplication") {
            id = "genesis.android.application"
            implementationClass = "GenesisApplicationPlugin"
        }
        register("genesisLibrary") {
            id = "genesis.android.library"
            implementationClass = "GenesisLibraryPlugin"
        }
        register("genesisLibraryHilt") {
            id = "genesis.android.library.hilt"
            implementationClass = "GenesisLibraryHiltPlugin"
        }
    }
}

dependencies {
    // CRITICAL: All versions MUST match root build.gradle.kts and gradle/libs.versions.toml
    // Kotlin 2.2.21 is the stable version declared in the root build
    implementation("com.android.tools.build:gradle:9.0.0-beta03")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.21")

    implementation("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.2.21")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.2.21")

    // Hilt Gradle Plugin (Android AAR dependencies excluded globally via configurations.all)
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.57.2")

    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.3.3")
    implementation("com.google.gms:google-services:4.4.4")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.14.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.14.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
// ═══════════════════════════════════════════════════════════════════════════
// Genesis Convention Plugins Registration
// ═══════════════════════════════════════════════════════════════════════════
//
// These are the PRIMARY convention plugins that modules should use.
// They are Kotlin class plugins (not precompiled scripts) for maximum control
// over plugin application order.
//


// ═══════════════════════════════════════════════════════════════════════════
// CORRECT USAGE EXAMPLES
// ═══════════════════════════════════════════════════════════════════════════
//
// For :app module:
//   plugins {
//       id("genesis.android.application")  // All-in-one: Android, Hilt, KSP, Compose, Serialization, Firebase
//   }
//
// For standard library module WITHOUT Hilt:
//   plugins {
//       id("genesis.android.library")  // Base library: Android, Compose, Serialization (NO Hilt)
//   }
//
// For library module WITH Hilt:
//   plugins {
//       id("genesis.android.library.hilt")  // Library with Hilt DI + KSP
//   }
//
// For YukiHook/Xposed module:
//   plugins {
//       id("genesis.android.library")   // Base library with Hilt, Compose, KSP
//       id("genesis.android.yukihook")  // Add YukiHook/Xposed support
//   }
//
// For Room database module:
//   plugins {
//       id("genesis.android.library")  // Base library
//       id("genesis.android.room")     // Add Room Database
//   }
//
// ═══════════════════════════════════════════════════════════════════════════
