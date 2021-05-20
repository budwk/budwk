package com.budwk.starter.openapi;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.openapi.http.GlobalHeaderParam;
import com.budwk.starter.openapi.utils.NutzReaderUtils;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.PathUtils;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.oas.integration.ContextUtils;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.ApiVersion;
import org.nutz.mvc.annotation.At;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author wizzer@qq.com
 */
public class NutzReader implements OpenApiReader {
    private static final Log log = Logs.get();
    protected OpenAPIConfiguration config;

    private OpenAPI openAPI;
    private Components components;
    private Paths paths;
    private List<Tag> openApiTags;
    private List<GlobalHeaderParam> headerParams;

    private static final String GET_METHOD = "get";
    private static final String POST_METHOD = "post";
    private static final String PUT_METHOD = "put";
    private static final String DELETE_METHOD = "delete";
    private static final String PATCH_METHOD = "patch";
    private static final String TRACE_METHOD = "trace";
    private static final String HEAD_METHOD = "head";
    private static final String OPTIONS_METHOD = "options";
    private static final String MEDIA_TYPE = "application/json;charset=utf-8;";

    public NutzReader() {
        this.openAPI = new OpenAPI();
        paths = new Paths();
        openApiTags = new ArrayList<>();
        components = new Components();
        headerParams = new ArrayList<>();
    }

    public NutzReader(OpenAPI openAPI) {
        this();
        setConfiguration(new SwaggerConfiguration().openAPI(openAPI));
    }

    public NutzReader(OpenAPIConfiguration openApiConfiguration, List<GlobalHeaderParam> headers) {
        this();
        headerParams.addAll(headers);
        setConfiguration(openApiConfiguration);
    }

    public OpenAPI getOpenAPI() {
        return openAPI;
    }

    @Override
    public void setConfiguration(OpenAPIConfiguration openAPIConfiguration) {
        if (openAPIConfiguration != null) {
            this.config = ContextUtils.deepCopy(openAPIConfiguration);
            if (openAPIConfiguration.getOpenAPI() != null) {
                this.openAPI = this.config.getOpenAPI();
                if (this.openAPI.getComponents() != null) {
                    this.components = this.openAPI.getComponents();
                }
            }
        }
    }

    @Override
    public OpenAPI read(Set<Class<?>> classes, Map<String, Object> map) {
        return read(classes);
    }

    public OpenAPI read(Set<Class<?>> classes) {
        Set<Class<?>> sortedClasses = new TreeSet<>(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> class1, Class<?> class2) {
                if (class1.equals(class2)) {
                    return 0;
                } else if (class1.isAssignableFrom(class2)) {
                    return -1;
                } else if (class2.isAssignableFrom(class1)) {
                    return 1;
                }
                return class1.getName().compareTo(class2.getName());
            }
        });
        sortedClasses.addAll(classes);
        for (Class<?> cls : sortedClasses) {
            read(cls, null, null, false, new LinkedHashSet<String>(), new ArrayList<Parameter>(), new HashSet<Class<?>>());
        }
        return openAPI;
    }

    @SuppressWarnings(value = {"unchecked", "deprecation"})
    public OpenAPI read(Class<?> cls,
                        String parentPath,
                        String parentMethod,
                        boolean isSubresource,
                        Set<String> parentTags,
                        List<Parameter> parentParameters,
                        Set<Class<?>> scannedResources) {
        ApiDefinition apiDefinition = ReflectionUtils.getAnnotation(cls, ApiDefinition.class);
        if (apiDefinition == null) {
            return openAPI;
        }
        if (Strings.isNotBlank(apiDefinition.tag())) {
            openApiTags.add(new Tag().name(apiDefinition.tag()));
        }
        At apiPath = ReflectionUtils.getAnnotation(cls, At.class);
        ApiVersion apiVersion = ReflectionUtils.getAnnotation(cls, ApiVersion.class);

        JavaType classType = TypeFactory.defaultInstance().constructType(cls);
        BeanDescription bd = Json.mapper().getSerializationConfig().introspect(classType);
        // iterate class methods
        Method methods[] = cls.getMethods();
        for (Method method : methods) {
            if (!isOperation(method)) {
                continue;
            }
            AnnotatedMethod annotatedMethod = bd.findMethod(method.getName(), method.getParameterTypes());
            if (ReflectionUtils.isOverriddenMethod(method, cls)) {
                continue;
            }
            At methodPath = ReflectionUtils.getAnnotation(method, At.class);
            ApiVersion methodApiVersion = ReflectionUtils.getAnnotation(method, ApiVersion.class);
            // 计算请求路径
            String operationPath = NutzReaderUtils.getPath(apiPath, apiVersion, methodPath, methodApiVersion, parentPath, isSubresource, method.getName());
            Map<String, String> regexMap = new LinkedHashMap<>();
            operationPath = PathUtils.parsePath(operationPath, regexMap);
            if (operationPath != null) {

                final Class<?> subResource = getSubResourceWithNutzSubresourceLocatorSpecs(method);

                String httpMethod = NutzReaderUtils.extractOperationMethod(method);
                httpMethod = (httpMethod == null && isSubresource) ? parentMethod : httpMethod;

                if (StringUtils.isBlank(httpMethod) && subResource == null) {
                    continue;
                } else if (StringUtils.isBlank(httpMethod) && subResource != null) {
                    Type returnType = method.getGenericReturnType();
                    if (annotatedMethod != null && annotatedMethod.getType() != null) {
                        returnType = annotatedMethod.getType();
                    }
                    // 返回类型判断
                    if (shouldIgnoreClass(returnType.getTypeName()) && !method.getGenericReturnType().equals(subResource)) {
                        continue;
                    }
                }
                Operation operation = parseMethod(
                        apiDefinition.tag(),
                        method,
                        httpMethod,
                        operationPath,
                        isSubresource,
                        annotatedMethod);
                if (operation != null) {
                    PathItem pathItemObject;
                    if (openAPI.getPaths() != null && openAPI.getPaths().get(operationPath) != null) {
                        pathItemObject = openAPI.getPaths().get(operationPath);
                    } else {
                        pathItemObject = new PathItem();
                    }

                    if (StringUtils.isBlank(httpMethod)) {
                        continue;
                    }
                    setPathItemOperation(pathItemObject, httpMethod, operation);
                    paths.addPathItem(operationPath, pathItemObject);
                    if (openAPI.getPaths() != null) {
                        this.paths.putAll(openAPI.getPaths());
                    }
                    openAPI.setPaths(this.paths);
                }
            }
        }
        for (GlobalHeaderParam gp : headerParams) {
            components.addSecuritySchemes(gp.getValue(),
                    new SecurityScheme().type(SecurityScheme.Type.APIKEY
                    ).in(SecurityScheme.In.HEADER).name(gp.getValue()).description(gp.getName()));
        }
        openAPI.setComponents(components);
        openAPI.setTags(openApiTags);
        return openAPI;
    }

    protected Operation parseMethod(String tag, Method method, String httpMethod, String operationPath, boolean isSubresource, AnnotatedMethod annotatedMethod) {
        ApiOperation apiOperation = ReflectionUtils.getAnnotation(method, ApiOperation.class);
        if (apiOperation == null) {
            return null;
        }
        Map<String, ApiImplicitParam> implicitParamMap = new HashMap<>();
        ApiImplicitParams apiImplicitParams = ReflectionUtils.getAnnotation(method, ApiImplicitParams.class);
        if (apiImplicitParams != null) {
            for (ApiImplicitParam param : apiImplicitParams.value()) {
                implicitParamMap.put(param.name(), param);
            }
        }
        Operation operation = new Operation();
        operation.setOperationId(getOperationId(method.getName()));
        if (Strings.isNotBlank(apiOperation.description())) {
            operation.setDescription(apiOperation.description());
        }
        // token 权限
        SaCheckLogin saCheckLogin = ReflectionUtils.getAnnotation(method, SaCheckLogin.class);
        SaCheckPermission saCheckPermission = ReflectionUtils.getAnnotation(method, SaCheckPermission.class);
        SaCheckRole saCheckRole = ReflectionUtils.getAnnotation(method, SaCheckRole.class);
        List<SecurityRequirement> security = new ArrayList<>();
        if (saCheckLogin != null) {
            List<String> tmp = new ArrayList<>();
            tmp.add("已登陆");
            security.add(new SecurityRequirement().addList("访问权限", tmp));
        }
        if (saCheckPermission != null) {
            List<String> tmp = new ArrayList<>(Arrays.asList(saCheckPermission.value()));
            security.add(new SecurityRequirement().addList("访问权限", "权限标识: " + tmp + " 逻辑关系: " + saCheckPermission.mode().name()));
        }
        if (saCheckRole != null) {
            List<String> tmp = new ArrayList<>(Arrays.asList(saCheckRole.value()));
            security.add(new SecurityRequirement().addList("访问权限", "角色标识: " + tmp + " 逻辑关系: " + saCheckRole.mode().name()));
        }
        // 如果需要权限验证,则添加公共header参数
        if (security.size() > 0) {
            operation.security(security);
            for (GlobalHeaderParam globalHeaderParam : headerParams) {
                Parameter globalParameter = new Parameter().name(globalHeaderParam.getValue())
                        .required(globalHeaderParam.isRequired())
                        .description(globalHeaderParam.getName())
                        .in(globalHeaderParam.getIn())
                        .schema(new Schema().type("string"));
                operation.addParametersItem(globalParameter);
            }
        }
        // path 路径参数处理
//        java.lang.reflect.Parameter[] parameters = method.getParameters();
//        for (java.lang.reflect.Parameter parameter : parameters) {
//            if (operationPath.contains("{" + parameter.getName() + "}")) {
//                Parameter param = new Parameter().name(parameter.getName()).in("path");
//                ApiImplicitParam apiImplicitParam = implicitParamMap.get(parameter.getName());
//                if (apiImplicitParam != null && apiImplicitParam.in() == ParamIn.PATH) {
//                    param.required(apiImplicitParam.required());
//                    param.description(apiImplicitParam.name());
//                    Schema schema = new Schema();
//                    schema.type(apiImplicitParam.type());
//                    schema.setDefault(apiImplicitParam.df());
//                    param.schema(schema);
//                }
//                operation.addParametersItem(param);
//            }
//        }
        // 参数处理
        if (apiImplicitParams != null) {
            for (ApiImplicitParam param : apiImplicitParams.value()) {
                Schema schema = new Schema();
                schema.type(param.type());
                schema.format(NutzReaderUtils.getFormatByType(param.type()));
                if (Strings.isNotBlank(param.df())) {
                    schema.setDefault(NutzReaderUtils.getValueByType(param.type(), param.df()));
                }
                if (Strings.isNotBlank(param.example())) {
                    schema.setExample(NutzReaderUtils.getValueByType(param.type(), param.example()));
                }
                Parameter parameter = new Parameter().name(param.name())
                        .required(param.required())
                        .description(param.description())
                        .in(param.in().toString())
                        .schema(schema);
                if (Strings.isNotBlank(param.example())) {
                    parameter.setExample(NutzReaderUtils.getValueByType(param.type(), param.example()));
                }
                operation.addParametersItem(parameter);
            }
        }

        // 自定义 ApiFormParams
        ApiFormParams apiFormParams = ReflectionUtils.getAnnotation(method, ApiFormParams.class);
        if (apiFormParams != null) {
            MediaType mediaType = new MediaType();
            Schema schema = new Schema();
            schema.setType("object");
            List<String> requiredItem = new ArrayList<>();
            for (ApiFormParam formParam : apiFormParams.value()) {
                Schema property = new Schema();
                property.setType(formParam.type());
                if (Strings.isNotBlank(formParam.df())) {
                    property.setDefault(NutzReaderUtils.getValueByType(formParam.type(), formParam.df()));
                }
                if (Strings.isNotBlank(formParam.example())) {
                    property.setExample(NutzReaderUtils.getValueByType(formParam.type(), formParam.example()));
                }
                property.setDescription(formParam.description());
                if (formParam.required()) {
                    requiredItem.add(formParam.name());
                }
                schema.addProperties(formParam.name(), property);
            }

            // 实体类的解析
            Class<?> thatClass = apiFormParams.implementation();
            if (!thatClass.isAssignableFrom(Void.class)) {
                ApiModel apiModel = ReflectionUtils.getAnnotation(thatClass, ApiModel.class);
                if (apiModel != null) {
                    Mirror<?> mirror = Mirror.me(thatClass);
                    Field[] fields = mirror.getFields(ApiModelProperty.class);
                    for (Field field : fields) {
                        ApiModelProperty modelProperty = field.getAnnotation(ApiModelProperty.class);
                        String type = NutzReaderUtils.getParamType(field.getType().getTypeName());
                        // 表单参数不解析对象,只在下面都响应里进行解析
                        if ("object".equalsIgnoreCase(type) || "array".equalsIgnoreCase(type)) {
                            continue;
                        }
                        // 如果是表单参数字段
                        if (modelProperty.param()) {
                            Schema property = new Schema();
                            String name = modelProperty.name();
                            if (Strings.isBlank(name)) {
                                name = field.getName();
                            }
                            property.setName(name);
                            property.description(modelProperty.description());
                            property.setType(type);
                            if (Strings.isNotBlank(modelProperty.df())) {
                                property.setDefault(NutzReaderUtils.getValueByType(property.getType(), modelProperty.df()));
                            }
                            if (Strings.isNotBlank(modelProperty.example())) {
                                property.setExample(NutzReaderUtils.getValueByType(property.getType(), modelProperty.example()));
                            }
                            if (modelProperty.required()) {
                                requiredItem.add(name);
                            }
                            // 如果字段是对象,则进行对象解析
                            // 表单参数不解析对象,只在下面都响应里进行解析
//                            if ("object".equalsIgnoreCase(type)) {
//                                Class<?> fieldClass = field.getType();
//                                if(fieldClass.isMemberClass()) {
//                                    for (Map.Entry<String, Schema> objectSchema : getObjectSchema(fieldClass).entrySet()) {
//                                        property.addProperties(objectSchema.getKey(), objectSchema.getValue());
//                                    }
//                                }
//                            }
                            schema.addProperties(name, property);
                        }
                    }
                }
            }
            schema.required(requiredItem);
            mediaType.setSchema(schema);
            RequestBody requestBody = new RequestBody();
            Content content = new Content();
            content.addMediaType(apiFormParams.mediaType(), mediaType);
            requestBody.setContent(content);
            operation.setRequestBody(requestBody);
        }

        com.budwk.starter.common.openapi.annotation.ApiResponses apiResponses = ReflectionUtils.getAnnotation(method, com.budwk.starter.common.openapi.annotation.ApiResponses.class);
        if (apiResponses != null) {
            ApiResponses responses = new ApiResponses();
            MediaType contentMediaType = new MediaType();
            Content content = new Content();
            // 如果是json格式数据,则统一为 Result 对象
            if (apiResponses.mediaType().equalsIgnoreCase(MEDIA_TYPE)) {
                Schema contentSchema = new Schema();
                contentSchema.setType("object");
                Schema code = new Schema();
                code.setName("code");
                code.setType("integer");
                code.setFormat(NutzReaderUtils.getFormatByType("integer"));
                code.setDescription("错误代码 [0=执行成功]");
                contentSchema.addProperties("code", code);
                Schema msg = new Schema();
                msg.setName("msg");
                msg.setType("string");
                msg.setFormat(NutzReaderUtils.getFormatByType("string"));
                msg.setDescription("提示信息");
                contentSchema.addProperties("msg", msg);
                Schema data = new Schema();
                data.setType("object");
                data.setDescription("数据对象");
                io.swagger.v3.oas.models.responses.ApiResponse item = new io.swagger.v3.oas.models.responses.ApiResponse();
                for (com.budwk.starter.common.openapi.annotation.ApiResponse res : apiResponses.value()) {
                    Schema property = new Schema();
                    property.setName(res.name());
                    property.setType(res.type());
                    property.setFormat(NutzReaderUtils.getFormatByType(res.type()));
                    property.setDescription(res.description());
                    data.addProperties(res.name(), property);
                }
                // 实体类的解析
                Class<?> thatClass = apiResponses.implementation();
                if (!thatClass.isAssignableFrom(Void.class)) {
                    ApiModel apiModel = ReflectionUtils.getAnnotation(thatClass, ApiModel.class);
                    if (apiModel != null) {
                        Mirror<?> mirror = Mirror.me(thatClass);
                        Field[] fields = mirror.getFields(ApiModelProperty.class);
                        for (Field field : fields) {
                            ApiModelProperty modelProperty = field.getAnnotation(ApiModelProperty.class);
                            Schema property = new Schema();
                            String name = modelProperty.name();
                            if (Strings.isBlank(name)) {
                                name = field.getName();
                            }
                            property.setName(name);
                            property.description(modelProperty.description());
                            String type = NutzReaderUtils.getParamType(field.getType().getTypeName());
                            property.setType(type);
                            if (Strings.isNotBlank(modelProperty.df())) {
                                property.setDefault(NutzReaderUtils.getValueByType(property.getType(), modelProperty.df()));
                            }
                            if (Strings.isNotBlank(modelProperty.example())) {
                                property.setExample(NutzReaderUtils.getValueByType(property.getType(), modelProperty.example()));
                            }
                            // 如果字段是对象,则进行对象解析
                            if ("object".equalsIgnoreCase(type)) {
                                Class<?> fieldClass = field.getType();
                                if (fieldClass.isMemberClass()) {
                                    for (Map.Entry<String, Schema> objectSchema : getObjectSchema(fieldClass).entrySet()) {
                                        property.addProperties(objectSchema.getKey(), objectSchema.getValue());
                                    }
                                }
                            }
                            data.addProperties(name, property);
                        }
                    }
                }
                contentSchema.addProperties("data", data);
                if (Strings.isNotBlank(apiResponses.example())) {
                    contentSchema.example(org.nutz.json.Json.fromJson(apiResponses.example()));
                }
                contentMediaType.schema(contentSchema);
                content.addMediaType(apiResponses.mediaType(), contentMediaType);
                item.content(content);
                responses.addApiResponse("200", item);
            } else {
                io.swagger.v3.oas.models.responses.ApiResponse item = new io.swagger.v3.oas.models.responses.ApiResponse();
                content.addMediaType(apiResponses.mediaType(), contentMediaType);
                item.content(content);
                responses.addApiResponse("200", item);
            }
            operation.setResponses(responses);
        }
        List<String> tagList = new ArrayList<>();
        tagList.add(tag);
        operation.setTags(tagList);
        operation.setSummary(apiOperation.name());
        //JavaType classType = TypeFactory.defaultInstance().constructType(method.getDeclaringClass());
        return operation;
    }

    protected Map<String, Schema> getObjectSchema(Class<?> thatClass) {
        Map<String, Schema> schemaMap = new HashMap<>();
        // 实体类的解析
        ApiModel apiModel = ReflectionUtils.getAnnotation(thatClass, ApiModel.class);
        if (apiModel != null) {
            Mirror<?> mirror = Mirror.me(thatClass);
            Field[] fields = mirror.getFields(ApiModelProperty.class);
            for (Field field : fields) {
                ApiModelProperty modelProperty = field.getAnnotation(ApiModelProperty.class);
                String name = modelProperty.name();
                if (Strings.isBlank(name)) {
                    name = field.getName();
                }
                Schema property = new Schema();
                property.setName(name);
                property.description(modelProperty.description());
                String type = NutzReaderUtils.getParamType(field.getType().getTypeName());
                property.setType(type);
                if (Strings.isNotBlank(modelProperty.df())) {
                    property.setDefault(NutzReaderUtils.getValueByType(property.getType(), modelProperty.df()));
                }
                if (Strings.isNotBlank(modelProperty.example())) {
                    property.setExample(NutzReaderUtils.getValueByType(property.getType(), modelProperty.example()));
                }
                // 如果字段是对象,则进行对象解析
                if ("object".equalsIgnoreCase(type)) {
                    Class<?> fieldClass = field.getDeclaringClass();
                    if (fieldClass.isMemberClass()) {
                        for (Map.Entry<String, Schema> objectSchema : getObjectSchema(fieldClass).entrySet()) {
                            property.addProperties(objectSchema.getKey(), objectSchema.getValue());
                        }
                    }
                }
                schemaMap.put(name, property);
            }
        }
        return schemaMap;
    }

    protected boolean isOperation(Method method) {
        ApiOperation apiOperation = ReflectionUtils.getAnnotation(method, ApiOperation.class);
        return apiOperation != null;
    }

    protected Class<?> getSubResourceWithNutzSubresourceLocatorSpecs(Method method) {
        final Class<?> rawType = method.getReturnType();
        final Class<?> type;
        if (Class.class.equals(rawType)) {
            type = getClassArgument(method.getGenericReturnType());
            if (type == null) {
                return null;
            }
        } else {
            type = rawType;
        }
        if (method.getAnnotation(At.class) != null) {
            if (NutzReaderUtils.extractOperationMethod(method) == null) {
                return type;
            }
        }
        return null;
    }

    private static Class<?> getClassArgument(Type cls) {
        if (cls instanceof ParameterizedType) {
            final ParameterizedType parameterized = (ParameterizedType) cls;
            final Type[] args = parameterized.getActualTypeArguments();
            if (args.length != 1) {
                log.errorf("Unexpected class definition: {}", cls);
                return null;
            }
            final Type first = args[0];
            if (first instanceof Class) {
                return (Class<?>) first;
            } else {
                return null;
            }
        } else {
            log.errorf("Unknown class definition: {}", cls);
            return null;
        }
    }

    private boolean shouldIgnoreClass(String className) {
        if (StringUtils.isBlank(className)) {
            return true;
        }
        boolean ignore = false;
        String rawClassName = className;
        if (rawClassName.startsWith("[")) { // jackson JavaType
            rawClassName = className.replace("[simple type, class ", "");
            rawClassName = rawClassName.substring(0, rawClassName.length() - 1);
        }
        ignore = ignore || rawClassName.equalsIgnoreCase("void");
        ignore = ignore || ModelConverters.getInstance().isRegisteredAsSkippedClass(rawClassName);
        return ignore;
    }

    private void setPathItemOperation(PathItem pathItemObject, String method, Operation operation) {
        switch (method) {
            case POST_METHOD:
                pathItemObject.post(operation);
                break;
            case GET_METHOD:
                pathItemObject.get(operation);
                break;
            case DELETE_METHOD:
                pathItemObject.delete(operation);
                break;
            case PUT_METHOD:
                pathItemObject.put(operation);
                break;
            case PATCH_METHOD:
                pathItemObject.patch(operation);
                break;
            case TRACE_METHOD:
                pathItemObject.trace(operation);
                break;
            case HEAD_METHOD:
                pathItemObject.head(operation);
                break;
            case OPTIONS_METHOD:
                pathItemObject.options(operation);
                break;
            default:
                // Do nothing here
                break;
        }
    }

    protected String getOperationId(String operationId) {
        boolean operationIdUsed = existOperationId(operationId);
        String operationIdToFind = null;
        int counter = 0;
        while (operationIdUsed) {
            operationIdToFind = String.format("%s_%d", operationId, ++counter);
            operationIdUsed = existOperationId(operationIdToFind);
        }
        if (operationIdToFind != null) {
            operationId = operationIdToFind;
        }
        return operationId;
    }

    private boolean existOperationId(String operationId) {
        if (openAPI == null) {
            return false;
        }
        if (openAPI.getPaths() == null || openAPI.getPaths().isEmpty()) {
            return false;
        }
        for (PathItem path : openAPI.getPaths().values()) {
            Set<String> pathOperationIds = extractOperationIdFromPathItem(path);
            if (pathOperationIds.contains(operationId)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> extractOperationIdFromPathItem(PathItem path) {
        Set<String> ids = new HashSet<>();
        if (path.getGet() != null && StringUtils.isNotBlank(path.getGet().getOperationId())) {
            ids.add(path.getGet().getOperationId());
        }
        if (path.getPost() != null && StringUtils.isNotBlank(path.getPost().getOperationId())) {
            ids.add(path.getPost().getOperationId());
        }
        if (path.getPut() != null && StringUtils.isNotBlank(path.getPut().getOperationId())) {
            ids.add(path.getPut().getOperationId());
        }
        if (path.getDelete() != null && StringUtils.isNotBlank(path.getDelete().getOperationId())) {
            ids.add(path.getDelete().getOperationId());
        }
        if (path.getOptions() != null && StringUtils.isNotBlank(path.getOptions().getOperationId())) {
            ids.add(path.getOptions().getOperationId());
        }
        if (path.getHead() != null && StringUtils.isNotBlank(path.getHead().getOperationId())) {
            ids.add(path.getHead().getOperationId());
        }
        if (path.getPatch() != null && StringUtils.isNotBlank(path.getPatch().getOperationId())) {
            ids.add(path.getPatch().getOperationId());
        }
        return ids;
    }

}
