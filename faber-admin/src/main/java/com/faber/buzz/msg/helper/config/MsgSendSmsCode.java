package com.faber.buzz.msg.helper.config;

import com.faber.buzz.admin.enums.MsgBuzzTypeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * 短信模板配置
 */
@Data
@ToString
@PropKey(value = "SMD_CODE", smsEnable = true)
public class MsgSendSmsCode extends MsgSendConfig {

    /**
     * 短信验证码
     */
    private String code;

    @Builder
    public MsgSendSmsCode(boolean sendAppPush, boolean sendSms, String smsCode, MsgBuzzTypeEnum buzzType, String buzzId, String code) {
        super(true, true, smsCode, buzzType, buzzId);
        this.code = code;
    }
}