package com.socketserver.thrack.server.handlers;


import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.socketserver.thrack.server.client.Client;
import com.socketserver.thrack.server.client.ClientMap;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.socketserver.thrack.model.user.SocketUser;
import com.socketserver.thrack.server.handlers.AuthenticationHandler.ChannelStatus;

import io.netty.channel.Channel;

public class Commons {
	
	private static final Logger logger = LoggerFactory.getLogger(Commons.class);

	// 双向MAP，存放用户与channel关系
	public static final BiMap<SocketUser, Channel> activeDeviceChannelMap = Maps.synchronizedBiMap(
    		HashBiMap.<SocketUser, Channel>create() );
    
    // 存放channel状态（连接时间，是否验证等）
	public static final Map<Channel, ChannelStatus> channelStatusMap = new ConcurrentHashMap<>();  // 存放channel的建立、验证时间
	
	//设备id-用户账号 map，用于保存用户操作设备记录
	public static final Map<String, String> deviceToAccountMap = new HashMap<String, String>();
    
    // 记录用户是否有未读消息，用户初次连接时加入，查缓存查库无记录则移除，作为目标收到消息加入
    // 为了避免用户每心跳一次查库一次
	public static final Set<SocketUser> hasUnreadUserSet = new ConcurrentSkipListSet<>();
	
	public static void writeAndFlushSingletonListOf( Object object, Channel channel )
			throws IOException, JsonGenerationException, JsonMappingException {
		if(channel == null) {
			logger.info("null channel.");
			return;
		}
		final ObjectMapper jsonMapper = new ObjectMapper();
		String sendingStr = jsonMapper.writeValueAsString( Collections.singletonList( object ) );
		logger.info("send pakage: {}", sendingStr);
		if( channel.isActive() ) {
			channel.writeAndFlush( sendingStr + "\n" );
		} else {
			removeCloseChannel(channel);
		}
	}
	

	public static void removeCloseChannel(Channel channel) {
		if(channel == null) {
			logger.info("null channel.");
			return;
		}
		// 从各种记录集合中移除用户与channel
		Client client = ClientMap.getClient(channel);
		logger.info("removing user: {}, channel: {}", client, channel);

		ClientMap.removeClient(channel);
		if(channel.isOpen()) {
			channel.close();
		}
	}

}
