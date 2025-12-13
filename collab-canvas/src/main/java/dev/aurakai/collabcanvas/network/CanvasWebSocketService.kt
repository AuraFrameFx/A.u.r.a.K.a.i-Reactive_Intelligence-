package dev.aurakai.collabcanvas.network

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CanvasWebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    private val TAG = "CanvasWebSocket"
    private var webSocket: WebSocket? = null
    private val _events = MutableSharedFlow<CanvasWebSocketEvent>()
    val events: SharedFlow<CanvasWebSocketEvent> = _events.asSharedFlow()

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "WebSocket connection opened")
            _events.tryEmit(CanvasWebSocketEvent.Connected)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "Message received: $text")
            try {
                val message = gson.fromJson(text, CanvasWebSocketMessage::class.java)
                _events.tryEmit(CanvasWebSocketEvent.MessageReceived(message))
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing WebSocket message", e)
                _events.tryEmit(CanvasWebSocketEvent.Error("Error parsing message: ${e.message}"))
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d(TAG, "Binary message received")
            _events.tryEmit(CanvasWebSocketEvent.BinaryMessageReceived(bytes))
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closing: $code / $reason")
            _events.tryEmit(CanvasWebSocketEvent.Closing(code, reason))
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closed: $code / $reason")
            _events.tryEmit(CanvasWebSocketEvent.Disconnected)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket error", t)
            _events.tryEmit(CanvasWebSocketEvent.Error(t.message ?: "Unknown error"))
        }
    }

    fun connect(url: String) {
        if (webSocket != null) {
            Log.w(TAG, "WebSocket already connected")
            return
        }

        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = okHttpClient.newWebSocket(request, webSocketListener)
    }

    fun disconnect() {
        webSocket?.close(1000, "User initiated disconnect")
        webSocket = null
    }

    fun sendMessage(message: CanvasWebSocketMessage): Boolean {
        return try {
            val json = gson.toJson(message)
            webSocket?.send(json) ?: run {
                Log.e(TAG, "WebSocket is not connected")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending WebSocket message", e)
            false
        }
    }

    fun isConnected(): Boolean {
        return webSocket != null
    }
}

sealed class CanvasWebSocketEvent {
    object Connected : CanvasWebSocketEvent()
    object Disconnected : CanvasWebSocketEvent()
    data class MessageReceived(val message: CanvasWebSocketMessage) : CanvasWebSocketEvent()
    data class BinaryMessageReceived(val bytes: ByteString) : CanvasWebSocketEvent()
    data class Error(val message: String) : CanvasWebSocketEvent()
    data class Closing(val code: Int, val reason: String) : CanvasWebSocketEvent()
}

sealed class CanvasWebSocketMessage {
    abstract val type: String
    abstract val canvasId: String
    abstract val userId: String
    abstract val timestamp: Long
}

data class ElementAddedMessage(
    override val canvasId: String,
    override val userId: String,
    override val timestamp: Long = System.currentTimeMillis(),
    val element: CanvasElement,
) : CanvasWebSocketMessage() {
    override val type: String = "ELEMENT_ADDED"
}

data class ElementUpdatedMessage(
    override val canvasId: String,
    override val userId: String,
    override val timestamp: Long = System.currentTimeMillis(),
    val elementId: String,
    val updates: Map<String, Any>,
) : CanvasWebSocketMessage() {
    override val type: String = "ELEMENT_UPDATED"
}

data class ElementRemovedMessage(
    override val canvasId: String,
    override val userId: String,
    override val timestamp: Long = System.currentTimeMillis(),
    val elementId: String,
) : CanvasWebSocketMessage() {
    override val type: String = "ELEMENT_REMOVED"
}
