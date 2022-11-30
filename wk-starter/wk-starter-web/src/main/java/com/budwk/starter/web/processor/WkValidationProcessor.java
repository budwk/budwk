package com.budwk.starter.web.processor;

import com.budwk.starter.common.enums.Validation;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.common.utils.WebUtil;
import com.budwk.starter.web.validation.Errors;
import com.budwk.starter.web.validation.ValidationUtil;
import com.budwk.starter.web.wrapper.RequestWrapper;
import org.nutz.json.Json;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.AbstractProcessor;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
public class WkValidationProcessor extends AbstractProcessor {

    private static final Log log = Logs.get();

    public void init(NutConfig config, ActionInfo ai) throws Throwable {
    }

    @Override
    public void process(ActionContext ac) throws Throwable {
        Method method = ac.getMethod();
        HttpServletRequest req = ac.getRequest();
        Errors errors = new Errors();
        // 传参验证,步骤:
        // 1.验证是否必填
        // 2.验证正则表达式
        // 3.验证数据类型
        ApiImplicitParams implicitParams = method.getAnnotation(ApiImplicitParams.class);
        if (implicitParams != null) {
            ApiImplicitParam[] params = implicitParams.value();
            String val = "";
            List<ApiImplicitParam> pathParamList = new ArrayList<>();
            for (ApiImplicitParam param : params) {
                if (param.check()) {
                    if (param.in() == ParamIn.HEADER) {
                        val = req.getHeader(param.name());
                    }
                    if (param.in() == ParamIn.QUERY) {
                        val = req.getParameter(param.name());
                    }
                    if (param.in() == ParamIn.PATH) {
                        pathParamList.add(param);
                        continue;
                    }
                    if (Strings.isNotBlank(val)) {
                        if (Validation.NONE != param.validation() && !ValidationUtil.regex(val, param.validation())) {
                            errors.add(param.name(), param.description(), Strings.isBlank(param.validation().getMsg()) ? "正则表达式不匹配" : param.validation().getMsg());
                        } else if (Strings.isNotBlank(param.regex()) && !ValidationUtil.regex(val, param.regex())) {
                            errors.add(param.name(), param.description(), Strings.isBlank(param.msg()) ? "正则表达式不匹配" : param.msg());
                        } else if (!ValidationUtil.type(val, param.type())) {
                            errors.add(param.name(), param.description(), "类型转换错误: '" + val + "' to " + param.type());
                        }
                    } else if (param.required()) {
                        errors.add(param.name(), param.description(), "参数不可为空");
                    }
                    val = "";
                }
            }
            // 路径参数验证
            String uri = req.getRequestURI();
            if (uri.contains("/")) {
                String[] uris = Strings.splitIgnoreBlank(uri, "/");
                for (int i = 0; i < pathParamList.size(); i++) {
                    ApiImplicitParam param = pathParamList.get(i);
                    if (param.check()) {
                        if (param.required() && Strings.isBlank(uris[uris.length - pathParamList.size() + i])) {
                            errors.add(param.name(), param.description(), "参数不可为空");
                        }
                    }
                }
            }
        }
        // 表单参数验证
        ApiFormParams apiFormParams = method.getAnnotation(ApiFormParams.class);
        if (apiFormParams != null) {
            NutMap paramsMap = NutMap.NEW();
            int type = 0;
            if (req.getContentType().toLowerCase().contains("application/json")) {
                type = 1;
                // 从包装器里取数据,因为 request.getInputStream() 不能被取两次,从包装器取不影响下一个filter执行
                String json = new RequestWrapper(req).getBodyString();
                paramsMap = Json.fromJson(NutMap.class, json);
            }
            if (req.getContentType().toLowerCase().contains("application/x-www-form-urlencoded")
                    || req.getContentType().toLowerCase().contains("multipart/form-data")
            ) {
                type = 2;
            }
            if (type > 0) {
                String val = "";
                ApiFormParam[] formParams = apiFormParams.value();
                for (ApiFormParam formParam : formParams) {
                    if (formParam.check()) {
                        if (type == 1 && paramsMap != null) {
                            val = paramsMap.getString(formParam.name());

                        }
                        if (type == 2) {
                            val = req.getParameter(formParam.name());
                        }
                        if (Strings.isNotBlank(val)) {
                            if (Validation.NONE != formParam.validation() && !ValidationUtil.regex(val, formParam.validation())) {
                                errors.add(formParam.name(), formParam.description(), Strings.isBlank(formParam.validation().getMsg()) ? "正则表达式不匹配" : formParam.validation().getMsg());
                            } else if (Strings.isNotBlank(formParam.regex()) && !ValidationUtil.regex(val, formParam.regex())) {
                                errors.add(formParam.name(), formParam.description(), Strings.isBlank(formParam.msg()) ? "正则表达式不匹配" : formParam.msg());
                            } else if (!ValidationUtil.type(val, formParam.type())) {
                                errors.add(formParam.name(), formParam.description(), "数据类型错误: '" + val + "' to " + formParam.type());
                            }
                        } else if (formParam.required()) {
                            errors.add(formParam.name(), formParam.description(), "参数不可为空");
                        }
                        val = "";
                    }
                }
                Class<?> thatClass = apiFormParams.implementation();
                if (!thatClass.isAssignableFrom(Void.class)) {
                    ApiModel apiModel = thatClass.getAnnotation(ApiModel.class);
                    if (apiModel != null) {
                        Mirror<?> mirror = Mirror.me(thatClass);
                        Field[] fields = mirror.getFields(ApiModelProperty.class);
                        String valTemp = "";
                        for (Field field : fields) {
                            ApiModelProperty modelProperty = field.getAnnotation(ApiModelProperty.class);
                            if (modelProperty.param() && modelProperty.check()) {
                                String name = modelProperty.name();
                                if (Strings.isBlank(name)) {
                                    name = field.getName();
                                }
                                if (type == 1 && paramsMap != null) {
                                    valTemp = paramsMap.getString(name);
                                }
                                if (type == 2) {
                                    valTemp = req.getParameter(name);
                                }
                                if (Strings.isNotBlank(valTemp)) {
                                    String fieldType = field.getGenericType().getTypeName();
                                    fieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);
                                    if (Validation.NONE != modelProperty.validation() && !ValidationUtil.regex(valTemp, modelProperty.validation())) {
                                        errors.add(name, modelProperty.description(), Strings.isBlank(modelProperty.validation().getMsg()) ? "正则表达式不匹配" : modelProperty.validation().getMsg());
                                    } else if (Strings.isNotBlank(modelProperty.regex()) && !ValidationUtil.regex(valTemp, modelProperty.regex())) {
                                        errors.add(name, modelProperty.description(), Strings.isBlank(modelProperty.msg()) ? "正则表达式不匹配" : modelProperty.msg());
                                    } else if (!ValidationUtil.type(valTemp, fieldType)) {
                                        errors.add(name, modelProperty.description(), "数据类型错误: '" + valTemp + "' to " + field.getGenericType().getTypeName());
                                    }
                                } else if (modelProperty.required()) {
                                    errors.add(name, modelProperty.description(), "参数不可为空");
                                }
                                valTemp = "";
                            }
                        }
                    }
                }
            }
        }
        if (errors.hasError()) {
            WebUtil.rendAjaxResp(ac.getRequest(), ac.getResponse(),
                    Result.error(ResultCode.PARAM_ERROR)
                            .addData(errors.getErrorMap()));
            return;
        }
        doNext(ac);
    }
}
