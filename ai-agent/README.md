# AI Agent — Java + Spring Boot

A **ReAct-style AI Agent** (Reason + Act) built with Java 17 and Spring Boot 3.2.
This project builds on your Java learning journey — every concept used here is something you already know.

---

## What is a ReAct Agent?

A ReAct agent follows a **Reason + Act** loop — it thinks about what to do, then does it.

---

### High-Level Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (curl / browser)                  │
└───────────────────────────┬─────────────────────────────────────┘
                            │  POST /agent/chat
                            │  { "message": "calculate 25 * 48" }
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                      AgentController                            │
│   - Receives HTTP request                                       │
│   - Validates input (@Valid @NotBlank)                          │
│   - Calls agentService.chat(message)                           │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                       AgentService                              │
│                                                                 │
│   1. memory.add("user", message)                               │
│   2. toolName = detectTool(message)    ← keyword matching      │
│                                                                 │
│         toolName != null?                                       │
│         ┌─────────┴──────────┐                                 │
│        YES                   NO                                 │
│         │                    │                                  │
│         ▼                    ▼                                  │
│   tool.execute(input)   direct response                        │
│         │                    │                                  │
│         └─────────┬──────────┘                                 │
│                   │                                             │
│   3. memory.add("assistant", response)                         │
│   4. return response                                           │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                      AgentController                            │
│   - Wraps response in ChatResponse DTO                         │
│   - Returns 200 OK with JSON body                              │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
                   { "response": "Used [calculator] -> 1200.0",
                     "timestamp": "2026-09-23T10:30:00" }
```

---

### ReAct Agent Loop (detailed)

```
┌─────────────────────────────────────────────────────────────────┐
│  User: "calculate 25 * 48"                                      │
└──────────────────────┬──────────────────────────────────────────┘
                       │
                       ▼
             ┌─────────────────┐
             │  STEP 1: SAVE   │  memory.add("user", "calculate 25 * 48")
             │  to memory      │  history: ["user: calculate 25 * 48"]
             └────────┬────────┘
                      │
                      ▼
             ┌─────────────────┐
             │  STEP 2: THINK  │  detectTool("calculate 25 * 48")
             │  (detect tool)  │  message contains "calculate" → "calculator"
             └────────┬────────┘
                      │
          ┌───────────┴────────────┐
          │                        │
    tool found                no tool
    "calculator"              (null)
          │                        │
          ▼                        ▼
┌──────────────────┐    ┌──────────────────────┐
│   STEP 3: ACT    │    │  STEP 3: RESPOND     │
│                  │    │                      │
│ tool = tools     │    │  "I received your    │
│   .get("calc..") │    │   message..."        │
│                  │    └──────────┬───────────┘
│ input = "25 * 48"│               │
│  (keywords strip)│               │
│                  │               │
│ result =         │               │
│  tool.execute()  │               │
│  → "1200.0"      │               │
│                  │               │
│ response =       │               │
│ "Used [calc] ->  │               │
│  1200.0"         │               │
└────────┬─────────┘               │
         │                         │
         └────────────┬────────────┘
                      │
                      ▼
             ┌─────────────────┐
             │  STEP 4: SAVE   │  memory.add("assistant", response)
             │  to memory      │  history: ["user: ...", "assistant: ..."]
             └────────┬────────┘
                      │
                      ▼
             ┌─────────────────┐
             │  STEP 5: RETURN │  → back to AgentController → JSON response
             └─────────────────┘
```

---

### Tool Selection Flow

```
                  User message
                       │
                       ▼
              toLowerCase(message)
                       │
         ┌─────────────┼──────────────┐
         │             │              │
    contains        contains      contains
  "calculate"     "read file"    "search"
  OR "what is"    OR "open file" OR "look up"
   + numbers           │              │
         │             │              │
         ▼             ▼              ▼
   "calculator"  "file_reader"   "web_search"
         │             │              │
         └─────────────┼──────────────┘
                       │
                  tools.get(name)   ← HashMap O(1) lookup
                       │
                  tool.execute(input)
```

---

### ConversationMemory (ArrayList)

```
After 3 messages:

history ArrayList:
  index 0 → "user: calculate 25 * 48"
  index 1 → "assistant: Used [calculator] -> 1200.0"
  index 2 → "user: search Java streams"
  index 3 → "assistant: Search results for ..."
  index 4 → "user: read file /home/notes.txt"
  index 5 → "assistant: (file contents)"

getRecent(4):
  size = 6
  from = Math.max(0, 6 - 4) = 2
  subList(2, 6) → last 4 messages only
  ┌─────────────────────────────────────────┐
  │ index 2 → "user: search Java streams"   │
  │ index 3 → "assistant: Search results.." │
  │ index 4 → "user: read file /home/..."   │
  │ index 5 → "assistant: (file contents)"  │
  └─────────────────────────────────────────┘
  (older messages excluded — saves tokens when calling LLM)
```

---

### Spring Bean Wiring (how everything connects)

```
Spring starts up
       │
       ├── creates @Component CalculatorTool  ──┐
       ├── creates @Component FileReaderTool   ──┤
       ├── creates @Component WebSearchTool    ──┤
       │                                         │
       ├── creates @Service AgentService         │  injected via
       │     tools = {}  (empty HashMap)         │  constructor
       │                                         │
       ├── creates @Configuration AgentConfig ◄──┘
       │     injects: AgentService + all 3 tools
       │
       ├── calls ApplicationRunner.run()
       │     agentService.registerTool(calculatorTool)
       │     agentService.registerTool(fileReaderTool)
       │     agentService.registerTool(webSearchTool)
       │     tools = { "calculator":..., "file_reader":..., "web_search":... }
       │
       ├── creates @RestController AgentController
       │     injects: AgentService
       │
       └── starts Tomcat on port 8080
             ready to accept requests ✓
```

---

### Request/Response Data Flow

```
  curl POST /agent/chat
  Body: { "message": "calculate 25 * 48" }
              │
              ▼ Jackson deserializes JSON → ChatRequest object
  ┌───────────────────────┐
  │ ChatRequest            │
  │   message = "calc..." │
  │   clearHistory = false│
  └───────────┬───────────┘
              │ @Valid checks @NotBlank
              ▼
  AgentController.chat(request)
              │
              ▼
  AgentService.chat("calculate 25 * 48")
              │
              ▼ returns String
  "Used [calculator] -> 1200.0"
              │
              ▼ wrapped in DTO
  ┌───────────────────────────────────┐
  │ ChatResponse                       │
  │   response  = "Used [calc] -> ..." │
  │   timestamp = 2026-09-23T10:30:00 │
  └───────────────────────────────────┘
              │ Jackson serializes Java → JSON
              ▼
  { "response": "Used [calculator] -> 1200.0",
    "timestamp": "2026-09-23T10:30:00" }
```

---

**Example:**
- User: `"calculate 25 * 48"`
- Agent detects keyword `"calculate"` → picks `CalculatorTool`
- Tool evaluates `25 * 48` → returns `"1200.0"`
- Agent responds: `"Used [calculator] -> 1200.0"`

This is the same architecture used by LangChain, AutoGPT, and OpenAI Assistants under the hood.

---

## Java Concepts Used (from your learning history)

| Concept | Where it's used | You learned it in |
|---------|----------------|-------------------|
| `interface` | `Tool.java` — contract all tools implement | Session 2 OOP |
| `ArrayList` | `ConversationMemory` — stores chat history | Session 2 Collections |
| `HashMap` | `AgentService` — stores tools by name for fast lookup | Session 2 Collections |
| `Collections.unmodifiableList()` | `ConversationMemory.getHistory()` | Session 2 Collections utility |
| `try-with-resources` | `FileReaderTool` — auto-closes file handles | Session 2 File I/O |
| `BufferedReader` | `FileReaderTool` — reads file line by line | Session 2 File I/O |
| `StringBuilder` | `FileReaderTool` — builds file content string | Session 2 String & Math |
| `@Service`, `@Component` | Spring beans | TaskManager project |
| `@RestController` | `AgentController` REST endpoints | TaskManager project |
| `switch` expression | `AgentService.extractToolInput()` | Java 14+ syntax |
| `@Builder` (Lombok) | `ChatResponse` — builder pattern | Interview-level Java |

---

## Project Structure

```
ai-agent/
├── pom.xml                                         ← Maven build config, lists all dependencies
├── README.md                                       ← This file
└── src/
    └── main/
        ├── java/com/eyeapss/aiagent/
        │   │
        │   ├── AiAgentApplication.java             ← Spring Boot entry point — starts the app
        │   │
        │   ├── agent/                              ← Core agent logic
        │   │   ├── AgentService.java               ← The brain: detects tools, runs them, manages memory
        │   │   └── ConversationMemory.java         ← Stores chat history using ArrayList
        │   │
        │   ├── tool/                               ← All tools the agent can use
        │   │   ├── Tool.java                       ← Interface — contract every tool must follow
        │   │   ├── CalculatorTool.java             ← Evaluates math: "25 * 48" → "1200.0"
        │   │   ├── FileReaderTool.java             ← Reads a local file, returns its content
        │   │   └── WebSearchTool.java              ← Web search stub (replace with real API)
        │   │
        │   ├── controller/
        │   │   └── AgentController.java            ← REST endpoints: /agent/chat, /agent/history
        │   │
        │   ├── dto/                                ← Data Transfer Objects (request/response shapes)
        │   │   ├── ChatRequest.java                ← What the client sends: { "message": "..." }
        │   │   └── ChatResponse.java               ← What the server returns: { "response": "..." }
        │   │
        │   └── config/
        │       └── AgentConfig.java                ← Registers all tools into AgentService on startup
        │
        └── resources/
            └── application.yml                     ← App config: port, API keys, logging
```

---

## How Each File Works

### 1. `Tool.java` — The Interface (Contract)
Every tool in this project implements this interface.
The agent only talks to tools through this interface — it doesn't care about the details inside each tool.

```
Tool interface
    ├── name()        → unique ID, e.g. "calculator"
    ├── description() → tells the agent when to use it
    └── execute()     → runs the tool, returns String result
```

This is the same as `Flyable` and `Swimmable` from your Session 2 — a contract that multiple classes implement differently.

---

### 2. `ConversationMemory.java` — Chat History
Stores every message exchanged so far as a list of strings:

```
history = [
  "user: calculate 25 * 48",
  "assistant: Used [calculator] → 1200.0",
  "user: search Java streams",
  ...
]
```

Key methods:
- `add(role, message)` — adds `"user: ..."` or `"assistant: ..."` to the list
- `getRecent(n)` — returns only the last N messages (prevents sending too much to an LLM)
- `getHistory()` — returns the full list, but unmodifiable (read-only)

---

### 3. `AgentService.java` — The Brain
This is the most important class. It:
1. Takes the user message
2. Saves it to memory
3. Detects if a tool is needed (keyword matching)
4. Runs the tool if needed
5. Saves the response to memory
6. Returns the response

Tools are stored in a `HashMap<String, Tool>`:
```
tools = {
  "calculator"  → CalculatorTool instance,
  "file_reader" → FileReaderTool instance,
  "web_search"  → WebSearchTool instance
}
```
This gives O(1) lookup — instantly finds the right tool by name.

---

### 4. `CalculatorTool.java` — Math Evaluator
- Input: `"25 * 48"` or `"100 / 4"`
- Output: `"1200.0"` or `"25.0"`
- Uses `ScriptEngineManager` to evaluate expressions
- Has a manual fallback (`manualEvaluate`) for basic operations if ScriptEngine is unavailable

---

### 5. `FileReaderTool.java` — File Reader
- Input: absolute file path like `/home/user/notes.txt`
- Output: file contents as a String (max 100 lines)
- Uses `BufferedReader` + `try-with-resources` — same as Session 2 File I/O
- Includes safety checks: file exists? is readable? is it a directory?

---

### 6. `WebSearchTool.java` — Web Search (Stub)
- Currently returns a fake/simulated result
- To make it real: replace `execute()` with an HTTP call using Java's `HttpClient`
- Plug in SerpAPI or Google Custom Search when you have an API key

---

### 7. `AgentController.java` — REST API
Exposes 3 endpoints:

| Method | URL | What it does |
|--------|-----|-------------|
| POST | `/agent/chat` | Send a message, get a response |
| GET | `/agent/history` | View full conversation history |
| POST | `/agent/clear` | Clear conversation memory |

Uses `@RestController`, `@PostMapping`, `@GetMapping` — same annotations from your TaskManager.

---

### 8. `ChatRequest.java` and `ChatResponse.java` — DTOs
DTOs (Data Transfer Objects) define the shape of JSON going in and out.

**Request** (what you send):
```json
{
  "message": "calculate 25 * 48",
  "clearHistory": false
}
```

**Response** (what you get back):
```json
{
  "response": "Used [calculator] → 1200.0",
  "timestamp": "2026-09-23T10:30:00"
}
```

---

### 9. `AgentConfig.java` — Tool Registration
Runs once on startup using `ApplicationRunner`.
Calls `agentService.registerTool(...)` for each tool so the agent knows they exist.
Without this, the HashMap in `AgentService` would be empty and no tools would work.

---

### 10. `application.yml` — Configuration
```yaml
server:
  port: 8080          # app runs on http://localhost:8080

agent:
  openai-api-key: ... # set this when connecting a real LLM
  model: gpt-4o-mini
  max-history: 10     # only send last 10 messages to LLM as context
```

---

## Setup & Running

### Prerequisites
- Java 17+
- Maven 3.6+

### Verify Java version
```bash
java -version
# should say: openjdk 17 or higher
```

### Build the project
```bash
cd ai-agent
mvn compile
```

### Run the app
```bash
mvn spring-boot:run
```

You should see:
```
Started AiAgentApplication in 2.3 seconds
Registered tool: calculator
Registered tool: file_reader
Registered tool: web_search
```

App is now running at `http://localhost:8080`

---

## Testing the API

### 1. Chat — math calculation
```bash
curl -X POST http://localhost:8080/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "calculate 25 * 48"}'
```
Response:
```json
{
  "response": "Used [calculator] → 1200.0",
  "timestamp": "2026-09-23T10:30:00"
}
```

### 2. Chat — file reading
```bash
curl -X POST http://localhost:8080/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "read file /home/eyeapss/java/output.txt"}'
```

### 3. Chat — web search
```bash
curl -X POST http://localhost:8080/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "search Java streams tutorial"}'
```

### 4. View conversation history
```bash
curl http://localhost:8080/agent/history
```
Response:
```json
[
  "user: calculate 25 * 48",
  "assistant: Used [calculator] → 1200.0"
]
```

### 5. Clear memory
```bash
curl -X POST http://localhost:8080/agent/clear
```

### 6. New message + clear history in one request
```bash
curl -X POST http://localhost:8080/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "calculate 10 + 5", "clearHistory": true}'
```

---

## Tool Trigger Keywords

The agent picks a tool based on keywords in your message:

| Tool | Trigger keywords | Example message |
|------|-----------------|-----------------|
| `calculator` | "calculate", "what is" + numbers | `"calculate 100 / 4"` |
| `file_reader` | "read file", "open file" | `"read file /home/user/notes.txt"` |
| `web_search` | "search", "look up" | `"search Spring Boot tutorial"` |

If no keyword matches, the agent responds directly without using a tool.

---

## Adding a New Tool

1. Create a class in `src/main/java/com/eyeapss/aiagent/tool/`
2. Implement the `Tool` interface
3. Add `@Component` so Spring detects it
4. Register it in `AgentConfig.java`

**Example — WeatherTool:**
```java
@Component
public class WeatherTool implements Tool {

    @Override
    public String name() {
        return "weather";
    }

    @Override
    public String description() {
        return "Gets current weather for a city. Input: city name.";
    }

    @Override
    public String execute(String input) {
        // Call a weather API here
        return "Weather in " + input + ": 25°C, sunny";
    }
}
```

Then in `AgentConfig.java`:
```java
// inject WeatherTool
private final WeatherTool weatherTool;

// register it
agentService.registerTool(weatherTool);
```

And add the trigger in `AgentService.detectTool()`:
```java
if (lower.contains("weather") || lower.contains("temperature")) {
    return "weather";
}
```

---

## Connecting a Real LLM (OpenAI)

Right now the agent uses keyword matching to pick tools. To connect OpenAI:

1. Get an API key from https://platform.openai.com
2. Set it in `application.yml`:
   ```yaml
   agent:
     openai-api-key: sk-your-key-here
   ```
   Or as an environment variable:
   ```bash
   export OPENAI_API_KEY=sk-your-key-here
   ```
3. In `AgentService.chat()`, replace the keyword detection block with an HTTP call to OpenAI using Java's built-in `HttpClient` — send the user message + available tool descriptions, and let the LLM decide which tool to use.

---

## Dependencies (pom.xml)

| Dependency | Why |
|-----------|-----|
| `spring-boot-starter-web` | REST API + embedded Tomcat server |
| `spring-boot-starter-validation` | `@NotBlank`, `@Valid` on request DTOs |
| `lombok` | `@Getter`, `@Builder`, `@Slf4j` — removes boilerplate |
| `jackson-databind` | JSON ↔ Java object conversion (auto-included with Web) |
| `spring-boot-starter-test` | JUnit 5 + Mockito for testing |

---

## What to Build Next

1. **Connect OpenAI** — replace keyword detection with a real LLM call
2. **Add more tools** — WeatherTool, DatabaseQueryTool, EmailTool
3. **Add Spring Security** — protect the `/agent/chat` endpoint with JWT (you built this in TaskManager)
4. **Add a database** — save conversation history to PostgreSQL with Spring Data JPA
5. **Write tests** — unit test `AgentService` with Mockito, integration test `AgentController`
6. **Build a frontend** — React chat UI that calls `POST /agent/chat`
