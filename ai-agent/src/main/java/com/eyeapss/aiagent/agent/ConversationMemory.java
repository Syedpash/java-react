package com.eyeapss.aiagent.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ConversationMemory — stores the full chat history between user and agent.
 *
 * WHY IS THIS NEEDED?
 *   Every time you send a message, the agent needs context from past messages
 *   to give a coherent response. Without memory, every message would feel
 *   like a fresh start with no context.
 *
 * HOW MESSAGES ARE STORED:
 *   Each message is stored as a single String with a role prefix:
 *     "user: calculate 25 * 48"
 *     "assistant: Used [calculator] → 1200.0"
 *     "user: search Java streams"
 *     ...
 *
 * DATA STRUCTURE CHOICE — WHY ArrayList?
 *   - Messages are ordered (we need insertion order preserved)
 *   - We frequently add to the end → O(1) with ArrayList
 *   - We access recent messages using subList() → efficient
 *   - We never delete from the middle → ArrayList is a good fit
 *   - HashMap wouldn't work here — messages don't have unique keys
 *
 * JAVA CONCEPTS USED:
 *   - ArrayList<String>              — ordered resizable list (Session 2 Collections)
 *   - Collections.unmodifiableList() — read-only wrapper (Collections utility class)
 *   - subList(from, to)              — slices a list like substring slices a String
 *   - Math.max()                     — prevents negative index if history is short
 */
public class ConversationMemory {

    // The actual storage — ArrayList preserves insertion order
    // "final" means we can't replace this list with a new one,
    // but we CAN still add/remove items from it
    private final List<String> history = new ArrayList<>();

    /**
     * Adds a message to history.
     *
     * @param role    "user" or "assistant"
     * @param message the actual message text
     *
     * Example output in history: "user: calculate 25 * 48"
     */
    public void add(String role, String message) {
        // Concatenate role and message, store as one string
        // e.g. "user" + ": " + "hello" → "user: hello"
        history.add(role + ": " + message);
    }

    /**
     * Returns the full conversation history.
     *
     * Returns an UNMODIFIABLE view — the caller can read it but not modify it.
     * This prevents bugs where someone accidentally clears or modifies history
     * from outside this class.
     *
     * Note: this is the Collections.unmodifiableList() you learned —
     *       same concept as making a defensive copy.
     */
    public List<String> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Returns only the last N messages.
     *
     * WHY LIMIT MESSAGES?
     *   When sending history to an LLM (like OpenAI), you have a token limit.
     *   Sending 100+ messages wastes tokens and costs money.
     *   Sending only the last 10 gives the agent enough recent context.
     *
     * HOW subList WORKS:
     *   history = ["msg1", "msg2", "msg3", "msg4", "msg5"]
     *   getRecent(3):
     *     size = 5
     *     from = Math.max(0, 5 - 3) = 2
     *     return subList(2, 5) → ["msg3", "msg4", "msg5"]
     *
     * Math.max(0, size - n) prevents negative index when history has fewer than n messages:
     *   history has 2 messages, n = 10 → Math.max(0, 2-10) = Math.max(0, -8) = 0
     *   returns the whole list safely
     *
     * @param n number of recent messages to return
     */
    public List<String> getRecent(int n) {
        int size = history.size();
        int from = Math.max(0, size - n); // start index — never goes below 0
        return history.subList(from, size); // slice from "from" to end
    }

    /**
     * Clears all stored messages.
     * Called when the user wants to start a fresh conversation.
     */
    public void clear() {
        history.clear();
    }

    /**
     * @return total number of messages stored
     */
    public int size() {
        return history.size();
    }
}
