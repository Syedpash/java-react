package com.eyeapss.aiagent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ChatResponse — the shape of JSON the server sends back from POST /agent/chat.
 *
 * EXAMPLE JSON OUTPUT:
 *   {
 *     "response": "Used [calculator] → 1200.0",
 *     "timestamp": "2026-09-23T10:30:00.123456"
 *   }
 *
 * LOMBOK ANNOTATIONS EXPLAINED:
 *
 *   @Data
 *     Generates: getters, setters, toString(), equals(), hashCode()
 *
 *   @Builder
 *     Generates a builder so you can create objects like this:
 *       ChatResponse.builder()
 *           .response("Used [calculator] → 1200.0")
 *           .build();
 *     Instead of:
 *       ChatResponse r = new ChatResponse();
 *       r.setResponse("Used [calculator] → 1200.0");
 *     Builder pattern is cleaner, especially when there are many optional fields.
 *
 *   @NoArgsConstructor
 *     Generates: public ChatResponse() {}
 *     Jackson needs this to deserialize JSON → Java object.
 *     (Even though we mostly serialize Java → JSON here, it's good practice.)
 *
 *   @AllArgsConstructor
 *     Generates: public ChatResponse(String response, LocalDateTime timestamp) {}
 *     Required by @Builder when combined with @NoArgsConstructor.
 *
 * WHY @Builder.Default?
 *   When using @Builder, field initializers like = LocalDateTime.now() are IGNORED.
 *   @Builder.Default tells Lombok: "use this default value when the builder
 *   doesn't set this field explicitly."
 *
 *   Without @Builder.Default:
 *     ChatResponse.builder().response("hi").build()
 *     → timestamp would be null!
 *
 *   With @Builder.Default:
 *     ChatResponse.builder().response("hi").build()
 *     → timestamp = LocalDateTime.now() at creation time ✓
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * The agent's response text.
     * Example: "Used [calculator] → 1200.0"
     */
    private String response;

    /**
     * When this response was generated.
     * Automatically set to the current date/time when the object is built.
     *
     * LocalDateTime — date and time without timezone (Java 8+)
     * LocalDateTime.now() → "2026-09-23T10:30:00.123456"
     *
     * Jackson serializes this as an ISO 8601 string in the JSON output.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
