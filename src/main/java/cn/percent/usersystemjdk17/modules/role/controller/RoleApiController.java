package cn.percent.usersystemjdk17.modules.role.controller;


import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import cn.percent.usersystemjdk17.modules.role.entity.RoleEntity;
import cn.percent.usersystemjdk17.modules.role.service.RoleEntityService;
import cn.percent.usersystemjdk17.modules.user.dto.RoleDTO;
import cn.percent.usersystemjdk17.modules.user.dto.RoleQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: zhangpengju
 * Date: 2021/11/29
 * Time: 14:41
 * Description:
 */
@RequestMapping("/role/api")
@RestController
@Api("角色相关的api")
public class RoleApiController {

    @Autowired
    private RoleEntityService roleEntityService;

    /**
     * 根据对应的权限获取其管理的角色
     *
     * @param roleQuery
     * @return
     */
    @ApiOperation(value = "获取当前登录用户管理的角色", notes = "获取当前的角色", response = ApiResultUtils.class)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public ApiResultUtils<Page<RoleEntity>> listRoleInfo(RoleQuery roleQuery) {
        Page<RoleEntity> roleEntityPage = roleEntityService.pageList(roleQuery);
        return ApiResultUtils.ok(roleEntityPage);
    }

    /**
     * 根据对应的权限获取其管理的角色
     *
     * @return
     */
    @ApiOperation(value = "获取对应的角色树", notes = "获取对应的角色树", response = ApiResultUtils.class)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SERVICE_ADMIN','ROLE_SUPER_ADMIN')")
    @GetMapping("/roleTree")
    public ApiResultUtils<List<RoleDTO>> roleTree() {
        List<RoleDTO> roleTree = roleEntityService.roleTree();
        return ApiResultUtils.ok(roleTree);
    }

    /**
     * 新增角色
     *
     * @param roleQuery
     * @return
     */
    @ApiOperation(value = "新增角色", notes = "新增角色", response = ApiResultUtils.class)
    @PostMapping("/save")
    public ApiResultUtils saveRole(@RequestBody RoleQuery roleQuery) {
        RoleEntity roleEntity = new RoleEntity();
        BeanUtils.copyProperties(roleQuery, roleEntity);
        roleEntityService.save(roleEntity);
        return ApiResultUtils.ok(roleEntity);
    }

    @ApiOperation(value = "修改角色", notes = "修改角色", response = ApiResultUtils.class)
    @PutMapping("/update")
    public ApiResultUtils updateRole(@RequestBody RoleQuery roleQuery) {
        roleEntityService.updateRole(roleQuery);
        return ApiResultUtils.ok(roleQuery);
    }

    /**
     * 给指定的角色分配权限
     *
     * @return
     */
    @ApiOperation(value = "給指定的角色分配权限", notes = "給指定的角色分配权限", response = ApiResultUtils.class)
    @PostMapping("/allotAuth")
    public ApiResultUtils allotAuth(@RequestBody RoleQuery roleQuery) {
        roleEntityService.allotAuth(roleQuery);
        return ApiResultUtils.ok();
    }


}
