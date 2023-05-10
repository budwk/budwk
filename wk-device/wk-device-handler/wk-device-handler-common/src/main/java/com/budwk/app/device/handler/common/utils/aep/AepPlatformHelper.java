package com.budwk.app.device.handler.common.utils.aep;

import com.budwk.app.device.handler.common.codec.DeviceOperator;
import com.budwk.app.device.handler.common.device.ProductInfo;
import com.budwk.app.device.handler.common.utils.aep.model.AepResult;
import com.budwk.app.device.handler.common.utils.aep.model.CmdBody;
import com.budwk.app.device.handler.common.utils.aep.model.CmdInfo;
import com.budwk.app.device.handler.common.utils.aep.model.DeviceInfo;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wizzer.cn
 */
public class AepPlatformHelper {
    private static final Log log = Logs.get();
    private final AepApiClient aepApiClient;

    private final AepConfig aepConfig;

    public AepPlatformHelper(NutMap configs) {
        this.aepConfig = newAepConfig(configs);
        this.aepApiClient = getAepApiClient();
    }

    /**
     * 注册设备
     */
    public String registerDevice(DeviceOperator deviceOperator) {
        CreateDeviceRequest request = new CreateDeviceRequest(aepConfig.apiBaseUrl, aepConfig.appKey,
                aepConfig.appSecret);
        ProductInfo productInfo = deviceOperator.getProduct();
        NutMap authConfig = NutMap.NEW();
        if (productInfo != null) {
            authConfig = productInfo.getAuthConfig();
        }
        request.setMasterKey(authConfig.getString("masterKey"));
        DeviceInfo apiDeviceInfo = request.createDevice();
        apiDeviceInfo.setProductId(authConfig.getString("productId"));
        apiDeviceInfo.setAutoObserver(true);
        apiDeviceInfo.setDeviceName(Strings.sBlank(deviceOperator.getProperty("deviceNo"), deviceOperator.getDeviceId()));
        apiDeviceInfo.setDeviceSn(deviceOperator.getDeviceId());
        apiDeviceInfo.setImei((String) deviceOperator.getProperty("imei"));
        apiDeviceInfo.setOperator(aepConfig.appId);
        request.setDeviceInfo(apiDeviceInfo);
        AepResult aepResult = aepApiClient.createDevice(request);
        if (aepResult.getCode() == 0) {
            NutMap result = Lang.obj2nutmap(aepResult.getData());
            return result.getString("deviceId");
        } else {
            log.warnf("AEP 设备 %s 注册失败，错误信息：%s", deviceOperator.getDeviceId(), aepResult.getMsg());
        }
        return null;
    }

    /**
     * 删除设备
     *
     * @param platformDeviceId AEP平台设备ID=platformDeviceId
     * @return
     */
    public void deleteDevice(String masterKey, String productId, String platformDeviceId) {
        DeleteDeviceRequest request = new DeleteDeviceRequest(aepConfig.apiBaseUrl, aepConfig.appKey,
                aepConfig.appSecret);
        request.setMasterKey(masterKey);
        request.setDeviceId(platformDeviceId);
        request.setProductId(productId);
        AepResult aepResult = aepApiClient.deleteDevice(request);
        log.debugf("AEP 删除设备结果: %s", aepResult.toString());
    }


    /**
     * 创建指令(不带profile)
     *
     * @param platformDeviceId 设备对应平台的id
     * @param payload          指令内容
     * @return 返回指令id
     */
    public String createCommand(String masterKey, String productId, String platformDeviceId, Object payload) {
        CreateCommandRequest request = new CreateCommandRequest(aepConfig.apiBaseUrl, aepConfig.appKey,
                aepConfig.appSecret);
        request.setMasterKey(masterKey);
        CmdInfo cmdInfo = request.createCmdInfo();
        cmdInfo.setProductId(productId);
        cmdInfo.setDeviceId(platformDeviceId);
        cmdInfo.setOperator(Strings.sBlank(aepConfig.appId, "API"));
        Map content = new HashMap();
        content.put("dataType", 2);
        content.put("payload", payload);
        cmdInfo.setContent(content);
        request.setCmdInfo(cmdInfo);
        AepResult aepResult = aepApiClient.createCommand(request);
        log.debugf("AEP 平台指令下发结果: %s", aepResult.toString());
        if (aepResult.getCode() == 0) {
            NutMap data = Lang.obj2nutmap(aepResult.getData());
            return String.format("%s_%s", data.getString("deviceId"), data.getString("commandId"));
        } else {
            return null;
        }
    }

    /**
     * 创建指令(带profile)
     *
     * @param platformDeviceId
     * @param command
     * @return
     */
    public String createCommandProfile(String masterKey, String productId, String platformDeviceId, Object command) {
        CreateCommandLwmProfileRequest request = new CreateCommandLwmProfileRequest(aepConfig.apiBaseUrl, aepConfig.appKey,
                aepConfig.appSecret);
        request.setMasterKey(masterKey);
        CmdBody cmdBody = request.createCmdBody();
        cmdBody.setProductId(productId);
        cmdBody.setDeviceId(platformDeviceId);
        cmdBody.setOperator(Strings.sBlank(aepConfig.appId, "API"));
        NutMap map = NutMap.NEW();
        map.addv("serviceId", "Gas");
        map.addv("method", "CMD");
        map.addv("paras", NutMap.NEW().addv("cmd", command));
        cmdBody.setCommand(map);
        cmdBody.setTtl(60);
        request.setCmdBody(cmdBody);
        AepResult aepResult = aepApiClient.createCommandLwm2mProfile(request);
        log.debugf("AEP 平台指令下发结果: %s", aepResult.toString());
        if (aepResult.getCode() == 0) {
            NutMap data = Lang.obj2nutmap(aepResult.getData());
            return String.format("%s_%s", data.getString("deviceId"), data.getString("commandId"));
        } else {
            return null;
        }
    }

    private AepApiClient getAepApiClient() {
        return new AepApiClient();
    }

    public AepConfig newAepConfig(NutMap conf) {
        return new AepConfig(conf, "aep");
    }

    static class AepConfig {
        public AepConfig() {

        }

        public AepConfig(NutMap conf, String configPrefix) {
            this.apiBaseUrl = conf.getString(configPrefix + ".url");
            this.appId = conf.getString(configPrefix + ".appId");
            this.appKey = conf.getString(configPrefix + ".appKey");
            this.appSecret = conf.getString(configPrefix + ".appSecret");
        }

        /**
         * api 相关配置信息
         */
        String apiBaseUrl;
        String appId;
        String appKey;
        String appSecret;
    }
}
