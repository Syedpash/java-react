package com.eyeapss.aiagent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ChatRequest — the shape of JSON the client sends to POST /agent/chat.
 *
 * DTO = Data Transfer Object
 *   A DTO is a plain object whose only job is to carry data between layers.
 *   It has no business logic — just fields, getters, setters.
 *   You used this same pattern in your TaskManager project.
 *
 * JSON → Java (how it works):
 *   When a client sends:
 *     { "message": "calculate 25 * 48", "clearHistory": false }
 *
 *   Spring + Jackson automatically maps this to a ChatRequest object:
 *     chatRequest.getMessage()      → "calculate 25 * 48"
 *     chatRequest.isClearHistory()  → false
 *
 * LOMBOK ANNOTATIONS:
 *   @Data — generates all of these automatically:
 *     - getters for all fields   (getMessage(), isClearHistory())
 *     - setters for all fields   (setMessage(), setClearHistory())
 *     - toString()               (for logging: "ChatRequest(message=..., clearHistory=false)")
 *     - equals() and hashCode()  (for comparison)
 *   Without @Data you'd write all of this by hand.
 *
 * VALIDATION:
 *   @NotBlank — rejects null, empty string "", and whitespace-only string "   "
 *   When @Valid is on the controller parameter, Spring checks this before calling the method.
 *   If invalid → 400 Bad Request is returned automatically with an error message.
 */
@Data
public class ChatRequest {

    /**
     * The user's message to the agent.
     * Cannot be null, empty, or whitespace-only.
     *
     * Valid:   { "message": "calculate 25 * 48" }
     * Invalid: { "message": "" }     → 400 Bad Request: "Message cannot be empty"
     * Invalid: { "message": "   " }  → 400 Bad Request: "Message cannot be empty"
     */
    @NotBlank(message = "Message cannot be empty")
    private String message;

    /**
     * Optional flag — if true, clears conversation history before processing this message.
     * Useful when starting a completely new topic.
     *
     * Defaults to false — history is preserved across messages.
     * If the client doesn't send this field, Jackson sets it to false automatically.
     *
     * Example to clear and start fresh:
     *   { "message": "hello", "clearHistory": true }
     */
    private boolean clearHistory = false;
}
