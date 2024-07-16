package cn.percent.usersystemjdk17.security.filter;

import cn.hutool.core.text.CharSequenceUtil;
import cn.percent.usersystemjdk17.common.utils.*;
import cn.percent.usersystemjdk17.modules.user.dto.ImgCodeDTO;
import cn.percent.usersystemjdk17.modules.user.dto.LoginDTO;
import cn.percent.usersystemjdk17.modules.user.dto.TokenDTO;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.service.ImgCodeService;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import cn.percent.usersystemjdk17.security.dto.ResponseDTO;
import cn.percent.usersystemjdk17.security.service.TokenService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangpengju
 * JwtLoginFilter 是一个扩展了 UsernamePasswordAuthenticationFilter 的类。
 * 它用于处理 Spring Security 应用中的身份验证过程。
 * 它包括尝试身份验证、处理成功的身份验证和处理失败的身份验证的方法。
 */
@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final UserEntityService userEntityService;
    private final RedisUtils redisUtils;
    private final TokenService tokenService;
    private final ImgCodeService imgCodeService;
    private String body;
    private Integer failNum;

    /**
     * JwtLoginFilter 的构造函数。
     *
     * @param redisUtils        用于 Redis 操作的实用程序类
     * @param tokenService      用于令牌操作的服务类
     * @param userEntityService 用于用户实体操作的服务类
     * @param imgCodeService    用于图像代码操作的服务类
     */
    public JwtLoginFilter(RedisUtils redisUtils, TokenService tokenService,
                          UserEntityService userEntityService, ImgCodeService imgCodeService) {
        this.redisUtils = redisUtils;
        this.tokenService = tokenService;
        this.userEntityService = userEntityService;
        this.imgCodeService = imgCodeService;
    }

    /**
     * 尝试对用户进行身份验证。
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return 如果成功，则返回 Authentication 对象，否则返回 null
     * @throws AuthenticationException 如果身份验证失败
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("获取请求体失败:{}", e.getMessage());
        }

        LoginDTO loginDTO = JSON.parseObject(body, LoginDTO.class);
        if (loginDTO == null || CharSequenceUtil.isEmpty(loginDTO.getUsername()) || CharSequenceUtil.isEmpty(loginDTO.getPassword())) {
            ApiResultUtils.write(response, JSON.toJSONString(new ResponseDTO<>(ApiCodeUtils.LOGIN_USERNAME_OR_PASSWORD.getCode(), ApiCodeUtils.LOGIN_USERNAME_OR_PASSWORD.getMsg())));
            return null;
        }

        UserEntity userEntity = userEntityService.query().eq("login_acct", loginDTO.getUsername()).one();
        if (Boolean.TRUE.equals(userEntity.getDisable())) {
            ApiResultUtils.write(response, JSON.toJSONString(new ResponseDTO<>(ApiCodeUtils.USER_DISABLE.getCode(), ApiCodeUtils.USER_DISABLE.getMsg())));
            return null;
        }

        failNum = userEntity.getLoginFailNum();
        if (failNum >= ConstantUtil.SIX && !Objects.equals(redisUtils.get(loginDTO.getUsername()), loginDTO.getInputImgCode())) {
            ApiResultUtils.write(response, JSON.toJSONString(new ResponseDTO<>(ApiCodeUtils.LOGIN_IMG_CODE_FAIL.getCode(), ApiCodeUtils.LOGIN_IMG_CODE_FAIL.getMsg())));
            return null;
        }

        String password = Rsa1024Utils.decrypt(loginDTO.getPassword(), Rsa1024Utils.PRIVATE_KEY);
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUsername(), password);
        this.setDetails(request, authenticationToken);
        return this.getAuthenticationManager().authenticate(authenticationToken);
    }

    /**
     * 处理成功的身份验证。
     *
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param chain      FilterChain
     * @param authResult Authentication 对象
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) {
        String jsonString = JSON.toJSONString(authResult.getPrincipal());
        LoginDTO loginDTO = JSON.parseObject(jsonString, LoginDTO.class);
        UserEntity userEntity = userEntityService.getById(loginDTO.getId());
        if (userEntity.getFirstLoginTime() == null) {
            userEntityService.update(new UpdateWrapper<UserEntity>().eq("id", userEntity.getId())
                    .set("first_login_time", LocalDateTime.now()).set("last_login_time", LocalDateTime.now()));
        }
        TokenDTO token = tokenService.getToken(loginDTO, Boolean.TRUE);
        redisUtils.set("token:" + userEntity.getId().toString(), JSON.toJSONString(token), 60L, TimeUnit.MINUTES);
        ApiResultUtils.write(response, JSON.toJSONString(new ResponseDTO<>(ApiCodeUtils.ok.getCode(), "登录成功", token)));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        LoginDTO loginDTO = JSON.parseObject(body, LoginDTO.class);
        userEntityService.update(new UpdateWrapper<UserEntity>().eq("login_acct", loginDTO.getUsername()).setSql("login_fail_num = login_fail_num+1"));
        ResponseDTO<ImgCodeDTO> dto = new ResponseDTO<>(ApiCodeUtils.JWT_LOGIN_FAILURE.getCode(), ApiCodeUtils.JWT_LOGIN_FAILURE.getMsg());
        if (failNum + 1 >= ConstantUtil.SIX) {
            dto.setData(imgCodeService.generate(80, 28, loginDTO.getUsername()));
        }
        ApiResultUtils.write(response, JSON.toJSONString(dto));
    }
}