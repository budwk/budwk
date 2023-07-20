package com.budwk.app.device.handler.support;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.app.device.enums.DeviceValveState;
import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.codec.MeterOperator;
import com.budwk.app.device.handler.common.codec.impl.DefaultDeviceOperator;
import com.budwk.app.device.handler.common.enums.ValveState;
import com.budwk.app.device.providers.IDeviceInfoProvider;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

/**
 * @author zyang  2022/8/4 15:58
 */
@IocBean
@Slf4j
public class DeviceOperatorFlushSupport {

    @Inject
    @Reference
    private IDeviceInfoProvider deviceInfoProvider;


    public void flush(DeviceOperator operator) {
        if (!(operator instanceof DefaultDeviceOperator)) {
            return;
        }
        DefaultDeviceOperator dfOperator = (DefaultDeviceOperator) operator;
//        if (Lang.isNotEmpty(dfOperator.getUpdatedProperties())) {
//            // 更新设备扩展属性
//            deviceInfoProvider.saveExtraProperties(dfOperator.getDeviceId(), dfOperator.getUpdatedProperties());
//        }
        // 如果是表具，更新阀门状态
        if (operator instanceof MeterOperator) {
            this.flushMeter(dfOperator);
        }

    }

    private void flushMeter(DefaultDeviceOperator operator) {
        NutMap properties = NutMap.WRAP(operator.getUpdatedProperties());
        if (!properties.containsKey("valve_state")) {
            return;
        }
        int valveState = properties.getInt("valve_state", -99);
        if (valveState == -99) {
            return;
        }
        ValveState valveStateEnum = ValveState.from(valveState);
        if (valveStateEnum == null) {
            return;
        }
        deviceInfoProvider.updateMeterValveState(operator.getDeviceId(), convertValveState(valveStateEnum));
    }


    private DeviceValveState convertValveState(ValveState valveState) {
        if (null == valveState) {
            return null;
        }
        switch (valveState) {
            case STATE_0:
                return DeviceValveState.NONE;
            case STATE_1:
                return DeviceValveState.ON;
            case STATE_2:
                return DeviceValveState.TEMPORARY_OFF;
            case STATE_3:
                return DeviceValveState.FORCE_OFF;
            case STATE_4:
                return DeviceValveState.UNKNOWN;
            case STATE_5:
                return DeviceValveState.FAULT;
        }
        return null;
    }
}
