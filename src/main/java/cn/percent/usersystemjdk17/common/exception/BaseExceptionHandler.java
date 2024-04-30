package cn.percent.usersystemjdk17.common.exception;

import cn.percent.usersystemjdk17.common.utils.ApiCodeUtils;
import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 异常处理器
 *
 * @author cuids
 * @date 2016年10月27日 下午10:16:19
 */
@RestControllerAdvice
public class BaseExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 自定义异常
     */
    @ExceptionHandler(BaseException.class)
    public ApiResultUtils<String> handleBaseException(BaseException e) {
        return ApiResultUtils.result(ApiCodeUtils.FAIL, e.getMsg(), null);
    }

    /**
     * 唯一值异常处理handler
     *
     * @param e
     * @return
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResultUtils<String> handleDuplicateKeyException(DuplicateKeyException e) {
        logger.error(e.getMessage(), e);
        return ApiResultUtils.fail("数据库中已存在该记录");
    }

    /**
     * 通用异常处理器
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ApiResultUtils handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return ApiResultUtils.result(ApiCodeUtils.FAIL, e.getMessage(), null);
    }

    /**
     * 捕获@PreAuthorize注解无权访问的信息
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResultUtils handleException(AccessDeniedException e) {
        logger.error(e.getMessage(), e);
        return ApiResultUtils.result(ApiCodeUtils.FORBIDDEN, ApiCodeUtils.FORBIDDEN.getMsg(), null);
    }

    /**
     * 登录失败抛出异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AuthenticationException.class)
    public ApiResultUtils handleException(AuthenticationException e) {
        logger.error(e.getMessage(), e);
        return ApiResultUtils.result(ApiCodeUtils.JWT_LOGIN_FAILURE, ApiCodeUtils.JWT_LOGIN_FAILURE.getMsg(), null);
    }


    /**
     * 用来捕捉spring提供的@Validated所抛出的异常
     *
     * @param methodArgumentNotValidException
     * @return
     * @throws Exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResultUtils exceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException) throws Exception {
        List<FieldError> fieldErrors = methodArgumentNotValidException.getBindingResult().getFieldErrors();
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < fieldErrors.size(); i++) {
            message.append(fieldErrors.get(i).getDefaultMessage());
            if (i < fieldErrors.size() - 1) {
                message.append(";");
            }
        }
        return ApiResultUtils.result(ApiCodeUtils.PARAMETER_EXCEPTION, message.toString(), null);
    }
}
