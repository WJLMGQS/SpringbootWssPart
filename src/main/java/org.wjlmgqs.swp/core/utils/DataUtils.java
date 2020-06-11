package org.wjlmgqs.swp.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.util.CollectionUtils;
import org.wjlmgqs.swp.core.exps.CustomizedException;

import java.text.SimpleDateFormat;
import java.util.*;

public class DataUtils {

    private static final String BASE_RAMDOM_STRING = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static Long zero2Null(Long val) {
        if (val == null || val.intValue() == 0) {
            return null;
        }
        return val;
    }

    public static Integer zero2Null(Integer val) {
        if (val == null || val.intValue() == 0) {
            return null;
        }
        return val;
    }

    public static <T> T null2Error(T obj, String msg) {
        if (obj == null) {
            throw new CustomizedException(msg);
        }
        return obj;
    }

    public static <T> Map<String, Object> obj2Map(T obj) {
        if (obj == null) {
            return new HashMap<>();
        }
        return new ObjectMapper().setTimeZone(TimeZone.getTimeZone("GMT+8")).setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).convertValue(obj, Map.class);
    }

    public static <T> List<T> null2List(List<T> t){
        if(t==null){
            return new ArrayList<>();
        }
        return t;
    }


    public static String encode64(String fileName){
        byte[] encode = Base64.getEncoder().encode(fileName.getBytes());
        return new String(encode);
    }

    public static String decode64(String encode){
        byte[] decode = Base64.getDecoder().decode(encode.getBytes());
        return new String(decode);
    }

    /**
     * 随机生成指定位数的uuid
     */
    public static String buildUUID(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int number = RandomUtils.nextInt(0, BASE_RAMDOM_STRING.length() - 1);
            sb.append(BASE_RAMDOM_STRING.charAt(number));
        }
        return sb.toString();
    }


    public static <T,P> T getMapValue(Map<P,T> map , P p , T def){
        T val = def;
        if(map.containsKey(p)){
            val = map.get(p);
        }
        return val;
    }

    public static <T> Set<T> list2Set(List<T> t){
        Set<T> s = new HashSet<>();
        if(!CollectionUtils.isEmpty(t)){
            t.forEach(item -> {
                s.add(item);
            });
        }
        return s;
    }

    public static <T> List<T> set2List(Set<T> t){
        List<T> s = new ArrayList<>();
        if(!CollectionUtils.isEmpty(t)){
            t.forEach(item -> {
                s.add(item);
            });
        }
        return s;
    }

}
