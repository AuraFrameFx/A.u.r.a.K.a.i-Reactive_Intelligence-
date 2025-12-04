package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ui.KaiController
import javax.inject.Singleton

/**
 * Hilt Module for providing KaiController as a singleton dependency.
 *
 * KaiController manages gesture-based interactions with the Kai assistant,
 * including taps, long presses, and swipe gestures for navigation and control.
 */
@Module
@InstallIn(SingletonComponent::class)
object KaiModule {

    /**
     * Provides a singleton instance of KaiController.
     *
     * KaiController is a stateful controller with no external dependencies,
     * managing its own internal state for Kai UI elements and gesture interactions.
     *
     * @return A KaiController instance for managing Kai assistant interactions
     */
    @Provides
    @Singleton
    fun provideKaiController(): KaiController {
        // KaiController has no dependencies - it manages its own state internally
        return KaiController()
    }
}
