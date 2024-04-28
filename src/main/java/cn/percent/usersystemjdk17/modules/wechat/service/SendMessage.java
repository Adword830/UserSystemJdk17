package cn.percent.usersystemjdk17.modules.wechat.service;

import cn.percent.usersystemjdk17.modules.wechat.entity.InMsgEntity;
import cn.percent.usersystemjdk17.modules.wechat.entity.OutMsgEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * SendMessage接口定义了一系列与发送消息相关的方法。
 * 这些方法包括定时通知用户信息、处理用户发送的消息、校验微信公众号信息、创建菜单、上传文件和获取媒体列表等。
 *
 * @author pengju.zhang
 * @date 2022-08-30 16:32
 */
public interface SendMessage {

    /**
     * 定时通知用户相关的信息。
     *
     * @param flag 标识符
     * @return 返回通知结果
     */
    String send(int flag);

    /**
     * 定时通知用户相关的信息。
     *
     * @param flag 标识符
     * @param openApi 开放API
     * @return 返回通知结果
     */
    String send(int flag,String openApi);

    /**
     * 处理用户发送过来的消息，并进行相应的回复。
     *
     * @param inMsgEntity 用户发送的消息
     * @return 返回回复的消息
     */
    OutMsgEntity inMessage(InMsgEntity inMsgEntity);

    /**
     * 校验微信公众号相关信息。
     *
     * @param signature 签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @return 返回校验结果
     */
    String checkSignature(String signature, String timestamp, String nonce, String echostr);

    /**
     * 创建菜单。
     *
     * @param jsonStr 菜单的JSON字符串
     * @return 返回创建结果
     */
    String createMenu(String jsonStr);

    /**
     * 上传文件。
     *
     * @param multipartFile 要上传的文件
     * @param description 文件描述
     * @param type 文件类型
     * @return 返回上传结果
     */
    String uploadFile(MultipartFile multipartFile, String description,String type);

    /**
     * 获取媒体列表。
     *
     * @param count 获取的数量
     * @param offset 偏移量
     * @param type 媒体类型
     * @return 返回媒体列表
     */
    String getMediaList(String count, String offset, String type);
}