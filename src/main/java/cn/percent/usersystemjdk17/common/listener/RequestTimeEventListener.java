package cn.percent.usersystemjdk17.common.listener;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * @author pengju.zhang
 * @date 2024-12-02 11:42
 */
@Slf4j
@Component
public class RequestTimeEventListener implements ApplicationListener<ServletRequestHandledEvent> {
    @Override
    public void onApplicationEvent(ServletRequestHandledEvent event) {
        Throwable failureCause = event.getFailureCause();
        String failureMessage = failureCause == null ? "" : failureCause.getMessage();
        if (failureCause != null) {
            failureMessage = failureCause.getMessage();
        }
        String clientAddress = event.getClientAddress();
        String requestUrl = event.getRequestUrl();
        String method = event.getMethod();
        long processingTimeMillis = event.getProcessingTimeMillis();
        if (CharSequenceUtil.isEmpty(failureMessage)) {
            log.info("clientAddress = {} requestUrl = {} method = {} costTime = {}", clientAddress, requestUrl, method, processingTimeMillis);
        } else {
            log.error("clientAddress = {} requestUrl = {} method = {} costTime = {} error = {}", clientAddress, requestUrl, method, processingTimeMillis, failureMessage);
        }
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
