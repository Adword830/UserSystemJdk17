package cn.percent.usersystemjdk17.security.service.impl;

import cn.percent.usersystemjdk17.common.exception.BaseException;
import cn.percent.usersystemjdk17.common.utils.ApiCodeUtils;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import cn.percent.usersystemjdk17.security.service.SecurityService;
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

    private final UserEntityService userEntityService;

    public SecurityServiceImpl(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    @Override
    public Boolean checkUser(Authentication authentication, Long id) {
        UserEntity userEntity = userEntityService.getById(id);
        return userEntity.getLoginAcct().equals(authentication.getPrincipal());
    }

    @Override
    public Boolean isUser(Long id) {
        UserEntity userEntity = userEntityService.getById(id);
        if (userEntity == null) {
            throw new BaseException(ApiCodeUtils.USER_NOT_EXIST);
        }
        return true;
    }
}
