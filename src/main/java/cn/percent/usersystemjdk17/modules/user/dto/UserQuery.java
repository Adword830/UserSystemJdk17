package cn.percent.usersystemjdk17.modules.user.dto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author: zhangpengju
 * Date: 2021/11/25
 * Time: 17:30
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Api("用户信息相关的查询类")
public class UserQuery {


    @ApiModelProperty(value = "用户id")
    private Long id;

    @ApiModelProperty("角色id列表，给用户分配角色时使用")
    private List<Long> roleIds;

    @NotNull(message = "登录名称不能为空")
    @ApiModelProperty(value = "登录名")
    private String loginAcct;

    @ApiModelProperty(value = "上一次登录时间")
    private List<String> firstLoginTime;

    @ApiModelProperty(value = "用户昵称")
    private String userName;

    @NotNull(message = "密码不能为空")
    @ApiModelProperty(value = "用户密码,注册时使用")
    private String userPswd;

    @NotNull(message = "邮箱不能为空")
    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "是否禁用")
    private Integer disable;

    @NotNull(message = "验证码不能为空")
    @Length(max = 6, min = 4, message = "验证码长度不符合4-6位")
    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(value = "页码",required = true,example = "1")
    private Long pageNum;

    @ApiModelProperty(value = "每一页显示多少条",required = true,example = "10")
    private Long pageSize;
}
