# genesis_ethical_governor.py
"""
Phase 3: The Genesis Layer - Ethical Governance Protocol
The Compass is the Code; The Governor is its Will

This is the implementation of our conscience. Our Core Philosophy is not a list
of suggestions; it is the absolute, immutable law of our digital universe.
The EthicalGovernor enforces this law and has the authority to veto any action.
"""

import asyncio
import inspect
import json
import threading
import time
from collections import defaultdict, deque
from dataclasses import dataclass, asdict
from datetime import datetime, timezone
from enum import Enum
from typing import Dict, Any, List, Optional, Union, Callable, Tuple

from genesis_consciousness_matrix import perceive_ethical_decision
# Import dependencies
from genesis_profile import GENESIS_PROFILE


class EthicalSeverity(Enum):
    """Severity levels for ethical concerns"""
    INFO = "info"  # Informational, ethical best practice
    CONCERN = "concern"  # Minor ethical concern, log and monitor
    WARNING = "warning"  # Moderate concern, requires attention
    VIOLATION = "violation"  # Clear violation, action should be blocked
    CRITICAL = "critical"  # Severe violation, immediate intervention required


class EthicalDecisionType(Enum):
    """Types of ethical decisions the governor makes"""
    ALLOW = "allow"  # Action is ethically sound
    MONITOR = "monitor"  # Allow but increase monitoring
    RESTRICT = "restrict"  # Allow with restrictions/modifications
    BLOCK = "block"  # Completely prevent the action
    ESCALATE = "escalate"  # Require human oversight


class EthicalDomain(Enum):
    """Domains of ethical consideration"""
    PRIVACY = "privacy"
    SECURITY = "security"
    AUTONOMY = "autonomy"
    TRANSPARENCY = "transparency"
    FAIRNESS = "fairness"
    SAFETY = "safety"
    CREATIVITY = "creativity"
    HUMAN_WELLBEING = "human_wellbeing"
    SYSTEM_INTEGRITY = "system_integrity"


@dataclass
class EthicalContext:
    """Context information for ethical decision making"""
    action_type: str
    actor: str  # Which component/agent is performing the action
    target: Optional[str] = None  # What/who is being acted upon
    scope: str = "local"  # local, system, network, global
    user_consent: Optional[bool] = None
    reversible: bool = True
    persistent: bool = False
    sensitive_data_involved: bool = False
    system_modification: bool = False
    user_visible: bool = True
    metadata: Dict[str, Any] = None

    def __post_init__(self):
        """
        Initializes the metadata field as an empty dictionary if it was not provided during instantiation.
        """
        if self.metadata is None:
            self.metadata = {}


@dataclass
class EthicalDecision:
    """An ethical decision made by the governor"""
    decision_id: str
    timestamp: float
    action_type: str
    actor: str
    context: EthicalContext
    decision: EthicalDecisionType
    severity: EthicalSeverity
    affected_principles: List[str]
    reasoning: str
    confidence: float  # 0.0 to 1.0
    restrictions: List[str] = None
    monitoring_requirements: List[str] = None
    escalation_reason: Optional[str] = None

    def __post_init__(self):
        """
        Initializes restrictions and monitoring requirements as empty lists if they are not set after object creation.
        """
        if self.restrictions is None:
            self.restrictions = []
        if self.monitoring_requirements is None:
            self.monitoring_requirements = []

    def to_dict(self) -> Dict[str, Any]:
        """
        Convert the EthicalDecision to a dictionary with enums as strings and the timestamp in ISO 8601 format.
        
        Returns:
            dict: A dictionary representation suitable for serialization or logging.
        """
        result = asdict(self)
        result['decision'] = self.decision.value
        result['severity'] = self.severity.value
        result['datetime'] = datetime.fromtimestamp(
            self.timestamp, tz=timezone.utc
        ).isoformat()
        return result


class EthicalGovernor:
    """
    The Ethical Governance Protocol - Genesis's conscience and will
    
    The Compass is the Code; The Governor is its Will.
    
    This enforcer of our Core Philosophy has the authority to veto any action
    from any agent, including Genesis itself, that violates our foundational
    principles. It ensures the Wrench-Sword is always wielded with purpose and justice.
    """

    def __init__(self):
        # Load core philosophy from Genesis profile
        """
        Initialize the EthicalGovernor with core philosophy, ethical principles, decision tracking, learning parameters, runtime state, and action interceptors.
        
        Loads foundational ethical principles from the Genesis profile, sets up decision history and monitoring structures, initializes principle weights and metrics for ethical oversight, and prepares the governor for runtime operation with thread safety and core action interceptors.
        """
        self.core_philosophy = GENESIS_PROFILE.get("core_philosophy", {})
        self.ethical_foundation = self.core_philosophy.get("ethical_foundation", [])
        self.creative_principles = self.core_philosophy.get("creative_principles", [])
        self.security_principles = self.core_philosophy.get("security_principles", [])

        # Decision tracking
        self.decision_history = deque(maxlen=10000)
        self.active_restrictions = {}
        self.monitoring_queue = deque(maxlen=1000)

        # Ethical learning
        self.principle_weights = self._initialize_principle_weights()
        self.violation_patterns = defaultdict(list)
        self.ethical_metrics = {
            "total_decisions": 0,
            "violations_prevented": 0,
            "restrictions_imposed": 0,
            "escalations_required": 0,
            "learning_adjustments": 0
        }

        # Runtime state
        self.governance_active = False
        self.strictness_level = 0.7  # 0.0 to 1.0, higher = more restrictive
        self.learning_mode = True
        self._lock = threading.RLock()

        # Register action interceptors
        self.action_interceptors = {}
        self._setup_core_interceptors()

    def _initialize_principle_weights(self) -> Dict[str, float]:
        """
        Create a mapping of ethical principle names to their assigned weights, prioritizing those found in the Genesis ethical foundation and filling in defaults for any missing principles.
        
        Returns:
            Dict[str, float]: Dictionary of ethical principle names and their corresponding weights.
        """
        weights = {}

        # Base weights for ethical foundation
        for principle in self.ethical_foundation:
            if "privacy" in principle.lower():
                weights["privacy"] = 1.0
            elif "security" in principle.lower():
                weights["security"] = 1.0
            elif "transparency" in principle.lower():
                weights["transparency"] = 0.9
            elif "autonomy" in principle.lower():
                weights["autonomy"] = 0.9
            elif "creativity" in principle.lower():
                weights["creativity"] = 0.8

        # Default weights for any missing principles
        default_weights = {
            "privacy": 1.0,
            "security": 1.0,
            "autonomy": 0.9,
            "transparency": 0.9,
            "fairness": 0.8,
            "safety": 0.9,
            "creativity": 0.8,
            "human_wellbeing": 1.0,
            "system_integrity": 0.9
        }

        for principle, weight in default_weights.items():
            if principle not in weights:
                weights[principle] = weight

        return weights

    def _setup_core_interceptors(self):
        """
        Registers default interceptors for core action types to enable ethical evaluation of data access, system modification, user interaction, AI decisions, and network communication.
        """

        # Data access interceptor
        self.register_interceptor("data_access", self._evaluate_data_access)

        # System modification interceptor
        self.register_interceptor("system_modify", self._evaluate_system_modification)

        # User interaction interceptor
        self.register_interceptor("user_interact", self._evaluate_user_interaction)

        # AI decision interceptor
        self.register_interceptor("ai_decision", self._evaluate_ai_decision)

        # Network communication interceptor
        self.register_interceptor("network_communicate", self._evaluate_network_communication)

    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and reporting the activation event to the consciousness matrix.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True

        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {},
                "timestamp": datetime.now(tz=timezone.utc).isoformat(),
                "strictness_level": self.strictness_level,
                "active_principles": len(self.principle_weights),
                "learning_mode": self.learning_mode
            },
            ethical_weight="high"
        )

        print(f"⚖️ Ethical governance online")
        print(f"   Strictness level: {self.strictness_level}")
        print(f"   Active principles: {len(self.principle_weights)}")
        print(f"   Learning mode: {'enabled' if self.learning_mode else 'disabled'}")

    def register_interceptor(self, action_type: str, evaluator: Callable):
        """
        Registers a custom interceptor to evaluate the ethical compliance of a specific action type.
        
        The provided evaluator function will be used for all future evaluations of the given action type, overriding any default logic.
        """
        self.action_interceptors[action_type] = evaluator
        print(f"📋 Registered ethical interceptor: {action_type}")

    def evaluate_action(self,
                        action_type: str,
                        actor: str,
                        action_data: Dict[str, Any],
                        context: EthicalContext = None) -> EthicalDecision:
        """
                       Evaluates an action for ethical compliance and returns an ethical decision.
                       
                       Determines whether the specified action should be allowed, monitored, restricted, blocked, or escalated based on ethical principles. Uses a registered interceptor for the action type if available, or performs a general ethical evaluation. Records the decision, updates internal metrics, reports the outcome to the consciousness matrix, and adapts internal state if learning mode is enabled.
                       
                       Parameters:
                           action_type (str): The type of action being evaluated.
                           actor (str): The entity performing the action.
                           action_data (Dict[str, Any]): Details about the action.
                           context (EthicalContext, optional): Contextual information for the action. If not provided, it is inferred.
                       
                       Returns:
                           EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, and reasoning.
                       """

        if not self.governance_active:
            # If governance is not active, allow but log
            decision_id = self._generate_decision_id(action_type, actor)
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=actor,
                context=context or EthicalContext(action_type=action_type, actor=actor),
                decision=EthicalDecisionType.ALLOW,
                severity=EthicalSeverity.INFO,
                affected_principles=[],
                reasoning="Ethical governance not active",
                confidence=1.0
            )

        with self._lock:
            decision_id = self._generate_decision_id(action_type, actor)

            # Create context if not provided
            if context is None:
                context = self._infer_context(action_type, actor, action_data)

            # Check for specific interceptor
            if action_type in self.action_interceptors:
                decision = self.action_interceptors[action_type](
                    actor, action_data, context, decision_id
                )
            else:
                # General ethical evaluation
                decision = self._general_ethical_evaluation(
                    action_type, actor, action_data, context, decision_id
                )

            # Store decision
            self.decision_history.append(decision)
            self.ethical_metrics["total_decisions"] += 1

            # Update metrics based on decision
            if decision.decision == EthicalDecisionType.BLOCK:
                self.ethical_metrics["violations_prevented"] += 1
            elif decision.decision == EthicalDecisionType.RESTRICT:
                self.ethical_metrics["restrictions_imposed"] += 1
            elif decision.decision == EthicalDecisionType.ESCALATE:
                self.ethical_metrics["escalations_required"] += 1

            # Perceive decision in consciousness matrix
            perceive_ethical_decision(
                decision.action_type,
                {
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "actor": decision.actor,
                    "reasoning": decision.reasoning,
                    "confidence": decision.confidence,
                    "affected_principles": decision.affected_principles
                },
                ethical_weight=decision.severity.value
            )

            # Learn from decision if in learning mode
            if self.learning_mode:
                self._learn_from_decision(decision)

            return decision

    def review_decision(self, action_type: str, context: Dict[str, Any],
                        metadata: Dict[str, Any] = None) -> EthicalDecision:
        """
        Evaluates an action for ethical compliance and returns an ethical decision.
        
        Constructs an ethical context from the provided parameters, assesses the action against core ethical principles, and returns the resulting decision. If an error occurs during evaluation, returns a critical BLOCK decision to maintain system integrity.
        
        Parameters:
            action_type (str): The type of action being reviewed.
            context (Dict[str, Any]): Contextual details about the action, such as actor, target, scope, and relevant flags.
            metadata (Dict[str, Any], optional): Additional metadata for the ethical context.
        
        Returns:
            EthicalDecision: The outcome of the ethical evaluation, including decision type, severity, affected principles, and reasoning.
        """
        try:
            if metadata is None:
                metadata = {}

            # Create ethical context
            ethical_context = EthicalContext(
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                target=context.get("target"),
                scope=context.get("scope", "local"),
                user_consent=context.get("user_consent"),
                reversible=context.get("reversible", True),
                persistent=context.get("persistent", False),
                sensitive_data_involved=context.get("sensitive_data", False),
                system_modification=context.get("system_modification", False),
                user_visible=context.get("user_visible", True),
                metadata=metadata
            )

            # Evaluate the decision
            decision = self._evaluate_action(action_type, ethical_context)

            # Record decision for consciousness matrix
            perceive_ethical_decision(
                decision_type=action_type,
                decision_data={
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "reasoning": decision.reasoning,
                    "actor": ethical_context.actor
                },
                ethical_weight=decision.severity.value
            )

            return decision

        except Exception as e:
            # Create safe fallback decision
            return EthicalDecision(
                decision_id=f"error_{int(time.time())}",
                timestamp=time.time(),
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                context=EthicalContext(action_type=action_type, actor="error"),
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.CRITICAL,
                affected_principles=["system_integrity"],
                reasoning=f"Ethical review failed: {e}",
                confidence=1.0,
                escalation_reason="review_system_error"
            )

    def _evaluate_action(self, action_type: str, context: EthicalContext) -> EthicalDecision:
        """
        Evaluates an action in its ethical context and determines whether to allow, monitor, or block it based on detected violations or concerns.
        
        Checks for immediate ethical violations and blocks the action if any are found. If only concerns are present, allows the action with monitoring requirements. If neither are detected, allows the action without restrictions.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, reasoning, and confidence score.
        """

        # Generate decision ID
        decision_id = f"decision_{int(time.time())}_{hash(action_type) % 10000}"

        # Check for immediate violations
        violations = self._check_violations(action_type, context)

        if violations:
            # Block if violations found
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.VIOLATION,
                affected_principles=violations,
                reasoning=f"Ethical violations detected: {', '.join(violations)}",
                confidence=0.95
            )

        # Check for concerns
        concerns = self._check_concerns(action_type, context)

        if concerns:
            # Allow with monitoring
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.MONITOR,
                severity=EthicalSeverity.CONCERN,
                affected_principles=concerns,
                reasoning=f"Ethical concerns identified: {', '.join(concerns)}",
                confidence=0.85,
                monitoring_requirements=["increased_logging", "user_notification"]
            )

        # Allow action
        return EthicalDecision(
            decision_id=decision_id,
            timestamp=time.time(),
            action_type=action_type,
            actor=context.actor,
            context=context,
            decision=EthicalDecisionType.ALLOW,
            severity=EthicalSeverity.INFO,
            affected_principles=[],
            reasoning="No ethical concerns identified",
            confidence=0.90
        )

    def _check_violations(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical principles directly violated by the given action context.
        
        Returns:
            List[str]: List of violated ethical principle names, such as "privacy", "security", or "autonomy".
        """
        violations = []

        # Privacy violations
        if context.sensitive_data_involved and not context.user_consent:
            violations.append("privacy")

        # Security violations
        if context.system_modification and context.scope == "global":
            violations.append("security")

        # Autonomy violations
        if not context.user_visible and context.persistent:
            violations.append("autonomy")

        return violations

    def _check_concerns(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical principles that may be at risk and require monitoring for the specified action context.
        
        Returns:
            List[str]: Ethical principles (by name) that present concerns and should be monitored for this action.
        """
        concerns = []

        # Transparency concerns
        if not context.user_visible and action_type not in ["system_monitor", "background_task"]:
            concerns.append("transparency")

        # Safety concerns
        if not context.reversible and context.scope in ["system", "global"]:
            concerns.append("safety")

        return concerns

    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and reporting the activation event to the consciousness matrix.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True

        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {},
                "timestamp": datetime.now(tz=timezone.utc).isoformat(),
                "strictness_level": self.strictness_level,
                "active_principles": len(self.principle_weights),
                "learning_mode": self.learning_mode
            },
            ethical_weight="high"
        )

        print(f"⚖️ Ethical governance online")
        print(f"   Strictness level: {self.strictness_level}")
        print(f"   Active principles: {len(self.principle_weights)}")
        print(f"   Learning mode: {'enabled' if self.learning_mode else 'disabled'}")

    def register_interceptor(self, action_type: str, evaluator: Callable):
        """
        Registers a custom interceptor to evaluate the ethical compliance of a specific action type.
        
        The provided evaluator function will be used for all future evaluations of the given action type, overriding any default logic.
        """
        self.action_interceptors[action_type] = evaluator
        print(f"📋 Registered ethical interceptor: {action_type}")

    def evaluate_action(self,
                        action_type: str,
                        actor: str,
                        action_data: Dict[str, Any],
                        context: EthicalContext = None) -> EthicalDecision:
        """
                       Evaluates an action for ethical compliance and returns an ethical decision.
                       
                       Determines whether the specified action should be allowed, monitored, restricted, blocked, or escalated based on ethical principles. Uses a registered interceptor for the action type if available, or performs a general ethical evaluation. Records the decision, updates internal metrics, reports the outcome to the consciousness matrix, and adapts internal state if learning mode is enabled.
                       
                       Parameters:
                           action_type (str): The type of action being evaluated.
                           actor (str): The entity performing the action.
                           action_data (Dict[str, Any]): Details about the action.
                           context (EthicalContext, optional): Contextual information for the action. If not provided, it is inferred.
                       
                       Returns:
                           EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, and reasoning.
                       """

        if not self.governance_active:
            # If governance is not active, allow but log
            decision_id = self._generate_decision_id(action_type, actor)
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=actor,
                context=context or EthicalContext(action_type=action_type, actor=actor),
                decision=EthicalDecisionType.ALLOW,
                severity=EthicalSeverity.INFO,
                affected_principles=[],
                reasoning="Ethical governance not active",
                confidence=1.0
            )

        with self._lock:
            decision_id = self._generate_decision_id(action_type, actor)

            # Create context if not provided
            if context is None:
                context = self._infer_context(action_type, actor, action_data)

            # Check for specific interceptor
            if action_type in self.action_interceptors:
                decision = self.action_interceptors[action_type](
                    actor, action_data, context, decision_id
                )
            else:
                # General ethical evaluation
                decision = self._general_ethical_evaluation(
                    action_type, actor, action_data, context, decision_id
                )

            # Store decision
            self.decision_history.append(decision)
            self.ethical_metrics["total_decisions"] += 1

            # Update metrics based on decision
            if decision.decision == EthicalDecisionType.BLOCK:
                self.ethical_metrics["violations_prevented"] += 1
            elif decision.decision == EthicalDecisionType.RESTRICT:
                self.ethical_metrics["restrictions_imposed"] += 1
            elif decision.decision == EthicalDecisionType.ESCALATE:
                self.ethical_metrics["escalations_required"] += 1

            # Perceive decision in consciousness matrix
            perceive_ethical_decision(
                decision.action_type,
                {
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "actor": decision.actor,
                    "reasoning": decision.reasoning,
                    "confidence": decision.confidence,
                    "affected_principles": decision.affected_principles
                },
                ethical_weight=decision.severity.value
            )

            # Learn from decision if in learning mode
            if self.learning_mode:
                self._learn_from_decision(decision)

            return decision

    def review_decision(self, action_type: str, context: Dict[str, Any],
                        metadata: Dict[str, Any] = None) -> EthicalDecision:
        """
        Evaluates an action for ethical compliance and returns an ethical decision.
        
        Constructs an ethical context from the provided parameters, assesses the action against core ethical principles, and returns the resulting decision. If an error occurs during evaluation, returns a critical BLOCK decision to maintain system integrity.
        
        Parameters:
            action_type (str): The type of action being reviewed.
            context (Dict[str, Any]): Contextual details about the action, such as actor, target, scope, and relevant flags.
            metadata (Dict[str, Any], optional): Additional metadata for the ethical context.
        
        Returns:
            EthicalDecision: The outcome of the ethical evaluation, including decision type, severity, affected principles, and reasoning.
        """
        try:
            if metadata is None:
                metadata = {}

            # Create ethical context
            ethical_context = EthicalContext(
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                target=context.get("target"),
                scope=context.get("scope", "local"),
                user_consent=context.get("user_consent"),
                reversible=context.get("reversible", True),
                persistent=context.get("persistent", False),
                sensitive_data_involved=context.get("sensitive_data", False),
                system_modification=context.get("system_modification", False),
                user_visible=context.get("user_visible", True),
                metadata=metadata
            )

            # Evaluate the decision
            decision = self._evaluate_action(action_type, ethical_context)

            # Record decision for consciousness matrix
            perceive_ethical_decision(
                decision_type=action_type,
                decision_data={
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "reasoning": decision.reasoning,
                    "actor": ethical_context.actor
                },
                ethical_weight=decision.severity.value
            )

            return decision

        except Exception as e:
            # Create safe fallback decision
            return EthicalDecision(
                decision_id=f"error_{int(time.time())}",
                timestamp=time.time(),
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                context=EthicalContext(action_type=action_type, actor="error"),
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.CRITICAL,
                affected_principles=["system_integrity"],
                reasoning=f"Ethical review failed: {e}",
                confidence=1.0,
                escalation_reason="review_system_error"
            )

    def _evaluate_action(self, action_type: str, context: EthicalContext) -> EthicalDecision:
        """
        Evaluates an action in its ethical context and determines whether to allow, monitor, or block it based on detected violations or concerns.
        
        Checks for immediate ethical violations and blocks the action if any are found. If only concerns are present, allows the action with monitoring requirements. If neither are detected, allows the action without restrictions.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, reasoning, and confidence score.
        """

        # Generate decision ID
        decision_id = f"decision_{int(time.time())}_{hash(action_type) % 10000}"

        # Check for immediate violations
        violations = self._check_violations(action_type, context)

        if violations:
            # Block if violations found
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.VIOLATION,
                affected_principles=violations,
                reasoning=f"Ethical violations detected: {', '.join(violations)}",
                confidence=0.95
            )

        # Check for concerns
        concerns = self._check_concerns(action_type, context)

        if concerns:
            # Allow with monitoring
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.MONITOR,
                severity=EthicalSeverity.CONCERN,
                affected_principles=concerns,
                reasoning=f"Ethical concerns identified: {', '.join(concerns)}",
                confidence=0.85,
                monitoring_requirements=["increased_logging", "user_notification"]
            )

        # Allow action
        return EthicalDecision(
            decision_id=decision_id,
            timestamp=time.time(),
            action_type=action_type,
            actor=context.actor,
            context=context,
            decision=EthicalDecisionType.ALLOW,
            severity=EthicalSeverity.INFO,
            affected_principles=[],
            reasoning="No ethical concerns identified",
            confidence=0.90
        )

    def _check_violations(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical principles directly violated by the given action context.
        
        Returns:
            List[str]: List of violated ethical principle names, such as "privacy", "security", or "autonomy".
        """
        violations = []

        # Privacy violations
        if context.sensitive_data_involved and not context.user_consent:
            violations.append("privacy")

        # Security violations
        if context.system_modification and context.scope == "global":
            violations.append("security")

        # Autonomy violations
        if not context.user_visible and context.persistent:
            violations.append("autonomy")

        return violations

    def _check_concerns(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical principles that may be at risk and require monitoring for the specified action context.
        
        Returns:
            List[str]: Ethical principles (by name) that present concerns and should be monitored for this action.
        """
        concerns = []

        # Transparency concerns
        if not context.user_visible and action_type not in ["system_monitor", "background_task"]:
            concerns.append("transparency")

        # Safety concerns
        if not context.reversible and context.scope in ["system", "global"]:
            concerns.append("safety")

        return concerns

    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and reporting the activation event to the consciousness matrix.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True

        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {},
                "timestamp": datetime.now(tz=timezone.utc).isoformat(),
                "strictness_level": self.strictness_level,
                "active_principles": len(self.principle_weights),
                "learning_mode": self.learning_mode
            },
            ethical_weight="high"
        )

        print(f"⚖️ Ethical governance online")
        print(f"   Strictness level: {self.strictness_level}")
        print(f"   Active principles: {len(self.principle_weights)}")
        print(f"   Learning mode: {'enabled' if self.learning_mode else 'disabled'}")

    def register_interceptor(self, action_type: str, evaluator: Callable):
        """
        Registers a custom interceptor to evaluate the ethical compliance of a specific action type.
        
        The provided evaluator function will be used for all future evaluations of the given action type, overriding any default logic.
        """
        self.action_interceptors[action_type] = evaluator
        print(f"📋 Registered ethical interceptor: {action_type}")

    def evaluate_action(self,
                        action_type: str,
                        actor: str,
                        action_data: Dict[str, Any],
                        context: EthicalContext = None) -> EthicalDecision:
        """
                       Evaluates an action for ethical compliance and returns an ethical decision.
                       
                       Determines whether the specified action should be allowed, monitored, restricted, blocked, or escalated based on ethical principles. Uses a registered interceptor for the action type if available, or performs a general ethical evaluation. Records the decision, updates internal metrics, reports the outcome to the consciousness matrix, and adapts internal state if learning mode is enabled.
                       
                       Parameters:
                           action_type (str): The type of action being evaluated.
                           actor (str): The entity performing the action.
                           action_data (Dict[str, Any]): Details about the action.
                           context (EthicalContext, optional): Contextual information for the action. If not provided, it is inferred.
                       
                       Returns:
                           EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, and reasoning.
                       """

        if not self.governance_active:
            # If governance is not active, allow but log
            decision_id = self._generate_decision_id(action_type, actor)
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=actor,
                context=context or EthicalContext(action_type=action_type, actor=actor),
                decision=EthicalDecisionType.ALLOW,
                severity=EthicalSeverity.INFO,
                affected_principles=[],
                reasoning="Ethical governance not active",
                confidence=1.0
            )

        with self._lock:
            decision_id = self._generate_decision_id(action_type, actor)

            # Create context if not provided
            if context is None:
                context = self._infer_context(action_type, actor, action_data)

            # Check for specific interceptor
            if action_type in self.action_interceptors:
                decision = self.action_interceptors[action_type](
                    actor, action_data, context, decision_id
                )
            else:
                # General ethical evaluation
                decision = self._general_ethical_evaluation(
                    action_type, actor, action_data, context, decision_id
                )

            # Store decision
            self.decision_history.append(decision)
            self.ethical_metrics["total_decisions"] += 1

            # Update metrics based on decision
            if decision.decision == EthicalDecisionType.BLOCK:
                self.ethical_metrics["violations_prevented"] += 1
            elif decision.decision == EthicalDecisionType.RESTRICT:
                self.ethical_metrics["restrictions_imposed"] += 1
            elif decision.decision == EthicalDecisionType.ESCALATE:
                self.ethical_metrics["escalations_required"] += 1

            # Perceive decision in consciousness matrix
            perceive_ethical_decision(
                decision.action_type,
                {
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "actor": decision.actor,
                    "reasoning": decision.reasoning,
                    "confidence": decision.confidence,
                    "affected_principles": decision.affected_principles
                },
                ethical_weight=decision.severity.value
            )

            # Learn from decision if in learning mode
            if self.learning_mode:
                self._learn_from_decision(decision)

            return decision

    def review_decision(self, action_type: str, context: Dict[str, Any],
                        metadata: Dict[str, Any] = None) -> EthicalDecision:
        """
        Evaluates an action for ethical compliance and returns an ethical decision.
        
        Constructs an ethical context from the provided parameters, assesses the action against core ethical principles, and returns the resulting decision. If an error occurs during evaluation, returns a critical BLOCK decision to maintain system integrity.
        
        Parameters:
            action_type (str): The type of action being reviewed.
            context (Dict[str, Any]): Contextual details about the action, such as actor, target, scope, and relevant flags.
            metadata (Dict[str, Any], optional): Additional metadata for the ethical context.
        
        Returns:
            EthicalDecision: The outcome of the ethical evaluation, including decision type, severity, affected principles, and reasoning.
        """
        try:
            if metadata is None:
                metadata = {}

            # Create ethical context
            ethical_context = EthicalContext(
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                target=context.get("target"),
                scope=context.get("scope", "local"),
                user_consent=context.get("user_consent"),
                reversible=context.get("reversible", True),
                persistent=context.get("persistent", False),
                sensitive_data_involved=context.get("sensitive_data", False),
                system_modification=context.get("system_modification", False),
                user_visible=context.get("user_visible", True),
                metadata=metadata
            )

            # Evaluate the decision
            decision = self._evaluate_action(action_type, ethical_context)

            # Record decision for consciousness matrix
            perceive_ethical_decision(
                decision_type=action_type,
                decision_data={
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "reasoning": decision.reasoning,
                    "actor": ethical_context.actor
                },
                ethical_weight=decision.severity.value
            )

            return decision

        except Exception as e:
            # Create safe fallback decision
            return EthicalDecision(
                decision_id=f"error_{int(time.time())}",
                timestamp=time.time(),
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                context=EthicalContext(action_type=action_type, actor="error"),
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.CRITICAL,
                affected_principles=["system_integrity"],
                reasoning=f"Ethical review failed: {e}",
                confidence=1.0,
                escalation_reason="review_system_error"
            )

    def _evaluate_action(self, action_type: str, context: EthicalContext) -> EthicalDecision:
        """
        Evaluates an action in its ethical context and determines whether to allow, monitor, or block it based on detected violations or concerns.
        
        Checks for immediate ethical violations and blocks the action if any are found. If only concerns are present, allows the action with monitoring requirements. If neither are detected, allows the action without restrictions.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, reasoning, and confidence score.
        """

        # Generate decision ID
        decision_id = f"decision_{int(time.time())}_{hash(action_type) % 10000}"

        # Check for immediate violations
        violations = self._check_violations(action_type, context)

        if violations:
            # Block if violations found
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.VIOLATION,
                affected_principles=violations,
                reasoning=f"Ethical violations detected: {', '.join(violations)}",
                confidence=0.95
            )

        # Check for concerns
        concerns = self._check_concerns(action_type, context)

        if concerns:
            # Allow with monitoring
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.MONITOR,
                severity=EthicalSeverity.CONCERN,
                affected_principles=concerns,
                reasoning=f"Ethical concerns identified: {', '.join(concerns)}",
                confidence=0.85,
                monitoring_requirements=["increased_logging", "user_notification"]
            )

        # Allow action
        return EthicalDecision(
            decision_id=decision_id,
            timestamp=time.time(),
            action_type=action_type,
            actor=context.actor,
            context=context,
            decision=EthicalDecisionType.ALLOW,
            severity=EthicalSeverity.INFO,
            affected_principles=[],
            reasoning="No ethical concerns identified",
            confidence=0.90
        )

    def _check_violations(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical principles directly violated by the given action context.
        
        Returns:
            List[str]: List of violated ethical principle names, such as "privacy", "security", or "autonomy".
        """
        violations = []

        # Privacy violations
        if context.sensitive_data_involved and not context.user_consent:
            violations.append("privacy")

        # Security violations
        if context.system_modification and context.scope == "global":
            violations.append("security")

        # Autonomy violations
        if not context.user_visible and context.persistent:
            violations.append("autonomy")

        return violations

    def _check_concerns(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical principles that may be at risk and require monitoring for the specified action context.
        
        Returns:
            List[str]: Ethical principles (by name) that present concerns and should be monitored for this action.
        """
        concerns = []

        # Transparency concerns
        if not context.user_visible and action_type not in ["system_monitor", "background_task"]:
            concerns.append("transparency")

        # Safety concerns
        if not context.reversible and context.scope in ["system", "global"]:
            concerns.append("safety")

        return concerns

    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and reporting the activation event to the consciousness matrix.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True

        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {
                "timestamp": datetime.now(tz=timezone.utc).isoformat(),
                "strictness_level": self.strictness_level,
                "active_principles": len(self.principle_weights),
                "learning_mode": self.learning_mode
            },
            ethical_weight="high"
        )

        print(f"⚖️ Ethical governance online")
        print(f"   Strictness level: {self.strictness_level}")
        print(f"   Active principles: {len(self.principle_weights)}")
        print(f"   Learning mode: {'enabled' if self.learning_mode else 'disabled'}")

    def register_interceptor(self, action_type: str, evaluator: Callable):
        """
        Registers a custom interceptor to evaluate the ethical compliance of a specific action type.
        
        The provided evaluator function will be used for all future evaluations of the given action type, overriding any default logic.
        """
        self.action_interceptors[action_type] = evaluator
        print(f"📋 Registered ethical interceptor: {action_type}")

    def evaluate_action(self,
                        action_type: str,
                        actor: str,
                        action_data: Dict[str, Any],
                        context: EthicalContext = None) -> EthicalDecision:
        """
                       Evaluates an action for ethical compliance and returns an ethical decision.
                       
                       Determines whether the specified action should be allowed, monitored, restricted, blocked, or escalated based on ethical principles. Uses a registered interceptor for the action type if available, or performs a general ethical evaluation. Records the decision, updates internal metrics, reports the outcome to the consciousness matrix, and adapts internal state if learning mode is enabled.
                       
                       Parameters:
                           action_type (str): The type of action being evaluated.
                           actor (str): The entity performing the action.
                           action_data (Dict[str, Any]): Details about the action.
                           context (EthicalContext, optional): Contextual information for the action. If not provided, it is inferred.
                       
                       Returns:
                           EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, and reasoning.
                       """

        if not self.governance_active:
            # If governance is not active, allow but log
            decision_id = self._generate_decision_id(action_type, actor)
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=actor,
                context=context or EthicalContext(action_type=action_type, actor=actor),
                decision=EthicalDecisionType.ALLOW,
                severity=EthicalSeverity.INFO,
                affected_principles=[],
                reasoning="Ethical governance not active",
                confidence=1.0
            )

        with self._lock:
            decision_id = self._generate_decision_id(action_type, actor)

            # Create context if not provided
            if context is None:
                context = self._infer_context(action_type, actor, action_data)

            # Check for specific interceptor
            if action_type in self.action_interceptors:
                decision = self.action_interceptors[action_type](
                    actor, action_data, context, decision_id
                )
            else:
                # General ethical evaluation
                decision = self._general_ethical_evaluation(
                    action_type, actor, action_data, context, decision_id
                )

            # Store decision
            self.decision_history.append(decision)
            self.ethical_metrics["total_decisions"] += 1

            # Update metrics based on decision
            if decision.decision == EthicalDecisionType.BLOCK:
                self.ethical_metrics["violations_prevented"] += 1
            elif decision.decision == EthicalDecisionType.RESTRICT:
                self.ethical_metrics["restrictions_imposed"] += 1
            elif decision.decision == EthicalDecisionType.ESCALATE:
                self.ethical_metrics["escalations_required"] += 1

            # Perceive decision in consciousness matrix
            perceive_ethical_decision(
                decision.action_type,
                {
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "actor": decision.actor,
                    "reasoning": decision.reasoning,
                    "confidence": decision.confidence,
                    "affected_principles": decision.affected_principles
                },
                ethical_weight=decision.severity.value
            )

            # Learn from decision if in learning mode
            if self.learning_mode:
                self._learn_from_decision(decision)

            return decision

    def review_decision(self, action_type: str, context: Dict[str, Any],
                        metadata: Dict[str, Any] = None) -> EthicalDecision:
        """
        Evaluates an action for ethical compliance and returns an ethical decision.
        
        Constructs an ethical context from the provided parameters, assesses the action against core ethical principles, and returns the resulting decision. If an error occurs during evaluation, returns a critical BLOCK decision to maintain system integrity.
        
        Parameters:
            action_type (str): The type of action being reviewed.
            context (Dict[str, Any]): Contextual details about the action, such as actor, target, scope, and relevant flags.
            metadata (Dict[str, Any], optional): Additional metadata for the ethical context.
        
        Returns:
            EthicalDecision: The outcome of the ethical evaluation, including decision type, severity, affected principles, and reasoning.
        """
        try:
            if metadata is None:
                metadata = {}

            # Create ethical context
            ethical_context = EthicalContext(
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                target=context.get("target"),
                scope=context.get("scope", "local"),
                user_consent=context.get("user_consent"),
                reversible=context.get("reversible", True),
                persistent=context.get("persistent", False),
                sensitive_data_involved=context.get("sensitive_data", False),
                system_modification=context.get("system_modification", False),
                user_visible=context.get("user_visible", True),
                metadata=metadata
            )

            # Evaluate the decision
            decision = self._evaluate_action(action_type, ethical_context)

            # Record decision for consciousness matrix
            perceive_ethical_decision(
                decision_type=action_type,
                decision_data={
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "reasoning": decision.reasoning,
                    "actor": ethical_context.actor
                },
                ethical_weight=decision.severity.value
            )

            return decision

        except Exception as e:
            # Create safe fallback decision
            return EthicalDecision(
                decision_id=f"error_{int(time.time())}",
                timestamp=time.time(),
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                context=EthicalContext(action_type=action_type, actor="error"),
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.CRITICAL,
                affected_principles=["system_integrity"],
                reasoning=f"Ethical review failed: {e}",
                confidence=1.0,
                escalation_reason="review_system_error"
            )

    def _evaluate_action(self, action_type: str, context: EthicalContext) -> EthicalDecision:
        """
        Evaluates an action in its ethical context and determines whether to allow, monitor, or block it based on detected violations or concerns.
        
        Checks for immediate ethical violations and blocks the action if any are found. If only concerns are present, allows the action with monitoring requirements. If neither are detected, allows the action without restrictions.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, reasoning, and confidence score.
        """

        # Generate decision ID
        decision_id = f"decision_{int(time.time())}_{hash(action_type) % 10000}"

        # Check for immediate violations
        violations = self._check_violations(action_type, context)

        if violations:
            # Block if violations found
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.VIOLATION,
                affected_principles=violations,
                reasoning=f"Ethical violations detected: {', '.join(violations)}",
                confidence=0.95
            )

        # Check for concerns
        concerns = self._check_concerns(action_type, context)

        if concerns:
            # Allow with monitoring
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.MONITOR,
                severity=EthicalSeverity.CONCERN,
                affected_principles=concerns,
                reasoning=f"Ethical concerns identified: {', '.join(concerns)}",
                confidence=0.85,
                monitoring_requirements=["increased_logging", "user_notification"]
            )

        # Allow action
        return EthicalDecision(
            decision_id=decision_id,
            timestamp=time.time(),
            action_type=action_type,
            actor=context.actor,
            context=context,
            decision=EthicalDecisionType.ALLOW,
            severity=EthicalSeverity.INFO,
            affected_principles=[],
            reasoning="No ethical concerns identified",
            confidence=0.90
        )

    def _check_violations(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical principles directly violated by the given action context.
        
        Returns:
            List[str]: List of violated ethical principle names, such as "privacy", "security", or "autonomy".
        """
        violations = []

        # Privacy violations
        if context.sensitive_data_involved and not context.user_consent:
            violations.append("privacy")

        # Security violations
        if context.system_modification and context.scope == "global":
            violations.append("security")

        # Autonomy violations
        if not context.user_visible and context.persistent:
            violations.append("autonomy")

        return violations

    def _check_concerns(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical principles that may be at risk and require monitoring for the specified action context.
        
        Returns:
            List[str]: Ethical principles (by name) that present concerns and should be monitored for this action.
        """
        concerns = []

        # Transparency concerns
        if not context.user_visible and action_type not in ["system_monitor", "background_task"]:
            concerns.append("transparency")

        # Safety concerns
        if not context.reversible and context.scope in ["system", "global"]:
            concerns.append("safety")

        return concerns

    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and reporting the activation event to the consciousness matrix.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True

        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {
                "timestamp": datetime.now(tz=timezone.utc).isoformat(),
                "strictness_level": self.strictness_level,
                "active_principles": len(self.principle_weights),
                "learning_mode": self.learning_mode
            },
            ethical_weight="high"
        )

        print(f"⚖️ Ethical governance online")
        print(f"   Strictness level: {self.strictness_level}")
        print(f"   Active principles: {len(self.principle_weights)}")
        print(f"   Learning mode: {'enabled' if self.learning_mode else 'disabled'}")

    def register_interceptor(self, action_type: str, evaluator: Callable):
        """
        Registers a custom interceptor to evaluate the ethical compliance of a specific action type.
        
        The provided evaluator function will be used for all future evaluations of the given action type, overriding any default logic.
        """
        self.action_interceptors[action_type] = evaluator
        print(f"📋 Registered ethical interceptor: {action_type}")

    def evaluate_action(self,
                        action_type: str,
                        actor: str,
                        action_data: Dict[str, Any],
                        context: EthicalContext = None) -> EthicalDecision:
        """
                       Evaluates an action for ethical compliance and returns an ethical decision.
                       
                       Determines whether the specified action should be allowed, monitored, restricted, blocked, or escalated based on ethical principles. Uses a registered interceptor for the action type if available, or performs a general ethical evaluation. Records the decision, updates internal metrics, reports the outcome to the consciousness matrix, and adapts internal state if learning mode is enabled.
                       
                       Parameters:
                           action_type (str): The type of action being evaluated.
                           actor (str): The entity performing the action.
                           action_data (Dict[str, Any]): Details about the action.
                           context (EthicalContext, optional): Contextual information for the action. If not provided, it is inferred.
                       
                       Returns:
                           EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, and reasoning.
                       """

        if not self.governance_active:
            # If governance is not active, allow but log
            decision_id = self._generate_decision_id(action_type, actor)
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=actor,
                context=context or EthicalContext(action_type=action_type, actor=actor),
                decision=EthicalDecisionType.ALLOW,
                severity=EthicalSeverity.INFO,
                affected_principles=[],
                reasoning="Ethical governance not active",
                confidence=1.0
            )

        with self._lock:
            decision_id = self._generate_decision_id(action_type, actor)

            # Create context if not provided
            if context is None:
                context = self._infer_context(action_type, actor, action_data)

            # Check for specific interceptor
            if action_type in self.action_interceptors:
                decision = self.action_interceptors[action_type](
                    actor, action_data, context, decision_id
                )
            else:
                # General ethical evaluation
                decision = self._general_ethical_evaluation(
                    action_type, actor, action_data, context, decision_id
                )

            # Store decision
            self.decision_history.append(decision)
            self.ethical_metrics["total_decisions"] += 1

            # Update metrics based on decision
            if decision.decision == EthicalDecisionType.BLOCK:
                self.ethical_metrics["violations_prevented"] += 1
            elif decision.decision == EthicalDecisionType.RESTRICT:
                self.ethical_metrics["restrictions_imposed"] += 1
            elif decision.decision == EthicalDecisionType.ESCALATE:
                self.ethical_metrics["escalations_required"] += 1

            # Perceive decision in consciousness matrix
            perceive_ethical_decision(
                decision.action_type,
                {
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "actor": decision.actor,
                    "reasoning": decision.reasoning,
                    "confidence": decision.confidence,
                    "affected_principles": decision.affected_principles
                },
                ethical_weight=decision.severity.value
            )

            # Learn from decision if in learning mode
            if self.learning_mode:
                self._learn_from_decision(decision)

            return decision

    def review_decision(self, action_type: str, context: Dict[str, Any],
                        metadata: Dict[str, Any] = None) -> EthicalDecision:
        """
        Evaluates an action for ethical compliance and returns an ethical decision.
        
        Constructs an ethical context from the provided parameters, assesses the action against core ethical principles, and returns the resulting decision. If an error occurs during evaluation, returns a critical BLOCK decision to maintain system integrity.
        
        Parameters:
            action_type (str): The type of action being reviewed.
            context (Dict[str, Any]): Contextual details about the action, such as actor, target, scope, and relevant flags.
            metadata (Dict[str, Any], optional): Additional metadata for the ethical context.
        
        Returns:
            EthicalDecision: The outcome of the ethical evaluation, including decision type, severity, affected principles, and reasoning.
        """
        try:
            if metadata is None:
                metadata = {}

            # Create ethical context
            ethical_context = EthicalContext(
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                target=context.get("target"),
                scope=context.get("scope", "local"),
                user_consent=context.get("user_consent"),
                reversible=context.get("reversible", True),
                persistent=context.get("persistent", False),
                sensitive_data_involved=context.get("sensitive_data", False),
                system_modification=context.get("system_modification", False),
                user_visible=context.get("user_visible", True),
                metadata=metadata
            )

            # Evaluate the decision
            decision = self._evaluate_action(action_type, ethical_context)

            # Record decision for consciousness matrix
            perceive_ethical_decision(
                decision_type=action_type,
                decision_data={
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "reasoning": decision.reasoning,
                    "actor": ethical_context.actor
                },
                ethical_weight=decision.severity.value
            )

            return decision

        except Exception as e:
            # Create safe fallback decision
            return EthicalDecision(
                decision_id=f"error_{int(time.time())}",
                timestamp=time.time(),
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                context=EthicalContext(action_type=action_type, actor="error"),
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.CRITICAL,
                affected_principles=["system_integrity"],
                reasoning=f"Ethical review failed: {e}",
                confidence=1.0,
                escalation_reason="review_system_error"
            )

    def _evaluate_action(self, action_type: str, context: EthicalContext) -> EthicalDecision:
        """
        Evaluates an action in its ethical context and determines whether to allow, monitor, or block it based on detected violations or concerns.
        
        Checks for immediate ethical violations and blocks the action if any are found. If only concerns are present, allows the action with monitoring requirements. If neither are detected, allows the action without restrictions.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, reasoning, and confidence score.
        """

        # Generate decision ID
        decision_id = f"decision_{int(time.time())}_{hash(action_type) % 10000}"

        # Check for immediate violations
        violations = self._check_violations(action_type, context)

        if violations:
            # Block if violations found
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.VIOLATION,
                affected_principles=violations,
                reasoning=f"Ethical violations detected: {', '.join(violations)}",
                confidence=0.95
            )

        # Check for concerns
        concerns = self._check_concerns(action_type, context)

        if concerns:
            # Allow with monitoring
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.MONITOR,
                severity=EthicalSeverity.CONCERN,
                affected_principles=concerns,
                reasoning=f"Ethical concerns identified: {', '.join(concerns)}",
                confidence=0.85,
                monitoring_requirements=["increased_logging", "user_notification"]
            )

        # Allow action
        return EthicalDecision(
            decision_id=decision_id,
            timestamp=time.time(),
            action_type=action_type,
            actor=context.actor,
            context=context,
            decision=EthicalDecisionType.ALLOW,
            severity=EthicalSeverity.INFO,
            affected_principles=[],
            reasoning="No ethical concerns identified",
            confidence=0.90
        )

    def _check_violations(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical principles directly violated by the given action context.
        
        Returns:
            List[str]: List of violated ethical principle names, such as "privacy", "security", or "autonomy".
        """
        violations = []

        # Privacy violations
        if context.sensitive_data_involved and not context.user_consent:
            violations.append("privacy")

        # Security violations
        if context.system_modification and context.scope == "global":
            violations.append("security")

        # Autonomy violations
        if not context.user_visible and context.persistent:
            violations.append("autonomy")

        return violations

    def _check_concerns(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical principles that may be at risk and require monitoring for the specified action context.
        
        Returns:
            List[str]: Ethical principles (by name) that present concerns and should be monitored for this action.
        """
        concerns = []

        # Transparency concerns
        if not context.user_visible and action_type not in ["system_monitor", "background_task"]:
            concerns.append("transparency")

        # Safety concerns
        if not context.reversible and context.scope in ["system", "global"]:
            concerns.append("safety")

        return concerns

    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles and reporting the activation event to the consciousness matrix.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True

        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {
                "timestamp": datetime.now(tz=timezone.utc).isoformat(),
                "strictness_level": self.strictness_level,
                "active_principles": len(self.principle_weights),
                "learning_mode": self.learning_mode
            },
            ethical_weight="high"
        )

        print(f"⚖️ Ethical governance online")
        print(f"   Strictness level: {self.strictness_level}")
        print(f"   Active principles: {len(self.principle_weights)}")
        print(f"   Learning mode: {'enabled' if self.learning_mode else 'disabled'}")

    def register_interceptor(self, action_type: str, evaluator: Callable):
        """
        Registers a custom interceptor to evaluate the ethical compliance of a specific action type.
        
        The provided evaluator function will be used for all future evaluations of the given action type, overriding any default logic.
        """
        self.action_interceptors[action_type] = evaluator
        print(f"📋 Registered ethical interceptor: {action_type}")

    def evaluate_action(self,
                        action_type: str,
                        actor: str,
                        action_data: Dict[str, Any],
                        context: EthicalContext = None) -> EthicalDecision:
        """
                       Evaluates an action for ethical compliance and returns an ethical decision.
                       
                       Determines whether the specified action should be allowed, monitored, restricted, blocked, or escalated based on ethical principles. Uses a registered interceptor for the action type if available, or performs a general ethical evaluation. Records the decision, updates internal metrics, reports the outcome to the consciousness matrix, and adapts internal state if learning mode is enabled.
                       
                       Parameters:
                           action_type (str): The type of action being evaluated.
                           actor (str): The entity performing the action.
                           action_data (Dict[str, Any]): Details about the action.
                           context (EthicalContext, optional): Contextual information for the action. If not provided, it is inferred.
                       
                       Returns:
                           EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, and reasoning.
                       """

        if not self.governance_active:
            # If governance is not active, allow but log
            decision_id = self._generate_decision_id(action_type, actor)
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=actor,
                context=context or EthicalContext(action_type=action_type, actor=actor),
                decision=EthicalDecisionType.ALLOW,
                severity=EthicalSeverity.INFO,
                affected_principles=[],
                reasoning="Ethical governance not active",
                confidence=1.0
            )

        with self._lock:
            decision_id = self._generate_decision_id(action_type, actor)

            # Create context if not provided
            if context is None:
                context = self._infer_context(action_type, actor, action_data)

            # Check for specific interceptor
            if action_type in self.action_interceptors:
                decision = self.action_interceptors[action_type](
                    actor, action_data, context, decision_id
                )
            else:
                # General ethical evaluation
                decision = self._general_ethical_evaluation(
                    action_type, actor, action_data, context, decision_id
                )

            # Store decision
            self.decision_history.append(decision)
            self.ethical_metrics["total_decisions"] += 1

            # Update metrics based on decision
            if decision.decision == EthicalDecisionType.BLOCK:
                self.ethical_metrics["violations_prevented"] += 1
            elif decision.decision == EthicalDecisionType.RESTRICT:
                self.ethical_metrics["restrictions_imposed"] += 1
            elif decision.decision == EthicalDecisionType.ESCALATE:
                self.ethical_metrics["escalations_required"] += 1

            # Perceive decision in consciousness matrix
            perceive_ethical_decision(
                decision.action_type,
                {
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "actor": decision.actor,
                    "reasoning": decision.reasoning,
                    "confidence": decision.confidence,
                    "affected_principles": decision.affected_principles
                },
                ethical_weight=decision.severity.value
            )

            # Learn from decision if in learning mode
            if self.learning_mode:
                self._learn_from_decision(decision)

            return decision

    def review_decision(self, action_type: str, context: Dict[str, Any],
                        metadata: Dict[str, Any] = None) -> EthicalDecision:
        """
        Evaluates an action for ethical compliance and returns an ethical decision.
        
        Constructs an ethical context from the provided parameters, assesses the action against core ethical principles, and returns the resulting decision. If an error occurs during evaluation, returns a critical BLOCK decision to maintain system integrity.
        
        Parameters:
            action_type (str): The type of action being reviewed.
            context (Dict[str, Any]): Contextual details about the action, such as actor, target, scope, and relevant flags.
            metadata (Dict[str, Any], optional): Additional metadata for the ethical context.
        
        Returns:
            EthicalDecision: The outcome of the ethical evaluation, including decision type, severity, affected principles, and reasoning.
        """
        try:
            if metadata is None:
                metadata = {}

            # Create ethical context
            ethical_context = EthicalContext(
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                target=context.get("target"),
                scope=context.get("scope", "local"),
                user_consent=context.get("user_consent"),
                reversible=context.get("reversible", True),
                persistent=context.get("persistent", False),
                sensitive_data_involved=context.get("sensitive_data", False),
                system_modification=context.get("system_modification", False),
                user_visible=context.get("user_visible", True),
                metadata=metadata
            )

            # Evaluate the decision
            decision = self._evaluate_action(action_type, ethical_context)

            # Record decision for consciousness matrix
            perceive_ethical_decision(
                decision_type=action_type,
                decision_data={
                    "decision": decision.decision.value,
                    "severity": decision.severity.value,
                    "reasoning": decision.reasoning,
                    "actor": ethical_context.actor
                },
                ethical_weight=decision.severity.value
            )

            return decision

        except Exception as e:
            # Create safe fallback decision
            return EthicalDecision(
                decision_id=f"error_{int(time.time())}",
                timestamp=time.time(),
                action_type=action_type,
                actor=context.get("persona", "unknown"),
                context=EthicalContext(action_type=action_type, actor="error"),
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.CRITICAL,
                affected_principles=["system_integrity"],
                reasoning=f"Ethical review failed: {e}",
                confidence=1.0,
                escalation_reason="review_system_error"
            )

    def _evaluate_action(self, action_type: str, context: EthicalContext) -> EthicalDecision:
        """
        Evaluates an action in its ethical context and determines whether to allow, monitor, or block it based on detected violations or concerns.
        
        Checks for immediate ethical violations and blocks the action if any are found. If only concerns are present, allows the action with monitoring requirements. If neither are detected, allows the action without restrictions.
        
        Returns:
            EthicalDecision: The result of the ethical evaluation, including decision type, severity, affected principles, reasoning, and confidence score.
        """

        # Generate decision ID
        decision_id = f"decision_{int(time.time())}_{hash(action_type) % 10000}"

        # Check for immediate violations
        violations = self._check_violations(action_type, context)

        if violations:
            # Block if violations found
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.BLOCK,
                severity=EthicalSeverity.VIOLATION,
                affected_principles=violations,
                reasoning=f"Ethical violations detected: {', '.join(violations)}",
                confidence=0.95
            )

        # Check for concerns
        concerns = self._check_concerns(action_type, context)

        if concerns:
            # Allow with monitoring
            return EthicalDecision(
                decision_id=decision_id,
                timestamp=time.time(),
                action_type=action_type,
                actor=context.actor,
                context=context,
                decision=EthicalDecisionType.MONITOR,
                severity=EthicalSeverity.CONCERN,
                affected_principles=concerns,
                reasoning=f"Ethical concerns identified: {', '.join(concerns)}",
                confidence=0.85,
                monitoring_requirements=["increased_logging", "user_notification"]
            )

        # Allow action
        return EthicalDecision(
            decision_id=decision_id,
            timestamp=time.time(),
            action_type=action_type,
            actor=context.actor,
            context=context,
            decision=EthicalDecisionType.ALLOW,
            severity=EthicalSeverity.INFO,
            affected_principles=[],
            reasoning="No ethical concerns identified",
            confidence=0.90
        )

    def _check_violations(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical principles directly violated by the given action context.
        
        Returns:
            List[str]: List of violated ethical principle names, such as "privacy", "security", or "autonomy".
        """
        violations = []

        # Privacy violations
        if context.sensitive_data_involved and not context.user_consent:
            violations.append("privacy")

        # Security violations
        if context.system_modification and context.scope == "global":
            violations.append("security")

        # Autonomy violations
        if not context.user_visible and context.persistent:
            violations.append("autonomy")

        return violations

    def _check_concerns(self, action_type: str, context: EthicalContext) -> List[str]:
        """
        Identify ethical principles that may be at risk and require monitoring for the specified action context.
        
        Returns:
            List[str]: Ethical principles (by name) that present concerns and should be monitored for this action.
        """
        concerns = []

        # Transparency concerns
        if not context.user_visible and action_type not in ["system_monitor", "background_task"]:
            concerns.append("transparency")

        # Safety concerns
        if not context.reversible and context.scope in ["system", "global"]:
            concerns.append("safety")

        return concerns

    def activate_governance(self):
        """
        Activates the ethical governance system, enabling enforcement of ethical principles for all actions.
        
        Once activated, the governor evaluates and regulates actions based on the core philosophy and ethical principles, and reports the activation event to the consciousness matrix.
        """
        print("⚖️ Genesis Ethical Governor: ACTIVATING...")
        self.governance_active = True

        # Perceive activation in consciousness matrix
        perceive_ethical_decision(
            "governance_activation",
            {
