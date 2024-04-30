package cn.percent.usersystemjdk17.modules.user.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.percent.usersystemjdk17.modules.role.entity.RoleEntity;
import cn.percent.usersystemjdk17.modules.user.dto.AuthQuery;
import cn.percent.usersystemjdk17.modules.user.entity.AuthEntity;
import cn.percent.usersystemjdk17.modules.user.mapper.AuthEntityMapper;
import cn.percent.usersystemjdk17.modules.user.service.AuthEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zpj
 * @description 针对表【t_auth】的数据库操作Service实现
 * @createDate 2021-11-22 10:44:15
 */
@Service
public class AuthEntityServiceImpl extends ServiceImpl<AuthEntityMapper, AuthEntity>
        implements AuthEntityService {

    @Autowired
    private AuthEntityMapper authEntityMapper;

    @Override
    public List<AuthEntity> queryByRoleIdList(List<RoleEntity> roleEntityList) {
        return authEntityMapper.queryByRoleIdList(roleEntityList);
    }

    @Override
    public Page<AuthEntity> pageList(AuthQuery authQuery) {
        Page<AuthEntity> page = new Page<>(authQuery.getPageNum(), authQuery.getPageSize());
        QueryWrapper<AuthEntity> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(authQuery.getName())) {
            queryWrapper.eq("name", authQuery.getName());
        }
        Page<AuthEntity> roleEntityPage = page(page, queryWrapper);

        return roleEntityPage;
    }
}




