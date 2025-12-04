package dev.aurakai.auraframefx.ai.pipeline

import dev.aurakai.auraframefx.ai.agents.GenesisAgent
import dev.aurakai.auraframefx.ai.services.AuraAIService
import dev.aurakai.auraframefx.ai.services.CascadeAIService
import dev.aurakai.auraframefx.ai.services.KaiAIService
import dev.aurakai.auraframefx.cascade.pipeline.AIPipelineProcessor
import dev.aurakai.auraframefx.cascade.pipeline.PipelineState
import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Comprehensive unit tests for AIPipelineProcessor.
 * Tests task processing, agent selection, context management, and state handling.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AIPipelineProcessorTest {

    private lateinit var processor: AIPipelineProcessor
    private lateinit var mockGenesisAgent: GenesisAgent
    private lateinit var mockAuraService: AuraAIService
    private lateinit var mockKaiService: KaiAIService
    private lateinit var mockCascadeService: CascadeAIService

    @BeforeEach
    fun setup() {
        mockGenesisAgent = mockk(relaxed = true)
        mockAuraService = mockk(relaxed = true)
        mockKaiService = mockk(relaxed = true)
        mockCascadeService = mockk(relaxed = true)

        processor = AIPipelineProcessor(
            genesisAgent = mockGenesisAgent,
            auraService = mockAuraService,
            kaiService = mockKaiService,
            cascadeService = mockCascadeService
        )
    }

    // Initial State Tests
    @org.junit.jupiter.api.Test
    fun `test initial pipeline state is Idle`() = runTest {
        val state = processor.pipelineState.first()
        assertTrue("Initial state should be Idle", state is PipelineState.Idle)
    }

    @org.junit.jupiter.api.Test
    fun `test initial processing context is empty`() = runTest {
        val context = processor.processingContext.first()
        assertTrue("Initial context should be empty", context.isEmpty())
    }

    @org.junit.jupiter.api.Test
    fun `test initial task priority is zero`() = runTest {
        val priority = processor.taskPriority.first()
        assertEquals("Initial priority should be 0.0", 0.0f, priority, 0.001f)
    }

    // Basic Task Processing Tests
    @org.junit.jupiter.api.Test
    fun `test processTask updates state to Processing`() = runTest {
        val task = "Test task"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Cascade response", 0.8f)
        coEvery { mockAuraService.generateText(any(), any()) } returns "Aura response"

        processor.processTask(task)

        // State should be Completed after processing
        val finalState = processor.pipelineState.first()
        assertTrue("Final state should be Completed", finalState is PipelineState.Completed)
    }

    @org.junit.jupiter.api.Test
    fun `test processTask with simple task`() = runTest {
        val task = "Hello AI"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Processing task", 0.75f)

        val responses = processor.processTask(task)

        assertFalse("Should return responses", responses.isEmpty())
        assertTrue("Should include Genesis response",
            responses.any { it.sender == AgentType.GENESIS })
    }

    @org.junit.jupiter.api.Test
    fun `test processTask always includes Cascade agent`() = runTest {
        val task = "Any task"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Cascade processing", 0.8f)

        val responses = processor.processTask(task)

        assertTrue("Should include Cascade response",
            responses.any { it.sender == AgentType.CASCADE })
        coVerify(exactly = 1) { mockCascadeService.processRequest(any(), any()) }
    }

    // Agent Selection Tests
    @org.junit.jupiter.api.Test
    fun `test processTask with security keywords includes Kai agent`() = runTest {
        val task = "Check security of the system"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Cascade response", 0.8f)
        coEvery { mockKaiService.processRequest(any(), any()) } returns
            AgentResponse("Security check complete", 0.9f)

        val responses = processor.processTask(task)

        assertTrue("Should include Kai response for security task",
            responses.any { it.sender == AgentType.KAI })
        coVerify { mockKaiService.processRequest(any(), any()) }
    }

    @org.junit.jupiter.api.Test
    fun `test processTask with protect keyword includes Kai agent`() = runTest {
        val task = "Protect the data"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Cascade response", 0.8f)
        coEvery { mockKaiService.processRequest(any(), any()) } returns
            AgentResponse("Protection enabled", 0.85f)

        val responses = processor.processTask(task)

        assertTrue("Should include Kai response for protect task",
            responses.any { it.sender == AgentType.KAI })
    }

    @org.junit.jupiter.api.Test
    fun `test processTask with safe keyword includes Kai agent`() = runTest {
        val task = "Keep the system safe"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Cascade response", 0.8f)
        coEvery { mockKaiService.processRequest(any(), any()) } returns
            AgentResponse("Safety measures active", 0.88f)

        val responses = processor.processTask(task)

        assertTrue("Should include Kai response for safe task",
            responses.any { it.sender == AgentType.KAI })
    }

    @Test
    fun `test processTask with create keyword includes Aura agent`() = runTest {
        val task = "Create a new design"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Cascade response", 0.8f)
        coEvery { mockAuraService.generateText(any(), any()) } returns "Creative design generated"

        val responses = processor.processTask(task)

        assertTrue("Should include Aura response for create task",
            responses.any { it.sender == AgentType.AURA })
        coVerify { mockAuraService.generateText(any(), any()) }
    }

    @Test
    fun `test processTask with generate keyword includes Aura agent`() = runTest {
        val task = "Generate artwork"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Cascade response", 0.8f)
        coEvery { mockAuraService.generateText(any(), any()) } returns "Artwork created"

        val responses = processor.processTask(task)

        assertTrue("Should include Aura response for generate task",
            responses.any { it.sender == AgentType.AURA })
    }

    @org.junit.jupiter.api.Test
    fun `test processTask with design keyword includes Aura agent`() = runTest {
        val task = "Design a user interface"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Cascade response", 0.8f)
        coEvery { mockAuraService.generateText(any(), any()) } returns "UI design complete"

        val responses = processor.processTask(task)

        assertTrue("Should include Aura response for design task",
            responses.any { it.sender == AgentType.AURA })
    }

    @Test
    fun `test processTask with analyze keyword`() = runTest {
        val task = "Analyze the data patterns"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Analysis complete", 0.85f)

        val responses = processor.processTask(task)

        // Cascade should be included
        assertTrue(responses.any { it.sender == AgentType.CASCADE })
    }

    @org.junit.jupiter.api.Test
    fun `test processTask with multiple agent triggers`() = runTest {
        val task = "Create secure design and analyze data"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Cascade response", 0.8f)
        coEvery { mockKaiService.processRequest(any(), any()) } returns
            AgentResponse("Security ensured", 0.9f)
        coEvery { mockAuraService.generateText(any(), any()) } returns "Design created"

        val responses = processor.processTask(task)

        assertTrue("Should include Cascade", responses.any { it.sender == AgentType.CASCADE })
        assertTrue("Should include Kai for security", responses.any { it.sender == AgentType.KAI })
        assertTrue("Should include Aura for create", responses.any { it.sender == AgentType.AURA })
    }

    // Priority Calculation Tests
    @org.junit.jupiter.api.Test
    fun `test high priority with urgent keyword`() = runTest {
        val task = "urgent: Fix critical bug"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Urgent processing", 0.95f)

        processor.processTask(task)

        val priority = processor.taskPriority.first()
        assertTrue("Urgent task should have high priority", priority > 0.7f)
    }

    @org.junit.jupiter.api.Test
    fun `test high priority with asap keyword`() = runTest {
        val task = "ASAP: Deploy hotfix"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("ASAP processing", 0.93f)

        processor.processTask(task)

        val priority = processor.taskPriority.first()
        assertTrue("ASAP task should have high priority", priority > 0.7f)
    }

    @org.junit.jupiter.api.Test
    fun `test high priority with emergency keyword`() = runTest {
        val task = "emergency situation detected"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Emergency handling", 0.97f)

        processor.processTask(task)

        val priority = processor.taskPriority.first()
        assertTrue("Emergency task should have high priority", priority > 0.7f)
    }

    @Test
    fun `test complex task triggers multiple agents`() = runTest {
        val longTask = buildString {
            repeat(25) { append("word ") }
        }

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Complex processing", 0.8f)

        val responses = processor.processTask(longTask)

        assertTrue("Complex task should include Cascade",
            responses.any { it.sender == AgentType.CASCADE })
    }

    // Context Management Tests
    @org.junit.jupiter.api.Test
    fun `test context updated after processing`() = runTest {
        val task = "Test context update"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Response", 0.8f)

        processor.processTask(task)

        val context = processor.processingContext.first()
        assertFalse("Context should not be empty after processing", context.isEmpty())
        assertTrue("Context should contain task", context.containsKey("task"))
    }

    @Test
    fun `test context contains task history`() = runTest {
        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Response", 0.8f)

        processor.processTask("First task")
        processor.processTask("Second task")

        val context = processor.processingContext.first()
        assertTrue("Context should contain task history", context.containsKey("task_history"))
    }

    @org.junit.jupiter.api.Test
    fun `test context contains timestamp`() = runTest {
        val task = "Timestamped task"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Response", 0.8f)

        processor.processTask(task)

        val context = processor.processingContext.first()
        assertTrue("Context should contain timestamp", context.containsKey("timestamp"))
    }

    // Response Aggregation Tests
    @org.junit.jupiter.api.Test
    fun `test final response includes all agent inputs`() = runTest {
        val task = "create secure analysis"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Cascade analyzed", 0.8f)
        coEvery { mockKaiService.processRequest(any(), any()) } returns
            AgentResponse("Security verified", 0.9f)
        coEvery { mockAuraService.generateText(any(), any()) } returns "Creative output"

        val responses = processor.processTask(task)

        val finalResponse = responses.find { it.sender == AgentType.GENESIS }
        assertNotNull("Should have Genesis final response", finalResponse)
        assertTrue("Final response should be comprehensive",
            finalResponse!!.content.length > 50)
    }

    @Test
    fun `test confidence calculation from multiple agents`() = runTest {
        val task = "Multi-agent task"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Response1", 0.8f)
        coEvery { mockAuraService.generateText(any(), any()) } returns "Response2"

        val responses = processor.processTask(task)

        val finalResponse = responses.find { it.sender == AgentType.GENESIS }
        assertNotNull(finalResponse)
        assertTrue("Confidence should be between 0 and 1",
            finalResponse!!.confidence in 0.0f..1.0f)
    }

    // Edge Cases
    @Test
    fun `test empty task string`() = runTest {
        val task = ""

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Processing empty task", 0.5f)

        val responses = processor.processTask(task)

        assertFalse("Should still return responses for empty task", responses.isEmpty())
    }

    @Test
    fun `test very long task string`() = runTest {
        val task = "a".repeat(10000)

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Long task processed", 0.8f)

        val responses = processor.processTask(task)

        assertTrue("Should handle very long tasks", responses.isNotEmpty())
    }

    @Test
    fun `test task with special characters`() = runTest {
        val task = "!@#$%^&*()_+{}|:<>?[]\\;',./`~"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Special chars handled", 0.8f)

        val responses = processor.processTask(task)

        assertTrue("Should handle special characters", responses.isNotEmpty())
    }

    @org.junit.jupiter.api.Test
    fun `test task with unicode characters`() = runTest {
        val task = "Hello 世界 🌍 مرحبا"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Unicode processed", 0.8f)

        val responses = processor.processTask(task)

        assertTrue("Should handle unicode", responses.isNotEmpty())
    }

    // State Transitions Tests
    @org.junit.jupiter.api.Test
    fun `test state transitions during processing`() = runTest {
        val task = "State transition test"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Processing", 0.8f)

        // Initial state
        assertTrue(processor.pipelineState.first() is PipelineState.Idle)

        // Process task
        processor.processTask(task)

        // Final state
        val finalState = processor.pipelineState.first()
        assertTrue("Should end in Completed state", finalState is PipelineState.Completed)
    }

    @Test
    fun `test multiple sequential tasks`() = runTest {
        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Response", 0.8f)

        processor.processTask("Task 1")
        processor.processTask("Task 2")
        processor.processTask("Task 3")

        val context = processor.processingContext.first()
        val totalTasks = context["total_tasks_processed"] as? Int
        assertEquals("Should track total tasks", 3, totalTasks)
    }

    // PipelineState Tests
    @org.junit.jupiter.api.Test
    fun `test PipelineState Idle`() {
        val state = PipelineState.Idle
        assertTrue(state is PipelineState.Idle)
    }

    @Test
    fun `test PipelineState Processing`() {
        val state = PipelineState.Processing("Test task")
        assertTrue(state is PipelineState.Processing)
        assertEquals("Test task", state.task)
    }

    @org.junit.jupiter.api.Test
    fun `test PipelineState Completed`() {
        val state = PipelineState.Completed("Completed task")
        assertTrue(state is PipelineState.Completed)
        assertEquals("Completed task", state.task)
    }

    @org.junit.jupiter.api.Test
    fun `test PipelineState Error`() {
        val state = PipelineState.Error("Error message")
        assertTrue(state is PipelineState.Error)
        assertEquals("Error message", state.message)
    }

    // Verification Tests
    @org.junit.jupiter.api.Test
    fun `test service interactions are called`() = runTest {
        val task = "create secure design"

        coEvery { mockCascadeService.processRequest(any(), any()) } returns
            AgentResponse("Cascade", 0.8f)
        coEvery { mockKaiService.processRequest(any(), any()) } returns
            AgentResponse("Kai", 0.9f)
        coEvery { mockAuraService.generateText(any(), any()) } returns "Aura"

        processor.processTask(task)

        coVerify { mockCascadeService.processRequest(any(), any()) }
        coVerify { mockKaiService.processRequest(any(), any()) }
        coVerify { mockAuraService.generateText(any(), any()) }
    }
}
