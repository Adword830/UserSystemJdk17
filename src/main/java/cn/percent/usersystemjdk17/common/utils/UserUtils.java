package cn.percent.usersystemjdk17.common.utils;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.percent.usersystemjdk17.common.exception.BaseException;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * @author: zhangpengju
 * Date: 2021/11/29
 * Time: 14:31
 * Description:
 */
@Component
public class UserUtils {


    private static UserEntityService userEntityService;

    public static ThreadLocal<UserEntity> threadLocal = new ThreadLocal<>();

    @Autowired
    public void userEntityService(UserEntityService userEntityService) {
        UserUtils.userEntityService = userEntityService;
    }


    /**
     * 判断其实否具有某一个管理员的权限
     *
     * @param authentication
     * @return
     */
    public boolean checkRoleAdmin(Authentication authentication) {
        int size = authentication.getAuthorities().stream()
                .filter(authority -> authority.getAuthority().endsWith("Admin"))
                .collect(Collectors.toList()).size();
        if (size > 0) {
            return true;
        }
        return false;
    }

    /**
     * 根据用户id来获取指定的用户信息
     *
     * @param userId
     * @return
     */
    public static UserEntity getUser(Long userId) {
        UserEntity userEntity = userEntityService.getById(userId);
        threadLocal.set(userEntity);
        return userEntity;
    }

    /**
     * 校验邮箱是否符合规则
     *
     * @param email
     */
    public static void checkEmail(String email) {
        if (StrUtil.isEmpty(email)) {
            throw new BaseException("填写的邮箱不能为空");
        }
        boolean flag = Validator.isEmail(email);
        if (!flag) {
            throw new BaseException("不是一个正确的邮箱");
        }
    }

    /**
     * 隐藏部分邮箱
     *
     * @param email
     */
    public static void hiddenEmail(String email) {
        int emailLen = email.indexOf("@");
        String substring = email.substring(emailLen - 6, emailLen);
        email = email.replace(substring, "******");
    }

    /**
     * 隐藏部分登录名称
     *
     * @param loginAcct
     */
    public static void hiddenLoginAcct(String loginAcct) {
        int acctLen = loginAcct.length();
        if (acctLen <= ConstantUtil.THREE) {
            String str = "";
            for (int i = 0; i < acctLen - ConstantUtil.ONE; i++) {
                str += "*";
            }
            loginAcct = loginAcct.replace(loginAcct.substring(1, ConstantUtil.THREE), str);
        }
        if (ConstantUtil.THREE < acctLen && acctLen < ConstantUtil.SIX) {
            String str = "";
            for (int i = 0; i < acctLen - ConstantUtil.TWO; i++) {
                str += "*";
            }
            loginAcct = loginAcct.replace(loginAcct.substring(1, ConstantUtil.THREE), str);
        }

    }
}
