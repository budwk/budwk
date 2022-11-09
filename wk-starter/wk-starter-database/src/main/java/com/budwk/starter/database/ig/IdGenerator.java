package com.budwk.starter.database.ig;

import org.nutz.el.opt.RunMethod;

/**
 * @author wizzer@qq.com
 */
public interface IdGenerator extends RunMethod {
    String next();
}
