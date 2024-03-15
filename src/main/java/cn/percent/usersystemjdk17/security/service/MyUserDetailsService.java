package cn.percent.usersystemjdk17.security.service;

import cn.hutool.core.util.StrUtil;

import cn.percent.usersystemjdk17.modules.role.entity.RoleEntity;
import cn.percent.usersystemjdk17.modules.role.service.RoleEntityService;
import cn.percent.usersystemjdk17.modules.user.entity.AuthEntity;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.entity.details.MyUserDetails;
import cn.percent.usersystemjdk17.modules.user.service.AuthEntityService;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhangpengju
 * Date: 2021/11/19
 * Time: 10:05
 * Description:
 */
@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserEntityService userEntityService;

    @Autowired
    private RoleEntityService roleEntityService;

    @Autowired
    private AuthEntityService authEntityService;

    /**
     * 通过此方法初始化用户的权限
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public MyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StrUtil.isEmpty(username)) {
            return null;
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(12);
        // 从数据库中获取到账号和密码
        UserEntity userEntity = userEntityService.query().eq("login_acct", username)
                .list().get(0);
        // 获取当前账号对应的角色信息
        List<RoleEntity> roleEntityList = roleEntityService.queryByUserIdList(userEntity.getId());
        if (!CollectionUtils.isEmpty(roleEntityList)) {
            roleEntityList.forEach(item -> {
                String role = item.getName();
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role);
                grantedAuthorities.add(simpleGrantedAuthority);
            });
            // 根据角色信息获取到对应的权限
            List<AuthEntity> authEntityList = authEntityService.queryByRoleIdList(roleEntityList);
            if (!CollectionUtils.isEmpty(authEntityList)) {
                authEntityList.forEach(item -> {
                    String auth = item.getName();
                    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(auth);
                    grantedAuthorities.add(simpleGrantedAuthority);
                });
            }
        }

        // 再放入springSecurity中
        return new MyUserDetails(userEntity.getId(), userEntity.getLoginAcct(),
                userEntity.getUserPswd(), userEntity.getUserName(), userEntity.getEmail(), grantedAuthorities);
    }

}
