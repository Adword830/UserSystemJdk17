package cn.percent.usersystemjdk17.security.handler;

import cn.hutool.core.util.StrUtil;
import cn.percent.usersystemjdk17.common.utils.ApiCodeUtils;
import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import cn.percent.usersystemjdk17.common.utils.RedisUtils;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import cn.percent.usersystemjdk17.security.dto.ResponseDTO;
import cn.percent.usersystemjdk17.security.service.TokenService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.time.LocalDateTime;

/**
 * @author: zhangpengju
 * Date: 2021/12/2
 * Time: 20:06
 * Description:
 */
public class MyLogoutHandler implements LogoutHandler {

    public static final String bearer = "Bearer ";

    private TokenService tokenService;

    private UserEntityService userEntityService;

    private RedisUtils redisUtils;

    public MyLogoutHandler(TokenService tokenService, UserEntityService userEntityService, RedisUtils redisUtils) {
        this.tokenService = tokenService;
        this.userEntityService = userEntityService;
        this.redisUtils = redisUtils;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String bearerToken = request.getHeader("Authorization");
        // 没有token传过来，或者token不符合规则
        if (StrUtil.isEmpty(bearerToken) || !bearerToken.startsWith(bearer)) {
            ResponseDTO dto = new ResponseDTO(ApiCodeUtils.NOT_LOGGED_IN.getCode(), ApiCodeUtils.NOT_LOGGED_IN.getMsg());
            ApiResultUtils.write(response, JSONObject.toJSONString(dto));
            return;
        }
        String token = bearerToken.replace(bearer, "");
        Claims claims = tokenService.checkToken(token);
        if (claims != null) {
            Long userId = (Long) claims.get("userId");
            // 修改最后的登录时间
            userEntityService.update(new UpdateWrapper<UserEntity>().eq("id", userId).set("last_login_time", LocalDateTime.now()));
            // 把token进行删除
            redisUtils.remove(String.valueOf(userId));
        }
    }
}
