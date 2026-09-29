package com.eyeapss.aiagent.controller;

import com.eyeapss.aiagent.agent.AgentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AgentController.
 *
 * @SpringBootTest        — loads the full Spring context
 * @AutoConfigureMockMvc  — configures MockMvc automatically
 * @MockBean AgentService — replaces the real AgentService with a mock
 * @WithMockUser          — injects a fake authenticated user (bypasses JWT)
 */
@SpringBootTest
@AutoConfigureMockMvc
class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AgentService agentService; // fake — we control what it returns

    // ─── POST /agent/chat ─────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /agent/chat — authenticated user gets response")
    @WithMockUser(username = "alice")
    void chat_authenticated_returnsResponse() throws Exception {
        when(agentService.chat("calculate 10 + 5")).thenReturn("Used [calculator] -> 15.0");

        mockMvc.perform(post("/agent/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\": \"calculate 10 + 5\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Used [calculator] -> 15.0"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(agentService, times(1)).chat("calculate 10 + 5");
    }

    @Test
    @DisplayName("POST /agent/chat — unauthenticated returns 401/403")
    void chat_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/agent/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\": \"hello\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /agent/chat — blank message returns 400")
    @WithMockUser
    void chat_blankMessage_returns400() throws Exception {
        mockMvc.perform(post("/agent/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /agent/chat with clearHistory=true — clears memory before chat")
    @WithMockUser
    void chat_withClearHistory_clearsMemory() throws Exception {
        when(agentService.chat(anyString())).thenReturn("fresh start");

        mockMvc.perform(post("/agent/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\": \"hello\", \"clearHistory\": true}"))
                .andExpect(status().isOk());

        verify(agentService, times(1)).clearMemory();
        verify(agentService, times(1)).chat("hello");
    }

    // ─── GET /agent/history ───────────────────────────────────────────────────

    @Test
    @DisplayName("GET /agent/history — returns conversation history")
    @WithMockUser
    void history_returnsHistory() throws Exception {
        when(agentService.getHistory()).thenReturn(
                List.of("user: hello", "assistant: Hi there!"));

        mockMvc.perform(get("/agent/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("user: hello"))
                .andExpect(jsonPath("$[1]").value("assistant: Hi there!"));
    }

    @Test
    @DisplayName("GET /agent/history — unauthenticated returns 401/403")
    void history_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/agent/history"))
                .andExpect(status().is4xxClientError());
    }

    // ─── POST /agent/clear ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /agent/clear — clears memory and returns success")
    @WithMockUser
    void clear_clearsMemory() throws Exception {
        mockMvc.perform(post("/agent/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("memory cleared"));

        verify(agentService, times(1)).clearMemory();
    }

    @Test
    @DisplayName("POST /agent/clear — unauthenticated returns 401/403")
    void clear_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/agent/clear"))
                .andExpect(status().is4xxClientError());
    }
}
