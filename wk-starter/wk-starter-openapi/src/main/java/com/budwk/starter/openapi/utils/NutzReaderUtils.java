package com.budwk.starter.openapi.utils;

import org.apache.commons.lang3.StringUtils;
import org.nutz.mvc.annotation.*;

import java.lang.reflect.Method;

/**
 * @author wizzer@qq.com
 */
public class NutzReaderUtils {
    private static final String GET_METHOD = "get";
    private static final String POST_METHOD = "post";
    private static final String PUT_METHOD = "put";
    private static final String DELETE_METHOD = "delete";
    private static final String OPTIONS_METHOD = "options";
    private static final String PATCH_METHOD = "patch";
    private static final String PATH_DELIMITER = "/";

    public static String getPath(At classLevelPath, ApiVersion classLevelApiVersion, At methodLevelPath, ApiVersion methodLevelApiVersion, String parentPath, boolean isSubresource, String methodName) {
        if (classLevelPath == null && methodLevelPath == null && StringUtils.isEmpty(parentPath)) {
            return null;
        }
        StringBuilder b = new StringBuilder();
        appendPathComponent(parentPath, b);
        if (classLevelPath != null && !isSubresource) {
            for (String path : classLevelPath.value()) {
                // 替换路径中的API版本号
                if (methodLevelApiVersion != null) {
                    path = path.replace("{version}", methodLevelApiVersion.value());
                } else if (classLevelApiVersion != null) {
                    path = path.replace("{version}", classLevelApiVersion.value());
                }
                appendPathComponent(path, b);
            }
        }
        if (methodLevelPath != null) {
            if (methodLevelPath.value().length > 0) {
                for (String path : methodLevelPath.value()) {
                    appendPathComponent(path, b);
                }
            } else {
                appendPathComponent(methodName, b);
            }
        }
        return b.length() == 0 ? "/" : b.toString();
    }

    /**
     * appends a path component string to a StringBuilder
     * guarantees:
     * <ul>
     *     <li>nulls, empty strings and "/" are nops</li>
     *     <li>output will always start with "/" and never end with "/"</li>
     * </ul>
     *
     * @param component component to be added
     * @param to        output
     */
    private static void appendPathComponent(String component, StringBuilder to) {
        if (component == null || component.isEmpty() || "/".equals(component)) {
            return;
        }
        if (!component.startsWith("/") && (to.length() == 0 || '/' != to.charAt(to.length() - 1))) {
            to.append("/");
        }
        if (component.endsWith("/")) {
            to.append(component, 0, component.length() - 1);
        } else {
            to.append(component);
        }
    }

    /**
     * 通过注解获取请求方式
     *
     * @param method Method
     * @return
     */
    public static String extractOperationMethod(Method method) {
        if (method.getAnnotation(GET.class) != null) {
            return GET_METHOD;
        } else if (method.getAnnotation(PUT.class) != null) {
            return PUT_METHOD;
        } else if (method.getAnnotation(POST.class) != null) {
            return POST_METHOD;
        } else if (method.getAnnotation(DELETE.class) != null) {
            return DELETE_METHOD;
        } else if (method.getAnnotation(OPTIONS.class) != null) {
            return OPTIONS_METHOD;
        } else if (method.getAnnotation(PATCH.class) != null) {
            return PATCH_METHOD;
        } else {
            return POST_METHOD;
        }
    }

    public static String getFormatByType(String type) {
        if (type.toLowerCase().contains("integer") || type.toLowerCase().contains("int")) {
            return "int32";
        } else if (type.toLowerCase().contains("float")) {
            return "float";
        } else if (type.toLowerCase().contains("long")) {
            return "int64";
        } else if (type.toLowerCase().contains("double")) {
            return "double";
        } else if (type.toLowerCase().contains("date")) {
            return "date";
        } else if (type.toLowerCase().contains("string")) {
            return "string";
        }

        return "";
    }

    public static Object getValueByType(String type, String value) {
        if (type.toLowerCase().contains("integer") || type.toLowerCase().contains("int")) {
            return Integer.valueOf(value);
        } else if (type.toLowerCase().contains("float")) {
            return Float.valueOf(value);
        } else if (type.toLowerCase().contains("long")) {
            return Long.valueOf(value);
        } else if (type.toLowerCase().contains("double")) {
            return Double.valueOf(value);
        }
        return value;
    }

    public static String getParamType(String typeName) {
        if (typeName.toLowerCase().contains("string")) {
            return "string";
        } else if (typeName.toLowerCase().contains("integer")) {
            return "integer";
        } else if (typeName.toLowerCase().contains("int")) {
            return "integer";
        } else if (typeName.toLowerCase().contains("float")) {
            return "number";
        } else if (typeName.toLowerCase().contains("long")) {
            return "integer";
        } else if (typeName.toLowerCase().contains("double")) {
            return "number";
        } else if (typeName.toLowerCase().contains("boolean")) {
            return "boolean";
        } else if (typeName.toLowerCase().contains("date")) {
            return "string";
        } else if (typeName.toLowerCase().contains("list")) {
            return "array";
        }
        return "object";
    }
}
