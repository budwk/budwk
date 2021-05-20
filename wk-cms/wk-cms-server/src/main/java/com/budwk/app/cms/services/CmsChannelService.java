package com.budwk.app.cms.services;


import com.budwk.app.cms.models.Cms_channel;
import com.budwk.starter.database.service.BaseService;

import java.util.List;

/**
 * @author wizzer(wizzer@qq.com) on 2019/12/12.
 */
public interface CmsChannelService extends BaseService<Cms_channel> {
    /**
     * 添加栏目
     *
     * @param channel 栏目对象
     * @param pid 父ID
     */
    void save(Cms_channel channel, String pid);

    /**
     * 级联删除栏目
     *
     * @param channel 栏目对象
     */
    void deleteAndChild(Cms_channel channel);

    /**
     * 从缓存中获取栏目数据
     *
     * @param id   栏目ID
     * @param code 栏目标识
     * @return
     */
    Cms_channel getChannel(String id, String code);

    /**
     * 根据编码判断栏目是否存在
     *
     * @param code 栏目标识
     * @return
     */
    boolean hasChannel(String code);

    /**
     * 从缓存中获取栏目列表
     *
     * @param parentId 父栏目ID
     * @param parentCode 父栏目标识
     * @return
     */
    List<Cms_channel> listChannel(String parentId, String parentCode);

    /**
     * 清空缓存
     */
    void cacheClear();
}
