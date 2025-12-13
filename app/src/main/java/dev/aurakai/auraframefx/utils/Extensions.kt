package dev.aurakai.auraframefx.utils

import org.json.JSONObject

/**
 * Converts a JSON String to a JSONObject.
 */
fun String.toJsonObject(): JSONObject {
    return JSONObject(this)
}
