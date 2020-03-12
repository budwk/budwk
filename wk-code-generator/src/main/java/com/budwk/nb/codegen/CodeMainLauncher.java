package com.budwk.nb.codegen;

import com.budwk.nb.codegen.builder.EntityDescLoader;
import com.budwk.nb.codegen.builder.Generator;
import com.budwk.nb.codegen.builder.Loader;
import com.budwk.nb.codegen.builder.TableDescriptor;
import org.nutz.boot.NbApp;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.Modules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/11
 */
@IocBean(create = "init")
@Modules(packages = "com.budwk.nb")
public class CodeMainLauncher {
    private static final Log log = Logs.get();

    @Inject("refer:$ioc")
    private Ioc ioc;
    @Inject
    private PropertiesProxy conf;
    private List<String> confTables = new ArrayList<>();
    private List<String> needTables = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("com.budwk.nb");
        nb.run();
    }


    public void init() throws Exception {
        List<String> confTables = conf.getList("codegen.tables", ",");
        if (confTables.size() == 0) {
            log.error("未设置表名...");
        }
        for (String name : confTables) {
            needTables.add(name.toLowerCase());
        }
        Pattern includePattern = Pattern.compile(".*");
        File programRootDir = new File("");
        String baseProjectName = programRootDir.getAbsolutePath();
        String basePackageName = "com.budwk.nb";
        String servicePackageName = "services";
        String modelPackageName = "com.budwk.nb";
        String outputDir = "src/main/java";
        boolean force = conf.getBoolean("codegen.force", false);
        String basePath = "";
        String baseUri = "/";
        Loader loader = (Loader) Mirror.me(EntityDescLoader.class).born();
        Map<String, TableDescriptor> tables = loader.loadTables(ioc,
                basePackageName,
                basePath,
                baseUri,
                servicePackageName,
                modelPackageName, needTables);
        for (Map.Entry<String, TableDescriptor> entry : tables.entrySet()) {
            String tableName = entry.getKey();
            if (includePattern != null) {
                if (!includePattern.matcher(tableName).find()) {
                    log.debug("skip " + tableName);
                    continue;
                }
            }

            TableDescriptor table = entry.getValue();
            log.debug("generate " + tableName + " ...");
            Generator generator = new Generator(tables, table);
            String packagePath = basePackageName.replace('.', '/');
            // 生成服务接口类
            File fileService = new File(baseProjectName + "/wk-module/" + outputDir, packagePath + "/" + table.getModelName() + "/services/" + table.getServiceClassName() + ".java");
            log.debug("generate service : " + fileService.getName());
            generator.generate(basePackageName, "template/service.vm", fileService, force);
            // 生成服务实现类
            File fileServiceImpl = new File(baseProjectName + "/wk-nb-service-" + table.getModelName() + "/" + outputDir, packagePath + "/" + table.getModelName() + "/services/impl/" + table.getServiceClassName() + "Impl.java");
            log.debug("generate impl : " + fileServiceImpl.getName());
            generator.generate(basePackageName, "template/service_impl.vm", fileServiceImpl, force);
            // 生成控制类
            File fileController = new File(baseProjectName + "/wk-nb-web-admin/" + outputDir, packagePath + "/web/controllers/platform/" + table.getModelName() + "/" + table.getControllerClassName() + ".java");
            log.debug("generate controller : " + fileController.getName());
            generator.generate(basePackageName, "template/controller.vm", fileController, force);
        }
        log.debug("done!");
    }

}
