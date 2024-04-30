package cn.percent.usersystemjdk17.modules.wechat.task;

import cn.percent.usersystemjdk17.modules.wechat.service.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 这个类代表了微信相关的任务调度器。
 * 它使用Spring的@Scheduled注解来在特定时间安排任务。
 *
 * @author pengju.zhang
 * @date 2022-08-31 19:45
 */
@Component
public class WechatTask {

    private final SendMessage sendMessage;

    /**
     * WechatTask类的构造函数。
     *
     * @param sendMessage 用于发送消息的SendMessage服务
     */
    public WechatTask(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
    }

    /**
     * 这个任务被安排在每天7:30运行。
     * 它发送一个ID为1的消息。
     */
    @Scheduled(cron = "0 30 7 * * ?")
    public void task() {
        sendMessage.send(1);
    }

    /**
     * 这个任务被安排在每天11:30运行。
     * 它发送一个ID为2的消息。
     */
    @Scheduled(cron = "0 30 11 * * ?")
    public void task2() {
        sendMessage.send(2);
    }

    /**
     * 这个任务被安排在每天17:30运行。
     * 它发送一个ID为3的消息。
     */
    @Scheduled(cron = "0 30 17 * * ?")
    public void task3() {
        sendMessage.send(3);
    }
}