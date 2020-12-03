package com.budwk.nb.cms.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.budwk.nb.cms.models.Cms_channel;
import com.budwk.nb.cms.services.CmsChannelService;
import com.budwk.nb.commons.base.service.BaseServiceImpl;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import java.util.List;

import static com.budwk.nb.commons.constants.RedisConstant.PLATFORM_REDIS_WKCACHE_PREFIX;

/**
 * @author wizzer(wizzer @ qq.com) on 2019/12/12.
 */
@IocBean(args = {"refer:dao"})
@Service(interfaceClass = CmsChannelService.class)
@CacheDefaults(cacheName = PLATFORM_REDIS_WKCACHE_PREFIX + "cms_channel")
public class CmsChannelServiceImpl extends BaseServiceImpl<Cms_channel> implements CmsChannelService {
    public CmsChannelServiceImpl(Dao dao) {
        super(dao);
    }

    /**
     * 新增菜单
     *
     * @param channel
     * @param pid
     */
    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void save(Cms_channel channel, String pid) {
        String path = "";
        if (!Strings.isEmpty(pid)) {
            Cms_channel pp = this.fetch(pid);
            path = pp.getPath();
        } else {
            pid = "";
        }
        channel.setPath(getSubPath("cms_channel", "path", path));
        channel.setParentId(pid);
        dao().insert(channel);
        if (!Strings.isEmpty(pid)) {
            this.update(Chain.make("hasChildren", true), Cnd.where("id", "=", pid));
        }
    }

    /**
     * 级联删除菜单
     *
     * @param channel
     */
    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(Cms_channel channel) {
        dao().execute(Sqls.create("delete from cms_channel where path like @path").setParam("path", channel.getPath() + "%"));
        dao().execute(Sqls.create("delete from cms_article where channelId=@id or channelId in(SELECT id FROM cms_channel WHERE path like @path)").setParam("id", channel.getId()).setParam("path", channel.getPath() + "%"));
        if (!Strings.isEmpty(channel.getParentId())) {
            int count = count(Cnd.where("parentId", "=", channel.getParentId()));
            if (count < 1) {
                dao().execute(Sqls.create("update cms_channel set hasChildren=0 where id=@pid").setParam("pid", channel.getParentId()));
            }
        }
    }

    @Override
    @CacheResult
    public Cms_channel getChannel(String id, String code) {
        if (Strings.isNotBlank(code)) {
            return this.fetch(Cnd.where("code", "=", code).and("disabled", "=", false));
        }
        return this.fetch(id);
    }

    @Override
    @CacheResult
    public boolean hasChannel(String code) {
        return this.count(Cnd.where("code", "=", code).and("disabled", "=", false)) > 0;
    }


    @Override
    @CacheResult
    public List<Cms_channel> listChannel(String parentId, String parentCode) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(parentCode)) {
            Cms_channel channel = this.fetch(Cnd.where("code", "=", parentCode));
            if (channel != null) {
                cnd.and("parentId", "=", channel.getId()).and("disabled", "=", false);
            }
        } else {
            cnd.and("parentId", "=", Strings.sNull(parentId)).and("disabled", "=", false);
        }
        cnd.asc("location");
        return this.query(cnd);
    }

    @Override
    @CacheRemoveAll
    @Async
    public void clearCache() {

    }
}
