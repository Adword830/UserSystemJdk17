package cn.percent.usersystemjdk17.security.service.impl;

import cn.percent.usersystemjdk17.common.exception.BaseException;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import cn.percent.usersystemjdk17.security.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * @author: zhangpengju
 * Date: 2021/12/7
 * Time: 15:38
 * Description:
 */
@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private UserEntityService userEntityService;

    @Override
    public Boolean checkUser(Authentication authentication, Long id) {
        UserEntity userEntity = userEntityService.getById(id);
        if (userEntity.getLoginAcct().equals(authentication.getPrincipal())) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean isUser(Long id) {
        UserEntity userEntity = userEntityService.getById(id);
        if (userEntity == null) {
            throw new BaseException("用户信息不存在");
        }
        return true;
    }
}
