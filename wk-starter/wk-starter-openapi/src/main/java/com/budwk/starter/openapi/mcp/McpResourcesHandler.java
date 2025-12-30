package com.budwk.starter.openapi.mcp;

import com.budwk.starter.openapi.mcp.model.McpResource;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.tags.Tag;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.*;

/**
 * MCP Resources Handler - Exposes API documentation as MCP resources
 * 
 * @author wizzer@qq.com
 */
public class McpResourcesHandler {
    private static final Log log = Logs.get();
    private final OpenAPI openAPI;
    private List<McpResource> resources;

    public McpResourcesHandler(OpenAPI openAPI) {
        this.openAPI = openAPI;
        this.resources = new ArrayList<>();
        buildResources();
    }

    /**
     * Build resource definitions from OpenAPI spec
     */
    private void buildResources() {
        // Full OpenAPI schema resource
        resources.add(new McpResource(
                "openapi://schema",
                "Full API Schema",
                "Complete OpenAPI 3.0 specification for all available APIs",
                "application/json"));

        // Tag-based resources
        if (openAPI != null && openAPI.getTags() != null) {
            for (Tag tag : openAPI.getTags()) {
                resources.add(new McpResource(
                        "openapi://tags/" + tag.getName(),
                        tag.getName() + " APIs",
                        "API operations for " + tag.getName(),
                        "application/json"));
            }
        }

        // Operation-based resources
        if (openAPI != null && openAPI.getPaths() != null) {
            openAPI.getPaths().forEach((path, pathItem) -> {
                extractOperations(path, pathItem).forEach((method, operation) -> {
                    String operationId = operation.getOperationId();
                    if (Strings.isNotBlank(operationId)) {
                        resources.add(new McpResource(
                                "openapi://operations/" + operationId,
                                operationId,
                                operation.getSummary() != null ? operation.getSummary()
                                        : "API operation: " + operationId,
                                "application/json"));
                    }
                });
            });
        }

        log.infof("MCP: Built %d resources", resources.size());
    }

    /**
     * Extract operations from path item
     */
    private Map<String, Operation> extractOperations(String path, PathItem pathItem) {
        Map<String, Operation> operations = new HashMap<>();

        if (pathItem.getGet() != null) {
            operations.put("GET", pathItem.getGet());
        }
        if (pathItem.getPost() != null) {
            operations.put("POST", pathItem.getPost());
        }
        if (pathItem.getPut() != null) {
            operations.put("PUT", pathItem.getPut());
        }
        if (pathItem.getDelete() != null) {
            operations.put("DELETE", pathItem.getDelete());
        }
        if (pathItem.getPatch() != null) {
            operations.put("PATCH", pathItem.getPatch());
        }

        return operations;
    }

    /**
     * List all available resources
     */
    public Map<String, Object> listResources() {
        Map<String, Object> result = new HashMap<>();
        result.put("resources", resources);
        return result;
    }

    /**
     * Read a specific resource by URI
     */
    public Map<String, Object> readResource(Object params) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> paramsMap = (Map<String, Object>) params;

        String uri = (String) paramsMap.get("uri");
        if (Strings.isBlank(uri)) {
            throw new Exception("Resource URI is required");
        }

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();

        if ("openapi://schema".equals(uri)) {
            // Return full OpenAPI schema
            contents.add(Map.of(
                    "uri", uri,
                    "mimeType", "application/json",
                    "text", io.swagger.v3.core.util.Json.pretty(openAPI)));
        } else if (uri.startsWith("openapi://tags/")) {
            // Return operations for a specific tag
            String tagName = uri.substring("openapi://tags/".length());
            String tagOperations = getOperationsByTag(tagName);
            contents.add(Map.of(
                    "uri", uri,
                    "mimeType", "application/json",
                    "text", tagOperations));
        } else if (uri.startsWith("openapi://operations/")) {
            // Return specific operation details
            String operationId = uri.substring("openapi://operations/".length());
            String operationDetails = getOperationDetails(operationId);
            contents.add(Map.of(
                    "uri", uri,
                    "mimeType", "application/json",
                    "text", operationDetails));
        } else {
            throw new Exception("Resource not found: " + uri);
        }

        result.put("contents", contents);
        return result;
    }

    /**
     * Get operations by tag
     */
    private String getOperationsByTag(String tagName) throws Exception {
        Map<String, Object> tagOps = new HashMap<>();
        tagOps.put("tag", tagName);
        List<Map<String, Object>> operations = new ArrayList<>();

        if (openAPI != null && openAPI.getPaths() != null) {
            openAPI.getPaths().forEach((path, pathItem) -> {
                extractOperations(path, pathItem).forEach((method, operation) -> {
                    if (operation.getTags() != null && operation.getTags().contains(tagName)) {
                        Map<String, Object> opInfo = new HashMap<>();
                        opInfo.put("operationId", operation.getOperationId());
                        opInfo.put("method", method);
                        opInfo.put("path", path);
                        opInfo.put("summary", operation.getSummary());
                        opInfo.put("description", operation.getDescription());
                        operations.add(opInfo);
                    }
                });
            });
        }

        tagOps.put("operations", operations);
        return io.swagger.v3.core.util.Json.pretty(tagOps);
    }

    /**
     * Get operation details by operation ID
     */
    private String getOperationDetails(String operationId) throws Exception {
        if (openAPI != null && openAPI.getPaths() != null) {
            for (Map.Entry<String, PathItem> entry : openAPI.getPaths().entrySet()) {
                String path = entry.getKey();
                PathItem pathItem = entry.getValue();

                Map<String, Operation> operations = extractOperations(path, pathItem);
                for (Map.Entry<String, Operation> opEntry : operations.entrySet()) {
                    Operation operation = opEntry.getValue();
                    if (operationId.equals(operation.getOperationId())) {
                        Map<String, Object> opDetails = new HashMap<>();
                        opDetails.put("operationId", operationId);
                        opDetails.put("method", opEntry.getKey());
                        opDetails.put("path", path);
                        opDetails.put("summary", operation.getSummary());
                        opDetails.put("description", operation.getDescription());
                        opDetails.put("parameters", operation.getParameters());
                        opDetails.put("requestBody", operation.getRequestBody());
                        opDetails.put("responses", operation.getResponses());
                        opDetails.put("security", operation.getSecurity());
                        return io.swagger.v3.core.util.Json.pretty(opDetails);
                    }
                }
            }
        }

        throw new Exception("Operation not found: " + operationId);
    }
}
