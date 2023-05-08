package com.budwk.app.device.handler.common.utils.aep;

import com.budwk.app.device.handler.common.utils.aep.model.BaseRequest;
import com.budwk.app.device.handler.common.utils.aep.model.CmdBody;
import org.nutz.http.Request;

/**
 * lwm2m协议有profile指令下发请求
 * @author wizzer.cn
 */
public class CreateCommandLwmProfileRequest extends BaseRequest {

    /**
     * 报文请求体
     */
    private CmdBody cmdBody;

    public CreateCommandLwmProfileRequest(String appKey, String appSecret) {
        super(appKey, appSecret);
    }

    public CreateCommandLwmProfileRequest(String baseUrl, String appKey, String appSecret) {
        super(baseUrl, appKey, appSecret);
    }

    /**
     * MasterKey 必填
     *
     * @param masterKey
     */
    public void setMasterKey(String masterKey) {
        header.put("MasterKey", masterKey);
    }

    public String getMasterKey() {
        return header.getString("MasterKey");
    }

    @Override
    public Request.METHOD getMethod() {
        return Request.METHOD.POST;
    }

    @Override
    public String getPath() {
        return "/aep_device_command_lwm_profile/commandLwm2mProfile";
    }

    public CmdBody createCmdBody(){
        return new CmdBody();
    }

    public CmdBody getCmdBody() {
        return cmdBody;
    }

    public void setCmdBody(CmdBody cmdBody) {
        this.cmdBody = cmdBody;
    }
}
