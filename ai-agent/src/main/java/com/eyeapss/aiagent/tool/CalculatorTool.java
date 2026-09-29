package com.eyeapss.aiagent.tool;

import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * CalculatorTool — evaluates math expressions and returns the result.
 *
 * EXAMPLES:
 *   Input:  "25 * 48"     → Output: "1200.0"
 *   Input:  "100 / 4"     → Output: "25.0"
 *   Input:  "2 + 3 * 4"   → Output: "14.0"  (respects operator precedence)
 *   Input:  "10 / 0"      → Output: "Infinity"
 *
 * IMPLEMENTS Tool INTERFACE:
 *   This class implements the Tool interface — same pattern as implementing
 *   Flyable or Swimmable from Session 2. It must provide all 3 methods:
 *   name(), description(), execute().
 *
 * @Component vs @Service:
 *   @Service   — for business logic classes (AgentService)
 *   @Component — for general-purpose Spring-managed classes (tools, utilities)
 *   Both tell Spring: "create one instance of me and manage it as a bean."
 *   The difference is just semantic — helps humans reading the code understand the role.
 */
@Component
public class CalculatorTool implements Tool {

    /**
     * Unique name used as the HashMap key in AgentService.
     * AgentService looks this up with: tools.get("calculator")
     */
    @Override
    public String name() {
        return "calculator";
    }

    /**
     * Description sent to the LLM (in a real agent) so it knows when to use this tool.
     */
    @Override
    public String description() {
        return "Evaluates math expressions. Input: a math expression like '2 + 2' or '10 * 5 / 2'.";
    }

    /**
     * Evaluates the math expression in the input string.
     *
     * APPROACH 1 — ScriptEngineManager (primary):
     *   Java has a built-in JavaScript engine (Nashorn/GraalVM).
     *   We use it to evaluate math expressions as if they were JS code.
     *   "25 * 48" is valid JavaScript, so it evaluates to 1200.
     *   This handles complex expressions like "2 + 3 * 4" with correct precedence.
     *
     * APPROACH 2 — manualEvaluate() (fallback):
     *   If the JS engine isn't available (some JVM configs), we fall back
     *   to manually splitting on +, -, *, / and parsing each number.
     *   Only works for simple single-operation expressions.
     *
     * try/catch:
     *   If the expression is invalid (e.g. "abc + def"), the engine throws.
     *   We catch it and return a friendly error string instead of crashing.
     *
     * @param input math expression string, e.g. "25 * 48"
     * @return result as string, or error message
     */
    @Override
    public String execute(String input) {
        try {
            // Create a ScriptEngineManager — factory for script engines
            ScriptEngineManager manager = new ScriptEngineManager();

            // Get the JavaScript engine by name
            // Returns null if not available (some stripped-down JVM builds)
            ScriptEngine engine = manager.getEngineByName("JavaScript");

            if (engine == null) {
                // JavaScript engine not available — use simple fallback
                return manualEvaluate(input);
            }

            // eval() executes the expression as code and returns the result
            // input.trim() removes any leading/trailing spaces
            Object result = engine.eval(input.trim());

            // Convert result to String (could be Integer, Double, Long depending on expression)
            return String.valueOf(result);

        } catch (Exception e) {
            // Something went wrong — invalid expression, null input, etc.
            return "Error evaluating expression: " + e.getMessage();
        }
    }

    /**
     * Fallback manual evaluator for basic single-operation expressions.
     * Handles: a + b, a - b, a * b, a / b
     *
     * HOW IT WORKS:
     *   1. Detect which operator is present using .contains()
     *   2. Split the string on that operator using .split()
     *   3. Parse each half as a Double using Double.parseDouble()
     *   4. Apply the operation and return the result as String
     *
     * EXAMPLE: "25 * 48"
     *   input.contains("*")  → true
     *   split("\\*")         → ["25 ", " 48"]  (\\* escapes * in regex)
     *   parts[0].trim()      → "25"
     *   parts[1].trim()      → "48"
     *   Double.parseDouble() → 25.0 and 48.0
     *   25.0 * 48.0          → 1200.0
     *   String.valueOf()     → "1200.0"
     *
     * NOTE on regex escaping:
     *   "+" and "*" are special regex characters — they must be escaped with \\
     *   "-" is not special in this context, so no escape needed
     *   "/" is not special in Java regex, no escape needed
     *
     * @param input simple math expression
     * @return result as String, or error message
     */
    private String manualEvaluate(String input) {
        try {
            input = input.trim();

            if (input.contains("+")) {
                String[] parts = input.split("\\+"); // split on +
                double a = Double.parseDouble(parts[0].trim());
                double b = Double.parseDouble(parts[1].trim());
                return String.valueOf(a + b);

            } else if (input.contains("-")) {
                String[] parts = input.split("-"); // split on -
                double a = Double.parseDouble(parts[0].trim());
                double b = Double.parseDouble(parts[1].trim());
                return String.valueOf(a - b);

            } else if (input.contains("*")) {
                String[] parts = input.split("\\*"); // split on * (escaped)
                double a = Double.parseDouble(parts[0].trim());
                double b = Double.parseDouble(parts[1].trim());
                return String.valueOf(a * b);

            } else if (input.contains("/")) {
                String[] parts = input.split("/"); // split on /
                double a = Double.parseDouble(parts[0].trim());
                double b = Double.parseDouble(parts[1].trim());

                // Guard against division by zero — same pattern from Session 2 exceptions
                if (b == 0) return "Error: division by zero";

                return String.valueOf(a / b);
            }

            return "Unsupported expression: " + input;

        } catch (NumberFormatException e) {
            // Double.parseDouble() throws this if the string isn't a valid number
            // e.g. "abc + 5" → "abc" can't be parsed
            return "Invalid numbers in expression: " + input;
        }
    }
}
