var ioc = {
	wxApi2 : {
		type : "org.nutz.weixin.impl.WxApi2Impl",
		fields : {
			conf : {refer:"conf"},
			accessTokenStore : {refer : "wxAccessTokenStore"}
		},
		events : {
			create : "init"
		}
	},
	wxLogin : {
		type : "org.nutz.weixin.impl.WxLoginImpl",
		factory : "$conf#make",
		args : ["org.nutz.weixin.impl.WxLoginImpl", "wxlogin."]
	},
	wxAccessTokenStore : {
		type : "org.nutz.weixin.spi.WxAccessTokenStore",
		args : [{java:"$conf.get('weixin.atstore')"}, {refer:"$ioc"}],
		factory : "org.nutz.weixin.at.impl.WxAccessTokenStoreFactory#make"
	}
};
