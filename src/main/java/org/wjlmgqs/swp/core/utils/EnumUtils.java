package org.wjlmgqs.swp.core.utils;

import org.wjlmgqs.swp.core.enums.EnumInterface;
import org.wjlmgqs.swp.core.exps.SwpCustomizedException;

import java.util.ArrayList;
import java.util.List;

/**
 * 枚举工具集合（必须实现EnumInterface接口）
 *
 * @author wjlmgqs@sina.com
 * @date 2018/9/12
 */
public class EnumUtils {

    /**
     * 判断val是否是指定枚举的value值
     *
     * @param tClass 枚举
     * @param code   值
     * @return 是否是枚举值
     */
    public static boolean isEnumCode(Class<? extends EnumInterface> tClass, String code) {
        isEnum(tClass);
        EnumInterface[] enumConstants = tClass.getEnumConstants();
        for (EnumInterface ei : enumConstants) {
            if (ei.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断name是否是指定枚举的名称
     *
     * @param tClass 枚举
     * @param name   名称
     * @return 是否是枚举值
     */
    public static boolean isEnumName(Class<? extends EnumInterface> tClass, String name) {
        isEnum(tClass);
        EnumInterface[] enumConstants = tClass.getEnumConstants();
        for (EnumInterface ei : enumConstants) {
            if (ei.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断int类型value值是否是指定枚举的value
     *
     * @param ei   枚举
     * @param code int类型值
     */
    public static boolean equalsIntCode(EnumInterface ei, Integer code) {
        if (code == null) {
            return false;
        }
        return equalsCode(ei, "" + code);
    }

    /**
     * 判断int类型value值是否是指定枚举的value
     *
     * @param ei   枚举
     * @param code 类型值
     */
    public static boolean equalsCode(EnumInterface ei, String code) {
        return ei.getCode().equals(code) ? true : false;
    }


    /**
     * 根据val值返回对应enum对象
     *
     * @param tClass 枚举
     * @param code   值
     * @return 未空时, code非法
     */
    public static <T extends EnumInterface> T getEnumByCode(Class<T> tClass, int code) {
        return getEnumByCode(tClass, code + "");
    }

    /**
     * 根据val值返回对应enum对象
     *
     * @param tClass 枚举
     * @param code   值
     * @return 未空时, code非法
     */
    public static <T extends EnumInterface> T getEnumByCode(Class<T> tClass, String code) {
        isEnum(tClass);
        EnumInterface[] enumConstants = tClass.getEnumConstants();
        for (EnumInterface ei : enumConstants) {
            if (ei.getCode().equals(code)) {
                return (T) ei;
            }
        }
        return null;
    }

    /**
     * 根据val值返回对应enum对象value
     *
     * @param tClass 枚举
     * @param code   值
     * @param defVal 默认值
     * @return 未空时, code非法
     */
    public static <T extends EnumInterface> String getEnumValByCode(Class<T> tClass, String code, String defVal) {
        isEnum(tClass);
        EnumInterface[] enumConstants = tClass.getEnumConstants();
        for (EnumInterface ei : enumConstants) {
            if (ei.getCode().equals(code)) {
                return ei.getValue();
            }
        }
        return defVal;
    }

    /**
     * 根据val值返回对应enum对象value , 默认值""
     *
     * @param tClass 枚举
     * @param code   值
     * @return 未空时, code非法
     */
    public static <T extends EnumInterface> String getEnumValByCode(Class<T> tClass, String code) {
        return getEnumValByCode(tClass, code, "");
    }


    /**
     * 根据name值返回对应enum对象
     *
     * @param tClass 枚举
     * @param name   名称
     * @return 未空时, name非法
     */
    public static <T extends EnumInterface> T getEnumByName(Class<T> tClass, String name) {
        isEnum(tClass);
        EnumInterface[] enumConstants = tClass.getEnumConstants();
        for (EnumInterface ei : enumConstants) {
            if (ei.getName().equals(name)) {
                return (T) ei;
            }
        }
        return null;
    }

    /**
     * 获取枚举code列表
     *
     * @param tClass 枚举类
     */
    public static <T extends EnumInterface> List<String> getEnumCodes(Class<T> tClass) {
        List<String> codes = new ArrayList<>();
        isEnum(tClass);
        EnumInterface[] enumConstants = tClass.getEnumConstants();
        for (EnumInterface ei : enumConstants) {
            codes.add(ei.getCode());
        }
        return codes;
    }

    /**
     * 获取枚举value列表
     *
     * @param tClass 枚举类
     */
    public static <T extends EnumInterface> List<String> getEnumValues(Class<T> tClass) {
        List<String> codes = new ArrayList<>();
        isEnum(tClass);
        EnumInterface[] enumConstants = tClass.getEnumConstants();
        for (EnumInterface ei : enumConstants) {
            codes.add(ei.getValue());
        }
        return codes;
    }

    /**
     * 判断tClass对象是否是枚举类型class（不是时，抛出异常）
     *
     * @param tClass 枚举class
     */
    private static <T extends EnumInterface> void isEnum(Class<T> tClass) {
        if (!tClass.isEnum()) {
            throw new SwpCustomizedException("未找到有效的枚举信息");//无效的枚举class
        }
    }


}
