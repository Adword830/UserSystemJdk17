package cn.percent.usersystemjdk17.modules.user.mapper;

import cn.percent.usersystemjdk17.modules.role.entity.RoleEntity;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author zpj
* @description 针对表【t_admin】的数据库操作Mapper
* @createDate 2021-11-22 10:44:15
* @Entity cn.percent.system.entity.UserEntity
*/
@Mapper
public interface UserEntityMapper extends BaseMapper<UserEntity> {

    /**
     * 给用户分配角色
     * @param userId 用户id
     * @param roleIds 角色id列表
     */
    void allotRole(@Param("userId") Long userId, @Param("roleIds")List<Long> roleIds);

    /**
     * 根据用户id查询对应角色信息
     * @param userId
     * @return
     */
    List<RoleEntity> selectRoleByUserId(@Param("userId")Long userId);

    /**
     * 根据用户id和roleIds删除用户的角色信息
     * @param userId
     * @param roleIds
     */
    void deleteRoleByUserIdAndUserId(@Param("userId")Long userId,@Param("roleIds") List<Long> roleIds);
}




