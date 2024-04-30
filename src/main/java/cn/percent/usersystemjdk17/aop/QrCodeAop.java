package cn.percent.usersystemjdk17.aop;

import cn.percent.usersystemjdk17.common.server.WebSocketServer;
import cn.percent.usersystemjdk17.modules.user.dto.QrCodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: zhangpengju
 * Date: 2021/12/20
 * Time: 9:37
 * Description:
 */
@Slf4j
@Aspect
public class QrCodeAop {

    /**
     * 切点
     */
    private static final String POINTCUT = "execution(public * cn.percent.system.modules.user.service..*.*(..)))";
    @Autowired
    private WebSocketServer webSocketServer;

    @Pointcut("execution(public * cn.percent.system.modules.user.service..*.buildQrCode(..)))")
    public void buildQrCode() {
    }

    /**
     * 方法执行之前使用
     *
     * @param joinPoint
     */
    @Before("buildQrCode()")
    public void doBefore(JoinPoint joinPoint) {
    }

    /**
     * 方法执行之后使用
     *
     * @param joinPoint
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
     * @param result
     */
    @AfterReturning(returning = "result", pointcut = "buildQrCode()")
    public void doAfterReturn(Object result) {
        //获取到当前使用方法的返回值
        QrCodeDTO qrCodeDTO = (QrCodeDTO) result;
        String uuid = qrCodeDTO.getUuid();

        log.info("result {}", result);
    }


}
