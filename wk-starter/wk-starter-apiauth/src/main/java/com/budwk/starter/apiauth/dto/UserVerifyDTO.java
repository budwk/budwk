package com.budwk.starter.apiauth.dto;

import lombok.Data;
import org.nutz.lang.util.NutMap;

@Data
public class UserVerifyDTO {
    /**
     * 是否验证成功
     */
    private boolean verified;

    private String userId;

    private NutMap userInfo;
}
