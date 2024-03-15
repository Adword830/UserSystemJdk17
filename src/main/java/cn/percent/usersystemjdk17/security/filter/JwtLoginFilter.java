package cn.percent.usersystemjdk17.security.filter;

import cn.hutool.core.util.StrUtil;
import cn.percent.usersystemjdk17.common.utils.*;
import cn.percent.usersystemjdk17.modules.user.dto.ImgCodeDTO;
import cn.percent.usersystemjdk17.modules.user.dto.LoginDTO;
import cn.percent.usersystemjdk17.modules.user.dto.TokenDTO;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.service.ImgCodeService;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import cn.percent.usersystemjdk17.security.dto.ResponseDTO;
import cn.percent.usersystemjdk17.security.service.TokenService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
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
 * @author: zhangpengju
 * Date: 2021/11/18
 * Time: 14:58
 * Description:
 */
@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * 用户相关的登录信息储存
     */
    private AuthenticationManager authenticationManager;

    /**
     * 用户
     */
    private UserEntityService userEntityService;

    /**
     * redis
     */
    private RedisUtils redisUtils;

    /**
     * token相关
     */
    private TokenService tokenService;

    private ImgCodeService imgCodeService;

    private String body;

    private Integer failNum;

    public JwtLoginFilter(AuthenticationManager authenticationManager, RedisUtils redisUtils, TokenService tokenService,
                          UserEntityService userEntityService, ImgCodeService imgCodeService) {
        // 拦截相关的登录请求
        this.redisUtils = redisUtils;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userEntityService = userEntityService;
        this.imgCodeService = imgCodeService;
    }

    /**
     * 用户密码校验
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 获取到登录相关的参数
        try {
            body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LoginDTO loginDTO = JSON.parseObject(body, LoginDTO.class);

        if (loginDTO == null) {
            ResponseDTO dto = new ResponseDTO(ApiCodeUtils.LOGIN_USERNAME_OR_PASSWORD.getCode(), ApiCodeUtils.LOGIN_USERNAME_OR_PASSWORD.getMsg());
            ApiResultUtils.write(response, JSON.toJSONString(dto));
            return null;
        }
        // 登录名或者密码为空是抛出异常
        if (StrUtil.isEmpty(loginDTO.getUsername()) || StrUtil.isEmpty(loginDTO.getPassword())) {
            ResponseDTO dto = new ResponseDTO(ApiCodeUtils.LOGIN_USERNAME_OR_PASSWORD.getCode(), ApiCodeUtils.LOGIN_USERNAME_OR_PASSWORD.getMsg());
            ApiResultUtils.write(response, JSON.toJSONString(dto));
            return null;
        }
        UserEntity userEntity = userEntityService.query().eq("login_acct", loginDTO.getUsername()).one();
        // 查询系统中是否存在用户
        if (userEntity == null) {
            ResponseDTO dto = new ResponseDTO(ApiCodeUtils.LOGIN_USERNAME_NULL.getCode(), ApiCodeUtils.LOGIN_USERNAME_NULL.getMsg());
            ApiResultUtils.write(response, JSON.toJSONString(dto));
            return null;
        }
        if (userEntity.getDisable().equals(1)) {
            ResponseDTO dto = new ResponseDTO(ApiCodeUtils.USER_DISABLE.getCode(), ApiCodeUtils.USER_DISABLE.getMsg());
            ApiResultUtils.write(response, JSON.toJSONString(dto));
            return null;
        }

        if (StrUtil.isNotEmpty(loginDTO.getUsername())) {
            // 获取登录失败的次数
            failNum = userEntity.getLoginFailNum();
        }
        // 失败次数超出则需要输入图片验证码
        if (failNum >= ConstantUtil.SIX) {
            String imgCode = (String) redisUtils.get(loginDTO.getUsername());
            String inputImgCode = loginDTO.getInputImgCode();
            if (StrUtil.isEmpty(inputImgCode)) {
                ResponseDTO dto = new ResponseDTO(ApiCodeUtils.LOGIN_IMG_CODE_NOT_NULL.getCode(), ApiCodeUtils.LOGIN_IMG_CODE_NOT_NULL.getMsg());
                ApiResultUtils.write(response, JSON.toJSONString(dto));
                return null;
            }
            if (!Objects.equals(imgCode, inputImgCode)) {
                ResponseDTO dto = new ResponseDTO(ApiCodeUtils.LOGIN_IMG_CODE_FAIL.getCode(), ApiCodeUtils.LOGIN_IMG_CODE_FAIL.getMsg());
                ApiResultUtils.write(response, JSON.toJSONString(dto));
                return null;
            }
        }
        // 密码从前端传过来的是加密的密码将其解析为明文密码
        String password = Rsa1024Utils.decrypt(loginDTO.getPassword(), Rsa1024Utils.PRIVATE_KEY);
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUsername(), password);
        this.setDetails(request, authenticationToken);
        // 让springSecurity去给我们匹配用户和密码
        return this.getAuthenticationManager().authenticate(authenticationToken);
    }


    /**
     * 登录成功时调用
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) {


        // 获取到当前用户的登录信息
        String jsonString = JSONObject.toJSONString(authResult.getPrincipal());
        LoginDTO loginDTO = JSONObject.parseObject(jsonString, LoginDTO.class);
        // 判断用户是否第一次登录
        UserEntity userEntity = userEntityService.getById(loginDTO.getId());
        // 暂无登录时间则
        if (userEntity.getFirstLoginTime() == null) {
            // 记录第一次登录时间
            userEntityService.update(new UpdateWrapper<UserEntity>().eq("id", userEntity.getId())
                    .set("first_login_time", LocalDateTime.now()).set("last_login_time", LocalDateTime.now()));
        }
        // 登录成功之后返回token相关的信息,生成token
        TokenDTO token = tokenService.getToken(loginDTO, Boolean.TRUE);
        // token储存进redis
        redisUtils.set(userEntity.getId().toString(), JSONObject.toJSONString(token), 60L, TimeUnit.MINUTES);
        // 返回对应的token信息
        ApiResultUtils.write(response, JSONObject.toJSONString(new ResponseDTO<TokenDTO>(ApiCodeUtils.SUCCESS.getCode(), "登录成功", token)));
    }

    /**
     * 登录失败时调用
     *
     * @param request
     * @param response
     * @param failed
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        LoginDTO loginDTO = JSON.parseObject(body, LoginDTO.class);
        userEntityService.update(new UpdateWrapper<UserEntity>().eq("login_acct", loginDTO.getUsername()).setSql("login_fail_num = login_fail_num+1"));
        if (failNum + 1 >= ConstantUtil.SIX) {
            ImgCodeDTO imgCode = imgCodeService.generate(80, 28, loginDTO.getUsername());
            ResponseDTO dto = new ResponseDTO(ApiCodeUtils.JWT_LOGIN_FAILURE.getCode(), ApiCodeUtils.JWT_LOGIN_FAILURE.getMsg(), imgCode);
            ApiResultUtils.write(response, JSON.toJSONString(dto));
            return;
        }
        // 超出指定的次数需要使用图形验证码
        ResponseDTO dto = new ResponseDTO(ApiCodeUtils.JWT_LOGIN_FAILURE.getCode(), ApiCodeUtils.JWT_LOGIN_FAILURE.getMsg());
        ApiResultUtils.write(response, JSON.toJSONString(dto));
    }
}
