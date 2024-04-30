package cn.percent.usersystemjdk17.modules.user.service.impl;

import cn.percent.usersystemjdk17.common.utils.RandomUtils;
import cn.percent.usersystemjdk17.common.utils.RedisUtils;
import cn.percent.usersystemjdk17.modules.user.dto.ImgCodeDTO;
import cn.percent.usersystemjdk17.modules.user.service.ImgCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhangpengju
 * Date: 2021/12/8
 * Time: 9:53
 * Description:
 */
@Slf4j
@Service
public class ImgCodeServiceImpl implements ImgCodeService {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 设置背景颜色及大小，干扰线
     *
     * @param graphics
     * @param width
     * @param height
     */
    private static void fillBackground(Graphics graphics, int width, int height) {
        // 填充背景
        graphics.setColor(Color.WHITE);
        //设置矩形坐标x y 为0
        graphics.fillRect(0, 0, width, height);

        // 加入干扰线条
        for (int i = 0; i < 8; i++) {
            //设置随机颜色算法参数
            graphics.setColor(RandomUtils.randomColor(40, 150));
            Random random = new Random();
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            graphics.drawLine(x, y, x1, y1);
        }
    }

    @Override
    public String generate(Integer width, Integer height, OutputStream os) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        fillBackground(graphics, width, height);
        String randomStr = RandomUtils.randomString(IMG_CODE_LENGTH);
        createCharacter(graphics, randomStr);
        graphics.dispose();
        //设置JPEG格式
        ImageIO.write(image, "JPEG", os);
        return randomStr;
    }

    @Override
    public ImgCodeDTO generate(Integer width, Integer height, String loginAcct) {

        ImgCodeDTO imgCodeDTO = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            String code = generate(width, height, baos);
            imgCodeDTO = new ImgCodeDTO();
            imgCodeDTO.setCode(code);
            byte[] bytes = baos.toByteArray();
            imgCodeDTO.setImgBytes(bytes);
            // 验证码储存进redis
            redisUtils.set(loginAcct, code, 1L, TimeUnit.MINUTES);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            imgCodeDTO = null;
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgCodeDTO;
    }

    /**
     * 设置字符颜色大小
     *
     * @param g
     * @param randomStr
     */
    private void createCharacter(Graphics g, String randomStr) {
        char[] charArray = randomStr.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            //设置RGB颜色算法参数
            g.setColor(new Color(50 + RandomUtils.nextInt(100),
                    50 + RandomUtils.nextInt(100), 50 + RandomUtils.nextInt(100)));
            //设置字体大小，类型
            g.setFont(new Font(FONT_TYPES[RandomUtils.nextInt(FONT_TYPES.length)], Font.BOLD, 26));
            //设置x y 坐标
            g.drawString(String.valueOf(charArray[i]), 15 * i + 5, 19 + RandomUtils.nextInt(8));
        }
    }
}
