# Java Learning Journey

## Environment Setup
- OS: WSL (Windows Subsystem for Linux)
- Java Version: OpenJDK 17 (upgraded from 11 on 2026-09-15)
- JAVA_HOME: `/usr/lib/jvm/java-17-openjdk-amd64`
- Maven: 3.8.7
- PostgreSQL: 16.15
- Node.js / Next.js: 16.3.5 (added 2026-09-16)

---

## Session 1 — 2026-08-25

### 1. Java Basics
- Java is statically-typed, object-oriented, runs on JVM
- Every program needs a `class` with a `main` method
- Statements end with `;`
- `System.out.println()` for output

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

### 2. Variables & Data Types
- Primitives: `int`, `double`, `boolean`, `char`
- Reference type: `String`
- Static typing — types declared at compile time

```java
int age = 25;
double price = 9.99;
boolean isActive = true;
char letter = 'A';
String name = "Alice";
```

### 3. Control Flow
- `if/else` conditionals
- `for` loop with counter
- `while` loop

```java
if (age >= 18) {
    System.out.println("Adult");
} else {
    System.out.println("Minor");
}

for (int i = 0; i < 5; i++) {
    System.out.println(i);
}
```

### 4. Methods
- Parameters and return types
- `static` methods called without an object instance

```java
public static int add(int a, int b) {
    return a + b;
}
```

### 5. Classes & Objects (OOP)
- Fields (state), constructors, methods (behavior)
- `this` keyword to refer to current instance
- Creating objects with `new`

```java
public class Car {
    String brand;
    int speed;

    public Car(String brand, int speed) {
        this.brand = brand;
        this.speed = speed;
    }

    public void describe() {
        System.out.println(brand + " going " + speed + " mph");
    }
}
```

---

## Session 2 — 2026-09-09

### 6. OOP Advanced
- `extends` for inheritance, `super()` to call parent constructor
- `@Override` to replace parent methods
- Polymorphism — parent type variable holds child type object
- `abstract` classes — templates that can't be instantiated
- `abstract` methods — must be implemented by subclasses
- `interface` — contract defining capabilities, implemented with `implements`
- A class can implement multiple interfaces but extend only one class

```java
public abstract class Shape {
    public abstract double area();
    public void printArea() { System.out.println("Area: " + area()); }
}

public interface Flyable { void fly(); }

public class Duck extends Animal implements Flyable, Swimmable {
    @Override public void fly() { System.out.println(name + " is flying."); }
    @Override public void swim() { System.out.println(name + " is swimming."); }
}
```

### 7. Collections
- `ArrayList<T>` — ordered, resizable list; access by index
- `HashMap<K, V>` — key-value pairs; fast lookup by key
- Use `.containsKey()` before `.get()` to avoid null returns
- Enhanced for-loop (`for (T item : list)`) preferred when index not needed

```java
ArrayList<String> names = new ArrayList<>();
names.add("Alice");
names.get(0);
names.remove("Alice");
names.contains("Bob");

HashMap<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);
scores.get("Alice");
scores.containsKey("Bob");
```

### 8. Exception Handling
- `try/catch` to handle runtime errors gracefully
- Multiple `catch` blocks for different exception types
- `finally` always runs — use for cleanup
- `throw` to raise exceptions manually
- `try-with-resources` — auto-closes resources

```java
public static int divide(int a, int b) {
    if (b == 0) throw new IllegalArgumentException("Cannot divide by zero");
    return a / b;
}

try {
    System.out.println(divide(6, 0));
} catch (IllegalArgumentException e) {
    System.out.println("Error: " + e.getMessage());
}
```

Common exceptions: `ArithmeticException`, `NullPointerException`,
`ArrayIndexOutOfBoundsException`, `NumberFormatException`, `IllegalArgumentException`, `IOException`

### 9. Java Standard Library — String & Math
- String is immutable; use `.equals()` not `==` for comparison
- Key methods: `length()`, `trim()`, `toUpperCase()`, `toLowerCase()`,
  `contains()`, `replace()`, `split()`, `indexOf()`, `substring()`, `charAt()`
- Case-insensitive check: `input.toLowerCase().contains("word")`
- `StringBuilder` for efficient string building in loops; `reverse()` built-in
- `Math`: `abs()`, `max()`, `min()`, `pow()`, `sqrt()`, `round()`, `floor()`, `ceil()`, `random()`

```java
String s = "Hello, World!";
s.toUpperCase();
s.contains("World");
s.split(",");
new StringBuilder(s).reverse().toString();

Math.pow(2, 10);   // 1024.0
Math.random();     // 0.0 to 1.0
(int)(Math.random() * 100); // random int 0–99
```

### 10. File I/O
- `FileWriter` to write; `BufferedReader` + `FileReader` to read line by line
- Use `try-with-resources` — auto-closes file handles
- Append mode: `new FileWriter("file.txt", true)`
- `File` class to check existence, size, etc.

```java
try (FileWriter writer = new FileWriter("notes.txt")) {
    writer.write("Line 1\n");
}

try (BufferedReader reader = new BufferedReader(new FileReader("notes.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}
```

---


## Core Java — Completed (2026-09-09)
1. [x] Basics — variables, types, operators
2. [x] Control flow — if/else, loops, switch
3. [x] Methods — parameters, return types
4. [x] OOP intro — classes, objects
5. [x] OOP advanced — inheritance, interfaces, polymorphism
6. [x] Collections — ArrayList, HashMap
7. [x] Exceptions — try/catch/finally
8. [x] Java Standard Library — String, Math
9. [x] File I/O
10. [x] Generics
11. [x] Streams & Lambda (Java 8+)

## Core Java — Interview Level (completed: 2026-09-10)

### OOP Deep Level
12. [x] Access modifiers — public, private, protected, package-private
13. [x] static keyword — static fields, static blocks, static vs instance
14. [x] final keyword — final variables, final methods, final classes
15. [x] Enums — type-safe constants, methods on enums
16. [x] Optional — null-safe programming (Java 8+)

### Collections Internals
17. [x] How HashMap works internally — hashing, buckets, collision handling
18. [x] LinkedList vs ArrayList — time complexities, when to use which
19. [x] HashSet, LinkedHashMap, TreeMap — differences and use cases
20. [x] Collections utility class — sort, reverse, shuffle, unmodifiable

### Java 8+ Features
21. [x] Functional interfaces — Predicate, Function, Consumer, Supplier
22. [x] Method references — Class::method, object::method, constructor::new
23. [x] Optional in depth — of(), orElse(), map(), ifPresent(), flatMap()

### OOP Design Principles
24. [x] SOLID principles — with Java examples
25. [x] Composition vs Inheritance — when to prefer which

### Concurrency
26. [x] Thread and Runnable — creating and starting threads
27. [x] ExecutorService and thread pools
28. [x] synchronized and volatile
29. [x] Deadlock and race conditions
30. [x] Callable and Future

---

## Session 3 — Interview-Ready Java Fullstack (started: 2026-09-09)

### Goal: Build a production-level Task Manager API (Spring Boot + Security + JPA + Next.js)
### Target: Java Fullstack role, 5 years experience
### Session dates: 2026-09-15 to 2026-09-16

---

### Project: Task Manager — Full Stack App
- JWT-authenticated REST API
- Spring Boot 4.0.0 + Spring Security + Spring Data JPA
- PostgreSQL database
- Next.js 16 frontend with protected routes
- Full test coverage (JUnit 5 + Mockito)

### Key Concepts Learned This Session

#### Spring Boot
- `@SpringBootApplication` — entry point, enables component scan + auto-config
- `application.yml` — centralized config for DB, JPA, JWT settings
- `pom.xml` — Maven dependency management, Spring Boot parent manages versions
- `ddl-auto: update` — Hibernate auto-creates/updates tables from entities

#### JPA & Entities
- `@Entity` / `@Table` — maps class to DB table
- `@Id` + `@GeneratedValue` — primary key with auto-increment
- `@Column` — maps field to column, constraints (nullable, unique)
- `@ManyToOne` + `@JoinColumn` — foreign key relationship
- `@Enumerated(EnumType.STRING)` — store enum as string in DB
- `@PrePersist` / `@PreUpdate` — lifecycle hooks for auto timestamps
- `FetchType.LAZY` — don't load related entities until accessed

#### Spring Data JPA (Repositories)
- `JpaRepository<T, ID>` — gives save, findById, findAll, delete for free
- Method name queries — `findByUsername()` → generates SQL automatically
- `Optional<T>` — null-safe return type, forces you to handle "not found"

#### DTOs
- Never expose entities directly to API — use DTOs to shape input/output
- `TaskRequest` — what client sends in (create/update)
- `TaskResponse` — what server sends out (no sensitive fields)
- `RegisterRequest` / `LoginRequest` / `AuthResponse` — auth DTOs

#### Spring Security + JWT
- `SecurityFilterChain` — defines which endpoints are public/protected
- `JwtUtil` — generates and validates JWT tokens (HMAC-SHA256)
- `JwtAuthFilter` — intercepts every request, extracts JWT, sets auth context
- `CustomUserDetailsService` — loads user from DB for Spring Security
- `DaoAuthenticationProvider` — wires UserDetailsService + BCrypt for login
- `BCryptPasswordEncoder` — slow hashing, never store plain text passwords
- `SessionCreationPolicy.STATELESS` — no server sessions, JWT is self-contained
- CSRF disabled — not needed for stateless REST APIs
- CORS configured in `SecurityFilterChain` — must allow OPTIONS preflight

#### Testing
- `@ExtendWith(MockitoExtension.class)` — enables Mockito in unit tests
- `@Mock` — fake object, does nothing by default
- `@InjectMocks` — real object with mocks injected
- `when(...).thenReturn(...)` — set up mock behavior
- `verify(...)` — assert a method was called on a mock
- `assertThrows(...)` — assert exception is thrown
- `@WebMvcTest` — loads only web layer for controller tests
- `@MockitoBean` — registers mock in Spring context (Spring Boot 4+)
- `MockMvc` — simulate HTTP requests without real server
- `@WithMockUser` — inject fake authenticated user in tests

#### Next.js (App Router)
- File-based routing — folder structure = URL structure
- `"use client"` — required for useState, useEffect, event handlers
- `layout.tsx` — wraps all pages, good for global providers
- `middleware.ts` — runs before requests, used for auth redirect
- Axios interceptors — auto-attach JWT to every request
- React Context — global state (auth token, username)
- Cookie + localStorage — localStorage for Axios, cookie for middleware

---

### PHASE 1 — Environment & Project Setup ✅
1. [x] Upgrade to Java 17, verify Maven, install PostgreSQL
2. [x] Generate Spring Boot project — Web, Security, JPA, PostgreSQL, Lombok
3. [x] Understand pom.xml, main class, application.yml, package layout

### PHASE 2 — Database & JPA ✅
4. [x] Set up PostgreSQL DB, configure application.yml, verify connection
5. [x] Create User and Task entities with JPA annotations
6. [x] Create UserRepository and TaskRepository (Spring Data JPA)
7. [x] Implement TaskService with CRUD using repository

### PHASE 3 — Spring Security & JWT ✅
8. [x] Implement JWT utility class (generate, validate, extract claims)
9. [x] Create UserDetailsService implementation
10. [x] Configure SecurityFilterChain and JWT filter
11. [x] Build AuthController — POST /auth/register and POST /auth/login

### PHASE 4 — REST API ✅
12. [x] Build TaskController — GET/POST/PUT/DELETE /tasks
13. [x] Add @ControllerAdvice for global exception handling
14. [x] Use DTOs instead of exposing entities directly

### PHASE 5 — Testing ✅
15. [x] Unit tests for TaskService with JUnit 5 + Mockito
16. [x] Integration tests for TaskController
17. [x] Test auth endpoints and protected routes

### PHASE 6 — Next.js Frontend (React + Next.js 14) ✅
18. [x] Set up Next.js app (App Router), configure Axios, create auth context
19. [x] Build Login and Register pages, store JWT in localStorage
20. [x] Build Task list, create, edit, delete with protected routes

#### Next.js Concepts Covered
- App Router — file-based routing with `app/` directory
- `layout.tsx` — shared layout wrapping all pages
- `page.tsx` — route entry point for each URL
- Client vs Server components (`"use client"` directive)
- Middleware — intercept requests for auth protection
- Axios — HTTP client for calling Spring Boot API
- Auth context — global login state with React Context API
- Protected routes — redirect to login if no JWT token

### PHASE 7 — Advanced Java ✅
21. [x] Concurrency — ExecutorService, synchronized, volatile, deadlocks
22. [x] Design Patterns — Singleton, Factory, Builder, Strategy, Observer
23. [x] JVM — heap/stack, garbage collection, memory leaks

### PHASE 8 — DSA (LeetCode in Java) ✅
24. [x] Arrays & Strings — 5 problems
25. [x] HashMap & Sets — 5 problems
26. [x] Trees & BFS/DFS — 5 problems
27. [x] Dynamic Programming basics — 5 problems

### PHASE 9 — Interview Prep ✅
28. [x] Common Spring Boot interview questions
29. [x] Common Java concurrency interview questions
30. [x] System design — URL shortener, Twitter feed

#### Spring Boot Interview Q&A Covered
- `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`
- Difference between `@Component`, `@Service`, `@Repository`, `@Controller` (all are beans, semantic difference)
- 3 types of dependency injection — constructor (recommended), setter, field
- `@Transactional` — wraps method in DB transaction, auto-rollback on unchecked exception
- `@RestController` vs `@Controller` — REST returns JSON, Controller returns view name
- Spring Security filter chain — `JwtAuthFilter` → validate → `UserDetailsService` → `SecurityContextHolder`
- `@Bean` vs `@Component` — `@Bean` is manual in `@Configuration`, `@Component` is auto-detected
- `ResponseEntity` — controls full HTTP response (status, headers, body)

#### Java Concurrency Q&A Covered
- Race condition — two threads access shared data simultaneously; fix with `synchronized` or `AtomicInteger`
- `synchronized` — lock on object, only one thread executes block at a time
- `volatile` — ensures visibility from main memory, does NOT fix atomicity
- Deadlock — two threads each hold a lock the other needs; prevent by consistent lock ordering
- `ExecutorService` — thread pool, reuses threads; prefer over `new Thread()`
- `Callable<T>` vs `Runnable` — Callable returns value + throws checked exceptions; Runnable does neither
- `Future<T>` — holds result of Callable, `.get()` blocks until ready
- `synchronized` vs `ReentrantLock` — Lock has `tryLock()`, timeout, fairness; synchronized is simpler

#### System Design Covered

**URL Shortener:**
- Base62 encode auto-increment DB ID → 6-char short code
- Redis cache for hot URLs — avoids DB hit on every redirect
- 301 (permanent, browser caches) vs 302 (temporary, server sees every hit — better for analytics)
- Scale: read replicas for reads, partitioning by short code for writes

**Twitter Feed:**
- Fan-out on write — push tweetId to followers' Redis feed on post (fast reads, slow writes)
- Fan-out on read — compute feed at request time (slow reads, fast writes)
- Hybrid: fan-out on write for normal users, fan-out on read for celebrities
- Redis sorted sets for ordered feed, cursor-based pagination (not offset)

---

## Session 4 — AI Agent Project (started: 2026-09-23)

### Goal: Build a ReAct-style AI Agent using Spring Boot
### Project location: `/home/eyeapss/java/ai-agent`

---

### What is a ReAct Agent?
- **ReAct = Reason + Act** — agent thinks about what to do, then does it
- Agent receives a message → detects if a tool is needed → runs tool or responds directly
- Same architecture used by LangChain, AutoGPT, OpenAI Assistants under the hood

### Agent Loop
```
User message → detectTool() → tool found? → YES: tool.execute() → respond
                                           → NO:  respond directly
```

---

### Project: AI Agent — Spring Boot + Tools

#### Architecture
- `Tool` interface — contract every tool implements (`name()`, `description()`, `execute()`)
- `ConversationMemory` — ArrayList-based chat history with `getRecent(n)` for context limiting
- `AgentService` — brain: HashMap of tools, keyword-based tool detection, ReAct loop
- `AgentController` — REST API: `POST /agent/chat`, `GET /agent/history`, `POST /agent/clear`
- `AgentConfig` — registers all tools into AgentService on startup via `ApplicationRunner`
- DTOs: `ChatRequest` (`@NotBlank` message), `ChatResponse` (`@Builder` with timestamp)

#### Built-in Tools
| Tool | Trigger | What it does |
|------|---------|-------------|
| `CalculatorTool` | "calculate", "what is" + numbers | Evaluates math via `ScriptEngineManager` |
| `FileReaderTool` | "read file", "open file" | Reads local file with `BufferedReader` |
| `WebSearchTool` | "search", "look up" | Stub — replace with real API call |

#### New Concepts Learned

**`interface` applied to tools:**
- Tool is an interface — `CalculatorTool`, `FileReaderTool`, `WebSearchTool` all implement it
- Agent only interacts via the interface — doesn't know which specific tool it's calling (polymorphism)

**`HashMap<String, Tool>` for tool registry:**
- Tools stored by name: `{ "calculator" → CalculatorTool, ... }`
- O(1) lookup — `tools.get("calculator")` instantly finds the right tool
- Much faster than iterating a List

**`Collections.unmodifiableList()`:**
- Returns a read-only view of the history list
- Prevents callers from accidentally modifying memory from outside the class

**`subList(from, to)` on ArrayList:**
- Slices a list like `substring` slices a String
- Used in `getRecent(n)` to return only the last N messages

**`ApplicationRunner`:**
- Spring Boot interface — `run()` is called exactly ONCE after context is ready
- Used in `AgentConfig` to register tools before the server accepts requests

**`switch` expression (Java 14+):**
- Returns a value directly — cleaner than traditional switch statement
- Used in `extractToolInput()` to strip trigger words based on tool name

**`ScriptEngineManager`:**
- Java built-in — can evaluate JavaScript expressions
- Used in `CalculatorTool` to evaluate math like `"25 * 48"` → `1200.0`

**`Paths.get().normalize()`:**
- Resolves `..` sequences in file paths — prevents directory traversal security issue
- Used in `FileReaderTool` before reading any file

**`@Configuration` + `@Bean`:**
- `@Configuration` — marks a class as Spring config
- `@Bean` — method that Spring calls once and stores the result as a managed bean

**`ResponseEntity<T>`:**
- Lets you control HTTP status code in controller responses
- `ResponseEntity.ok(body)` → 200 OK with body
- `ResponseEntity.status(400).body(...)` → custom status

**`@Builder.Default` (Lombok):**
- When using `@Builder`, field initializers like `= LocalDateTime.now()` are ignored
- `@Builder.Default` tells Lombok to use the default value when not explicitly set

**Regex in Java:**
- `(?i)` flag in regex = case insensitive
- `\\*` = escaped `*` in regex (special character)
- `.*[0-9+\\-*/].*` = matches any string containing a digit or math operator
- Used in `detectTool()` and `extractToolInput()` to parse user messages

---

### Project Structure
```
ai-agent/
├── pom.xml
├── README.md                          ← full docs with 6 flow diagrams
└── src/main/java/com/eyeapss/aiagent/
    ├── AiAgentApplication.java
    ├── agent/
    │   ├── AgentService.java          ← brain
    │   └── ConversationMemory.java    ← ArrayList history
    ├── tool/
    │   ├── Tool.java                  ← interface
    │   ├── CalculatorTool.java
    │   ├── FileReaderTool.java
    │   └── WebSearchTool.java         ← stub
    ├── controller/
    │   └── AgentController.java
    ├── dto/
    │   ├── ChatRequest.java
    │   └── ChatResponse.java
    └── config/
        └── AgentConfig.java
```

### API Endpoints
| Method | URL | Body | Description |
|--------|-----|------|-------------|
| POST | `/agent/chat` | `{ "message": "..." }` | Chat with agent |
| GET | `/agent/history` | — | Get conversation history |
| POST | `/agent/clear` | — | Clear memory |

### Status
- [x] Project setup — pom.xml, main class, directory structure
- [x] Tool interface
- [x] ConversationMemory
- [x] AgentService with ReAct loop
- [x] CalculatorTool, FileReaderTool, WebSearchTool
- [x] AgentController REST API
- [x] ChatRequest, ChatResponse DTOs
- [x] AgentConfig — tool registration on startup
- [x] application.yml
- [x] README.md with 6 ASCII flow diagrams + detailed comments on all files
- [x] OpenAiService — Java HttpClient, key-ready, stub fallback
- [x] AgentService updated — OpenAI path + keyword fallback path
- [x] Spring Security JWT — User entity, UserRepository, JwtUtil, JwtAuthFilter, SecurityConfig
- [x] AuthController — POST /auth/register, POST /auth/login
- [x] H2 in-memory database (no PostgreSQL setup needed)
- [x] Unit tests — AgentServiceTest (8 tests), AgentControllerTest (8 tests) — 16/16 passing
- [x] Next.js chat UI — login page, chat page, Axios interceptor, AuthContext
- [ ] Connect real OpenAI API key (set OPENAI_API_KEY env var when ready)

---

### New Concepts Learned (Session 4 — Extended)

#### OpenAI API Integration (Java HttpClient)
- `HttpClient.newBuilder().connectTimeout().build()` — built-in Java 11+ HTTP client
- `HttpRequest.newBuilder().uri().header().POST().timeout().build()` — builds an HTTP request
- `httpClient.send(request, BodyHandlers.ofString())` — sends request, gets String response
- `ObjectMapper.createObjectNode()` / `createArrayNode()` — build JSON programmatically
- `objectMapper.readTree(body).path("choices").get(0).path("message").path("content")` — parse JSON response
- `@Value("${agent.openai-api-key:}")` — inject config with empty default if not set

#### Spring Security JWT (applied to AI Agent)
- `@EnableWebSecurity` — activates Spring Security
- `SecurityFilterChain` — defines public vs protected routes, session policy, CORS
- `SessionCreationPolicy.STATELESS` — no server sessions, JWT only
- `CorsConfigurationSource` — allow Next.js (localhost:3000) to call the API
- `DaoAuthenticationProvider` — wires UserDetailsService + BCrypt for login
- `UsernamePasswordAuthenticationToken` — Spring's auth object, stored in SecurityContext
- `OncePerRequestFilter` — base class for JWT filter, runs exactly once per request
- H2 in-memory DB + `ddl-auto: create-drop` — no DB setup, great for dev/testing

#### Unit Testing Patterns
- `@SpringBootTest + @AutoConfigureMockMvc` — full context + MockMvc (more reliable than @WebMvcTest for complex security)
- `@MockBean AgentService` — replaces real bean in Spring context with a mock
- `@WithMockUser` — injects fake authenticated user, bypasses JWT filter
- `status().is4xxClientError()` — flexible check for 401/403 (Spring returns 403 by default without credentials)
- `jsonPath("$.response")` — assert specific JSON field value in response body
- `verify(mock, times(1)).method()` — assert a method was called exactly once

#### Next.js (Auth + Chat UI)
- `axios.interceptors.request` — auto-attach JWT to every request header
- `axios.interceptors.response` — intercept 401/403 and redirect to login
- `useContext + createContext` — React global state for auth (token, username)
- `useEffect + router.push` — client-side redirect when auth state changes
- `useRef` — scroll chat to bottom on new messages (`scrollIntoView`)
- `localStorage` — persist JWT between page refreshes
- Optimistic UI update — add user message to screen before server responds
