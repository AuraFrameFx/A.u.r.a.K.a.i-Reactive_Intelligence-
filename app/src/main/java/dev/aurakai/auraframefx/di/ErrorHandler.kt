package dev.aurakai.auraframefx.di

/** Minimal ErrorHandler interface placeholder to satisfy KSP bindings during incremental build. */
interface ErrorHandler {
    fun handleError(throwable: Throwable)
}

