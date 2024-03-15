package cn.percent.usersystemjdk17.modules.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: zhangpengju
 * Date: 2021/11/25
 * Time: 17:28
 * Description:
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO implements Serializable {

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 登录名
     */
    private String loginAcct;


    /**
     * 昵称
     */
    private String userName;

    /**
     * 邮箱
     */
    private String email;

    public UserDTO(Long id, String loginAcct, String email) {
        this.id = id;
        this.loginAcct = loginAcct;
        this.email = email;
    }

}
