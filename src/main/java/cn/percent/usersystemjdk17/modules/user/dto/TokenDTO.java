package cn.percent.usersystemjdk17.modules.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: zhangpengju
 * Date: 2021/11/19
 * Time: 11:49
 * Description:
 */
@Data
public class TokenDTO implements Serializable {

    @ApiModelProperty("access token")
    private String accessToken;

    @ApiModelProperty(value = "refresh token")
    private String refreshToken;

    @ApiModelProperty(value = "过期时间")
    private Long expired;

}
