package cn.percent.usersystemjdk17.modules.user.service;



import cn.percent.usersystemjdk17.modules.role.entity.RoleEntity;
import cn.percent.usersystemjdk17.modules.user.dto.AuthQuery;
import cn.percent.usersystemjdk17.modules.user.entity.AuthEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author zpj
* @description 针对表【t_auth】的数据库操作Service
* @createDate 2021-11-22 10:44:15
*/
public interface AuthEntityService extends IService<AuthEntity> {
    /**
     * 根据指定的角色集合来获取对应的权限
     * @param roleEntityList
     * @return
     */
    List<AuthEntity> queryByRoleIdList(List<RoleEntity> roleEntityList);

    /**
     * 获取所有的权限信息
     * @param authQuery
     * @return
     */
    Page<AuthEntity> pageList(AuthQuery authQuery);
}
