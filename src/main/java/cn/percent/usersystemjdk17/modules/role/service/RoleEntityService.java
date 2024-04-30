package cn.percent.usersystemjdk17.modules.role.service;


import cn.percent.usersystemjdk17.modules.role.entity.RoleEntity;
import cn.percent.usersystemjdk17.modules.user.dto.RoleDTO;
import cn.percent.usersystemjdk17.modules.user.dto.RoleQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author zpj
 * @description 针对表【t_role】的数据库操作Service
 * @createDate 2021-11-22 10:44:15
 */
public interface RoleEntityService extends IService<RoleEntity> {
    /**
     * 通过userId获取对应的权限信息
     *
     * @param userId
     * @return
     */
    List<RoleEntity> queryByUserIdList(Long userId);

    /**
     * 分页查询
     *
     * @param roleQuery
     * @return
     */
    Page<RoleEntity> pageList(RoleQuery roleQuery);

    /**
     * 给角色分配权限
     *
     * @param roleQuery
     */
    void allotAuth(RoleQuery roleQuery);

    /**
     * 修改角色
     *
     * @param roleQuery
     */
    void updateRole(RoleQuery roleQuery);

    /**
     * 获取角色树
     *
     * @return
     */
    List<RoleDTO> roleTree();
}
