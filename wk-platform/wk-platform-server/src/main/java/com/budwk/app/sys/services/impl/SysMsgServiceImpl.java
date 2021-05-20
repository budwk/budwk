package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.enums.SysMsgScope;
import com.budwk.app.sys.models.Sys_msg;
import com.budwk.app.sys.models.Sys_msg_user;
import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.providers.ISysMsgProvider;
import com.budwk.app.sys.services.SysMsgService;
import com.budwk.app.sys.services.SysMsgUserService;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class SysMsgServiceImpl extends BaseServiceImpl<Sys_msg> implements SysMsgService {
    public SysMsgServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private SysMsgUserService sysMsgUserService;

    @Inject
    private SysUserService sysUserService;

    @Inject
    private ISysMsgProvider sysMsgProvider;

    @Override
    public int getUnreadNum(String userId) {
        int size = sysMsgUserService.count(Cnd.where("delFlag", "=", false).and("userId", "=", userId)
                .and("status", "=", 0));
        return size;
    }

    @Override
    public List<Sys_msg_user> getUnreadList(String userId, int pageNumber, int pageSize) {
        return sysMsgUserService.query(Cnd.where("delFlag", "=", false).and("userId", "=", userId)
                .and("status", "=", 0)
                .desc("createdAt"), "msg", Cnd.orderBy().desc("sendAt"), new Pager().setPageNumber(pageNumber).setPageSize(pageSize));

    }

    @Override
    public void deleteMsg(String id) {
        this.vDelete(id);
        sysMsgUserService.vDelete(Cnd.where("msgId", "=", id));
    }

    @Override
    public void saveMsg(Sys_msg msg, String[] ids) {
        Sys_msg dbMsg = this.insert(msg);
        if (dbMsg != null) {
            if (SysMsgScope.SCOPE.equals(dbMsg.getScope()) && ids != null) {
                for (String userId : ids) {
                    Sys_user user = sysUserService.fetch(userId);
                    Sys_msg_user sys_msg_user = new Sys_msg_user();
                    sys_msg_user.setMsgId(dbMsg.getId());
                    sys_msg_user.setStatus(0);
                    sys_msg_user.setCreatedBy(dbMsg.getCreatedBy());
                    sys_msg_user.setUserId(user.getId());
                    sys_msg_user.setLoginname(user.getLoginname());
                    sys_msg_user.setUsername(user.getUsername());
                    sysMsgUserService.insert(sys_msg_user);
                    sysMsgProvider.getMsg(userId, true);
                }
            }
            if (SysMsgScope.ALL.equals(dbMsg.getScope())) {
                Cnd cnd = Cnd.where("disabled", "=", false).and("delFlag", "=", false);
                int total = sysUserService.count(cnd);
                int size = 500;
                Pagination pagination = new Pagination();
                pagination.setTotalCount(total);
                pagination.setPageSize(size);
                for (int i = 1; i <= pagination.getTotalPage(); i++) {
                    Pagination pagination2 = sysUserService.listPage(i, size, cnd);
                    for (Sys_user user : pagination2.getList(Sys_user.class)) {
                        Sys_msg_user sys_msg_user = new Sys_msg_user();
                        sys_msg_user.setMsgId(dbMsg.getId());
                        sys_msg_user.setStatus(0);
                        sys_msg_user.setCreatedBy(dbMsg.getCreatedBy());
                        sys_msg_user.setUserId(user.getId());
                        sys_msg_user.setLoginname(user.getLoginname());
                        sys_msg_user.setUsername(user.getUsername());
                        sysMsgUserService.insert(sys_msg_user);
                        sysMsgProvider.getMsg(user.getId(), true);
                    }
                }
            }
        }
    }
}
