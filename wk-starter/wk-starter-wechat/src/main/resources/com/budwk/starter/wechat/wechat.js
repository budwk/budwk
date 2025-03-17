var ioc = {
	wxApi2 : {
		type : "com.budwk.starter.wechat.impl.WxApi2Impl",
		fields : {
			conf : {refer:"conf"},
			accessTokenStore : {refer : "wxAccessTokenStore"}
		},
		events : {
			create : "init"
		}
	},
	wxLogin : {
		type : "com.budwk.starter.wechat.impl.WxLoginImpl",
		factory : "$conf#make",
		args : ["com.budwk.starter.wechat.impl.WxLoginImpl", "wxlogin."]
	},
	wxAccessTokenStore : {
		type : "com.budwk.starter.wechat.spi.WxAccessTokenStore",
		args : [{java:"$conf.get('weixin.atstore')"}, {refer:"$ioc"}],
		factory : "com.budwk.starter.wechat.at.impl.WxAccessTokenStoreFactory#make"
	}
};
