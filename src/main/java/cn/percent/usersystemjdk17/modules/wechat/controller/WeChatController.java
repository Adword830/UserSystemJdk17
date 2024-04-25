package cn.percent.usersystemjdk17.modules.wechat.controller;

import cn.percent.usersystemjdk17.common.utils.ApiResultUtils;
import cn.percent.usersystemjdk17.modules.wechat.entity.InMsgEntity;
import cn.percent.usersystemjdk17.modules.wechat.entity.OutMsgEntity;
import cn.percent.usersystemjdk17.modules.wechat.service.SendMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author pengju.zhang
 * @date 2022-09-01 15:34
 */
@RestController
@RequestMapping("/wechat")
@Api("微信公众号相关的接口")
public class WeChatController {

    private final SendMessage sendMessage;

    public WeChatController(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
    }

    /**
     * @param signature 微信加密签名，signature结合了开发者填写的 token 参数和请求中的 timestamp 参数、nonce参数。
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param echostr   随机字符串
     * @return
     */
    @GetMapping(value = "/checkSignature")
    @ApiOperation(value = "校验微信推送过来的相关信息", notes = "校验微信推送过来的相关信息", response = ApiResultUtils.class)
    public String checkSignature(String signature, String timestamp,
                                 String nonce, String echostr) {
        return sendMessage.checkSignature(signature, timestamp, nonce, echostr);
    }


    /**
     * 接收用户消息
     *
     * @param inMsgEntity
     * @return
     */
    @ApiOperation(value = "接收用户发送过来的信息", notes = "接收用户发送过来的信息", response = ApiResultUtils.class)
    @PostMapping(value = "/checkSignature", produces = "application/xml; charset=UTF-8")
    public OutMsgEntity InMessage(@RequestBody InMsgEntity inMsgEntity) {
        return sendMessage.inMessage(inMsgEntity);
    }


    /**
     * 上传视频或者图片
     *
     * @param multipartFile
     * @param description
     * @param type
     * @return
     */
    @ApiOperation(value = "上传视频或者图片", notes = "上传视频或者图片", response = ApiResultUtils.class)
    @PostMapping(value = "/uploadForever")
    public String uploadForever(@RequestParam("media") MultipartFile multipartFile,
                                @RequestParam(value = "description", required = false) String description,
                                @RequestParam("type") String type) {
        return sendMessage.uploadFile(multipartFile, description, type);
    }

    /**
     * @param count
     * @param offset
     * @param type
     * @return
     */
    @ApiOperation(value = "获取所有的素材列表", notes = "获取所有的素材列表", response = ApiResultUtils.class)
    @GetMapping(value = "/getMediaList")
    public String getMediaList(@RequestParam("count") String count,
                               @RequestParam(value = "offset") String offset,
                               @RequestParam("type") String type) {
        return sendMessage.getMediaList(count,offset,type);
    }

    /**
     * 手动发送相关信息
     *
     * @param flag
     * @return
     */
    @ApiOperation(value = "手动发送相关信息", notes = "手动发送相关信息", response = ApiResultUtils.class)
    @GetMapping(value = "/manualSend/{flag}")
    public String manualSend(@PathVariable("flag") Integer flag) {
        return sendMessage.send(flag);
    }


    /**
     * 给指定用户发送信息
     *
     * @param flag
     * @return
     */
    @ApiOperation(value = "给指定用户发送信息", notes = "给指定用户发送信息", response = ApiResultUtils.class)
    @GetMapping(value = "/manualSendOpenApi/{flag}")
    public String manualSend(@PathVariable("flag") Integer flag,@RequestParam("openApi")String openApi) {
        return sendMessage.send(flag,openApi);
    }
    /**
     * 创建菜单
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation(value = "创建菜单", notes = "创建菜单", response = ApiResultUtils.class)
    @PostMapping(value = "/createMenu")
    public String createMenu(@RequestBody String jsonObject) {
        return sendMessage.createMenu(jsonObject);
    }


}
