package cn.percent.usersystemjdk17.security.handler;


import cn.percent.usersystemjdk17.common.utils.ApiCodeUtils;
import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import cn.percent.usersystemjdk17.security.dto.ResponseDTO;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * 解决用户访问资源无权限时的返回
 *
 * @author: zhangpengju
 * Date: 2021/11/19
 * Time: 9:32
 * Description:
 */
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * 解决用户访问资源无权限时的返回
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param accessDeniedException AccessDeniedException
     * @throws IOException IOException
     * @throws ServletException ServletException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ApiResultUtils.write(response, JSONObject.toJSONString(new ApiResultUtils<>(ApiCodeUtils.FORBIDDEN)));
    }
}
