package com.socketserver.thrack.commons;

import org.apache.commons.lang3.EnumUtils;

/**
 * Created by ziye on 2017/3/31.
 */
public class RedisConstants {
    /**
     * Key的类型
     */
    public enum KeyType {
        string, list, set, zset, hash;

        public static KeyType getKeyType(String type){
            return EnumUtils.getEnum(KeyType.class, type);
        }
    }

    /**
     * 以秒为单位的时间常量。先定义，再使用，提高代码可读性
     */
    public enum TimeInSecond {
        /** 5天 */
        _5_DAYS(5*24*3600),

        /** 24小时 */
        _24_HOURS(24*3600),

        /** 8小时 */
        _8_HOURS(8*3600),

        /** 120分钟 */
        _120_MINS(120*60),

        /** 10分钟 */
        _10_MINS(10*60),

        /** 5分钟 */
        _5_MINS(5*60),

        /** 无穷大 ≈68年 */
        INFINITE(Integer.MAX_VALUE),

        /** 不指定有效期，比如根据不同情况使用不同的有效期的情况、或其他不适用的情况 */
        NA(Integer.MIN_VALUE);

        TimeInSecond(int seconds){
            this.seconds = seconds;
        }

        public int val() {
            return seconds;
        }

        private int seconds;
    }



    /**
     * Key的前缀：前缀String、类型、有效期（秒）
     * 类型如果不指定则默认为string
     * 有效期如果不指定则默认为5天
     */
    public enum Prefix {
        /** dtu设备最近一次发送消息的时间 */
        CHANNEL_LAST_SEND_TIME("channel.last.sendTime:"),

        /** 配送版的userInfo */
        LS_USER_INFO("user.info.ysb-ls:", KeyType.hash),

        /** WEB（PC采购）登录的token前缀，value=userId */
        WEB_TOKEN("web.token.to.userid:", TimeInSecond._24_HOURS),

        /** 用户登录相关的信息，APP登录和WEB登录共用 */
        USER_LOGIN_INFO("userLoginInfo:", KeyType.hash, TimeInSecond.INFINITE);

        /**
         * 定义了前缀，缺省类型为string、有效期5天
         */
        Prefix(String id){
            this.id = id;
            this.type = KeyType.string;
            this.ttl = TimeInSecond._5_DAYS;
        }

        /**
         * 定义了前缀和有效期，缺省类型为string
         */
        Prefix(String id, TimeInSecond ttl){
            this.id = id;
            this.type = KeyType.string;
            this.ttl = ttl;
        }

        /**
         * 定义了前缀和类型，缺省有效期5天
         */
        Prefix(String id, KeyType type){
            this.id = id;
            this.type = type;
            this.ttl = TimeInSecond._5_DAYS;
        }

        /**
         * 定义了前缀、类型、和有效期
         */
        Prefix(String id, KeyType type, TimeInSecond ttl){
            this.id = id;
            this.type = type;
            this.ttl = ttl;
        }

        public String id() {
            return id;
        }

        public KeyType type() {
            return type;
        }

        public int ttl() {
            return ttl.val();
        }

        @Override
        public String toString() {
            return id;
        }

        private String id;	//前缀字符串
        private KeyType type;	//类型
        private TimeInSecond ttl;	//过期时间（秒）


        public static Prefix findPrefixById(String prefixId){
            for(Prefix p : Prefix.values()){
                if(p.id().equals(prefixId)){
                    return p;
                }
            }
            return null;
        }
    }


}
