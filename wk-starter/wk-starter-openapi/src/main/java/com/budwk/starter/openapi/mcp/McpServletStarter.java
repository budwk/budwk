package com.budwk.starter.openapi.mcp;

import com.budwk.starter.openapi.mcp.model.JsonRpcRequest;
import com.budwk.starter.openapi.mcp.model.JsonRpcResponse;
import io.swagger.v3.oas.models.OpenAPI;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * MCP Servlet Starter - Handles MCP protocol requests
 * 
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
public class McpServletStarter extends HttpServlet implements WebServletFace {
    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    protected static final String PRE = "openapi.mcp.";

    @PropDoc(value = "MCP 是否启用", defaultValue = "false", type = "boolean")
    public static final String PROP_ENABLE = PRE + "enable";

    @PropDoc(value = "MCP 服务器名称", defaultValue = "BudWk API Server")
    public static final String PROP_NAME = PRE + "name";

    @PropDoc(value = "MCP 服务器版本", defaultValue = "1.0.0")
    public static final String PROP_VERSION = PRE + "version";

    private McpProtocolHandler protocolHandler;

    public void init(ServletConfig config) throws ServletException {
        try {
            // Get OpenAPI instance from servlet context
            OpenAPI openAPI = (OpenAPI) config.getServletContext().getAttribute("openapi");
            if (openAPI == null) {
                log.warn("OpenAPI not found in servlet context, MCP resources will be limited");
            }

            // Get configuration
            String scanPackage = conf.get("openapi.scanner.package", appContext.getPackage());
            String serverName = conf.get(PROP_NAME, "BudWk API Server");
            String serverVersion = conf.get(PROP_VERSION, "1.0.0");

            // Initialize handlers
            McpToolsHandler toolsHandler = new McpToolsHandler(scanPackage);
            McpResourcesHandler resourcesHandler = new McpResourcesHandler(openAPI);
            McpPromptsHandler promptsHandler = new McpPromptsHandler(openAPI);

            // Initialize protocol handler
            protocolHandler = new McpProtocolHandler(
                    toolsHandler,
                    resourcesHandler,
                    promptsHandler,
                    serverName,
                    serverVersion);

            log.info("MCP Servlet initialized successfully");
        } catch (Exception e) {
            log.error("Error initializing MCP servlet", e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Read request body
            String requestBody = Streams.readAndClose(Streams.utf8r(req.getInputStream()));

            // Parse JSON-RPC request
            JsonRpcRequest request = protocolHandler.parseRequest(requestBody);

            // Handle request
            JsonRpcResponse response = protocolHandler.handleRequest(request);

            // Write response
            resp.setStatus(200);
            PrintWriter pw = resp.getWriter();
            pw.write(protocolHandler.serializeResponse(response));
            pw.close();

        } catch (Exception e) {
            log.error("Error processing MCP request", e);
            resp.setStatus(500);
            PrintWriter pw = resp.getWriter();
            JsonRpcResponse errorResponse = protocolHandler.createErrorResponse(
                    McpProtocolHandler.INTERNAL_ERROR,
                    "Internal server error: " + e.getMessage(),
                    null);
            try {
                pw.write(protocolHandler.serializeResponse(errorResponse));
            } catch (Exception ex) {
                pw.write(
                        "{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-32603,\"message\":\"Internal error\"},\"id\":null}");
            }
            pw.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(200);

        PrintWriter pw = resp.getWriter();
        pw.write("{\"status\":\"MCP server is running\",\"protocol\":\"JSON-RPC 2.0\"}");
        pw.close();
    }

    @Override
    public String getName() {
        return "mcp";
    }

    @Override
    public String getPathSpec() {
        return "/mcp";
    }

    @Override
    public Servlet getServlet() {
        if (!conf.getBoolean(PROP_ENABLE, false)) {
            return null;
        }
        return this;
    }
}
