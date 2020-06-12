package org.wjlmgqs.swp.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.util.SafeEncoder;

import java.util.*;

/**
 * @author wjlmgqs@sina.com
 * @date 2018/2/28 16:38
 */
@Component
public class RedisUtils {

    private static StringRedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }

    public static StringRedisTemplate getTemplate() {
        return redisTemplate;
    }

    /**
     * 单个业务持有锁的时间10s,防止死锁
     */
    public static final int LOCK_EXPIRE = 10;

    /**
     * 单个业务持有锁的时间半小时,防止死锁
     */
    public static final int LOCK_EXPIRE_180 = 180;

    /**
     * 分布式设置key，成功返回true（没有失效时间）
     */
    public static void set(final String key, final Object value, long expire) {
        getTemplate().execute((RedisConnection connection) -> {
            String json = JSON.toJSONString(value);
            byte[] keyBytes = SafeEncoder.encode(key);
            connection.set(keyBytes, json.getBytes());
            connection.expire(keyBytes, expire);//默认60秒失效
            return true;
        });
    }

    /**
     * 分布式设置key，成功返回true（默认180秒，3分钟）
     */
    public static void set(final String key, final Object value) {
        set(key, value, LOCK_EXPIRE_180);
    }

    /**
     * 从redis中获取指定对象
     */
    public static <T> T get(final String key, final Class<T> clazz) {
        Object t = getTemplate().execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = connection.get(SafeEncoder.encode(key));
                return json2Object(byte2String(keyBytes), clazz);
            }
        });
        return (T) t;
    }

    public static Long remove(final String key) {
        return (Long) getTemplate().execute((RedisConnection connection) -> connection.del(SafeEncoder.encode(key)));
    }

    /**
     * 获取key存入时间
     */
    public static Long ttl(final String key) {
        return (Long) getTemplate().execute((RedisConnection connection) -> connection.ttl(SafeEncoder.encode(key)));
    }

    /**
     * 设置此key的生存时间，单位秒(s)
     */
    public static void setExpire(final String key, final int seconds) {
        getTemplate().execute((RedisConnection connection) -> connection.expire(SafeEncoder.encode(key), seconds));
    }

    /**
     * 获取指定字符串队列中列表数据
     */
    public static List<String> lrange(final String key) {
        return lrange(key, 0, -1, String.class);
    }

    /**
     * 获取指定字符串队列中列表数据，start下标从0开始，end为-1时，表示最后一个元素
     */
    public static List<String> lrange(final String key, final int start, final int end) {
        return lrange(key, start, end, String.class);
    }

    /**
     * 获取指定数据类型的队列数据
     */
    public static <T> List<T> lrange(final String key, Class<T> clazz) {
        return lrange(key, 0, -1, clazz);
    }

    /**
     * 获取指定数据类型的队列数据，start下标从0开始，end为-1时，表示最后一个元素
     */
    public static <T> List<T> lrange(final String key, final int start, final int end, Class<T> clazz) {
        return (List<T>) getTemplate().execute((RedisConnection connection) -> {
            List<byte[]> list = connection.lRange(SafeEncoder.encode(key), start, end);
            List<T> result = new ArrayList<T>();
            if (CollectionUtils.isEmpty(list)) {
                return result;
            }
            result.forEach(item -> {
                result.add(JSON.parseObject(byte2String(list.get(0)), clazz));
            });
            return result;
        });
    }

    /**
     * 分布式设置key，成功返回true（默认10秒）
     */
    public static boolean setnx(final String key, final Object value) {
        return setnx(key, value, 10);
    }

    /**
     * 分布式incr
     *
     * @param key     键
     * @param seconds 有效期秒数
     */
    public static Long incr(final String key, final long seconds) {
        return (Long) getTemplate().execute((RedisConnection connection) -> {
            byte[] keyBytes = SafeEncoder.encode(key);
            Long result = connection.incr(keyBytes);
            if (result != null && seconds > 0) {
                connection.expire(keyBytes, seconds);
            }
            return result;
        });
    }

    /**
     * 分布式设置key，成功返回true（指定失效时间）
     *
     * @param key     键
     * @param value   值
     * @param seconds 有效期秒数
     */
    public static boolean setnx(final String key, final Object value, final long seconds) {
        return getTemplate().execute((RedisConnection connection) -> {
            String json = JSON.toJSONString(value);
            byte[] keyBytes = SafeEncoder.encode(key);
            Boolean result = true;
            try {
                result = connection.setNX(keyBytes, json.getBytes());
            } finally {
                if (result && seconds > 0) {
                    connection.expire(keyBytes, seconds);
                }
            }
            return result;
        });
    }

    /**
     * 获取Redis list 第一个元素（默认字符串类型）
     */
    public static String lPop(final String key) {
        return lPop(key, String.class);
    }


    /**
     * 获取Redis list 第一个指定数据类型的元素
     */
    public static <T> T lPop(final String key, final Class<T> clazz) {
        return (T) getTemplate().execute((RedisConnection connection) -> {
            byte[] ek = SafeEncoder.encode(key);
            byte[] bytes = connection.lPop(ek);
            return json2Object(byte2String(bytes), clazz);
        });
    }

    /**
     * 队列末尾添加key
     */
    public static Integer rpush(final String key, final Object... objs) {
        if (objs.length == 0) {
            return 0;
        }
        byte[][] datas = new byte[objs.length][];
        for (int i = 0; i < objs.length; i++) {
            datas[i] = JSON.toJSONString(objs[i]).getBytes();
        }
        return (Integer) getTemplate().execute((RedisConnection connection) -> {
            for (byte[] item : datas) {
                connection.rPush(SafeEncoder.encode(key), item);
            }
            return datas.length;
        });
    }

    /**
     * 队列末尾添加key
     */
    public static <T> Integer rpushs(final String key, final List<T> objs) {
        if (CollectionUtils.isEmpty(objs)) {
            return 0;
        }
        byte[][] datas = new byte[objs.size()][];
        for (int i = 0; i < objs.size(); i++) {
            datas[i] = JSON.toJSONString(objs.get(i)).getBytes();
        }
        return (Integer) getTemplate().execute((RedisConnection connection) -> {
            for (byte[] item : datas) {
                connection.rPush(SafeEncoder.encode(key), item);
            }
            return datas.length;
        });
    }


    /**
     * 获取指定key中的指定field中的value
     */
    public static Boolean hset(final String key, final String field, final Object value) {
        return (Boolean) getTemplate().execute((RedisConnection connection) -> {
            connection.hSet(SafeEncoder.encode(key), SafeEncoder.encode(field), JSON.toJSONString(value).getBytes());
            return true;
        });
    }

    /**
     * 获取指定key中的指定field中的value
     */
    @SuppressWarnings("unchecked")
    public static Boolean hmset(final String key, final Map<String, Object> params) {
        Map<byte[], byte[]> data = new HashMap<>();
        params.forEach((index, val) -> data.put(SafeEncoder.encode(index), JSON.toJSONString(val).getBytes()));
        return (Boolean) getTemplate().execute((RedisConnection connection) -> {
            connection.hMSet(SafeEncoder.encode(key), data);
            return true;
        });
    }

    /**
     * 获取指定key中的指定field中的value
     */
    @SuppressWarnings("unchecked")
    public static <T> T hget(final String key, final String field, final Class<T> clazz) {
        return (T) getTemplate().execute((RedisConnection connection) -> {
            byte[] value = connection.hGet(SafeEncoder.encode(key), SafeEncoder.encode(field));
            if (value == null || value.length == 0) {
                return null;
            }
            return JSON.parseObject(byte2String(value), clazz);
        });
    }

    /**
     * 移除指定key中的指定field
     */
    public static void hremove(final String key, final String field) {
        getTemplate().execute((RedisConnection connection) -> connection.hDel(SafeEncoder.encode(key), SafeEncoder.encode(field)));
    }

    /**
     * 获取key集合中所有相关的value
     */
    public static <T> Map<String, T> hgetAll(final String key, final Class<T> clazz) {
        return (Map<String, T>) getTemplate().execute((RedisConnection connection) -> {
            Map<byte[], byte[]> map = connection.hGetAll(SafeEncoder.encode(key));
            Map<String, T> result = new HashMap();
            for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
                if (entry.getKey() != null && entry.getKey().length > 0 && entry.getValue() != null && entry.getValue().length > 0) {
                    result.put(byte2String(entry.getKey()), json2Object(byte2String(entry.getValue()), clazz));
                }
            }
            return result;
        });
    }


    /**
     * 获取key集合中所有相关的value
     */
    public static List<String> keys(final String key) {
        Set<String> keys = getTemplate().keys(key + "*");//查出所有匹配的key
        List<String> results = new ArrayList<>();
        keys.forEach(k -> results.add(k));
        return results;//查出所有匹配的key
    }

    public static String byte2String(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return null;
        return new String(bytes);
    }

    public static <T> T json2Object(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return JSONObject.parseObject(json, clazz);
    }


}
