package com.example.demo.comm.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {



    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.redis.host}")
    private String redisIP;

    @Value("${spring.redis.port}")
    private int redisPost;

    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }

            return true;
        } catch (Exception e) {
            log.error("设置key的有效时间时发生异常-----11100001, key：[" + key + "]", e);
            return false;
        }
    }

    public Object get(String key) {
        try {
            Object o = redisTemplate.opsForValue().get(key);
            return key == null ? null : o;
        } catch (Exception e) {
            log.error("根据key获取缓存中的数据时发生异常-----11100002, key：[" + key + "]", e);

        }
        return null;
    }

    /**
     * 通过前缀查询一组数据
     *
     * @param key
     * @return
     */
    public Set<String> keys(String key) {
        Set<String> set = null;
        try {
            set = redisTemplate.keys(key);
        } catch (Exception e) {
            log.error("通过前缀查询一组缓存数据时发生异常-----11100003, key：[" + key + "]", e);
        }
        return set;
    }


    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("根据key和value设置缓存时发生异常-----11100004, key：[" + key + "]", e);
            return false;
        }
    }

    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("根据key和value以及超时时间设置缓存数据时发生异常-----11100005, key：[" + key + "]", e);
            return false;
        }
    }

    public boolean setIfAbsent(String key, Object value, long seconds) {
        try {
            return this.redisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("根据key和value以及超时时间设置缓存不存在数据时发生异常-----11100006, key：[" + key + "]", e);
            return false;
        }
    }

    public long incr(String key, long step) {
        try {
            if (step < 0) {
                throw new RuntimeException("步长必须大于0");
            }
            return redisTemplate.opsForValue().increment(key, step);
        } catch (Exception e) {
            log.error("根据key设置缓存数据的数值时发生异常-----11100007, key：[" + key + "]", e);
            return -1;
        }
    }

    /**
     * 删除
     *
     * @param key
     * @return
     */
    public boolean remove(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("删除缓存中的key信息时发生异常-----11100008, key：[" + key + "]", e);
        }
        return false;
    }

    /**
     * 批量删除
     *
     * @param pattern
     * @return
     */
    public void removeArray(String pattern) {
        try {
            //模糊查询这一组数据 并删除
            Set<String> set = redisTemplate.keys(pattern + "*");
            if (set != null && !set.isEmpty()) {
                redisTemplate.delete(set);
            }
        } catch (Exception e) {
            log.error("批量移除缓存中的数据时异常-----11100009, pattern:[" + pattern + "]", e);
        }
    }


    public Object getSeialize(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("redis getSeialize error, key:{}-----11100010, key：[" + key + "]", key);
            log.error("根据key获取缓存的序列化数据时异常-----11100011, key：[" + key + "]", e);
        }
        return null;
    }

    public Object setSeialize(final String key, final Object value, final int expireTime) {
        try {


//             byte[] keyBytes = key.getBytes();
//             byte[] valueBytes = RedisService.serialize(value);
            if (value == null) {
                redisTemplate.delete(key);
//            	 new Jedis(getRedisIP(),6379).del(keyBytes);
            } else {
                redisTemplate.opsForValue().set(key, value);
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
//            	 new Jedis(getRedisIP(),6379).set(keyBytes, valueBytes);
//            	 new Jedis(getRedisIP(),6379).expire(keyBytes, expireTime);
            }
        } catch (Exception e) {
            log.error("根据key、value和过期时间设置缓存时发生异常-----11100012, key：[" + key + "]", e);
        }
        return null;
    }

    private static byte[] serialize(Object object) throws IOException {
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        byte[] bytes;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException var9) {
            throw var9;
        } finally {
            if (objectOutputStream != null) {
                objectOutputStream.flush();
                objectOutputStream.close();
            }

            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }

        }

        return bytes;
    }

    private static Object deSeialize(byte[] bytes) throws Exception {
        ByteArrayInputStream byteArrayOutputStream = null;
        ObjectInputStream objectInputStream = null;

        Object object;
        try {
            byteArrayOutputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayOutputStream);
            object = objectInputStream.readObject();
        } catch (IOException var9) {
            throw new IOException(var9);
        } finally {
            if (objectInputStream != null) {
                objectInputStream.close();
            }

            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }

        }

        return object;
    }


    public static String createKey(String... strs) {

        StringBuilder keyBuilder = new StringBuilder();
        for (String str : strs) {
            keyBuilder.append(str).append(":");
        }

        return keyBuilder.toString();
    }

}
