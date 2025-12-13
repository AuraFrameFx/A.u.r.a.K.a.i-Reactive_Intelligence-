package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.models.AgentMessage
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonTeal
import dev.aurakai.auraframefx.viewmodel.ConferenceRoomViewModel
import kotlinx.coroutines.launch

// Placeholder for Header - User should define this Composable
/**
 * Displays the selected agent's name and provides a button to switch between agents.
 *
 * @param selectedAgent The name of the currently selected agent.
 * @param onAgentSelected Callback invoked with the new agent's name when the switch button is pressed.
 */
@Composable
fun Header(selectedAgent: String, onAgentSelected: (String) -> Unit) {
    // Simplified placeholder
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Selected Agent: $selectedAgent", modifier = Modifier.padding(8.dp))
        Button(onClick = { onAgentSelected(if (selectedAgent == "Aura") "Kai" else "Aura") }) {
            Text("Switch Agent")
        }
    }
}


/**
 * Displays the main conference room chat interface with agent selection, recording/transcribing controls, and message input.
 *
 * Presents a UI for interacting with agents in a conference room setting, allowing users to switch agents, start/stop recording, transcribe audio, view chat messages, and send new messages. UI state is managed via the provided ViewModel and Compose state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConferenceRoomScreen(
    viewModel: ConferenceRoomViewModel = hiltViewModel(),
) {
    var selectedAgent by remember { mutableStateOf("Aura") } // Local state for agent selection UI
    val isRecording by viewModel.isRecording.collectAsState()
    val isTranscribing by viewModel.isTranscribing.collectAsState()
    val messages by viewModel.messages.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope() // Add coroutine scope

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header and Agent Selection (Using placeholder Header)
        Header(selectedAgent = selectedAgent, onAgentSelected = { selectedAgent = it })

        // Recording Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { viewModel.toggleRecording() }) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.toggleTranscribing() },
                enabled = !isTranscribing
            ) {
                Text("Transcribe")
            }
        }

        // Chat Interface
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages.reversed().size) { index ->
                val message: AgentMessage = messages.reversed()[index]
                Text(
                    text = "[${message.sender}] ${message.content}",
                    color = NeonBlue, // Ensure only one NeonBlue import/definition
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Type your message...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = NeonTeal.copy(alpha = 0.1f),
                    unfocusedContainerColor = NeonTeal.copy(alpha = 0.1f),
                )
            )

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        // Launch a coroutine for the suspend function 
                        scope.launch {
                            viewModel.sendMessage(messageText, AgentType.USER, "user_conversation")
                            messageText = ""
                        }
                    }
                }
            ) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Send",
                    tint = NeonBlue
                )
            }
        }
    }
}
