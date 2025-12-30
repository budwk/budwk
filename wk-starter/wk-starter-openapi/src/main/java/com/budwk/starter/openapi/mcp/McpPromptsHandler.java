package com.budwk.starter.openapi.mcp;

import com.budwk.starter.openapi.mcp.model.McpPrompt;
import com.budwk.starter.openapi.mcp.model.McpPrompt.PromptArgument;
import io.swagger.v3.oas.models.OpenAPI;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.*;

/**
 * MCP Prompts Handler - Provides reusable prompt templates for API usage
 * 
 * @author wizzer@qq.com
 */
public class McpPromptsHandler {
    private static final Log log = Logs.get();
    private final OpenAPI openAPI;
    private List<McpPrompt> prompts;

    public McpPromptsHandler(OpenAPI openAPI) {
        this.openAPI = openAPI;
        this.prompts = new ArrayList<>();
        buildPrompts();
    }

    /**
     * Build prompt templates
     */
    private void buildPrompts() {
        // Authentication prompt
        prompts.add(new McpPrompt(
                "how_to_authenticate",
                "Explains how to authenticate with the API using tokens",
                new ArrayList<>()));

        // API overview prompt
        prompts.add(new McpPrompt(
                "api_overview",
                "Provides an overview of all available API operations",
                new ArrayList<>()));

        // Operation-specific prompt
        prompts.add(new McpPrompt(
                "how_to_call_operation",
                "Provides detailed instructions on how to call a specific API operation",
                Arrays.asList(
                        new PromptArgument("operationId", "The ID of the operation to get help for", true))));

        // Tag-based prompt
        prompts.add(new McpPrompt(
                "explore_tag",
                "Explores all operations under a specific tag/category",
                Arrays.asList(
                        new PromptArgument("tag", "The tag name to explore", true))));

        log.infof("MCP: Built %d prompts", prompts.size());
    }

    /**
     * List all available prompts
     */
    public Map<String, Object> listPrompts() {
        Map<String, Object> result = new HashMap<>();
        result.put("prompts", prompts);
        return result;
    }

    /**
     * Get a specific prompt with arguments
     */
    public Map<String, Object> getPrompt(Object params) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> paramsMap = (Map<String, Object>) params;

        String name = (String) paramsMap.get("name");
        @SuppressWarnings("unchecked")
        Map<String, String> arguments = (Map<String, String>) paramsMap.get("arguments");

        if (Strings.isBlank(name)) {
            throw new Exception("Prompt name is required");
        }

        String promptText = generatePromptText(name, arguments);

        Map<String, Object> result = new HashMap<>();
        result.put("description", getPromptDescription(name));
        result.put("messages", Arrays.asList(
                Map.of(
                        "role", "user",
                        "content", Map.of(
                                "type", "text",
                                "text", promptText))));

        return result;
    }

    /**
     * Get prompt description by name
     */
    private String getPromptDescription(String name) {
        for (McpPrompt prompt : prompts) {
            if (prompt.getName().equals(name)) {
                return prompt.getDescription();
            }
        }
        return "Prompt: " + name;
    }

    /**
     * Generate prompt text based on name and arguments
     */
    private String generatePromptText(String name, Map<String, String> arguments) throws Exception {
        switch (name) {
            case "how_to_authenticate":
                return generateAuthenticationPrompt();
            case "api_overview":
                return generateApiOverviewPrompt();
            case "how_to_call_operation":
                String operationId = arguments != null ? arguments.get("operationId") : null;
                if (Strings.isBlank(operationId)) {
                    throw new Exception("operationId argument is required");
                }
                return generateOperationPrompt(operationId);
            case "explore_tag":
                String tag = arguments != null ? arguments.get("tag") : null;
                if (Strings.isBlank(tag)) {
                    throw new Exception("tag argument is required");
                }
                return generateTagPrompt(tag);
            default:
                throw new Exception("Unknown prompt: " + name);
        }
    }

    /**
     * Generate authentication prompt
     */
    private String generateAuthenticationPrompt() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("# API Authentication Guide\n\n");
        prompt.append("This API uses token-based authentication. To authenticate:\n\n");
        prompt.append("1. Include the authentication token in the request header\n");
        prompt.append("2. Header name: `wk-user-token` (or as configured)\n");
        prompt.append("3. Header value: Your authentication token\n\n");
        prompt.append("Example:\n");
        prompt.append("```\n");
        prompt.append("curl -H \"wk-user-token: YOUR_TOKEN\" https://api.example.com/endpoint\n");
        prompt.append("```\n\n");
        prompt.append("Some operations may require specific permissions or roles.\n");
        return prompt.toString();
    }

    /**
     * Generate API overview prompt
     */
    private String generateApiOverviewPrompt() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("# API Overview\n\n");

        if (openAPI != null && openAPI.getInfo() != null) {
            prompt.append("**").append(openAPI.getInfo().getTitle()).append("**\n");
            prompt.append("Version: ").append(openAPI.getInfo().getVersion()).append("\n\n");
        }

        if (openAPI != null && openAPI.getTags() != null && !openAPI.getTags().isEmpty()) {
            prompt.append("## Available API Categories\n\n");
            openAPI.getTags().forEach(tag -> {
                prompt.append("- **").append(tag.getName()).append("**");
                if (Strings.isNotBlank(tag.getDescription())) {
                    prompt.append(": ").append(tag.getDescription());
                }
                prompt.append("\n");
            });
        }

        prompt.append("\nUse the `explore_tag` prompt to see operations in each category.\n");
        return prompt.toString();
    }

    /**
     * Generate operation-specific prompt
     */
    private String generateOperationPrompt(String operationId) throws Exception {
        StringBuilder prompt = new StringBuilder();
        prompt.append("# How to Call: ").append(operationId).append("\n\n");

        // This would need to look up the operation in the OpenAPI spec
        // and provide detailed usage instructions
        prompt.append("Operation details would be provided here based on the OpenAPI specification.\n");
        prompt.append("This includes: HTTP method, path, parameters, request body, and response format.\n");

        return prompt.toString();
    }

    /**
     * Generate tag exploration prompt
     */
    private String generateTagPrompt(String tag) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("# Exploring: ").append(tag).append("\n\n");

        if (openAPI != null && openAPI.getPaths() != null) {
            prompt.append("## Available Operations\n\n");
            openAPI.getPaths().forEach((path, pathItem) -> {
                // Check each operation in the path
                if (pathItem.getGet() != null && hasTag(pathItem.getGet().getTags(), tag)) {
                    prompt.append("- **GET ").append(path).append("**: ")
                            .append(pathItem.getGet().getSummary()).append("\n");
                }
                if (pathItem.getPost() != null && hasTag(pathItem.getPost().getTags(), tag)) {
                    prompt.append("- **POST ").append(path).append("**: ")
                            .append(pathItem.getPost().getSummary()).append("\n");
                }
                if (pathItem.getPut() != null && hasTag(pathItem.getPut().getTags(), tag)) {
                    prompt.append("- **PUT ").append(path).append("**: ")
                            .append(pathItem.getPut().getSummary()).append("\n");
                }
                if (pathItem.getDelete() != null && hasTag(pathItem.getDelete().getTags(), tag)) {
                    prompt.append("- **DELETE ").append(path).append("**: ")
                            .append(pathItem.getDelete().getSummary()).append("\n");
                }
            });
        }

        return prompt.toString();
    }

    /**
     * Check if tags list contains the specified tag
     */
    private boolean hasTag(List<String> tags, String targetTag) {
        return tags != null && tags.contains(targetTag);
    }
}
