package com.eyeapss.aiagent.tool;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * FileReaderTool — reads a local file and returns its content as a String.
 *
 * EXAMPLES:
 *   Input:  "/home/eyeapss/java/output.txt"
 *   Output: (the file's contents, up to 100 lines)
 *
 *   Input:  "/nonexistent/file.txt"
 *   Output: "Error: file not found at path: /nonexistent/file.txt"
 *
 * FILE I/O CONCEPTS USED (from Session 2):
 *   - BufferedReader      — reads text file line by line efficiently
 *   - FileReader          — connects BufferedReader to an actual file
 *   - try-with-resources  — auto-closes the file handle even if an exception occurs
 *   - StringBuilder       — efficiently builds the content string in a loop
 *   - IOException         — checked exception thrown when file operations fail
 *
 * NEW CONCEPTS:
 *   - java.nio.file.Path  — modern way to represent file paths (Java 7+)
 *   - Files.exists()      — checks if the path exists on the filesystem
 *   - Paths.get().normalize() — resolves ".." in paths to prevent directory traversal
 */
@Component
public class FileReaderTool implements Tool {

    @Override
    public String name() {
        return "file_reader";
    }

    @Override
    public String description() {
        return "Reads a local file and returns its content. Input: absolute file path.";
    }

    /**
     * Reads the file at the given path and returns its content.
     *
     * SAFETY CHECKS (before reading):
     *   1. Normalize the path — removes ".." sequences that could escape the directory
     *      e.g. "/home/user/../etc/passwd" → "/etc/passwd" (we'd catch this and reject)
     *   2. Check file exists — return friendly error instead of exception
     *   3. Check file is readable — some files may have permission restrictions
     *   4. Check it's not a directory — directories can't be read as text
     *
     * READING (after safety checks pass):
     *   Same pattern as Session 2 File I/O:
     *     try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
     *       while ((line = reader.readLine()) != null) { ... }
     *     }
     *   try-with-resources automatically closes the reader when done.
     *
     *   We cap at 100 lines to avoid reading huge files into memory.
     *
     * @param input absolute file path as a String
     * @return file content as String, or an error message
     */
    @Override
    public String execute(String input) {
        String filePath = input.trim(); // remove any leading/trailing whitespace

        // ── SAFETY CHECKS ────────────────────────────────────────────────────

        // Paths.get() creates a Path object from the string
        // .normalize() resolves ".." and "." in the path
        // e.g. "/home/user/../etc" becomes "/etc"
        Path path = Paths.get(filePath).normalize();

        // Check 1: file must exist
        if (!Files.exists(path)) {
            return "Error: file not found at path: " + filePath;
        }

        // Check 2: file must be readable (not permission-denied)
        if (!Files.isReadable(path)) {
            return "Error: file is not readable: " + filePath;
        }

        // Check 3: must be a file, not a directory
        if (Files.isDirectory(path)) {
            return "Error: path is a directory, not a file: " + filePath;
        }

        // ── FILE READING ─────────────────────────────────────────────────────

        // StringBuilder: efficient string building in a loop
        // Using + in a loop creates many temporary String objects (String is immutable)
        // StringBuilder reuses one internal buffer → much faster for loops
        StringBuilder content = new StringBuilder();

        // try-with-resources — FileReader + BufferedReader auto-closed after the block
        // If an exception happens inside, the file is still properly closed
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineCount = 0;

            // reader.readLine() returns the next line, or null at end of file
            // We also cap at 100 lines to prevent reading huge files
            while ((line = reader.readLine()) != null && lineCount < 100) {
                content.append(line).append("\n"); // append line + newline character
                lineCount++;
            }

            // .trim() removes the final trailing newline we added
            return content.toString().trim();

        } catch (IOException e) {
            // IOException is thrown when the file can't be read (disk error, permissions, etc.)
            // We catch it and return a friendly error string instead of crashing the app
            return "Error reading file: " + e.getMessage();
        }
    }
}
