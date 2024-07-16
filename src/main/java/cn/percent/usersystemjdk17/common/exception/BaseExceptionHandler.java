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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 自定义异常
     */
    @ExceptionHandler(BaseException.class)
    public ApiResultUtils<String> handleBaseException(BaseException e) {
        return ApiResultUtils.fail(e.getStatusCode(), e.getMsg());
    }

    /**
     * 唯一值异常处理handler
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResultUtils<String> handleDuplicateKeyException(DuplicateKeyException e) {
        logger.error(e.getMessage(), e);
        return ApiResultUtils.fail(ApiCodeUtils.DUPLICATE_KEY);
    }

    /**
     * 通用异常处理器
     */
    @ExceptionHandler(Exception.class)
    public ApiResultUtils<String> handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return ApiResultUtils.fail(ApiCodeUtils.FAIL, e.getMessage());
    }

    /**
     * 捕获@PreAuthorize注解无权访问的信息
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResultUtils<String> handleException(AccessDeniedException e) {
        logger.error(e.getMessage(), e);
        return ApiResultUtils.fail(ApiCodeUtils.FORBIDDEN);
    }

    /**
     * 登录失败抛出异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ApiResultUtils<String> handleException(AuthenticationException e) {
        logger.error(e.getMessage(), e);
        return ApiResultUtils.fail(ApiCodeUtils.JWT_LOGIN_FAILURE);
    }


    /**
     * 用来捕捉spring提供的@Validated所抛出的异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResultUtils<String> exceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException) {
        List<FieldError> fieldErrors = methodArgumentNotValidException.getBindingResult().getFieldErrors();
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < fieldErrors.size(); i++) {
            message.append(fieldErrors.get(i).getDefaultMessage());
            if (i < fieldErrors.size() - 1) {
                message.append(";");
            }
        }
        return ApiResultUtils.fail(ApiCodeUtils.PARAMETER_EXCEPTION.getCode(), message.toString());
    }
}
