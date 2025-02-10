package cn.percent.usersystemjdk17.security.service.impl;

import cn.percent.usersystemjdk17.common.constant.Constant;
import cn.percent.usersystemjdk17.common.exception.BaseException;
import cn.percent.usersystemjdk17.common.utils.ApiCodeUtils;
import cn.percent.usersystemjdk17.common.utils.JwtUtils;
import cn.percent.usersystemjdk17.common.utils.RedisUtils;
import cn.percent.usersystemjdk17.modules.user.dto.LoginDTO;
import cn.percent.usersystemjdk17.modules.user.dto.TokenDTO;
import cn.percent.usersystemjdk17.modules.user.dto.UserDTO;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import cn.percent.usersystemjdk17.security.service.TokenService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhangpengju
 * Date: 2021/11/19
 * Time: 13:47
 * Description:
 */
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final RedisUtils redisUtils;

    @Value("${jwt.access.token.expiration}")
    private Long tokenExpiration;

    @Value("${jwt.refresh.token.expiration}")
    private Long refreshTokenExpiration;

    private final UserEntityService userEntityService;


    public TokenServiceImpl(RedisUtils redisUtils, UserEntityService userEntityService) {
        this.redisUtils = redisUtils;
        this.userEntityService = userEntityService;
    }

    @Override
    public TokenDTO getToken(LoginDTO loginDTO, Boolean refreshed) {
        TokenDTO tokenDTO = new TokenDTO();
        // 设置过期时间戳
        tokenDTO.setExpired(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(tokenExpiration));
        // 根据用户的信息来生成相关的token
        String accessToken = JwtUtils.createToken(loginDTO.getId(), loginDTO.getUsername(),
                TimeUnit.MINUTES.toMillis(tokenExpiration), "accessToken");
        tokenDTO.setAccessToken(accessToken);
        // 是否生成刷新token
        if (Boolean.TRUE.equals(refreshed)) {
            String refreshToken = JwtUtils.createToken(loginDTO.getId(), loginDTO.getUsername(),
                    TimeUnit.MINUTES.toMillis(refreshTokenExpiration), "refreshToken");
            tokenDTO.setRefreshToken(refreshToken);
        }
        return tokenDTO;
    }

    @Override
    public Claims checkToken(String token) {
        Claims claims = JwtUtils.getClaims(token);
        Long userId = JSON.parseObject(JSON.toJSONString(claims.get("userId")),
                Long.class);
        if (redisUtils.get("token:" + userId) != null) {
            TokenDTO tokenDTO = JSON.parseObject((String) redisUtils.get("token:" + userId), TokenDTO.class);
            if (Objects.equals(token, tokenDTO.getAccessToken())) {
                return claims;
            }
        }
        throw new BaseException(ApiCodeUtils.JWT_EXPIRED);
    }


    @Override
    public TokenDTO refreshToken(UserDTO userDTO) {
        String loginAcct = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tokenString = "";
        if (userDTO.getLoginAcct().equals(loginAcct)) {
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserEntity::getLoginAcct, loginAcct);
            UserEntity userEntity = userEntityService.getBaseMapper().selectOne(queryWrapper);
            tokenString = redisUtils.get(String.valueOf(userEntity.getId())).toString();
            String accessToken = JSONObject.parseObject(tokenString).getString(Constant.ACCESS_TOKEN);
            Claims claims = this.checkToken(accessToken);
            if (claims == null) {
                LoginDTO loginDTO = new LoginDTO();
                loginDTO.setUsername(userEntity.getLoginAcct());
                loginDTO.setId(userEntity.getId());
                TokenDTO token = this.getToken(loginDTO, Boolean.TRUE);
                // 储存token进redis
                redisUtils.set(loginDTO.getId().toString(), JSONObject.toJSONString(token), refreshTokenExpiration, TimeUnit.MINUTES);
                return token;
            }
        }
        throw new BaseException(ApiCodeUtils.NOT_LOGGED_IN);
    }
}