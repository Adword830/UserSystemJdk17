package cn.percent.usersystemjdk17.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author zpj
 * @TableName user
 */
@TableName(value ="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {
    /**
     * 主键 JsonFormat解决雪花算法丢失精度问题
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 登录名
     */
    @TableField(value = "login_acct")
    private String loginAcct;

    /**
     * 登录密码
     */
    private String userPswd;

    /**
     * 昵称
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 登录失败的次数
     */
    @TableField(value = "login_fail_num")
    private Integer loginFailNum;

    /**
     * 获取验证码的次数
     */
    @TableField(value = "code_num")
    private Integer codeNum;

    /**
     * 创建时间
     */
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 首次登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "first_login_time")
    private LocalDateTime firstLoginTime;

    /**
     * 上一次最后最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 是否禁用
     */
    @TableField(value = "disable")
    private Boolean disable;

    /**
     * 删除标识
     */
    @TableField(value = "del", fill = FieldFill.INSERT)
    @TableLogic(value = "0", delval = "1")
    private Boolean del;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}