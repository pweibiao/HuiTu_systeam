package com.peng.cssys.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SuppressWarnings(value = {"unchecked","rawtypes"})
@Component
public class RedisCache {

    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param <T> 缓存对象的类型
     */
    public <T> void setCacheObject(final String key, final T value){
        redisTemplate.opsForValue().set(key,value);
    }

    /**
     * 缓存基本的对象
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒
     * @param <T> 缓存对象的类型
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key,value,timeout,timeUnit);
    }

    /**
     * 设置有效时间
     * @param key 缓存的键值
     * @param timeout 超时时间
     * @return 1成功 0失败
     */
    public boolean expire(final String key, final long timeout){
        return redisTemplate.expire(key,timeout,TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     * @param key 缓存的键值
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 1成功 0失败
     */
    public boolean expire(final String key, final long timeout,final TimeUnit unit){
        return redisTemplate.expire(key,timeout,unit);
    }

    /**
     * 获取缓存对象
     * @param key 缓存键值
     * @param <T> 缓存对象类型
     * @return 数据
     */
    public <T> T getCacheObject(final String key){
        ValueOperations<String,T> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    /**
     * 删除缓存对象
     * @param key 缓存键值
     * @return 1成功 0失败
     */
    public boolean deleteObject(final String key){
        return redisTemplate.delete(key);
    }

    /**
     * 删除多个对象
     * @param collection 集合对象
     * @return 1成功 0失败
     */
    public long deleteObjects(final Collection collection){
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     * @param key 键值
     * @param datalist List数据
     * @param <T> list类型
     * @return 缓存对象
     */
    public <T> long setCacheList(final String key, final List<T> datalist){
        Long count = redisTemplate.opsForList().rightPushAll(key,datalist);
        return count == null ? 0 : count;
    }

    /**
     * 获取缓存List对象
     * @param key 键值
     * @param <T> 对象类型
     * @return 缓存的对应数据
     */
    public <T> List<T> getCacheList(final String key){
        return redisTemplate.opsForList().range(key,0,-1);
    }

    /**
     * 缓存Set数据
     * @param key 键值
     * @param dataSet Set数据
     * @param <T> Set类型
     * @return 缓存对像
     */
    public <T> long setCacheSet(final String key , final Set<T> dataSet){
        Long count = redisTemplate.opsForSet().add(key,dataSet);
        return count == null ? 0 : count;
    }

    /**
     * 获取Set对象
     * @param key 键值
     * @param <T> 对象类型
     * @return 缓存对象
     */
    public <T> Set<T> getCacheSet(final String key){
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map对象
     * @param key 键值
     * @param dataMap 对象
     * @param <T> 类型
     */
    public <T> void setCacheMap(final String key, final Map<String,T> dataMap){
        if(dataMap != null){
            redisTemplate.opsForHash().putAll(key,dataMap);
        }
    }

    /**
     * 获取Map对象
     * @param key 键值
     * @param <T> 类型
     * @return
     */
    public <T> Map<String,T> getCacheMap(final String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash存入数据
     * @param key redis键
     * @param hKey hash键
     * @param value 值
     * @param <T> 类型
     */
    public <T> void setCacheMapValue(final String key ,final String hKey ,final T value ){
        redisTemplate.opsForHash().put(key , hKey, value);
    }

    /**
     * 获取Hash数据
     * @param key redis键
     * @param hKey hash键
     * @param <T>
     * @return
     */
    public <T> T getCacheMapValue(final String key, final String hKey){
        HashOperations<String,String,T> operations = redisTemplate.opsForHash();
        return  operations.get(key, hKey);
    }

    /**
     * 获取多个Hash数据
     * @param key Redis键
     * @param hKeys Hash键集合
     * @param <T> 类型
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys){
        return redisTemplate.opsForHash().multiGet(key,hKeys);
    }

    /**
     * 获取缓存的基本对象列表
     * @param pattern 字符串
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern){
        return redisTemplate.keys(pattern);
    }
}
