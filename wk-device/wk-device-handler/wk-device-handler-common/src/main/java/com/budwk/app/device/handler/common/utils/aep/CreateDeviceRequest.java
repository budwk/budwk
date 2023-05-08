package com.budwk.app.device.handler.common.utils.aep;

import com.budwk.app.device.handler.common.utils.aep.model.BaseRequest;
import com.budwk.app.device.handler.common.utils.aep.model.DeviceInfo;
import org.nutz.http.Request;

/**
 * @author wizzer.cn
 */
public class CreateDeviceRequest extends BaseRequest {
    private static final long serialVersionUID = -4867857798096163942L;

    private String masterKey;
    private DeviceInfo deviceInfo;

    public CreateDeviceRequest(String baseUrl, String appKey, String appSecret) {
        super(baseUrl, appKey, appSecret);
    }
    public CreateDeviceRequest(String appKey, String appSecret) {
        super(appKey, appSecret);
    }

    public String getMasterKey() {
        return header.getString("MasterKey");
    }

    public void setMasterKey(String masterKey) {
        header.put("MasterKey",masterKey);
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public  DeviceInfo createDevice(){
        return new DeviceInfo();
    }

    @Override
    public Request.METHOD getMethod() {
        return Request.METHOD.POST;
    }

    @Override
    public String getPath() {
        return "/aep_device_management/device";
    }
}
