package cn.percent.usersystemjdk17.security.filter;

import cn.percent.usersystemjdk17.common.utils.ApiCodeUtils;
import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import cn.percent.usersystemjdk17.common.utils.RedisUtils;
import cn.percent.usersystemjdk17.common.utils.UserUtils;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.entity.details.MyUserDetails;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import cn.percent.usersystemjdk17.security.dto.ResponseDTO;
import cn.percent.usersystemjdk17.security.service.MyUserDetailsService;
import cn.percent.usersystemjdk17.security.service.TokenService;
import com.alibaba.fastjson.JSON;
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

    public static final String bearer = "Bearer ";

    public static final String accessToken = "accessToken";

    private TokenService tokenService;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private UserEntityService userEntityService;

    private MyUserDetailsService myUserDetailsService;

    private RedisUtils redisUtils;


    @Override
    public AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, TokenService tokenService,
                                   AuthenticationEntryPoint authenticationEntryPoint, UserEntityService userEntityService,
                                   MyUserDetailsService myUserDetailsService, RedisUtils redisUtils) {
        super(authenticationManager);
        this.tokenService = tokenService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userEntityService = userEntityService;
        this.myUserDetailsService = myUserDetailsService;
        this.redisUtils = redisUtils;
    }

    /**
     * 当前这个类进行token的校验
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 获取到指定的token
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith(bearer)) {
            // 释放给下一个过滤器
            chain.doFilter(request, response);
            return;
        }
        log.info("路径：{}", request.getRequestURL().toString());
        String token = bearerToken.replace(bearer, "");
        // 校验当前token是否合法（包括失效）
        Claims claims = tokenService.checkToken(token);
        if (claims == null) {
            // 获取到refreshToken
            String refreshToken = request.getHeader("refreshToken");
            // 用户id
            try {
                // 校验刷新token是否有效 有效果直接放行前端并且告诉前端调用刷新token的controller
                Claims refreshClaims = tokenService.checkToken(refreshToken);
                if (refreshClaims == null) {
                    ResponseDTO dto = new ResponseDTO(ApiCodeUtils.LOGON__FAILURE.getCode(), ApiCodeUtils.LOGON__FAILURE.getMsg());
                    ApiResultUtils.write(response, JSON.toJSONString(dto));
                    return;
                }
                // 用户id
                Long userId = (Long) refreshClaims.get("userId");
                UserEntity userEntity = userEntityService.lambdaQuery().eq(UserEntity::getId, userId).one();
                if (userEntity.getDisable().equals(1)) {
                    ResponseDTO dto = new ResponseDTO(ApiCodeUtils.USER_DISABLE.getCode(), ApiCodeUtils.USER_DISABLE.getMsg());
                    ApiResultUtils.write(response, JSON.toJSONString(dto));
                    return;
                }
                 String loginAcct = (String) refreshClaims.get("loginAcct");
                 MyUserDetails userDetails = myUserDetailsService.loadUserByUsername(loginAcct);
                 SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(loginAcct, UserUtils.getUser(userId).getUserPswd(), userDetails.getAuthorities()));
            } catch (Exception e) {
                e.printStackTrace();
                ResponseDTO dto = new ResponseDTO(ApiCodeUtils.LOGON__FAILURE.getCode(), ApiCodeUtils.LOGON__FAILURE.getMsg());
                ApiResultUtils.write(response, JSON.toJSONString(dto));
                return;
            }
        } else {
            // 是否accessToken的校验
            String status = (String) claims.get("status");
            if (accessToken.equals(status)) {
                // 用户id
                Long userId = (Long) claims.get("userId");
                UserEntity userEntity = userEntityService.lambdaQuery().eq(UserEntity::getId, userId).one();
                if (userEntity.getDisable().equals(1)) {
                    ResponseDTO dto = new ResponseDTO(ApiCodeUtils.USER_DISABLE.getCode(), ApiCodeUtils.USER_DISABLE.getMsg());
                    ApiResultUtils.write(response, JSON.toJSONString(dto));
                    return;
                }
                 String loginAcct = (String) claims.get("loginAcct");
                 MyUserDetails userDetails = myUserDetailsService.loadUserByUsername(loginAcct);
                 // 将用户信息设置给Security
                 SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(loginAcct, UserUtils.getUser(userId).getUserPswd(), userDetails.getAuthorities()));
            }
        }
        // 释放给下一个过滤器
        chain.doFilter(request, response);
        log.info("成功：{}", request.getRequestURL().toString());
        return;
    }


}
