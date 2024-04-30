package cn.percent.usersystemjdk17.modules.role.mapper;

import cn.percent.usersystemjdk17.modules.role.entity.RoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zpj
 * @description 针对表【t_role】的数据库操作Mapper
 * @createDate 2021-11-22 10:44:15
 * @Entity cn.percent.system.entity.RoleEntity
 */
@Mapper
public interface RoleEntityMapper extends BaseMapper<RoleEntity> {
    /**
     * 根据用户id获取到对应的角色信息
     *
     * @param userId
     * @return
     */
    List<RoleEntity> queryByUserIdList(@Param("userId") Long userId);

    /**
     * 给角色分配上权限
     *
     * @param id
     * @param authIds
     */
    void allotAuth(@Param("id") Long id, @Param("authIds") Long[] authIds);
}




