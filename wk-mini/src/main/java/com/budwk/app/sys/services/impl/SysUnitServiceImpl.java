package com.budwk.app.sys.services.impl;

import com.budwk.app.sys.enums.SysLeaderType;
import com.budwk.app.sys.enums.SysUnitType;
import com.budwk.app.sys.models.Sys_unit;
import com.budwk.app.sys.models.Sys_unit_user;
import com.budwk.app.sys.services.SysUnitService;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.common.exception.BaseException;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = RedisConstant.WKCACHE + "sys_unit", isHash = false, cacheLiveTime = RedisConstant.WKCACHE_TIMEOUT)
public class SysUnitServiceImpl extends BaseServiceImpl<Sys_unit> implements SysUnitService {
    public SysUnitServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private SysUserService sysUserService;

    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void save(Sys_unit unit) {
        Sys_unit parentUnit = this.fetch(unit.getParentId());
        unit.setPath(getSubPath("sys_unit", "path", parentUnit.getPath()));
        unit.setParentId(unit.getParentId());
        dao().insert(unit);
        this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", unit.getParentId()));
    }

    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void update(Sys_unit unit, String[] leaders, String[] highers, String[] assigners) {
        this.updateIgnoreNull(unit);
        this.dao().clear("sys_unit_user", Cnd.where("unitId", "=", unit.getId()));
        if (leaders != null) {
            for (String leader : leaders) {
                Sys_unit_user unitUser = new Sys_unit_user();
                unitUser.setUnitId(unit.getId());
                unitUser.setUserId(leader);
                unitUser.setLeaderType(SysLeaderType.LEADER);
                unitUser.setCreatedBy(unitUser.getUpdatedBy());
                this.dao().insert(unitUser);
            }
        }
        if (highers != null) {
            for (String higher : highers) {
                Sys_unit_user unitUser = new Sys_unit_user();
                unitUser.setUnitId(unit.getId());
                unitUser.setUserId(higher);
                unitUser.setLeaderType(SysLeaderType.HIGHER);
                unitUser.setCreatedBy(unitUser.getUpdatedBy());
                this.dao().insert(unitUser);
            }
        }
        if (assigners != null) {
            for (String assigner : assigners) {
                Sys_unit_user unitUser = new Sys_unit_user();
                unitUser.setUnitId(unit.getId());
                unitUser.setUserId(assigner);
                unitUser.setLeaderType(SysLeaderType.ASSIGNER);
                unitUser.setCreatedBy(unitUser.getUpdatedBy());
                this.dao().insert(unitUser);
            }
        }
    }

    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(Sys_unit unit) {
        dao().execute(Sqls.create("delete from sys_unit where path like @path").setParam("path", unit.getPath() + "%"));
        dao().execute(Sqls.create("delete from sys_unit_user where unitId=@id or unitId in(SELECT id FROM sys_unit WHERE path like @path)").setParam("id", unit.getId()).setParam("path", unit.getPath() + "%"));
        dao().execute(Sqls.create("delete from sys_role where unitId=@id or unitId in(SELECT id FROM sys_unit WHERE path like @path)").setParam("id", unit.getId()).setParam("path", unit.getPath() + "%"));
        if (!Strings.isEmpty(unit.getParentId())) {
            int count = count(Cnd.where("parentId", "=", unit.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update sys_unit set hasChildren=0 where id=@pid").setParam("pid", unit.getParentId()));
            }
        }
    }

    @Override
    public Pagination searchUser(String username, String unitId) {
        if (Strings.isBlank(unitId)) {
            return new Pagination();
        }
        //获取本级及下一级单位的用户信息
        Sys_unit unit = this.fetch(unitId);
        String path = unit.getPath();
        Sql sql = Sqls.create("SELECT id AS VALUE,username AS label from sys_user where (unitId=@unitId or unitPath like @unitPath) $s order by username asc");
        sql.setParam("unitPath", path + "____");
        sql.setParam("unitId", unitId);
        if (Strings.isNotBlank(username)) {
            sql.setVar("s", " and (username like '%" + username + "%' or loginname like '%"+username+"%')");
        }
        return this.listPage(1, 20, sql);
    }

    @Override
    public String getMasterCompanyId(String unitId) {
        Sys_unit unit = this.fetch(unitId);
        if (unit == null) {
            throw new BaseException(ResultCode.NULL_DATA_ERROR.getMsg());
        }
        if (SysUnitType.GROUP == unit.getType() || SysUnitType.COMPANY == unit.getType()) {
            return unit.getId();
        } else if (Strings.isNotBlank(unit.getParentId())) {
            return getMasterCompanyId(unit.getParentId());
        } else {
            return "";
        }
    }

    @Override
    public String getMasterCompanyPath(String unitId) {
        Sys_unit unit = this.fetch(unitId);
        if (unit == null) {
            throw new BaseException(ResultCode.NULL_DATA_ERROR.getMsg());
        }
        if (SysUnitType.GROUP == unit.getType() || SysUnitType.COMPANY == unit.getType()) {
            return unit.getPath();
        } else if (Strings.isNotBlank(unit.getParentId())) {
            return getMasterCompanyPath(unit.getParentId());
        } else {
            return "0000";
        }
    }

    @Override
    public Sys_unit getMasterCompany(String unitId) {
        Sys_unit unit = this.fetch(unitId);
        if (unit == null) {
            throw new BaseException(ResultCode.NULL_DATA_ERROR.getMsg());
        }
        if (SysUnitType.GROUP == unit.getType() || SysUnitType.COMPANY == unit.getType()) {
            return unit;
        } else if (Strings.isNotBlank(unit.getParentId())) {
            return getMasterCompany(unit.getParentId());
        } else {
            return null;
        }
    }

    public List<String> getSubUnitIds(String parentId) {
        List<Sys_unit> list = this.query();
        //转为树形数据
        NutMap nutMap = NutMap.NEW();
        for (Sys_unit unit : list) {
            List<Sys_unit> list1 = nutMap.getList(unit.getParentId(), Sys_unit.class);
            if (list1 == null) {
                list1 = new ArrayList<>();
            }
            list1.add(unit);
            nutMap.put(Strings.sNull(unit.getParentId()), list1);
        }
        List<String> unitIds = new ArrayList<>();
        getSubTree(unitIds, nutMap, parentId);
        return unitIds;
    }

    private void getSubTree(List<String> treeList, NutMap nutMap, String parentId) {
        List<Sys_unit> subList = nutMap.getList(parentId, Sys_unit.class);
        for (Sys_unit unit : subList) {
            if (SysUnitType.UNIT == unit.getType()) {
                treeList.add(unit.getId());
                getSubTree(treeList, nutMap, unit.getId());
            }
        }
    }
}
