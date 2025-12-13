package dev.aurakai.auraframefx.di.qualifiers

import javax.inject.Qualifier

/**
 * Dagger qualifier for providing the base URL for API requests.
 * This allows for different base URLs to be injected in different contexts
 * (e.g., production, staging, development).
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl
