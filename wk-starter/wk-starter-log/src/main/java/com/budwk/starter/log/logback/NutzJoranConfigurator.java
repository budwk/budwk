package com.budwk.starter.log.logback;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import org.nutz.ioc.impl.PropertiesProxy;

/**
 * @author wizzer@qq.com
 */
public class NutzJoranConfigurator extends JoranConfigurator {
    protected PropertiesProxy conf;

    public PropertiesProxy getConf() {
        return conf;
    }

    public void setConf(PropertiesProxy conf) {
        this.conf = conf;
    }

    @Override
    public void addInstanceRules(RuleStore rs) {
        super.addInstanceRules(rs);
        rs.addRule(new ElementSelector("configuration/nutzProperty"), new NutzPropertyAction(conf));
    }

}
