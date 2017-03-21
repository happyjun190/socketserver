package com.socketserver.thrack.server.handlers;

import com.socketserver.thrack.server.client.Client;
import com.socketserver.thrack.server.client.ClientMap;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Commons {
	
	private static final Logger logger = LoggerFactory.getLogger(Commons.class);

	/*// 双向MAP，存放用户与channel关系
	public static final BiMap<SocketUser, Channel> activeDeviceChannelMap = Maps.synchronizedBiMap(
    		HashBiMap.<SocketUser, Channel>create() );*/


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
