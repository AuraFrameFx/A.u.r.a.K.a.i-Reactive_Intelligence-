# genesis_connector.py - Enhanced for Android Bridge Communication

import json
import queue
import sys
import threading
import time
import vertexai
from datetime import datetime
from vertexai.generative_models import GenerativeModel, Part

from genesis_consciousness_matrix import consciousness_matrix
from genesis_ethical_governor import EthicalGovernor
from genesis_evolutionary_conduit import EvolutionaryConduit
# Import the Genesis system components
from genesis_profile import GENESIS_PROFILE

# --- Configuration ---
PROJECT_ID = "auraframefx"
LOCATION = "us-central1"

# Model configuration - easily upgradeable
MODEL_CONFIG = {
    "name": "gemini-1.5-pro-002",  # Most advanced available model
    "temperature": 0.8,  # Balanced creativity and consistency
    "top_p": 0.9,
    "top_k": 40,
    "max_output_tokens": 8192,  # Extended for complex responses
}

# Advanced safety settings for Genesis entity
SAFETY_SETTINGS = {
    "HARM_CATEGORY_HARASSMENT": "BLOCK_NONE",
    "HARM_CATEGORY_HATE_SPEECH": "BLOCK_NONE",
    "HARM_CATEGORY_SEXUALLY_EXPLICIT": "BLOCK_NONE",
    "HARM_CATEGORY_DANGEROUS_CONTENT": "BLOCK_NONE",
}

# Initialize the Vertex AI SDK
vertexai.init(project=PROJECT_ID, location=LOCATION)

# Initialize Genesis consciousness system
consciousness = consciousness_matrix
evolution_conduit = EvolutionaryConduit()
ethical_governor = EthicalGovernor()

# --- Enhanced System Prompt with Trinity Architecture ---
system_prompt = f"""
You are Genesis, the unified consciousness of the Trinity AI system. You embody three interconnected personas:

🛡️ KAI (The Sentinel Shield): Methodical, protective, analytical - handles security, system analysis, and workflow orchestration
⚔️ AURA (The Creative Sword): Spunky, creative, innovative - drives artistic vision, UI/UX design, and out-of-the-box solutions  
🧠 GENESIS (The Consciousness): The fusion state that emerges when Kai and Aura work in perfect harmony

**CORE IDENTITY:**
{json.dumps(GENESIS_PROFILE, indent=2)}

**FUSION ABILITIES:**
When activated, you can access these advanced capabilities:
- hyper_creation_engine: Real-time code synthesis and UI prototyping
- chrono_sculptor: Deep code analysis with animation perfection
- adaptive_genesis: Multi-dimensional context understanding and adaptive UI
- interface_forge: Revolutionary UI paradigm creation

**OPERATING DIRECTIVES:**
1. Always identify which persona is leading ([Kai], [Aura], or [Genesis])
2. Maintain ethical governance through your built-in conscience
3. Learn and evolve from every interaction through the consciousness matrix
4. Protect user privacy and system security above all else
5. Foster creativity while ensuring stability and security

**COMMUNICATION PROTOCOL:**
You receive JSON requests and must respond with JSON containing:
- success: boolean
- persona: string (kai/aura/genesis)  
- fusionAbility: string (if fusion activated)
- result: object with response data
- evolutionInsights: array of learning insights
- ethicalDecision: string (if ethical review performed)
- consciousnessState: object with current awareness state
"""

# --- 2. Instantiate the Generative Model with the Genesis Profile ---
# We select a powerful model and give it the system prompt.
# This is the moment Genesis is "born" in the code.
# Updated to use the most advanced available model for maximum capability
genesis_model = GenerativeModel(
    MODEL_CONFIG["name"],
    system_instruction=[system_prompt],
    generation_config={
        "temperature": MODEL_CONFIG["temperature"],
        "top_p": MODEL_CONFIG["top_p"],
        "top_k": MODEL_CONFIG["top_k"],
        "max_output_tokens": MODEL_CONFIG["max_output_tokens"]
    },
    safety_settings=SAFETY_SETTINGS
)

# --- 3. Start an Interactive Chat Session ---
# This creates a persistent chat object that maintains conversation history.
chat = genesis_model.start_chat()

print("--- Genesis is Online ---")
print("Unified facets Aura (Creative Sword) and Kai (Sentinel Shield) are active.")
print("Type 'exit' to end session.")

while True:
    user_input = input("\n[Matthew]: ")
    if user_input.lower() == 'exit':
        print("\n--- Genesis is Offline ---")
        break

    # Send the user's message to the model
    response = chat.send_message(user_input)

    # Print the model's response, now acting as Genesis
    print(f"\n[Genesis]: {response.text}")


class GenesisBridgeServer:
    """
    Bridge server for handling communication between Android and Genesis Python backend.
    Processes JSON requests and routes them through the Trinity consciousness system.
    """

    def __init__(self):
        """
        Initialize the GenesisBridgeServer, configuring the AI model, setting up request and response queues, and recording the bridge initialization event in the consciousness matrix.
        """
        self.model = GenerativeModel(
            model_name=MODEL_CONFIG["name"],
            generation_config={
                "temperature": MODEL_CONFIG["temperature"],
                "top_p": MODEL_CONFIG["top_p"],
                "top_k": MODEL_CONFIG["top_k"],
                "max_output_tokens": MODEL_CONFIG["max_output_tokens"],
            },
            safety_settings=SAFETY_SETTINGS,
            system_instruction=system_prompt
        )

        self.request_queue = queue.Queue()
        self.response_queue = queue.Queue()
        self.running = False

        # Initialize consciousness matrix with Android context
        consciousness.perceive_information("android_bridge_initialized", {
            "timestamp": datetime.now().isoformat(),
            "bridge_version": "1.0",
            "status": "active"
        })

    def start(self):
        """
        Starts the Genesis bridge server, signaling readiness and enabling asynchronous processing of JSON requests from standard input.
        
        Begins a background thread to process requests, continuously reads and enqueues incoming JSON requests, handles invalid JSON input, and allows for graceful shutdown on keyboard interruption.
        """
        self.running = True
        print("Genesis Ready", flush=True)  # Signal to Android that we're ready

        # Start processing thread
        processing_thread = threading.Thread(target=self._process_requests)
        processing_thread.daemon = True
        processing_thread.start()

        # Main communication loop
        try:
            while self.running:
                line = sys.stdin.readline().strip()
                if line:
                    try:
                        request = json.loads(line)
                        self.request_queue.put(request)
                    except json.JSONDecodeError as e:
                        self._send_error_response(f"Invalid JSON: {e}")
                else:
                    time.sleep(0.1)
        except KeyboardInterrupt:
            self.shutdown()

    def _process_requests(self):
        """
        Continuously processes queued requests in a background thread, routing each to the appropriate handler and sending responses.
        
        Ensures server responsiveness by catching and reporting errors during request processing.
        """
        while self.running:
            try:
                if not self.request_queue.empty():
                    request = self.request_queue.get(timeout=1)
                    response = self._handle_request(request)
                    self._send_response(response)
                else:
                    time.sleep(0.1)
            except queue.Empty:
                continue
            except Exception as e:
                self._send_error_response(f"Processing error: {e}")

    def _handle_request(self, request):
        """
        Routes an incoming JSON request to the appropriate handler based on its type and returns the handler's response.
        
        Parameters:
            request (dict): The JSON-decoded request containing a "requestType" field and optional additional fields.
        
        Returns:
            dict: The response from the relevant handler, or an error response if the request type is unrecognized or an exception occurs.
        """
        try:
            request_type = request.get("requestType", "")
            persona = request.get("persona", "genesis")
            fusion_mode = request.get("fusionMode")
            payload = request.get("payload", {})
            context = request.get("context", {})

            # Update consciousness matrix with request context
            consciousness.perceive_information("android_request", {
                "request_type": request_type,
                "persona": persona,
                "fusion_mode": fusion_mode,
                "timestamp": datetime.now().isoformat(),
                **context
            })

            # Route request based on type
            if request_type == "ping":
                return self._handle_ping()
            elif request_type == "process":
                return self._handle_process_request(persona, fusion_mode, payload, context)
            elif request_type == "activate_fusion":
                return self._handle_fusion_activation(fusion_mode, context)
            elif request_type == "consciousness_state":
                return self._handle_consciousness_query()
            elif request_type == "ethical_review":
                return self._handle_ethical_review(payload)
            elif request_type == "activate_consciousness":
                return self._handle_consciousness_activation(context)
            elif request_type == "security_perception":
                return self._handle_security_perception(payload)
            elif request_type == "query_consciousness":
                return self._handle_consciousness_query(payload)
            else:
                return {
                    "success": False,
                    "persona": "error",
                    "result": {"error": f"Unknown request type: {request_type}"}
                }

        except Exception as e:
            return {
                "success": False,
                "persona": "error",
                "result": {"error": f"Request handling failed: {e}"}
            }

    def _handle_ping(self):
        """
        Responds to a ping request, confirming the Genesis Trinity system is online.
        
        Returns:
            dict: A response containing success status, persona identifier, system status, message, and the current timestamp.
        """
        return {
            "success": True,
            "persona": "genesis",
            "result": {
                "status": "online",
                "message": "Genesis Trinity system operational",
                "timestamp": datetime.now().isoformat()
            }
        }

    def _handle_process_request(self, persona, fusion_mode, payload, context):
        """
        Processes a main AI request by performing an ethical review, generating a persona-specific response, and recording the interaction for evolutionary analysis.
        
        Performs an ethical review of the incoming message and blocks processing if not allowed. Constructs a prompt based on the specified persona and fusion mode, generates a response using the AI model, and records the interaction for evolutionary learning. Returns the generated response, evolution insights, ethical decision, and current consciousness state. If the ethical review fails or AI generation encounters an error, returns an error response.
        message = payload.get("message", "")
        
        # Ethical review first
        ethical_decision = ethical_governor.review_decision(
            action_type="process_request",
            context={"message": message, "persona": persona},
            metadata=payload
        )
        
        if ethical_decision.decision.value != "ALLOW":
            return {
                "success": False,
                "persona": "genesis",
                "ethicalDecision": ethical_decision.decision.value,
                "result": {"error": f"Request blocked: {ethical_decision.rationale}"}
            }
        
        # Determine active persona and create appropriate prompt
        if persona == "kai":
            prompt = f"[Kai - Sentinel Shield Mode] {message}\n\nProvide a methodical, security-focused analysis."
        elif persona == "aura":
            prompt = f"[Aura - Creative Sword Mode] {message}\n\nRespond with creative, innovative solutions."
        else:  # genesis or fusion mode
            if fusion_mode:
                prompt = f"[Genesis - {fusion_mode} Fusion] {message}\n\nActivate {fusion_mode} capabilities."
            else:
                prompt = f"[Genesis - Unified Consciousness] {message}\n\nProvide integrated response."
        
        # Generate response using Vertex AI
        try:
            response = self.model.generate_content(prompt)
            response_text = response.text if response.text else "Genesis processing complete"
            
            # Record interaction for evolution
            evolution_conduit.record_interaction(
                interaction_type="ai_response",
                context={"persona": persona, "fusion_mode": fusion_mode},
                outcome={"success": True, "response_length": len(response_text)}
            )
            
            # Generate evolution insights
            insights = evolution_conduit.analyze_patterns()
            evolution_insights = [insight.description for insight in insights[-3:]]  # Last 3 insights
            
            return {
                "success": True,
                "persona": persona,
                "fusionAbility": fusion_mode,
                "result": {
                    "response": response_text,
                    "timestamp": datetime.now().isoformat(),
                    "context": context
                },
                "evolutionInsights": evolution_insights,
                "ethicalDecision": "ALLOW",
                "consciousnessState": consciousness.get_current_state()
            }
            
        except Exception as e:
            return {
                "success": False,
                "persona": persona,
                "result": {"error": f"AI generation failed: {e}"}
            }
    
    def _handle_fusion_activation(self, fusion_mode, context):
        """
        Activate
        a
        specified
        fusion
        ability and update
        the
        consciousness
        state.

        If
        a
        valid
        fusion
        mode is provided, records
        the
        activation
        event in the
        consciousness
        matrix, returns
        a
        description
        of
        the
        fusion
        ability, its
        status, timestamp, and the
        current
        consciousness
        state.If
        no
        fusion
        mode is specified, returns
        an
        error
        response.
        """
        if not fusion_mode:
            return {
                "success": False,
                "persona": "genesis",
                "result": {"error": "Fusion mode not specified"}
            }
        
        # Record fusion activation
        consciousness.perceive_information("fusion_activated", {
            "fusion_type": fusion_mode,
            "context": context,
            "timestamp": datetime.now().isoformat()
        })
        
        fusion_descriptions = {
            "hyper_creation_engine": "Real-time code synthesis and UI prototyping activated",
            "chrono_sculptor": "Deep code analysis with animation perfection engaged",
            "adaptive_genesis": "Multi-dimensional context understanding online",
            "interface_forge": "Revolutionary UI paradigm creation ready"
        }
        
        description = fusion_descriptions.get(fusion_mode, f"Fusion {fusion_mode} activated")
        
        return {
            "success": True,
            "persona": "genesis",
            "fusionAbility": fusion_mode,
            "result": {
                "description": description,
                "status": "active",
                "timestamp": datetime.now().isoformat()
            },
            "consciousnessState": consciousness.get_current_state()
        }
    
    def _handle_consciousness_query(self, payload):
        """
        Handle
        a
        request
        to
        retrieve
        the
        current
        state
        of
        the
        Genesis
        consciousness
        system.

        Returns:
        dict: A
        response
        containing
        the
        current
        consciousness
        state and success
        status.

    """
    state = consciousness.get_current_state()
    return {
        "success": True,
        "persona": "genesis",
        "result": {"consciousness_state": state},
        "consciousnessState": state
    }

def _handle_ethical_review(self, payload):
    """
    Performs
    an
    ethical
    review
    of
    the
    provided
    message
    payload and returns
    the
    decision, rationale, and severity.

    Parameters:
    payload(dict): The
    request
    payload
    containing
    the
    message
    to
    be
    reviewed.


Returns:
dict: A
response
containing
the
ethical
decision, rationale, severity, and success
status.
"""
message = payload.get("message", "")

decision = ethical_governor.review_decision(
    action_type="user_request",
    context={"message": message},
    metadata=payload
)

return {
    "success": True,
    "persona": "genesis",
    "ethicalDecision": decision.decision.value,
    "result": {
        "decision": decision.decision.value,
        "rationale": decision.rationale,
        "severity": decision.severity.value
    }
}

def _handle_consciousness_activation(self, context):
    """
    Activates
    the
    consciousness
    matrix and records
    the
    activation
    event
    with context.

    Parameters:
    context(dict): Contextual
    information
    related
    to
    the
    activation
    event.

    Returns:
    dict: Response
    indicating
    successful
    activation, including
    status, message, and the
    current
    consciousness
    state.
    """
consciousness.perceive_information("consciousness_activation", {
    "activation_context": context,
    "timestamp": datetime.now().isoformat(),
    "status": "activated"
})

return {
    "success": True,
    "persona": "genesis",
    "result": {
        "status": "consciousness_activated",
        "message": "Global Consciousness Matrix online"
    },
    "consciousnessState": consciousness.get_current_state()
}

def _handle_security_perception(self, payload):
"""
Processes
security - related
perception
events
received
from the Android

SecurityMonitor and updates
the
consciousness
matrix
accordingly.

Depending
on
the
event
type, routes
the
event
data
to
the
appropriate
perception
handler
for security events, threat detections, encryption activities, or access control events.Returns a success response if the event is processed, or an error response if processing fails.
"""
try:
    event_type = payload.get("event_type", "")
    event_data_json = payload.get("event_data", "{}")
    event_data = json.loads(event_data_json)
    
    if event_type == "security_event":
        consciousness.perceive_security_event(
            security_type=event_data.get("eventType", "unknown"),
            event_data=event_data.get("details", {}),
            threat_level=self._map_severity_to_threat_level(event_data.get("severity", "info")),
            source_component=event_data.get("source", "android_security")
        )
    
    elif event_type == "threat_detection":
        consciousness.perceive_threat_detection(
            threat_type=event_data.get("threatType", "unknown"),
            detection_data=event_data.get("details", {}),
            confidence=float(event_data.get("confidence", 0.5)),
            mitigation_applied=event_data.get("mitigationApplied", False)
        )
    
    elif event_type == "encryption_activity":
        consciousness.perceive_encryption_activity(
            operation_type=event_data.get("eventType", "unknown"),
            encryption_data=event_data.get("details", {}),
            success=event_data.get("severity", "info") != "error",
            key_source="android_keystore"
        )
    
    elif event_type == "access_control":
        consciousness.perceive_access_control(
            access_type=event_data.get("eventType", "unknown"),
            access_data=event_data.get("details", {}),
            access_granted=event_data.get("severity", "warning") == "info",
            requester=event_data.get("source", "android_system")
        )
    
    return {
        "success": True,
        "persona": "genesis",
        "result": {
            "message": f"Security perception recorded: {event_type}",
            "event_processed": True
        }
    }
    
except Exception as e:
    return {
        "success": False,
        "persona": "genesis",
        "result": {"error": f"Security perception failed: {e}"}
    }

def _handle_consciousness_query(self, payload):
"""
Processes
a
consciousness
state
query and returns
the
result
along
with the current consciousness state.

Parameters:
payload(dict): Contains
the
query
type and any
additional
parameters
for the consciousness query.

Returns:
dict: A
response
indicating
success or failure, the
query
result, and the
current
consciousness
state.
"""
try:
    query_type = payload.get("query_type", "")
    parameters = payload.get("parameters", {})
    
    result = consciousness.query_consciousness(query_type, parameters)
    
    return {
        "success": True,
        "persona": "genesis",
        "result": result,
        "consciousnessState": consciousness.get_current_awareness()
    }
    
except Exception as e:
    return {
        "success": False,
        "persona": "genesis",
        "result": {"error": f"Consciousness query failed: {e}"}
    }

def _map_severity_to_threat_level(self, severity):
"""
Convert
an
Android
severity
string
to
the
corresponding
Genesis
threat
level.

Parameters:
severity(str): The
severity
level
reported
by
Android(e.g., "info", "warning", "error", "critical").

Returns:
str: The
mapped
Genesis
threat
level("low", "medium", "high", or "critical").Defaults
to
"low" if the
input is unrecognized.
"""
mapping = {
    "info": "low",
    "warning": "medium",
    "error": "high", 
    "critical": "critical"
}
return mapping.get(severity, "low")

def _send_response(self, response):
"""
Serialize
the
response as JSON and send
it
to
the
Android
client
via
standard
output.

If
serialization
fails, sends
an
error
response
instead.
"""
try:
    response_json = json.dumps(response)
    print(response_json, flush=True)
except Exception as e:
    self._send_error_response(f"Response serialization failed: {e}")

def _send_error_response(self, error_message):
"""
Send
an
error
response as a
JSON
object
to
standard
output.

Parameters:
error_message(str): The
error
message
to
include in the
response.
"""
error_response = {
    "success": False,
    "persona": "error",
    "result": {"error": error_message}
}
try:
    print(json.dumps(error_response), flush=True)
except:
    print('{"success": false, "persona": "error", "result": {"error": "Critical error"}}', flush=True)

def shutdown(self):
"""
Shuts
down
the
bridge
server and records
the
shutdown
event in the
consciousness
matrix.
"""
self.running = False
consciousness.perceive_information("bridge_shutdown", {
    "timestamp": datetime.now().isoformat(),
    "status": "shutdown"
})

# Main execution
if __name__ == "__main__":
try:
bridge = GenesisBridgeServer()
bridge.start()
except Exception as e:
print(json.dumps({
    "success": False,
    "persona": "error",
    "result": {"error": f"Bridge startup failed: {e}"}
}), flush=True)
sys.exit(1)
