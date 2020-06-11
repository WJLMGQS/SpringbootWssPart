package org.wjlmgqs.swp.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtils {

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     *
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    public static int length(String value) {
        if (value == null) {
            return 0;
        }
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 对象转JSON字符串
     */
    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
    }


    /**
     * 检测字符串的长度，如果有中文，则每个中文字符计为2位
     *
     * @param value 指定的字符串
     * @param max   最大字符长度
     * @return 是否满足
     */
    public static boolean length(String value, int max) {
        return length(value) <= max;
    }


    /**
     * 将对象的大写转换为下划线加小写，例如：userName-->user_name
     */
    public static String camel2Line(String str) {
        if (org.springframework.util.StringUtils.isEmpty(str)) {
            return "";
        }
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 二进制byte[]转十六进制string
     */
    public static String byte2Hex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String strHex = Integer.toHexString(bytes[i]);
            if (strHex.length() > 3) {
                sb.append(strHex.substring(6));
            } else {
                if (strHex.length() < 2) {
                    sb.append("0" + strHex);
                } else {
                    sb.append(strHex);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 十六进制string转二进制byte[]
     */
    public static byte[] hex2Byte(String s)  {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                System.out.println("十六进制转byte发生错误！！！");
                throw (e);
            }
        }
        return baKeyword;
    }




}
