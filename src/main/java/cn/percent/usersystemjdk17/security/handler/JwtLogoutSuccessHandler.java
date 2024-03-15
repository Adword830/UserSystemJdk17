package cn.percent.usersystemjdk17.security.handler;


import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

/**
 * 退出登录成功之后
 *
 * @author: zhangpengju
 * Date: 2021/12/2
 * Time: 16:01
 * Description:
 */
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    private UserEntityService userEntityService;

    public JwtLogoutSuccessHandler(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }


    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 退出登录

        // 退出成功后直接跳转到对应的登录页面

        return;
    }
}
