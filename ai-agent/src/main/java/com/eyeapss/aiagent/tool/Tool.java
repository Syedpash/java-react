package com.eyeapss.aiagent.tool;

/**
 * Tool — the interface (contract) every tool must implement.
 *
 * WHY AN INTERFACE?
 *   The agent doesn't need to know HOW a tool works internally.
 *   It just needs to know:
 *     - what is this tool called?      → name()
 *     - when should I use it?          → description()
 *     - how do I run it?               → execute()
 *
 *   This is called "programming to an interface" — a key OOP principle.
 *   Same idea as Flyable/Swimmable from Session 2, but applied to agent tools.
 *
 * HOW IT'S USED:
 *   AgentService stores tools in a HashMap<String, Tool>:
 *     tools.put("calculator", new CalculatorTool());
 *
 *   When the agent needs to run a tool:
 *     Tool tool = tools.get("calculator");  // fetches by name
 *     tool.execute("25 * 48");              // runs it — doesn't care it's a CalculatorTool
 *
 * IMPLEMENTING THIS INTERFACE:
 *   public class MyTool implements Tool {
 *       @Override public String name() { return "my_tool"; }
 *       @Override public String description() { return "Does X when Y"; }
 *       @Override public String execute(String input) { return "result"; }
 *   }
 */
public interface Tool {

    /**
     * Unique identifier for this tool.
     * Used as the key in AgentService's HashMap.
     *
     * Examples: "calculator", "file_reader", "web_search"
     *
     * @return lowercase, underscore-separated tool name
     */
    String name();

    /**
     * Human-readable description of what this tool does and when to use it.
     * In a real LLM-backed agent, this description is sent to the AI
     * so it can decide which tool to pick.
     *
     * @return description string
     */
    String description();

    /**
     * Executes the tool with the given input and returns the result.
     *
     * Always returns a String — even errors are returned as strings
     * so the agent can include the error in its response.
     *
     * @param input the raw input extracted from the user's message
     * @return result as a String
     */
    String execute(String input);
}
