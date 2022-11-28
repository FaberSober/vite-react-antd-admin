package com.faber.config.utils.user;

import com.faber.buzz.admin.entity.User;
import com.faber.common.enums.BoolEnum;
import com.faber.common.exception.auth.UserInvalidException;

/**
 * 用户信息检查
 * @author xu.pengfei
 * @date 2022/11/28 11:30
 */
public class UserCheckUtil {

    /**
     * 校验用户有效性
     * @param user
     */
    public static void checkUserValid(User user) {
        if (user == null) {
            throw new UserInvalidException();
        }
        if (user.getStatus() == BoolEnum.NO) {
            throw new UserInvalidException("账户被冻结，请联系管理员");
        }
    }

}
