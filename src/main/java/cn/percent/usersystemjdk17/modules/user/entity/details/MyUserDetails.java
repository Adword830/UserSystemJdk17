package cn.percent.usersystemjdk17.modules.user.entity.details;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author: zhangpengju
 * Date: 2021/11/22
 * Time: 10:49
 * Description:
 */
@Getter
@Setter
public class MyUserDetails extends User {

    /**
     * 用户id
     */
    @ApiModelProperty("主键id")
    private Long id;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;

    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    private String nickName;

    /**
     * 构造方法
     * @param username 用户名
     * @param password 密码
     * @param authorities 权限信息
     */
    public MyUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    /**
     * 构造方法
     * @param id 主键
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @param authorities 权限信息
     */
    public MyUserDetails(Long id,String username, String password,String nickName,String email, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.email=email;
        this.nickName=nickName;
    }


}
