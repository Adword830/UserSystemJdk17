package cn.percent.usersystemjdk17.modules.user.service.impl;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.CharSequenceUtil;
import cn.percent.usersystemjdk17.common.constant.Constant;
import cn.percent.usersystemjdk17.common.enums.QrCodeStatusEnum;
import cn.percent.usersystemjdk17.common.exception.BaseException;
import cn.percent.usersystemjdk17.common.server.WebSocketServer;
import cn.percent.usersystemjdk17.common.utils.*;
import cn.percent.usersystemjdk17.modules.role.entity.RoleEntity;
import cn.percent.usersystemjdk17.modules.role.service.RoleEntityService;
import cn.percent.usersystemjdk17.modules.user.dto.QrCodeDTO;
import cn.percent.usersystemjdk17.modules.user.dto.UpdateUserQuery;
import cn.percent.usersystemjdk17.modules.user.dto.UserDTO;
import cn.percent.usersystemjdk17.modules.user.dto.UserQuery;
import cn.percent.usersystemjdk17.modules.user.entity.UserEntity;
import cn.percent.usersystemjdk17.modules.user.mapper.UserEntityMapper;
import cn.percent.usersystemjdk17.modules.user.service.QrCodeService;
import cn.percent.usersystemjdk17.modules.user.service.UserEntityService;
import cn.percent.usersystemjdk17.modules.userroledept.entity.UserRoleDeptEntity;
import cn.percent.usersystemjdk17.modules.userroledept.service.UserRoleDeptEntityService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zpj
 * @description 针对表【t_admin】的数据库操作Service实现
 * @createDate 2021-11-22 10:44:15
 */
@Slf4j
@Service
public class UserEntityServiceImpl extends ServiceImpl<UserEntityMapper, UserEntity>
        implements UserEntityService {

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.subject}")
    private String subject;

    @Value("${ras.private.key}")
    private String privateKey;

    @Value("${login.url}")
    private String url;

    @Resource
    private JavaMailSender mailSender;

    private final UserRoleDeptEntityService userRoleDeptEntityService;

    private final QrCodeService qrCodeService;

    private final RedisUtils redisUtils;

    private final WebSocketServer webSocketServer;

    @Resource
    private JavaMailSender javaMailSender;

    private final RoleEntityService roleEntityService;

    public UserEntityServiceImpl(UserRoleDeptEntityService userRoleDeptEntityService, QrCodeService qrCodeService, RedisUtils redisUtils, WebSocketServer webSocketServer, RoleEntityService roleEntityService) {
        this.userRoleDeptEntityService = userRoleDeptEntityService;
        this.qrCodeService = qrCodeService;
        this.redisUtils = redisUtils;
        this.webSocketServer = webSocketServer;
        this.roleEntityService = roleEntityService;
    }


    @Override
    public Page<UserEntity> pageList(UserQuery userQuery) {
        // 构建一个page类这个Page类是Myatis-plus包下的第一个参数代表当前页码，第二个参数表示当前页显示多少条数据
        Page<UserEntity> page = new Page<>(userQuery.getPageNum(), userQuery.getPageSize());
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(CharSequenceUtil.isNotEmpty(userQuery.getLoginAcct()), "login_acct", userQuery.getLoginAcct());
        if (userQuery.getFirstLoginTime() != null && userQuery.getFirstLoginTime().size() == 2) {
            queryWrapper.between("first_login_time", userQuery.getFirstLoginTime().get(0), userQuery.getFirstLoginTime().get(1));
        }
        // 调用分页查询方法进行分页查询，第一个参数代表page对象，第二个参数代表查询条件
        return page(page, queryWrapper.select("id", "user_name", "email", "login_acct", "first_login_time", "last_login_time", "disable"));
    }


    @Transactional(rollbackFor = {})
    @Override
    public void registerUser(UserQuery userQuery) {
        // 判断用户是否已经注册
        UserEntity userEntity = query().select("id").eq("login_acct", userQuery.getLoginAcct()).one();
        if (userEntity != null) {
            throw new BaseException("当前用户已经注册");
        }
        // 判断邮箱是否符合规则
        UserUtils.checkEmail(userQuery.getEmail());
        // 如果昵称为空则默认为登录名
        String userName = userQuery.getUserName();
        if (CharSequenceUtil.isEmpty(userName)) {
            userQuery.setUserName(userQuery.getLoginAcct());
        }
        // 判断验证码是否相同
        String code = (String) redisUtils.get(userQuery.getEmail() + "#");
        if (!Objects.equals(code, userQuery.getCode())) {
            throw new BaseException("验证码错误");
        }
        UserEntity user = new UserEntity();
        // 前端传过来的密码都是经过Rsa加密,需要对密码进行解密操作
        String password = Rsa1024Utils.decrypt(userQuery.getUserPswd(), privateKey);
        // 储存进数据库对密码进行加密，使用springSecurity加盐的方式进行加密
        // 创建 BCryptPasswordEncoder 对象
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String pwd = passwordEncoder.encode(password);
        userQuery.setUserPswd(pwd);
        BeanUtils.copyProperties(userQuery, user);
        user.setLoginFailNum(0);
        this.save(user);
    }

    @Override
    public UserEntity info() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String object = (String) authentication.getPrincipal();
        UserEntity userEntity = new UserEntity();
        userEntity.setLoginAcct(object);
        userEntity.setUserName(object);

        return userEntity;
    }

    @Override
    public String sendMessage(String email, Boolean backUsePwd) {
        UserEntity userEntity = query().select("id").eq("email", email).one();
        if (userEntity != null) {
            throw new BaseException(Constant.EMAIL_IS_EXIST);
        }
        String code = (String) redisUtils.get(email + "#");
        if (CharSequenceUtil.isNotEmpty(code)) {
            throw new BaseException(Constant.REPEAT_GET_CODE);
        }
        // 校验邮箱
        UserUtils.checkEmail(email);
        // 生成对应区间的随机数
        BigDecimal db = BigDecimal.valueOf(Math.random() * (100000 - 1000) + 1000);
        // B保留整数
        db.setScale(0, RoundingMode.UP);

        long longValue = db.longValue();
        try {
            // 创建简单邮件消息
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //谁发的
            helper.setFrom(from);
            //谁要接收
            helper.setTo(email);
            //邮件标题
            helper.setSubject(subject);
            //邮件内容
            helper.setText(buildContent(String.valueOf(longValue) + ""), true);
            mailSender.send(message);
            log.info("邮件发送成功");
        } catch (MessagingException e) {
            log.error("邮件发送失败, to: {}, title: {}", email, subject, e);
        }
        if (Boolean.TRUE.equals(backUsePwd)) {
            // 找回密码用验证码存储到redis中 key:邮箱&，value：code
            redisUtils.set(email + "&", String.valueOf(longValue), 3L, TimeUnit.MINUTES);
            update(new UpdateWrapper<UserEntity>().eq("email", email).setSql("code_num = code_num+1"));
        } else {
            // 注册用验证码存储到redis中 key:邮箱#，value：code
            redisUtils.set(email + "#", String.valueOf(longValue), 3L, TimeUnit.MINUTES);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void allotRole(UserQuery userQuery) {
        if (userQuery.getRoleIds() == null || userQuery.getRoleIds().isEmpty()) {
            return;
        }
        // 分配角色前查看是否存在角色
        List<Long> roleIdList = userRoleDeptEntityService.lambdaQuery()
                .select(UserRoleDeptEntity::getRoleId)
                .eq(UserRoleDeptEntity::getUserId, userQuery.getId())
                .eq(UserRoleDeptEntity::getDel, false).list().stream().map(UserRoleDeptEntity::getRoleId).collect(Collectors.toList());

        List<Long> resList = new ArrayList<>();
        roleIdList.forEach(item -> {
            if (userQuery.getRoleIds().contains(item)) {
                resList.add(item);
            }
        });
        userQuery.getRoleIds().removeAll(resList);
        roleIdList.removeAll(resList);
        UpdateWrapper<UserRoleDeptEntity> updateWrapper = new UpdateWrapper<>();
        // 删除旧的role角色信息
        if (!roleIdList.isEmpty()) {
            updateWrapper.lambda().set(UserRoleDeptEntity::getDel, true).eq(UserRoleDeptEntity::getUserId, userQuery.getId())
                    .in(UserRoleDeptEntity::getRoleId, roleIdList);
            userRoleDeptEntityService.update(updateWrapper);
        }
        // 保存新的roleId信息
        List<UserRoleDeptEntity> userRoleDeptList = new ArrayList<>();
        userQuery.getRoleIds().forEach(item -> {
            UserRoleDeptEntity userRoleDept = new UserRoleDeptEntity();
            userRoleDept.setUserId(userQuery.getId());
            userRoleDept.setRoleId(item);
            userRoleDeptList.add(userRoleDept);
        });
        userRoleDeptEntityService.saveBatch(userRoleDeptList);
    }

    @Override
    public void updateUsePwd(UpdateUserQuery updateUserQuery) {
        UserEntity userEntity = getById(updateUserQuery.getId());
        // 找回密码时需要验证邮箱验证码
        if (Boolean.TRUE.equals(updateUserQuery.getFlag())) {
            String code = (String) redisUtils.get(updateUserQuery.getEmail() + "&");
            if (!Objects.equals(updateUserQuery.getCode(), code)) {
                throw new BaseException("验证码错误");
            }
        }
        if (updateUserQuery.getPwd().equals(userEntity.getUserPswd())) {
            throw new BaseException("原密码输入错误");
        }
        if (updateUserQuery.getNewPwd().equals(userEntity.getUserPswd())) {
            throw new BaseException("原密码和新密码一致");
        }
        UpdateWrapper<UserEntity> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("id", updateUserQuery.getId()).set("userPswd", updateUserQuery.getNewPwd());
        // 进行密码修改
        update(queryWrapper);
    }

    @Override
    public void updateUser(UserQuery userQuery) {
        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(userQuery, user);
        if (CharSequenceUtil.isEmpty(user.getUserName())) {
            return;
        }
        UpdateWrapper<UserEntity> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("id", user.getId()).set("username", user.getUserName());
        update(queryWrapper);
    }

    @Override
    public UserDTO backUsePwd(String emailOrLoginAcct) {
        UserEntity userEntity;
        // 是邮箱则直接用邮箱查询
        if (Validator.isEmail(emailOrLoginAcct)) {
            // 需要找回密码的用户
            userEntity = query().eq("email", emailOrLoginAcct).one();
        } else {
            // 需要找回密码的用户
            userEntity = query().eq("loginAcct", emailOrLoginAcct).one();
        }
        String email = userEntity.getEmail();
        String loginAcct = userEntity.getLoginAcct();
        // 隐藏部分邮箱
        UserUtils.hiddenEmail(email);
        // 隐藏部分用户名
        UserUtils.hiddenLoginAcct(loginAcct);
        return new UserDTO(userEntity.getId(), loginAcct, email);
    }

    @Override
    public Boolean checkUUID(String uuid) {
        String imgCodeUUID = (String) redisUtils.get("qrCodeUUID");
        return Objects.equals(imgCodeUUID, uuid);
    }

    @Override
    public QrCodeDTO buildQrCode() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String content = url + "?uuid=" + uuid;
        log.info("qrCodeUUID {} | url {}", uuid, content);
        //设置长宽
        String qrCode = qrCodeService.getBase64QRCode(content);
        QrCodeDTO qrCodeDTO = new QrCodeDTO(qrCode, uuid, QrCodeStatusEnum.NOT_SCAN);
        // 初始化二维码设置过期时间三分钟
        redisUtils.set(uuid, QrCodeStatusEnum.NOT_SCAN.getValue().toString(), 3L, TimeUnit.MINUTES);
        return qrCodeDTO;
    }

    @Override
    public String qrCodeScan(String uuid) {
        if (redisUtils.get(uuid) == null) {
            throw new BaseException("二维码过期");
        }
        log.info("成功扫描二维码信息");
        // 扫描成功之后刷新二维码三分钟过期
        redisUtils.set(uuid, QrCodeStatusEnum.SCANNED.getValue().toString(), 3L, TimeUnit.MINUTES);
        try {
            webSocketServer.sendMessageAll(JSON.toJSONString(ApiResultUtils.ok(ApiCodeUtils.QRCODE_SCAN_SUCCESS)), uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uuid;

    }

    @Override
    public void qrCodeLogin(String uuid, HttpServletRequest request, HttpServletResponse response) {
        // 修改二维码状态为确认完成
        redisUtils.set(uuid, QrCodeStatusEnum.VERIFIED.getValue().toString());
        webSocketServer.sendMessageTo("用户确认登录，二维码长链接结束，执行token登录步骤", uuid);
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith(ConstantUtil.BEARER)) {
            return;
        }
        String token = bearerToken.replace(ConstantUtil.BEARER, "");
        Claims claims = JwtUtils.getClaims(token);
        // 解析出来的token信息与数据库的用户信息匹配上就可以登录成功
        assert claims != null;
        String loginAcct = (String) claims.get("loginAcct");
        Long num = this.query().eq("login_acct", loginAcct).count();
        if (num == null) {
            throw new BaseException("用户信息不符合,登录失败");
        }
        // 修改二维码状态为登录成功的状态并在1分钟后进行删除
        redisUtils.set(uuid, QrCodeStatusEnum.FINISH.getValue().toString(), 1L, TimeUnit.MINUTES);
        webSocketServer.sendMessageTo("登录成功", uuid);

    }

    @Override
    public void disable(String loginAcct, Integer disable) {
        UpdateWrapper<UserEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("disable", disable).eq("login_Acct", loginAcct);
        this.update(updateWrapper);
    }

    @Override
    public List<RoleEntity> selectRoleByUserId(Long userId) {
        return this.baseMapper.selectRoleByUserId(userId);
    }

    /**
     * 读取邮件模板
     * 替换模板中的信息
     *
     * @param title 内容
     * @return
     */
    public static String buildContent(String title) {
        //加载邮件html模板
        ClassPathResource resource = new ClassPathResource("/template/mailtemplate.ftl");
        InputStream inputStream = null;
        BufferedReader fileReader = null;
        StringBuilder buffer = new StringBuilder();
        String line = "";
        try {
            inputStream = resource.getStream();
            fileReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = fileReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            log.info("发送邮件读取模板失败{}", e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //替换html模板中的参数
        return MessageFormat.format(buffer.toString(), title);
    }
}




