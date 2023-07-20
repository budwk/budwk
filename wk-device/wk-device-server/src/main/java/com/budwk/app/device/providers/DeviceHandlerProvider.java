package com.budwk.app.device.providers;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.device.models.Device_handler;
import com.budwk.app.device.objects.dto.DeviceHandlerDTO;
import com.budwk.app.device.services.DeviceHandlerService;
import com.budwk.app.sys.providers.ISysConfigProvider;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import static com.budwk.starter.common.constant.GlobalConstant.DEFAULT_COMMON_APPID;

/**
 * @author wizzer.cn
 */
@Service(interfaceClass = IDeviceHandlerProvider.class)
@IocBean
public class DeviceHandlerProvider implements IDeviceHandlerProvider {
    @Inject
    @Reference(check = false)
    private ISysConfigProvider sysConfigProvider;
    @Inject
    private DeviceHandlerService deviceHandlerService;

    @Override
    public DeviceHandlerDTO getHandler(String code) {
        Device_handler deviceHandler = deviceHandlerService.fetch(Cnd.where("code", "=", code).and("enabled", "=", true));
        if (deviceHandler != null) {
            DeviceHandlerDTO dto = new DeviceHandlerDTO();
            dto.setCode(deviceHandler.getCode());
            dto.setMainClass(deviceHandler.getClassPath());
            dto.setFileUrl(getFilePath(deviceHandler.getFilePath()));
            return dto;
        }
        return null;
    }

    public String getFilePath(String fileP) {
        String fileDomain = sysConfigProvider.getString(DEFAULT_COMMON_APPID, "AppFileDomainIn");
        if (Strings.isBlank(fileDomain)) {
            fileDomain = sysConfigProvider.getString(DEFAULT_COMMON_APPID, "AppFileDomain");
        }
        return Strings.sBlank(fileDomain) + fileP;
    }
}
