package cn.percent.usersystemjdk17.modules.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhangpengju
 * Date: 2021/12/8
 * Time: 9:50
 * Description:
 */
@Data
public class ImgCodeDTO {

    @ApiModelProperty("验证码")
    private String code;

    @ApiModelProperty("图片大小")
    private byte[] imgBytes;

    @ApiModelProperty("生成时间")
    private Long expireTime;
}
