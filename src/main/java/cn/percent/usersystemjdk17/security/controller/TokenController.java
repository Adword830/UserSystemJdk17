package cn.percent.usersystemjdk17.security.controller;


import cn.percent.usersystemjdk17.common.constant.Constant;
import cn.percent.usersystemjdk17.common.utils.ApiCodeUtils;
import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import cn.percent.usersystemjdk17.common.utils.RedisUtils;
import cn.percent.usersystemjdk17.modules.user.dto.LoginDTO;
import cn.percent.usersystemjdk17.modules.user.dto.TokenDTO;
import cn.percent.usersystemjdk17.modules.user.dto.UserDTO;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import cn.percent.usersystemjdk17.security.service.TokenService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author pengju.zhang
 * @date 2023-09-19 11:24
 */
@RestController
@RequestMapping("/token")
public class TokenController {

    private final TokenService tokenService;

    private final RedisUtils redisUtils;
    private final UserEntityService userEntityService;
    @Value("${jwt.refresh.token.expiration}")
    private Long refreshTokenExpiration;

    public TokenController(TokenService tokenService, RedisUtils redisUtils, UserEntityService userEntityService) {
        this.tokenService = tokenService;
        this.redisUtils = redisUtils;
        this.userEntityService = userEntityService;
    }

    /**
     * 刷新token
     *
     * @param userDTO
     * @return
     */
    @PostMapping("/refreshToken")
    public ApiResultUtils<TokenDTO> refreshToken(@RequestBody UserDTO userDTO) {
        String loginAcct = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tokenString = "";
        if (userDTO.getLoginAcct().equals(loginAcct)) {
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserEntity::getLoginAcct, loginAcct);
            UserEntity userEntity = userEntityService.getBaseMapper().selectOne(queryWrapper);
            tokenString = redisUtils.get(String.valueOf(userEntity.getId())).toString();
            String accessToken = JSONObject.parseObject(tokenString).getString(Constant.ACCESS_TOKEN);
            Claims claims = tokenService.checkToken(accessToken);
            if (claims == null) {
                LoginDTO loginDTO = new LoginDTO();
                loginDTO.setUsername(userEntity.getLoginAcct());
                loginDTO.setId(userEntity.getId());
                TokenDTO token = tokenService.getToken(loginDTO, Boolean.TRUE);
                // 储存token进redis
                redisUtils.set(loginDTO.getId().toString(), JSONObject.toJSONString(token), refreshTokenExpiration, TimeUnit.MINUTES);
                return ApiResultUtils.ok(token);
            }
        }
        // 否则直接返回redis中的token
        return ApiResultUtils.fail(ApiCodeUtils.LOGON__FAILURE);
    }
}
