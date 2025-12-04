package dev.aurakai.auraframefx.system.homescreen.model

import dev.aurakai.auraframefx.system.homescreen.HomeScreenTransitionType

data class HomeScreenTransitionConfig(
    val type: HomeScreenTransitionType = HomeScreenTransitionType.GLOBE_ROTATE,
    val duration: Int = 500,
    val properties: Map<String, Any> = emptyMap(),
)
