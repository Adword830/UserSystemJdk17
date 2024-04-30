package cn.percent.usersystemjdk17.modules.user.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: zhangpengju
 * Date: 2021/11/29
 * Time: 14:28
 * Description:
 */
@Api("分配权限的参数")
@Data
public class AuthQuery implements Serializable {

    @ApiModelProperty("权限id")
    private Long id;

    @ApiModelProperty("权限名称")
    private String name;

    @ApiModelProperty("角色id")
    private String roleId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("权限id")
    private Long[] authId;

    @ApiModelProperty(value = "每一页的显示条数", required = true, example = "1")
    private Long pageSize;

    @ApiModelProperty(value = "每一页的数量", required = true, example = "10")
    private Long pageNum;

}
