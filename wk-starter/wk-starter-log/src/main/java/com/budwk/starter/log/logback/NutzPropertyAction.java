package com.budwk.starter.log.logback;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.nutz.ioc.impl.PropertiesProxy;
import org.xml.sax.Attributes;

/**
 * @author wizzer@qq.com
 */
public class NutzPropertyAction extends Action {

    private static final String SOURCE_ATTRIBUTE = "source";

    private static final String DEFAULT_VALUE_ATTRIBUTE = "defaultValue";

    private final PropertiesProxy conf;

    public NutzPropertyAction(PropertiesProxy conf) {
        this.conf = conf;
    }

    @Override
    public void begin(InterpretationContext context, String s, Attributes attributes) throws ActionException {
        String name = attributes.getValue(NAME_ATTRIBUTE);
        String source = attributes.getValue(SOURCE_ATTRIBUTE);
        ActionUtil.Scope scope = ActionUtil.stringToScope(attributes.getValue(SCOPE_ATTRIBUTE));
        String defaultValue = attributes.getValue(DEFAULT_VALUE_ATTRIBUTE);
        if (OptionHelper.isEmpty(name) || OptionHelper.isEmpty(source)) {
            addError("The \"name\" and \"source\" attributes of <nutzProperty> must be set");
        }
        ActionUtil.setProperty(context, name, getValue(source, defaultValue), scope);
    }

    private String getValue(String source, String defaultValue) {
        if (this.conf == null) {
            addWarn("No Properties available to resolve " + source);
            return defaultValue;
        }
        return this.conf.get(source, defaultValue);
    }

    @Override
    public void end(InterpretationContext interpretationContext, String s) throws ActionException {

    }
}
