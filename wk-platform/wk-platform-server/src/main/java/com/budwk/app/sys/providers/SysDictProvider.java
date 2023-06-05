package com.budwk.app.sys.providers;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.app.sys.models.Sys_dict;
import com.budwk.app.sys.services.SysDictService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;
import java.util.Map;

/**
 * @author wizzer@qq.com
 */
@Service(interfaceClass = ISysDictProvider.class)
@IocBean
public class SysDictProvider implements ISysDictProvider {
    @Inject
    private SysDictService sysDictService;

    @Override
    public String getNameByCode(String code) {
        return sysDictService.getNameByCode(code);
    }

    @Override
    public String getNameById(String id) {
        return sysDictService.getNameById(id);
    }

    @Override
    public List<Sys_dict> getSubListByPath(String path) {
        return sysDictService.getSubListByPath(path);
    }

    @Override
    public List<Sys_dict> getSubListByCode(String code) {
        return sysDictService.getSubListByCode(code);
    }

    @Override
    public List<Sys_dict> getSubListByCode(String filedName, String code) {
        return sysDictService.getSubListByCode(filedName, code);
    }

    @Override
    public List<Sys_dict> getSubListById(String id) {
        return sysDictService.getSubListById(id);
    }

    @Override
    public Map<String, String> getSubMapByPath(String path) {
        return sysDictService.getSubMapByPath(path);
    }

    @Override
    public Map<String, String> getSubMapByCode(String code) {
        return sysDictService.getSubMapByCode(code);
    }

    @Override
    public Map<String, String> getSubMapById(String id) {
        return sysDictService.getSubMapById(id);
    }
}
