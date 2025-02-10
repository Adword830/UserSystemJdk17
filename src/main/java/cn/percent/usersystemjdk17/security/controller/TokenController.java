package cn.percent.usersystemjdk17.security.controller;


import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import cn.percent.usersystemjdk17.modules.user.dto.TokenDTO;
import cn.percent.usersystemjdk17.modules.user.dto.UserDTO;
import cn.percent.usersystemjdk17.security.service.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pengju.zhang
 * @date 2023-09-19 11:24
 */
@RestController
@RequestMapping("/token")
public class TokenController {

    private final TokenService tokenService;


    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * 刷新token
     *
     * @param userDTO 用户信息
     * @return token
     */
    @PostMapping("/refreshToken")
    public ApiResultUtils<TokenDTO> refreshToken(@RequestBody UserDTO userDTO) {
        return ApiResultUtils.ok(tokenService.refreshToken(userDTO));
    }
}