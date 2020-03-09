package com.budwk.nb.cms.commons.ig;

import org.nutz.el.opt.RunMethod;

/**
 * @author wizzer(wizzer.cn) on 2018/3/17.
 */
public interface IdGenerator extends RunMethod {
    /**
     * 主键生成器规则
     * @param tableName 表名
     * @param prefix 主键前缀
     * @return
     * @throws Exception
     */
    String next(String tableName, String prefix) throws Exception;
}