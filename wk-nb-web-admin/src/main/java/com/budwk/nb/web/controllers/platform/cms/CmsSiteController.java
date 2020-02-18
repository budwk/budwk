package com.budwk.nb.web.controllers.platform.cms;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.cms.models.Cms_site;
import com.budwk.nb.cms.services.CmsSiteService;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;


/**
 * @author wizzer(wizzer.cn)
 * @date 2020/2/10
 */
@IocBean
@At("/api/{version}/platform/cms/site")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "测试")}, servers = {@Server(url = "/")})
public class CmsSiteController {
    private static final Log log = Logs.get();
    @Inject
    @Reference(check = false)
    private CmsSiteService cmsSiteService;

    @At("/test/{id}")
    @POST
    @Operation(tags = "测试", description = "测试", summary = "测试1",
            parameters = {
                    @Parameter(name = "id", description = "id", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "404", description = "成功")
            })
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "test", description = "测试")
            },
            implementation = Cms_site.class,
            mediaType = "application/json"
    )
    @AdaptBy(type=JsonAdaptor.class)
    public Object test(String id, @Param("..") Cms_site site) {

        return Result.success("哈哈哈:::" + id).addData(site);
    }

    @At("/test2")
    @POST
    @Operation(tags = "测试", description = "测试", summary = "测试2",
            security = {
                    @SecurityRequirement(name = "Logon")
            },
            responses = {
                    @ApiResponse(description = "成功", content = @Content(schema = @Schema(implementation = Cms_site.class))),
                    @ApiResponse(responseCode = "404", description = "成功")
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "txt", description = "txt", format = "int32", type = "integer", example = "1", required = true)
            }
    )
    public Object test2(@Param("txt") String txt) {
        return Result.success("哈哈哈:::" + txt);
    }
}
