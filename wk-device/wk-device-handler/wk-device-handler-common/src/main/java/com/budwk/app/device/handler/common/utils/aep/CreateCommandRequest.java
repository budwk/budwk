package com.budwk.app.device.handler.common.utils.aep;

import com.budwk.app.device.handler.common.utils.aep.model.BaseRequest;
import com.budwk.app.device.handler.common.utils.aep.model.CmdInfo;
import org.nutz.http.Request;

/**
 * @author wizzer.cn
 */
public class CreateCommandRequest extends BaseRequest {
    private static final long serialVersionUID = 6800744380792716818L;
    /**
     * 指令信息，必填
     */
    private CmdInfo cmdInfo;



    public CreateCommandRequest(String appKey, String appSecret) {
        super(appKey, appSecret);
    }

    public CreateCommandRequest(String baseUrl, String appKey, String appSecret) {
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

    public CmdInfo getCmdInfo() {
        return cmdInfo;
    }

    public void setCmdInfo(CmdInfo cmdInfo) {
        this.cmdInfo = cmdInfo;
    }

    @Override
    public Request.METHOD getMethod() {
        return Request.METHOD.POST;
    }

    @Override
    public String getPath() {
        return "/aep_device_command/command";
    }

    public CmdInfo createCmdInfo() {
        return new CmdInfo();
    }
}
