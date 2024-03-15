package cn.percent.usersystemjdk17.common.exception;

import org.springframework.http.HttpStatus;

/**
 * @author: zhangpengju
 * Date: 2021/11/25
 * Time: 17:24
 * Description:
 */
public class BaseException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private String msg;
  private HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

  public BaseException(String msg) {
    super(msg);
    this.msg = msg;
  }

  public BaseException(String msg, Throwable e) {
    super(msg, e);
    this.msg = msg;
  }

  public BaseException(String msg, HttpStatus statusCode) {
    super(msg);
    this.msg = msg;
    this.statusCode = statusCode;
  }

  public BaseException(String msg, HttpStatus statusCode, Throwable e) {
    super(msg, e);
    this.msg = msg;
    this.statusCode = statusCode;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public HttpStatus getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(HttpStatus statusCode) {
    this.statusCode = statusCode;
  }

}
