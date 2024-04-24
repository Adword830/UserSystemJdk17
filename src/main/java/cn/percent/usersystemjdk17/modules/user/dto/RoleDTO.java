package cn.percent.usersystemjdk17.modules.user.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author pengju.zhang
 * @date 2023-09-14 15:04
 */
@Data
public class RoleDTO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 角色名对应角色名称
     */
    private String value;

    /**
     * 角色描述
     */
    private String label;

    /**
     * 子集
     */
    List<RoleDTO> children;

}
