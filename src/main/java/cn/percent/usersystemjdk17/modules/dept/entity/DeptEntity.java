package cn.percent.usersystemjdk17.modules.dept.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangpengju
 * @TableName dept
 */
@Data
public class DeptEntity implements Serializable {

    /**
     * 主键
     */
    @NotNull(message = "[主键]不能为空")
    @ApiModelProperty("主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long deptId;
    /**
     * 部门名称
     */
    @Size(max = 32, message = "编码长度不能超过32")
    @ApiModelProperty("部门名称")
    @Length(max = 32, message = "编码长度不能超过32")
    private String deptName;
    /**
     * 父id
     */
    @ApiModelProperty("父id")
    private Long deptParentId;
    /**
     * 创建人id
     */
    @ApiModelProperty("创建人id")
    private Long createUserId;
    /**
     * 创建人名称
     */
    @Size(max = 32, message = "编码长度不能超过32")
    @ApiModelProperty("创建人名称")
    @Length(max = 32, message = "编码长度不能超过32")
    private String createUser;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createUserTime;
    /**
     * 修改人id
     */
    @ApiModelProperty("修改人id")
    private Long updateUserId;
    /**
     * 修改人名称
     */
    @Size(max = 32, message = "编码长度不能超过32")
    @ApiModelProperty("修改人名称")
    @Length(max = 32, message = "编码长度不能超过32")
    private String updateUser;
    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date updateUserTime;
    /**
     * 删除标识0.否 1.是
     */
    @ApiModelProperty("删除标识0.否 1.是")
    private Integer del;
}
