package com.budwk.app.device.handler.common.utils.aep;

import com.budwk.app.device.handler.common.utils.aep.model.AepResult;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Arrays;

/**
 * @author zyang
 * @author wizzer.cn
 */
public class AepApiClient {
    private static final Log log = Logs.get();

    /**
     * 创建设备 <br />
     * 成功返回设备信息
     * <pre>
     *     {
     *       "tupIsProfile": 1,
     *       "deviceId": "",// 设备id
     *       "deviceName": "TEST_001", // 设备名
     *       "deviceStatus": 0, // 设备状态
     *       "autoObserver": 0, // 自动订阅
     *       "productId": 123456, // 产品id
     *       "createBy": "x",
     *       "tenantId": "10090247", // 租户id
     *       "imei": "86800000000000123"
     *    }
     * </pre>
     *
     * @param request 设备请求信息
     * @return
     */
    public AepResult createDevice(CreateDeviceRequest request) {
        try {
            if (Lang.isEmpty(request.getDeviceInfo())) {
                return new AepResult(-1, "设备信息不能为空", null);
            }
            request.setHeader("Content-Type", "application/json; charset=UTF-8");
            String jsonStr = Json.toJson(Lang.obj2map(request.getDeviceInfo()), JsonFormat.tidy());
            request.setBody(jsonStr.getBytes());
            NutMap signInput = NutMap.NEW();
            signInput.put("MasterKey", request.getMasterKey());
            String signature = AepApiUtil.sign(request.getAppSecret(), request.getHeader(), signInput, request.getBody());
            // 指定本次接口版本
            request.setHeader("version", "20181031202117");
            request.setHeader("signature", signature);
            return AepApiUtil.doRequest(request);
        } catch (Exception e) {
            log.error("接口调用失败", e);
            return new AepResult(-1, e.getMessage(), null);
        }
    }


    /**
     * 删除设备
     *
     * @param request
     * @return
     */
    public AepResult deleteDevice(DeleteDeviceRequest request) {
        try {
            NutMap signInput = NutMap.NEW();
            signInput.put("MasterKey", request.getMasterKey());
            signInput.put("productId", request.getProductId());
            signInput.putAll(request.getParams());
            signInput.put("deviceIds", Arrays.asList(request.getDeviceIds()));
            request.setHeader("Content-Type", "application/json; charset=UTF-8");
            String signature = AepApiUtil.sign(request.getAppSecret(), request.getHeader(), signInput, request.getBody());
            // 指定本次接口版本
            request.setHeader("version", "20181031202131");
            request.setHeader("signature", signature);
            return AepApiUtil.doRequest(request);
        } catch (Exception e) {
            log.error("接口调用失败", e);
            return new AepResult(-1, e.getMessage(), null);
        }
    }

    /**
     * {
     * "code": 0,
     * "msg": "ok",
     * "data": {
     * "finishTime": null,
     * "productId": 10002145,
     * "level": 0,
     * "resultPayload": null,
     * "dataType": null,
     * "resultCode": null,
     * "serviceName": "控阀",
     * "type": 6,
     * "deviceId": "b02110e915244e5c865634fd547651a4",
     * "deviceSn": null,
     * "ttl": 7200, // 过期时间
     * "productProtocol": 3,
     * "content": "{\"isTr\":1,\"datasetId\":\"8001\",\"value\":{\"ValveStatus\":2},\"taskId\":7}",
     * "deviceTaskId": 7, // 指令id，单一设备唯一
     * "operator": "s8hns55jnkgbroq7l0bnktaaam",
     * "controlType": null,
     * "payloadType": 0,
     * "createTime": 1548061417829,
     * "tenantId": "10090247",
     * "imei": "860000000000002",
     * "isTr": null,
     * "serviceId": "8001",
     * "taskId": 2074545,
     * "status": 1
     * }
     * }
     *
     * @param request
     * @return
     */
    public AepResult createCommand(CreateCommandRequest request) {
        try {
            if (Strings.isBlank(request.getMasterKey())) {
                return new AepResult(-1, "缺少必要参数", null);
            }
            if (Lang.isEmpty(request.getCmdInfo())) {
                return new AepResult(-1, "指令信息不能为空", null);
            }
            request.setHeader("Content-Type", "application/json; charset=UTF-8");
            String jsonStr = Json.toJson(Lang.obj2map(request.getCmdInfo()), JsonFormat.tidy());
            request.setBody(jsonStr.getBytes());
            NutMap signInput = NutMap.NEW();
            signInput.put("MasterKey", request.getMasterKey());
            String signature = AepApiUtil.sign(request.getAppSecret(), request.getHeader(), signInput, request.getBody());
            // 指定本次接口版本
            request.setHeader("version", "20190712225145");
            request.setHeader("signature", signature);
            return AepApiUtil.doRequest(request);
        } catch (Exception e) {
            log.error("接口调用失败", e);
            return new AepResult(-1, e.getMessage(), null);
        }
    }

    /**
     * lwm2m有profile指令下发
     *
     * @param request
     * @return
     */
    public AepResult createCommandLwm2mProfile(CreateCommandLwmProfileRequest request) {
        try {
            if (Strings.isBlank(request.getMasterKey())) {
                return new AepResult(-1, "缺少必要参数", null);
            }
            if (Lang.isEmpty(request.getCmdBody())) {
                return new AepResult(-1, "指令信息不能为空", null);
            }
            request.setHeader("Content-Type", "application/json; charset=UTF-8");
            String jsonStr = Json.toJson(Lang.obj2map(request.getCmdBody()), JsonFormat.tidy());
            log.debugf("jsonStr:%s", jsonStr);
            request.setBody(jsonStr.getBytes());
            NutMap signInput = NutMap.NEW();
            signInput.put("MasterKey", request.getMasterKey());
            String signature = AepApiUtil.sign(request.getAppSecret(), request.getHeader(), signInput, request.getBody());
            // 指定本次接口版本
            request.setHeader("version", "20191231141545");
            request.setHeader("signature", signature);
            return AepApiUtil.doRequest(request);
        } catch (Exception e) {
            log.error("接口调用失败", e);
            return new AepResult(-1, e.getMessage(), null);
        }
    }
}
