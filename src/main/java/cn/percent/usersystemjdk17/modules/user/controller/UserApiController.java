package cn.percent.usersystemjdk17.modules.user.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.percent.usersystemjdk17.common.enums.QrCodeStatusEnum;
import cn.percent.usersystemjdk17.common.server.WebSocketServer;
import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import cn.percent.usersystemjdk17.common.utils.RedisUtils;
import cn.percent.usersystemjdk17.common.utils.UserUtils;
import cn.percent.usersystemjdk17.modules.role.entity.RoleEntity;
import cn.percent.usersystemjdk17.modules.user.dto.*;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.service.ImgCodeService;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: zhangpengju
 * Date: 2021/11/25
 * Time: 17:24
 * Description:
 */
@RestController
@RequestMapping("/system/user")
@Api("用户管理模块")
public class UserApiController {

    private final UserEntityService userEntityService;

    private final ImgCodeService imgCodeService;

    private final RedisUtils redisUtils;

    private final WebSocketServer webSocketServer;

    public UserApiController(UserEntityService userEntityService, ImgCodeService imgCodeService, RedisUtils redisUtils, WebSocketServer webSocketServer) {
        this.userEntityService = userEntityService;
        this.imgCodeService = imgCodeService;
        this.redisUtils = redisUtils;
        this.webSocketServer = webSocketServer;
    }

    /**
     * 当前方法可以查询所有的用户信息超级管理员可以查看
     *
     * @param userQuery
     * @return
     */
    @ApiOperation(value = "获取当前系统的所有用户", notes = "获取当前系统的所有用户", response = ApiResultUtils.class)
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @GetMapping("/infoList")
    public ApiResultUtils<Page<UserEntity>> listUserInfo(UserQuery userQuery) {
        Page<UserEntity> userEntityPage = userEntityService.pageList(userQuery);
        return ApiResultUtils.ok(userEntityPage);
    }

    /**
     * 获取登录用户的信息
     *
     * @param
     * @return
     */
    @ApiOperation(value = "获取当前用户详情", notes = "获取当前用户详情", response = ApiResultUtils.class)
    @GetMapping("/info")
    public ApiResultUtils<UserDTO> getUserInfo() {
        UserEntity userEntity = userEntityService.info();
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userEntity, userDTO);
        return ApiResultUtils.ok(userDTO);
    }


    /**
     * 注册
     *
     * @param userQuery
     * @return
     */
    @ApiOperation(value = "注册", notes = "注册", response = ApiResultUtils.class)
    @PostMapping("/register")
    public ApiResultUtils<String> registerUser(@RequestBody @Valid UserQuery userQuery) {
        userEntityService.registerUser(userQuery);
        return ApiResultUtils.ok("用户 " + userQuery.getLoginAcct() + " 注册成功");
    }

    /**
     * 当前方法只能是本人执行,且本人只能修改到自己的信息
     *
     * @param userQuery
     * @return
     */
    @ApiOperation(value = "用户基础信息修改修改昵称", notes = "用户基础信息修改", response = ApiResultUtils.class)
    @PreAuthorize("@securityServiceImpl.checkUser(authentication,#userQuery.id)")
    @PutMapping("/update")
    public ApiResultUtils updateUser(@RequestBody UserQuery userQuery) {
        userEntityService.updateUser(userQuery);
        return ApiResultUtils.ok("密码修改成功");
    }

    /**
     * 传入对应的邮箱进行验真码发送
     *
     * @param email
     * @return
     */
    @ApiOperation(value = "注册发送验证码,和找回密码时候发送的验证码", notes = "发送验证码", response = ApiResultUtils.class)
    @GetMapping("/sendMessage")
    public ApiResultUtils<String> sendMessage(@RequestParam(required = false, name = "email") String email,
                                              @RequestParam(required = false, defaultValue = "false", name = "backUsePwd") Boolean backUsePwd,
                                              @RequestParam(required = false, name = "id") Long id) {
        // 如果是找回密码发送验证码
        if (Boolean.TRUE.equals(backUsePwd)) {
            UserEntity userEntity = userEntityService.getById(id);
            userEntityService.sendMessage(userEntity.getEmail(), backUsePwd);
            return ApiResultUtils.ok();
        }
        userEntityService.sendMessage(email, backUsePwd);
        return ApiResultUtils.ok();
    }

    /**
     * 修改密码
     *
     * @param updateUserQuery
     * @return
     */
    @ApiOperation(value = "修改密码", notes = "修改密码", response = ApiResultUtils.class)
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/updateUsePwd")
    public ApiResultUtils updateUsePwd(@RequestBody UpdateUserQuery updateUserQuery) {
        // 修改密码  进入修改密码页——>输入原密码判断是否正确——>输入新密码——>密码修改成功——>返回登录页
        userEntityService.updateUsePwd(updateUserQuery);
        return ApiResultUtils.ok();
    }

    /**
     * 找回密码
     *
     * @param emailOrLoginAcct
     * @return
     */
    @ApiOperation(value = "找回密码", notes = "找回密码", response = ApiResultUtils.class)
    @PostMapping("/backUsePwd")
    public ApiResultUtils backUsePwd(String emailOrLoginAcct) {
        // 找回密码  先输入登录名——>填写邮箱或者登录名称——>发送验证码——>进入修改密码——>返回登录页
        UserDTO userDTO = userEntityService.backUsePwd(emailOrLoginAcct);
        return ApiResultUtils.ok(userDTO);
    }

    /**
     * 生成图片验证码
     *
     * @return
     */
    @ApiOperation(value = "生成图片验证码", notes = "生成图片验证码", response = ApiResultUtils.class)
    @GetMapping("/imgCode")
    public ApiResultUtils<ImgCodeDTO> imgCode(String loginAcct) {
        //设置长宽
        ImgCodeDTO imgCode = imgCodeService.generate(80, 28, loginAcct);
        return ApiResultUtils.ok(imgCode);
    }

    /**
     * 生成登录二维码
     *
     * @return
     */
    @ApiOperation(value = "生成登录二维码", notes = "生成登录二维码", response = ApiResultUtils.class)
    @GetMapping("/qrCode")
    public ApiResultUtils<QrCodeDTO> qrCode() {
        QrCodeDTO qrCodeDTO = userEntityService.buildQrCode();
        return ApiResultUtils.ok(qrCodeDTO);
    }

    /**
     * 验证码长链接3分钟内不断的交互检查
     *
     * @param uuid
     * @return
     */
    @ApiOperation(value = "验证码长链接检查用户是否扫描二维码，直至二维码过期或者用户确认登录", notes = "验证码长链接3分钟内不断的交互检查", response = ApiResultUtils.class)
    @GetMapping("/qrCodeScan")
    public ApiResultUtils<String> qrCodeScan(@RequestParam("uuid") String uuid) {
        String message = userEntityService.qrCodeScan(uuid);
        return ApiResultUtils.ok(message);
    }

    /**
     * 用户扫描成功二维码确认登录
     *
     * @param uuid
     * @return
     */
    @ApiOperation(value = "二维码确认登录", notes = "二维码确认登录", response = ApiResultUtils.class)
    @GetMapping("/qrCodeLogin")
    public void qrCodeLogin(@RequestParam("uuid") String uuid,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        userEntityService.qrCodeLogin(uuid, request, response);
    }

    /**
     * 用户成功扫描二维码调用的链接
     *
     * @param uuid
     * @return
     */
    @ApiOperation(value = "用户成功扫描二维码", notes = "用户成功扫描二维码", response = ApiResultUtils.class)
    @GetMapping("/qrCodeScanSuc")
    public ApiResultUtils<String> qrCodeScanSuc(@RequestParam("uuid") String uuid) {
        String status = (String) redisUtils.get(uuid);
        if (CharSequenceUtil.isNotEmpty(status)) {
            webSocketServer.sendMessageTo("二维码扫描成功", uuid);
            redisUtils.set(uuid, QrCodeStatusEnum.EXPIRED.getValue().toString());
            return ApiResultUtils.ok("二维码扫描成功");
        }
        return ApiResultUtils.fail("二维码失效");
    }

    /**
     * 给指定的用户分配角色
     *
     * @return
     */
    @ApiOperation(value = "给用户分配角色", notes = "分配角色", response = ApiResultUtils.class)
    @PostMapping("/allotRole")
    public ApiResultUtils allotRole(@RequestBody UserQuery userQuery) {
        userEntityService.allotRole(userQuery);
        UserEntity userEntity = UserUtils.threadLocal.get();
        return ApiResultUtils.ok("用户 " + userEntity.getLoginAcct() + " 角色分配成功");
    }

    /**
     * 禁用和启用用户
     *
     * @param userQuery
     * @return
     */
    @ApiOperation(value = "禁用和启用用户", notes = "禁用和启用用户", response = ApiResultUtils.class)
    @PostMapping("/disable")
    public ApiResultUtils disable(@RequestBody UserQuery userQuery) {
        userEntityService.disable(userQuery.getLoginAcct(), userQuery.getDisable());
        return ApiResultUtils.ok();
    }


    /**
     * 根据用户id查询对应的角色信息
     *
     * @return
     */
    @ApiOperation(value = "根据用户id查询对应的角色信息", notes = "根据用户id查询对应的角色信息", response = ApiResultUtils.class)
    @GetMapping("/listRoleInfo")
    public ApiResultUtils<List<RoleEntity>> listRoleInfo(UserQuery userQuery) {
        List<RoleEntity> roleEntityList = userEntityService.selectRoleByUserId(userQuery.getId());
        return ApiResultUtils.ok(roleEntityList);
    }
}