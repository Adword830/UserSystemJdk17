package cn.percent.usersystemjdk17.aop;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.percent.usersystemjdk17.common.utils.ApiCodeUtils;
import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import cn.percent.usersystemjdk17.common.utils.IpUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class LogAop {

    @Pointcut("execution(public * cn.percent.usersystemjdk17..*.controller..*.*(..))")
    private void point() {
    }

    @Around("point()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        var request = attributes.getRequest();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("请求路径", request.getRequestURI());
        map.put("ip地址", IpUtils.getRequestIp());
        map.put("请求方式", request.getMethod());
        map.put("contentType", request.getContentType());
        map.put("time", LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd hh:mm:ss"));
        map.put("token", request.getHeader("Authorization"));

        log.info("请求参数:{}", JSON.toJSONString(map));

        Object result = joinPoint.proceed();

        if (result instanceof ApiResultUtils apiResult) {
            String responseResultInfo = JSON.toJSONString(apiResult);
            if (apiResult.getCode() == ApiCodeUtils.SUCCESS.getCode()) {
                log.info("返回的参数:{}",responseResultInfo);
            } else {
                log.warn("返回的参数:{}",responseResultInfo);
            }
        }
        return result;
    }
}