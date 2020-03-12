package com.budwk.nb.codegen.builder;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 入口类
 * Created by wizzer on 2017/1/23.
 */
public class Generator {
    private static final Log log = Logs.get();
    private final TableDescriptor table;

    public Generator(Map<String, TableDescriptor> tables, TableDescriptor table) {
        this.table = table;
    }

    public void generate(String packageName, String templatePath, File file, boolean force)
            throws IOException {
        if (file.exists() && !force) {
            log.debug("file " + file + " exists, skipped");
            return;
        }

        String code = generateCode(packageName, templatePath);
        file.getParentFile().mkdirs();
        Files.write(file, code.getBytes(Charset.forName("utf8")));

    }

    public String generateCode(String packageName, String templatePath) throws IOException {
        VelocityContext context = new VelocityContext();
        context.put("table", table);
        context.put("packageName", packageName);
        StringWriter writer = new StringWriter();

        String template = new String(Streams.readBytes(ClassLoader.getSystemResourceAsStream(templatePath)),
                Charset.forName("utf8"));
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("runtime.references.strict", false);
        engine.init();
        engine.evaluate(context, writer, "generator", template);
        return writer.toString();

    }

    private static boolean isTypeMatch(String[] types, String type) {
        for (String t : types) {
            if (t.equalsIgnoreCase(type) || (t + "Impl").equalsIgnoreCase(type) || "all".equalsIgnoreCase(t)) {
                return true;
            }
        }
        return false;
    }


    private static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main [options] [all|entity|service|controller|view]", options);
        System.exit(1);
    }

}
