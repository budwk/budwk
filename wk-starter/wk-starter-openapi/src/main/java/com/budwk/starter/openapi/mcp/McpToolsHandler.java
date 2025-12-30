package com.budwk.starter.openapi.mcp;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.openapi.mcp.model.McpTool;
import com.budwk.starter.openapi.utils.NutzReaderUtils;
import io.swagger.v3.core.util.ReflectionUtils;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.resource.Scans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * MCP Tools Handler - Exposes API operations as MCP tools
 * 
 * @author wizzer@qq.com
 */
public class McpToolsHandler {
    private static final Log log = Logs.get();
    private final String scanPackage;
    private List<McpTool> tools;
    private Map<String, ToolMetadata> toolMetadataMap;

    public McpToolsHandler(String scanPackage) {
        this.scanPackage = scanPackage;
        this.tools = new ArrayList<>();
        this.toolMetadataMap = new HashMap<>();
        scanAndBuildTools();
    }

    /**
     * Scan packages and build tool definitions
     */
    private void scanAndBuildTools() {
        try {
            for (Class<?> cls : Scans.me().scanPackage(scanPackage)) {
                ApiDefinition apiDefinition = ReflectionUtils.getAnnotation(cls, ApiDefinition.class);
                if (apiDefinition == null) {
                    continue;
                }

                At apiPath = ReflectionUtils.getAnnotation(cls, At.class);
                Method[] methods = cls.getMethods();

                for (Method method : methods) {
                    ApiOperation apiOperation = ReflectionUtils.getAnnotation(method, ApiOperation.class);
                    if (apiOperation == null) {
                        continue;
                    }

                    At methodPath = ReflectionUtils.getAnnotation(method, At.class);
                    String httpMethod = NutzReaderUtils.extractOperationMethod(method);

                    if (Strings.isBlank(httpMethod)) {
                        continue;
                    }

                    // Build tool definition
                    String toolName = buildToolName(cls, method);
                    String description = buildToolDescription(apiOperation, apiDefinition.tag());
                    Map<String, Object> inputSchema = buildInputSchema(method);

                    McpTool tool = new McpTool(toolName, description, inputSchema);
                    tools.add(tool);

                    // Store metadata for tool invocation
                    ToolMetadata metadata = new ToolMetadata();
                    metadata.clazz = cls;
                    metadata.method = method;
                    metadata.httpMethod = httpMethod;
                    metadata.path = buildPath(apiPath, methodPath);
                    toolMetadataMap.put(toolName, metadata);
                }
            }
            log.infof("MCP: Loaded %d tools from package: %s", tools.size(), scanPackage);
        } catch (Exception e) {
            log.error("Error scanning packages for MCP tools", e);
        }
    }

    /**
     * Build tool name from class and method
     */
    private String buildToolName(Class<?> cls, Method method) {
        String className = cls.getSimpleName().replace("Module", "").replace("Controller", "");
        String methodName = method.getName();
        return className + "_" + methodName;
    }

    /**
     * Build tool description
     */
    private String buildToolDescription(ApiOperation apiOperation, String tag) {
        StringBuilder desc = new StringBuilder();
        if (Strings.isNotBlank(tag)) {
            desc.append("[").append(tag).append("] ");
        }
        desc.append(apiOperation.name());
        if (Strings.isNotBlank(apiOperation.description())) {
            desc.append(" - ").append(apiOperation.description());
        }
        return desc.toString();
    }

    /**
     * Build input schema from method parameters
     */
    private Map<String, Object> buildInputSchema(Method method) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        // Process ApiImplicitParams
        ApiImplicitParams apiImplicitParams = ReflectionUtils.getAnnotation(method, ApiImplicitParams.class);
        if (apiImplicitParams != null) {
            for (ApiImplicitParam param : apiImplicitParams.value()) {
                Map<String, Object> paramSchema = new HashMap<>();
                paramSchema.put("type", param.type());
                paramSchema.put("description", param.description());

                if (Strings.isNotBlank(param.example())) {
                    paramSchema.put("example", param.example());
                }

                properties.put(param.name(), paramSchema);

                if (param.required()) {
                    required.add(param.name());
                }
            }
        }

        // Process ApiFormParams
        ApiFormParams apiFormParams = ReflectionUtils.getAnnotation(method, ApiFormParams.class);
        if (apiFormParams != null) {
            for (ApiFormParam formParam : apiFormParams.value()) {
                Map<String, Object> paramSchema = new HashMap<>();
                paramSchema.put("type", formParam.type());
                paramSchema.put("description", formParam.description());

                if (Strings.isNotBlank(formParam.example())) {
                    paramSchema.put("example", formParam.example());
                }

                properties.put(formParam.name(), paramSchema);

                if (formParam.required()) {
                    required.add(formParam.name());
                }
            }

            // Process implementation class if specified
            Class<?> implClass = apiFormParams.implementation();
            if (!implClass.isAssignableFrom(Void.class)) {
                ApiModel apiModel = ReflectionUtils.getAnnotation(implClass, ApiModel.class);
                if (apiModel != null) {
                    Mirror<?> mirror = Mirror.me(implClass);
                    Field[] fields = mirror.getFields(ApiModelProperty.class);
                    for (Field field : fields) {
                        ApiModelProperty modelProperty = field.getAnnotation(ApiModelProperty.class);
                        if (modelProperty.param()) {
                            String fieldName = Strings.isBlank(modelProperty.name()) ? field.getName()
                                    : modelProperty.name();
                            String type = NutzReaderUtils.getParamType(field.getType().getTypeName());

                            Map<String, Object> paramSchema = new HashMap<>();
                            paramSchema.put("type", type);
                            paramSchema.put("description", modelProperty.description());

                            if (Strings.isNotBlank(modelProperty.example())) {
                                paramSchema.put("example", modelProperty.example());
                            }

                            properties.put(fieldName, paramSchema);

                            if (modelProperty.required()) {
                                required.add(fieldName);
                            }
                        }
                    }
                }
            }
        }

        schema.put("properties", properties);
        if (!required.isEmpty()) {
            schema.put("required", required);
        }

        return schema;
    }

    /**
     * Build path from class and method annotations
     */
    private String buildPath(At apiPath, At methodPath) {
        StringBuilder path = new StringBuilder();
        if (apiPath != null && apiPath.value().length > 0) {
            path.append(apiPath.value()[0]);
        }
        if (methodPath != null && methodPath.value().length > 0) {
            String methodPathStr = methodPath.value()[0];
            if (!methodPathStr.startsWith("/")) {
                path.append("/");
            }
            path.append(methodPathStr);
        }
        return path.toString();
    }

    /**
     * List all available tools
     */
    public Map<String, Object> listTools() {
        Map<String, Object> result = new HashMap<>();
        result.put("tools", tools);
        return result;
    }

    /**
     * Call a tool by name with parameters
     */
    public Map<String, Object> callTool(Object params) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> paramsMap = (Map<String, Object>) params;

        String toolName = (String) paramsMap.get("name");
        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) paramsMap.get("arguments");

        if (Strings.isBlank(toolName)) {
            throw new Exception("Tool name is required");
        }

        ToolMetadata metadata = toolMetadataMap.get(toolName);
        if (metadata == null) {
            throw new Exception("Tool not found: " + toolName);
        }

        // Check security annotations
        checkSecurity(metadata.method);

        // Note: Actual tool invocation would require more complex logic
        // to instantiate the controller, prepare parameters, and invoke the method
        // This is a simplified placeholder
        Map<String, Object> result = new HashMap<>();
        result.put("content", Arrays.asList(
                Map.of(
                        "type", "text",
                        "text", "Tool invocation not yet implemented. Tool: " + toolName)));

        return result;
    }

    /**
     * Check security annotations on method
     */
    private void checkSecurity(Method method) throws Exception {
        SaCheckLogin saCheckLogin = ReflectionUtils.getAnnotation(method, SaCheckLogin.class);
        if (saCheckLogin != null) {
            // Note: Actual security check would require sa-token context
            log.debug("Tool requires login");
        }

        SaCheckPermission saCheckPermission = ReflectionUtils.getAnnotation(method, SaCheckPermission.class);
        if (saCheckPermission != null) {
            log.debugf("Tool requires permissions: %s", Arrays.toString(saCheckPermission.value()));
        }

        SaCheckRole saCheckRole = ReflectionUtils.getAnnotation(method, SaCheckRole.class);
        if (saCheckRole != null) {
            log.debugf("Tool requires roles: %s", Arrays.toString(saCheckRole.value()));
        }
    }

    /**
     * Tool metadata for invocation
     */
    private static class ToolMetadata {
        Class<?> clazz;
        Method method;
        String httpMethod;
        String path;
    }
}
