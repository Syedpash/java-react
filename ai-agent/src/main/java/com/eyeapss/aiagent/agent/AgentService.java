package com.eyeapss.aiagent.agent;

import com.eyeapss.aiagent.service.OpenAiService;
import com.eyeapss.aiagent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AgentService — the brain of the AI Agent.
 *
 * TWO MODES:
 *   1. OpenAI mode (when API key is configured):
 *      User message → OpenAI decides which tool to use → tool.execute() → final answer
 *
 *   2. Keyword mode (no API key — stub/fallback):
 *      User message → keyword matching → tool.execute() or stub response
 *
 * The switch between modes is automatic — OpenAiService.isConfigured() decides.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentService {

    // Injected by Spring via constructor (@RequiredArgsConstructor)
    private final OpenAiService openAiService;

    // Tools registered by name — HashMap for O(1) lookup
    private final Map<String, Tool> tools = new HashMap<>();

    // Conversation history
    private final ConversationMemory memory = new ConversationMemory();

    // System prompt — tells the AI how to behave and what tools are available
    private static final String SYSTEM_PROMPT =
        "You are a helpful AI assistant with access to the following tools:\n" +
        "- calculator: evaluates math expressions (e.g. '25 * 48')\n" +
        "- file_reader: reads a local file given its absolute path\n" +
        "- web_search: searches the web for information\n\n" +
        "When the user asks something that requires a tool, respond with EXACTLY this format:\n" +
        "USE_TOOL: tool_name | tool_input\n\n" +
        "Examples:\n" +
        "USE_TOOL: calculator | 25 * 48\n" +
        "USE_TOOL: file_reader | /home/user/notes.txt\n" +
        "USE_TOOL: web_search | Java streams tutorial\n\n" +
        "If no tool is needed, just answer directly.";

    public void registerTool(Tool tool) {
        tools.put(tool.name(), tool);
        log.info("Registered tool: {}", tool.name());
    }

    /**
     * Main entry point — user sends a message, agent returns a response.
     *
     * FLOW WITH OPENAI:
     *   1. Send message + history to OpenAI
     *   2. If OpenAI responds with "USE_TOOL: name | input" → run that tool
     *   3. Send tool result back to OpenAI for a final answer
     *   4. Return final answer
     *
     * FLOW WITHOUT OPENAI (keyword mode):
     *   1. Detect tool by keywords
     *   2. Run tool if found
     *   3. Return result directly
     */
    public String chat(String userMessage) {
        log.info("User: {}", userMessage);
        memory.add("user", userMessage);

        String response;

        if (openAiService.isConfigured()) {
            response = chatWithOpenAi(userMessage);
        } else {
            response = chatWithKeywords(userMessage);
        }

        memory.add("assistant", response);
        log.info("Agent: {}", response);
        return response;
    }

    // ─── OpenAI Path ────────────────────────────────────────────────────────

    private String chatWithOpenAi(String userMessage) {
        // Step 1: Ask OpenAI what to do
        List<String> recentHistory = memory.getRecent(10);
        String aiResponse = openAiService.chat(SYSTEM_PROMPT, recentHistory, userMessage);

        // Step 2: Did OpenAI decide to use a tool?
        if (aiResponse.startsWith("USE_TOOL:")) {
            return handleToolCall(aiResponse, userMessage);
        }

        // No tool — return OpenAI's direct answer
        return aiResponse;
    }

    /**
     * Parses "USE_TOOL: calculator | 25 * 48", runs the tool,
     * then sends the result back to OpenAI for a natural final answer.
     */
    private String handleToolCall(String aiResponse, String userMessage) {
        try {
            // Parse: "USE_TOOL: calculator | 25 * 48"
            String toolPart = aiResponse.substring("USE_TOOL:".length()).trim(); // "calculator | 25 * 48"
            String[] parts = toolPart.split("\\|", 2);                           // ["calculator ", " 25 * 48"]
            String toolName = parts[0].trim();                                   // "calculator"
            String toolInput = parts.length > 1 ? parts[1].trim() : "";         // "25 * 48"

            if (!tools.containsKey(toolName)) {
                return "I tried to use tool '" + toolName + "' but it is not available.";
            }

            // Run the tool
            String toolResult = tools.get(toolName).execute(toolInput);
            log.info("Tool [{}] result: {}", toolName, toolResult);

            // Send tool result back to OpenAI for a final natural language answer
            String followUpPrompt = "Tool result for '" + toolName + "': " + toolResult +
                                    "\nPlease give the user a clear final answer.";
            return openAiService.chat(SYSTEM_PROMPT, memory.getRecent(10), followUpPrompt);

        } catch (Exception e) {
            log.error("Error handling tool call: {}", e.getMessage());
            return "Error processing tool call: " + e.getMessage();
        }
    }

    // ─── Keyword Path (no API key) ───────────────────────────────────────────

    private String chatWithKeywords(String userMessage) {
        String toolName = detectTool(userMessage);

        if (toolName != null && tools.containsKey(toolName)) {
            Tool tool = tools.get(toolName);
            String toolInput = extractToolInput(userMessage, toolName);
            log.info("Using tool: {} | input: {}", toolName, toolInput);
            String toolResult = tool.execute(toolInput);
            return "Used [" + toolName + "] -> " + toolResult;
        }

        List<String> recent = memory.getRecent(5);
        return "[No API key] I received your message. History: " + recent.size() +
               " messages. Set OPENAI_API_KEY to get real AI responses.";
    }

    private String detectTool(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("calculate") ||
            (lower.contains("what is") && lower.matches(".*[0-9+\\-* /].*"))) {
            return "calculator";
        }
        if (lower.contains("read file") || lower.contains("open file")) {
            return "file_reader";
        }
        if (lower.contains("search") || lower.contains("look up")) {
            return "web_search";
        }
        return null;
    }

    private String extractToolInput(String message, String toolName) {
        return switch (toolName) {
            case "calculator"  -> message.replaceAll("(?i)calculate|what is", "").trim();
            case "file_reader" -> message.replaceAll("(?i)read file|open file", "").trim();
            case "web_search"  -> message.replaceAll("(?i)search|look up", "").trim();
            default            -> message;
        };
    }

    // ─── Public helpers ──────────────────────────────────────────────────────

    public void clearMemory() {
        memory.clear();
    }

    public List<String> getHistory() {
        return memory.getHistory();
    }

    public boolean isOpenAiConfigured() {
        return openAiService.isConfigured();
    }
}
