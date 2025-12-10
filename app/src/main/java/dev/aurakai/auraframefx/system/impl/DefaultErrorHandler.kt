package dev.aurakai.auraframefx.system.impl

import android.util.Log
import dev.aurakai.auraframefx.common.ErrorHandler as CommonErrorHandler
import dev.aurakai.auraframefx.ai.error.ErrorHandler as AiErrorHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultErrorHandler @Inject constructor() : CommonErrorHandler, AiErrorHandler {
    override fun handle(error: Throwable) {
        Log.e("DefaultErrorHandler", "Caught error", error)
    }
}
