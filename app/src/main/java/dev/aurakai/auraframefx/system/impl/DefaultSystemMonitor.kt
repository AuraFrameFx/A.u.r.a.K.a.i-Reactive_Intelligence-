package dev.aurakai.auraframefx.system.impl

import android.content.Context
import dev.aurakai.auraframefx.utils.AuraFxLogger
import dev.aurakai.auraframefx.system.monitor.SystemMonitor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSystemMonitor @Inject constructor(
    context: Context,
    logger: AuraFxLogger,
) : SystemMonitor(context, logger)
