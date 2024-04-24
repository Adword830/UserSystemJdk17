package cn.percent.usersystemjdk17.modules.role.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.percent.usersystemjdk17.modules.role.entity.RoleEntity;
import cn.percent.usersystemjdk17.modules.role.mapper.RoleEntityMapper;
import cn.percent.usersystemjdk17.modules.role.service.RoleEntityService;
import cn.percent.usersystemjdk17.modules.user.dto.RoleDTO;
import cn.percent.usersystemjdk17.modules.user.dto.RoleQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author zpj
* @description 针对表【t_role】的数据库操作Service实现
* @createDate 2021-11-22 10:44:15
*/
@Service
public class RoleEntityServiceImpl extends ServiceImpl<RoleEntityMapper, RoleEntity>
    implements RoleEntityService {

    @Autowired
    private RoleEntityMapper roleEntityMapper;

    @Override
    public List<RoleEntity> queryByUserIdList(Long userId) {
        return roleEntityMapper.queryByUserIdList(userId);
    }

    @Override
    public Page<RoleEntity> pageList(RoleQuery roleQuery) {
        Page<RoleEntity> page = new Page<>(roleQuery.getPageNum(), roleQuery.getPageSize());
        QueryWrapper<RoleEntity> queryWrapper=new QueryWrapper<>();
        if (StrUtil.isNotEmpty(roleQuery.getName())) {
            queryWrapper.eq("name",roleQuery.getName());
        }
        List<RoleEntity> roleList = roleEntityMapper.queryByUserIdList(Long.parseLong(roleQuery.getUserId()));
        List<Long> idList = roleList.stream()
                .filter(role -> role.getParentId()==null)
                .map(RoleEntity::getId).collect(Collectors.toList());
        // 查询出对应的角色下管理的角色
        if (idList.size() > 0) {
            queryWrapper.in("parent_id",idList);
        }
        Page<RoleEntity> roleEntityPage = page(page,queryWrapper);
        return roleEntityPage;
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void allotAuth(RoleQuery roleQuery) {
        if (roleQuery.getAuthIds()==null || roleQuery.getAuthIds().length==0) {
            return;
        }
        this.baseMapper.allotAuth(roleQuery.getId(),roleQuery.getAuthIds());
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void updateRole(RoleQuery roleQuery) {
        RoleEntity role=new RoleEntity();
        BeanUtils.copyProperties(roleQuery, role);
        // 修改基础信息
        updateById(role);
        // 修改当前角色的权限信息
        this.baseMapper.allotAuth(roleQuery.getId(),roleQuery.getAuthIds());
    }

    @Override
    public List<RoleDTO> roleTree() {
        List<RoleEntity> entityList = this.baseMapper.selectList(new QueryWrapper<>());
        List<RoleDTO> roleDTOList=new ArrayList<>(10);
        entityList.forEach(item->{
            RoleDTO roleDTO=new RoleDTO();
            roleDTO.setId(item.getId());
            roleDTO.setValue(item.getName());
            roleDTO.setLabel(item.getNameDesc());
            roleDTO.setParentId(item.getParentId());
            roleDTOList.add(roleDTO);
        });
        List<RoleDTO> treeList = selectTree(0L, roleDTOList);
        return treeList;

    }

    private List<RoleDTO> selectTree(Long parentId, List<RoleDTO> roleDTOList) {
        return roleDTOList.stream().filter(menu -> menu.getParentId().equals(parentId))
                .peek(menu -> menu.setChildren(selectTree(menu.getId(), roleDTOList)))
                .collect(Collectors.toList());
    }
}




