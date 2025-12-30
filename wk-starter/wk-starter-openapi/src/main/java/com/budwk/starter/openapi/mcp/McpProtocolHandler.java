package com.budwk.starter.openapi.mcp;

import com.budwk.starter.openapi.mcp.model.JsonRpcRequest;
import com.budwk.starter.openapi.mcp.model.JsonRpcResponse;
import com.budwk.starter.openapi.mcp.model.JsonRpcResponse.JsonRpcError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.HashMap;
import java.util.Map;

/**
 * MCP Protocol Handler - Handles JSON-RPC 2.0 protocol
 * 
 * @author wizzer@qq.com
 */
public class McpProtocolHandler {
    private static final Log log = Logs.get();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // JSON-RPC 2.0 Error Codes
    public static final int PARSE_ERROR = -32700;
    public static final int INVALID_REQUEST = -32600;
    public static final int METHOD_NOT_FOUND = -32601;
    public static final int INVALID_PARAMS = -32602;
    public static final int INTERNAL_ERROR = -32603;

    private final McpToolsHandler toolsHandler;
    private final McpResourcesHandler resourcesHandler;
    private final McpPromptsHandler promptsHandler;
    private final String serverName;
    private final String serverVersion;

    public McpProtocolHandler(McpToolsHandler toolsHandler,
            McpResourcesHandler resourcesHandler,
            McpPromptsHandler promptsHandler,
            String serverName,
            String serverVersion) {
        this.toolsHandler = toolsHandler;
        this.resourcesHandler = resourcesHandler;
        this.promptsHandler = promptsHandler;
        this.serverName = serverName;
        this.serverVersion = serverVersion;
    }

    /**
     * Parse JSON-RPC 2.0 request from JSON string
     */
    public JsonRpcRequest parseRequest(String json) throws Exception {
        return objectMapper.readValue(json, JsonRpcRequest.class);
    }

    /**
     * Handle JSON-RPC 2.0 request and return response
     */
    public JsonRpcResponse handleRequest(JsonRpcRequest request) {
        try {
            // Validate JSON-RPC version
            if (!"2.0".equals(request.getJsonrpc())) {
                return createErrorResponse(INVALID_REQUEST, "Invalid JSON-RPC version", request.getId());
            }

            // Validate method
            if (Strings.isBlank(request.getMethod())) {
                return createErrorResponse(INVALID_REQUEST, "Method is required", request.getId());
            }

            // Route to appropriate handler
            Object result = routeRequest(request);
            return new JsonRpcResponse(result, request.getId());

        } catch (Exception e) {
            log.error("Error handling MCP request", e);
            return createErrorResponse(INTERNAL_ERROR, e.getMessage(), request.getId());
        }
    }

    /**
     * Route request to appropriate handler based on method
     */
    private Object routeRequest(JsonRpcRequest request) throws Exception {
        String method = request.getMethod();

        switch (method) {
            case "initialize":
                return handleInitialize(request);
            case "tools/list":
                return toolsHandler.listTools();
            case "tools/call":
                return toolsHandler.callTool(request.getParams());
            case "resources/list":
                return resourcesHandler.listResources();
            case "resources/read":
                return resourcesHandler.readResource(request.getParams());
            case "prompts/list":
                return promptsHandler.listPrompts();
            case "prompts/get":
                return promptsHandler.getPrompt(request.getParams());
            default:
                throw new Exception("Method not found: " + method);
        }
    }

    /**
     * Handle initialize request
     */
    private Map<String, Object> handleInitialize(JsonRpcRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", "2024-11-05");

        Map<String, Object> serverInfo = new HashMap<>();
        serverInfo.put("name", serverName);
        serverInfo.put("version", serverVersion);
        result.put("serverInfo", serverInfo);

        Map<String, Object> capabilities = new HashMap<>();

        // Tools capability
        Map<String, Object> tools = new HashMap<>();
        tools.put("listChanged", false);
        capabilities.put("tools", tools);

        // Resources capability
        Map<String, Object> resources = new HashMap<>();
        resources.put("subscribe", false);
        resources.put("listChanged", false);
        capabilities.put("resources", resources);

        // Prompts capability
        Map<String, Object> prompts = new HashMap<>();
        prompts.put("listChanged", false);
        capabilities.put("prompts", prompts);

        result.put("capabilities", capabilities);

        return result;
    }

    /**
     * Create error response
     */
    public JsonRpcResponse createErrorResponse(int code, String message, Object id) {
        return new JsonRpcResponse(new JsonRpcError(code, message), id);
    }

    /**
     * Serialize response to JSON string
     */
    public String serializeResponse(JsonRpcResponse response) throws Exception {
        return objectMapper.writeValueAsString(response);
    }
}
