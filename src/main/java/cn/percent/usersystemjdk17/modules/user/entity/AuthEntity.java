package cn.percent.usersystemjdk17.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zpj
 * @TableName auth
 */
@TableName(value = "auth")
@Data
public class AuthEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 权限名称或者url
     */
    private String name;
    @TableField("parent_id")
    private Long parentId;
    /**
     * 权限描述
     */
    @TableField("desc_name")
    private String descName;
    /**
     * 是否为url
     */
    private boolean isUrl;
    /**
     *
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     *
     */
    @TableField(fill = FieldFill.INSERT)
    private String createUser;
    /**
     *
     */
    @TableField(fill = FieldFill.INSERT)
    private String createUserId;
    /**
     *
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     *
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateUser;
    /**
     *
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateUserId;
    /**
     *
     */
    @TableField(fill = FieldFill.INSERT)
    @TableLogic(value = "0", delval = "1")
    private Boolean del;
}