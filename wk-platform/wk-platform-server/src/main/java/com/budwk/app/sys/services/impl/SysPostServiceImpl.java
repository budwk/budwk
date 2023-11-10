package com.budwk.app.sys.services.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.app.sys.enums.SysMsgType;
import com.budwk.app.sys.models.Sys_post;
import com.budwk.app.sys.providers.ISysMsgProvider;
import com.budwk.app.sys.services.SysPostService;
import com.budwk.starter.common.exception.BaseException;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class SysPostServiceImpl extends BaseServiceImpl<Sys_post> implements SysPostService {
    public SysPostServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    @Reference(check = false)
    private ISysMsgProvider sysMsgProvider;

    public void importData(String fileName, List<Sys_post> list, boolean over, String userId, String loginname) {
        if (list == null || list.size() == 0) {
            throw new BaseException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder resultMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (Sys_post obj : list) {
            obj.setCode(Strings.sNull(obj.getCode()).trim());
            obj.setName(Strings.sNull(obj.getName()).trim());
            obj.setCreatedBy(userId);
            obj.setUpdatedBy(userId);
            if (over) {
                int have = this.count(Cnd.where("code", "=", obj.getCode()));
                if (have > 0) {
                    this.update(Chain.make("name", obj.getName())
                            .add("updatedBy", userId)
                            .add("updatedAt", System.currentTimeMillis()), Cnd.where("code", "=", obj.getCode())
                    );
                    successNum++;
                } else {
                    try {
                        this.insert(obj);
                        successNum++;
                    } catch (Exception e) {
                        failureNum++;
                        String msg = "<br/>" + failureNum + "、职务名称：" + obj.getName() + "<br/>";
                        failureMsg.append(msg + e.getMessage());
                    }
                }
            } else {
                try {
                    this.insert(obj);
                    successNum++;
                } catch (Exception e) {
                    failureNum++;
                    String msg = "<br/>" + failureNum + "、职务名称：" + obj.getName() + "<br/>";
                    failureMsg.append(msg + e.getMessage());
                }
            }
        }
        resultMsg.insert(0, "导入结果：共成功 " + successNum + " 条");
        if (failureNum > 0) {
            resultMsg.append("，失败 " + failureNum + " 条，失败数据如下：<br/>");
            resultMsg.append(failureMsg);
        }
        sysMsgProvider.sendMsg(userId, SysMsgType.USER, "职务导入 " + fileName + " 完成", "", resultMsg.toString(), userId);
    }
}
