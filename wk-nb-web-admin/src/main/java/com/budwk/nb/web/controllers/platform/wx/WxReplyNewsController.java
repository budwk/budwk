package com.budwk.nb.web.controllers.platform.wx;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.ApiVersion;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/5
 */
@IocBean
@At("/api/{version}/platform/wx/reply/news")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "微信_自动回复")}, servers = {@Server(url = "/")})
public class WxReplyNewsController {
}
