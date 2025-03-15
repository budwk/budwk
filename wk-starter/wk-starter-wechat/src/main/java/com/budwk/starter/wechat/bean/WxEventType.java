package com.budwk.starter.wechat.bean;

public enum WxEventType {

	/**
	 * 订阅
	 */
	subscribe, 
	
	/**
	 * 取消订阅
	 */
	unsubscribe,

	/**
	 * 用户操作订阅通知弹窗
	 */
	subscribe_msg_popup_event,

	/**
	 * 用户管理订阅通知
	 */
	subscribe_msg_change_event,
	/**
	 * 发送订阅通知
	 * 场景：调用 bizsend 接口发送通知
	 */
	subscribe_msg_sent_event,
	
	/**
	 * 
	 */
	SCAN, 
	
	/**
	 * 
	 */
	LOCATION, 
	
	/**
	 * 点击推事件
	 * 
	 * <pre>
	 * 用户点击click类型按钮后，微信服务器会通过消息接口推送消息类型为event的结构给开发者（参考消息接口指南）
	 * 并且带上按钮中开发者填写的key值，开发者可以通过自定义的key值与用户进行交互；
	 * </pre>
	 */
	CLICK, 
	
	/**
	 * 跳转URL
	 * 
	 * <pre>
	 * 用户点击view类型按钮后，微信客户端将会打开开发者在按钮中填写的网页URL
	 * 可与网页授权获取用户基本信息接口结合，获得用户基本信息。
	 * </pre>
	 */
	VIEW, 
	
	/**
	 * 
	 */
	TEMPLATESENDJOBFINISH,

	// 以下事件仅支持, 微信iPhone5.4.1+, Android5.4+
	/**
	 * 扫码推事件
	 * 
	 * <pre>
	 * 用户点击按钮后，微信客户端将调起扫一扫工具
	 * 完成扫码操作后显示扫描结果（如果是URL，将进入URL），且会将扫码的结果传给开发者，开发者可以下发消息。
	 * </pre>
	 */
	scancode_push,

	/**
	 * 扫码推事件且弹出“消息接收中”提示框
	 * 
	 * <pre>
	 * 用户点击按钮后，微信客户端将调起扫一扫工具
	 * 完成扫码操作后，将扫码的结果传给开发者，同时收起扫一扫工具
	 * 然后弹出“消息接收中”提示框，随后可能会收到开发者下发的消息。
	 * </pre>
	 */
	scancode_waitmsg,

	/**
	 * 弹出系统拍照发图
	 * 
	 * <pre>
	 * 用户点击按钮后，微信客户端将调起系统相机
	 * 完成拍照操作后，会将拍摄的相片发送给开发者，并推送事件给开发者，同时收起系统相机，随后可能会收到开发者下发的消息。
	 * </pre>
	 */
	pic_sysphoto,

	/**
	 * 弹出拍照或者相册发图
	 * 
	 * <pre>
	 * 用户点击按钮后，微信客户端将弹出选择器供用户选择“拍照”或者“从手机相册选择”。
	 * 用户选择后即走其他两种流程。
	 * </pre>
	 */
	pic_photo_or_album,

	/**
	 * 弹出微信相册发图器
	 * 
	 * <pre>
	 * 用户点击按钮后，微信客户端将调起微信相册
	 * 完成选择操作后，将选择的相片发送给开发者的服务器，并推送事件给开发者，同时收起相册，随后可能会收到开发者下发的消息。
	 * </pre>
	 */
	pic_weixin,

	/**
	 * 弹出地理位置选择器
	 * 
	 * <pre>
	 * 用户点击按钮后，微信客户端将调起地理位置选择工具
	 * 完成选择操作后，将选择的地理位置发送给开发者的服务器，同时收起位置选择工具，随后可能会收到开发者下发的消息。
	 * </pre>
	 */
	location_select,
	
	MASSSENDJOBFINISH,
	
	// 卡卷
	card_pass_check, card_pass_not_check,
	user_get_card,user_del_card,user_consume_card,
	user_pay_from_pay_cell,user_view_card,
	user_enter_session_from_card,update_member_card,
	card_sku_remind,card_pay_order,
}
