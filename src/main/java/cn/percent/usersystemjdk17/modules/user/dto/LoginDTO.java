package cn.percent.usersystemjdk17.modules.user.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author: zhangpengju
 * Date: 2021/11/18
 * Time: 15:41
 * Description:
 */
@Valid
@Data
@AllArgsConstructor
@NoArgsConstructor
@Api("登录相关的参数")
public class LoginDTO {

   @ApiModelProperty("主键id")
   private Long id;

   @ApiModelProperty("登录名")
   private String username;

   @ApiModelProperty("登录密码")
   private String password;

   @ApiModelProperty("邮箱")
   private String email;

   @ApiModelProperty("记住密码")
   private Boolean rememberMe;

   @ApiModelProperty("用户输入的图片验证码")
   private String inputImgCode;

   public LoginDTO(Long id,String username){
      this.id = id;
      this.username = username;
   }
}
