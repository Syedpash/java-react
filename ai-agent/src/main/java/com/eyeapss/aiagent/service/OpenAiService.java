package com.eyeapss.aiagent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * OpenAiService — calls the OpenAI Chat Completions API.
 *
 * KEY-READY DESIGN:
 *   - If openai.api-key is set (not blank, not placeholder) → real API call
 *   - If not set → falls back to stub response
 *   This lets the app run and develop without an API key.
 *
 * USES Java built-in HttpClient (Java 11+) — no extra dependency needed.
 *
 * API ENDPOINT: POST https://api.openai.com/v1/chat/completions
 * REQUEST BODY:
 *   {
 *     "model": "gpt-4o-mini",
 *     "messages": [
 *       { "role": "system", "content": "You are a helpful assistant." },
 *       { "role": "user",   "content": "Hello!" }
 *     ]
 *   }
 * RESPONSE: { "choices": [{ "message": { "content": "Hi there!" } }] }
 */
@Service
@Slf4j
public class OpenAiService {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    // Injected from application.yml — ${agent.openai-api-key}
    @Value("${agent.openai-api-key:}")
    private String apiKey;

    // Which model to use — default gpt-4o-mini (cheap and fast)
    @Value("${agent.model:gpt-4o-mini}")
    private String model;

    // Jackson ObjectMapper for building and parsing JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Java 11+ built-in HTTP client — reused across calls
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Returns true if a real API key is configured.
     * Used by AgentService to decide whether to call OpenAI or use keyword matching.
     */
    public boolean isConfigured() {
        return apiKey != null
                && !apiKey.isBlank()
                && !apiKey.equals("your-api-key-here");
    }

    /**
     * Calls OpenAI Chat Completions API with the given messages.
     *
     * @param systemPrompt instructions for the AI (its "personality")
     * @param history      recent conversation messages ["user: ...", "assistant: ..."]
     * @param userMessage  the latest user message
     * @return AI response text, or stub if no key configured
     */
    public String chat(String systemPrompt, List<String> history, String userMessage) {
        if (!isConfigured()) {
            log.info("No OpenAI API key configured — returning stub response");
            return stubResponse(userMessage);
        }

        try {
            // Build the JSON request body
            String requestBody = buildRequestBody(systemPrompt, history, userMessage);
            log.debug("OpenAI request body: {}", requestBody);

            // Build the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            // Send request and get response
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            log.debug("OpenAI response status: {}", response.statusCode());

            if (response.statusCode() == 200) {
                return parseResponse(response.body());
            } else {
                log.error("OpenAI API error: {} — {}", response.statusCode(), response.body());
                return "OpenAI API error (" + response.statusCode() + "). Check your API key and quota.";
            }

        } catch (Exception e) {
            log.error("Failed to call OpenAI API: {}", e.getMessage());
            return "Failed to reach OpenAI: " + e.getMessage();
        }
    }

    /**
     * Builds the JSON request body for OpenAI Chat Completions API.
     *
     * Converts conversation history strings like "user: hello" into
     * OpenAI message objects: { "role": "user", "content": "hello" }
     */
    private String buildRequestBody(String systemPrompt, List<String> history, String userMessage)
            throws Exception {

        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", model);

        ArrayNode messages = objectMapper.createArrayNode();

        // System message — sets the AI's behavior
        ObjectNode systemMsg = objectMapper.createObjectNode();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        // Add conversation history — parse "user: ..." and "assistant: ..." strings
        for (String entry : history) {
            if (entry.startsWith("user: ")) {
                ObjectNode msg = objectMapper.createObjectNode();
                msg.put("role", "user");
                msg.put("content", entry.substring(6)); // strip "user: " prefix
                messages.add(msg);
            } else if (entry.startsWith("assistant: ")) {
                ObjectNode msg = objectMapper.createObjectNode();
                msg.put("role", "assistant");
                msg.put("content", entry.substring(11)); // strip "assistant: " prefix
                messages.add(msg);
            }
        }

        // Current user message
        ObjectNode userMsg = objectMapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        root.set("messages", messages);

        return objectMapper.writeValueAsString(root);
    }

    /**
     * Parses the OpenAI response JSON and extracts the assistant's reply text.
     *
     * Response structure:
     * {
     *   "choices": [
     *     { "message": { "role": "assistant", "content": "Hello!" } }
     *   ]
     * }
     */
    private String parseResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        return root
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
    }

    /**
     * Stub response when no API key is configured.
     * Gives a helpful message so the developer knows what to do.
     */
    private String stubResponse(String userMessage) {
        return "[Stub] No OpenAI API key configured. " +
               "Set OPENAI_API_KEY environment variable to get real AI responses. " +
               "Your message was: \"" + userMessage + "\"";
    }
}
