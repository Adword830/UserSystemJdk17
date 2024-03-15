package cn.percent.usersystemjdk17.modules.user.dto;

import cn.percent.usersystemjdk17.common.enums.QrCodeStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: zhangpengju
 * Date: 2021/12/9
 * Time: 11:11
 * Description:
 */
@Api("QrCodeDTO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeDTO implements Serializable {

    @ApiModelProperty("二维码内容")
    private String qrCode;

    @ApiModelProperty("uuid")
    private String uuid;

    @ApiModelProperty("二维码状态")
    private QrCodeStatusEnum status;

    public QrCodeDTO(String qrCode,QrCodeStatusEnum status){
        this.qrCode = qrCode;
        this.status = status;
    }
}
