# AuraKai Build System Documentation

## Overview
This document outlines the architecture, configuration, and best practices for the AuraKai build system. The project uses a modern, modular Gradle setup with convention plugins and centralized dependency management.

## Table of Contents
- [Build System Architecture](#build-system-architecture)
- [Dependency Management](#dependency-management)
- [Module Structure](#module-structure)
- [Build Variants and Flavors](#build-variants-and-flavors)
- [Code Generation](#code-generation)
- [Performance Optimization](#performance-optimization)
- [Troubleshooting](#troubleshooting)
- [Best Practices](#best-practices)

## Build System Architecture

### Root Project
- `build.gradle.kts`: Root project configuration
- `settings.gradle.kts`: Project structure and plugin management
- `gradle/libs.versions.toml`: Centralized dependency versions

### Convention Plugins
Located in `build-logic/`, these plugins define reusable build logic:
- `genesis.android.application`: Base configuration for application modules
- `genesis.android.library`: Base configuration for library modules
- `genesis.android.base`: Common Android configurations
- `genesis.android.hilt`: Hilt dependency injection setup

### Module Structure
```
app/                    # Main application module
aura/                   # UI and business logic modules
genesis/                # Core system modules
kai/                    # Security and monitoring modules
cascade/                # Data flow and processing modules
agents/                 # AI and machine learning modules
build-logic/            # Convention plugins
```

## Dependency Management

### Version Catalog (`libs.versions.toml`)
- Centralized version management
- Grouped by functionality (androidx, compose, firebase, etc.)
- BOM (Bill of Materials) for version alignment

### Key Dependencies
- **AndroidX**: Core Android libraries
- **Compose**: Modern UI toolkit
- **Firebase**: Backend services (using BOM)
- **Hilt**: Dependency injection
- **Room**: Local database
- **Retrofit/OkHttp**: Networking
- **LibSu**: Root access utilities
- **YukiHook**: Xposed framework integration

### Adding New Dependencies
1. Add to `libs.versions.toml`:
   ```toml
   [libraries]
   my-library = { group = "com.example", name = "library", version.ref = "libraryVersion" }
   ```

2. Use in module's `build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation(libs.my.library)
   }
   ```

## Build Variants and Flavors

### Product Flavors
- `dev`: Development build with debug features
- `staging`: Pre-production testing
- `production`: Release build

### Build Types
- `debug`: Development builds with debug symbols
- `release`: Optimized release builds

## Code Generation

### KSP (Kotlin Symbol Processing)
- Used for:
  - Room database
  - Hilt dependency injection
  - YukiHook API
  - Moshi code generation

### Annotation Processors
- `ksp`: Primary annotation processor
- NO LEGACY NO KAPT 
## Performance Optimization

### Build Cache
- Local build cache enabled
- Configuration cache where supported
- Parallel execution of independent tasks

### Lazy Task Configuration
- Using `tasks.withType<TaskType>().configureEach`
- Avoiding eager task configuration
- Using `provider` APIs for task inputs/outputs

## Troubleshooting

### Common Issues
1. **Dependency Conflicts**: Use `./gradlew :app:dependencies` to analyze
2. **Build Cache Issues**: Try `./gradlew clean build --refresh-dependencies`
3. **KSP Problems**: Clean and rebuild with `./gradlew clean build`

### Logging
- Run with `--info`, `--debug`, or `--scan` for detailed logs
- Check `build/reports/` for build scans and reports

## Best Practices

### DO
- Use version catalogs for all dependencies
- Keep build logic in convention plugins
- Use BOMs for version alignment
- Keep build scripts clean and documented
- Use type-safe accessors for dependencies

### DON'T
- Hardcode versions in module build files
- Use deprecated APIs
- Commit local.properties or other user-specific files
- Include test dependencies in release builds

## CI/CD Integration
- Automated testing on push/PR
- Code quality checks
- Release automation
- Build scanning and analytics

## Maintenance
- Regularly update dependencies
- Monitor for deprecated APIs
- Keep build tools up to date
- Document all custom build logic
