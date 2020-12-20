package com.peng.cssys.utils.uuid;

/***
 * ID生成器
 */
public class IdUtils {
    /**
     * 获取随机UUID
     * @return
     */
    public static String randomUUID(){
        return UUID.randomUUID().toString();
    }

    /**
     * 简化UUID
     * @return
     */
    public static String simpleUUID(){
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID
     * 使用ThreadLocalRandom生成UUID
     * @return
     */
    public static String fastUUID(){
        return UUID.fastUUID().toString();
    }

    /**
     * 简化UUID
     * 使用ThreadLocalRandom生成UUID
     * @return
     */
    public static String fastSimpleUUID(){
        return UUID.fastUUID().toString(true);
    }

}

