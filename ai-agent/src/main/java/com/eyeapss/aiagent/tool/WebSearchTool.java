package com.eyeapss.aiagent.tool;

import org.springframework.stereotype.Component;

/**
 * WebSearchTool — searches the web and returns results.
 *
 * CURRENT STATE: STUB (placeholder)
 *   Right now this returns a fake/simulated response.
 *   It's a stub so the project compiles and runs without needing an API key.
 *
 * TO MAKE IT REAL:
 *   Replace the execute() body with an HTTP call to a search API:
 *
 *   Option A — SerpAPI (paid, easy to use):
 *     URL: https://serpapi.com/search?q={query}&api_key={key}
 *     Returns JSON with search results.
 *
 *   Option B — Google Custom Search API (free tier: 100 queries/day):
 *     URL: https://www.googleapis.com/customsearch/v1?q={query}&key={key}&cx={cx}
 *
 *   Use Java's built-in HttpClient (Java 11+) — no extra dependency needed:
 *     HttpClient client = HttpClient.newHttpClient();
 *     HttpRequest request = HttpRequest.newBuilder()
 *         .uri(URI.create("https://serpapi.com/search?q=" + URLEncoder.encode(input, "UTF-8") + "&api_key=YOUR_KEY"))
 *         .build();
 *     HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
 *     // parse response.body() with Jackson ObjectMapper
 */
@Component
public class WebSearchTool implements Tool {

    @Override
    public String name() {
        return "web_search";
    }

    @Override
    public String description() {
        return "Searches the web for information. Input: a search query string.";
    }

    /**
     * Performs a web search for the given query.
     *
     * CURRENTLY: returns a stub response (no real HTTP call).
     * Replace this with a real API call when you have an API key.
     *
     * @param input the search query, e.g. "Java streams tutorial"
     * @return search results as formatted string
     */
    @Override
    public String execute(String input) {
        // ── STUB RESPONSE ────────────────────────────────────────────────────
        // This simulates what a real search API would return.
        // String concatenation with + here is fine since it's not in a loop.
        return "Search results for \"" + input.trim() + "\":\n"
             + "1. [Stub] This is a placeholder result.\n"
             + "2. [Stub] Connect a real search API (SerpAPI, Google Custom Search) to get live results.\n"
             + "   Tip: set your API key in application.yml and use Java HttpClient to make the call.";

        // ── REPLACE WITH THIS when you have an API key ────────────────────
        // try {
        //     String encodedQuery = URLEncoder.encode(input.trim(), StandardCharsets.UTF_8);
        //     String url = "https://serpapi.com/search?q=" + encodedQuery + "&api_key=" + apiKey;
        //
        //     HttpClient client = HttpClient.newHttpClient();
        //     HttpRequest request = HttpRequest.newBuilder()
        //             .uri(URI.create(url))
        //             .GET()
        //             .build();
        //
        //     HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //     return response.body(); // parse JSON here with ObjectMapper
        //
        // } catch (Exception e) {
        //     return "Error performing search: " + e.getMessage();
        // }
    }
}
