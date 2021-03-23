package com.budwk.nb.web.controllers.open.lang;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.sys.services.SysLangLocalService;
import com.budwk.nb.sys.services.SysLangService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

/**
 * 获取多语言标识符
 *
 * @author wizzer(wizzer @ qq.com) on 2019/11/13
 */
@IocBean
@At("/api/{version}/open/language")
@ApiVersion
@OpenAPIDefinition(tags = {@Tag(name = "公共_多语言")}, servers = @Server(url = "/"))
public class ApiLangController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysLangService sysLangService;
    @Inject
    @Reference
    private SysLangLocalService sysLangLocalService;

    @At("/get_data")
    @GET
    @Ok("json")
    @Operation(
            tags = "公共_多语言", summary = "通过语言标识获取字符串",
            security = {
            },
            parameters = {
                    @Parameter(name = "language", example = "zh-CN", description = "语言标识", in = ParameterIn.QUERY)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object getData(@Param("language") String language) {
        try {
            return Result.success().addData(sysLangService.getLang(language));
        } catch (Exception e) {
            log.error(e);
        }
        return Result.error();
    }

    @At("/get_lang")
    @GET
    @Ok("json")
    @Operation(
            tags = "公共_多语言", summary = "获取语言列表",
            security = {
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object getLang() {
        try {
            return Result.success().addData(sysLangLocalService.getLocal());
        } catch (Exception e) {
            log.error(e);
        }
        return Result.error();
    }


    @At("/set_lang")
    @POST
    @Ok("json")
    @Operation(
            tags = "公共_多语言", summary = "设置当前语言",
            security = {
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "lang", example = "zh-CN", description = "语言标识")
            }
    )
    public Object setLang(@Param("lang") String lang) {
        try {
            if (Strings.isNotBlank(lang)) {
                Mvcs.setLocalizationKey(lang);
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e);
        }
        return Result.error();
    }
}
