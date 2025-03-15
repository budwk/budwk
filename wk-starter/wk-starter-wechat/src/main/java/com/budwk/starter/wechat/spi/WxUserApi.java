package com.budwk.starter.wechat.spi;

import java.util.List;

import org.nutz.lang.Each;
import com.budwk.starter.wechat.bean.WxGroup;
import com.budwk.starter.wechat.bean.WxTag;

/**
 * 用户管理API
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public interface WxUserApi {

	WxResp groups_create(WxGroup group);

	WxResp groups_get();

	WxResp groups_getid(String openid);

	WxResp groups_update(WxGroup group);

	WxResp groups_member_update(String openid, String groupid);

	// -----------------------------------------------

	WxResp tags_create(WxTag tag);

	WxResp tags_get();

	WxResp tags_update(WxTag tag);

	WxResp tags_delete(WxTag tag);

	WxResp tag_getusers(String tagid, String nextOpenid);

	WxResp tags_members_batchtagging(List<String> openids, String tagid);

	WxResp tags_members_chuntagging(List<String> openids, String tagid);

	WxResp tags_getidlist(String openid);

	// -----------------------------------------------

	WxResp user_info(String openid, String lang);

	void user_get(Each<String> each);

	WxResp user_info_updatemark(String openid, String remark);
}
