package com.budwk.nb.codegen.builder;

import org.nutz.ioc.Ioc;

import java.util.List;
import java.util.Map;

/**
 * 基础的数据结构加载器
 * Created by wizzer on 2017/1/23.
 */
public abstract class Loader {

    public abstract Map<String, TableDescriptor> loadTables(Ioc ioc,
                                                            String basePackageName, String basePath, String baseUri, String servPackageName, String modPackageName, List<String> tables, String tableNamePrefix) throws Exception;

}
