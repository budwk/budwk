package com.budwk.app.device.handler.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.app.device.cache.DeviceCaching;
import com.budwk.app.device.cache.ProductCaching;
import com.budwk.app.device.enums.DeviceOnline;
import com.budwk.app.device.enums.DeviceType;
import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.codec.DeviceRegistry;
import com.budwk.app.device.handler.common.codec.impl.DefaultDeviceOperator;
import com.budwk.app.device.handler.common.device.CommandInfo;
import com.budwk.app.device.handler.common.device.ProductInfo;
import com.budwk.app.device.handler.support.ProductInfoSupport;
import com.budwk.app.device.models.Device_cmd_record;
import com.budwk.app.device.models.Device_info;
import com.budwk.app.device.providers.IDeviceCommandProvider;
import com.budwk.app.device.providers.IDeviceInfoProvider;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer.cn
 */
@IocBean
public class DefaultDeviceRegistry implements DeviceRegistry {
    @Inject
    @Reference(check = false)
    private IDeviceInfoProvider deviceInfoProvider;

    @Inject
    private DeviceCaching deviceCaching;
    @Inject
    private ProductCaching productCaching;
    @Inject
    private ProductInfoSupport productInfoSupport;
    @Inject
    @Reference(check = false)
    private IDeviceCommandProvider deviceCommandProvider;

    @Override
    public DeviceOperator getGatewayDevice(String field, String value) {
        //todo
        return null;
    }

    @Override
    public DeviceOperator getDeviceOperator(String field, String value) {
        Device_info deviceInfo = deviceInfoProvider.getDevice(Cnd.where(field, "=", value));
        if (null == deviceInfo) {
            return null;
        }
        DeviceOperator operator = buildDeviceOperator(deviceInfo);
        operator.setProperty(field, value);
        return operator;
    }

    private DeviceOperator buildDeviceOperator(Device_info deviceInfo) {
        ProductInfo productInfo = productInfoSupport.getProduct(deviceInfo.getProductId());
        if (DeviceType.METER.value().equals(productInfo.getDeviceType())) {
            //todo 表具业务
            //return this.buildMeterOperator(deviceInfo, productInfoCache);
        }
        return this.buildDefaultOperator(deviceInfo, productInfo);
    }

    private DeviceOperator buildDefaultOperator(Device_info deviceInfo, ProductInfo productInfo) {
        DefaultDeviceOperator operator = new DefaultDeviceOperator() {
            @Override
            public int getWaitCmdCount() {
                return deviceCommandProvider.getWaitCount(getDeviceId());
            }
        };
        operator.setDeviceId(deviceInfo.getId());
        fillOperator(deviceInfo, operator);
        operator.setProductInfo(productInfo);
        return operator;
    }

    private void fillOperator(Device_info deviceInfo, DefaultDeviceOperator operator) {
        operator.setDeviceId(deviceInfo.getId());
        operator.setProperty("deviceNo", deviceInfo.getDeviceNo());
        operator.setProperty("deviceCode", deviceInfo.getDeviceCode());
        operator.setProperty("nameplateNo", deviceInfo.getNameplateNo());
        operator.setProperty("imei", deviceInfo.getImei());
        operator.setProperty("iccid", deviceInfo.getIccid());
        operator.setProperty("platformDeviceId", deviceInfo.getPlatformDeviceId());
        operator.getUpdatedProperties().clear();
    }

    @Override
    public DeviceOperator getDeviceOperator(String deviceId) {
        return getDeviceOperator("id", deviceId);
    }

    @Override
    public CommandInfo getDeviceCommand(String deviceId) {
        Device_cmd_record needSendCommand = deviceCommandProvider.getNeedSendCommand(deviceId);
        if (null != needSendCommand) {
            CommandInfo commandInfo = new CommandInfo();
            commandInfo.setCommandId(needSendCommand.getId());
            commandInfo.setCommandSerialNo(needSendCommand.getSerialNo());
            commandInfo.setDeviceId(deviceId);
            commandInfo.setCommandCode(needSendCommand.getCommandCode());
            commandInfo.setParams(needSendCommand.getParams());
            return commandInfo;
        }
        return null;
    }

    @Override
    public void updateDeviceOnline(String deviceId) {
        deviceInfoProvider.updateDeviceOnline(deviceId, DeviceOnline.ONLINE);
    }
}
