package com.eyeapss.aiagent.agent;

import com.eyeapss.aiagent.service.OpenAiService;
import com.eyeapss.aiagent.tool.CalculatorTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AgentService.
 *
 * Uses Mockito to fake OpenAiService — we test AgentService logic in isolation.
 *
 * @ExtendWith(MockitoExtension.class) — enables Mockito annotations
 * @Mock    — creates a fake object (does nothing by default)
 * @InjectMocks — creates real AgentService with mocks injected
 */
@ExtendWith(MockitoExtension.class)
class AgentServiceTest {

    @Mock
    private OpenAiService openAiService; // fake — we control what it returns

    @InjectMocks
    private AgentService agentService;   // real AgentService with fake OpenAiService injected

    private CalculatorTool calculatorTool;

    @BeforeEach
    void setUp() {
        // Register a real CalculatorTool for tool-calling tests
        calculatorTool = new CalculatorTool();
        agentService.registerTool(calculatorTool);
    }

    // ─── Tests: Keyword Mode (no API key) ────────────────────────────────────

    @Test
    @DisplayName("Should use calculator tool when no API key and message contains 'calculate'")
    void chat_keywordMode_calculatorTriggered() {
        // Arrange: no API key configured
        when(openAiService.isConfigured()).thenReturn(false);

        // Act
        String response = agentService.chat("calculate 10 + 5");

        // Assert: tool was used and result is in response
        assertThat(response).contains("calculator");
        assertThat(response).contains("15");
    }

    @Test
    @DisplayName("Should return stub response when no API key and no tool keyword")
    void chat_keywordMode_noToolMatch_returnsStub() {
        // Arrange
        when(openAiService.isConfigured()).thenReturn(false);

        // Act
        String response = agentService.chat("Hello, how are you?");

        // Assert: stub message returned
        assertThat(response).contains("No API key");
    }

    // ─── Tests: OpenAI Mode ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should return OpenAI direct response when no tool needed")
    void chat_openAiMode_directResponse() {
        // Arrange: API key configured, OpenAI returns a direct answer
        when(openAiService.isConfigured()).thenReturn(true);
        when(openAiService.chat(anyString(), anyList(), anyString()))
                .thenReturn("Hello! I am an AI agent.");

        // Act
        String response = agentService.chat("Hello!");

        // Assert
        assertThat(response).isEqualTo("Hello! I am an AI agent.");
        verify(openAiService, times(1)).chat(anyString(), anyList(), anyString());
    }

    @Test
    @DisplayName("Should call tool when OpenAI responds with USE_TOOL directive")
    void chat_openAiMode_toolCall() {
        // Arrange: OpenAI says to use calculator, then gives final answer
        when(openAiService.isConfigured()).thenReturn(true);
        when(openAiService.chat(anyString(), anyList(), eq("calculate 25 * 48")))
                .thenReturn("USE_TOOL: calculator | 25 * 48");
        when(openAiService.chat(anyString(), anyList(), contains("1200")))
                .thenReturn("25 multiplied by 48 equals 1200.");

        // Act
        String response = agentService.chat("calculate 25 * 48");

        // Assert: OpenAI was called twice (once to decide, once for final answer)
        assertThat(response).isEqualTo("25 multiplied by 48 equals 1200.");
        verify(openAiService, times(2)).chat(anyString(), anyList(), anyString());
    }

    @Test
    @DisplayName("Should handle unknown tool gracefully")
    void chat_openAiMode_unknownTool() {
        // Arrange: OpenAI requests a tool that doesn't exist
        when(openAiService.isConfigured()).thenReturn(true);
        when(openAiService.chat(anyString(), anyList(), anyString()))
                .thenReturn("USE_TOOL: unknown_tool | some input");

        // Act
        String response = agentService.chat("do something");

        // Assert: friendly error message
        assertThat(response).contains("unknown_tool");
        assertThat(response).contains("not available");
    }

    // ─── Tests: Memory ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should save messages to memory")
    void chat_savesMessagesToMemory() {
        when(openAiService.isConfigured()).thenReturn(false);

        agentService.chat("Hello");

        assertThat(agentService.getHistory()).hasSize(2); // user + assistant
        assertThat(agentService.getHistory().get(0)).startsWith("user:");
        assertThat(agentService.getHistory().get(1)).startsWith("assistant:");
    }

    @Test
    @DisplayName("Should clear memory when clearMemory() is called")
    void clearMemory_clearsHistory() {
        when(openAiService.isConfigured()).thenReturn(false);

        agentService.chat("Hello");
        assertThat(agentService.getHistory()).isNotEmpty();

        agentService.clearMemory();
        assertThat(agentService.getHistory()).isEmpty();
    }

    @Test
    @DisplayName("isOpenAiConfigured() should reflect OpenAiService state")
    void isOpenAiConfigured_delegatesToOpenAiService() {
        when(openAiService.isConfigured()).thenReturn(true);
        assertThat(agentService.isOpenAiConfigured()).isTrue();

        when(openAiService.isConfigured()).thenReturn(false);
        assertThat(agentService.isOpenAiConfigured()).isFalse();
    }
}
