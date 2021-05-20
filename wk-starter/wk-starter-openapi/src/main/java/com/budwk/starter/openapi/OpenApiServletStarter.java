package com.budwk.starter.openapi;

import com.budwk.starter.openapi.http.GlobalHeaderParam;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
public class OpenApiServletStarter extends HttpServlet implements WebServletFace {
    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    protected static final String PRE = "openapi.";

    @PropDoc(value = "openapi 是否启用", defaultValue = "true", type = "boolean")
    public static final String PROP_ENABLE = PRE + "enable";

    @PropDoc(value = "openapi API服务地址", defaultValue = "")
    public static final String PROP_SERVERS = PRE + "servers";

    @PropDoc(value = "openapi 权限验证公共Header参数", defaultValue = "")
    public static final String PROP_HEADERS = PRE + "headers";

    @PropDoc(value = "openapi 扫描包路径", type = "string")
    public static final String PROP_RESOURCE_PACKAGES = PRE + "scanner.package";

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    public Info getInfo() {
        Info info = conf.makeDeep(Info.class, PRE + "info.");
        info.setContact(conf.makeDeep(Contact.class, PRE + "info.contact."));
        info.setLicense(conf.makeDeep(License.class, PRE + "info.license."));
        return info;
    }

    public List<Server> getServers() {
        List<Server> serverList = new ArrayList<>();
        List<String> list = conf.getList(PROP_SERVERS);
        for (String str : list) {
            serverList.add(Json.fromJson(Server.class, str));
        }
        return serverList;
    }

    public List<GlobalHeaderParam> getHeaders() {
        List<GlobalHeaderParam> headers = new ArrayList<>();
        List<String> list = conf.getList(PROP_HEADERS);
        for (String str : list) {
            headers.add(Json.fromJson(GlobalHeaderParam.class, str));
        }
        return headers;
    }

    public void init(ServletConfig config) throws ServletException {
        Ioc ioc = appContext.getIoc();
        String pkg = conf.get(PROP_RESOURCE_PACKAGES, appContext.getPackage());
        try {
            OpenAPI oas = new OpenAPI();
            oas.info(getInfo());
            oas.servers(getServers());
            Set<String> pkgs = new HashSet<>();
            pkgs.add(pkg);
            SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                    .openAPI(oas)
                    .prettyPrint(true)
                    .resourcePackages(pkgs);
            HashSet<Class<?>> classes = new HashSet<>();
            for (Class<?> klass : Scans.me().scanPackage(pkg)) {
                classes.add(klass);
            }
            OpenAPI api = new NutzReader(oasConfig, getHeaders()).read(classes);
            config.getServletContext().setAttribute("openapi", api);
        } catch (Exception e) {
            log.error(e);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String pathInfo = req.getRequestURI();
        if (pathInfo.endsWith("/openapi.json") || pathInfo.endsWith("/openapi.yaml")) {
            OpenAPI oas = (OpenAPI) req.getServletContext().getAttribute("openapi");
            String type = "json";
            String acceptHeader = req.getHeader("Accept");
            if (Strings.isNotBlank(acceptHeader) && acceptHeader.toLowerCase().contains("application/yaml")) {
                type = "yaml";
            } else if (req.getRequestURL().toString().toLowerCase().endsWith("yaml")) {
                type = "yaml";
            }

            resp.setStatus(200);
            PrintWriter pw;
            if (type.equalsIgnoreCase("yaml")) {
                resp.setContentType("application/yaml");
                pw = resp.getWriter();
                pw.write(io.swagger.v3.core.util.Yaml.pretty(oas));
                pw.close();
            } else {
                resp.setContentType("application/json");
                pw = resp.getWriter();
                pw.write(io.swagger.v3.core.util.Json.pretty(oas));
                pw.close();
            }

        } else {
            resp.setStatus(404);
        }
    }

    @Override
    public String getName() {
        return "openapi";
    }

    @Override
    public String getPathSpec() {
        return "";
    }

    /**
     * 设置访问路径
     *
     * @return
     */
    @Override
    public String[] getPathSpecs() {
        return new String[]{"/openapi/openapi.json", "/openapi/openapi.yaml"};
    }

    @Override
    public Servlet getServlet() {
        if (!conf.getBoolean(PROP_ENABLE, false))
            return null;
        return this;
    }
}
