package cn.percent.usersystemjdk17.common.handler;

import cn.percent.usersystemjdk17.common.utils.UserUtils;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author: zhangpengju
 * Date: 2021/11/30
 * Time: 10:42
 * Description:
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 当前的用户信息
        UserEntity userEntity = UserUtils.threadLocal.get();
        if (userEntity != null) {
            this.setFieldValByName("createUser", userEntity.getLoginAcct(), metaObject);
            this.setFieldValByName("createUserId", userEntity.getId(), metaObject);
            this.setFieldValByName("updateUser", userEntity.getLoginAcct(), metaObject);
            this.setFieldValByName("updateUserId", userEntity.getId(), metaObject);
        }
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("del", false, metaObject);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 当前的用户信息
        UserEntity userEntity = UserUtils.threadLocal.get();
        if (userEntity != null) {
            this.setFieldValByName("updateUser", userEntity.getLoginAcct(), metaObject);
            this.setFieldValByName("updateUserId", userEntity.getId(), metaObject);
        }
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
}
