package cn.percent.usersystemjdk17.security.service;

import org.springframework.security.core.Authentication;

/**
 * @author: zhangpengju
 * Date: 2021/12/7
 * Time: 15:38
 * Description:
 */
public interface SecurityService {
    /**
     * checkUser校验当前用户是否为本人登录
     *
     * @param authentication 当前用户信息
     * @param id             当前用户id
     * @return 是否为本人登录
     */
    Boolean checkUser(Authentication authentication, Long id);

    /**
     * 判断当前系统是否存在当前用户
     *
     * @param id   当前用户id
     * @return 是否存在当前用户
     */
    Boolean isUser(Long id);
}
