package com.socketserver.thrack.service;


import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.socketserver.thrack.commons.SocketServerConstants;
import com.socketserver.thrack.model.user.SocketUser;
import com.socketserver.thrack.server.handlers.Commons;

import io.netty.channel.Channel;
import redis.clients.jedis.JedisCluster;

/**
 * 2015.3.26修改为jedisCluster实现
 * @author wushenjun
 *
 */
@Service
public class UserConnectionRegisterServiceImpl implements UserConnectionRegisterService {

	private static final Logger logger = LoggerFactory.getLogger(UserConnectionRegisterServiceImpl.class);
	
	private static final String PREFIX_USER_SET = "socketUserSetConnectedTo:";
	private static final String PREFIX_USER_KEY = "userToSocketAddr:";
	public static final String PREFIX_SCM_USER_SET_OF_PROVIDER = "scmUserOfProviderSetOfProvider:";
	public static final String PREFIX_SCM_USER = "scmUser:";
	
	public static final String USER_VERSION_HSET_KEY = "socketserver.version";
	
	// Rule: KEY : SocketUser -> timestamp
	public static final String USER_ONLINE_SET_KEY	= "socketserver.online";
	
	private static String USER_SET_KEY = null;
	private static String LOCAL_ADDR_STR = null;
    
	// 双向MAP，存放用户到version
	public static final Map<SocketUser, String> userVersionMap = new ConcurrentHashMap<>();
	
	@Autowired
	private JedisCluster jedisCluster;
	
	
	@Override
	public void registerDeviceChannelToThisServer(SocketUser user, Channel channel) {
		Commons.activeDeviceChannelMap.put(user, channel);
		initKeys(channel);
		
		jedisCluster.setex(PREFIX_USER_KEY + user, 
				SocketServerConstants.USER_TO_SOCKET_SERVER_REDIS_LIFETIME_SECONDS,
				LOCAL_ADDR_STR);
		jedisCluster.sadd(USER_SET_KEY, user.toString());
	}

	@Override
	public void removeUserChannelFromThisServer(Channel channel) {
		// 先取出用户对象
		SocketUser user = Commons.activeDeviceChannelMap.inverse().get(channel);
		// 再从用户channel集合移除
		Commons.removeUserAndCloseChannel(channel);
		initKeys(channel);		
		if(user != null) {
			logger.info("removing user from redis: {}, channel: {}", user, channel);
			jedisCluster.del(PREFIX_USER_KEY + user);
			jedisCluster.srem(USER_SET_KEY, user.toString());
		}
	}
	
	@Override
	public String getUserConnectingAddress(SocketUser user) {
		return jedisCluster.get(PREFIX_USER_KEY + user);
	}

	private void initKeys(Channel channel) {
		if(USER_SET_KEY == null) {
			InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();
			LOCAL_ADDR_STR = localAddress.getAddress().getHostAddress() + ":" + localAddress.getPort();
			USER_SET_KEY = PREFIX_USER_SET + LOCAL_ADDR_STR;
		}
	}
	
	@Override
	public void setVersion(SocketUser user, String version) {
		userVersionMap.put(user, version);
		
		jedisCluster.hset(USER_VERSION_HSET_KEY, user.toString(), version);
	}
	
	@Override
	public String getVersion(SocketUser user) {
		String version = userVersionMap.get(user);
		if (!StringUtils.isEmpty(version)) {
			return version;
		}
		else {
			return jedisCluster.hget(USER_VERSION_HSET_KEY, user.toString());
		}
		
	}

	@Override
	public void login(SocketUser user) {
		// Field: socket user, value: login timestamp
		jedisCluster.hset(USER_ONLINE_SET_KEY, user.toString(), 
				String.valueOf((DateTime.now().getMillis() / 1000)));
	}

	@Override
	public void logout(SocketUser user) {
		jedisCluster.hdel(USER_ONLINE_SET_KEY, user.toString());
	}

	@Override
	public boolean isOnline(SocketUser user) {
		return jedisCluster.hget(USER_ONLINE_SET_KEY, user.toString()) != null;
	}
	
}
