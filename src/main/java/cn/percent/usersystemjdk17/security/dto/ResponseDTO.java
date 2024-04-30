package cn.percent.usersystemjdk17.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: zhangpengju
 * Date: 2021/12/3
 * Time: 9:54
 * Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseDTO<T> implements Serializable {

    private Integer code;

    private String msg;
    private T data;

    public ResponseDTO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;

    }

}
