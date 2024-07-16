package cn.percent.usersystemjdk17.aop;

import cn.percent.usersystemjdk17.modules.user.dto.QrCodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * @author: zhangpengju
 * Date: 2021/12/20
 * Time: 9:37
 * Description:
 */
@Slf4j
@Aspect
public class QrCodeAop {


    @Pointcut("execution(public * cn.percent.usersystemjdk17.modules.user.service..*.buildQrCode(..)))")
    public void buildQrCode() {
    }

    /**
     * 方法执行之后使用
     *
     */
    @After("buildQrCode()")
    public void doAfter(JoinPoint joinPoint) {
        // 二维码生成成功之后
        Object[] args = joinPoint.getArgs();
        log.info("二维码uuid {} 扫描成功", args[0]);
    }

    /**
     * 方法的返回值
     *
     */
    @AfterReturning(returning = "result", pointcut = "buildQrCode()")
    public void doAfterReturn(Object result) {
        //获取到当前使用方法的返回值
        QrCodeDTO qrCodeDTO = (QrCodeDTO) result;

        log.info("result {}", result);
    }


}
