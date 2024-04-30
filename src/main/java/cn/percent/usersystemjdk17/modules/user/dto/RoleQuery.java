package cn.percent.usersystemjdk17.modules.user.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: zhangpengju
 * Date: 2021/11/29
 * Time: 10:04
 * Description:
 */
@Api("分配角色的Query")
@Data
public class RoleQuery implements Serializable {

    @ApiModelProperty("角色id")
    private Long id;

    @ApiModelProperty("父id")
    private Long parentId;

    @ApiModelProperty(value = "用户id在查询当前登录的用户管理的用户", required = true)
    private String userId;

    @ApiModelProperty("角色名称，新增或者修改角色时使用")
    private String name;

    @ApiModelProperty("角色描述，新增或者修改角色时使用")
    private String nameDesc;

    @ApiModelProperty("相关的权限id,新增或者修改角色时使用")
    private Long[] authIds;

    @ApiModelProperty(value = "每一页的显示条数,查询角色时使用", required = true, example = "1")
    private Long pageSize;

    @ApiModelProperty(value = "每一页的数量,查询角色时使用", required = true, example = "10")
    private Long pageNum;

}
