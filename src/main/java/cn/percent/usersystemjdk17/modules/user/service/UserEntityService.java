package cn.percent.usersystemjdk17.modules.user.service;


import cn.percent.usersystemjdk17.modules.role.entity.RoleEntity;
import cn.percent.usersystemjdk17.modules.user.dto.QrCodeDTO;
import cn.percent.usersystemjdk17.modules.user.dto.UpdateUserQuery;
import cn.percent.usersystemjdk17.modules.user.dto.UserDTO;
import cn.percent.usersystemjdk17.modules.user.dto.UserQuery;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author zpj
 * @description 针对表【t_admin】的数据库操作Service
 * @createDate 2021-11-22 10:44:15
 */
public interface UserEntityService extends IService<UserEntity> {

    /**
     * 分页查询
     *
     * @param userQuery
     * @return
     */
    Page<UserEntity> pageList(UserQuery userQuery);

    /**
     * 注册
     *
     * @param userQuery
     * @return
     */
    void registerUser(UserQuery userQuery);

    /**
     * 获取登录用户的信息
     *
     * @return
     */
    UserEntity info();

    /**
     * 发送验证码
     *
     * @param email
     * @param backUsePwd
     * @return
     */
    String sendMessage(String email, Boolean backUsePwd);

    /**
     * 分配角色
     *
     * @param userQuery
     */
    void allotRole(UserQuery userQuery);

    /**
     * 根据用户id修改密码
     *
     * @param updateUserQuery
     */
    void updateUsePwd(UpdateUserQuery updateUserQuery);

    /**
     * 修改用户基础信息
     *
     * @param userQuery
     */
    void updateUser(UserQuery userQuery);

    /**
     * 找回密码
     *
     * @param emailOrLoginAcct
     * @return
     */
    UserDTO backUsePwd(String emailOrLoginAcct);

    /**
     * 校验uuid
     *
     * @param uuid
     * @return
     */
    Boolean checkUUID(String uuid);

    /**
     * 生成登录二维码
     *
     * @return
     */
    QrCodeDTO buildQrCode();

    /**
     * 检查用户是否扫描了二维码
     *
     * @param uuid
     * @return
     */
    String qrCodeScan(String uuid);

    /**
     * 用户二维码确认登录
     *
     * @param uuid
     * @param request
     * @param response
     */
    void qrCodeLogin(String uuid, HttpServletRequest request, HttpServletResponse response);

    /**
     * 禁用和启用用户
     *
     * @param loginAcct
     * @param disable
     * @return
     */
    void disable(String loginAcct, Integer disable);

    /**
     * 根据用户id查询对应的角色信息
     *
     * @param userId
     * @return
     */
    List<RoleEntity> selectRoleByUserId(Long userId);
}
