package com.campusconnect.campusconnect.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key:}")
    private String key;

    @Value("${gemini.model:gemini-3.6-flash}")
    private String model;

    private final ObjectMapper mapper = new ObjectMapper();

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(4))
            .build();

    private final List<ChatMessage> conversation = new ArrayList<>();

    private static class ChatMessage {

        String role;
        String text;

        ChatMessage(String role, String text) {
            this.role = role;
            this.text = text;
        }
    }

    public synchronized String ask(String prompt) throws Exception {

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(
                    "Gemini is not configured. Set GEMINI_API_KEY and restart CampusConnect."
            );
        }

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be empty.");
        }

        String cleanPrompt = prompt.trim();

        String cleanModel = model;

        if (cleanModel == null || cleanModel.isBlank()) {
            cleanModel = "gemini-3.6-flash";
        }

        cleanModel = cleanModel.trim()
                .replaceFirst("^models/", "");

        // Add user's message
        conversation.add(
                new ChatMessage("user", cleanPrompt)
        );

        // Keep the latest 20 messages
        if (conversation.size() > 20) {
            conversation.subList(
                    0,
                    conversation.size() - 20
            ).clear();
        }

        ObjectNode body = mapper.createObjectNode();

        ArrayNode contents = mapper.createArrayNode();

        // Build conversation history
        for (ChatMessage message : conversation) {

            ObjectNode content = mapper.createObjectNode();

            content.put(
                    "role",
                    message.role.equals("assistant")
                            ? "model"
                            : "user"
            );

            ArrayNode parts = mapper.createArrayNode();

            ObjectNode part = mapper.createObjectNode();
            part.put("text", message.text);

            parts.add(part);

            content.set("parts", parts);

            contents.add(content);
        }

        body.set("contents", contents);

        // Generation settings
        ObjectNode generationConfig =
                mapper.createObjectNode();

        generationConfig.put("temperature", 0.4);
        generationConfig.put("maxOutputTokens", 1024);

        body.set(
                "generationConfig",
                generationConfig
        );

        // CampusConnect AI instructions
        ObjectNode systemInstruction =
                mapper.createObjectNode();

        ArrayNode systemParts =
                mapper.createArrayNode();

        ObjectNode systemPart =
                mapper.createObjectNode();

        systemPart.put(
                "text",
                """
                You are CampusConnect AI, the AI assistant
                inside the CampusConnect university social
                application.

                Be helpful, clear and conversational.

                Remember the previous messages in the
                conversation.

                If the user says things like:
                "explain further",
                "continue",
                "tell me more",
                "what about that",
                or similar phrases, use the previous
                conversation to understand what they mean.

                Do not repeatedly say:
                "I'd be happy to help"
                or similar filler.

                Answer the user's actual question directly.

                When explaining academic topics, explain
                them simply first, then provide more detail
                when requested.

                When relevant, you can discuss universities,
                campus life, students, opportunities,
                events, communities and education.
                """
        );

        systemParts.add(systemPart);

        systemInstruction.set(
                "parts",
                systemParts
        );

        body.set(
                "systemInstruction",
                systemInstruction
        );

        // Gemini API URL
        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + cleanModel
                        + ":generateContent?key="
                        + key.trim();

        HttpRequest request =
                HttpRequest.newBuilder(
                        URI.create(url)
                )
                .header(
                        "Content-Type",
                        "application/json"
                )
                .timeout(
                        Duration.ofSeconds(90)
                )
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                body.toString()
                        )
                )
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        JsonNode root =
                mapper.readTree(response.body());

        // Handle API errors
        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            String apiMessage =
                    root.at("/error/message")
                            .asText("");

            if (apiMessage.isBlank()) {
                apiMessage =
                        "Gemini API returned HTTP "
                                + response.statusCode();
            }

            // Remove failed user message
            if (!conversation.isEmpty()) {
                conversation.remove(
                        conversation.size() - 1
                );
            }

            throw new IllegalArgumentException(
                    "Gemini API error: "
                            + apiMessage
            );
        }

        JsonNode candidates =
                root.path("candidates");

        if (!candidates.isArray() ||
                candidates.isEmpty()) {

            String blockReason =
                    root.at(
                            "/promptFeedback/blockReason"
                    ).asText("");

            if (!blockReason.isBlank()) {

                if (!conversation.isEmpty()) {
                    conversation.remove(
                            conversation.size() - 1
                    );
                }

                throw new IllegalArgumentException(
                        "Gemini blocked this request: "
                                + blockReason
                );
            }

            if (!conversation.isEmpty()) {
                conversation.remove(
                        conversation.size() - 1
                );
            }

            throw new IllegalArgumentException(
                    "Gemini returned no response. "
                            + "Check the API key and selected model."
            );
        }

        JsonNode candidate =
                candidates.get(0);

        JsonNode parts =
                candidate
                        .path("content")
                        .path("parts");

        if (!parts.isArray() ||
                parts.isEmpty()) {

            String finishReason =
                    candidate
                            .path("finishReason")
                            .asText("");

            if (!conversation.isEmpty()) {
                conversation.remove(
                        conversation.size() - 1
                );
            }

            throw new IllegalArgumentException(
                    finishReason.isBlank()
                            ? "Gemini returned an empty response."
                            : "Gemini finished without text: "
                                    + finishReason
            );
        }

        StringBuilder answer =
                new StringBuilder();

        for (JsonNode part : parts) {

            String text =
                    part.path("text")
                            .asText("");

            if (!text.isBlank()) {

                if (answer.length() > 0) {
                    answer.append("\n");
                }

                answer.append(text);
            }
        }

        if (answer.length() == 0) {

            if (!conversation.isEmpty()) {
                conversation.remove(
                        conversation.size() - 1
                );
            }

            throw new IllegalArgumentException(
                    "Gemini returned no text."
            );
        }

        String finalAnswer =
                answer.toString().trim();

        // Save Gemini's answer
        conversation.add(
                new ChatMessage(
                        "assistant",
                        finalAnswer
                )
        );

        // Keep latest 20 messages
        if (conversation.size() > 20) {
            conversation.subList(
                    0,
                    conversation.size() - 20
            ).clear();
        }

        return finalAnswer;
    }

    public synchronized void clearConversation() {
        conversation.clear();
    }
}

