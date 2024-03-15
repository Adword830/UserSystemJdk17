package cn.percent.usersystemjdk17.modules.user.service;


import cn.percent.usersystemjdk17.modules.user.dto.ImgCodeDTO;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author: zhangpengju
 * Date: 2021/12/8
 * Time: 9:53
 * Description:
 */
public interface ImgCodeService {


    String[] FONT_TYPES = { "\u5b8b\u4f53", "\u65b0\u5b8b\u4f53", "\u9ed1\u4f53", "\u6977\u4f53", "\u96b6\u4e66" };

    Integer IMG_CODE_LENGTH = 4;

    /**
     * 生成验证码并返回code，将图片写的os中
     *
     * @param width
     * @param height
     * @param os
     * @return
     * @throws IOException
     */
    String generate(Integer width, Integer height, OutputStream os) throws IOException;

    /**
     * 生成验证码对象
     *
     * @param width
     * @param height
     * @param uuid
     * @return
     * @throws IOException
     */
    ImgCodeDTO generate(Integer width, Integer height, String uuid);
}
