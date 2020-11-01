package com.alone.counter.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.util
 * @Author: Alone
 * @CreateTime: 2020-10-29 19:31
 * @Description: 验证码生成工具类
 */
public class Captcha {

    //验证码
    private String code;

    //图片
    private BufferedImage bufferImg;

    //随机数发生器
    private Random random = new Random();

    public Captcha(int width, int height, int codeCount, int lineCount) {
        //1.生成图像
        bufferImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //2.背景色
        Graphics g = bufferImg.getGraphics();//得到画笔
        g.setColor(getRandColor(200, 250));//为画笔添加随机的颜色
        g.fillRect(0, 0, width, height);//填充背景
        Font font = new Font("Fixedsys", Font.BOLD, height - 5);//生成验证码的字体
        g.setFont(font);
        //3.生成干扰线,噪点
        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width);
            int ye = ys + random.nextInt(width);
            g.setColor(getRandColor(1, 255));
            g.drawLine(xs, ys, xe, ye);
        }
        float yawpRate = 0.01f;
        int area = (int)(yawpRate * width * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            bufferImg.setRGB(x, y, random.nextInt(255));
        }
        //4.添加字符
        this.code = randomStr(codeCount);
        //画数字
        int fontWidth = width / codeCount;
        int fontHeight = height - 5;
        for (int i = 0; i < codeCount; i++) {
            String str = this.code.substring(i, i + 1);
            g.setColor(getRandColor(1, 255));
            g.drawString(str, i * fontWidth + 3, fontHeight - 3);
        }
    }

    /**
     * 随机生成字符串
     * @param codeCount
     * @return
     */
    private String randomStr(int codeCount) {
        //字典
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        int len = str.length() - 1;
        double r;
        for (int i = 0; i < codeCount; i++) {
            r = (Math.random()) * len;
            sb.append(str.charAt((int)r));
        }
        return sb.toString();
    }

    /**
     * 随机生成颜色
     * @param fc
     * @param bc
     * @return
     */
    private Color getRandColor(int fc, int bc) {
        if(fc > 255) {
            fc = 255;
        }
        if(bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    public String getCode() {
        return code.toLowerCase();
    }

    /**
     * 获取验证码图片的base64编码
     * @return
     */
    public String getBase64ByteStr() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferImg, "png", baos);
        String s = Base64.getEncoder().encodeToString(baos.toByteArray());
        //去掉回车和换行
        s = s.replaceAll("\n", "").replaceAll("\r", "");
        return "data:image/jpg;base64," + s;
    }
}
