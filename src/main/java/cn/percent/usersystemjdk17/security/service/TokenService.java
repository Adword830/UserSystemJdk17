package cn.percent.usersystemjdk17.security.service;

import cn.percent.usersystemjdk17.modules.user.dto.LoginDTO;
import cn.percent.usersystemjdk17.modules.user.dto.TokenDTO;
import io.jsonwebtoken.Claims;

/**
 * @author: zhangpengju
 * Date: 2021/11/19
 * Time: 11:48
 * Description:
 */
public interface TokenService {

    /**
     * 生成token和刷新token
     *
     * @param loginDTO  用户登录信息
     * @param refreshed 是否生成刷新token
     * @return
     */
    TokenDTO getToken(LoginDTO loginDTO, Boolean refreshed);

    /**
     * 校验当前token是否合法
     *
     * @param token
     * @return
     */
    Claims checkToken(String token);
}
