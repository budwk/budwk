package com.budwk.starter.wechat.spi;

/**
 * 
 * @author JinYi(wdhlzd@163.com)
 *
 */
public interface WxCardTicketApi {

	/**
	 * 注入用于存储卡券api_ticket（调用卡券相关接口的临时票据）的仓库
	 *
	 * @author JinYi
	 * @param cts
	 */
	void setCardTicketStore(WxCardTicketStore cts);

	/**
	 * 获取用于存储卡券api_ticket（调用卡券相关接口的临时票据）的仓库
	 *
	 * @author JinYi
	 * @return
	 */
    WxCardTicketStore getCardTicketStore();

    /**
     * 获取卡券api_ticket（调用卡券相关接口的临时票据）
     *
     * @author JinYi
     * @return
     */
    String getCardTicket();

}
