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
	public void removeChannelFromThisServer(Channel channel) {
		// 先取出用户对象
		SocketUser user = Commons.activeDeviceChannelMap.inverse().get(channel);
		// 再从用户channel集合移除
		Commons.removeCloseChannel(channel);
		initKeys(channel);		
		if(user != null) {
			logger.info("removing user from redis: {}, channel: {}", user, channel);
			jedisCluster.del(PREFIX_USER_KEY + user);
			jedisCluster.srem(USER_SET_KEY, user.toString());
		}
	}


	private void initKeys(Channel channel) {
		if(USER_SET_KEY == null) {
			InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();
			LOCAL_ADDR_STR = localAddress.getAddress().getHostAddress() + ":" + localAddress.getPort();
			USER_SET_KEY = PREFIX_USER_SET + LOCAL_ADDR_STR;
		}
	}

}
