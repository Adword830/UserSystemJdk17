package cn.percent.usersystemjdk17.modules.user.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;

/**
 * @author: zhangpengju
 * Date: 2021/12/21
 * Time: 10:50
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Api("用户修改密码的参数")
public class UpdateUserQuery implements Serializable {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("密码")
    private String pwd;

    @ApiModelProperty("新密码")
    private String newPwd;

    @ApiModelProperty("是否为找回密码修改密码")
    private Boolean flag;

    @ApiModelProperty("找回密码的邮箱")
    private String email;

    @ApiModelProperty("邮箱验证码")
    private String code;
}
