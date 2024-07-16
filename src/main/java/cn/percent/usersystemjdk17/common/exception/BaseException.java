package cn.percent.usersystemjdk17.common.exception;

import cn.percent.usersystemjdk17.common.utils.ApiCodeUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * @author: zhangpengju
 * Date: 2021/11/25
 * Time: 17:24
 * Description:
 */
@Setter
@Getter
public class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String msg;
    private final Integer statusCode;


    public BaseException(String msg, Integer statusCode) {
        this.msg = msg;
        this.statusCode = statusCode;
    }

    public BaseException(ApiCodeUtils apiCodeUtils) {
        this.msg = apiCodeUtils.getMsg();
        this.statusCode = apiCodeUtils.getCode();
    }

    public BaseException(String msg) {
        this.msg = msg;
        this.statusCode = -1;
    }
}
