package cn.percent.usersystemjdk17.security.filter;

import cn.percent.usersystemjdk17.common.utils.ApiCodeUtils;
import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import cn.percent.usersystemjdk17.common.utils.UserUtils;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.entity.details.MyUserDetails;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import cn.percent.usersystemjdk17.security.dto.ResponseDTO;
import cn.percent.usersystemjdk17.security.service.MyUserDetailsService;
import cn.percent.usersystemjdk17.security.service.TokenService;
import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

/**
 * 这个类会对资源进行拦截,释放资源或者拦截资源
 *
 * @author: zhangpengju
 * Date: 2021/11/18
 * Time: 16:49
 * Description:
 */
@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    public static final String BEARER = "Bearer ";

    public static final String ACCESS_TokEN = "accessToken";

    private final TokenService tokenService;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final UserEntityService userEntityService;

    private final MyUserDetailsService myUserDetailsService;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, TokenService tokenService,
                                   AuthenticationEntryPoint authenticationEntryPoint, UserEntityService userEntityService,
                                   MyUserDetailsService myUserDetailsService) {
        super(authenticationManager);
        this.tokenService = tokenService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userEntityService = userEntityService;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    public AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }

    /**
     * 当前这个类进行token的校验
     *
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 获取到指定的token
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith(BEARER)) {
            // 释放给下一个过滤器
            chain.doFilter(request, response);
            return;
        }
        String token = bearerToken.replace(BEARER, "");
        // 校验当前token是否合法（包括失效）
        Claims claims = tokenService.checkToken(token);
        // 是否accessToken的校验
        String status = (String) claims.get("status");
        if (ACCESS_TokEN.equals(status)) {
            // 用户id
            Long userId = (Long) claims.get("userId");
            UserEntity userEntity = userEntityService.lambdaQuery().eq(UserEntity::getId, userId).one();
            if (Boolean.TRUE.equals(userEntity.getDisable())) {
                ResponseDTO<Object> dto = new ResponseDTO<>(ApiCodeUtils.USER_DISABLE.getCode(), ApiCodeUtils.USER_DISABLE.getMsg());
                ApiResultUtils.write(response, JSON.toJSONString(dto));
                return;
            }
            String loginAcct = (String) claims.get("loginAcct");
            MyUserDetails userDetails = myUserDetailsService.loadUserByUsername(loginAcct);
            // 将用户信息设置给Security
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(loginAcct, UserUtils.getUser(userId).getUserPswd(), userDetails.getAuthorities()));
        }

        // 释放给下一个过滤器
        chain.doFilter(request, response);
        log.info("成功：{}", request.getRequestURL().toString());
    }


}
