package cn.percent.usersystemjdk17.modules.wechat.task;

import cn.percent.usersystemjdk17.modules.wechat.service.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author pengju.zhang
 * @date 2022-08-31 19:45
 */
@Component
public class WechatTask {

    @Autowired
    private SendMessage sendMessage;

    @Scheduled(cron = "0 30 7 * * ?")
    public void task(){
        sendMessage.send(1);
    }

    @Scheduled(cron = "0 30 11 * * ?")
    public void task2(){
        sendMessage.send(2);
    }

    @Scheduled(cron = "0 30 17 * * ?")
    public void task3(){
        sendMessage.send(3);
    }
}
