package cn.percent.usersystemjdk17.security.handler;


import cn.percent.usersystemjdk17.common.utils.ApiCodeUtils;
import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * 用来解决匿名用户(未登录)访问无权限资源时的异常
 *
 * @author: zhangpengju
 * Date: 2021/11/18
 * Time: 16:51
 * Description:
 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ApiResultUtils.write(response, JSONObject.toJSONString(new ApiResultUtils<>(ApiCodeUtils.NOT_LOGGED_IN)));
    }
}
