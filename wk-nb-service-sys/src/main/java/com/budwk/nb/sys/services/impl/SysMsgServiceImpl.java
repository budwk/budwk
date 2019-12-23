package com.budwk.nb.sys.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import com.budwk.nb.sys.enums.SysMsgTypeEnum;
import com.budwk.nb.sys.models.Sys_msg;
import com.budwk.nb.sys.models.Sys_msg_user;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.sys.services.SysMsgService;
import com.budwk.nb.sys.services.SysMsgUserService;
import com.budwk.nb.sys.services.SysUserService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author wizzer(wizzer@qq.com) on 2019/12/12.
 */
@IocBean(args = {"refer:dao"})
@Service(interfaceClass = SysMsgService.class)
public class SysMsgServiceImpl extends BaseServiceImpl<Sys_msg> implements SysMsgService {
    public SysMsgServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private SysMsgUserService sysMsgUserService;
    @Inject
    private SysUserService sysUserService;

    /**
     * 发送消息,已考虑用户比较多的情况,采用分页发送
     *
     * @param sysMsg
     * @param users
     * @return
     */
    @Override
    public Sys_msg saveMsg(Sys_msg sysMsg, String[] users) {
        Sys_msg dbMsg = this.insert(sysMsg);
        if (dbMsg != null) {
            if (SysMsgTypeEnum.USER.equals(dbMsg.getType()) && users != null) {
                for (String loginname : users) {
                    Sys_msg_user sys_msg_user = new Sys_msg_user();
                    sys_msg_user.setMsgId(dbMsg.getId());
                    sys_msg_user.setStatus(0);
                    sys_msg_user.setLoginname(loginname);
                    sysMsgUserService.insert(sys_msg_user);
                }
            }
            if (SysMsgTypeEnum.SYSTEM.equals(dbMsg.getType())) {
                Cnd cnd = Cnd.where("disabled", "=", false).and("delFlag", "=", false);
                int total = sysUserService.count(cnd);
                int size = 1000;
                Pagination pagination = new Pagination();
                pagination.setTotalCount(total);
                pagination.setPageSize(size);
                for (int i = 1; i <= pagination.getTotalPage(); i++) {
                    Pagination pagination2 = sysUserService.listPage(i, size, cnd);
                    for (Object sysUser : pagination2.getList()) {
                        Sys_user user = (Sys_user) sysUser;
                        Sys_msg_user sys_msg_user = new Sys_msg_user();
                        sys_msg_user.setMsgId(dbMsg.getId());
                        sys_msg_user.setStatus(0);
                        sys_msg_user.setLoginname(user.getLoginname());
                        sysMsgUserService.insert(sys_msg_user);
                    }
                }
            }
        }
        sysMsgUserService.clearCache();
        return dbMsg;
    }

    @Override
    public void deleteMsg(String id) {
        this.vDelete(id);
        sysMsgUserService.vDelete(Cnd.where("msgId", "=", id));
        sysMsgUserService.clearCache();
    }
}
