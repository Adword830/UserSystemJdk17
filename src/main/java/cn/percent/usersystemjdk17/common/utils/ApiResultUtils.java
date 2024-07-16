package cn.percent.usersystemjdk17.common.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * @author: zhangpengju
 * Date: 2021/11/19
 * Time: 14:20
 * Description:
 */
@Slf4j
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
public class ApiResultUtils<T> {


    private int code;

    private T data;

    private String msg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    public ApiResultUtils(ApiCodeUtils apiCode) {
        this.code = apiCode.getCode();
        this.msg = apiCode.getMsg();
    }

    public ApiResultUtils(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResultUtils(String msg) {
        this.code = -1;
        this.msg = msg;
    }

    /**
     * 失败带数据
     *
     * @param apiCode 错误码
     * @param data    数据
     * @return 结果
     */
    public static <T> ApiResultUtils<T> fail(ApiCodeUtils apiCode, T data) {
        ApiResultUtils<T> resultUtils = new ApiResultUtils<>(apiCode);
        resultUtils.setData(data);
        resultUtils.setTime(LocalDateTime.now());
        return resultUtils;
    }

    /**
     * 失败不带数据
     *
     * @param apiCode 错误码
     * @return 结果
     */
    public static <T> ApiResultUtils<T> fail(ApiCodeUtils apiCode) {
        ApiResultUtils<T> resultUtils = new ApiResultUtils<>(apiCode);
        resultUtils.setTime(LocalDateTime.now());
        return resultUtils;
    }

    /**
     * 失败带自定义code和msg
     *
     * @param code 错误码
     * @param msg  错误信息
     * @return 结果
     */
    public static <T> ApiResultUtils<T> fail(Integer code, String msg) {
        ApiResultUtils<T> resultUtils = new ApiResultUtils<>(code, msg);
        resultUtils.setTime(LocalDateTime.now());
        return resultUtils;
    }

    /**
     * 失败带自定义msg
     *
     * @param msg  错误信息
     * @return 结果
     */
    public static <T> ApiResultUtils<T> fail(String msg) {
        ApiResultUtils<T> resultUtils = new ApiResultUtils<>(msg);
        resultUtils.setTime(LocalDateTime.now());
        return resultUtils;
    }


    /**
     * 成功带数据
     *
     * @param data 数据
     * @return 结果
     */
    public static <T> ApiResultUtils<T> ok(T data) {
        ApiResultUtils<T> resultUtils = new ApiResultUtils<>(ApiCodeUtils.ok);
        resultUtils.setData(data);
        resultUtils.setTime(LocalDateTime.now());
        return resultUtils;
    }

    public static <T> ApiResultUtils<T> ok() {
        ApiResultUtils<T> resultUtils = new ApiResultUtils<>(ApiCodeUtils.ok);
        resultUtils.setTime(LocalDateTime.now());
        return resultUtils;
    }


    public static void write(HttpServletResponse response, String body) {
        write(response, null, body);
    }

    public static void write(HttpServletResponse response, Integer status, String body) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        if (status == null) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            response.setStatus(status);
        }
        if (body != null) {
            try (PrintWriter writer = response.getWriter()) {
                writer.write(body);
            } catch (IOException e) {
                log.error("response write fail:", e);
            }
        }
    }
}
