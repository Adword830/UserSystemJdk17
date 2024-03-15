package cn.percent.usersystemjdk17.modules.user.service;

/**
 * @author: zhangpengju
 * Date: 2021/12/9
 * Time: 11:00
 * Description:
 */
public interface QrCodeService {

    /**
     * 默认宽度
     */
    Integer WIDTH = 140;
    /**
     * 默认高度
     */
    Integer HEIGHT = 140;

    /**
     * LOGO 默认宽度
     */
    Integer LOGO_WIDTH = 22;
    /**
     * LOGO 默认高度
     */
    Integer LOGO_HEIGHT = 22;

    /**
     * 图片格式
     */
    String IMAGE_FORMAT = "png";
    String CHARSET = "utf-8";
    /**
     * 原生转码前面没有 data:image/png;base64 这些字段，返回给前端是无法被解析
     */
    String BASE64_IMAGE = "data:image/png;base64,%s";

    /**
     * 生成二维码，使用默认尺寸
     * @param content
     * @return
     */
    String getBase64QRCode(String content);


    /**
     * 生成二维码，使用默认尺寸二维码，插入默认尺寸logo
     *
     * @param content
     * @param logoUrl
     * @return
     */
    String getBase64QRCode(String content,String logoUrl);

    /**
     * 生成二维码
     *
     * @param content    内容
     * @param width      二维码宽度
     * @param height     二维码高度
     * @param logoUrl    logo 在线地址
     * @param logoWidth  logo 宽度
     * @param logoHeight logo 高度
     * @return
     */
    String getBase64QRCode(String content, Integer width, Integer height, String logoUrl, Integer logoWidth, Integer logoHeight);

}
