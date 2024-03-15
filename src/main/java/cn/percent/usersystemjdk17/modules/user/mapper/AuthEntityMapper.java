package cn.percent.usersystemjdk17.modules.user.mapper;


import cn.percent.usersystemjdk17.modules.role.entity.RoleEntity;
import cn.percent.usersystemjdk17.modules.user.entity.AuthEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author zpj
* @description 针对表【t_auth】的数据库操作Mapper
* @createDate 2021-11-22 10:44:15
* @Entity cn.percent.system.entity.AuthEntity
*/
@Mapper
public interface AuthEntityMapper extends BaseMapper<AuthEntity> {
    /**
     * 根据指定的集合来获取对应的权限列表
     * @param roleEntityList
     * @return
     */
    List<AuthEntity> queryByRoleIdList(@Param("roleList") List<RoleEntity> roleEntityList);
}




