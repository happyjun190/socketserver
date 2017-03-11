package com.socketserver.thrack.configuration;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.google.common.cache.CacheBuilder;

/**
 * Cache配置類，用于接口缓存数据
 * @author wushenjun
 *
 */
@Configuration
//@EnableCaching //@EnableCaching和Mybatis配置有冲突，改为xml文件中配置
public class CacheConfig {

	public static final int DEFAULT_MAXSIZE = 50000;
	public static final int DEFAULT_TTL = 10;
	
	/**
	 * 定義cache名稱、超時時長（秒）、最大size
	 * 每个cache缺省10秒超时、最多缓存50000条数据，需要修改可以在构造方法的参数中指定。
	 */
	public enum Caches{

		/*userInfo(10),
		userDevice(120),
		messageContent(3600)*/
		;
		
		Caches() {
		}

		Caches(int ttl) {
			this.ttl = ttl;
		}

		Caches(int ttl, int maxSize) {
			this.ttl = ttl;
			this.maxSize = maxSize;
		}
		
		private int maxSize=DEFAULT_MAXSIZE;	//最大數量
		private int ttl=DEFAULT_TTL;		//过期时间（秒）
		
		public int getMaxSize() {
			return maxSize;
		}
		public void setMaxSize(int maxSize) {
			this.maxSize = maxSize;
		}
		public int getTtl() {
			return ttl;
		}
		public void setTtl(int ttl) {
			this.ttl = ttl;
		}
		
		public static Caches findCacheByName(String name){
			for(Caches c : Caches.values()){
				if(c.name().equals(name)){
					return c;
				}
			}
			return null;
		}
	}
	
	/**
	 * 创建基于guava的Cache Manager
	 * @return
	 */
	@Bean
	@Primary
	public CacheManager guavaCacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		
		ArrayList<GuavaCache> caches = new ArrayList<GuavaCache>();
		for(Caches c : Caches.values()){
			caches.add(new GuavaCache(c.name(), CacheBuilder.newBuilder().recordStats().expireAfterWrite(c.getTtl(), TimeUnit.SECONDS).maximumSize(c.getMaxSize()).build()));
		}
		
		cacheManager.setCaches(caches);

		return cacheManager;
	}
	
}
