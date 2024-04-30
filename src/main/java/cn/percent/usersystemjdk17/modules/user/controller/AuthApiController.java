package cn.percent.usersystemjdk17.modules.user.controller;

import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import cn.percent.usersystemjdk17.modules.user.dto.AuthQuery;
import cn.percent.usersystemjdk17.modules.user.entity.AuthEntity;
import cn.percent.usersystemjdk17.modules.user.service.AuthEntityService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zhangpengju
 * Date: 2021/11/29
 * Time: 14:42
 * Description:
 */
@RequestMapping("/auth/api/")
@RestController
@Api("权限相关的api")
public class AuthApiController {

    @Autowired
    private AuthEntityService authEntityService;

    /**
     * 根据对应的权限获取其管理的角色
     *
     * @param authQuery
     * @return
     */
    @ApiOperation(value = "获取当前系统的权限", notes = "获取当前系统的权限", response = ApiResultUtils.class)
    @GetMapping("/info")
    public ApiResultUtils<Page<AuthEntity>> listAuthInfo(AuthQuery authQuery) {
        Page<AuthEntity> authEntityPage = authEntityService.pageList(authQuery);
        return ApiResultUtils.ok(authEntityPage);
    }


}
