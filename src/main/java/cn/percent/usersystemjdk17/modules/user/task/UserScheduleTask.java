package cn.percent.usersystemjdk17.modules.user.task;

import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author: zhangpengju
 * Date: 2021/12/7
 * Time: 17:35
 * Description:
 */
@Configuration
@EnableScheduling
public class UserScheduleTask {

    private final UserEntityService userEntityService;

    public UserScheduleTask(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    /**
     * 每天执行一次清除掉登录失败的次数
     */
    @Scheduled(cron = "0 0 0 * * ?")
    private void configureTasks() {
        userEntityService.update(new UpdateWrapper<UserEntity>().gt("login_fail_num", 0).set("login_fail_num", 0));
    }
}
